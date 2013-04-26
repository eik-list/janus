package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

abstract class AESHelper extends AbstractCipherHelper {
	
	public int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential) {
		ByteArray stateDifference;
		int sum = 0;
		int fromRound = stateDifferential.fromRound;
		int toRound = stateDifferential.toRound;
		
		if (fromRound == 1) {
			stateDifference = stateDifferential.getIntermediateStateDifference(0).getDelta();
			sum += stateDifference.countNumActiveBytes();
			sum += countActiveKeyBytes(0, keyDifferential.getKeyDifference(0).getDelta());
		} else {
			stateDifference = stateDifferential.getStateDifference(fromRound - 1).getDelta();
			sum += stateDifference.countNumActiveBytes();
		}
		
		for (int round = fromRound; round < toRound; round++) {
			stateDifference = stateDifferential.getStateDifference(round).getDelta();
			sum += stateDifference.countNumActiveBytes();
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			sum += countActiveKeyBytes(round, keyDifferential.getKeyDifference(round).getDelta());
		}
		
		return sum;
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
	
	protected abstract int countActiveKeyBytes(int round, ByteArray key);
	
}
