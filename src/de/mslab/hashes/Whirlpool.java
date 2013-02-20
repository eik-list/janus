package de.mslab.hashes;

import de.mslab.ciphers.WhirlpoolCipher;
import de.mslab.core.ByteArray;

/**
 * The WHIRLPOOL hash function.
 * 
 */
public class Whirlpool extends AbstractCipherBasedHashFunction {
	
	private static final int NUM_BYTES_IN_512_BIT = 512 / Byte.SIZE;
	private static final ByteArray INITIAL_VALUE = new ByteArray(NUM_BYTES_IN_512_BIT);
	
	private WhirlpoolCipher cipher;
	
	public Whirlpool() {
		this.cipher = new WhirlpoolCipher();
		this.blockSize = NUM_BYTES_IN_512_BIT;
		this.digestSize = NUM_BYTES_IN_512_BIT;
		this.initialValue = INITIAL_VALUE;
	}
	
	public ByteArray processMessage(ByteArray message, ByteArray chainingValue) {
		message = applyPadding(message);
		lastChainingValue = chainingValue;
		long numMessageBlocks = message.length() / blockSize;
		
		for (int i = 0; i < numMessageBlocks; i++) {
			lastMessageBlock = message.splice(i * blockSize, (i + 1) * blockSize);
			lastChainingValue = processBlock(lastMessageBlock, lastChainingValue);
		}
		
		return lastChainingValue;
	}
	
	protected ByteArray applyCompressionFunction(ByteArray message, ByteArray chainingValue) {
		cipher.setKey(chainingValue);
		return cipher.encrypt(message);
	}
	
	protected ByteArray processBlock(ByteArray messageBlock, ByteArray chainingValue) {
		ByteArray message = messageBlock.clone();
		message.xor(chainingValue);
		ByteArray result = applyCompressionFunction(message, chainingValue);
		
		result.xor(chainingValue);
		result.xor(messageBlock);
		return result;
	}
	
	/**
	 * Before being subjected to the hashing operation, a message M of bit length
	 * L < 2^{256} is padded with a 1-bit, then with as few 0-bits as necessary to obtain
	 * a bit string whose length is an odd multiple of 256, and finally with the 256-bit
	 * right-justified binary representation of L, resulting in the padded message m.
	 */
	protected ByteArray applyPadding(ByteArray message) {
		int numBytes = message.length();
		int numZeroBytes;
		
		if (numBytes < 32) {
			numZeroBytes = 32 - numBytes;
		} else if (numBytes % NUM_BYTES_IN_512_BIT >= 32) {
			numZeroBytes = 64 - ((numBytes - 32) % NUM_BYTES_IN_512_BIT);
		} else {
			numZeroBytes = 32 - ((numBytes - 32) % NUM_BYTES_IN_512_BIT);
		}
		
		int numAdditionalBytes = numZeroBytes + 32;
		short[] array = new short[numAdditionalBytes];
		array[0] = 0x80;
		
		for (int i = 1; i < numZeroBytes; i++) {
			array[i] = 0;
		}
		
		int numBits = numBytes << 3; 
		
		for (int i = 0; i < 4; i++) {
			int leastSignificantLengthByte = numBits & 0xFF;
			numBits >>= 8;
			array[array.length - 1 - i] = (short)leastSignificantLengthByte;
		}
		
		message.concat(new ByteArray(array));
		return message;
	}
	
}
