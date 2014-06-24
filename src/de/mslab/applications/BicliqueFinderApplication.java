package de.mslab.applications;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import de.mslab.bicliquesearch.BicliqueFinder;
import de.mslab.bicliquesearch.BicliqueFinderContext;
import de.mslab.ciphers.CipherFactory;
import de.mslab.ciphers.CipherFactory.CipherName;
import de.mslab.ciphers.helpers.BitwiseDifferentialComparator;
import de.mslab.ciphers.helpers.BytewiseDifferentialComparator;
import de.mslab.ciphers.helpers.CipherHelperFactory;
import de.mslab.ciphers.helpers.NibblewiseDifferentialComparator;
import de.mslab.core.Biclique;
import de.mslab.diffbuilder.BitwiseDifferenceBuilder;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueRenderer;
import de.mslab.rendering.IBicliqueRenderer;
import de.mslab.utils.BicliqueXMLSerializer;

/**
 * The application for biclique search. 
 */
public class BicliqueFinderApplication extends AbstractApplication {
	
	public static void main(String[] args) {
		new BicliqueFinderApplication(args);
	}
	
	private BicliqueFinder finder;
	private BicliqueFinderContext finderContext;
	private int maxBicliqueRounds;
	private IBicliqueRenderer renderer;
	private BicliqueXMLSerializer serializer;
	private String resultPath;
	
	public BicliqueFinderApplication(String[] args) {
		super(args);
	}
	
	public void setUp(CommandLine commandLine) {
		CipherName cipherName = CipherFactory.toCipherName(getRequiredOption(commandLine, "c"));
		int maxBicliqueRounds = Integer.parseInt(getOptionValue(commandLine, "r", "4"));
		int dimension = Integer.parseInt(getOptionValue(commandLine, "d", "8"));
		boolean stopAfterFoundFirstBiclique = commandLine.hasOption("stop");
		boolean debug = commandLine.hasOption("debug");
		String resultPath = getOptionValue(commandLine, "o", "");
		
		setUp(cipherName, dimension, maxBicliqueRounds, stopAfterFoundFirstBiclique, debug, resultPath);
	}
	
	/**
	 * Sets up the application. Creates the <code>cipher</code> from the given name, and creates
	 * a <code>BicliqueFinder</code> and a <code>BicliqueFinderContext</code> from the given parameters.
	 * @param cipherName
	 * @param dimension
	 * @param maxBicliqueRounds
	 * @param stopAfterFoundFirstBiclique
	 * @param debug
	 */
	public void setUp(CipherName cipherName, int dimension, int maxBicliqueRounds, 
		boolean stopAfterFoundFirstBiclique, boolean debug, String resultPath) {
		initializeCipher(cipherName);
		initializeBicliqueFinder(cipherName, dimension, stopAfterFoundFirstBiclique, resultPath);
		renderer = new BicliqueRenderer();
		this.maxBicliqueRounds = maxBicliqueRounds;
		logger.isDebugEnabled = debug;
	}
	
	public void run() {
		findBicliquesAtCipherEnd();
		findBicliquesAtCipherStart();
	}
	
	/**
	 * Tears down the application
	 */
	public void tearDown() {
		finder.tearDown();
		finder = null;
	}
	
	protected void createOptions() {
		String cipherNames = getCipherNames();
		options.addOption(createOptionWithArg("c", "Name of the cipher to test. Allowed values for cipherName include (any casing allowed):\n" + cipherNames, true));
		options.addOption(createOptionWithArg("d", "Dimension of the biclique. Defaults to 8.", false));
		options.addOption(createOptionWithArg("r", "Maximum number of rounds for bicliques.", false));
		options.addOption(createOptionWithArg("o", "Path to an output directory to save serialized bicliques in XML, and their visualizations in PDF format. Defaults to execution directory.", false));
		options.addOption(createOption("stop", "Stops the search on current rounds, if one biclique was found. Defaults to false."));
		options.addOption(createOption("debug", "Log debugging information. Defaults to false."));
	}
	
	protected void logHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("bicliquesearch", options);
	}
	
	private void initializeBicliqueFinder(CipherName cipherName, int dimension, boolean stopAfterFoundFirstBiclique, String resultPath) {
		finderContext = new BicliqueFinderContext();
		finderContext.cipher = cipher;
		finderContext.stopAfterFoundFirstBiclique = stopAfterFoundFirstBiclique;
		finderContext.dimension = dimension;
		finderContext.comparator = CipherHelperFactory.createCipherHelper(cipherName);
		
		if (cipher.operatesNibblewise()) {
			finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
			finderContext.comparator = new NibblewiseDifferentialComparator();
		} else if (cipher.operatesBytewise()) {
			finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
			finderContext.comparator = new BytewiseDifferentialComparator();
		} else {
			finderContext.differenceBuilder = new BitwiseDifferenceBuilder();
			finderContext.comparator = new BitwiseDifferentialComparator();
		}
		
		finder = new BicliqueFinder();
		finder.setContext(finderContext);
		finder.setLogger(logger);
		
		serializer = new BicliqueXMLSerializer();
		
		if (resultPath != null && resultPath.length() > 0) {
			String lastChar = resultPath.substring(resultPath.length() - 1);
			
			if (!lastChar.equals("/")) {
				resultPath += "/";
			}
		}
		
		this.resultPath = resultPath;
	}
	
	/**
	 * Searches for bicliques in the <code>maxBicliqueRounds</code> rounds of the cipher.
	 * This means, that first, bicliques will be searched for the final round, then for 
	 * the two final rounds, etc.     
	 */
	private void findBicliquesAtCipherEnd() {
		int numRounds = cipher.getNumRounds();
		int endRound = numRounds - maxBicliqueRounds + 1;
		
		for (int fromRound = numRounds; fromRound >= endRound; fromRound--) {
			findBicliques(fromRound, numRounds);
		}
	}
	
	/**
	 * Searches for bicliques in the <code>maxBicliqueRounds</code> rounds of the cipher.
	 * This means, that first, bicliques will be searched for the initial round, then for 
	 * the rounds 1 - 2, etc.     
	 */
	private void findBicliquesAtCipherStart() {
		for (int toRound = 1; toRound <= maxBicliqueRounds; toRound++) {
			findBicliques(1, toRound);
		}
	}
	
	private void findBicliques(int fromRound, int toRound) {
		finderContext.fromRound = fromRound;
		finderContext.toRound = toRound;
		finder.findBicliques();
		
		if (finder.getBicliques().size() > 0) {
			logFirstBiclique();
			saveFirstBiclique();
			renderFirstBiclique();
		}
	}
	
	private void logFirstBiclique() {
		Biclique biclique = finder.getBicliques().get(0);
		logger.info("Found {0} biclique(s) for {1} rounds [{2} - {3}]", finder.getBicliques().size(), cipher.getName(), finderContext.fromRound, finderContext.toRound);
		logger.info(biclique);
	}

	private void renderFirstBiclique() {
		File file = new File(resultPath);
		
		if (!file.exists()) {
			file.mkdir();
		}
		
		Biclique biclique = finder.getBicliques().get(0);
		String filename = finderContext.cipher.getName() + "_" + biclique.deltaDifferential.fromRound + "_" + biclique.deltaDifferential.toRound + ".pdf";
		
		try {
			renderer.renderBiclique(resultPath + filename, biclique, finderContext.cipher);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private void saveFirstBiclique() {
		File file = new File(resultPath);
		
		if (!file.exists()) {
			file.mkdir();
		}
		
		Biclique biclique = finder.getBicliques().get(0);
		String filename = cipher.getName() + "_" + biclique.deltaDifferential.fromRound + "_" + biclique.deltaDifferential.toRound + ".xml";
		file = new File(resultPath + filename);
		
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			
			serializer.serialize(biclique, file);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
}
