package de.mslab.diffbuilder;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class BKRDifferentialBuilder {
	
	public RoundBasedBlockCipher cipher;
	
	public BKRDifferentialBuilder() {
		
	}
	
	public synchronized Differential computeBackwardDifferentialFromRoundKeys(int fromRound, int toRound, 
		ByteArray startingStateDifference, ByteArray startingKeyDifference, ByteArray firstExpandedKey) {
		
		ByteArray secondState = startingStateDifference;
		ByteArray firstState = new ByteArray(0, secondState.length());
		ByteArray secondExpandedKey;
		
		if (cipher.canInvertKeySchedule()) {
			ByteArray firstKeyPart = cipher.computeKeyPart(firstExpandedKey, toRound);
			ByteArray secondKeyPart = firstKeyPart.clone();
			secondKeyPart.xor(startingKeyDifference);
			secondExpandedKey = computeExpandedKey(secondKeyPart, toRound);
		} else { // The first key is a secret key, yet, not expanded
			secondExpandedKey = firstExpandedKey.clone();
			secondExpandedKey.xor(startingKeyDifference);
			
			cipher.setKey(firstExpandedKey);
			firstExpandedKey = cipher.getExpandedKey();
			
			cipher.setKey(secondExpandedKey);
			secondExpandedKey = cipher.getExpandedKey();
		}

		return computeBackwardDifferentialFromRoundKeys(
			fromRound, toRound, firstState, secondState, firstExpandedKey, secondExpandedKey
		);
	}
	
	/**
	 * Generates a forward differential trail for the given cipher from fromRound to toRound,
	 * including toRound. 
	 * E. g. if fromRound = 8 and toRound = 10, the differential will start at round 8 and move
	 * forwards to round 10.
	 * 
	 * Used by the BicliqueFinder to compute biclique differentials:
	 * s <- c (different c, different round keys)
	 * s <- c (different c, different round keys), since \Delta c = 0 in DifferenceBuilder
	 * Backward differential (fromround <- toround) : startKeyDifference, startStateDifference
	 * 
	 * @param fromRound The first round of the differential that is included in the differential.
	 * @param firstStartingState The starting state at round toRound for the first computation.
	 * @param secondStartingState The starting state at round toRound for the second computation.
	 * @param firstExpandedKey The expanded key which is used for the first computation.
	 * @param secondExpandedKey The expanded key which is used for the second computation.
	 * @return The forward differential.
	 */
	public synchronized Differential computeBackwardDifferentialFromRoundKeys(int fromRound, int toRound, 
		ByteArray firstStartingState, ByteArray secondStartingState, 
		ByteArray firstExpandedKey, ByteArray secondExpandedKey) {
		
		Differential differential = new Differential(fromRound, toRound);
		differential.firstSecretKey = firstExpandedKey.splice(0, cipher.getKeySize());
		differential.secondSecretKey = secondExpandedKey.splice(0, cipher.getKeySize());
		
		initializeDifferential(differential, cipher.getStateSize(), cipher.getKeySize());
		cipher.setExpandedKey(firstExpandedKey);
		computeBackward(differential, firstStartingState);
		cipher.setExpandedKey(secondExpandedKey);
		computeBackward(differential, secondStartingState);
		return differential;
	}
	
	public synchronized Differential computeForwardDifferentialFromRoundKeys(int fromRound, int toRound, 
		ByteArray startingStateDifference, ByteArray startingKeyDifference, ByteArray firstExpandedKey) {
		
		ByteArray secondState = startingStateDifference;
		ByteArray firstState = new ByteArray(0, secondState.length());
		ByteArray secondExpandedKey;
		
		if (cipher.canInvertKeySchedule()) { 
			ByteArray firstKeyPart = cipher.computeKeyPart(firstExpandedKey, fromRound);
			ByteArray secondKeyPart = firstKeyPart.clone();
			
			secondKeyPart.xor(startingKeyDifference);
			secondExpandedKey = computeExpandedKey(secondKeyPart, fromRound);
		} else { // The first key is a secret key, it is not yet expanded
			secondExpandedKey = firstExpandedKey.clone();
			secondExpandedKey.xor(startingKeyDifference);
			
			cipher.setKey(firstExpandedKey);
			firstExpandedKey = cipher.getExpandedKey();
			
			cipher.setKey(secondExpandedKey);
			secondExpandedKey = cipher.getExpandedKey();
		}
		
		return computeForwardDifferentialFromRoundKeys(
			fromRound, toRound, firstState, secondState, firstExpandedKey, secondExpandedKey
		);
	}
	
	/**
	 * Generates a backward differential trail for the given cipher from toRound to fromRound,
	 * including fromRound. 
	 * E. g. if fromRound = 8 and toRound = 10, the differential will start at round 10 and move
	 * backwards to round 8.
	 * 
	 * Used by the BicliqueFinder to compute biclique differentials:
	 * s -> c (different s, different round keys)
	 * s -> c (same s, different round keys), da \Delta s = 0 in DifferenceBuilder
	 * Forward differential (fromround -> toround) : startKeyDifference, startStateDifference
	 * 
	 * @param fromRound The first round of the differential that is included in the differential.
	 * @param toRound The last round of the differential that is included in the differential.
	 * @param firstStartingState The starting state at round fromRound for the first computation.
	 * @param secondStartingState The starting state at round fromRound for the second computation.
	 * @param firstExpandedKey The expanded key which is used for the first computation.
	 * @param secondExpandedKey The expanded key which is used for the second computation.
	 * @return The backward differential.
	 */
	public synchronized Differential computeForwardDifferentialFromRoundKeys(int fromRound, int toRound, 
		ByteArray firstStartingState, ByteArray secondStartingState, ByteArray firstExpandedKey, ByteArray secondExpandedKey) {
		
		Differential differential = new Differential(fromRound, toRound);
		differential.firstSecretKey = firstExpandedKey.splice(0, cipher.getKeySize());
		differential.secondSecretKey = secondExpandedKey.splice(0, cipher.getKeySize());
		
		initializeDifferential(differential, cipher.getStateSize(), cipher.getKeySize());
		cipher.setExpandedKey(firstExpandedKey);
		computeForward(differential, firstStartingState);
		cipher.setExpandedKey(secondExpandedKey);
		computeForward(differential, secondStartingState);
		return differential;
	}
	
	private void initializeDifferential(Differential differential, int stateSize, int keySize) {
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;
		
		differential.setStateDifference(fromRound - 1, new ByteArray(stateSize));
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			differential.setKeyDifference(0, new ByteArray(stateSize));
			differential.setIntermediateStateDifference(0, new ByteArray(stateSize));
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			differential.setStateDifference(round, new ByteArray(stateSize));
			
			if (cipher.hasKeyInjectionInRound(round)) {
				differential.setIntermediateStateDifference(round, new ByteArray(stateSize));
				differential.setKeyDifference(round, new ByteArray(stateSize));
			}
		}
		
		if (toRound == cipher.getNumRounds() && cipher.hasKeyInjectionInRound(toRound + 1)) {
			differential.setIntermediateStateDifference(toRound + 1, new ByteArray(stateSize));
			differential.setKeyDifference(toRound + 1, new ByteArray(stateSize));
		}
	}
	
	private void computeForward(Differential differential, ByteArray startingState) {
		ByteArray key = null;
		ByteArray state = startingState.clone();
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;
		
		differential.getStateDifference(fromRound - 1).xor(state);
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			key = cipher.getRoundKey(0);
			differential.getKeyDifference(0).xor(key);
			storeIntermediateState(differential, state, key, 0);
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (cipher.hasKeyInjectionInRound(round)) {
				key = cipher.getRoundKey(round);
				differential.getKeyDifference(round).xor(key);
				
				if (cipher.injectsKeyAtRoundBegin(round)) {
					storeIntermediateState(differential, state, key, round);
				}
			}
			
			state = cipher.encryptRound(state, round);
			differential.getStateDifference(round).xor(state);
			
			if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundEnd(round)) {
				storeIntermediateState(differential, state, key, round);
			}
		}
		
		if (toRound == cipher.getNumRounds() && cipher.hasKeyInjectionInRound(toRound + 1)) {
			key = cipher.getRoundKey(toRound + 1);
			differential.getKeyDifference(toRound + 1).xor(key);
			storeIntermediateState(differential, state, key, toRound + 1);
		}
	}
	
	private void computeBackward(Differential differential, ByteArray startingState) {
		ByteArray key = null;
		ByteArray state = startingState.clone();
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;

		if (toRound == cipher.getNumRounds() && cipher.hasKeyInjectionInRound(toRound + 1)) {
			key = cipher.getRoundKey(toRound + 1);
			differential.getKeyDifference(toRound + 1).xor(key);
			storeIntermediateState(differential, state, key, toRound + 1);
		}
		
		differential.getStateDifference(toRound).xor(state);
		
		for (int round = toRound; round >= fromRound; round--) {
			if (cipher.hasKeyInjectionInRound(round)) {
				key = cipher.getRoundKey(round);
				differential.getKeyDifference(round).xor(key);
			}
			
			if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundEnd(round)) {
				storeIntermediateState(differential, state, key, round);
			}
			
			state = cipher.decryptRound(state, round);
			differential.getStateDifference(round - 1).xor(state);
			
			if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundBegin(round)) {
				storeIntermediateState(differential, state, key, round);
			}
		}
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			key = cipher.getRoundKey(0);
			differential.getKeyDifference(0).xor(key);
			storeIntermediateState(differential, state, key, 0);
		}
	}
	
	private ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		if (cipher.canInvertKeySchedule()) {
			return cipher.computeExpandedKey(keyPart, round);
		} else {
			cipher.setKey(keyPart);
			return cipher.getExpandedKey();
		}
	}
	
	private void storeIntermediateState(Differential differential, ByteArray state, ByteArray key, int round) {
		state = state.clone();
		state.xor(key);
		differential.getIntermediateStateDifference(round).xor(state);
	}
	
}
