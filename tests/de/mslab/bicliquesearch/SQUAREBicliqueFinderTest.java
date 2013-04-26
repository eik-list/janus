package de.mslab.bicliquesearch;

import org.junit.Test;

import de.mslab.ciphers.SQUARE;
import de.mslab.ciphers.helpers.SQUAREHelper;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class SQUAREBicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new SQUARE();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new SQUAREHelper();
		
		maxNumBicliqueRounds = 4;
	}
	
	@Test
	public void testFindBicliques() {
		find(7, 8);
	}
	
	@Test
	public void testFindBicliquesAtStart() {
		
	}
	
	protected int determineScoreForBiclique(Biclique biclique) {
		return determineDataComplexity(biclique)
			+ determineFoo(biclique.nablaDifferential);
	}
	
	private int determineFoo(Differential nabla) {
		if (hasActiveByteInFirstRow(nabla.keyDifferences.get(nabla.toRound).getDelta())) {
			return 1000;
		} else {
			return 0;
		}
	}

	private boolean hasActiveByteInFirstRow(ByteArray key) {
		for (int i = 0; i < 4; i++) {
			if (key.get(i) != 0) {
				return true;
			}
		}
		
		return false;
	}
	
}
