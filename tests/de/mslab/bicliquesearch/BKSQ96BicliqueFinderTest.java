package de.mslab.bicliquesearch;

import de.mslab.ciphers.BKSQ96;
import de.mslab.ciphers.helpers.BKSQ96Helper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class BKSQ96BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new BKSQ96();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new BKSQ96Helper();
		
		maxNumBicliqueRounds = 5;
	}
	
}
