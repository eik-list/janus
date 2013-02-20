package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidArgumentError;
import de.mslab.utils.Logger;


abstract class AbstractDifferenceBuilder implements DifferenceBuilder {

	/**
	 * The positions of the bits. These have to be arrays, because we do not know the number of position
	 * counters in advance. 
	 */
	protected int[] bitPositions;
	/**
	 * Each bit can only be iterated from a start to an end position.
	 * These arrays store these positions for fast lookup. 
	 */
	protected int[] bitStartPositions;
	protected int[] bitEndPositions;
	protected int dimension;
	protected int numUnits;
	
	/**
	 * How many bytes do we need to store a value?
	 */
	protected int numBytes; 
	protected long numResults;
	/**
	 * The result, an array of potentially very long (numBits long) values
	 */
	protected ByteArray result;
	protected boolean shouldIncrementWeight;
	
	/**
	 * We store each value in a byte array.
	 */
	protected ByteArray value; 
	protected int weight;
	protected volatile boolean isFirst;
	protected Logger logger = new Logger();
	
	public int getCurrentWeight() {
		return weight;
	}
	
	public long getNumResults() {
		return numResults;
	}
	
	public abstract long initializeAndGetNumDifferences(int maxWeight, int numComponents) throws InvalidArgumentError;
	
	public synchronized DifferenceIterator next() {
		if (isFirst) {
			isFirst = false;
			return storeNextValue();
		}
		
		// Iterate the highest bit until it is at the leftmost position
		// 000...0010 to 100..0000
		if (bitPositions[weight] != numUnits) {
			shiftMSBAndIncrementValue();
			return storeNextValue();
		} else {
			// The highest bit is at the leftmost position
			// Find the next highest bit i which is not yet at its individual leftmost position, 
			// and move it one step to the left. Then bring all bits higher than i to their new start positions:
			// IF 100...0011 => 000...1101; continue outer loop.
			// IF 110...0001 => 000...1110; continue outer loop.
			// IF 111...0000 => we are done with this weight; break outer loop.
			
			for (int i = weight - 1; i >= 1; --i) {
				if (bitPositions[i] != bitEndPositions[i]) {
					shiftBitOnePositionAndResetHigherBits(i);
					return storeNextValue();
				}
			}
		}
		
		shouldIncrementWeight = true;
		return storeNextValue();
	}

	protected void resetBitPositionsAndValue() {
		// Set value = 0
		value = new ByteArray(numBytes);
		isFirst = true;
		
		// Reset masks and positions, e. g. for maxWeight = 3 and weight = 2:
		// mask[1] = 000...0001, pos[1] = 1
		// mask[2] = 000...0010, pos[2] = 2
		// mask[3] = 000...0000, pos[3] = -1, because mask[3] is not needed in this iteration
		
		// Set the value to start position for this weight, e. g. for weight = 2:
		// value   = 000...0011 = 0 | mask[1] | mask[2]
		
		// Determine start and end positions for the bits, e. g. for weight = 2:
		// startPos[1] = 1, endPos[1] = 1023, 000...0001 to 010...0000
		// startPos[2] = 2, endPos[2] = 1024, 000...0010 to 100...0000
		for (int i = 1; i <= dimension; ++i) {
			if (i <= weight) {
				bitPositions[i] = i;
				bitStartPositions[i] = i;
				bitEndPositions[i] = numUnits - weight + i;
				setValueAtPosition(i - 1, true);
			} else {
				bitPositions[i] = -1;
			}
		}
	}
	
	protected void shiftBitOnePositionAndResetHigherBits(int i) {
		// We have found the next highest bit at positon i, so move it one step to the left:
		// 100...0011 => 100...0101
		setValueAtPosition(bitPositions[i] - 1, false);
		bitPositions[i]++;
		setValueAtPosition(bitPositions[i] - 1, true);
		
		// Reset all higher bits to one position to the right:
		// 100...0011 => 000...1101
		for (int j = i + 1; j <= weight; ++j) {
			setValueAtPosition(bitPositions[j] - 1, false);
			bitPositions[j] = bitPositions[i] + j - i;
			setValueAtPosition(bitPositions[j] - 1, true);
		}
	}
	
	protected void shiftMSBAndIncrementValue() {
		// Reset the bit at the previous bit position
		// 000...0101 to 000...0001
		setValueAtPosition(bitPositions[weight] - 1, false);
		bitPositions[weight]++;
		
		// Set the bit at the new bit position
		// 000...0001 to 000...1001
		setValueAtPosition(bitPositions[weight] - 1, true);
	}
	
	protected abstract DifferenceIterator storeNextValue();
	
	protected abstract void setValueAtPosition(int position, boolean v); 
	
}
