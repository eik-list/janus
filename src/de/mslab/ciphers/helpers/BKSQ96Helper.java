package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;


public class BKSQ96Helper extends BKSQHelper {
	
	protected int countActiveKeyBytes(int round, ByteArray key, 
		ByteArray stateBeforeKeyAddition, ByteArray stateAfterKeyAddition) {
		int sum = 0;
		
		for (int i = 9; i < 12; i++) {
			if (key.get(i) != 0
				&& (stateBeforeKeyAddition.get(i) != 0 || stateAfterKeyAddition.get(i) != 0)) {
				sum++;
			}
		}
		
		return sum;
	}
	
}
