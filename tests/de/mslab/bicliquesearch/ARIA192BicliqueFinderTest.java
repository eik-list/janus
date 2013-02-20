package de.mslab.bicliquesearch;

import de.mslab.ciphers.ARIA192;
import de.mslab.ciphers.helpers.ARIA192Helper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class ARIA192BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new ARIA192();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new ARIA192Helper();
		
		maxNumBicliqueRounds = 4;
	}
	
}
