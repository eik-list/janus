package de.mslab.diffbuilder;

import de.mslab.errors.InvalidArgumentError;
import de.mslab.utils.MathUtil;

public class BytewiseDifferenceBuilder extends AbstractDifferenceBuilder {
	
	public long initializeAndGetNumDifferences(int dimension, int numBytes) throws InvalidArgumentError {
		if (dimension < 1) {
			throw new InvalidArgumentError("Dimension needs to be at least 1");
		}
		
		if (dimension > 8 * numBytes) {
			throw new InvalidArgumentError("Dimension needs to be equal to or less than 8 * numBytes. " +
					"Given dimension = " + dimension + ", numBits = " + (8 * numBytes) + ".");
		}
		
		this.dimension = dimension;
		this.numUnits = numBytes;
		this.numBytes = numBytes;
		this.weight = (int)Math.ceil((double)dimension / Byte.SIZE); // d = 8 => w = 1
		this.numResults = MathUtil.computeBinomialCoefficient(numBytes, weight);
		
		this.bitPositions = new int[dimension + 1];
		this.bitStartPositions = new int[dimension + 1];
		this.bitEndPositions = new int[dimension + 1];
		
		resetBitPositionsAndValue();
		return numResults;
	}
	
	protected void setValueAtPosition(int position, boolean v) {
		value.setAtEnd(position, v);
	}
	
	protected DifferenceIterator storeNextValue() {
		return new BytewiseDifferenceIterator(value, dimension, bitPositions);
	}
	
}
