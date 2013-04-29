package de.mslab.matching;

import de.mslab.ciphers.MCrypton128;
import de.mslab.ciphers.helpers.MCryptonHelper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;

public class MCrypton128MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/mCrypton128_7_11.xml";
		pdfPathname = "results/matching/mCrypton128_7_11.pdf";
		cipher = new MCrypton128();
		counter = new MCryptonHelper();
		
		super.setUp();

		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
