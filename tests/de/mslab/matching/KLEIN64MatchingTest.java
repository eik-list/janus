package de.mslab.matching;

import de.mslab.ciphers.KLEIN64;
import de.mslab.ciphers.helpers.KLEIN64Helper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;
import de.mslab.rendering.StateRenderer;

public class KLEIN64MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/KLEIN64_1_2.xml";
		pdfPathname = "results/matching/KLEIN64_1_2.pdf";
		cipher = new KLEIN64();
		counter = new KLEIN64Helper();
		
		super.setUp();
		
		StateRenderer stateRenderer = new LEDStateRenderer(10);
		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		differentialRenderer.setStateRenderer(stateRenderer);
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
