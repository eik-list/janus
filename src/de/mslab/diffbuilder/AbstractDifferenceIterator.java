package de.mslab.diffbuilder;

import java.util.Arrays;

import de.mslab.core.ByteArray;

public abstract class AbstractDifferenceIterator implements DifferenceIterator {
	
	protected ByteArray difference;
	protected long index = 0;
	protected long maximum;
	protected int numBytes;
	protected int[] activePositions;
	
	public AbstractDifferenceIterator(ByteArray difference, int dimension) {
		init(difference, dimension);
	}
	
	protected AbstractDifferenceIterator(ByteArray difference, int dimension, int[] activePositions) {
		init(difference, dimension);
		this.activePositions = cleanActivePositions(activePositions);
	}

	public boolean hasNext() {
		return index < maximum;
	}
	
	public ByteArray next() {
		if (hasNext()) {
			updateDifference();
			return difference;
		} else {
			return null;
		}
	}
	
	public void reset() {
		this.index = 0;
	}
	
	protected void init(ByteArray difference, int dimension) {
		this.difference = difference.clone();
		this.numBytes = difference.length();
		this.maximum = (1L << dimension) - 1;
		this.index = 0;
	}
	
	protected static int[] cleanActivePositions(int[] activePositions) {
		int[] newActivePositions = new int[activePositions.length];
		int numActivePositions = 0;
		
		for (int i = 0; i < activePositions.length; i++) {
			if (activePositions[i] > 0) {
				newActivePositions[numActivePositions++] = activePositions[i] - 1;
			}
		}
		
		return Arrays.copyOfRange(newActivePositions, 0, numActivePositions);
	}
	
	protected abstract void updateDifference();
	
}
