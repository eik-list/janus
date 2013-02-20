package de.mslab.matching;

import de.mslab.ciphers.BKSQ144;
import de.mslab.ciphers.helpers.BKSQ144Helper;

public class BKSQ144MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/BKSQ144_11_14.xml";
		pdfPathname = "results/matching/BKSQ144_11_14.pdf";
		cipher = new BKSQ144();
		counter = new BKSQ144Helper();
		
		super.setUp();
	}
	
}
