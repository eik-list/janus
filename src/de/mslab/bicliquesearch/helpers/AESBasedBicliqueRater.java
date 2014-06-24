package de.mslab.bicliquesearch.helpers;

import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class AESBasedBicliqueRater extends DefaultBicliqueRater {
	
	public int determineScoreForBiclique(Biclique biclique) {
		return determineDataComplexity(biclique)
			+ determineIfActiveByteInFirstRow(biclique.nablaDifferential);
	}
	
	private int determineIfActiveByteInFirstRow(Differential nablaDifferential) {
		if (hasActiveByteInFirstRow(nablaDifferential.keyDifferences.get(nablaDifferential.toRound).getDelta())) {
			return -1000;
		} else {
			return 0;
		}
	}
	
	private boolean hasActiveByteInFirstRow(ByteArray key) {
		for (int i = 0; i < 4; i++) {
			if (key.get(i) != 0) {
				return true;
			}
		}
		
		return false;
	}
	
}
