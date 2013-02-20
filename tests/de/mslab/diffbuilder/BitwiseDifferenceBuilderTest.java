package de.mslab.diffbuilder;

import static org.junit.Assert.assertFalse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.ByteArray;
import de.mslab.utils.Logger;
import de.mslab.utils.MathUtil;

public class BitwiseDifferenceBuilderTest {
	
	private static BitwiseDifferenceBuilder builder;
	private static Logger logger = new Logger();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		builder = new BitwiseDifferenceBuilder();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		builder = null;
	}
	
	@Test
	public final void testComputeNextValueWithMaxHammingWeight() {
		int dimension = 8;
		int numBits = 128;
		int numBytes = (int)Math.ceil((double)numBits / 8);
		Runtime runtime = Runtime.getRuntime();
		System.gc();
		long memory = runtime.totalMemory() - runtime.freeMemory();
		long numResults = builder.initializeAndGetNumDifferences(dimension, numBits);
		
		DifferenceIterator iterator = null;
		ByteArray difference = null;
		
		for (int i = 0; i != numResults; i++) {
			iterator = builder.next();
			
			while(iterator.hasNext()) {
				difference = iterator.next();
				assertFalse(MathUtil.hasHigherHammingWeightThan(difference, dimension, numBytes));
			}
		}
		
		memory = runtime.totalMemory() - runtime.freeMemory() - memory;
		logger.info("Memory used {0}", memory);
	}
	
}
