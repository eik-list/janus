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
import de.mslab.diffbuilder.MatchingDifferentialBuilder;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.errors.InvalidArgumentError;
import de.mslab.utils.Logger;

/**
 * <p>
 * Calculates the complexity for the matching part of a biclique attack on a given cipher
 * on those rounds which are not covered by a given biclique.
 * </p>
 */
public class MatchingFinder {
	
	private Logger logger = Logger.getLogger();
	private MatchingContext context;
	private MatchingFinderResult result;
	
	private Biclique biclique;
	private RoundBasedBlockCipher cipher;
	private RecomputedOperationsCounter counter;
	
	private ByteArray emptyState;
	private ByteArray[][] keys = new ByteArray[2][2];
	private BitwiseDifferenceIterator deltaDifferenceIterator; 
	private BitwiseDifferenceIterator nablaDifferenceIterator;
	private DifferenceBuilder matchingDifferenceBuilder;
	
	private MatchingDifferentialBuilder matchingDifferentialBuilder;
	
	/**
	 * Calculates the complexity for the matching part of a biclique attack on a given cipher
	 * on those rounds which are not covered by a given biclique.
	 * @param context 
	 */
	public MatchingFinderResult findOptimalMatching(MatchingContext context) {
		setUp(context);
		
		computeKeys();
		determineBicliqueRoundsAndDimension();
		createMatchingDifferenceBuilder();
		determineMatchingRoundsRange();
		logStart();
		doFindOptimalMatching();
		
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
		
		this.result = new MatchingFinderResult();
		this.matchingDifferentialBuilder = new MatchingDifferentialBuilder();
		this.matchingDifferentialBuilder.setCipher(cipher);
	}
	
	private void tearDown() {
		this.biclique = null;
		this.cipher = null;
		this.counter = null;
		this.context = null;
		this.matchingDifferentialBuilder.setCipher(null);
		this.matchingDifferentialBuilder = null;
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
		keys[0][j] = nabla.firstSecretKey.clone();
		
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
		
		int[] deltaActivePositions = findActiveBitPositionsInDifference(delta.keyDifference);
		int[] nablaActivePositions = findActiveBitPositionsInDifference(nabla.keyDifference);
		
		deltaDifferenceIterator = new BitwiseDifferenceIterator(
			delta.keyDifference, biclique.dimension, deltaActivePositions
		);
		nablaDifferenceIterator = new BitwiseDifferenceIterator(
			nabla.keyDifference, biclique.dimension, nablaActivePositions
		);
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
	 * Determines the number of rounds which are covered by the given biclique
	 * and the biclique dimension, i. e. the number of active bytes in the secret key.
	 */
	private void determineBicliqueRoundsAndDimension() {
		result.numBicliqueRounds = biclique.deltaDifferential.toRound - biclique.deltaDifferential.fromRound + 1;
		result.dimension = biclique.dimension;
	}
	
	/**
	 * Determines the rounds for the matching from the given cipher. 
	 * Be r the number of rounds in the cipher, e. g. r = 10.
	 * If the biclique is located at the beginning of the cipher, e. g. covering rounds 1 - 3, 
	 * then we will try to match at state after round 4, 5, 6, ... 10.
	 * If the biclique is located at the end of the cipher, e. g. covering rounds 7 - 10, 
	 * then we will try to match at state after round 1, 2, 3, ... 6.
	 */
	private void determineMatchingRoundsRange() {
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
	private void doFindOptimalMatching() throws InvalidArgumentError {
		result.minRecomputedOperations = -1;
		
		if (context.isMatchingFixed) {
			computeDifferentialsForFixedMatching();
		} else {
			if (context.numMatchingBits < 1) {
				throw new InvalidArgumentError("You must specify a number between 1 <= b <= n bits for matching, " +
					"where n is the state size of the used cipher. " +
					"Your current choice for b is " + context.numMatchingBits + " and n is " + cipher.getStateSize() +
					"."
				);
			}
			
			computeDifferentialsForAllRoundsAndBits();
		}
	}
	
	private void computeDifferentialsForFixedMatching() {
		final int[] matchingStateActiveBitPositions = findActiveBitPositionsInDifference(context.matchingStateDifference);
		final DifferenceIterator matchingBitsIterator = new BitwiseDifferenceIterator(
			emptyState, matchingStateActiveBitPositions.length, matchingStateActiveBitPositions
		);
		
		deltaDifferenceIterator.reset();
		nablaDifferenceIterator.reset();
		
		Differential p_to_v = matchingDifferentialBuilder.computeForwardDifferential(
			result.matchingFromRound, context.matchingRound, nablaDifferenceIterator, keys[0][0], 
			biclique.nablaDifferential.toRound
		);
		Differential s_to_v = matchingDifferentialBuilder.computeBackwardDifferential(
			context.matchingRound + 1, result.matchingToRound, deltaDifferenceIterator, keys[0][0], 
			biclique.deltaDifferential.fromRound
		);
		
		computeDifferentialsFromMiddleAndMerge(context.matchingRound, matchingBitsIterator, p_to_v, s_to_v);
		logProgress(context.matchingRound);
	}
	
	private void computeDifferentialsForAllRoundsAndBits() {
		for (int matchingRound = result.matchingFromRound; matchingRound < result.matchingToRound; matchingRound++) {
			deltaDifferenceIterator.reset();
			nablaDifferenceIterator.reset();
			
			Differential p_to_v = matchingDifferentialBuilder.computeForwardDifferential(
				result.matchingFromRound, matchingRound, nablaDifferenceIterator, keys[0][0], 
				biclique.nablaDifferential.toRound
			);
			Differential s_to_v = matchingDifferentialBuilder.computeBackwardDifferential(
				matchingRound + 1, result.matchingToRound, deltaDifferenceIterator, keys[0][0], 
				biclique.deltaDifferential.fromRound
			);
			
			//logger.info("p -> v {0}", p_to_v);
			//logger.info("s <- v {0}", s_to_v);
			
			long numStateDifferences = matchingDifferenceBuilder.initializeAndGetNumDifferences(
				context.numMatchingBits, cipher.getStateSize()
			);
			
			for (int i = 0; i < numStateDifferences; i++) {
				computeDifferentialsFromMiddleAndMerge(matchingRound, matchingDifferenceBuilder.next(), p_to_v, s_to_v);
			}
			
			logProgress(matchingRound);
		}
	}
	
	private void computeDifferentialsFromMiddleAndMerge(int matchingRound, DifferenceIterator matchingBitsIterator, 
		Differential p_to_v, Differential s_to_v) {
		final int i = 1;
		final int j = 1;
		
		Differential v_to_p = matchingDifferentialBuilder.computeBackwardDifferentialFromMiddle(
			result.matchingFromRound, matchingRound, emptyState, matchingBitsIterator, keys[i][j] 
		);
		Differential v_to_s = matchingDifferentialBuilder.computeForwardDifferentialFromMiddle(
			matchingRound + 1, result.matchingToRound, emptyState, matchingBitsIterator, keys[i][j]
		);
		
		//logger.info("p <- v {0}", v_to_p);
		//logger.info("s -> v {0}", v_to_s);
		
		Differential p_mergedto_v = mergeIntoForwardDifferential(p_to_v, v_to_p);
		Differential s_mergedto_v = mergeIntoBackwardDifferential(s_to_v, v_to_s);
		
		int numRecomputedOperations = counter.countRecomputedOperations(p_mergedto_v) 
			+ counter.countRecomputedOperations(s_mergedto_v);
		
		if (result.minRecomputedOperations == -1 || numRecomputedOperations < result.minRecomputedOperations) {
			result.minRecomputedOperations = numRecomputedOperations;
			result.bestMatchingRound = matchingRound;
			
			result.p_to_v = p_to_v;
			result.s_to_v = s_to_v;
			result.v_to_p = v_to_p;
			result.v_to_s = v_to_s;
			
			result.p_mergedto_v = p_mergedto_v;
			result.s_mergedto_v = s_mergedto_v;
		}
		
		result.minRecomputedOperations = counter.countRecomputedOperations(result.p_mergedto_v)
			+ counter.countRecomputedOperations(result.s_mergedto_v);
	}
	
	private int[] findActiveBitPositionsInDifference(ByteArray keyDifference) {
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
	
	private void logProgress(int matchingRound) {
		logger.info("Searched matching for {0} in round {1}. Minimum # recomputed operations: {2}", 
			cipher.getName(), matchingRound, result.minRecomputedOperations);
	}
	
	private void logStart() {
		logger.info("Started search for optimal matching for {2} in round interval [{0} ... {1}]", 
			result.matchingFromRound, result.matchingToRound, cipher.getName());
	}
	
}






