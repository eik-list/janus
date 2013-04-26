package de.mslab.ciphers.helpers;

import de.mslab.core.Difference;
import de.mslab.core.Differential;

/**
 * Compares two differentials for independency, that means, if they share active bits.
 * 
 */
public class BitwiseDifferentialComparator implements DifferentialComparator {
	
	/**
	 * Compares two differentials for independency, that means, if they share active bits.
	 * Comparing differentials means testing all round keys and round states. 
	 * Returns <code>true</code> if this differential shares any active bit with 
	 * the given other in any state or key.
	 */
	public boolean shareActiveNonLinearOperations(Differential deltaDifferential, Differential nablaDifferential) {
		int fromRound = deltaDifferential.fromRound;
		int toRound = deltaDifferential.toRound;
		Difference deltaStateDifference = deltaDifferential.getStateDifference(fromRound - 1);
		Difference nablaStateDifference = nablaDifferential.getStateDifference(fromRound - 1);
		
		if (deltaStateDifference.sharesActiveBitsWith(nablaStateDifference)) {
			return true;
		}
		
		Difference deltaKeyDifference;
		Difference nablaKeyDifference;
		
		if (fromRound == 1 
			&& deltaDifferential.getKeyDifference(0) != null
			&& nablaDifferential.getKeyDifference(0) != null) {
			deltaKeyDifference = deltaDifferential.getKeyDifference(0);
			nablaKeyDifference = nablaDifferential.getKeyDifference(0);
			
			if (deltaKeyDifference.sharesActiveBitsWith(nablaKeyDifference)) {
				return true;
			}
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			deltaStateDifference = deltaDifferential.getStateDifference(round);
			nablaStateDifference = nablaDifferential.getStateDifference(round);
			
			if (deltaStateDifference.sharesActiveBitsWith(nablaStateDifference)) {
				return true;
			}
			
			if (deltaDifferential.getKeyDifference(round) != null 
				&& nablaDifferential.getKeyDifference(round) != null) {
				deltaKeyDifference = deltaDifferential.getKeyDifference(round);
				nablaKeyDifference = nablaDifferential.getKeyDifference(round);
				
				if (deltaKeyDifference.sharesActiveBitsWith(nablaKeyDifference)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
