package de.mslab.diffbuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.ByteArray;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;
import de.mslab.utils.Logger;
import de.mslab.utils.MathUtil;

public class BytewiseDifferenceBuilderTest {
	
	private static BytewiseDifferenceBuilder builder;
	private static Logger logger = new Logger();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		builder = new BytewiseDifferenceBuilder();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		builder = null;
	}
	
	@Test
	public final void testComputeNextValueWithMaxHammingWeight() {
		int dimension = 12;
		int numBytes = 16;
		int weight = (int)(Math.ceil((double)dimension / (double)Byte.SIZE)); 
		Runtime runtime = Runtime.getRuntime();
		System.gc();
		long memory = runtime.totalMemory() - runtime.freeMemory();
		
		long expectedNumResults = MathUtil.computeBinomialCoefficient(numBytes, weight);
		long numResults = builder.initializeAndGetNumDifferences(dimension, numBytes);
		assertEquals(expectedNumResults, numResults);
		
		DifferenceIterator iterator = null;
		ByteArray difference = null;
		
		for (int i = 0; i != numResults; i++) {
			iterator = builder.next();
			
			while(iterator.hasNext()) {
				difference = iterator.next();
				
				if (i == 5) {
					logger.info(difference);
				}
				
				assertFalse(MathUtil.hasHigherHammingWeightThan(difference, dimension, numBytes));
			}
		}
		
		memory = runtime.totalMemory() - runtime.freeMemory() - memory;
		logger.info("Memory used {0}", memory);
	}
	
}
