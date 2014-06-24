package de.mslab.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import de.mslab.errors.InvalidArgumentError;
import de.mslab.utils.Logger;

public class ByteArrayTest {
	
	private static Logger logger = Logger.getLogger();
	
	@Test
	public final void testByteArray() {
		final ByteArray result = new ByteArray();
		assertEquals(0, result.length());
	}

	@Test
	public final void testByteArrayShortArray() {
		final short[] array = new short[]{ 6, 7, 8, 9 };
		final ByteArray result = new ByteArray(array);
		assertEquals(array.length, result.length());
		
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], result.get(i));
		}
	}

	@Test
	public final void testByteArrayIntArray() {
		final int[] array = new int[]{ 6, 7, 8, 9 };
		final ByteArray result = new ByteArray(array);
		assertEquals(array.length, result.length());
		
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], result.get(i));
		}
	}

	@Test
	public final void testByteArrayIntArrayInt() {
		final int[] array = new int[]{ 6, 7, 8, 9 };
		final int position = 7;
		final ByteArray result = new ByteArray(array, position);
		assertEquals(array.length + position, result.length());
		
		for (int i = 0; i < position; i++) {
			assertEquals(0, result.get(i));
		}
		
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], result.get(i + position));
		}
	}

	@Test
	public final void testByteArrayInt() {
		final int length = 27;
		final ByteArray result = new ByteArray(length);
		assertEquals(length, result.length());
		
		for (int i = 0; i < length; i++) {
			assertEquals(0, result.get(i));
		}
	}

	@Test
	public final void testByteArrayIntInt() {
		final int length = 27;
		final int value = 0x7a987654;
		final ByteArray result = new ByteArray(value, length);
		assertEquals(length, result.length());
		
		int mask = 0x1;
		int shouldBe;
		
		for (int i = 0; i < length; i++) {
			shouldBe = (value & mask) >> i;
			assertEquals(shouldBe, result.get(i));
			mask <<= 1;
		}
	}
	
	@Test
	public final void testByteArrayLongInt() {
		final int length = 27;
		final long value = 0xff7a987654L;
		final ByteArray result = new ByteArray(value, length);
		assertEquals(length, result.length());
		
		int mask = 0x1;
		long shouldBe;
		
		for (int i = 0; i < length; i++) {
			shouldBe = (value & mask) >>> i;
			assertEquals(shouldBe, result.get(i));
			mask <<= 1;
		}
	}
	
	@Test
	public final void testByteArrayLongArray() {
		final long[] array = new long[]{ 
			0x00000123456789abL, 0x000003456789abcdL, 0x56789abcdefL, 0x7234985adfeL, 0xe0345abca94L
		};
		final ByteArray result = new ByteArray(array);
		assertEquals(array.length * 8, result.length());
		
		long value;
		long mask;
		int shift, shouldBe;
		
		for (int i = 0; i < array.length; i++) {
			value = array[i];
			mask = 0xFF00000000000000L;
			shift = 56;
			
			for (int j = 0; j < 8; j++) {
				shouldBe = (short)((value & mask) >>> shift);
				assertEquals(shouldBe, result.get(i * 8 + j));
				mask >>>= 8;
				shift -= 8;
			}
		}
	}
	
	@Test
	public final void testByteArrayLongArrayArray() {
		final long[][] array = new long[][]{ 
			{ 0x123456789abL, 0x3456789abcdL, 0x56789abcdefL, 0x7234985adfeL }, 
			{ 0xe0345abca94L, 0x123456789abL, 0x3456789abcdL, 0x56789abcdefL, 0x7234985adfeL },
			{ 0x7234985adfeL, 0xe0345abca94L, 0x123456789abL, 0x3456789abcdL, 0x56789abcdefL, 0x12 },
		};
		final ByteArray result = new ByteArray(array);
		int numLongWords = 0;
		
		for (int i = 0; i < array.length; i++) {
			numLongWords += array[i].length;
		}
		
		assertEquals(numLongWords * 8, result.length());
		
		long value;
		long mask;
		int shift, shouldBe, index = 0;
		
		for (int i = 0; i < array.length; i++) {
			for (int k = 0; k < array[i].length; k++) {
				value = array[i][k];
				mask = 0xFF00000000000000L;
				shift = 56;
				
				for (int j = 0; j < 8; j++) {
					shouldBe = (int) ((value & mask) >>> shift);
					assertEquals(shouldBe, result.get(index));
					mask >>>= 8;
					shift -= 8;
					index++;
				}
			}
		}
	}

	@Test
	public final void testCreateEmpty() {
		final int length = 15;
		final ByteArray result = ByteArray.createEmpty(length);
		assertEquals(length, result.length());
		
		for (int i = 0; i < length; i++) {
			assertEquals(0, result.get(i));
		}
	}

	@Test
	public final void testAnd() {
		final int[] firstArray = new int[]{ 7, 9, 2, 345, 34, 43 };
		final int[] secondArray = new int[]{ 11, 99, 9, 2, 345, 34, 43 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		final ByteArray result = first.and(second);
		assertSame(first, result);
		assertEquals(6, result.length());
		
		for (int i = 0; i < result.length(); i++) {
			assertEquals(firstArray[i] & secondArray[i], result.get(i));
		}
	}

	@Test
	public final void testClone() {
		final int[] array = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final ByteArray original = new ByteArray(array);
		final ByteArray result = original.clone();
		result.set(5, 42);
		
		assertEquals(array.length, original.length());
		assertEquals(array.length, result.length());
		
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], original.get(i));
			
			if (i == 5) {
				assertEquals(42, result.get(i));
			} else {
				assertEquals(array[i], result.get(i));
			}
		}
	}

	@Test
	public final void testConcat() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2, 3, 4, 5, 6, 7, 8 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.concat(second);
		assertEquals(firstArray.length + secondArray.length, first.length());
		
		second.set(0, 77);
		second.set(1, 99);
		
		for (int i = 0; i < firstArray.length; i++) {
			assertEquals(firstArray[i], first.get(i));
		}
		
		for (int i = 0; i < secondArray.length; i++) {
			assertEquals(secondArray[i], first.get(i + firstArray.length));
		}
	}

	@Test
	public final void testCopyByte() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2, 3, 4, 5, 6, 7, 8 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyByte(second, 1, 3);
		
		assertEquals(firstArray.length, first.length());
		
		for (int i = 0; i < firstArray.length; i++) {
			if (i == 3) {
				assertEquals(second.get(1), first.get(i));
			} else {
				assertEquals(firstArray[i], first.get(i));
			}
		}
	}
	
	@Test
	public final void testCopyBytesByteArray() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyBytes(second);
		
		assertEquals(firstArray.length, first.length());
		
		for (int i = 0; i < 4; i++) {
			assertEquals(secondArray[i], first.get(i));
		}
		
		for (int i = 5; i < 8; i++) {
			assertEquals(firstArray[i], first.get(i));
		}
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testCopyBytesByteArrayFails() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2, 3, 4, 5, 6, 7, 8 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyBytes(second);
	}

	@Test
	public final void testCopyBytesByteArrayInt() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyBytes(second, 2);
		
		assertEquals(firstArray.length, first.length());
		
		for (int i = 0; i < 2; i++) {
			assertEquals(secondArray[i + 2], first.get(i));
		}
		
		for (int i = 3; i < 8; i++) {
			assertEquals(firstArray[i], first.get(i));
		}
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testCopyBytesByteArrayIntFails() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2, 3, 9, 0, 7, 6 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyBytes(second, 0);
		logger.info(first);
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testCopyBytesByteArrayIntFailsBadly() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2, 3, 9, 0, 7, 6 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyBytes(second, 0);
	}
	
	@Test
	public final void testCopyBytesByteArrayIntInt() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyBytes(second, 2, 2);
		
		assertEquals(firstArray.length, first.length());
		
		for (int i = 2; i < 4; i++) {
			assertEquals(secondArray[i], first.get(i));
		}
		
		for (int i = 0; i < 2; i++) {
			assertEquals(firstArray[i], first.get(i));
		}
		
		for (int i = 4; i < 8; i++) {
			assertEquals(firstArray[i], first.get(i));
		}
	}
	
	@Test
	public final void testCopyBytesByteArrayIntIntInt() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyBytes(second, 1, 3, 3);

		assertEquals(firstArray.length, first.length());
		
		for (int i = 3; i < 6; i++) {
			assertEquals(secondArray[i - 2], first.get(i));
		}
		
		for (int i = 0; i < 3; i++) {
			assertEquals(firstArray[i], first.get(i));
		}
		
		for (int i = 6; i < 8; i++) {
			assertEquals(firstArray[i], first.get(i));
		}
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testCopyBytesByteArrayIntIntIntFailsDueToLength() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyBytes(second, 0, 0, second.length() + 1);
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testCopyBytesByteArrayIntIntIntFailsDueToWrongDestIndex() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyBytes(second, 0, 9, 0);
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testCopyBytesByteArrayIntIntIntFailsDueToWrongSourceIndex() {
		final int[] firstArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] secondArray = new int[]{ 7, 9, 1, 2 };
		final ByteArray first = new ByteArray(firstArray);
		final ByteArray second = new ByteArray(secondArray);
		first.copyBytes(second, 7, 0, 0);
	}
	
	@Test
	public final void testCountNumActiveBytes() {
		final int numTimes = 10000, numArrayElements = 100;
		int numNotZeroes;
		int[] array;
		ByteArray result;
		
		for (int i = 0; i < numTimes; i++) {
			array = new int[(int)(Math.random() * numArrayElements)];
			numNotZeroes = 0;
			
			for (int j = 0; j < array.length; j++) {
				array[j] = (int)(Math.random() * 255);
				
				if (array[j] != 0) {
					numNotZeroes++;
				}
			}
			
			result = new ByteArray(array);
			assertEquals(numNotZeroes, result.countNumActiveBytes());
		}
	}

	@Test
	public final void testCountNumActiveNibbles() {
		final int numTimes = 10000, numArrayElements = 100;
		int numNotZeroes;
		int[] array;
		ByteArray result;
		
		for (int i = 0; i < numTimes; i++) {
			array = new int[(int)(Math.random() * numArrayElements)];
			numNotZeroes = 0;
			
			for (int j = 0; j < array.length; j++) {
				array[j] = (int)(Math.random() * 255);
				
				if ((array[j] & 0xF0) != 0) {
					numNotZeroes++;
				}
				if ((array[j] & 0x0F) != 0) {
					numNotZeroes++;
				}
			}
			
			result = new ByteArray(array);
			assertEquals(numNotZeroes, result.countNumActiveNibbles());
		}
	}
	
	@Test
	public final void testEqualsInt() {
		int[] array;
		ByteArray result;
		int expected;
		
		for (int i = 0; i < 100; i++) {
			array = new int[4];
			expected = (int)(Math.random() * 255);
			
			for (int j = 0; j < array.length; j++) {
				array[j] = expected;
			}
			
			result = new ByteArray(array);
			assertTrue(result.equals(expected));
		}
	}
	
	@Test
	public final void testEqualsIntIsFalse() {
		int[] array;
		ByteArray result;
		int expected = 0;
		
		for (int i = 0; i < 100; i++) {
			array = new int[4];
			
			for (int j = 0; j < array.length; j++) {
				array[j] = (int)(Math.random() * 255);
				expected = array[j]++;
			}
			
			result = new ByteArray(array);
			assertFalse(result.equals(expected));
		}
	}
	
	@Test
	public final void testEqualsIntIsFalseIfOneElementDiffers() {
		int[] array;
		ByteArray result;
		int expected;
		
		for (int i = 0; i < 100; i++) {
			array = new int[7];
			expected = (int)(Math.random() * 254);
			
			for (int j = 0; j < array.length; j++) {
				array[j] = expected;
			}
			
			array[(int)(Math.random() * array.length)] = expected + 1; 
			result = new ByteArray(array);
			assertFalse(result.equals(expected));
		}
	}
	
	@Test
	public final void testEqualsIntArray() {
		final int[] array = new int[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		final int[] expected = new int[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		assertTrue(result.equals(expected));
	}
	
	@Test
	public final void testEqualsIntArrayIsFalse() {
		final int[] array = new int[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		final int[] expected = new int[]{ 6, 7, 1110, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		assertFalse(result.equals(expected));
	}
	
	@Test
	public final void testEqualsByteArray() {
		final int[] firstArray = new int[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		final int[] secondArray = new int[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertTrue(first.equals(second));
	}
	
	@Test
	public final void testEqualsByteArrayIsFalse() {
		final int[] firstArray = new int[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		final int[] secondArray = new int[]{ 6, 7, 1110, 9, 1, 34, 3451, -134 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertFalse(first.equals(second));
	}
	
	@Test
	public final void testGet() {
		final int[] array = new int[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i] & 0xFF, result.get(i));
		}
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testGetFails() {
		final int[] array = new int[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		result.get(array.length + 1);
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testGetFailsForNegativeIndices() {
		final int[] array = new int[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		result.get(-1);
	}
	
	@Test
	public final void testGetArray() {
		final short[] array = new short[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		final short[] resultArray = result.getArray();
		
		for (int i = 1; i < array.length; i++) {
			assertEquals(array[i] & 0xFF, resultArray[i]);
		}
	}
	
	@Test
	public final void testGetArrayReturnsNewArray() {
		final short[] array = new short[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		array[0] = array[0]++;
		final short[] resultArray = result.getArray();
		
		for (int i = 1; i < resultArray.length; i++) {
			assertEquals(array[i] & 0xff, resultArray[i]);
		}
		
		assertEquals(6, resultArray[0]);
	}
	
	@Test
	public final void testGetArrayIntInt() {
		final short[] array = new short[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		final short[] resultArray = result.getArray(4, 7);
		
		assertEquals(3, resultArray.length);

		for (int i = 0; i < resultArray.length; i++) {
			assertEquals(array[i + 4] & 0xFF, resultArray[i]);
		}
	}
	
	@Test
	public final void testGetBit() {
		final short[] array = new short[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		int mask, index = 0;
		boolean bit;
		
		for (int i = 0; i < array.length; i++) {
			mask = 0x80;
			
			for (int j = 0; j < Byte.SIZE; j++) {
				bit = (array[i] & mask) != 0;
				mask >>= 1;
				
				assertEquals(bit, result.getBit(index++));
			}
		}
	}
	
	@Test
	public final void testGetDifference() {
		final short[] firstArray = new short[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		final short[] secondArray = new short[]{ 7, 9, 2345, 34, 1345, 30954, -3490, -234 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		ByteArray diff = first.getDifference(second);
		
		for (int i = 0; i < firstArray.length; i++) {
			assertEquals((firstArray[i] ^ secondArray[i]) & 0xFF, diff.get(i));
		}
	}
	
	@Test(expected=InvalidArgumentError.class)
	public final void testGetDifferenceFailsForDifferentLengths() {
		final short[] firstArray = new short[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		final short[] secondArray = new short[]{ 7, 9, 2345, 34, 1345, 30954, -3490, -234, 27 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		first.getDifference(second);
	}
	
	@Test
	public final void testGetNibble() {
		final short[] array = new short[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		int index = 0;
		int nibble;
		
		for (int i = 0; i < array.length; i++) {
			nibble = (array[i] & 0xF0) >>> 4;
			assertEquals(nibble, result.getNibble(index++));
			
			nibble = (array[i] & 0x0F);
			assertEquals(nibble, result.getNibble(index++));
		}
	}
	
	@Test
	public final void testIsFullyActive() {
		final short[] array = new short[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array);
		assertTrue(result.isFullyActive());
	}

	@Test
	public final void testIsFullyActiveIsFalse() {
		final short[] array = new short[]{ 6, 7, 8, 9, 1, 34, 0, 3451, -134 };
		ByteArray result = new ByteArray(array);
		assertFalse(result.isFullyActive());
	}
	
	@Test
	public final void testLength() {
		ByteArray result;
		int length;
		
		for (int i = 0; i < 100; i++) {
			length = (int)(Math.random() * 255); 
			result = new ByteArray(length);
			assertEquals(length, result.length());
		}
	}
	
	@Test
	public final void testOrByteArray() {
		int[] firstArray, secondArray;
		ByteArray first, second;
		final int length = 100;
		
		for (int i = 0; i < length; i++) {
			firstArray = new int[length];
			secondArray = new int[length];
			
			for (int j = 0; j < length; j++) {
				firstArray[j] = (int)(Math.random() * 255);
				secondArray[j] = (int)(Math.random() * 255);
			}
			
			first = new ByteArray(firstArray);
			second = new ByteArray(secondArray);
			first.or(second);
			
			for (int j = 0; j < length; j++) {
				assertEquals(firstArray[j] | secondArray[j], first.get(j));
			}
		}
	}
	
	@Test
	public final void testOrByteArrayIntInt() {
		int[] firstArray, secondArray;
		ByteArray first, second;
		final int length = 100;
		
		for (int i = 0; i < length; i++) {
			firstArray = new int[length];
			secondArray = new int[length];
			
			for (int j = 0; j < length; j++) {
				firstArray[j] = (int)(Math.random() * 255);
				secondArray[j] = (int)(Math.random() * 255);
			}
			
			first = new ByteArray(firstArray);
			second = new ByteArray(secondArray);
			first.or(second, 7, 10);
			
			for (int j = 0; j < 7; j++) {
				assertEquals(firstArray[j], first.get(j));
			}
			for (int j = 7; j < 10; j++) {
				assertEquals(firstArray[j] | secondArray[j], first.get(j));
			}
			for (int j = 10; j < length; j++) {
				assertEquals(firstArray[j], first.get(j));
			}
		}
	}
	
	@Test
	public final void testRandomize() {
		final int length = 100;
		ByteArray result = new ByteArray(length);
		result.randomize();
		int[] counts = new int[256];
		
		for (int i = 0; i < length; i++) {
			counts[result.get(i)]++;
		}
		
		for (int i = 0; i < length; i++) {
			assertTrue(counts[i] < 50);
		}
	}
	
	@Test
	public final void testReadLong() {
		final short[] array = new short[]{ 0x1a, 0x04, 0x8a, 0xb9, 0xf1, 0x11, 0xe9, 0xf9 };
		ByteArray result = new ByteArray(array);
		final long actual = result.readLong(0);
		final long expected = 0x1a048ab9f111e9f9L;
		assertEquals(expected, actual);
	}
	
	@Test
	public final void testReadLongs() {
		final int numTests = 100;
		final int length = 128;
		final int numLongs = length / 8;
		byte[] array;
		ByteArray result;
		long[] expected, actual;
		
		for (int i = 0; i < numTests; i++) {
			array = new byte[length];
			
			for (int j = 0; j < length; j++) {
				array[j] = (byte)(Math.random() * 255);
			}
			
			result = new ByteArray(array);
			actual = result.readLongs();
			expected = byteArrayToLongArray(array);
			
			for (int j = 0; j < numLongs; j++) {
				assertEquals(expected[j], actual[j]);
			}
		}
	}
	
	private long[] byteArrayToLongArray(byte[] array) {
		return byteArrayToLongArray(array, 0);
	}
	
	private long[] byteArrayToLongArray(byte[] array, int startPosition) {
		return byteArrayToLongArray(array, startPosition, (array.length - startPosition) / 8);
	}
	
	private long[] byteArrayToLongArray(byte[] array, int startPosition, int numLongs) {
		//final int numBytesPerLong = Long.SIZE / Byte.SIZE;
		//final int numLongs = (array.length - startPosition) / numBytesPerLong;
		long[] result = new long[numLongs];
		long value;
		int index = startPosition;
		
		for (int i = 0; i < numLongs; i++, index += 8) {
			value = 0;
			value = (long)(array[index] & 0xFF) << 56 | (long)(array[index+1] & 0xFF) << 48 
				| (long)(array[index+2] & 0xFF) << 40 | (long)(array[index+3] & 0xFF) << 32
				| (long)(array[index+4] & 0xFF) << 24 | (long)(array[index+5] & 0xFF) << 16 
				| (long)(array[index+6] & 0xFF) << 8 | (long)(array[index+7] & 0xFF);
			result[i] = value;
		}
		
		return result;
	}
	
	@Test
	public final void testReadLongsInt() {
		final int numTests = 100;
		final int length = 128;
		int numLongs = length / 8;
		byte[] array;
		ByteArray result;
		long[] expected, actual;
		
		for (int i = 0; i < numTests; i++) {
			array = new byte[length];
			
			for (int j = 0; j < length; j++) {
				array[j] = (byte)(Math.random() * 255);
			}
			
			result = new ByteArray(array);
			
			for (int position = 0; position < length; position += 8) {
				actual = result.readLongs(position);
				expected = byteArrayToLongArray(array, position);
				numLongs = (length - position) / 8;
				
				for (int j = position; j < numLongs; j++) {
					assertEquals(expected[j], actual[j]);
				}
			}
		}
	}
	
	@Test
	public final void testReadLongsIntInt() {
		final int length = 128;
		long[] expected, actual;
		byte[] array = new byte[length];
		
		for (int j = 0; j < length; j++) {
			array[j] = (byte)(Math.random() * 255);
		}
		
		ByteArray result = new ByteArray(array);
		
		actual = result.readLongs(6, 4);
		expected = byteArrayToLongArray(array, 6, 4);
		assertArrayEquals(expected, actual);
		
		actual = result.readLongs(0, 8);
		expected = byteArrayToLongArray(array, 0, 8);
		assertArrayEquals(expected, actual);
	}

	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testReadLongsIntIntFails() {
		final int length = 128;
		byte[] array = new byte[length];
		
		for (int j = 0; j < length; j++) {
			array[j] = (byte)(Math.random() * 255);
		}
		
		ByteArray result = new ByteArray(array);
		result.readLongs(6, 20);
	}
	
	@Test
	public final void testSetIntShort() {
		final short[] array = new short[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array.length);
		
		for (int i = 0; i < array.length; i++) {
			result.set(i, array[i]);
			assertEquals(array[i] & 0xFF, result.get(i));
		}
	}
	
	@Test
	public final void testSetIntInt() {
		final int[] array = new int[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray(array.length);
		
		for (int i = 0; i < array.length; i++) {
			result.set(i, array[i]);
			assertEquals(array[i] & 0xFF, result.get(i));
		}
	}
	
	@Test
	public final void testSetArray() {
		final short[] array = new short[]{ 6, 7, 8, 9, 1, 34, 3451, -134 };
		ByteArray result = new ByteArray();
		result.setArray(array);
		assertEquals(array.length, result.length());

		for (int i = 0; i < array.length; i++) {
			result.set(i, array[i]);
			assertEquals(array[i] & 0xFF, result.get(i));
		}
	}
	
	@Test
	public final void testSetAtEndIntBoolean() {
		ByteArray result = new ByteArray(27);
		result.setAtEnd(5, false);
		assertEquals(0, result.get(result.length() - 6));
		result.setAtEnd(5, true);
		assertEquals(0xFF, result.get(result.length() - 6));
	}
	
	@Test
	public final void testSetAtEndIntShort() {
		ByteArray result = new ByteArray(27);
		result.setAtEnd(5, (short)0x44);
		assertEquals(0x44, result.get(result.length() - 6));
		result.setAtEnd(5, (short)0x45);
		assertEquals(0x45, result.get(result.length() - 6));
	}
	
	@Test
	public final void testSetBit() {
		ByteArray result = new ByteArray(27);
		result.setBit(5, true);
		result.setBit(7, true);
		result.setBit(9, true);
		assertEquals(0x05, result.get(0));
		assertEquals(0x40, result.get(1));
		result.setBit(5, false);
		assertEquals(0x01, result.get(0));
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testSetBitFailsIfIndexOutOfRange() {
		ByteArray result = new ByteArray(27);
		result.setBit(27 * 8 - 1, true);
		assertEquals(0x01, result.get(26));
		
		result.setBit(27 * 8, true);
	}
	
	@Test
	public final void testSetBitAtEnd() {
		ByteArray result = new ByteArray(27);
		result.setBitAtEnd(5, true);
		result.setBitAtEnd(7, true);
		result.setBitAtEnd(9, true);
		assertEquals(0xa0, result.get(26));
		assertEquals(0x02, result.get(25));
		result.setBitAtEnd(5, false);
		assertEquals(0x80, result.get(26));
	}
	
	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testSetBitAtEndFailsIfIndexOutOfRange() {
		ByteArray result = new ByteArray(27);
		result.setBitAtEnd(27 * 8 - 1, true);
		assertEquals((short)0x80, result.get(0));
		result.setBitAtEnd(27 * 8, true);
	}
	
	@Test
	public final void testSetNibble() {
		final short[] array = new short[]{ 16, 17, 18, 19, 9, 34, 3451, -134 };
		ByteArray result = new ByteArray(array.length);
		
		for (int i = 0; i < array.length; i++) {
			result.setNibble(2 * i, (short)((array[i] >>> 4) & 0xF));
			result.setNibble(2 * i + 1, (short)(array[i] & 0xF));
			assertEquals(array[i] & 0xFF, result.get(i));
		}
	}

	@Test
	public final void testSetNibbleAtEndIntBoolean() {
		ByteArray result = new ByteArray(27);
		result.setNibbleAtEnd(0, true);
		result.setNibbleAtEnd(1, true);
		assertEquals(0xFF, result.get(26));
		
		result.setNibbleAtEnd(0, false);
		assertEquals(0xF0, result.get(26));
	}
	
	@Test
	public final void testSetNibbleAtEndIntShort() {
		ByteArray result = new ByteArray(27);
		result.setNibbleAtEnd(0, (short)11);
		result.setNibbleAtEnd(1, (short)7);
		assertEquals(0x7b, result.get(26));
		
		result.setNibbleAtEnd(1, (short)13);
		assertEquals(0xdb, result.get(26));
	}
	
	@Test
	public final void testSetValue() {
		int[] array = { 5, 7, 143, 45, 34, 35, 56, 10, 34 };
		ByteArray result = new ByteArray(array);
		
		for (int i = 0; i < array.length; i++) {
			result.set(i, array[i] + 1);
			assertEquals((short)(array[i] + 1), result.get(i));
		}
	}

	@Test
	public final void testSharesActiveBitsWith() {
		int[] firstArray = { 0xff, 0xff, 0xff, 0x80, 0x08 };
		int[] secondArray = { 0, 0, 0, 0x8F, 0 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertTrue(first.sharesActiveBitsWith(second));
		assertTrue(second.sharesActiveBitsWith(first));
	}
	
	@Test
	public final void testSharesActiveBitsWithOnFirstElement() {
		int[] firstArray = { 0xff, 0xff, 0xff, 0x80, 0x08 };
		int[] secondArray = { 0x80, 0, 0, 0, 0 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertTrue(first.sharesActiveBitsWith(second));
		assertTrue(second.sharesActiveBitsWith(first));
	}
	
	@Test
	public final void testSharesActiveBitsWithOnLastElement() {
		int[] firstArray = { 0xff, 0xff, 0xff, 0x80, 0x08 };
		int[] secondArray = { 0, 0, 0, 0, 0x0F };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertTrue(first.sharesActiveBitsWith(second));
		assertTrue(second.sharesActiveBitsWith(first));
	}
	
	@Test
	public final void testSharesActiveBitsWithIsFalse() {
		int[] firstArray = { 0x12, 0x34, 0x56, 0x78, 0x9a };
		int[] secondArray = { 0x21, 0x43, 0x21, 0x87, 0x65 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertFalse(first.sharesActiveBitsWith(second));
		assertFalse(second.sharesActiveBitsWith(first));
	}
	
	@Test
	public final void testSharesActiveBytesWith() {
		int[] firstArray = { 0xff, 0xff, 0xff, 0x80, 0x08 };
		int[] secondArray = { 0, 0, 0, 0x01, 0 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertTrue(first.sharesActiveBytesWith(second));
		assertTrue(second.sharesActiveBytesWith(first));
	}

	@Test
	public final void testSharesActiveBytesWithOnFirstElement() {
		int[] firstArray = { 0xff, 0xff, 0xff, 0x80, 0x08 };
		int[] secondArray = { 0x1, 0, 0, 0, 0 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertTrue(first.sharesActiveBytesWith(second));
		assertTrue(second.sharesActiveBytesWith(first));
	}
	
	@Test
	public final void testSharesActiveBytesWithOnLastElement() {
		int[] firstArray = { 0xff, 0xff, 0xff, 0x80, 0x08 };
		int[] secondArray = { 0, 0, 0, 0, 0x10 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertTrue(first.sharesActiveBytesWith(second));
		assertTrue(second.sharesActiveBytesWith(first));
	}
	
	@Test
	public final void testSharesActiveBytesWithIsFalse() {
		int[] firstArray = { 0xff, 0xff, 0, 0x80, 0x08 };
		int[] secondArray = { 0, 0, 0x1, 0, 0 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertFalse(first.sharesActiveBytesWith(second));
		assertFalse(second.sharesActiveBytesWith(first));
	}

	@Test
	public final void testSharesActiveNibblesWith() {
		int[] firstArray = { 0xff, 0xff, 0xff, 0x80, 0x08 };
		int[] secondArray = { 0, 0, 0, 0x10, 0 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertTrue(first.sharesActiveNibblesWith(second));
		assertTrue(second.sharesActiveNibblesWith(first));
	}

	@Test
	public final void testSharesActiveNibblesWithOnFirstElement() {
		int[] firstArray = { 0xff, 0xff, 0xff, 0x80, 0x08 };
		int[] secondArray = { 0x10, 0, 0, 0, 0 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertTrue(first.sharesActiveNibblesWith(second));
		assertTrue(second.sharesActiveNibblesWith(first));
	}
	
	@Test
	public final void testSharesActiveNibblesWithOnLastElement() {
		int[] firstArray = { 0xff, 0xff, 0xff, 0x80, 0x08 };
		int[] secondArray = { 0, 0, 0, 0, 0x01 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertTrue(first.sharesActiveNibblesWith(second));
		assertTrue(second.sharesActiveNibblesWith(first));
	}
	
	@Test
	public final void testSharesActiveNibblesWithIsFalse() {
		int[] firstArray = { 0xf0, 0x56, 0x06, 0x80, 0x01 };
		int[] secondArray = { 0x05, 0x00, 0x70, 0x08, 0xf0 };
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		assertFalse(first.sharesActiveNibblesWith(second));
		assertFalse(second.sharesActiveNibblesWith(first));
	}
	
	@Test
	public final void testSpliceInt() {
		int[] array = { 0xf0, 0x56, 0x06, 0x80, 0x01 };
		ByteArray result = new ByteArray(array);
		
		for (int i = 0; i < array.length; i++) {
			ByteArray actual = result.splice(i);
			
			for (int j = i; j < array.length; j++) {
				assertEquals(array[j], actual.get(j - i));
			}
		}
	}
	
	@Test
	public final void testSpliceIntInt() {
		final int length = 128;
		final int[] array = new int[length];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = (int)(Math.random() * 255);
		}
		
		final ByteArray result = new ByteArray(array);
		final int[][] splices = { 
			{0, 7}, {6, 13}, {29, 127}, {15, 128}, 
			{119, 128}, {0, 128}, {46, 46} 
		};
		ByteArray actual;
		int[] splice;
		
		for (int i = 0; i < splices.length; i++) {
			splice = splices[i];
			actual = result.splice(splice[0], splice[1]);
			assertEquals(splice[1] - splice[0], actual.length());
			
			for (int j = splice[0]; j < splice[1]; j++) {
				assertEquals(array[j], actual.get(j - splice[0]));
			}
		}
	}
	
	@Test(expected=NegativeArraySizeException.class)
	public final void testSpliceIntIntFails() {
		final int length = 128;
		final int[] array = new int[length];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = (int)(Math.random() * 255);
		}
		
		final ByteArray result = new ByteArray(array);
		result.splice(7, 5);
	}
	
	@Test
	public final void testToBitString() {
		int length = 100;
		int[] array = new int[length];
		
		for (int i = 0; i < length; i++) {
			array[i] = (int)(Math.random() * 255);
		}
		
		ByteArray result = new ByteArray(array);
		String actual = result.toBitString();
		String expected = "";
		String v;
		
		for (int i = 0; i < length; i++) {
			v = Integer.toBinaryString(array[i]);
			
			while (v.length() < 8) {
				v = "0" + v;
			}
			
			expected += v;
			
			if (i % 16 == 15 && i > 0) {
				expected += "\n";
			} else if (i + 1 < length) {
				expected += ",";
			}
		}
		
		assertEquals(expected, actual);
	}

	@Test
	public final void testToHexString() {
		int length = 100;
		int[] array = new int[length];
		
		for (int i = 0; i < length; i++) {
			array[i] = (int)(Math.random() * 255);
		}
		
		ByteArray result = new ByteArray(array);
		String actual = result.toHexString();
		String expected = "";
		String v;
		
		for (int i = 0; i < length; i++) {
			v = Integer.toHexString(array[i]);
			
			while (v.length() < 2) {
				v = "0" + v;
			}
			
			expected += v;
			
			if (i % 16 == 15 && i > 0) {
				expected += "\n";
			} else if (i + 1 < length) {
				expected += ",";
			}
		}
		
		assertEquals(expected, actual);
	}

	@Test
	public final void testToString() {
		int length = 100;
		int[] array = new int[length];
		
		for (int i = 0; i < length; i++) {
			array[i] = (short)(Math.random() * 255);
		}
		
		ByteArray result = new ByteArray(array);
		String actual = result.toString();
		String expected = "[";
		String v;
		
		for (int i = 0; i < length; i++) {
			v = Integer.toHexString(array[i]);
			
			if (v.length() < 2) {
				v = "0" + v;
			}
			
			expected += v;
			
			if (i % 16 == 15 && i > 0) {
				expected += "\n";
			} else if (i + 1 < length) {
				expected += ",";
			}
		}
		
		expected += "]";
		assertEquals(expected, actual);
	}

	@Test
	public final void testWriteBytes() {
		final int length = 128;
		final short[] array = new short[length];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = (short)(Math.random() * 255);
		}
		
		ByteArray result;
		final int[][] ranges = { 
			{ 7, 18 }, {0, 36}, { 23, 99 }, {127, 127}, 
			{ 0, 127}, {0, 16}, {0, 0} 
		};
		int[] range;
		short[] newBytes;
		
		for (int i = 0; i < ranges.length; i++) {
			range = ranges[i];
			result = new ByteArray(length);
			newBytes = Arrays.copyOfRange(array, range[0], range[1]);
			result.writeBytes(range[0], newBytes);
			
			for (int j = 0; j < newBytes.length; j++) {
				if (j >= range[0] && j < range[1]) {
					assertEquals(array[j], result.get(j));
				} else {
					assertEquals(0, result.get(j));
				}
			}
		}
	}
	
	@Test
	public final void testWriteLongLong() {
		final int length = 128;
		final long[] array = new long[length];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = (long)(Math.random() * 0xFFFFFFFFFFFFFFFFL);
		}
		
		ByteArray result = new ByteArray(8);
		
		for (int i = 0; i < array.length; i++) {
			result.writeLong(array[i]);
			assertEquals(array[i], result.readLong(0));
		}
	}
	
	@Test
	public final void testWriteLongIntLong() {
		final int length = 128;
		final long[] array = new long[length];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = (long)(Math.random() * 0xFFFFFFFFFFFFFFFFL);
		}
		
		ByteArray result = new ByteArray(length * 8);
		
		for (int i = 0; i < array.length; i++) {
			result.writeLong(i, array[i]);
			assertEquals(result.readLong(i), array[i]);
		}
	}
	
	@Test
	public final void testWriteLongsLongArray() {
		final int length = 128;
		final long[] array = new long[length];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = (long)(Math.random() * 0xFFFFFFFFFFFFFFFFL);
		}
		
		ByteArray result = new ByteArray(8 * length);
		long[] actual;
		
		for (int i = 0; i < array.length; i++) {
			result.writeLongs(array);
			actual = result.readLongs();
			assertArrayEquals(array, actual);
		}
	}
	
	@Test
	public final void testWriteLongsIntLongArray() {
		final int length = 128;
		final long[] array = new long[length];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = (long)(Math.random() * 0xFFFFFFFFFFFFFFFFL);
		}
		
		ByteArray result;
		long[] actual;
		
		for (int i = 0; i < array.length; i++) {
			result = new ByteArray(8 * length + i);
			result.writeLongs(i, array);
			actual = result.readLongs(i);
			assertArrayEquals(array, actual);
		}
	}
	
	@Test
	public final void testXorByteArray() {
		final int length = 128;
		final int[] firstArray = new int[length];
		final int[] secondArray = new int[length];
		
		for (int i = 0; i < length; i++) {
			firstArray[i] = (int)(Math.random() * 0xFF);
			secondArray[i] = (int)(Math.random() * 0xFF);
		}
		
		ByteArray first = new ByteArray(firstArray);
		ByteArray second = new ByteArray(secondArray);
		first.xor(second);
		
		for (int i = 0; i < length; i++) {
			assertEquals(firstArray[i] ^ secondArray[i], first.get(i));
			assertEquals(secondArray[i], second.get(i));
		}
	}
	
	@Test
	public final void testXorByteArrayIntInt() {
		final int length = 128;
		final int[] firstArray = new int[length];
		final int[] secondArray = new int[length];
		
		for (int i = 0; i < length; i++) {
			firstArray[i] = (int)(Math.random() * 0xFF);
			secondArray[i] = (int)(Math.random() * 0xFF);
		}
		
		ByteArray first, second;
		int[][] ranges = { {0, 0}, {0, 7}, {3, 7}, {127, 127}, {111, 127}, {19, 53} };
		int[] range;
		
		for (int j = 0; j < ranges.length; j++) {
			range = ranges[j];
			first = new ByteArray(firstArray);
			second = new ByteArray(secondArray);
			first.xor(second, range[0], range[1]);
			
			for (int i = 0; i < length; i++) {
				if (i >= range[0] && i < range[1]) {
					assertEquals(firstArray[i] ^ secondArray[i], first.get(i));
				} else {
					assertEquals(firstArray[i], first.get(i));
				}
				
				assertEquals(secondArray[i], second.get(i));
			}
		}
	}
	
	@Test
	public final void testXorByteArrayIntIntInt() {
		final int length = 128;
		final int[] firstArray = new int[length];
		final int[] secondArray = new int[length];
		
		for (int i = 0; i < length; i++) {
			firstArray[i] = (int)(Math.random() * 0xFF);
			secondArray[i] = (int)(Math.random() * 0xFF);
		}
		
		ByteArray first, second;
		int[][] ranges = { {0, 0, 0}, {0, 7, 7}, {3, 7, 17}, {127, 127, 0}, {111, 121, 6}, {19, 53, 55} };
		int[] range;
		int offset;
		
		for (int j = 0; j < ranges.length; j++) {
			range = ranges[j];
			first = new ByteArray(firstArray);
			second = new ByteArray(secondArray);
			offset = range[2];
			first.xor(second, range[0], range[1], range[2]);
			
			for (int i = 0; i < length; i++) {
				if (i >= range[0] && i < range[1]) {
					assertEquals(firstArray[i] ^ secondArray[i + offset], first.get(i));
				} else {
					assertEquals(firstArray[i], first.get(i));
				}
				
				assertEquals(secondArray[i], second.get(i));
			}
		}
	}
	
	@Test
	public final void testXorByteIntInt() {
		final int length = 128;
		final int[] array = new int[length];
		
		for (int i = 0; i < length; i++) {
			array[i] = (int)(Math.random() * 0xFF);
		}
		
		ByteArray first = new ByteArray(array);
		first.xorByte(77, 33);
		
		for (int i = 0; i < length; i++) {
			if (i == 33) {
				assertEquals(array[i] ^ 77, first.get(i));
			} else {
				assertEquals(array[i], first.get(i));
			}
		}
	}
	
	@Test
	public final void testXorByteShortInt() {
		final int length = 128;
		final int[] array = new int[length];
		
		for (int i = 0; i < length; i++) {
			array[i] = (int)(Math.random() * 0xFF);
		}
		
		ByteArray first = new ByteArray(array);
		first.xorByte((short)77, 33);
		
		for (int i = 0; i < length; i++) {
			if (i == 33) {
				assertEquals(array[i] ^ 77, first.get(i));
			} else {
				assertEquals(array[i], first.get(i));
			}
		}
	}

	@Test
	public final void testReadUInt() {
		int length = 4;
		int[] array = new int[4];
		
		for (int i = 0; i < length; i++) {
			array[i] = (int)(Math.random() * 255);
		}
		
		ByteArray result = new ByteArray(array);
		int actual, expected;
		int i = 0;
		
		expected = array[i] << 24 | array[i + 1] << 16 | array[i + 2] << 8 | array[i + 3];
		actual = result.readUInt();
		assertEquals(expected, actual);
	}

	@Test
	public final void testReadUIntInt() {
		int length = 100;
		int[] array = new int[length];
		
		for (int i = 0; i < length; i++) {
			array[i] = (int)(Math.random() * 255);
		}
		
		ByteArray result = new ByteArray(array);
		int actual, expected;
		
		for (int i = 0; i < length - 4; i++) {
			expected = array[i] << 24 | array[i + 1] << 16 | array[i + 2] << 8 | array[i + 3];
			actual = result.readUInt(i);
			assertEquals(expected, actual);
		}
	}
	
	@Test
	public final void testWriteUIntInt() {
		ByteArray result = new ByteArray(4);
		result.writeUInt(0xfedcba98);
		assertEquals(0xfedcba98, result.readUInt());
	}

	@Test(expected=ArrayIndexOutOfBoundsException.class)
	public final void testWriteUIntIntFails() {
		ByteArray result = new ByteArray(3);
		result.writeUInt(0xfedcba98);
		assertEquals(0xfedcba98, result.readUInt());
	}
	
	@Test
	public final void testWriteUIntIntInt() {
		ByteArray result = new ByteArray(128);
		result.writeUInt(4, 0xfedcba98);
		
		assertEquals(0xfedcba98, result.readUInt(4));
		assertEquals(0xdcba9800, result.readUInt(5));
		assertEquals(0xba980000, result.readUInt(6));
		assertEquals(0x98000000, result.readUInt(7));
		
		for (int i = 0; i < 125; i++) {
			if (i == 0 || i > 7) {
				assertEquals(0, result.readUInt(i));
			}
		}
		
	}

}
