package de.mslab.hashes;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.ByteArray;

/**
 * Base class for hash function which base on a block cipher. 
 * 
 */
abstract class AbstractCipherBasedHashFunction extends AbstractHashFunction {
	/**
	 * The underlying block cipher.
	 */
	protected RoundBasedBlockCipher cipher;
	
	/**
	 * Applies the block cipher compression function for the given message and the given chaining value.
	 */
	protected abstract ByteArray applyCompressionFunction(ByteArray message, ByteArray chainingValue);
}
