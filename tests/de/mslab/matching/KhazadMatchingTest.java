package de.mslab.matching;

import de.mslab.ciphers.Khazad;
import de.mslab.ciphers.helpers.KhazadHelper;

public class KhazadMatchingTest extends AbstractMatcherTest { 
	
	public void setUp() {
		xmlPathname = "results/xml/Khazad_6_8.xml";
		pdfPathname = "results/matching/Khazad_6_8.pdf";
		cipher = new Khazad();
		counter = new KhazadHelper();
		
		super.setUp();
		biclique.dimension = 8;
	}
	
}
