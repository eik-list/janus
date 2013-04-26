package de.mslab.bicliquesearch;

import de.mslab.ciphers.AES256;
import de.mslab.ciphers.helpers.AES256Helper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class AES256BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new AES256();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new AES256Helper();
		
		maxNumBicliqueRounds = 5;
	}
	
}
