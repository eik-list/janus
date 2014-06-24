package de.mslab.utils;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.Biclique;

public class BicliqueXMLParserTest {
	
	private static final String PATH = "results/xml/";
	
	private static Biclique biclique;
	private static Logger logger = Logger.getLogger();
	private static BicliqueXMLParser parser;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new BicliqueXMLParser();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		parser = null;
		biclique = null;
	}
	
	@Test
	public final void testParseAESBiclique() throws Exception {
		createBiclique("AES128_8_10.xml");
	}
	
	@Test
	public final void testParseBKSQBiclique() throws Exception {
		createBiclique("BKSQ144_11_14.xml");
	}
	
	private void createBiclique(String filename) throws JAXBException {
		File file = new File(PATH + filename);
		biclique = parser.parseXML(file);
		logger.info(biclique);
	}
	
}
