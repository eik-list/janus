package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class ThreeFishHelper extends AbstractCipherHelper {
	
	public int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential) {
		ByteArray stateDifference;
		int sum = 0;
		int fromRound = stateDifferential.fromRound;
		int toRound = stateDifferential.toRound;
		
		if (fromRound == 1) {
			stateDifference = stateDifferential.getIntermediateStateDifference(0).getDelta();
			sum += countMixOperations(stateDifference);
		} else {
			stateDifference = stateDifferential.getStateDifference(fromRound - 1).getDelta();
			sum += countMixOperations(stateDifference);
		}
		
		for (int round = fromRound; round < toRound; round++) {
			stateDifference = stateDifferential.getStateDifference(round).getDelta();
			sum += countMixOperations(stateDifference);
		}
		
		return sum;
	}
	
	public boolean shareActiveNonLinearOperations(Differential deltaDifferential, Differential nablaDifferential) {
		int fromRound = deltaDifferential.fromRound;
		int toRound = deltaDifferential.toRound;
		
		if (fromRound == 1) {
			if (shareActiveNonLinearOperationsInIntermediateState(0, deltaDifferential, nablaDifferential)) {
				return true;
			}
		} else {
			if (shareActiveNonLinearOperationsInState(fromRound - 1, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}

		if (checkExpandedKey(deltaDifferential, nablaDifferential)) {
			return true;
		}
		
		for (int round = fromRound; round < toRound; round++) {
			if (shareActiveNonLinearOperationsInState(round, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (round % 4 == 0 && shareActiveNonLinearOperationsInKey(round, deltaDifferential, nablaDifferential)) {
				return true;
			}
		}
		
		return false;
	}

	protected boolean shareActiveNonLinearOperationsInKey(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.keyDifferences.get(round).sharesActiveBitsWith(
			nablaDifferential.keyDifferences.get(round)
		);
	}
	
	protected boolean shareActiveNonLinearOperationsInIntermediateState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		long[] deltaLongs = deltaDifferential.intermediateStateDifferences.get(round).getDelta().readLongs(); 
		long[] nablaLongs = nablaDifferential.intermediateStateDifferences.get(round).getDelta().readLongs();
		
		for (int i = 0; i < deltaLongs.length; i += 2) {
			if ((deltaLongs[i] != 0 || deltaLongs[i + 1] != 0) 
				&& (nablaLongs[i] != 0 || nablaLongs[i + 1] != 0)) {
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean shareActiveNonLinearOperationsInState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		long[] deltaLongs = deltaDifferential.stateDifferences.get(round).getDelta().readLongs(); 
		long[] nablaLongs = nablaDifferential.stateDifferences.get(round).getDelta().readLongs();
		
		for (int i = 0; i < deltaLongs.length; i += 2) {
			if ((deltaLongs[i] != 0 || deltaLongs[i + 1] != 0) 
				&& (nablaLongs[i] != 0 || nablaLongs[i + 1] != 0)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean checkExpandedKey(Differential deltaDifferential, Differential nablaDifferential) {
		ByteArray deltaKey = deltaDifferential.firstSecretKey.clone();
		deltaKey.xor(deltaDifferential.secondSecretKey);
		
		ByteArray nablaKey = nablaDifferential.firstSecretKey.clone();
		nablaKey.xor(nablaDifferential.secondSecretKey);
		
		return deltaKey.sharesActiveBitsWith(nablaKey);
	}
	
	/**
	 * Returns the number of non-zero bits in the byte array.  
	 */
	private int countMixOperations(ByteArray state) {
		int sum = 0;
		long[] stateAsLong = state.readLongs();
		
		for (int i = 0; i < stateAsLong.length; i += 2) {
			if (stateAsLong[i] != 0 || stateAsLong[i + 1] != 0) {
				sum++;
			}
		}
		
		return sum;
	}
	
}
