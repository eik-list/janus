package de.mslab.matching;

import de.mslab.ciphers.WhirlpoolCipher;
import de.mslab.ciphers.helpers.WhirlpoolCipherHelper;

public class WhirlpoolMatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/WhirlpoolCipher_9_10.xml";
		pdfPathname = "results/matching/WhirlpoolCipher_9_10.pdf";
		cipher = new WhirlpoolCipher();
		counter = new WhirlpoolCipherHelper();
		
		super.setUp();
		biclique.dimension = 8;
	}
	
}
