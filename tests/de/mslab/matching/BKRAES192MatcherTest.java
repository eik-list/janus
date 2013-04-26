package de.mslab.matching;

import de.mslab.ciphers.AES192;
import de.mslab.ciphers.helpers.AES192Helper;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.diffbuilder.BKRDifferentialBuilder;

public class BKRAES192MatcherTest extends AbstractBKRMatcherTest { 
	
	public BKRAES192MatcherTest() {
		setUp();
	}
	
	public void setUp() {
		filename = "BKRAES192_9_12";
		cipher = new AES192();
		counter = new AES192Helper();
		
		numActiveBytesExpected = 61;
		complexityExpected = 190.191;
		
		matchingRound = 3;
		matchingStateDifference = new ByteArray(new int[]{0,0,0,0, 0,0,0,0, 0,0,0,0, 0xff,0,0,0});
		
		mixColumns();
		super.setUp();
	}
	
	protected void createBiclique() {
		ByteArray deltaKey = new ByteArray(new int[]{
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,1,38,0, 0,0,0,0
		});
		ByteArray nablaKey = new ByteArray(new int[]{
			0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,37, 0,0,0,0
		});
		ByteArray emptyState = new ByteArray(cipher.getStateSize());
		ByteArray initialKey = new ByteArray(cipher.getKeySize());
		cipher.setKey(initialKey);
		initialKey = cipher.getExpandedKey();
		
		int fromRound = 9;
		int toRound = 12;
		
		biclique = new Biclique();
		biclique.dimension = 8;
		differentialBuilder = new BKRDifferentialBuilder();
		differentialBuilder.cipher = cipher;
		biclique.deltaDifferential = differentialBuilder.computeForwardDifferentialFromRoundKeys(
			fromRound, toRound, emptyState.clone(), deltaKey, initialKey
		);
		biclique.nablaDifferential = differentialBuilder.computeBackwardDifferentialFromRoundKeys(
			fromRound, toRound, emptyState, nablaKey, initialKey
		);
		
		logger.info("biclique = {0}", biclique);
	}
	
	private void mixColumns() {
		ByteArray state = new ByteArray(cipher.getStateSize());
		ByteArray output;
		AESMixColumnsHelper aes = new AESMixColumnsHelper();
		
		for (int i = 0; i < state.length(); i++) {
			state.set(i, 0);
		}
		
		outer: for (int i1 = 1; i1 < 256; i1++) {
			for (int i2 = 0; i2 < 256; i2++) {
				state.set(1, i1);
				state.set(2, i2);
				output = aes.invertTheMixColumns(state.clone());
				
				if (output.get(3) == 0) {
					// logger.info("output = {0} i1 = {1} i2 = {2}", output, i1, i2);
					break outer;
				}
			}
		}
	}
	
}

class AESMixColumnsHelper extends AES192 {
	
	public ByteArray invertTheMixColumns(ByteArray state) {
		return invertMixColumns(state);
	}
	
}
