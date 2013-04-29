package de.mslab.matching;

import de.mslab.ciphers.MCrypton64;
import de.mslab.ciphers.helpers.MCryptonHelper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;

public class MCrypton64MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/mCrypton64_2_3.xml";
		pdfPathname = "results/matching/mCrypton64_2_3.pdf";
		cipher = new MCrypton64();
		counter = new MCryptonHelper();
		
		super.setUp();
		
		matchingContext.matchingFromRound = 3;
		matchingContext.matchingToRound = 12;
		matchingContext.numMatchingBits = 8;
		
		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
