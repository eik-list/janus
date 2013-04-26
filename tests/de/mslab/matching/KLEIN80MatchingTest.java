package de.mslab.matching;

import de.mslab.ciphers.KLEIN80;
import de.mslab.ciphers.helpers.KLEIN80Helper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;
import de.mslab.rendering.StateRenderer;

public class KLEIN80MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/KLEIN80_1_4.xml";
		pdfPathname = "results/matching/KLEIN80_1_4.pdf";
		cipher = new KLEIN80();
		counter = new KLEIN80Helper();
		
		super.setUp();
		
		StateRenderer stateRenderer = new LEDStateRenderer(10);
		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		differentialRenderer.setStateRenderer(stateRenderer);
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
