package de.mslab.matching;

import de.mslab.ciphers.ARIA192;
import de.mslab.ciphers.helpers.ARIA192Helper;

public class ARIA192MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/ARIA192_1_1.xml";
		pdfPathname = "results/matching/ARIA192_1_1.pdf";
		cipher = new ARIA192();
		counter = new ARIA192Helper();
		
		super.setUp();
	}
	
}
