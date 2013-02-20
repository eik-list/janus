package de.mslab.matching;

import de.mslab.ciphers.ThreeFish1024;
import de.mslab.ciphers.helpers.ThreeFishHelper;
import de.mslab.core.ByteArray;
import de.mslab.rendering.ThreeFishMatchingPhaseRenderer;

public class ThreeFish1024MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/ThreeFish1024_77_80.xml";
		pdfPathname = "results/matching/ThreeFish1024_77_80.pdf";
		cipher = new ThreeFish1024();
		counter = new ThreeFishHelper();
		
		ThreeFish1024 threeFish = (ThreeFish1024)cipher;
		threeFish.setTweak(new ByteArray(threeFish.getTweakSize()));
		matchingContext = new MatchingContext(biclique, cipher, counter);
		matchingContext.matchingFromRound = 55;
		
		super.setUp();
		
		renderer = new ThreeFishMatchingPhaseRenderer();
	}
	
}
