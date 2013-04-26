package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class WhirlpoolCipherHelper extends AbstractCipherHelper {

	public int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential) {
		ByteArray stateDifference, keyDifference;
		int sum = 0;
		int fromRound = stateDifferential.fromRound;
		int toRound = stateDifferential.toRound;
		
		if (fromRound == 1) {
			stateDifference = stateDifferential.getIntermediateStateDifference(0).getDelta();
			sum += stateDifference.countNumActiveBytes();
			keyDifference = keyDifferential.getKeyDifference(0).getDelta();
			sum += keyDifference.countNumActiveBytes();
		} else {
			stateDifference = stateDifferential.getStateDifference(fromRound - 1).getDelta();
			sum += stateDifference.countNumActiveBytes();
		}
		
		for (int round = fromRound; round < toRound; round++) {
			stateDifference = stateDifferential.getStateDifference(round).getDelta();
			sum += stateDifference.countNumActiveBytes();
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			keyDifference = keyDifferential.getKeyDifference(round).getDelta();
			sum += keyDifference.countNumActiveBytes();
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
	
}
