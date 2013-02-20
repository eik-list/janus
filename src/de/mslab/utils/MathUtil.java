package de.mslab.utils;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidArgumentError;

/**
 * Utility class for several mathematical operations.
 */
public class MathUtil {
	
	/**
	 * Table with 256 entries, where item {@code hammingWeights[i]} contains the hamming weight of i.  
	 */
	private static int[] hammingWeights = computeHammingWeights();
	
	/**
	 * Computes the binomial coefficient sum for n over k.
	 * @throws InvalidArgumentError If n is zero.
	 * @throws Error If k > n.
	 */
	public static long computeBinomialCoefficientSum(int n, int k) {
		long sum = 1;
		
		if (n == 0) {
			throw new IllegalArgumentException("n must not be zero.");
		}
		
		if (n == k) {
			return 1L << n;
		} else if (k > n) {
			throw new IllegalArgumentException("k must be less or equal to n. given k = " + k + ", n = " + n + ".");
		}
		
		if (k == 0) {
			return sum;
		}
		
		double divisor = 1; // n over 0
		double summand;
		long end;
		
		for (int i = 1; i <= k; i++) {
			divisor *= i; // 1 * 2 * 3 ... * k = k!
			summand = n;
			end = n - i + 1;
			
			for (int j = n - 1; j >= end; --j) {
				summand *= j;
			}
			
			// The division has to be here, because all factors of the divisor are contained in 
			// at least one value of j. When the division was placed at statement 
			// (summand = n) => (summand = (double)n / divisor), we had rounding errors.
			summand /= divisor;
			sum += summand;
		}
		
		return sum;
	}

	/**
	 * Computes the binomial coefficient for n over k.
	 * @throws InvalidArgumentError If n is zero.
	 * @throws Error If k > n.
	 */
	public static long computeBinomialCoefficient(int n, int k) {
		if (n == 0) {
			throw new IllegalArgumentException("n must not be zero.");
		}
		
		if (n == k) {
			return 1L << n;
		} else if (k > n) {
			throw new IllegalArgumentException("k must be less or equal to n. given k = " + k + ", n = " + n + ".");
		}
		
		if (k == 0) {
			return 0;
		}
		
		double sum = 1;
		int end = n - k;
		int j = 1;
		
		for (int i = n; i > end; i--) {
			sum *= i;
			sum /= j;
			j++;
		}
		
		return (long)sum;
	}
	
	/**
	 * Computes efficiently all values with "numBits" bit length, with a hamming weight less or equal to "maxweight".
	 * Efficiently means the following: Assume, numBits = 1024, and maxWeight = 3. 
	 * First, the algorithm computes all, 1024 in total, representations for values with 1 bit set, such as
	 * ...001, ...010, ...100 etc.
	 * Second, the algorithm computes all, (1023 * 1023) / 2 in total, representations for values with 2 bits set, such as
	 * ..0011, ..0101, ..1001 etc.
	 * Third, the algorithm computes all, (1022 * 1022 * 1022) / 2 in total, representations for values with 3 bits set, such as
	 * .00111, .01011, .10011 etc.
	 * These values are returned. 
	 * @param maxWeight The maximium hamming weight.
	 * @param numBits The number of bits in the value.
	 * @return An array with these values. As one value can have many bits (they are each "numBits" long), each value 
	 * is represented by a byte array. Note, that the MSB is the MSB of the first byte, the LSB is the LSB in the last byte.
	 */
	public static ByteArray[] computeValuesWithSmallerHammingWeight(int maxWeight, int numBits) {
		if (maxWeight > numBits) {
			throw new InvalidArgumentError("Max weight needs to be equal to or less than numBits. Given W_max = " + maxWeight + ", numBits = " + numBits + ".");
		}
		
		// How many bytes do we need to store a value?
		int numBytes = (int)Math.ceil((double)numBits / 8);
		
		// The number of results is: SUM(i = 0..k) (n over i), where n = numBits and k = maxWeight.
		// The - 1 excludes the all-zero-value ('000...000').
		long numResults = computeBinomialCoefficientSum(numBits, maxWeight) - 1; 
		
		if (numResults > Integer.MAX_VALUE) {
			throw new InvalidArgumentError("Too many results: " + numResults);
		}
		
		// The result, an array of potentially very long (numBits long) values
		ByteArray[] results = new ByteArray[(int)numResults];
		int resultIndex = 0;
		
		// We store each value in a byte array.
		ByteArray value;
		
		// The positions of the bits. These have to be arrays, because we do not know the number of position 
		// counters in advance.
		int[] bitPositions = new int[maxWeight + 1];
		
		// Each bit can only be iterated from a start to an end position. 
		// These arrays store these positions for fast lookup.
		int[] bitStartPositions = new int[maxWeight + 1];
		int[] bitEndPositions = new int[maxWeight + 1];
		
		for (int weight = 1; weight <= maxWeight; ++weight) {
			// Set value = 0
			value = new ByteArray(numBytes);
			
			// Reset masks and positions, e. g. for maxWeight = 3 and weight = 2:
			// mask[1] = 000...0001, pos[1] = 1
			// mask[2] = 000...0010, pos[2] = 2
			// mask[3] = 000...0000, pos[3] = -1, because mask[3] is not needed in this iteration
			
			// Set the value to start position for this weight, e. g. for weight = 2:
			// value   = 000...0011 = 0 | mask[1] | mask[2]
			
			// Determine start and end positions for the bits, e. g. for weight = 2:
			// startPos[1] = 1, endPos[1] = 1023, 000...0001 to 010...0000
			// startPos[2] = 2, endPos[2] = 1024, 000...0010 to 100...0000
			for (int i = 1; i <= maxWeight; ++i) {
				if (i <= weight) {
					bitPositions[i] = i;
					bitStartPositions[i] = i;
					bitEndPositions[i] = numBits - weight + i;
					value.setBitAtEnd(i - 1, true);
					 
				} else {
					bitPositions[i] = -1;
				}
			}
			
			results[resultIndex] = value.clone();
			resultIndex++;
			
			outer: 
			while(true) {
				// Iterate the highest bit until it is at the leftmost position
				// 000...0010 to 100..0000
				while(bitPositions[weight] != numBits) {
					// Reset the bit at the previous bit position
					// 000...0101 to 000...0001
					value.setBitAtEnd(bitPositions[weight] - 1, false);
					bitPositions[weight]++;
					
					// Set the bit at the new bit position
					// 000...0001 to 000...1001
					value.setBitAtEnd(bitPositions[weight] - 1, true);
					
					// Store the next value
					results[resultIndex] = value.clone();
					resultIndex++;
				}
				
				// The highest bit is at the leftmost position
				// Find the next highest bit i which is not yet at its individual leftmost position, 
				// and move it one step to the left. Then bring all bits higher than i to their new start positions:
				// IF 100...0011 => 000...1101; continue outer loop.
				// IF 110...0001 => 000...1110; continue outer loop.
				// IF 111...0000 => we are done with this weight; break outer loop.
				
				for (int i = weight - 1; i >= 1; --i) {
					
					if (bitPositions[i] != bitEndPositions[i]) {
						// We have found the next highest bit at positon i, so move it one step to the left:
						// 100...0011 => 100...0101
						value.setBitAtEnd(bitPositions[i] - 1, false);
						bitPositions[i]++;
						value.setBitAtEnd(bitPositions[i] - 1, true);
						
						// Reset all higher bits to one position to the right:
						// 100...0011 => 000...1101
						for (int j = i + 1; j <= weight; ++j) {
							value.setBitAtEnd(bitPositions[j] - 1, false);
							bitPositions[j] = bitPositions[i] + j - i;
							value.setBitAtEnd(bitPositions[j] - 1, true);
						}
						
						// Store the next value
						results[resultIndex] = value.clone();
						resultIndex++;
						continue outer;
					}
				}
				
				break outer;
			}
		}
		
		return results;
	}
	
	/**
	 * Computes the hamming weight of the given value.
	 */
	public static int getHammingWeightForValue(int value) {
		int weight = 0;
		
		while(value != 0) {
			value &= value - 1;
			weight++;
		}
		
		return weight;
	}
	
	/**
	 * {@link MathUtil#getHammingWeightForValue(int)}
	 */
	public static int getHammingWeightForValue(long value) {
		int weight = 0;
		
		while(value != 0) {
			value &= value - 1;
			weight++;
		}
		
		return weight;
	}
	
	/**
	 * Computes the hamming weight of the least significant {@code numBytes} bytes of the given value.
	 * @param value The value.
	 * @param numBytes Indicates how many bytes shall be included.
	 */
	public static int getHammingWeightForValue(long value, int numBytes) {
		int result = 0;
		int weight = 0;
		
		for (int i = 0; i != numBytes; i++) {
			result = (int)(value & 0xFF);
			value >>= 8;
			weight += hammingWeights[result];
		}
		
		return weight;
	}
	
	/**
	 * Calls {@link #getHammingWeightForValue} with parameters {@code value} and the length of the 
	 * given ByteArray.
	 */
	public static int getHammingWeightForValue(ByteArray value) {
		return getHammingWeightForValue(value, value.length());
	}
	
	/**
	 * Computes the sum of the hamming weights of the first {@code numBytes} bytes in the given array.
	 * @param value The array.
	 * @param numBytes Indicates how many bytes shall be included.
	 * @throws ArrayIndexOutOfBoundsException if the length of value is smaller or equal to {@code numBytes}.
	 */
	public static int getHammingWeightForValue(ByteArray value, int numBytes) {
		int result = 0;
		int weight = 0;
		
		for (int i = 0; i != numBytes; i++) {
			result = (int)(value.get(i) & 0xFF);
			weight += hammingWeights[result];
		}
		
		return weight;
	}
	
	/**
	 * Returns {@code true} if the first {@code numBytes} bytes in the given value have a hamming weight
	 * which is smaller or equal to {@code maxWeight}. Returns {@code false} otherwise.
	 */
	public static boolean hasHigherHammingWeightThan(long value, int maxWeight, int numBytes) {
		int result = 0;
		int weight = 0;
		
		for (int i = 0; i != numBytes; i++) {
			result = (int)(value & 0xFF);
			value >>= 8;
			weight += hammingWeights[result];
			
			if (weight > maxWeight) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns {@code true} if the first N bytes in the given value have a hamming weight
	 * which is smaller or equal to {@code maxWeight}. Returns {@code false} otherwise.
	 */
	public static boolean hasHigherHammingWeightThan(ByteArray value, int maxWeight, int numBytes) {
		int result = 0;
		int weight = 0;
		
		for (int i = 0; i != numBytes; i++) {
			result = (int)(value.get(i) & 0xFF);
			weight += hammingWeights[result];
			
			if (weight > maxWeight) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Computes the log_2({@code value}).
	 * @param value The value.
	 * @return log_2({@code value}).
	 */
	public static double log2(double value) {
		return Math.log(value) / Math.log(2);
	}
	
	/**
	 * Computes the hamming weight of the least significant {@code numBits} of the given {@code value}.
	 * @param value
	 * @param numBits
	 * @return The hamming weight of the least significant {@code numBits} of the given {@code value}.
	 */
	private static int computeHammingWeight(int value, int numBits) {
		int mask = 1;
		int weight = 0;
		
		for (int i = 0; i < numBits; i++) {
			if ((value & mask) != 0) {
				weight++;
			}
			
			mask <<= 1;
		}
		
		return weight;
	}
	
	/**
	 * Computes and stores the hamming weights of all values in [0,255]. 
	 * @return An array with 256 values, where {@code array[i]} contains the hamming weight of 
	 * {@code i}. 
	 */
	private static int[] computeHammingWeights() {
		hammingWeights = new int[256];
		int numBits = 8;
		
		for (int i = 0; i < 256; i++) {
			hammingWeights[i] = computeHammingWeight(i, numBits);
		}
		
		return hammingWeights;
	}
	
}
