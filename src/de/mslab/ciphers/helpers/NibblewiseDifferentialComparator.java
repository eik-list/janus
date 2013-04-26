package de.mslab.ciphers.helpers;

import de.mslab.core.Difference;
import de.mslab.core.Differential;

/**
 * Compares two differentials for independency, that means, if they share active bytes.
 */
public class NibblewiseDifferentialComparator implements DifferentialComparator {
	
	/**
	 * Compares two differentials for independency, that means, if they share active bytes.
	 * Comparing differentials means testing all round keys and round states. 
	 * Returns <code>true</code> if this differential shares any active byte with 
	 * the given other in any state or key.
	 */
	public boolean shareActiveNonLinearOperations(Differential deltaDifferential, Differential nablaDifferential) {
		int fromRound = deltaDifferential.fromRound;
		int toRound = deltaDifferential.toRound;
		Difference deltaKeyDifference;
		Difference nablaKeyDifference;
		Difference deltaStateDifference = deltaDifferential.stateDifferences.get(fromRound - 1);
		Difference nablaStateDifference = nablaDifferential.stateDifferences.get(fromRound - 1);
		
		if (deltaStateDifference.sharesActiveNibblesWith(nablaStateDifference)) {
			return true;
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			deltaKeyDifference = deltaDifferential.keyDifferences.get(round);
			nablaKeyDifference = nablaDifferential.keyDifferences.get(round);
			
			if (deltaKeyDifference != null && nablaKeyDifference != null) {
				if (deltaKeyDifference.sharesActiveNibblesWith(nablaKeyDifference)) {
					return true;
				}
			}
			
			deltaStateDifference = deltaDifferential.stateDifferences.get(round);
			nablaStateDifference = nablaDifferential.stateDifferences.get(round);
			
			if (deltaStateDifference.sharesActiveNibblesWith(nablaStateDifference)) {
				return true;
			}
		}
		
		return false;
	}
	
}
