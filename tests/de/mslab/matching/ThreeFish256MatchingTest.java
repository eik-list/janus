package de.mslab.matching;

import de.mslab.ciphers.ThreeFish256;
import de.mslab.ciphers.helpers.ThreeFishHelper;
import de.mslab.core.ByteArray;
import de.mslab.rendering.ThreeFishMatchingPhaseRenderer;

public class ThreeFish256MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/ThreeFish256_69_72.xml";
		pdfPathname = "results/matching/ThreeFish256_69_72.pdf";
		cipher = new ThreeFish256();
		counter = new ThreeFishHelper();
		
		ThreeFish256 threeFish = (ThreeFish256)cipher;
		threeFish.setTweak(new ByteArray(threeFish.getTweakSize()));
		matchingContext = new MatchingContext(biclique, cipher, counter);
		matchingContext.matchingFromRound = 41;
		
		super.setUp();
		
		renderer = new ThreeFishMatchingPhaseRenderer();
	}
	
}
