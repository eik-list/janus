package de.mslab.bicliquesearch;

import java.util.List;

import de.mslab.ciphers.WhirlpoolCipher;
import de.mslab.ciphers.helpers.WhirlpoolCipherHelper;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class WhirlpoolBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new WhirlpoolCipher();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new WhirlpoolCipherHelper();
		
		maxNumBicliqueRounds = 4;
	}
	
	protected void find(int fromRound, int toRound) {
		finderContext.fromRound = fromRound;
		finderContext.toRound = toRound;
		finder.findBicliques();
		
		if (finder.getBicliques().size() > 0) {
			Biclique biclique = determineBicliqueWithMinimumDataComplexity(finder.getBicliques());
			
			logBiclique(biclique);
			saveBiclique(biclique);
			renderBiclique(biclique);
		}
		
		tearDown();
	}
	
	private Biclique determineBicliqueWithMinimumDataComplexity(List<Biclique> bicliques) {
		final int numBicliques = bicliques.size();
		int dataComplexity;
		int minDataComplexity = Integer.MAX_VALUE;
		Biclique biclique;
		Biclique minBiclique = null;
		
		for (int i = 0; i < numBicliques; i++) {
			biclique = bicliques.get(i);
			dataComplexity = getDataComplexity(biclique);
			
			if (i == 0 || dataComplexity < minDataComplexity) {
				minDataComplexity = dataComplexity;
				minBiclique = biclique;
			}
		}
		
		logger.info("min data {0}", minDataComplexity);
		return minBiclique;
	}
	
	private int getDataComplexity(Biclique biclique) {
		if (biclique.deltaDifferential.fromRound == 1) {
			ByteArray plainText = biclique.nablaDifferential.getStateDifference(0).getDelta();
			return 4 * plainText.countNumActiveNibbles();
		} else {
			final int numRounds = finderContext.cipher.getNumRounds();
			ByteArray plainText = biclique.nablaDifferential.getStateDifference(numRounds).getDelta();
			return 4 * plainText.countNumActiveNibbles();
		}
	}
	
}
