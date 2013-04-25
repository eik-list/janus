package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidArgumentError;

/**
 * Computes in increasing manner all n-bit values with less or equal 
 * a maximum number of '1' bit, while all other bits are zero. 
 * 
 */
public class BitwiseDifferenceBuilder extends AbstractDifferenceBuilder {
	
	/**
	 * Default number of return difference values.
	 */
	public static final long DEFAULT_NUM_RESULTS = 100L;
	
	public long initializeAndGetNumDifferences(int dimension, int numBytes) throws InvalidArgumentError {
		if (dimension > 8 * numBytes) {
			throw new InvalidArgumentError("Dimension needs to be equal to or less than 8 * numBytes. " +
					"Given dimension = " + dimension + ", numBits = " + (8 * numBytes) + ".");
		}
		
		if (dimension < 1) {
			throw new InvalidArgumentError("Dimension needs to be at least 1");
		}
		
		this.dimension = dimension;
		this.numUnits = numBytes * Byte.SIZE;
		this.numBytes = numBytes;
		this.weight = dimension; // d = w
		this.numResults = DEFAULT_NUM_RESULTS; // MathUtil.computeBinomialCoefficient(numBytes, weight);
		
		this.value = new ByteArray(this.numBytes);
		this.bitPositions = new int[dimension];
		
		return numResults;
	}
	
	public synchronized DifferenceIterator next() {
		if (isFirst) {
			isFirst = false;
			return storeNextValue();
		}
		
		value = new ByteArray(numBytes);
		int bitPosition;
		int[] setBitPositions = new int[numUnits];
		this.bitPositions = new int[dimension];
		
		for (int i = 0; i < dimension; i++) {
			bitPosition = (int)(Math.random() * numUnits);
			
			if (setBitPositions[bitPosition] == 1) {
				bitPosition = 0;
				
				while (setBitPositions[bitPosition] == 1) {
					bitPosition++;
				}
			}
			
			setBitPositions[bitPosition] = 1;
			this.bitPositions[i] = bitPosition;
			value.setBit(bitPosition, true);
		}
		
		return storeNextValue();
	}
	
	public void setNumResults(long numResults) {
		this.numResults = numResults;
	}
	
	protected void setValueAtPosition(int position, boolean v) {
		value.setBitAtEnd(position, v);
	}
	
	protected DifferenceIterator storeNextValue() {
		return new BitwiseDifferenceIterator(value, dimension, bitPositions);
	}
	
}
