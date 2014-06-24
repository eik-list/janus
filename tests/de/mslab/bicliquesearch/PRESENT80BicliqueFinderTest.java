package de.mslab.bicliquesearch;

import org.junit.Test;

import de.mslab.bicliquesearch.helpers.PRESENTBicliqueRater;
import de.mslab.ciphers.PRESENT80;
import de.mslab.ciphers.helpers.PRESENTDifferentialCleaner;
import de.mslab.ciphers.helpers.PRESENTHelper;
import de.mslab.core.Biclique;
import de.mslab.diffbuilder.BitwiseDifferenceBuilder;
import de.mslab.rendering.BicliqueAllInOneRenderer;
import de.mslab.rendering.PRESENTDifferentialRenderer;

public class PRESENT80BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	PRESENTDifferentialCleaner cleaner;
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new PRESENT80();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 7;
		finderContext.differenceBuilder = new BitwiseDifferenceBuilder();
		finderContext.comparator = new PRESENTHelper();
		finderContext.bicliqueRater = new PRESENTBicliqueRater();

		renderer = new BicliqueAllInOneRenderer();
		renderer.setDifferentialRenderer(new PRESENTDifferentialRenderer());
		
		cleaner = new PRESENTDifferentialCleaner();
	}
	
	@Test
	public void testFindBicliques() {
		find(29, 31);
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		
	}
	
	protected void find(int fromRound, int toRound) {
		finderContext.fromRound = fromRound;
		finderContext.toRound = toRound;
		finder.findBicliques();
		logger.info("Found {0} biclique(s) for {1} rounds [{2} - {3}]", finder.getBicliques().size(), finderContext.cipher.getName(), finderContext.fromRound, finderContext.toRound);
		
		if (finder.getBicliques().size() > 0) {
			Biclique biclique = finder.getBicliques().get(0);
			
			cleaner.cleanForwardDifferential(
				biclique.deltaDifferential, finderContext.cipher
			);
			cleaner.cleanBackwardDifferential(
				biclique.nablaDifferential, finderContext.cipher
			);
			logBiclique(biclique);
			saveBiclique(biclique);
			renderBiclique(biclique);
		}
		
		tearDown();
	}
	
}
