package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;
import de.mslab.core.Nibble;

public class SerpentNibblewiseDifferenceIterator extends AbstractDifferenceIterator {
	
	public SerpentNibblewiseDifferenceIterator(ByteArray difference, int dimension, int[] bitPositions) {
		super(difference, dimension, bitPositions);
	}
	
	protected void updateDifference() {
		index++;
		difference = new ByteArray(numBytes);
		int shift = 0, j;
		final int mask = 0xF;
		// index = 0010 0001 => in i = 0 => j = 0001, i = 1 => j = 0010 
		
		for (int i = 0; i < activePositions.length; i++) {
			j = (int)(index >>> shift) & mask;
			difference.setBit(activePositions[i], (j & 1) != 0);
			difference.setBit(activePositions[i] + 32, (j & 2) != 0);
			difference.setBit(activePositions[i] + 64, (j & 4) != 0);
			difference.setBit(activePositions[i] + 96, (j & 8) != 0);
			shift += Nibble.SIZE;
		}
	}
	
}
