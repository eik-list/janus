package de.mslab.matching;

import de.mslab.ciphers.BKSQ192;
import de.mslab.ciphers.helpers.BKSQ192Helper;

public class BKSQ192MatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/BKSQ192_14_18.xml";
		pdfPathname = "results/matching/BKSQ192_14_18.pdf";
		cipher = new BKSQ192();
		counter = new BKSQ192Helper();
		
		super.setUp();
		biclique.dimension = 8;
	}
	
}
