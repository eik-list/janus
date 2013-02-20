package de.mslab.bicliquesearch;

import de.mslab.ciphers.BKSQ192;
import de.mslab.ciphers.helpers.BKSQ192Helper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class BKSQ192BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new BKSQ192();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new BKSQ192Helper();
		
		maxNumBicliqueRounds = 6;
	}
	
}
