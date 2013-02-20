package de.mslab.bicliquesearch;

import java.util.List;

import de.mslab.ciphers.KLEIN96;
import de.mslab.ciphers.helpers.KLEIN96Helper;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.diffbuilder.NibblewiseDifferenceBuilder;
import de.mslab.rendering.BicliqueDifferentialRenderer;
import de.mslab.rendering.LEDStateRenderer;

public class KLEIN96BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		finderContext.cipher = new KLEIN96();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new NibblewiseDifferenceBuilder();
		finderContext.comparator = new KLEIN96Helper();
		
		maxNumBicliqueRounds = 5;
		
		BicliqueDifferentialRenderer differentialRenderer = new BicliqueDifferentialRenderer();
		differentialRenderer.setStateRenderer(new LEDStateRenderer(10));
		renderer.setDifferentialRenderer(differentialRenderer);
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
			ByteArray plainText = biclique.nablaDifferential.getStateDifference(20).getDelta();
			return 4 * plainText.countNumActiveNibbles();
		}
	}
	
}
