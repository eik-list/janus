package de.mslab.bicliquesearch;

import de.mslab.ciphers.MCrypton128;
import de.mslab.ciphers.helpers.MCryptonHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueDifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;

public class MCrypton128BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new MCrypton128();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new MCryptonHelper();
		
		maxNumBicliqueRounds = 4;
		
		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
