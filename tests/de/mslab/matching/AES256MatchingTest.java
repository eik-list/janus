package de.mslab.matching;

import de.mslab.ciphers.AES256;
import de.mslab.ciphers.helpers.AES256Helper;

public class AES256MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/AES256_11_14.xml";
		pdfPathname = "results/matching/AES256_11_14.pdf";
		cipher = new AES256();
		counter = new AES256Helper();
		
		super.setUp();
		biclique.dimension = 8;
	}
	
}
