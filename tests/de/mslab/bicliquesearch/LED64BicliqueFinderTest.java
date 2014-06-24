package de.mslab.bicliquesearch;

import org.junit.Test;

import de.mslab.bicliquesearch.helpers.LED64BicliqueRater;
import de.mslab.ciphers.LED64;
import de.mslab.ciphers.helpers.LEDHelper;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueDifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;

public class LED64BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new LED64();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new LEDHelper();
		finderContext.bicliqueRater = new LED64BicliqueRater();
		maxNumBicliqueRounds = 4;
		
		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
	@Test
	public void testFindBicliques() {
		find(17,20);
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		
	}
	
}
