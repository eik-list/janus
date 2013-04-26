package de.mslab.bicliquesearch;

import de.mslab.ciphers.KLEIN64;
import de.mslab.ciphers.helpers.KLEIN64Helper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueDifferentialRenderer;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.StateRenderer;

public class KLEIN64BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new KLEIN64();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new KLEIN64Helper();
		
		maxNumBicliqueRounds = 4;
		
		StateRenderer stateRenderer = new LEDStateRenderer(10);
		DifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		differentialRenderer.setStateRenderer(stateRenderer);
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
