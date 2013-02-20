package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;

public class AES192Helper extends AESHelper {
	
	protected int countActiveKeyBytes(int round, ByteArray key) {
		int sum = 0;
		
		if (round % 3 == 1) { // col 1 in s-box
			for (int i = 4; i < 8; i++) {
				if (key.get(i) != 0) {
					sum++;
				}
			}
		} else if (round % 3 == 2) { // col 3 in s-box
			for (int i = 12; i < 16; i++) {
				if (key.get(i) != 0) {
					sum++;
				}
			}
		}
		
		return sum;
	}
	
}

