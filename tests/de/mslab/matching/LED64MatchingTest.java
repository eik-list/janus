package de.mslab.matching;

import de.mslab.ciphers.LED64;
import de.mslab.ciphers.helpers.LEDHelper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;

public class LED64MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/LED64_17_20.xml";
		pdfPathname = "results/matching/LED64_17_20.pdf";
		cipher = new LED64();
		counter = new LEDHelper();
		
		super.setUp();
		
		matchingContext.numMatchingBits = 8;
		
		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
