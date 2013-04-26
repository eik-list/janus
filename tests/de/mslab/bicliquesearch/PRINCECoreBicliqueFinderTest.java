package de.mslab.bicliquesearch;

import de.mslab.ciphers.PRINCECore;
import de.mslab.ciphers.helpers.PRINCECoreHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueDifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;

public class PRINCECoreBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new PRINCECore();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new PRINCECoreHelper();
		
		maxNumBicliqueRounds = 5;
		
		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
