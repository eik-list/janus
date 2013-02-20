package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;

public class AES128Helper extends AESHelper {
	
	protected int countActiveKeyBytes(int round, ByteArray key) {
		int sum = 0;
		
		for (int i = 12; i < 16; i++) {
			if (key.get(i) != 0) {
				sum++;
			}
		}
		
		return sum;
	}
	
}
