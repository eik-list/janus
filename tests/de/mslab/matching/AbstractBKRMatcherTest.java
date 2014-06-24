package de.mslab.matching;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.itextpdf.text.DocumentException;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.ciphers.helpers.RecomputedOperationsCounter;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.diffbuilder.BKRDifferentialBuilder;
import de.mslab.rendering.BicliqueRenderer;
import de.mslab.rendering.IBicliqueRenderer;
import de.mslab.rendering.MatchingPhaseRenderer;
import de.mslab.utils.BicliqueXMLSerializer;
import de.mslab.utils.Logger;

public abstract class AbstractBKRMatcherTest {
	
	protected Biclique biclique;
	protected RoundBasedBlockCipher cipher;
	protected RecomputedOperationsCounter counter;
	
	protected DecimalFormat decimalFormat;
	protected BKRDifferentialBuilder differentialBuilder;
	protected Logger logger = Logger.getLogger();
	protected MatchingFinder matchingDifferentialBuilder;
	protected ComplexityCalculator complexityCalculator;
	
	protected IBicliqueRenderer bicliqueRenderer;
	protected BicliqueXMLSerializer serializer;
	protected MatchingPhaseRenderer matchingRenderer;
	
	protected String bicliquePath = "results/biclique/";
	protected String matchingPath = "results/matching/";
	protected String xmlPath = "results/xml/";
	protected String filename;
	
	protected double complexityExpected;
	protected double epsilon = 0.001;
	protected int numActiveBytesExpected;
	protected int matchingRound;
	protected ByteArray matchingStateDifference;
	
	protected AbstractBKRMatcherTest() {
		setUp();
	}
	
	public void setUp() {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(','); 
		decimalFormat = new DecimalFormat("#.##", otherSymbols);
		
		complexityCalculator = new ComplexityCalculator();
		matchingDifferentialBuilder = new MatchingFinder();
		matchingRenderer = new MatchingPhaseRenderer();
		bicliqueRenderer = new BicliqueRenderer();
		serializer = new BicliqueXMLSerializer();
	}
	
	public void tearDownAfterClass() {
		complexityCalculator = null;
		cipher = null;
	}
	
	@Test
	public void testMatch() throws IOException, DocumentException, JAXBException {
		createBiclique();
		serializeBiclique();
		bicliqueRenderer.renderBiclique(bicliquePath + filename + ".pdf", biclique, cipher);
		
		MatchingContext matchingContext = new MatchingContext(biclique, cipher, counter, matchingRound, matchingStateDifference);
		MatchingFinderResult matchingResult = matchingDifferentialBuilder.findOptimalMatching(matchingContext);
		logMatchingResult(matchingResult);
		
		ComplexityCalculationResult result = complexityCalculator.computeComplexity(
			cipher, 
			matchingResult.dimension, 
			matchingResult.minRecomputedOperations, 
			matchingResult.numBicliqueRounds, 
			matchingResult.matchingToRound - matchingResult.matchingFromRound + 1, 
			matchingContext.numMatchingBits
		);
		logComplexity(matchingResult, result);
		
		matchingRenderer.renderMatchingPhase(matchingPath + filename + ".pdf", matchingResult, cipher);
		
		assertEquals(numActiveBytesExpected, matchingResult.minRecomputedOperations);
		assertEquals(complexityExpected, result.totalComplexityLog, epsilon);
	}
	
	protected void createBiclique() {
		
	}
	
	protected void renderMatchingDifferential(MatchingFinderResult matchingResult, String pathname) throws IOException, DocumentException {
		matchingRenderer.renderMatchingPhase(pathname, matchingResult, cipher);
	}
	
	protected void logMatchingResult(MatchingFinderResult matchingResult) {
		logger.info("P -> v");
		logger.info("{0}", matchingResult.p_to_v);
		logger.info("v <- S");
		logger.info("{0}", matchingResult.s_to_v);
		
		logger.info("P <- v");
		
		logger.info("{0}", matchingResult.v_to_p);
		logger.info("v -> S");
		logger.info("{0}", matchingResult.v_to_s);
		
		logger.info("P <-> v");
		logger.info("{0}", matchingResult.p_mergedto_v);
		logger.info("v <-> S");
		logger.info("{0}", matchingResult.s_mergedto_v);
	}
	
	protected void logComplexity(MatchingFinderResult matchingResult, ComplexityCalculationResult result) {
		logger.info("Match at round {0}", matchingResult.bestMatchingRound);
		logger.info("{0} active bytes in matching (P -> v <- S)", matchingResult.minRecomputedOperations);
		logger.info("C_{full} = 2^{n - 2d}(C_{biclique} + C_{precomp} + C_{recomp} + C_{falsepos} + C_{decrypt})");
		logger.info("2^{{0}} \\cdot (2^{{1}} + 2^{{2}} + 2^{{3}} + 2^{{4}} + 2^{{5}}) = 2^{{6}}", new Object[]{
			round(result.numBicliquesLog), 
			round(result.bicliqueComplexityLog), 
			round(result.precomputationsComplexityLog), 
			round(result.recomputationsComplexityLog), 
			round(result.falsePosComplexityLog), 
			round(result.decryptionsComplexityLog), 
			round(result.totalComplexityLog) 
		});
	}
	
	protected String round(double d) {
        return decimalFormat.format(d);
    }
	
	protected void serializeBiclique() throws IOException, JAXBException {
		File file = new File(xmlPath + filename + ".xml");
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		serializer.serialize(biclique, file);
	}
	
}
