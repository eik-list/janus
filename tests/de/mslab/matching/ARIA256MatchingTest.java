package de.mslab.matching;

import de.mslab.ciphers.ARIA256;
import de.mslab.ciphers.helpers.ARIA256Helper;

public class ARIA256MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/ARIA256_1_2.xml";
		pdfPathname = "results/matching/AES256_1_2.pdf";
		cipher = new ARIA256();
		counter = new ARIA256Helper();
		
		super.setUp();
	}
	
}
