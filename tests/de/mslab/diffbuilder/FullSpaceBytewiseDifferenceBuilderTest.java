package de.mslab.diffbuilder;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.utils.MathUtil;

public class FullSpaceBytewiseDifferenceBuilderTest {
	
	private static DifferenceBuilder builder;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		builder = new FullSpaceBytewiseDifferenceBuilder();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		builder = null;
	}
	
	@Test
	public final void testComputeNextValueWithMaxHammingWeight() {
		int dimension = 8;
		int numBytes = 16;
		Runtime runtime = Runtime.getRuntime();
		System.gc();
		long memory = runtime.totalMemory() - runtime.freeMemory();
		
		long expectedNumResults = MathUtil.computeBinomialCoefficientSum(numBytes, numBytes) - 1;
		long numResults = builder.initializeAndGetNumDifferences(dimension, numBytes);
		assertEquals(expectedNumResults, numResults);
		
		//ByteArray result = null;
		
		for (int i = 0; i != numResults; i++) {
			builder.next();
		}
		
		memory = runtime.totalMemory() - runtime.freeMemory() - memory;
		System.gc();
	}
	
}
