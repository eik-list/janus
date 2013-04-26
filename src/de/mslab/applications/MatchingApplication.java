package de.mslab.applications;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.itextpdf.text.DocumentException;

import de.mslab.ciphers.CipherFactory;
import de.mslab.ciphers.CipherFactory.CipherName;
import de.mslab.ciphers.helpers.CipherHelperFactory;
import de.mslab.ciphers.helpers.RecomputedOperationsCounter;
import de.mslab.core.Biclique;
import de.mslab.matching.ComplexityCalculationResult;
import de.mslab.matching.ComplexityCalculator;
import de.mslab.matching.MatchingContext;
import de.mslab.matching.MatchingFinder;
import de.mslab.matching.MatchingFinderResult;
import de.mslab.rendering.MatchingPhaseRenderer;
import de.mslab.utils.BicliqueXMLParser;

/**
 * The application which controls finding an optimal matching for a given biclique.
 */
public class MatchingApplication extends AbstractApplication {
	
	public static void main(String[] args) {
		new MatchingApplication(args);
	}
	
	private Biclique biclique;
	private RecomputedOperationsCounter counter;
	private MatchingFinder matchingDifferentialBuilder;
	private MatchingContext matchingContext;
	
	private ComplexityCalculator complexityCalculator;
	private BicliqueXMLParser parser;
	private MatchingPhaseRenderer renderer;
	private DecimalFormat decimalFormat;
	
	private String pdfPathname;
	private String xmlPathname;
	
	public MatchingApplication(String[] args) {
		super(args);
	}
	
	public void setUp(CommandLine commandLine) {
		CipherName cipherName = CipherFactory.toCipherName(getRequiredOption(commandLine, "c"));
		String xmlPathname = getRequiredOption(commandLine, "i");
		String pdfPathname = getRequiredOption(commandLine, "o"); 
		boolean debug = commandLine.hasOption("debug");
		
		setUp(cipherName, xmlPathname, pdfPathname, debug);
	}
	
	public void setUp(CipherName cipherName, String xmlPathname, String pdfPathname, boolean debug) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(','); 
		decimalFormat = new DecimalFormat("#.##", otherSymbols);
		
		cipher = CipherFactory.createCipher(cipherName);
		complexityCalculator = new ComplexityCalculator();
		counter = CipherHelperFactory.createCipherHelper(cipherName);
		
		matchingDifferentialBuilder = new MatchingFinder();
		parser = new BicliqueXMLParser();
		renderer = new MatchingPhaseRenderer();
		
		this.pdfPathname = pdfPathname;
		this.xmlPathname = xmlPathname;
		loadAndParseXML(this.xmlPathname);
		
		if (matchingContext == null) {
			matchingContext = new MatchingContext(biclique, cipher, counter);
		}
	}
	
	public void run() throws IOException, DocumentException {
		MatchingFinderResult matchingResult = matchingDifferentialBuilder.findOptimalMatching(matchingContext);
		ComplexityCalculationResult result = complexityCalculator.computeComplexity(
			cipher, 
			matchingResult.dimension, 
			matchingResult.minRecomputedOperations, 
			matchingResult.numBicliqueRounds, 
			matchingResult.matchingToRound - matchingResult.matchingFromRound + 1, 
			matchingContext.numMatchingBits
		);
		logComplexity(matchingResult, result);
		renderMatchingDifferential(matchingResult, pdfPathname);
	}
	
	public void tearDown() {
		
	}
	
	protected void createOptions() {
		String cipherNames = getCipherNames();
		options.addOption(createOptionWithArg("c", "Name of the cipher to test. Required. Allowed values for cipherName include (any casing allowed):\n" + cipherNames, true));
		options.addOption(createOptionWithArg("i", "Path to an input XML file, which contains a serialized biclique. Required.", true));
		options.addOption(createOptionWithArg("o", "Path to an output PDF file to render the found matching. Required.", true));
		options.addOption(createOption("debug", "Log debugging information. Optional. Defaults to true."));
	}
	
	private void loadAndParseXML(String pathname) {
		File file = new File(pathname);
		
		try {
			biclique = parser.parseXML(file);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private void logComplexity(MatchingFinderResult matchingResult, ComplexityCalculationResult result) {
		logger.info("Match at round {0}", matchingResult.bestMatchingRound);
		logger.info("{0} active components in matching (P -> v <- S)", matchingResult.minRecomputedOperations);
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
	
	protected void logHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("matchingsearch", options);
	}
	
	private void renderMatchingDifferential(MatchingFinderResult matchingResult, String pathname) throws IOException, DocumentException {
		renderer.renderMatchingPhase(pathname, matchingResult, cipher);
	}
	
	private String round(double d) {
        return decimalFormat.format(d);
    }
	
}
