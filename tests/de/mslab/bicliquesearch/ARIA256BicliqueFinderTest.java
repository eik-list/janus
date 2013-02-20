package de.mslab.bicliquesearch;

import de.mslab.ciphers.ARIA;
import de.mslab.ciphers.ARIA256;
import de.mslab.ciphers.helpers.ARIA256Helper;
import de.mslab.diffbuilder.ARIA256DifferenceBuilder;

public class ARIA256BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new ARIA256();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		
		ARIA256DifferenceBuilder differenceBuilder = new ARIA256DifferenceBuilder();
		differenceBuilder.cipher = (ARIA)finderContext.cipher;
		finderContext.differenceBuilder = differenceBuilder;
		finderContext.comparator = new ARIA256Helper();
		maxNumBicliqueRounds = 4;
	}
	
}
