package de.mslab.matching;

import de.mslab.ciphers.LED96;
import de.mslab.ciphers.helpers.LEDHelper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;

public class LED96MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/LED96_37_48.xml";
		pdfPathname = "results/matching/LED96_37_48.pdf";
		cipher = new LED96();
		counter = new LEDHelper();
		
		super.setUp();
		
		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
