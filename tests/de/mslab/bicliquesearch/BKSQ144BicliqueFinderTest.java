package de.mslab.bicliquesearch;

import de.mslab.ciphers.BKSQ144;
import de.mslab.ciphers.helpers.BKSQ144Helper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class BKSQ144BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new BKSQ144();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new BKSQ144Helper();
		
		maxNumBicliqueRounds = 5;
	}
	
}
