package de.mslab.matching;

import de.mslab.ciphers.Serpent;
import de.mslab.ciphers.helpers.SerpentHelper;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.MatchingDifferentialRenderer;
import de.mslab.rendering.SerpentStateRenderer;

public class SerpentMatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/Serpent_1_3.xml";
		pdfPathname = "results/matching/Serpent_1_3.pdf";
		cipher = new Serpent();
		counter = new SerpentHelper();
		
		super.setUp();
		matchingContext.matchingToRound = 5;
		
		DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
		int cellSize = 10;
		differentialRenderer.setStateRenderer(new SerpentStateRenderer(cellSize));
		renderer.setDifferentialRenderer(differentialRenderer);
	}
	
}
