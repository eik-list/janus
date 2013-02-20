package de.mslab.matching;

import de.mslab.ciphers.LBlock;
import de.mslab.ciphers.helpers.LBlockHelper;

public class LBlockMatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/LBlock_1_6.xml";
		pdfPathname = "results/matching/LBlock_1_6.pdf";
		cipher = new LBlock();
		counter = new LBlockHelper();
		super.setUp();
	}
	
}
