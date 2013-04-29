package de.mslab.bicliquesearch;

import org.junit.Test;

import de.mslab.bicliquesearch.helpers.AESBasedBicliqueRater;
import de.mslab.ciphers.SQUARE;
import de.mslab.ciphers.helpers.SQUAREHelper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class SQUAREBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new SQUARE();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new SQUAREHelper();
		finderContext.bicliqueRater = new AESBasedBicliqueRater();
		
		maxNumBicliqueRounds = 4;
	}
	
	@Test
	public void testFindBicliques() {
		find(7, 8);
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		
	}
	
}
