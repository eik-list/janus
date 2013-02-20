package de.mslab.bicliquesearch;

import java.util.List;

import org.junit.Test;

import de.mslab.ciphers.PRESENT80;
import de.mslab.ciphers.helpers.PRESENTHelper;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.diffbuilder.PRESENTNibblewiseDifferenceBuilder;

public class PRESENT80BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new PRESENT80();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new PRESENTNibblewiseDifferenceBuilder();
		finderContext.comparator = new PRESENTHelper();
		
		maxNumBicliqueRounds = 5;
	}
	
	@Test
	public void testFindBicliques() {
		
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		find(1, 4);
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
		
		return minBiclique;
	}
	
	private int getDataComplexity(Biclique biclique) {
		ByteArray plainText = biclique.nablaDifferential.getStateDifference(0).getDelta();
		return 4 * plainText.countNumActiveNibbles();
	}
	
}
