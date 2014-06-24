package de.mslab.ciphers.helpers;

import de.mslab.ciphers.PRESENT;
import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.ByteArray;
import de.mslab.core.Difference;
import de.mslab.core.Differential;
import de.mslab.utils.Logger;

public class PRESENTDifferentialCleaner {
	
	public RoundBasedBlockCipher cipher;
	public Logger logger = Logger.getLogger();
	
	public void cleanForwardDifferential(Differential differential, RoundBasedBlockCipher cipher) {
		cleanForwardDifferential(differential, differential.fromRound, differential.toRound, cipher);
	}	

	public void cleanBackwardDifferential(Differential differential, RoundBasedBlockCipher cipher) {
		cleanBackwardDifferential(differential, differential.fromRound, differential.toRound, cipher);
	}
	
	public void cleanForwardDifferential(Differential differential, int fromRound, int toRound, RoundBasedBlockCipher cipher) {
		this.cipher = cipher;
		
		for (int round = fromRound; round <= toRound; round++) {
			cleanForwardIntermediateStateDifference(
				differential.getStateDifference(round - 1), 
				differential.getIntermediateStateDifference(round)
			);
			cleanForwardStateDifference(
				differential.getStateDifference(round), 
				differential.getIntermediateStateDifference(round)
			);
		}
		
		if (toRound == cipher.getNumRounds()) {
			cleanForwardStateDifference(
				differential.getIntermediateStateDifference(toRound + 1), 
				differential.getIntermediateStateDifference(toRound)
			);
		}
		
		this.cipher = null;
	}
	
	public void cleanBackwardDifferential(Differential differential, int fromRound, int toRound, RoundBasedBlockCipher cipher) {
		this.cipher = cipher;
		
		if (toRound == cipher.getNumRounds()) {
			cleanBackwardIntermediateStateDifference(
				differential.getIntermediateStateDifference(toRound + 1), 
				differential.getIntermediateStateDifference(toRound)
			);
		}
		
		for (int round = toRound; round >= fromRound; round--) {
			cleanBackwardIntermediateStateDifference(
				differential.getStateDifference(round), 
				differential.getIntermediateStateDifference(round)
			);
			cleanBackwardStateDifference(
				differential.getStateDifference(round - 1), 
				differential.getIntermediateStateDifference(round)
			);
		}
		
		this.cipher = null;
	}
	
	private void cleanBackwardIntermediateStateDifference(Difference stateDifference,
		Difference intermediateStateDifference) {
		final ByteArray state = stateDifference.getDelta();
		final ByteArray intermediateState = intermediateStateDifference.getDelta();
		final int numBits = cipher.getStateSize() * Byte.SIZE;
		
		for (int i = 0, bit = 0; i < numBits; i++) {
			if (state.getBit(i)) {
				bit = PRESENT.PERMUTATION[i];
				intermediateState.setNibble(bit / 4, 0xF); 
			}
		}
	}
	
	private void cleanBackwardStateDifference(Difference stateDifference, Difference intermediateStateDifference) {
		final ByteArray state = stateDifference.getDelta();
		final ByteArray intermediateState = intermediateStateDifference.getDelta();
		final int numBits = cipher.getStateSize() * Byte.SIZE;
		
		for (int i = 0; i < numBits; i++) {
			if (intermediateState.getBit(i)) {
				state.setBit(i, true); 
			}
		}
	}
	
	private void cleanForwardIntermediateStateDifference(Difference stateDifference, 
		Difference intermediateStateDifference) {
		final ByteArray state = stateDifference.getDelta();
		final ByteArray intermediateState = intermediateStateDifference.getDelta();
		final int numBits = cipher.getStateSize() * Byte.SIZE;
		
		for (int i = 0; i < numBits; i++) {
			if (state.getBit(i)) {
				intermediateState.setBit(i, true);
			}
		}
	}
	
	private void cleanForwardStateDifference(Difference stateDifference, Difference intermediateStateDifference) {
		final ByteArray state = stateDifference.getDelta();
		final ByteArray intermediateState = intermediateStateDifference.getDelta();
		final int numNibbles = 2 * cipher.getStateSize();
		
		for (int i = 0, bit = 0; i < numNibbles; i++) {
			bit = 4 * i;
			
			if (intermediateState.getNibble(i) != 0) {
				state.setBit(PRESENT.INVERSE_PERMUTATION[bit++], true);
				state.setBit(PRESENT.INVERSE_PERMUTATION[bit++], true);
				state.setBit(PRESENT.INVERSE_PERMUTATION[bit++], true);
				state.setBit(PRESENT.INVERSE_PERMUTATION[bit++], true);
			}
		}
	}
	
}
