package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class LBlockHelper extends AbstractCipherHelper {

	public int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential) {
		ByteArray stateDifference, keyDifference;
		int sum = 0;
		int fromRound = stateDifferential.fromRound;
		int toRound = stateDifferential.toRound;
		
		for (int round = fromRound; round <= toRound; round++) {
			stateDifference = stateDifferential.getIntermediateStateDifference(round).getDelta();
			stateDifference = stateDifference.splice(0, 4);
			sum += stateDifference.countNumActiveNibbles();
			
			keyDifference = keyDifferential.getKeyDifference(round).getDelta().splice(0, 1);
			sum += keyDifference.countNumActiveNibbles();
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
	
	protected boolean shareActiveNonLinearOperationsInKey(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.keyDifferences.get(round).sharesActiveNibblesWith(
			nablaDifferential.keyDifferences.get(round)
		);
	}
	
	protected boolean shareActiveNonLinearOperationsInIntermediateState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		ByteArray deltaState = deltaDifferential.intermediateStateDifferences.get(round).getDelta().splice(0, 4);
		ByteArray nablaState = nablaDifferential.intermediateStateDifferences.get(round).getDelta().splice(0, 4);
		return deltaState.sharesActiveNibblesWith(nablaState);
	}
	
	protected boolean shareActiveNonLinearOperationsInState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		ByteArray deltaState = deltaDifferential.stateDifferences.get(round).getDelta().splice(0, 4);
		ByteArray nablaState = nablaDifferential.stateDifferences.get(round).getDelta().splice(0, 4);
		return deltaState.sharesActiveNibblesWith(nablaState);
	}
	
}
