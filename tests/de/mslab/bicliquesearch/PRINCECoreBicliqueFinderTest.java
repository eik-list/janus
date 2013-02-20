package de.mslab.bicliquesearch;

import org.junit.Test;

import de.mslab.ciphers.PRINCECore;
import de.mslab.ciphers.helpers.PRINCECoreHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueDifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;

public class PRINCECoreBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new PRINCECore();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new PRINCECoreHelper();
		
		maxNumBicliqueRounds = 5;
		
		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
	@Test
	public void testFindBicliques() {
		int numRounds = finderContext.cipher.getNumRounds(); // - 1;
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
		int fromRound = 1;
		
		if (endRound > numRounds) {
			endRound = numRounds;
		}
		
		for (int toRound = fromRound; toRound <= endRound; toRound++) {
			find(fromRound, toRound);
		}
	}
	
}
