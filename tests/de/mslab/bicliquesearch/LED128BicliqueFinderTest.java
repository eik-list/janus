package de.mslab.bicliquesearch;

import org.junit.Test;

import de.mslab.bicliquesearch.helpers.LED128BicliqueRater;
import de.mslab.ciphers.LED128;
import de.mslab.ciphers.helpers.LEDHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueDifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;

public class LED128BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new LED128();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new LEDHelper();
		finderContext.bicliqueRater = new LED128BicliqueRater();
		
		maxNumBicliqueRounds = 8;
		
		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		differentialRenderer.setStateRenderer(new LEDStateRenderer(10));
		renderer.setDifferentialRenderer(differentialRenderer);
	}

	@Test
	public void testFindBicliques() {
		find(25,32);
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		
	}
	
}
