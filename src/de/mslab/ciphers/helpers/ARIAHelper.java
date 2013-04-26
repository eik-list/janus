package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public abstract class ARIAHelper extends AbstractCipherHelper {
	
	protected int numRounds;
	
	public int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential) {
		ByteArray stateDifference, keyDifference;
		int sum = 0;
		int fromRound = stateDifferential.fromRound;
		int toRound = stateDifferential.toRound;
		
		if (toRound == numRounds) {
			keyDifference = keyDifferential.getKeyDifference(toRound + 1).getDelta();
			sum += keyDifference.countNumActiveBytes();
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			stateDifference = stateDifferential.getIntermediateStateDifference(round).getDelta();
			sum += stateDifference.countNumActiveBytes();
		}
		
		return sum;
	}
	
	public boolean shareActiveNonLinearOperations(Differential deltaDifferential, Differential nablaDifferential) {
		int fromRound = deltaDifferential.fromRound;
		int toRound = deltaDifferential.toRound;
		
		if (toRound == numRounds) {
			if (shareActiveNonLinearOperationsInKey(toRound + 1, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (shareActiveNonLinearOperationsInIntermediateState(round, deltaDifferential, nablaDifferential)
				|| shareActiveNonLinearOperationsInKey(round, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		return false;
	}
	
}
