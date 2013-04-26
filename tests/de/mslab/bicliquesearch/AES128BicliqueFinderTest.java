package de.mslab.bicliquesearch;

import de.mslab.ciphers.AES128;
import de.mslab.ciphers.helpers.AES128Helper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class AES128BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new AES128();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new AES128Helper();
		
		maxNumBicliqueRounds = 4;
	}
	
}
