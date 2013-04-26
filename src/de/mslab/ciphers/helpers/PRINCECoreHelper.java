package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class PRINCECoreHelper extends AbstractCipherHelper {
	
	public boolean shareActiveNonLinearOperations(Differential deltaDifferential, Differential nablaDifferential) {
		int fromRound = deltaDifferential.fromRound;
		int toRound = deltaDifferential.toRound;
		
		if (fromRound == 1) {
			if (shareActiveNonLinearOperationsInIntermediateState(0, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		if (toRound == 11) {
			for (int round = fromRound; round < toRound; round++) {
				if (shareActiveNonLinearOperationsInState(round, deltaDifferential, nablaDifferential)) {
					return true;
				}
			}
			
			if (shareActiveNonLinearOperationsInIntermediateState(12, deltaDifferential, nablaDifferential)) {
				return true;
			}
		} else {
			for (int round = fromRound; round <= toRound; round++) {
				if (shareActiveNonLinearOperationsInState(round, deltaDifferential, nablaDifferential)) {
					return true;
				}
			}
		}
		
		if (fromRound != 6) {
			if (shareActiveNonLinearOperationsInKey(fromRound, deltaDifferential, nablaDifferential)) {
				return true;
			}
		} else if (toRound != 6) {
			if (shareActiveNonLinearOperationsInKey(toRound, deltaDifferential, nablaDifferential)) {
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
		}
		
		if (toRound == 11) {
			for (int round = fromRound; round < toRound; round++) {
				stateDifference = stateDifferential.getStateDifference(round).getDelta();
				sum += stateDifference.countNumActiveNibbles();
			}

			stateDifference = stateDifferential.getIntermediateStateDifference(12).getDelta();
			sum += stateDifference.countNumActiveNibbles();
		} else {
			for (int round = fromRound; round <= toRound; round++) {
				stateDifference = stateDifferential.getStateDifference(round).getDelta();
				sum += stateDifference.countNumActiveNibbles();
			}
		}
		
		return sum;
	}
	
}
