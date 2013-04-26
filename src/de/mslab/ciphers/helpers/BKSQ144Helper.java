package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;


public class BKSQ144Helper extends BKSQHelper {

	protected int countActiveKeyBytes(int round, ByteArray key, 
		ByteArray stateBeforeKeyAddition, ByteArray stateAfterKeyAddition) {
		int sum = 0;
		
		if (round % 3 == 1) { // col 1 in s-box
			for (int i = 3; i < 6; i++) {
				if (key.get(i) != 0
					&& (stateBeforeKeyAddition.get(i) != 0 || stateAfterKeyAddition.get(i) != 0)) {
					sum++;
				}
			}
		} else if (round % 3 == 2) { // col 3 in s-box
			for (int i = 9; i < 12; i++) {
				if (key.get(i) != 0
					&& (stateBeforeKeyAddition.get(i) != 0 || stateAfterKeyAddition.get(i) != 0)) {
					sum++;
				}
			}
		}
		
		return sum;
	}
	
}
