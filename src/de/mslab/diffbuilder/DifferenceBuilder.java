package de.mslab.diffbuilder;

import de.mslab.errors.InvalidArgumentError;

/**
 * A value builder computes in increasing manner all n-bit/n-byte values with less or equal 
 * a maximum number of '1' bits/bytes. This can happen bit-wise, byte-wise or maybe in other
 * fashion. This is the common interface for value builder subclasses.   
 * 
 */
public interface DifferenceBuilder {
	/**
	 * Returns the hamming weight of the last computed value. 
	 */
	int getCurrentWeight();
	/**
	 * After this class was initialized by calling the {@link DifferenceBuilder#initializeAndGetNumDifferences(int, int)} 
	 * method, this getter returns the number of values this class can produce.  
	 */
	long getNumResults();
	/**
	 * Initializes the computation process and returns the number of differences.
	 * @return Returns the number of results.
	 * @throws InvalidArgumentError If the <code>maxWeight</code> is larger than the number
	 * of bits/bytes in the values n, then the whole creation process is senseless, as all possible 
	 * n-bit/n-byte values will be valid and an exception is thrown. If <code>maxWeight</code> is less than 1, 
	 * only 0 will be valid, thus exception is thrown. 
	 */
	long initializeAndGetNumDifferences(int dimension, int numBytes) throws InvalidArgumentError;
	/**
	 * Computes the next n-bit/n-byte value with n bits/n bytes and less or equal '1' bytes as specified in the 
	 * {@link DifferenceBuilder#initializeAndGetNumDifferences(int, int)} method before.
	 */
	DifferenceIterator next();
}
