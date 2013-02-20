package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;
import de.mslab.core.Nibble;
import de.mslab.errors.InvalidArgumentError;
import de.mslab.utils.MathUtil;

/**
 * Computes in increasing manner all n-byte values with less or equal 
 * a maximum number of '1' bytes, while all other bytes are zero. 
 * 
 */
public class NibblewiseDifferenceBuilder extends AbstractDifferenceBuilder {
	
	public long initializeAndGetNumDifferences(int dimension, int numBytes) throws InvalidArgumentError {
		if (dimension > 8 * numBytes) {
			throw new InvalidArgumentError("Dimension needs to be equal to or less than 8 * numBytes. " +
				"Given dimension = " + dimension + ", numBits = " + (8 * numBytes) + ".");
		}
		
		if (dimension < 1) {
			throw new InvalidArgumentError("Dimension needs to be at least 1");
		}
		
		this.dimension = dimension;
		this.numUnits = numBytes * 2; // numComponents are bytes => numNibbles = 2 * bytes
		this.numBytes = numBytes;
		this.weight = (int)Math.ceil((double)dimension / Nibble.SIZE);
		this.numResults = MathUtil.computeBinomialCoefficient(numUnits, weight); // d = 8 => w = 2
		
		this.value = new ByteArray(this.numBytes);
		this.bitPositions = new int[dimension + 1];
		this.bitStartPositions = new int[dimension + 1];
		this.bitEndPositions = new int[dimension + 1];
		
		resetBitPositionsAndValue();
		return numResults;
	}
	
	protected void setValueAtPosition(int position, boolean v) {
		value.setNibbleAtEnd(position, v);
	}
	
	protected DifferenceIterator storeNextValue() {
		return new NibblewiseDifferenceIterator(value, dimension, bitPositions);
	}
	
}
