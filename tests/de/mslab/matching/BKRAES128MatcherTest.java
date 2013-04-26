package de.mslab.matching;

import de.mslab.ciphers.AES128;
import de.mslab.ciphers.helpers.AES128Helper;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.diffbuilder.BKRDifferentialBuilder;

public class BKRAES128MatcherTest extends AbstractBKRMatcherTest { 
	
	public BKRAES128MatcherTest() {
		setUp();
	}
	
	public void setUp() {
		filename = "BKRAES128_8_10";
		cipher = new AES128();
		counter = new AES128Helper();
		
		numActiveBytesExpected = 54;
		complexityExpected = 126.178316;
		
		matchingRound = 2;
		matchingStateDifference = new ByteArray(new int[]{0,0,0,0, 0,0,0,0, 0,0,0,0, 0xff,0,0,0});
		super.setUp();
	}
	
	protected void createBiclique() {
		ByteArray deltaKey = new ByteArray(new int[]{0,0,0,0, 0,0,0,0, 37,0,0,0, 37,0,0,0});
		ByteArray nablaKey = new ByteArray(new int[]{0,33,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0});
		ByteArray emptyState = new ByteArray(cipher.getStateSize());
		ByteArray initialKey = new ByteArray(cipher.getKeySize());
		cipher.setKey(initialKey);
		initialKey = cipher.getExpandedKey();
		
		int fromRound = 8;
		int toRound = 10;
		
		biclique = new Biclique();
		biclique.dimension = 8;
		differentialBuilder = new BKRDifferentialBuilder();
		differentialBuilder.cipher = cipher;
		biclique.deltaDifferential = differentialBuilder.computeForwardDifferentialFromRoundKeys(
			fromRound, toRound, emptyState.clone(), deltaKey, initialKey
		);
		biclique.nablaDifferential = differentialBuilder.computeBackwardDifferentialFromRoundKeys(
			fromRound, toRound, emptyState.clone(), nablaKey, initialKey
		);
		
		logger.info("biclique = {0}", biclique);
	}
	
}
