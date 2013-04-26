package de.mslab.bicliquesearch;

import de.mslab.ciphers.KLEIN96;
import de.mslab.ciphers.helpers.KLEIN96Helper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueDifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;

public class KLEIN96BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new KLEIN96();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new KLEIN96Helper();
		
		maxNumBicliqueRounds = 5;
		
		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		differentialRenderer.setStateRenderer(new LEDStateRenderer(10));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
