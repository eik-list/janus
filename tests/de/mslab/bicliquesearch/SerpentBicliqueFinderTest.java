package de.mslab.bicliquesearch;

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
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new SerpentHelper();
		
		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		differentialRenderer.setStateRenderer(new SerpentStateRenderer(4));
		renderer.setDifferentialRenderer(differentialRenderer);
		
		maxNumBicliqueRounds = 5;
	}
	
}
