package de.mslab.bicliquesearch.helpers;

import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;

public class PRESENTBicliqueRater extends DefaultBicliqueRater {

	public int determineScoreForBiclique(Biclique biclique) {
		final int scoreForDataComplexity = super.determineScoreForBiclique(biclique);
		final int scoreForFirstNablaKey = determineScoreForActiveBitsInFirstRoundKey(
			biclique.nablaDifferential.firstSecretKey, biclique.nablaDifferential.secondSecretKey
		);
		return scoreForDataComplexity + scoreForFirstNablaKey; 
	}
	
	private int determineScoreForActiveBitsInFirstRoundKey(ByteArray firstKey, ByteArray secondKey) {
		int score = 0;
		firstKey = firstKey.clone().xor(secondKey);
		
		
		for (int i = 0; i < 8; i++) {
			if (firstKey.get(i) != 0) {
				score -= 4;
			}
		}
		
		return score;
	}
	
}
