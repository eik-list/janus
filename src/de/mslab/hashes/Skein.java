package de.mslab.hashes;

import de.mslab.ciphers.ThreeFish;
import de.mslab.core.ByteArray;

/**
 * The hash function Skein.
 * 
 */
abstract class Skein extends AbstractCipherBasedHashFunction {
	
	protected ThreeFish cipher;
	protected long[] initialValue; 
	
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
	
	protected ByteArray applyPadding(ByteArray message) {
		return message;
	}
	
	protected ByteArray processBlock(ByteArray messageBlock, ByteArray chainingValue) {
		ByteArray message = messageBlock.clone();
		ByteArray result = applyCompressionFunction(message, chainingValue);
		result.xor(messageBlock);
		return result;
	}
	
}
