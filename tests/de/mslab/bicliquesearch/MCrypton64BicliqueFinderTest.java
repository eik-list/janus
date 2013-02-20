package de.mslab.bicliquesearch;

import org.junit.Test;

import de.mslab.ciphers.MCrypton64;
import de.mslab.ciphers.helpers.MCryptonHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;

public class MCrypton64BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new MCrypton64();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new MCryptonHelper();
		
		maxNumBicliqueRounds = 4;
	}
	
	@Test
	public void testFindBicliques() {
		int numRounds = finderContext.cipher.getNumRounds() - 1;
		int endRound = numRounds - maxNumBicliqueRounds + 1;
		
		if (endRound < 1) {
			endRound = 1;
		}
		
		for (int fromRound = numRounds; fromRound >= endRound; fromRound--) {
			find(fromRound, numRounds);
		}
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		int numRounds = finderContext.cipher.getNumRounds();
		int endRound = maxNumBicliqueRounds;
		int fromRound = 2;
		
		if (endRound > numRounds) {
			endRound = numRounds;
		}
		
		for (int toRound = fromRound; toRound <= endRound; toRound++) {
			find(fromRound, toRound);
		}
	}
	
}
