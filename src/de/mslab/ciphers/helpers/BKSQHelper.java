package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public abstract class BKSQHelper extends AbstractCipherHelper {
	
	public int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential) {
		ByteArray stateDifference;
		int sum = 0;
		int fromRound = stateDifferential.fromRound;
		int toRound = stateDifferential.toRound;
		
		if (fromRound == 1) {
			sum += countActiveKeyBytes(
				0, keyDifferential.getKeyDifference(0).getDelta(), 
				keyDifferential.getStateDifference(0).getDelta(), 
				keyDifferential.getIntermediateStateDifference(0).getDelta()
			);
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			stateDifference = stateDifferential.getIntermediateStateDifference(round).getDelta();
			sum += stateDifference.countNumActiveBytes();
			sum += countActiveKeyBytes(
				round, keyDifferential.getKeyDifference(round).getDelta(),
				keyDifferential.getIntermediateStateDifference(round).getDelta(), 
				keyDifferential.getStateDifference(round).getDelta()
			);
		}
		
		return sum;
	}
	
	public boolean shareActiveNonLinearOperations(Differential deltaDifferential, Differential nablaDifferential) {
		int fromRound = deltaDifferential.fromRound;
		int toRound = deltaDifferential.toRound;
		
		if (fromRound == 1) {
			if (shareActiveNonLinearOperationsInKey(0, deltaDifferential, nablaDifferential)) {
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
	
	protected abstract int countActiveKeyBytes(int round, ByteArray key, 
		ByteArray stateBeforeKeyAddition, ByteArray stateAfterKeyAddition);
	
}
