package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidArgumentError;

public class ThreeFishBitwiseDifferenceBuilder extends BitwiseDifferenceBuilder {
	
	private static final int NUM_BITS_PER_WORD = 64;
	
	private int injectedDifferenceWordIndex;
	
	public int getInjectedDifferenceWordIndex() {
		return injectedDifferenceWordIndex;
	}
	
	public void setInjectedDifferenceWordIndex(int injectedDifferenceWordIndex) {
		this.injectedDifferenceWordIndex = injectedDifferenceWordIndex;
	}
	
	public long initializeAndGetNumDifferences(int dimension, int numBytes) throws InvalidArgumentError {
		super.initializeAndGetNumDifferences(dimension, numBytes);
		numResults = DEFAULT_NUM_RESULTS;
		return numResults;
	}
	
	public synchronized DifferenceIterator next() {
		value = new ByteArray(numBytes);
		int bitPosition;
		int[] setBitPositions = new int[numUnits];
		this.bitPositions = new int[dimension];
		int wordOffset = injectedDifferenceWordIndex * NUM_BITS_PER_WORD;
		
		for (int i = 0; i < dimension; i++) {
			bitPosition = (int)(Math.random() * NUM_BITS_PER_WORD);
			
			if (setBitPositions[bitPosition] == 1) {
				bitPosition = 0;
				
				while (setBitPositions[bitPosition] == 1) {
					bitPosition++;
				}
			}
			
			setBitPositions[bitPosition] = 1;
			this.bitPositions[i] = bitPosition + wordOffset;
			value.setBit(bitPosition + wordOffset, true);
		}
		
		return storeNextValue();
	}
	
}