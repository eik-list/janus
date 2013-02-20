package de.mslab.matching;

import de.mslab.ciphers.AES128;
import de.mslab.ciphers.helpers.AES128Helper;

public class AES128MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/AES128_8_10.xml";
		pdfPathname = "results/matching/AES128_8_10.pdf";
		cipher = new AES128();
		counter = new AES128Helper();
		
		super.setUp();
	}
	
}
