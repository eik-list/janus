package de.mslab.ciphers.helpers;

import de.mslab.ciphers.KLEIN96;
import de.mslab.core.ByteArray;

public class KLEIN96Helper extends KLEINHelper {
	
	public KLEIN96Helper() {
		numRounds = KLEIN96.NUM_ROUNDS;
	}

	protected int countActiveKeyNibblesInSBox(ByteArray keyDifference) {
		int halfLengthNibble = KLEIN96.NUM_BYTES_IN_96_BIT;
		int sum = 0;
		
		if (keyDifference.getNibble(halfLengthNibble + 2) != 0) {
			sum++;
		}
		if (keyDifference.getNibble(halfLengthNibble + 3) != 0) {
			sum++;
		}
		// not included in the round key differences
		/*if (keyDifference.getNibble(halfLengthNibble + 4) != 0) {
			sum++;
		}
		if (keyDifference.getNibble(halfLengthNibble + 5) != 0) {
			sum++;
		}*/
		
		return sum;
	}
	
}
