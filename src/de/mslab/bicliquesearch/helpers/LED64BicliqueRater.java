package de.mslab.bicliquesearch.helpers;

import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class LED64BicliqueRater extends DefaultBicliqueRater {
	
	public int determineScoreForBiclique(Biclique biclique) {
		final ByteArray deltaKeyDifference = getKeyDifference(biclique.deltaDifferential);
		final ByteArray nablaKeyDifference = getKeyDifference(biclique.nablaDifferential);
		
		int numRecomputedComponentsInForwardDirection = 0;
		int numRecomputedComponentsInBackwardDirection = 0;
		final int[][] columns = new int[][]{{ 0,5,10,15 },{ 1,6,11,12 },{ 2,7,8,13 },{ 3,4,9,14 }};
		
		for (int column = 0; column < 4; column++) {
			inner: 
			for (int row = 0; row < 4; row++) {
				if (deltaKeyDifference.getNibble(column + 4 * row) != 0) {
					numRecomputedComponentsInForwardDirection += 4;
					break inner;
				}
			}
		}
		
		for (int column = 0; column < 4; column++) {
			inner: 
			for (int row = 0; row < 4; row++) {
				if (nablaKeyDifference.getNibble(columns[column][row]) != 0) {
					numRecomputedComponentsInBackwardDirection += 4;
					break inner;
				}
			}
		}
		
		return (16 - numRecomputedComponentsInForwardDirection) 
			 + (16 - numRecomputedComponentsInBackwardDirection);
	}
	
	private ByteArray getKeyDifference(Differential differential) {
		return differential.firstSecretKey.clone().xor(differential.secondSecretKey);
	}
	
}
