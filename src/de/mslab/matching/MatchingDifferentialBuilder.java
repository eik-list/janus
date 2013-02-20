package de.mslab.matching;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.ciphers.helpers.DifferentialActiveComponentsCounter;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.core.Difference;
import de.mslab.core.Differential;
import de.mslab.diffbuilder.DifferentialBuilder;

/**
 * <p>
 * Calculates the complexity for the matching part of a biclique attack on a given cipher
 * on those rounds which are not covered by a given biclique.
 * </p>
 * 
 */
public class MatchingDifferentialBuilder {
	
	private MatchingContext context;
	private DifferentialBuilder differentialBuilder;
	private MatchingDifferentialBuilderResult result;

	private Biclique biclique;
	private RoundBasedBlockCipher cipher;
	private DifferentialActiveComponentsCounter counter;
	
	private ByteArray emptyState;
	private ByteArray[][] keys = new ByteArray[2][2];
	
	// private ByteArray[] previousSummedKeys = new ByteArray[2];
	// private ByteArray[] summedKeys = new ByteArray[2];
	// private Differential forwardKeyDifferential, backwardKeyDifferential;
	
	public MatchingDifferentialBuilder() {
		differentialBuilder = new DifferentialBuilder();
	}
	
	/**
	 * Calculates the complexity for the matching part of a biclique attack on a given cipher
	 * on those rounds which are not covered by a given biclique.
	 * @param context 
	 */
	public MatchingDifferentialBuilderResult findMinNumActiveBytes(MatchingContext context) {
		setUp(context);
		
		computeKeys();
		determineBicliqueDimension();
		determineBicliqueRounds();
		computeMatchingRoundsRange();
		computeDifferentials();
		
		for (context.iteration = 1; context.iteration < context.numIterations; context.iteration++) {
			computeKeys();
			computeDifferentialsForRoundAndByte(result.bestMatchingRound, result.bestMatchingByte);
		}
		
		tearDown();
		return result;
	}
	
	private void setUp(MatchingContext context) {
		this.context = context;
		result = new MatchingDifferentialBuilderResult();
		differentialBuilder.cipher = context.cipher;
		biclique = context.biclique;
		cipher = context.cipher;
		counter = context.counter;
		context.iteration = 0;
	}
	
	private void tearDown() {
		biclique = null;
		cipher = null;
		counter = null;
		differentialBuilder.cipher = null;
		context = null;
	}

	/**
	 * Determines the number of rounds which are covered by the given biclique.
	 */
	private void determineBicliqueRounds() {
		result.numBicliqueRounds = biclique.deltaDifferential.toRound - biclique.deltaDifferential.fromRound + 1;
	}
	
	/**
	 * Determines the biclique dimension, i. e. the number of active bytes in the secret key.
	 */
	private void determineBicliqueDimension() {
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
			// E. g.: Biclique: 1 - 3 => Matching: 4 - 10
			setMatchingRoundsRange(delta.toRound + 1, cipher.getNumRounds());
		} else { 
			// E. g.: Biclique: 8 - 10 => Matching: 1 - 7
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
	private void computeDifferentials() {
		result.minNumActiveBytes = -1;
		
		if (context.isMatchingFixed) {
			computeDifferentialsForRoundAndByte(context.matchingRound, context.activeMatchingByte);
		} else {
			int stateSize = cipher.getStateSize();
			
			for (int matchingRound = result.matchingFromRound; matchingRound < result.matchingToRound; matchingRound++) {
				for (int activeMatchingByte = 0; activeMatchingByte < stateSize; activeMatchingByte++) {
					computeDifferentialsForRoundAndByte(matchingRound, activeMatchingByte);
				}
			}
		}
	}
	
	private void computeDifferentialsForRoundAndByte(int matchingRound, int activeMatchingByte) {
		final int i = 1;
		final int j = 1;
		
		ByteArray matchingState = emptyState.clone();
		matchingState.set(activeMatchingByte, emptyState.get(activeMatchingByte) ^ 0xFF);
		
		Differential p_to_v = differentialBuilder.computeForwardDifferential(
			result.matchingFromRound, matchingRound, emptyState, emptyState, keys[i][0], keys[i][j]
		);
		Differential s_to_v = differentialBuilder.computeBackwardDifferential(
			matchingRound + 1, result.matchingToRound, emptyState, emptyState, keys[0][j], keys[i][j]
		);
		
		Differential v_to_p = differentialBuilder.computeBackwardDifferentialFromMiddle(
			result.matchingFromRound, matchingRound, emptyState, matchingState, keys[i][j] 
		);
		Differential v_to_s = differentialBuilder.computeForwardDifferentialFromMiddle(
			matchingRound + 1, result.matchingToRound, emptyState, matchingState, keys[i][j]
		);
		
		Differential p_mergedto_v = mergeIntoForwardDifferential(p_to_v, v_to_p);
		Differential s_mergedto_v = mergeIntoBackwardDifferential(s_to_v, v_to_s);
		
		if (context.iteration == 0) {
			int numActiveBytes = counter.countActiveComponents(p_mergedto_v) + counter.countActiveComponents(s_mergedto_v);
			
			if (result.minNumActiveBytes == -1 || numActiveBytes < result.minNumActiveBytes) {
				result.minNumActiveBytes = numActiveBytes;
				result.bestMatchingRound = matchingRound;
				result.bestMatchingByte = activeMatchingByte;
				
				result.p_to_v = p_to_v;
				result.s_to_v = s_to_v;
				result.v_to_p = v_to_p;
				result.v_to_s = v_to_s;
				
				result.p_mergedto_v = p_mergedto_v;
				result.s_mergedto_v = s_mergedto_v;
			}
		} else {
			mergeDifferentials(result.p_mergedto_v, p_mergedto_v);
			mergeDifferentials(result.s_mergedto_v, s_mergedto_v);
		}
		
		if (context.iteration + 1 == context.numIterations) {
			result.minNumActiveBytes = counter.countActiveComponents(result.p_mergedto_v)
				+ counter.countActiveComponents(result.s_mergedto_v);
			// result.minNumActiveBytes = counter.countActiveComponents(result.p_mergedto_v, forwardKeyDifferential) 
			// 	+ counter.countActiveComponents(result.s_mergedto_v, backwardKeyDifferential);
		}
	}
	
	private void mergeDifferentials(Differential original, Differential toMerge) {
		ByteArray originalState, mergeState;
		ByteArray originalIntermediateState, mergeIntermediateState;
		
		for (int round = original.fromRound; round <= original.toRound; round++) {
			originalState = original.getStateDifference(round).getDelta();
			mergeState = toMerge.getStateDifference(round).getDelta();
			originalState.or(mergeState);
			
			if (original.getIntermediateStateDifference(round) != null) {
				originalIntermediateState = original.getIntermediateStateDifference(round).getDelta();
				mergeIntermediateState = toMerge.getIntermediateStateDifference(round).getDelta();
				originalIntermediateState.or(mergeIntermediateState);
			}
		}
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
	
}
