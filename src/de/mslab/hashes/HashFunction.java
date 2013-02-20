package de.mslab.hashes;

import de.mslab.core.ByteArray;

public interface HashFunction {
	/**
	 * Returns the block size in bytes.
	 */
	int getBlockSize();
	/**
	 * Returns the digest size in bytes.
	 */
	int getDigestSize();
	/**
	 * Returns the initial value of this hash function.
	 */
	ByteArray getInitialValue();
	void setBlockSize(int digestSize);
	void setDigestSize(int digestSize);
	/**
	 * Processes a hash for the given message bytes with the initial value as chaining value.
	 */
	ByteArray processMessage(ByteArray message);
	/**
	 * Processes a hash for the given message bytes with the given chaining value.
	 */
	ByteArray processMessage(ByteArray message, ByteArray chainingValue);
	/**
	 * Resets internal variables of the last processed message block and last chaining value.
	 */
	void reset();
}



