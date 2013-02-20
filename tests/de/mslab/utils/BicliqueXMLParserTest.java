package de.mslab.utils;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.Biclique;

public class BicliqueXMLParserTest {
	
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
	
	@Test
	public final void testParseLED64StartBiclique() throws Exception {
		createBiclique("LED64_2_7.xml");
	}
	
	@Test
	public final void testParseLED64EndBiclique() throws Exception {
		createBiclique("LED64_25_31.xml");
	}
	
	@Test
	public final void testParseLED128StartBiclique() throws Exception {
		createBiclique("LED128_2_15.xml");
	}
	
	@Test
	public final void testParseLED128EndBiclique() throws Exception {
		createBiclique("LED128_37_48.xml");
	}
	
	private void createBiclique(String filename) throws JAXBException {
		String path = "results/xml/";
		File file = new File(path + filename);
		biclique = parser.parseXML(file);
		logger.info(biclique);
	}
	
}
