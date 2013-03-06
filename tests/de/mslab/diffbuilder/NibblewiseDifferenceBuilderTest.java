package de.mslab.diffbuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.ByteArray;
import de.mslab.core.Nibble;
import de.mslab.utils.Logger;
import de.mslab.utils.MathUtil;


public class NibblewiseDifferenceBuilderTest {
	
	private static NibblewiseDifferenceBuilder builder;
	private static Logger logger = new Logger();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		builder = new NibblewiseDifferenceBuilder();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		builder = null;
	}

	@Test
	public void test() {
		int numActiveNibbles = 2;
		int dimension = numActiveNibbles * Nibble.SIZE;
		int numBytes = 10;
		
		long expectedNumResults = MathUtil.computeBinomialCoefficient(2 * numBytes, numActiveNibbles);
		long numResults = builder.initializeAndGetNumDifferences(dimension, numBytes);
		assertEquals(expectedNumResults, numResults);
		
		DifferenceIterator iterator = null;
		ByteArray difference = null;
		
		for (int i = 0; i != numResults; i++) {
			iterator = builder.next();
			logger.info("i: {0}", i);
			
			while(iterator.hasNext()) {
				difference = iterator.next();
				assertFalse(MathUtil.hasHigherHammingWeightThan(difference, dimension, numBytes));
			}
		}
	}
		
	public final void testComputeNextValueWithMaxHammingWeight() {
		int maxWeight = 3;
		int numBytes = 16;
		int numBits = 16;
		Runtime runtime = Runtime.getRuntime();
		long memory = runtime.totalMemory() - runtime.freeMemory();
		
		long expectedNumResults = MathUtil.computeBinomialCoefficientSum(2 * numBits, maxWeight) - 1;
		long numResults = builder.initializeAndGetNumDifferences(maxWeight, numBits);
		logger.info("numResults: {0}", numResults);
		assertEquals(expectedNumResults, numResults);
		
		DifferenceIterator iterator = null;
		ByteArray difference = null;
		
		for (int i = 0; i != numResults; i++) {
			iterator = builder.next();
			
			while(iterator.hasNext()) {
				difference = iterator.next();
				assertFalse(MathUtil.hasHigherHammingWeightThan(difference, 2 * numBytes, numBytes));
			}
		}
		
		memory = runtime.totalMemory() - runtime.freeMemory() - memory;
		logger.info("Memory used {0}", memory);
	}
	
}
