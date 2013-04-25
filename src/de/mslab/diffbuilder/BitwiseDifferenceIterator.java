package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;

public class BitwiseDifferenceIterator extends AbstractDifferenceIterator {
	
	public BitwiseDifferenceIterator(ByteArray difference, int dimension, int[] activePositions) {
		super(difference, dimension);
		this.activePositions = activePositions;
	}
	
	protected void updateDifference() {
		index++;
		difference = new ByteArray(numBytes);
		int shift = 0;
		int mask = 1;
		
		for (int i = 0; i < activePositions.length; i++) {
			difference.setBit(activePositions[i], ((index >>> shift) & mask) == 1);
			shift++;
		}
	}
	
}
