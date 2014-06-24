package de.mslab.bicliquesearch;

import de.mslab.ciphers.ThreeFish512;
import de.mslab.ciphers.helpers.ThreeFishHelper;
import de.mslab.core.ByteArray;
import de.mslab.diffbuilder.BitwiseDifferenceBuilder;
import de.mslab.diffbuilder.DifferenceBuilder;

public class ThreeFish512BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		ThreeFish512 cipher = new ThreeFish512();
		cipher.setTweak(new ByteArray(cipher.getTweakSize()));
		
		finderContext.cipher = cipher;
		finderContext.stopAfterFoundFirstBiclique = true;
		finderContext.dimension = 8;

		DifferenceBuilder differenceBuilder = new BitwiseDifferenceBuilder();
		finderContext.differenceBuilder = differenceBuilder;
		
		finderContext.comparator = new ThreeFishHelper();
		finderContext.logInterval = 1000;
		
		maxNumBicliqueRounds = 8;
	}
	/*
	@Test
	public void testFindBicliques() {
		int toRound = finderContext.cipher.getNumRounds();
		int fromRound = toRound - maxNumBicliqueRounds + 1;
		int maxNumDOF = 370;
		int numDOF;
		
		while(true) {
			finderContext.fromRound = fromRound;
			finderContext.toRound = toRound;
			finder.findBicliques();
			logFirstBiclique();
			
			if (finder.getBicliques().size() > 0) {
				numDOF = countBits(finder.getBicliques().get(0));
				
				if (numDOF <= maxNumDOF) {
					saveFirstBiclique();
					renderFirstBiclique();
					tearDown();
					break;
				}
				
				tearDown();
			}
		}
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		
	}
	
	protected int countBits(Biclique biclique) {
		Differential delta = biclique.deltaDifferential;
		Differential nabla = biclique.nablaDifferential;
		ByteArray state;
		int numDeltaBits, numNablaBits;
		int numTotalDeltaBits = 0;
		int numTotalNablaBits = 0;
		
		for (int round = delta.fromRound - 1; round <= delta.toRound; round++) {
			state = delta.getStateDifference(round).getDelta();
			numDeltaBits = MathUtil.getHammingWeightForValue(state);
			numTotalDeltaBits += numDeltaBits;
			
			state = nabla.getStateDifference(round).getDelta();
			numNablaBits = MathUtil.getHammingWeightForValue(state);
			numTotalNablaBits += numNablaBits;
			logger.info(
				"round {0} delta {1} {2} nabla {3} {4}", 
				new Object[]{round, numDeltaBits, numTotalDeltaBits, numNablaBits, numTotalNablaBits}
			);
		}
		
		return numTotalDeltaBits + numTotalNablaBits;
	}
	*/
}
