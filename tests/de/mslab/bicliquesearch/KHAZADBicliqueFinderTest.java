package de.mslab.bicliquesearch;

import de.mslab.ciphers.Khazad;
import de.mslab.ciphers.helpers.KhazadHelper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class KHAZADBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new Khazad();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new KhazadHelper();
		
		maxNumBicliqueRounds = 4;
	}
	
}
