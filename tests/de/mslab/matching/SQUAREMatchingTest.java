package de.mslab.matching;

import de.mslab.ciphers.SQUARE;
import de.mslab.ciphers.helpers.SQUAREHelper;

public class SQUAREMatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/SQUARE_7_8.xml";
		pdfPathname = "results/matching/SQUARE_7_8.pdf";
		cipher = new SQUARE();
		counter = new SQUAREHelper();
		
		super.setUp();
	}
	
}
