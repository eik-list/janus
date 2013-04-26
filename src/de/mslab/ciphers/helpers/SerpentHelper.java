package de.mslab.ciphers.helpers;

import de.mslab.ciphers.Serpent;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class SerpentHelper extends AbstractCipherHelper {
	
	private static final int LAST_BIT = 1;
	
	public int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential) {
		ByteArray intermediateStateDifference;
		int sum = 0;
		int fromRound = stateDifferential.fromRound;
		int toRound = stateDifferential.toRound;
		
		for (int round = fromRound; round <= toRound; round++) {
			intermediateStateDifference = stateDifferential.getIntermediateStateDifference(round).getDelta();
			sum += intermediateStateDifference.countNumActiveNibbles();
		}
		
		return sum;
	}
	
	public boolean shareActiveNonLinearOperations(Differential deltaDifferential, Differential nablaDifferential) {
		int fromRound = deltaDifferential.fromRound;
		int toRound = deltaDifferential.toRound;
		
		for (int round = fromRound; round <= toRound; round++) {
			if (shareActiveNonLinearOperationsInIntermediateState(round, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		if (toRound == Serpent.NUM_ROUNDS + 1) {
			if (shareActiveNonLinearOperationsInIntermediateState(toRound, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean shareActiveNonLinearOperationsInIntermediateState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return shareActiveSBoxes(
			deltaDifferential.intermediateStateDifferences.get(round).getDelta().readUInts(), 
			nablaDifferential.intermediateStateDifferences.get(round).getDelta().readUInts()
		);
	}
	
	protected boolean shareActiveNonLinearOperationsInState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return shareActiveSBoxes(
			deltaDifferential.stateDifferences.get(round).getDelta().readUInts(), 
			nablaDifferential.stateDifferences.get(round).getDelta().readUInts()
		);
	}
	
	protected boolean shareActiveSBoxes(int[] x, int[] y) {
		int isFirstActive, isSecondActive, bit;
		
		for (bit = 0; bit < 32; bit++) {
			isFirstActive = ((x[0] >>> bit) & LAST_BIT)
				| ((x[1] >>> bit) & LAST_BIT)
				| ((x[2] >>> bit) & LAST_BIT)
				| ((x[3] >>> bit) & LAST_BIT);
			
			isSecondActive = ((y[0] >>> bit) & LAST_BIT)
				| ((y[1] >>> bit) & LAST_BIT)
				| ((y[2] >>> bit) & LAST_BIT)
				| ((y[3] >>> bit) & LAST_BIT);
			
			if (isFirstActive != 0 && isSecondActive != 0) {
				return true;
			}
        }
		
		return false;
	}
	
}
