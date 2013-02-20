package de.mslab.ciphers.helpers;

import de.mslab.ciphers.PRESENT;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class PRESENTHelper extends AbstractCipherHelper {
	
	protected int numRounds = PRESENT.NUM_ROUNDS;
	
	public int countActiveComponents(Differential stateDifferential, Differential keyDifferential) {
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
	
	public boolean shareActiveComponents(Differential deltaDifferential, Differential nablaDifferential) {
		int fromRound = deltaDifferential.fromRound;
		int toRound = deltaDifferential.toRound;
		
		if (toRound == numRounds) {
			if (checkKey(toRound + 1, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		for (int round = fromRound; round < toRound; round++) {
			if (checkIntermediateState(round, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (checkKey(round, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean checkKey(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.keyDifferences.get(round).sharesActiveNibblesWith(
			nablaDifferential.keyDifferences.get(round)
		);
	}
	
	protected boolean checkIntermediateState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.intermediateStateDifferences.get(round).sharesActiveNibblesWith(
			nablaDifferential.intermediateStateDifferences.get(round)
		);
	}
	
	protected boolean checkState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.stateDifferences.get(round).sharesActiveNibblesWith(
			nablaDifferential.stateDifferences.get(round)
		);
	}
	
	protected int countActiveKeyNibblesInSBox(ByteArray keyDifference) {
		int sum = 0;
		
		if (keyDifference.getNibble(0) != 0) {
			sum++;
		}
		
		return sum;
	}
	
}
