package de.mslab.benchmarks;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.itextpdf.text.DocumentException;

import de.mslab.ciphers.AES128;
import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.ciphers.helpers.AES128Helper;
import de.mslab.ciphers.helpers.RecomputedOperationsCounter;
import de.mslab.core.Biclique;
import de.mslab.matching.MatchingContext;
import de.mslab.matching.MatchingFinder;
import de.mslab.utils.BicliqueXMLParser;
import de.mslab.utils.Logger;

public class MatchingBenchmarkTest {
	
	private static final long BILLION = 1000000000L;
	
	private Biclique biclique;
	private RoundBasedBlockCipher cipher;
	private RecomputedOperationsCounter counter;
	private Logger logger = Logger.getLogger();
	private MatchingFinder matchingDifferentialBuilder;
	
	private BicliqueXMLParser parser;
	private String xmlPathname;
	private int numIterations;
	
	public MatchingBenchmarkTest() {
		setUp();
	}
	
	public void setUp() {
		cipher = new AES128();
		counter = new AES128Helper();
		matchingDifferentialBuilder = new MatchingFinder();
		numIterations = 100;
		xmlPathname = "results/xml/AES128_8_10.xml";
		
		parser = new BicliqueXMLParser();
		loadAndParseXML(xmlPathname);
	}
	
	public void tearDownAfterClass() {
		cipher = null;
		counter = null;
		matchingDifferentialBuilder = null;
	}
	
	@Test
	public void testMatch() throws IOException, DocumentException {
		long startTime = System.nanoTime();
		
		for (int i = 0; i < numIterations; i++) {
			MatchingContext matchingContext = new MatchingContext(biclique, cipher, counter);
			matchingDifferentialBuilder.findOptimalMatching(matchingContext);
		}
		
		long endTime = System.nanoTime();
		double elapsedTime = (double)(endTime - startTime) / BILLION;
		double timePerMatching = (long)(10000.0 * elapsedTime / numIterations) / 10000.0;
		logger.info("{0} matchings, in avg {1} s/iteration", numIterations, timePerMatching);
	}
	
	private void loadAndParseXML(String pathname) {
		File file = new File(pathname);
		
		try {
			biclique = parser.parseXML(file);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
}
