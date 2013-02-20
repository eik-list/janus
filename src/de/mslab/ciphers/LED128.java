package de.mslab.ciphers;

import de.mslab.core.ByteArray;

public class LED128 extends LED {
	
	private ByteArray firstKey;
	private ByteArray firstInternalKey;
	private ByteArray secondKey;
	private ByteArray secondInternalKey;
	private int numRoundsPerFirstKeyInjection;
	
	public LED128() {
		super();
		stateSize = 8;
		keySize = 16;
		numRounds = 48;
		name = "LED128";
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
		firstKey = secretKey.splice(0, 8);
		secondKey = secretKey.splice(8, 16);
		internalSecretKey = twoNibblesPerByteToSingleNibblePerByte(expandedKey);
		firstInternalKey = internalSecretKey.splice(0, 16);
		secondInternalKey = internalSecretKey.splice(16, 32);
	}
	
}
