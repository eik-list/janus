package de.mslab.ciphers.helpers;

import de.mslab.ciphers.KLEIN80;
import de.mslab.core.ByteArray;

public class KLEIN80Helper extends KLEINHelper {
	
	public KLEIN80Helper() {
		numRounds = KLEIN80.NUM_ROUNDS;
	}
	
	protected int countActiveKeyNibblesInSBox(ByteArray keyDifference) {
		int halfLengthNibble = KLEIN80.NUM_BYTES_IN_80_BIT;
		int sum = 0;
		
		if (keyDifference.getNibble(halfLengthNibble + 2) != 0) {
			sum++;
		}
		if (keyDifference.getNibble(halfLengthNibble + 3) != 0) {
			sum++;
		}
		if (keyDifference.getNibble(halfLengthNibble + 4) != 0) {
			sum++;
		}
		if (keyDifference.getNibble(halfLengthNibble + 5) != 0) {
			sum++;
		}
		
		return sum;
	}
	
}
