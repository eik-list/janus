package de.mslab.matching;

import de.mslab.ciphers.BKSQ96;
import de.mslab.ciphers.helpers.BKSQ96Helper;

public class BKSQ96MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/BKSQ96_8_10.xml";
		pdfPathname = "results/matching/BKSQ96_8_10.pdf";
		cipher = new BKSQ96();
		counter = new BKSQ96Helper();
		
		super.setUp();
		biclique.dimension = 8;
	}
	
}
