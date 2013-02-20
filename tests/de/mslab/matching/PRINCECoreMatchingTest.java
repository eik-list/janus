package de.mslab.matching;

import de.mslab.ciphers.PRINCECore;
import de.mslab.ciphers.helpers.PRINCECoreHelper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;

public class PRINCECoreMatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/PRINCECore_11_11.xml";
		pdfPathname = "results/matching/PRINCECore_11_11.pdf";
		cipher = new PRINCECore();
		counter = new PRINCECoreHelper();
		
		super.setUp();
		
		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new LEDStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
