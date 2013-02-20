package de.mslab.matching;

import de.mslab.ciphers.PRESENT128;
import de.mslab.ciphers.helpers.PRESENT128Helper;

public class PRESENT128MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/PRESENT128_1_4.xml";
		pdfPathname = "results/matching/PRESENT128_1_4.pdf";
		cipher = new PRESENT128();
		counter = new PRESENT128Helper();
		
		super.setUp();
		matchingContext.matchingToRound = 24;
	}
	
}
