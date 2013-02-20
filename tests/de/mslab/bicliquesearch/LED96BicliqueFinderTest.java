package de.mslab.bicliquesearch;

import de.mslab.ciphers.LED96;
import de.mslab.ciphers.helpers.LEDHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;

public class LED96BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new LED96();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new LEDHelper();
		
		maxNumBicliqueRounds = 16;
	}
	
}
