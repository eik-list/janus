package de.mslab.matching;

import de.mslab.ciphers.MCrypton96;
import de.mslab.ciphers.helpers.MCryptonHelper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;

public class MCrypton96MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/mCrypton96_9_12.xml";
		pdfPathname = "results/matching/mCrypton96_9_12.pdf";
		cipher = new MCrypton96();
		counter = new MCryptonHelper();
		
		super.setUp();
		
		matchingContext.matchingFromRound = 2;
		
		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
