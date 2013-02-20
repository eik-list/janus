package de.mslab.bicliquesearch;

import de.mslab.ciphers.LED80;
import de.mslab.ciphers.helpers.LEDHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;

public class LED80BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new LED80();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new LEDHelper();
		
		maxNumBicliqueRounds = 16;
	}
	
}
