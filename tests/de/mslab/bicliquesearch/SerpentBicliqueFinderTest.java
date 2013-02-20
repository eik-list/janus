package de.mslab.bicliquesearch;

import de.mslab.ciphers.Serpent;
import de.mslab.ciphers.helpers.SerpentHelper;
import de.mslab.diffbuilder.SerpentNibblewiseDifferenceBuilder;

public class SerpentBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new Serpent();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 4;
		finderContext.differenceBuilder = new SerpentNibblewiseDifferenceBuilder();
		finderContext.comparator = new SerpentHelper();
		
//		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
//		differentialRenderer.setStateRenderer(new SerpentStateRenderer(10));
//		renderer.setDifferentialRenderer(differentialRenderer);
		
		maxNumBicliqueRounds = 5;
	}
	
}
