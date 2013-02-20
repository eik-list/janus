package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;

public class PRESENT128Helper extends PRESENTHelper {
	
	protected int countActiveKeyNibblesInSBox(ByteArray keyDifference) {
		int sum = 0;
		
		if (keyDifference.getNibble(0) != 0) {
			sum++;
		}
		
		if (keyDifference.getNibble(1) != 0) {
			sum++;
		}
		
		return sum;
	}
	
}
