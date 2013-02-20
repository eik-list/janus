package de.mslab.matching;

import de.mslab.ciphers.AES192;
import de.mslab.ciphers.helpers.AES192Helper;

public class AES192MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/AES192_9_12.xml";
		pdfPathname = "results/matching/AES192_9_12.pdf";
		cipher = new AES192();
		counter = new AES192Helper();
		
		super.setUp();
		biclique.dimension = 8;
	}
	
}
