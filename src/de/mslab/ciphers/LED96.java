package de.mslab.ciphers;

import de.mslab.core.ByteArray;

public class LED96 extends LED {
	
	private static final int NUM_BYTES_IN_96_BIT = 96 / Byte.SIZE;
	private static final int NUM_ROUNDS = 48;
	
	private ByteArray firstKey;
	private ByteArray firstInternalKey;
	private ByteArray secondKey;
	private ByteArray secondInternalKey;
	private int numRoundsPerFirstKeyInjection;
	
	public LED96() {
		super();
		stateSize = NUM_BYTES_IN_64_BIT;
		keySize = NUM_BYTES_IN_96_BIT;
		numRounds = NUM_ROUNDS;
		name = "LED96";
		numRoundsPerFirstKeyInjection = 2 * numRoundsPerKeyInjection;
	}
	
	public ByteArray getRoundKey(int round) {
		if (round % numRoundsPerFirstKeyInjection == 0) {
			return firstKey;
		} else {
			return secondKey;
		}
	}
	
	protected ByteArray internalGetRoundKey(int round) {
		if (round % numRoundsPerFirstKeyInjection == 0) {
			return firstInternalKey;
		} else {
			return secondInternalKey;
		}
	}
	
	public void setExpandedKey(ByteArray expandedKey) {
		checkKeySize(expandedKey.length());
		secretKey = expandedKey.clone();
		secretKey.concat(new ByteArray(4));
		firstKey = secretKey.splice(0, 8);
		secondKey = secretKey.splice(8, 16);
		secretKey = secretKey.splice(0, 12);
		internalSecretKey = twoNibblesPerByteToSingleNibblePerByte(expandedKey);
		firstInternalKey = twoNibblesPerByteToSingleNibblePerByte(firstKey);
		secondInternalKey = twoNibblesPerByteToSingleNibblePerByte(secondKey);
	}
	
}
