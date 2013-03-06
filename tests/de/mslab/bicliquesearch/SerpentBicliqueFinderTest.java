package de.mslab.bicliquesearch;

import org.junit.Test;

import de.mslab.ciphers.Serpent;
import de.mslab.ciphers.helpers.SerpentHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueDifferentialRenderer;
import de.mslab.rendering.SerpentStateRenderer;

public class SerpentBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new Serpent();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 4;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new SerpentHelper();
		
		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		differentialRenderer.setStateRenderer(new SerpentStateRenderer(4));
		renderer.setDifferentialRenderer(differentialRenderer);
		
		maxNumBicliqueRounds = 5;
	}

	@Test
	public void testFindBicliques() {
		/*
		int numRounds = finderContext.cipher.getNumRounds();
		int endRound = numRounds - maxNumBicliqueRounds + 1;
		
		if (endRound < 1) {
			endRound = 1;
		}
		
		for (int fromRound = numRounds; fromRound >= endRound; fromRound--) {
			find(fromRound, numRounds);
		}*/
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		find(1, 3);
		/*int numRounds = finderContext.cipher.getNumRounds();
		int endRound = maxNumBicliqueRounds;
		
		if (endRound > numRounds) {
			endRound = numRounds;
		}
		
		for (int toRound = 1; toRound <= endRound; toRound++) {
			find(1, toRound);
		}*/
	}
	
}
