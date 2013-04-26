package de.mslab.ciphers.helpers;

import de.mslab.ciphers.KLEIN64;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;
import de.mslab.utils.Logger;

public abstract class KLEINHelper extends AbstractCipherHelper {
	
	protected int numRounds;
	protected Logger logger = Logger.getLogger();
	
	public int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential) {
		ByteArray stateDifference, keyDifference;
		int sum = 0;
		int fromRound = stateDifferential.fromRound;
		int toRound = stateDifferential.toRound;
		
		if (toRound == numRounds) {
			keyDifference = keyDifferential.getKeyDifference(toRound + 1).getDelta();
			sum += countActiveKeyNibblesInSBox(keyDifference);
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			stateDifference = stateDifferential.getIntermediateStateDifference(round).getDelta();
			sum += stateDifference.countNumActiveNibbles();
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			keyDifference = keyDifferential.getKeyDifference(round).getDelta();
			sum += countActiveKeyNibblesInSBox(keyDifference);
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
		
		for (int round = fromRound; round < toRound; round++) {
			if (shareActiveNonLinearOperationsInIntermediateState(round, deltaDifferential, nablaDifferential)) {
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
	
	protected boolean shareActiveNonLinearOperationsInKey(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.keyDifferences.get(round).sharesActiveNibblesWith(
			nablaDifferential.keyDifferences.get(round)
		);
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
	
	protected int countActiveKeyNibblesInSBox(ByteArray keyDifference) {
		int halfLengthNibble = KLEIN64.NUM_BYTES_IN_64_BIT;
		int sum = 0;
		
		if (keyDifference.getNibble(halfLengthNibble + 2) != 0) {
			sum++;
		}
		if (keyDifference.getNibble(halfLengthNibble + 3) != 0) {
			sum++;
		}
		if (keyDifference.getNibble(halfLengthNibble + 4) != 0) {
			sum++;
		}
		if (keyDifference.getNibble(halfLengthNibble + 5) != 0) {
			sum++;
		}
		
		return sum;
	}
	
}
