package de.mslab.bicliquesearch;

import de.mslab.ciphers.LED112;
import de.mslab.ciphers.helpers.LEDHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;

public class LED112BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new LED112();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new LEDHelper();
		
		maxNumBicliqueRounds = 16;
	}
	
}
