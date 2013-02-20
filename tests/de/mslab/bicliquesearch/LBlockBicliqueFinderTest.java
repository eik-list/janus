package de.mslab.bicliquesearch;

import de.mslab.ciphers.LBlock;
import de.mslab.ciphers.helpers.LBlockHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;

public class LBlockBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new LBlock();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new LBlockHelper();
		
		maxNumBicliqueRounds = 10;
	}
	
}
