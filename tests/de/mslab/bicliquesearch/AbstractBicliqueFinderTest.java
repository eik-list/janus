package de.mslab.bicliquesearch;

import java.io.File;

import org.junit.Test;

import de.mslab.ciphers.helpers.BytewiseDifferentialComparator;
import de.mslab.core.Biclique;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueRenderer;
import de.mslab.rendering.IBicliqueRenderer;
import de.mslab.utils.BicliqueXMLSerializer;
import de.mslab.utils.Logger;

public abstract class AbstractBicliqueFinderTest {
	
	protected BicliqueFinder finder = new BicliqueFinder();
	protected BicliqueFinderContext finderContext = new BicliqueFinderContext();
	protected Logger logger = Logger.getLogger();
	protected int maxNumBicliqueRounds = 4;
	
	protected BicliqueXMLSerializer serializer = new BicliqueXMLSerializer();
	protected IBicliqueRenderer renderer = new BicliqueRenderer();
	protected String xmlPath = "results/xml/";
	protected String pdfPath = "results/biclique/";
	
	public AbstractBicliqueFinderTest() {
		setUp();
	}
	
	public void setUp() {
		finderContext = new BicliqueFinderContext();
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new BytewiseDifferentialComparator();
		
		finder = new BicliqueFinder();
		finder.setContext(finderContext);
	}
	
	@Test
	public void testFindBicliques() {
		int numRounds = finderContext.cipher.getNumRounds();
		int endRound = numRounds - maxNumBicliqueRounds + 1;
		
		if (endRound < 1) {
			endRound = 1;
		}
		
		for (int fromRound = numRounds; fromRound >= endRound; fromRound--) {
			find(fromRound, numRounds);
		}
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		int numRounds = finderContext.cipher.getNumRounds();
		int endRound = maxNumBicliqueRounds;
		
		if (endRound > numRounds) {
			endRound = numRounds;
		}
		
		for (int toRound = 1; toRound <= endRound; toRound++) {
			find(1, toRound);
		}
	}
	
	protected void find(int fromRound, int toRound) {
		finderContext.fromRound = fromRound;
		finderContext.toRound = toRound;
		finder.findBicliques();
		logger.info("Found {0} biclique(s) for {1} rounds [{2} - {3}]", finder.getBicliques().size(), finderContext.cipher.getName(), finderContext.fromRound, finderContext.toRound);
		
		if (finder.getBicliques().size() > 0) {
			Biclique biclique = finder.getBicliques().get(0);
			
			logBiclique(biclique);
			saveBiclique(biclique);
			renderBiclique(biclique);
		}
		
		tearDown();
	}
	
	protected void logBiclique(Biclique biclique) {
		logger.info(biclique);
	}
	
	protected void renderBiclique(Biclique biclique) {
		File file = new File(pdfPath);
		
		if (!file.exists()) {
			file.mkdir();
		}
		
		String filename = finderContext.cipher.getName() + "_" + biclique.deltaDifferential.fromRound + "_" + biclique.deltaDifferential.toRound + ".pdf";
		
		try {
			renderer.renderBiclique(pdfPath + filename, biclique, finderContext.cipher);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	protected void saveBiclique(Biclique biclique) {
		File file = new File(xmlPath);
		
		if (!file.exists()) {
			file.mkdir();
		}
		
		String filename = finderContext.cipher.getName() + "_" + biclique.deltaDifferential.fromRound + "_" + biclique.deltaDifferential.toRound + ".xml";
		file = new File(xmlPath + filename);
		
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			
			serializer.serialize(biclique, file);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	protected void tearDown() {
		finder.tearDown();
	}
	
}
