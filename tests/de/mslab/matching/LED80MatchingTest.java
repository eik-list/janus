package de.mslab.matching;

import de.mslab.ciphers.LED80;
import de.mslab.ciphers.helpers.LEDHelper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;

public class LED80MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/LED80_37_48.xml";
		pdfPathname = "results/matching/LED80_37_48.pdf";
		cipher = new LED80();
		counter = new LEDHelper();
		
		super.setUp();
		
		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
