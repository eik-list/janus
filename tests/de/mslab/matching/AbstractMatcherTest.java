package de.mslab.matching;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.junit.Test;

import com.itextpdf.text.DocumentException;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.ciphers.helpers.DifferentialActiveComponentsCounter;
import de.mslab.core.Biclique;
import de.mslab.rendering.MatchingPhaseRenderer;
import de.mslab.utils.BicliqueXMLParser;
import de.mslab.utils.Logger;

public abstract class AbstractMatcherTest {
	
	protected Biclique biclique;
	protected RoundBasedBlockCipher cipher;
	protected DifferentialActiveComponentsCounter counter;
	protected Logger logger = Logger.getLogger();
	protected MatchingDifferentialBuilder matchingDifferentialBuilder;
	
	protected MatchingContext matchingContext;
	protected ComplexityCalculator complexityCalculator;
	protected BicliqueXMLParser parser;
	protected MatchingPhaseRenderer renderer;
	protected DecimalFormat decimalFormat;
	
	protected ComplexityCalculationResult complexityCalculationResult;
	protected String pdfPathname;
	protected String xmlPathname;
	
	public AbstractMatcherTest() {
		setUp();
	}
	
	public void setUp() {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(','); 
		decimalFormat = new DecimalFormat("#.##", otherSymbols);
		
		matchingDifferentialBuilder = new MatchingDifferentialBuilder();
		complexityCalculator = new ComplexityCalculator();
		parser = new BicliqueXMLParser();
		renderer = new MatchingPhaseRenderer();
		
		if (matchingContext == null) {
			matchingContext = new MatchingContext(biclique, cipher, counter);
		}
		
		loadAndParseXML(xmlPathname);
		matchingContext.biclique = biclique;
		logger.info("Biclique {0}", biclique);
	}
	
	public void tearDownAfterClass() {
		cipher = null;
		complexityCalculator = null;
		matchingDifferentialBuilder = null;
	}
	
	@Test
	public void testMatch() throws IOException, DocumentException {
		MatchingDifferentialBuilderResult matchingResult = matchingDifferentialBuilder.findMinNumActiveBytes(
			matchingContext
		);
		logMatchingResult(matchingResult);
		
		complexityCalculationResult = complexityCalculator.computeComplexity(
			cipher, 
			matchingResult.dimension, 
			matchingResult.minNumActiveBytes, 
			matchingResult.numBicliqueRounds, 
			matchingResult.matchingToRound - matchingResult.matchingFromRound + 1
		);
		logComplexity(matchingResult, complexityCalculationResult);
		renderMatchingDifferential(matchingResult, pdfPathname);
	}

	protected void logMatchingResult(MatchingDifferentialBuilderResult matchingResult) {
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
	
	protected void loadAndParseXML(String pathname) {
		File file = new File(pathname);
		
		try {
			biclique = parser.parseXML(file);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private void logComplexity(MatchingDifferentialBuilderResult matchingResult, ComplexityCalculationResult result) {
		logger.info("Match at round {0} at byte {1}", matchingResult.bestMatchingRound, matchingResult.bestMatchingByte);
		logger.info("{0} active components in matching (P -> v <- S)", matchingResult.minNumActiveBytes);
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
	
	protected void renderMatchingDifferential(MatchingDifferentialBuilderResult matchingResult, String pathname) throws IOException, DocumentException {
		renderer.renderMatchingPhase(pathname, matchingResult, cipher);
	}
	
	protected String round(double d) {
        return decimalFormat.format(d);
    }
	
}





