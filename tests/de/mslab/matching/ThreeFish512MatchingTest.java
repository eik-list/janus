package de.mslab.matching;

import de.mslab.ciphers.ThreeFish512;
import de.mslab.ciphers.helpers.ThreeFishHelper;
import de.mslab.core.ByteArray;
import de.mslab.rendering.ThreeFishMatchingPhaseRenderer;

public class ThreeFish512MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/ThreeFish512_69_72.xml";
		pdfPathname = "results/matching/ThreeFish512_69_72.pdf";
		cipher = new ThreeFish512();
		counter = new ThreeFishHelper();
		
		ThreeFish512 threeFish = (ThreeFish512)cipher;
		threeFish.setTweak(new ByteArray(threeFish.getTweakSize()));
		matchingContext = new MatchingContext(biclique, cipher, counter);
		matchingContext.matchingFromRound = 49;
		
		super.setUp();
		
		renderer = new ThreeFishMatchingPhaseRenderer();
	}
	
}
