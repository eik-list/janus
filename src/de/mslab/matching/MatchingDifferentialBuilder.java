package de.mslab.matching;

import java.util.ArrayList;
import java.util.List;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.ciphers.helpers.RecomputedOperationsCounter;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.core.Difference;
import de.mslab.core.Differential;
import de.mslab.diffbuilder.BitwiseDifferenceBuilder;
import de.mslab.diffbuilder.BitwiseDifferenceIterator;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;
import de.mslab.diffbuilder.DifferenceBuilder;
import de.mslab.diffbuilder.DifferenceIterator;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.errors.InvalidArgumentError;
import de.mslab.utils.Logger;

/**
 * <p>
 * Calculates the complexity for the matching part of a biclique attack on a given cipher
 * on those rounds which are not covered by a given biclique.
 * </p>
 */
public class MatchingDifferentialBuilder {
	
	private Logger logger = Logger.getLogger();
	
	private MatchingContext context;
	private MatchingDifferentialBuilderResult result;
	
	private Biclique biclique;
	private RoundBasedBlockCipher cipher;
	private RecomputedOperationsCounter counter;
	
	private ByteArray emptyState;
	private ByteArray[][] keys = new ByteArray[2][2];
	private BitwiseDifferenceIterator deltaDifferenceIterator; 
	private BitwiseDifferenceIterator nablaDifferenceIterator;
	private DifferenceBuilder matchingDifferenceBuilder;
	
	public MatchingDifferentialBuilder() {
		
	}
	
	/**
	 * Calculates the complexity for the matching part of a biclique attack on a given cipher
	 * on those rounds which are not covered by a given biclique.
	 * @param context 
	 */
	public MatchingDifferentialBuilderResult findMinNumActiveBytes(MatchingContext context) {
		setUp(context);
		
		computeKeys();
		createMatchingDifferenceBuilder();
		determineBicliqueRoundsAndDimension();
		computeMatchingRoundsRange();
		computeDifferentials();
		tearDown();
		return result;
	}
	
	private void createMatchingDifferenceBuilder() {
		if (cipher.operatesNibblewise()) {
			matchingDifferenceBuilder = new NibblewiseDifferenceBuilder();
		} else if (cipher.operatesBytewise()) {
			matchingDifferenceBuilder = new BytewiseDifferenceBuilder();
		} else {
			matchingDifferenceBuilder = new BitwiseDifferenceBuilder();
		}
	}
	
	private void setUp(MatchingContext context) {
		this.context = context;
		this.context.iteration = 0;
		this.biclique = context.biclique;
		this.cipher = context.cipher;
		this.counter = context.counter;

		this.result = new MatchingDifferentialBuilderResult();
	}
	
	private void tearDown() {
		this.biclique = null;
		this.cipher = null;
		this.counter = null;
		this.context = null;
	}
	
	/**
	 * Determines the number of rounds which are covered by the given biclique
	 * and the biclique dimension, i. e. the number of active bytes in the secret key.
	 */
	private void determineBicliqueRoundsAndDimension() {
		result.numBicliqueRounds = biclique.deltaDifferential.toRound - biclique.deltaDifferential.fromRound + 1;
		result.dimension = biclique.dimension;
	}
	
	/**
	 * Retrieves keys K[0,0], K[i,0] and K[0,j] from the biclique. 
	 * These keys will be used for encryption/decryption in the matching rounds.
	 */
	private void computeKeys() {
		Differential delta = biclique.deltaDifferential;
		Differential nabla = biclique.nablaDifferential;
		emptyState = new ByteArray(cipher.getStateSize());
		
		int i = 1;
		int j = 1;
		
		keys[0][0] = delta.firstSecretKey.clone();
		keys[i][0] = delta.secondSecretKey.clone();
		keys[0][j] = nabla.secondSecretKey.clone();
		
		keys[0][0] = computeExpandedKeyFromSecretKey(keys[0][0]);
		keys[i][0] = computeExpandedKeyFromSecretKey(keys[i][0]);
		keys[0][j] = computeExpandedKeyFromSecretKey(keys[0][j]);
		
		keys[0][0] = computeKeyPart(keys[0][0], delta.fromRound);
		keys[i][0] = computeKeyPart(keys[i][0], delta.fromRound);
		keys[0][j] = computeKeyPart(keys[0][j], delta.fromRound);
		
		ByteArray randomKey = new ByteArray(cipher.getKeySize());
		randomKey.randomize();
		
		keys[0][0].xor(randomKey);
		keys[i][0].xor(randomKey);
		keys[0][j].xor(randomKey);
		
		keys[i][j] = keys[0][0].clone();
		keys[i][j].xor(keys[i][0]);
		keys[i][j].xor(keys[0][j]);
		
		keys[0][0] = computeExpandedKey(keys[0][0], delta.fromRound);
		keys[i][0] = computeExpandedKey(keys[i][0], delta.fromRound);
		keys[0][j] = computeExpandedKey(keys[0][j], delta.fromRound);
		keys[i][j] = computeExpandedKey(keys[i][j], delta.fromRound);
		
		ByteArray ki0diff = keys[0][0].clone();
		ByteArray k0jdiff = keys[0][0].clone();
		ki0diff.xor(keys[i][0]);
		k0jdiff.xor(keys[0][j]);
		
		int[] deltaActivePositions = findActiveBitPositionsFromDifference(biclique.deltaDifferential.keyDifference);
		int[] nablaActivePositions = findActiveBitPositionsFromDifference(biclique.nablaDifferential.keyDifference);
		
		deltaDifferenceIterator = new BitwiseDifferenceIterator(
			biclique.deltaDifferential.keyDifference, biclique.dimension, deltaActivePositions
		);
		nablaDifferenceIterator = new BitwiseDifferenceIterator(
			biclique.nablaDifferential.keyDifference, biclique.dimension, nablaActivePositions
		);
	}
	
	private int[] findActiveBitPositionsFromDifference(ByteArray keyDifference) {
		List<Integer> activePositions = new ArrayList<Integer>();
		int length = keyDifference.length() * Byte.SIZE;
		int numActiveBits = 0;
		
		for (int i = 0; i < length; i++) {
			if (keyDifference.getBit(i)) {
				activePositions.add(i);
				numActiveBits++;
			}
		}
		
		int[] activePositionsArray = new int[numActiveBits];
		
		for (int i = 0; i < numActiveBits; i++) {
			activePositionsArray[i] = activePositions.get(i);
		}
		
		return activePositionsArray;
	}
	
	private ByteArray computeExpandedKeyFromSecretKey(ByteArray secretKey) {
		cipher.setKey(secretKey);
		return cipher.getExpandedKey();
	}
	
	private ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		if (cipher.canInvertKeySchedule()) {
			return cipher.computeExpandedKey(keyPart, round);
		} else {
			cipher.setKey(keyPart);
			return cipher.getExpandedKey();
		}
	}
	
	/**
	 * Returns the part of the key which was used as the difference from the given round.
	 */
	private ByteArray computeKeyPart(ByteArray expandedKey, int round) {
		int from = (round - 1) * cipher.getStateSize();
		
		if (cipher.hasKeyInjectionInRound(0)) {
			from += cipher.getStateSize();
		}
		
		int numBytesToCopy = cipher.getKeySize();
		
		if (from + numBytesToCopy > expandedKey.length()) {
			from = expandedKey.length() - numBytesToCopy;
		}
		
		ByteArray result = new ByteArray(cipher.getKeySize());
		result.copyBytes(expandedKey, from, 0, numBytesToCopy);
		return result;
	}
	
	/**
	 * Determines the rounds for the matching from the given cipher. 
	 * Be r the number of rounds in the cipher, e. g. r = 10.
	 * If the biclique is located at the beginning of the cipher, e. g. covering rounds 1 - 3, 
	 * then we will try to match at state after round 4, 5, 6, ... 10.
	 * If the biclique is located at the end of the cipher, e. g. covering rounds 7 - 10, 
	 * then we will try to match at state after round 1, 2, 3, ... 6.
	 */
	private void computeMatchingRoundsRange() {
		Differential delta = biclique.deltaDifferential;
		
		if (delta.fromRound <= 1) { 
			// E.g.: Biclique: 1 - 3 => Matching: 4 - 10
			setMatchingRoundsRange(delta.toRound + 1, cipher.getNumRounds());
		} else { 
			// E.g.: Biclique: 8 - 10 => Matching: 1 - 7
			setMatchingRoundsRange(1, delta.fromRound - 1);
		}
	}
	
	private void setMatchingRoundsRange(int fromRound, int toRound) {
		if (context.matchingFromRound == -1) {
			result.matchingFromRound = fromRound;
		} else {
			result.matchingFromRound = context.matchingFromRound;
		}
		if (context.matchingToRound == -1) {
			result.matchingToRound = toRound;
		} else {
			result.matchingToRound = context.matchingToRound;
		}
	}
	
	/**
	 * For each possible matching round in [matchFrom, matchTo] and for each possible byte in which we could match, 
	 * this function 
	 * computes the forward differentials P -> v, v <- S, 
	 * computes backward differentials P <- v, v -> S, 
	 * merges the differentials P -> v and P <- v, 
	 * merges v <- S with v -> S,
	 * counts the number of active bytes, 
	 * and stores in the result the minimum number of active bytes.
	 */
	private void computeDifferentials() throws InvalidArgumentError {
		result.minNumActiveBytes = -1;
		
		if (context.isMatchingFixed) {
			int[] matchingStateActiveBitPositions = findActiveBitPositionsFromDifference(context.matchingStateDifference);
			DifferenceIterator matchingBitsIterator = new BitwiseDifferenceIterator(emptyState, Byte.SIZE, matchingStateActiveBitPositions);
			computeDifferentials(context.matchingRound, matchingBitsIterator);
		} else {
			if (context.numMatchingBitsUsed < 1) {
				throw new InvalidArgumentError("You must specify a number between 1 <= b <= n bits for matching, " +
					"where n is the state size of the used cipher. " +
					"Your current choice for b is " + context.numMatchingBitsUsed + " and n is " + cipher.getStateSize() +
					"."
				);
			}
			
			long numStateDifferences = matchingDifferenceBuilder.initializeAndGetNumDifferences(
				context.numMatchingBitsUsed, cipher.getStateSize()
			);
			
			for (int matchingRound = result.matchingFromRound; matchingRound < result.matchingToRound; matchingRound++) {
				for (int i = 0; i < numStateDifferences; i++) {
					computeDifferentials(matchingRound, matchingDifferenceBuilder.next());
				}
			}
		}
	}
	
	private void computeDifferentials(int matchingRound, DifferenceIterator matchingBitsIterator) {
		final int i = 1;
		final int j = 1;
		
		deltaDifferenceIterator.reset();
		nablaDifferenceIterator.reset();
		
		Differential p_to_v = computeForwardDifferentialToMiddle(
			result.matchingFromRound, matchingRound, deltaDifferenceIterator, keys[0][0]
		);
		Differential s_to_v = computeBackwardDifferentialToMiddle(
			matchingRound + 1, result.matchingToRound, nablaDifferenceIterator, keys[0][0]
		);
		
		//logger.info("p -> v {0}", p_to_v);
		//logger.info("s <- v {0}", s_to_v);
		
		Differential v_to_p = computeBackwardDifferentialFromMiddle(
			result.matchingFromRound, matchingRound, emptyState, matchingBitsIterator, keys[i][j] 
		);
		Differential v_to_s = computeForwardDifferentialFromMiddle(
			matchingRound + 1, result.matchingToRound, emptyState, matchingBitsIterator, keys[i][j]
		);
		
		//logger.info("p <- v {0}", v_to_p);
		//logger.info("s -> v {0}", v_to_s);
		
		Differential p_mergedto_v = mergeIntoForwardDifferential(p_to_v, v_to_p);
		Differential s_mergedto_v = mergeIntoBackwardDifferential(s_to_v, v_to_s);
		
		int numActiveBytes = counter.countRecomputedOperations(p_mergedto_v) 
			+ counter.countRecomputedOperations(s_mergedto_v);
		
		if (result.minNumActiveBytes == -1 || numActiveBytes < result.minNumActiveBytes) {
			result.minNumActiveBytes = numActiveBytes;
			result.bestMatchingRound = matchingRound;
			
			result.p_to_v = p_to_v;
			result.s_to_v = s_to_v;
			result.v_to_p = v_to_p;
			result.v_to_s = v_to_s;
			
			result.p_mergedto_v = p_mergedto_v;
			result.s_mergedto_v = s_mergedto_v;
		}
		
		result.minNumActiveBytes = counter.countRecomputedOperations(result.p_mergedto_v)
			+ counter.countRecomputedOperations(result.s_mergedto_v);
		//logger.info("num active {0}", result.minNumActiveBytes);
	}
	
	private Differential mergeIntoForwardDifferential(Differential original, Differential toMergeIn) {
		Differential result = original.clone();
		
		for (int round = result.toRound; round >= result.fromRound; round--) {
			andDifferencesIfNotNull(result.getStateDifference(round), toMergeIn.getStateDifference(round));
		}
		
		for (int round = result.toRound; round >= result.fromRound; round--) {
			andDifferencesIfNotNull(result.getIntermediateStateDifference(round), toMergeIn.getIntermediateStateDifference(round));
		}
		
		return result;
	}
	
	private Differential mergeIntoBackwardDifferential(Differential original, Differential toMergeIn) {
		Differential result = original.clone();
		
		for (int round = result.fromRound - 1; round <= result.toRound; round++) {
			andDifferencesIfNotNull(result.getStateDifference(round), toMergeIn.getStateDifference(round));
		}
		
		for (int round = result.fromRound; round <= result.toRound; round++) {
			andDifferencesIfNotNull(result.getIntermediateStateDifference(round), toMergeIn.getIntermediateStateDifference(round));
		}
		
		return result;
	}
	
	private void andDifferencesIfNotNull(Difference first, Difference second) {
		if (first != null && second != null) {
			first.and(second);
		}
	}
	
	private Differential computeBackwardDifferentialFromMiddle(int fromRound, int toRound,
		ByteArray firstStartingState, DifferenceIterator stateBitsIterator, ByteArray expandedKey) {

		ByteArray secondStartingState;
		
		Differential accumulated = new Differential(fromRound, toRound);
		Differential current = new Differential(fromRound, toRound);
		Differential first = new Differential(fromRound, toRound);
		
		fillDifferential(accumulated);
		cipher.setExpandedKey(expandedKey);
		computeBackward(first, firstStartingState);
		
		stateBitsIterator.reset();
		
		while(stateBitsIterator.hasNext()) {
			secondStartingState = stateBitsIterator.next();
			computeBackward(current, secondStartingState);
			current.xor(first);
			accumulated.or(current);
		}
		
		return accumulated;
	}
	
	private Differential computeForwardDifferentialFromMiddle(int fromRound, int toRound, 
		ByteArray firstStartingState, DifferenceIterator stateBitsIterator, ByteArray expandedKey) {
		
		ByteArray secondStartingState;
		
		Differential accumulated = new Differential(fromRound, toRound);
		Differential current = new Differential(fromRound, toRound);
		Differential first = new Differential(fromRound, toRound);
		
		fillDifferential(accumulated);
		cipher.setExpandedKey(expandedKey);
		computeForward(first, firstStartingState);

		stateBitsIterator.reset();
		
		while(stateBitsIterator.hasNext()) {
			secondStartingState = stateBitsIterator.next();
			computeForward(current, secondStartingState);
			current.xor(first);
			accumulated.or(current);
		}
		
		return accumulated;
	}
	
	private Differential computeBackwardDifferentialToMiddle(int fromRound, int toRound, 
		DifferenceIterator keyDifferenceIterator, ByteArray firstExpandedKey) {
		
		ByteArray state = new ByteArray(cipher.getStateSize());
		ByteArray secondKeyPart;
		ByteArray secondExpandedKey = null;
		ByteArray firstKeyPart = cipher.computeKeyPart(firstExpandedKey, toRound);
		
		Differential accumulated = new Differential(fromRound, toRound);
		Differential current = new Differential(fromRound, toRound);
		Differential first = new Differential(fromRound, toRound);
		
		fillDifferential(accumulated);
		cipher.setExpandedKey(firstExpandedKey);
		computeBackward(first, state);
		
		accumulated.firstSecretKey = firstExpandedKey.splice(0, cipher.getKeySize());
		accumulated.keyDifference = new ByteArray(cipher.getKeySize());
		
		while(keyDifferenceIterator.hasNext()) {
			secondKeyPart = keyDifferenceIterator.next();
			accumulated.keyDifference.or(secondKeyPart);
			
			secondKeyPart.xor(firstKeyPart);
			secondExpandedKey = cipher.computeExpandedKey(secondKeyPart, toRound);
			
			cipher.setExpandedKey(secondExpandedKey);
			computeBackward(current, state);
			
			current.xor(first);
			accumulated.or(current);
		}
		
		accumulated.secondSecretKey = secondExpandedKey.splice(0, cipher.getKeySize());
		return accumulated;
	}
	
	private Differential computeForwardDifferentialToMiddle(int fromRound, int toRound, 
		DifferenceIterator keyDifferenceIterator, ByteArray firstExpandedKey) {
		
		ByteArray state = new ByteArray(cipher.getStateSize());
		ByteArray secondKeyPart;
		ByteArray secondExpandedKey = null;
		ByteArray firstKeyPart = cipher.computeKeyPart(firstExpandedKey, fromRound);
		
		Differential accumulated = new Differential(fromRound, toRound);
		Differential current = new Differential(fromRound, toRound);
		Differential first = new Differential(fromRound, toRound);
		
		fillDifferential(accumulated);
		cipher.setExpandedKey(firstExpandedKey);
		computeForward(first, state);
		
		accumulated.firstSecretKey = firstExpandedKey.splice(0, cipher.getKeySize());
		accumulated.keyDifference = new ByteArray(cipher.getKeySize());
		
		while(keyDifferenceIterator.hasNext()) {
			secondKeyPart = keyDifferenceIterator.next();
			accumulated.keyDifference.or(secondKeyPart);
			
			secondKeyPart.xor(firstKeyPart);
			secondExpandedKey = cipher.computeExpandedKey(secondKeyPart, fromRound);
			
			cipher.setExpandedKey(secondExpandedKey);
			computeForward(current, state);
			
			current.xor(first);
			accumulated.or(current);
		}
		
		accumulated.secondSecretKey = secondExpandedKey.splice(0, cipher.getKeySize());
		return accumulated;
	}
	
	private void computeBackward(Differential differential, ByteArray startingState) {
		ByteArray key = null;
		ByteArray state = startingState.clone();
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;
		
		if (toRound == cipher.getNumRounds() && cipher.hasKeyInjectionInRound(toRound + 1)) {
			key = cipher.getRoundKey(toRound + 1);
			differential.setKeyDifference(toRound + 1, key);
			storeIntermediateState(differential, state, key, toRound + 1);
		}
		
		differential.setStateDifference(toRound, state);
		
		for (int round = toRound; round >= fromRound; round--) {
			if (cipher.hasKeyInjectionInRound(round)) {
				key = cipher.getRoundKey(round);
				differential.setKeyDifference(round, key);
			}
			
			if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundEnd(round)) {
				storeIntermediateState(differential, state, key, round);
			}
			
			state = cipher.decryptRound(state, round);
			differential.setStateDifference(round - 1, state);
			
			if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundBegin(round)) {
				storeIntermediateState(differential, state, key, round);
			}
		}
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			key = cipher.getRoundKey(0);
			differential.setKeyDifference(0, key);
			storeIntermediateState(differential, state, key, 0);
		}
	}
	
	private void computeForward(Differential differential, ByteArray startingState) {
		ByteArray key = null;
		ByteArray state = startingState.clone();
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;
		
		differential.setStateDifference(fromRound - 1, state);
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			key = cipher.getRoundKey(0);
			differential.setKeyDifference(0, key);
			storeIntermediateState(differential, state, key, 0);
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (cipher.hasKeyInjectionInRound(round)) {
				key = cipher.getRoundKey(round);
				differential.setKeyDifference(round, key);
				
				if (cipher.injectsKeyAtRoundBegin(round)) {
					storeIntermediateState(differential, state, key, round);
				}
			}
			
			state = cipher.encryptRound(state, round);
			differential.setStateDifference(round, state);
			
			if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundEnd(round)) {
				storeIntermediateState(differential, state, key, round);
			}
		}
		
		if (toRound == cipher.getNumRounds() && cipher.hasKeyInjectionInRound(toRound + 1)) {
			key = cipher.getRoundKey(toRound + 1);
			differential.setKeyDifference(toRound + 1, key);
			storeIntermediateState(differential, state, key, toRound + 1);
		}
	}
	
	private void fillDifferential(Differential differential) {
		ByteArray key = new ByteArray(cipher.getStateSize());
		ByteArray state = new ByteArray(cipher.getStateSize());
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;
		
		differential.setStateDifference(fromRound - 1, state.clone());
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			differential.setKeyDifference(0, key.clone());
			storeIntermediateState(differential, state, key, 0);
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (cipher.hasKeyInjectionInRound(round)) {
				differential.setKeyDifference(round, key.clone());
				storeIntermediateState(differential, state, key, round);
			}
			
			differential.setStateDifference(round, state.clone());
		}
		
		if (toRound == cipher.getNumRounds() && cipher.hasKeyInjectionInRound(toRound + 1)) {
			differential.setKeyDifference(toRound + 1, key.clone());
			storeIntermediateState(differential, state, key, toRound + 1);
		}
	}
	
	private void storeIntermediateState(Differential differential, ByteArray state, ByteArray key, int round) {
		state = state.clone().xor(key);
		differential.setIntermediateStateDifference(round, state);
	}
	
}
