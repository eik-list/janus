package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidArgumentError;

/**
 * Computes in increasing manner all n-bit values with less or equal 
 * a maximum number of '1' bit, while all other bits are zero. 
 */
public class BitwiseDifferenceBuilder implements DifferenceBuilder {
	
	private int dimension;
	private int index;
	private int numBytes;
	private int numResults;
	
	private int[] bitPositions;
	private ByteArray value; 
	
	public long initializeAndGetNumDifferences(int dimension, int numBytes) throws InvalidArgumentError {
		if (dimension > 8 * numBytes) {
			throw new InvalidArgumentError("Dimension needs to be equal to or less than 8 * numBytes. " +
					"Given dimension = " + dimension + ", numBits = " + (8 * numBytes) + ".");
		}
		
		if (dimension < 1) {
			throw new InvalidArgumentError("Dimension needs to be at least 1");
		}
		
		this.dimension = dimension;
		this.index = 0;
		this.numBytes = numBytes;
		this.numResults = numBytes * Byte.SIZE - dimension + 1;
		this.value = new ByteArray(this.numBytes);
		return numResults;
	}
	
	public synchronized DifferenceIterator next() {
		if (index >= numResults) {
			return null;
		}
		
		bitPositions = new int[dimension];
		value = new ByteArray(numBytes);
		
		for (int i = 0; i < dimension; i++) {
			bitPositions[i] = index + i;
			value.setBit(index + i, true);
		}
		
		index++;
		return storeNextValue();
	}
	
	protected void setValueAtPosition(int position, boolean v) {
		value.setBitAtEnd(position, v);
	}
	
	protected DifferenceIterator storeNextValue() {
		return new BitwiseDifferenceIterator(value, dimension, bitPositions);
	}
	
	public int getCurrentWeight() {
		return this.dimension;
	}
	
	public long getNumResults() {
		return this.numResults;
	}
	
}
