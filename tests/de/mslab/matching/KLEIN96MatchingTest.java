package de.mslab.matching;

import de.mslab.ciphers.KLEIN96;
import de.mslab.ciphers.helpers.KLEIN96Helper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;
import de.mslab.rendering.StateRenderer;

public class KLEIN96MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/KLEIN96_1_4.xml";
		pdfPathname = "results/matching/KLEIN96_1_4.pdf";
		cipher = new KLEIN96();
		counter = new KLEIN96Helper();
		
		super.setUp();
		
		StateRenderer stateRenderer = new LEDStateRenderer(10);
		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		differentialRenderer.setStateRenderer(stateRenderer);
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
