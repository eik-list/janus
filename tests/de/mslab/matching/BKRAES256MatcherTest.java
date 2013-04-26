package de.mslab.matching;

import de.mslab.ciphers.AES256;
import de.mslab.ciphers.helpers.AES256Helper;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.diffbuilder.BKRDifferentialBuilder;

public class BKRAES256MatcherTest extends AbstractBKRMatcherTest { 
	
	public BKRAES256MatcherTest() {
		setUp();
	}
	
	public void setUp() {
		filename = "BKRAES256_11_14";
		cipher = new AES256();
		counter = new AES256Helper();
		
		numActiveBytesExpected = 101;
		complexityExpected = 254.584;
		
		matchingRound = 3;
		matchingStateDifference = new ByteArray(new int[]{0,0,0,0, 0,0,0,0, 0,0,0,0, 0xff,0,0,0});
		
		super.setUp();
	}
	
	protected void createBiclique() {
		ByteArray deltaKey = new ByteArray(new int[]{0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,1,0, 0,0,0,0, 0,0,0,0, 0,0,0,0});
		ByteArray nablaKey = new ByteArray(new int[]{0,0,0,0, 0,1,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0});
		ByteArray emptyState = new ByteArray(cipher.getKeySize());
		ByteArray initialKey = new ByteArray(cipher.getKeySize());
		cipher.setKey(initialKey);
		initialKey = cipher.getExpandedKey();
		
		emptyState = new ByteArray(cipher.getStateSize());
		int fromRound = 11;
		int toRound = 14;
		
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
	}
	
}


