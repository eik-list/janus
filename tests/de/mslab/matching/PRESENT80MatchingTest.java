package de.mslab.matching;

import de.mslab.ciphers.PRESENT80;
import de.mslab.ciphers.helpers.PRESENTHelper;

public class PRESENT80MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/PRESENT80_1_4.xml";
		pdfPathname = "results/matching/PRESENT80_1_4.pdf";
		cipher = new PRESENT80();
		counter = new PRESENTHelper();
		
		super.setUp();
		matchingContext.matchingToRound = 21;
	}
	
}
