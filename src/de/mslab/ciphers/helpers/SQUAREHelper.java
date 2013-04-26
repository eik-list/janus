package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class SQUAREHelper extends AbstractCipherHelper {
	
	public int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential) {
		ByteArray stateDifference;
		int sum = 0;
		int fromRound = stateDifferential.fromRound;
		int toRound = stateDifferential.toRound;
		
		for (int round = fromRound; round <= toRound; round++) {
			stateDifference = stateDifferential.getIntermediateStateDifference(round).getDelta();
			sum += stateDifference.countNumActiveBytes();
		}
		
		return sum;
	}
	
	public boolean shareActiveNonLinearOperations(Differential deltaDifferential, Differential nablaDifferential) {
		int fromRound = deltaDifferential.fromRound;
		int toRound = deltaDifferential.toRound;
		
		for (int round = fromRound; round <= toRound; round++) {
			if (shareActiveNonLinearOperationsInIntermediateState(round, deltaDifferential, nablaDifferential) 
				|| shareActiveNonLinearOperationsInKey(round, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		return false;
	}
	
}
