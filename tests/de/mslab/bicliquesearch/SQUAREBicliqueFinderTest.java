package de.mslab.bicliquesearch;

import de.mslab.ciphers.SQUARE;
import de.mslab.ciphers.helpers.SQUAREHelper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class SQUAREBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new SQUARE();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new SQUAREHelper();
		
		maxNumBicliqueRounds = 4;
	}
	
}
