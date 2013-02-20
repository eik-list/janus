package de.mslab.diffbuilder;

import java.util.Arrays;

import de.mslab.core.ByteArray;

public class BytewiseDifferenceIterator extends AbstractDifferenceIterator {
	
	public BytewiseDifferenceIterator(ByteArray difference, int dimension, int[] activePositions) {
		super(difference, dimension, activePositions);
	}
	
	public BytewiseDifferenceIterator(ByteArray difference, int dimension) {
		this(difference, dimension, findActivePositions(difference));
	}
	
	protected static int[] findActivePositions(ByteArray difference) {
		int length = difference.length();
		int[] activePositions = new int[length];
		int numActivePositions = 0;
		
		for (int i = 0; i < length; i++) {
			if (difference.get(i) != 0) {
				activePositions[numActivePositions++] = i;
			}
		}
		
		activePositions = Arrays.copyOfRange(activePositions, 0, numActivePositions);
		return activePositions;
	}
	
	protected void updateDifference() {
		index++;
		difference = new ByteArray(numBytes);
		int shift = 0;
		int mask = 0xff;
		
		for (int i = 0; i < activePositions.length; i++) {
			difference.set(activePositions[i], (short)((index >>> shift) & mask));
			shift += Byte.SIZE;
		}
	}
	
}
