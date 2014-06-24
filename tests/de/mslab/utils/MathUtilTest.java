package de.mslab.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class MathUtilTest {
	
	@Test
	public void testBinomialCoefficientSumWith32() {
		int n = 32;
		long[] expected = { 1, 32, 496, 4960, 35960, 201376, 906192 };
		long expectedSum = 0;
		long result;
		
		for (int k = 0; k < expected.length; k++) {
			result = MathUtil.computeBinomialCoefficientSum(n, k);
			expectedSum += expected[k];
			assertEquals(expectedSum, result);
		}
	}

	@Test
	public void testBinomialCoefficientSumWith128() {
		int n = 128;
		long[] expected = { 1, 128, 8128, 341376, 10668000, 264566400, 5423611200L };
		long expectedSum = 0;
		long result;
		
		for (int k = 0; k < expected.length; k++) {
			result = MathUtil.computeBinomialCoefficientSum(n, k);
			expectedSum += expected[k];
			assertEquals(expectedSum, result);
		}
	}
	
	@Test
	public void testBinomialCoefficientSum() {
		int n = 6;
		long[] expected = { 1, 6, 15, 20, 15, 6, 1 };
		long expectedSum = 0;
		long result;
		
		for (int k = 0; k < expected.length; k++) {
			result = MathUtil.computeBinomialCoefficientSum(n, k);
			expectedSum += expected[k];
			assertEquals(expectedSum, result);
		}
	}
	
	@Test
	public void testBinomialCoefficientSumWithBigNumber() {
		int n = 1024;
		long[] expected = { 1, 1024, 523776, 178433024 };
		long expectedSum = 0;
		long result;
		
		for (int k = 0; k < expected.length; k++) {
			result = MathUtil.computeBinomialCoefficientSum(n, k);
			expectedSum += expected[k];
			assertEquals(expectedSum, result);
		}
	}
	
	@Test
	public void testBinomialCoefficientSumLowerBound() {
		int n = 1;
		long[] expected = { 1 };
		long result;
		
		for (int k = 0; k < expected.length; k++) {
			result = MathUtil.computeBinomialCoefficientSum(n, k);
			assertEquals(expected[k], result);
		}
	}
	
	@Test
	public void testBinomialCoefficientSumThrowsErrorKLargerThanN() {
		int n = 6;
		int k = 7;
		
		try {
			MathUtil.computeBinomialCoefficientSum(n, k);
			fail("Should throw error if k > n");
		} catch (IllegalArgumentException exception) { }
	}

	@Test
	public void testBinomialCoefficientSumThrowsErrorIfNIsZero() {
		int n = 0;
		int k = 7;
		
		try {
			MathUtil.computeBinomialCoefficientSum(n, k);
			fail("Should throw error if n is zero");
		} catch (IllegalArgumentException exception) { }
	}
	
	@Test
	public final void testGetHammingWeightForZero() {
		int expected = 0;
		int result = MathUtil.getHammingWeightForValue(0);
		assertEquals(expected, result);
	}
	
	@Test
	public final void testGetHammingWeightForOther() {
		int expected = 9;
		int result = MathUtil.getHammingWeightForValue(511);
		assertEquals(expected, result);
	}
	
	@Test
	public final void testGetHammingWeightForLong() {
		int expected = 1;
		int result = MathUtil.getHammingWeightForValue(4294967296L);
		assertEquals(expected, result);
	}
	
	@Test
	public final void testGetHammingWeightForNegative() {
		int expected = 32;
		int result = MathUtil.getHammingWeightForValue(-1);
		assertEquals(expected, result);
	}
	
	@Test
	public final void testGetHammingWeightForNegativeLong() {
		int expected = 32;
		int result = MathUtil.getHammingWeightForValue(-4294967296L);
		assertEquals(expected, result);
	}

	@Test
	public final void testGetHammingWeightForNegativeLongMaxWeight() {
		int expected = 64;
		int result = MathUtil.getHammingWeightForValue(-1L);
		assertEquals(expected, result);
	}
	
	@Test
	public final void testHasHigherHammingWeightThanFalse() {
		// 38 should have a weight of 3 what is not higher than 3
		assertFalse(MathUtil.hasHigherHammingWeightThan(38, 3, 4));
	}

	@Test
	public final void testHasHigherHammingWeightThan() {
		// 39 should have a weight of 4 what is higher than 3
		assertTrue(MathUtil.hasHigherHammingWeightThan(39, 3, 4));
	}
	
}
