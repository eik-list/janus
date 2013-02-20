package de.mslab.bicliquesearch;

import de.mslab.ciphers.ARIA128;
import de.mslab.ciphers.helpers.ARIA128Helper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class ARIA128BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new ARIA128();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new ARIA128Helper();
		
		maxNumBicliqueRounds = 3;
	}
	
}
