package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class MCryptonHelper extends AbstractCipherHelper {
	
	private static final int[] SBOX_AFFECTED_NIBBLES_IN_KEYS = { 0, 5, 10, 15 };
	
	public MCryptonHelper() {
		super();
	}
	
	public boolean shareActiveNonLinearOperations(Differential deltaDifferential, Differential nablaDifferential) {
		int fromRound = deltaDifferential.fromRound;
		int toRound = deltaDifferential.toRound;
		
		if (fromRound == 1) {
			if (shareActiveNonLinearOperationsInIntermediateState(0, deltaDifferential, nablaDifferential)
				|| shareActiveNonLinearOperationsInKey(0, deltaDifferential, nablaDifferential)) {
				return true;
			}
		} else {
			if (shareActiveNonLinearOperationsInState(fromRound - 1, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		for (int round = fromRound; round < toRound; round++) {
			if (shareActiveNonLinearOperationsInState(round, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (shareActiveNonLinearOperationsInKey(round, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		return false;
	}
	
	public int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential) {
		ByteArray stateDifference;
		int sum = 0;
		int fromRound = stateDifferential.fromRound;
		int toRound = stateDifferential.toRound;
		
		if (fromRound == 1) {
			stateDifference = stateDifferential.getIntermediateStateDifference(0).getDelta();
			sum += stateDifference.countNumActiveNibbles();
			sum += countActiveNonLinearOperationsInKey(keyDifferential.getKeyDifference(0).getDelta());
		} else {
			stateDifference = stateDifferential.getStateDifference(fromRound - 1).getDelta();
			sum += stateDifference.countNumActiveNibbles();
		}
		
		for (int round = fromRound; round < toRound; round++) {
			stateDifference = stateDifferential.getStateDifference(round).getDelta();
			sum += stateDifference.countNumActiveNibbles();
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			sum += countActiveNonLinearOperationsInKey(keyDifferential.getKeyDifference(round).getDelta());
		}
		
		return sum;
	}
	
	protected int countActiveNonLinearOperationsInKey(ByteArray keyDifference) {
		int numActiveOperations = 0;
		
		for (int index : SBOX_AFFECTED_NIBBLES_IN_KEYS) {
			if (keyDifference.getNibble(index) != 0) {
				numActiveOperations++;
			}
		}
		
		return numActiveOperations;
	}
	
	protected boolean shareActiveNonLinearOperationsInKey(int round, Differential deltaDifferential, Differential nablaDifferential) {
		for (int index : SBOX_AFFECTED_NIBBLES_IN_KEYS) {
			if ((deltaDifferential.keyDifferences.get(round).getDelta().getNibble(index) != 0)
				&& (nablaDifferential.keyDifferences.get(round).getDelta().getNibble(index) != 0)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean shareActiveNonLinearOperationsInIntermediateState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.intermediateStateDifferences.get(round).sharesActiveNibblesWith(
			nablaDifferential.intermediateStateDifferences.get(round)
		);
	}
	
	protected boolean shareActiveNonLinearOperationsInState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.stateDifferences.get(round).sharesActiveNibblesWith(
			nablaDifferential.stateDifferences.get(round)
		);
	}
	
}
