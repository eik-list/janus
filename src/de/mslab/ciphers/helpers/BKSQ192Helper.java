package de.mslab.ciphers.helpers;

import de.mslab.core.ByteArray;


public class BKSQ192Helper extends BKSQHelper {
	
	protected int countActiveKeyBytes(int round, ByteArray key) {
		int sum = 0;
		
		if (round % 2 == 1) {
			for (int i = 9; i < 12; i++) {
				if (key.get(i) != 0) {
					sum++;
				}
			}
		}
		
		return sum;
	}
	
}
