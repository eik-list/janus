package de.mslab.diffbuilder;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;
import de.mslab.utils.Logger;

public abstract class AbstractDifferentialBuilder implements DifferentialBuilder {

	protected BackwardDifferentialsHelper backwardDifferentialsHelper;
	protected RoundBasedBlockCipher cipher;
	protected ForwardDifferentialsHelper forwardDifferentialsHelper;
	protected Logger logger;
	
	protected AbstractDifferentialBuilder() {
		logger = Logger.getLogger();
		forwardDifferentialsHelper = new ForwardDifferentialsHelper();
		backwardDifferentialsHelper = new BackwardDifferentialsHelper();
	}

	public RoundBasedBlockCipher getCipher() {
		return cipher;
	}
	
	public void setCipher(RoundBasedBlockCipher cipher) {
		this.cipher = 
		this.forwardDifferentialsHelper.cipher = 
		this.backwardDifferentialsHelper.cipher = cipher;
	}
	
	public synchronized Differential computeBackwardDifferential(int fromRound, int toRound, 
		DifferenceIterator keyDifferenceIterator, ByteArray firstExpandedKey, int keyDifferenceRound) {
		
		return computeDifferential(
			fromRound, toRound, keyDifferenceIterator, firstExpandedKey, keyDifferenceRound, 
			backwardDifferentialsHelper
		);
	}
	
	public synchronized Differential computeForwardDifferential(int fromRound, int toRound, 
		DifferenceIterator keyDifferenceIterator, ByteArray firstExpandedKey, int keyDifferenceRound) {
		
		return computeDifferential(
			fromRound, toRound, keyDifferenceIterator, firstExpandedKey, keyDifferenceRound, 
			forwardDifferentialsHelper
		);
	}
	
	protected Differential computeDifferential(int fromRound, int toRound, 
		DifferenceIterator keyDifferenceIterator, ByteArray firstExpandedKey, int keyDifferenceRound, 
		DifferentialsHelper differentialHelper) {
		
		ByteArray state = new ByteArray(cipher.getStateSize());
		ByteArray secondKeyPart;
		ByteArray secondExpandedKey = null;
		ByteArray firstKeyPart = cipher.computeKeyPart(firstExpandedKey, keyDifferenceRound);
		
		Differential accumulated = new Differential(fromRound, toRound);
		Differential current = new Differential(fromRound, toRound);
		Differential first = new Differential(fromRound, toRound);
		
		fillDifferential(accumulated);
		cipher.setExpandedKey(firstExpandedKey);
		differentialHelper.computeDifferential(first, state);
		
		accumulated.firstSecretKey = firstExpandedKey.splice(0, cipher.getKeySize());
		accumulated.keyDifference = new ByteArray(cipher.getKeySize());
		
		while(keyDifferenceIterator.hasNext()) {
			secondKeyPart = keyDifferenceIterator.next();
			accumulated.keyDifference.or(secondKeyPart);
			
			secondKeyPart.xor(firstKeyPart);
			secondExpandedKey = cipher.computeExpandedKey(secondKeyPart, keyDifferenceRound);
			
			cipher.setExpandedKey(secondExpandedKey);
			differentialHelper.computeDifferential(current, state);
			
			current.xor(first);
			accumulated.or(current);
		}
		
		accumulated.secondSecretKey = secondExpandedKey.splice(0, cipher.getKeySize());
		return accumulated;
	}
	
	protected void fillDifferential(Differential differential) {
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
	
	protected void storeIntermediateState(Differential differential, ByteArray state, ByteArray key, int round) {
		state = state.clone().xor(key);
		differential.setIntermediateStateDifference(round, state);
	}
	
}

abstract class DifferentialsHelper {
	
	RoundBasedBlockCipher cipher;
	
	abstract void computeDifferential(Differential differential, ByteArray startingState);
	
	void storeIntermediateState(Differential differential, ByteArray state, ByteArray key, int round) {
		state = state.clone().xor(key);
		differential.setIntermediateStateDifference(round, state);
	}
	
}

class ForwardDifferentialsHelper extends DifferentialsHelper {
	
	synchronized void computeDifferential(Differential differential, ByteArray startingState) {
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
	
}

class BackwardDifferentialsHelper extends DifferentialsHelper {
	
	synchronized void computeDifferential(Differential differential, ByteArray startingState) {
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
	
}
