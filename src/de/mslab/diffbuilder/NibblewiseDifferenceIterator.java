package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;
import de.mslab.core.Nibble;

public class NibblewiseDifferenceIterator extends AbstractDifferenceIterator {
	
	public NibblewiseDifferenceIterator(ByteArray difference, int dimension, int[] bitPositions) {
		super(difference, dimension, bitPositions);
	}
	
	protected void updateDifference() {
		index++;
		difference = new ByteArray(numBytes);
		int shift = 0;
		int mask = 0xF;
		
		for (int i = 0; i < activePositions.length; i++) {
			difference.setNibble(activePositions[i], (short)((index >>> shift) & mask));
			shift += Nibble.SIZE;
		}
		
		//Logger.getLogger().info("{3} difference {0}, index {1} activePositions {2}", difference, index, 
		//	Arrays.toString(activePositions), ID);
	}
	
}
