package de.mslab.bicliquesearch;

import de.mslab.ciphers.WhirlpoolCipher;
import de.mslab.ciphers.helpers.WhirlpoolCipherHelper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class WhirlpoolBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new WhirlpoolCipher();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new WhirlpoolCipherHelper();
		
		maxNumBicliqueRounds = 4;
	}
	
}
