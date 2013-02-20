package de.mslab.hashes;

import de.mslab.core.ByteArray;

/**
 * Base class for hash functions.
 * 
 */
abstract class AbstractHashFunction implements HashFunction {
	
	protected int blockSize;
	protected int digestSize;
	protected ByteArray initialValue;
	protected ByteArray lastChainingValue;
	protected ByteArray lastMessageBlock;
	
	public int getBlockSize() {
		return this.blockSize;
	}
	
	public int getDigestSize() {
		return this.digestSize;
	}
	
	public ByteArray getInitialValue() {
		return this.initialValue;
	}
	
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	public void setDigestSize(int digestSize) {
		this.digestSize = digestSize;
	}
	
	public ByteArray processMessage(ByteArray message) {
		return processMessage(message, initialValue);
	}
	
	public abstract ByteArray processMessage(ByteArray message, ByteArray chainingValue);
	
	public void reset() {
		lastMessageBlock = null;
		lastChainingValue = null;
	}
	
	/**
	 * Applies a padding to the given message.
	 */
	protected abstract ByteArray applyPadding(ByteArray message);
	
	/**
	 * Processes the given message block with the last stored chaining value.
	 */
	protected ByteArray processBlock(ByteArray messageBlock) {
		return processBlock(messageBlock, lastChainingValue);
	}
	
	/**
	 * Processes the given message block with the given chaining value.
	 */
	protected abstract ByteArray processBlock(ByteArray messageBlock, ByteArray chainingValue);
	
}
