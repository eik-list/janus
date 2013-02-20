package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidKeySizeError;

/**
 * Interface for all symmetric ciphers.
 * 
 */
public interface BlockCipher {
	/**
	 * Performs a full-cipher decryption of the given block with the current key.
	 */
	ByteArray decrypt(ByteArray block);
	/**
	 * Performs a full-cipher encryption of the given block with the current key.
	 */
	ByteArray encrypt(ByteArray block);
	/**
	 * Returns a byte array which represents the expanded key with all subkeys.
	 */
	ByteArray getExpandedKey();
	/**
	 * Returns the size in bytes of the secret key for this cipher.
	 */
	int getKeySize();
	/**
	 * Returns the name of this cipher.
	 */
	String getName();
	/**
	 * Returns the block size in bytes for this cipher.
	 */
	int getStateSize();
	/**
	 * Returns <code>true</code>, if the cipher operates byte-wise. <code>False</code> otherwise.
	 */
	boolean operatesBytewise();
	/**
	 * Returns <code>true</code>, if the cipher operates nibble-wise. <code>False</code> otherwise.
	 */
	boolean operatesNibblewise();
	/**
	 * Returns <code>true</code>, if the cipher operates byte-wise and column-wise. <code>False</code> otherwise.
	 */
	boolean operatesColumnwise();
	/**
	 * Sets the secret key used for encryption and decryption. 
	 * Internally, the cipher will be expected to check if the size of the given key is correct,  
	 * and run the key schedule.
	 * @throws InvalidKeySizeError The key size is invalid. 
	 */
	void setKey(ByteArray key);
	/**
	 * Sets the expanded key with all round keys.
	 * Internally, the cipher will be expected to check if the size of the given key is correct.
	 * This method will explicitly not run the key schedule.
	 * @throws InvalidKeySizeError The key size is invalid. 
	 */
	void setExpandedKey(ByteArray expandedKey);
}
