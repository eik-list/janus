package de.mslab.bicliquesearch;

import org.junit.Test;

import de.mslab.ciphers.PRESENT128;
import de.mslab.ciphers.helpers.PRESENTHelper;
import de.mslab.diffbuilder.PRESENTNibblewiseDifferenceBuilder;

public class PRESENT128BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new PRESENT128();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new PRESENTNibblewiseDifferenceBuilder();
		finderContext.comparator = new PRESENTHelper();
		
		maxNumBicliqueRounds = 5;
	}
	
	@Test
	public void testFindBicliques() {
		
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		find(1, 4);
	}
	
}
