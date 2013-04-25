package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidArgumentError;

/**
 * Both the {@link BytewiseDifferenceBuilder} and the {@link FullSpaceBytewiseDifferenceBuilder}
 * classes generate differences for the round key at the starting rounds at the bicliques. 
 * The BytewiseDifferenceBuilder class injects only Math.ceil(dimension/8) active bytes at a time.
 * The FullSpaceBytewiseDifferenceBuilder always makes Math.ceil(dimension/8) bytes active, and 
 * additionally, sets further bytes active.
 */
public class FullSpaceBytewiseDifferenceBuilder implements DifferenceBuilder {
	
	protected int index;
	protected int dimension;
	protected int numBytes; 
	protected long numResults;
	protected ByteArray result;
	protected ByteArray value; 
	protected int weight;
	protected volatile boolean isFirst;
	
	public int getCurrentWeight() {
		return 0;
	}
	
	public long getNumResults() {
		return numResults;
	}

	public long initializeAndGetNumDifferences(int dimension, int numBytes) throws InvalidArgumentError {
		if (dimension < 1) {
			throw new InvalidArgumentError("Dimension needs to be at least 1");
		}
		
		if (dimension > 8 * numBytes) {
			throw new InvalidArgumentError("Dimension needs to be equal to or less than numBits. Given dimension = " + dimension + ", numBits = " + numBytes + ".");
		}
		this.dimension = dimension;
		this.numBytes = numBytes;
		this.weight = (int)Math.ceil((double)dimension / Byte.SIZE); // d = 8 => w = 1; d = 24 => w = 3
		this.numResults = (1L << numBytes) - 1; // n = 16 => 2^{16}
		
		if (dimension > Byte.SIZE) {
			long numResultsNotConsidered = (1L << weight); // only consider 
			this.numResults -= numResultsNotConsidered;
		}
		
		this.value = new ByteArray(numBytes);
		this.index = 0;
		return numResults;
	}
	
	public synchronized DifferenceIterator next() {
		index++;
		int j = index;
		
		this.value = new ByteArray(numBytes);
		
		for (int i = 0; i < numBytes; i++) {
			value.set(i, j & 1);
			j >>>= 1;
		}
		
		return storeNextValue();
	}
	
	protected void setValueAtPosition(int position, boolean v) {
		value.setAtEnd(position, v);
	}
	
	protected DifferenceIterator storeNextValue() {
		return new BytewiseDifferenceIterator(value, dimension);
	}
	
}
