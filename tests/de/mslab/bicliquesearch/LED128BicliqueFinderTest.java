package de.mslab.bicliquesearch;

import org.junit.Test;

import de.mslab.ciphers.LED128;
import de.mslab.ciphers.helpers.LEDHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueDifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;

public class LED128BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new LED128();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new LEDHelper();
		
		maxNumBicliqueRounds = 16;
		
		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		differentialRenderer.setStateRenderer(new LEDStateRenderer(10));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
	@Test
	public void testFindBicliques() {
		int numRounds = finderContext.cipher.getNumRounds();
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
		int endRound = maxNumBicliqueRounds + 1;
		
		if (endRound > numRounds) {
			endRound = numRounds;
		}
		
		for (int toRound = 2; toRound <= endRound; toRound++) {
			find(1, toRound);
		}
	}
	
}
