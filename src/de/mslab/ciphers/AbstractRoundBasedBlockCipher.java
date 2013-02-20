package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.utils.Logger;

/**
 * Base class for round-based ciphers.
 */
abstract class AbstractRoundBasedBlockCipher implements RoundBasedBlockCipher {
	
	/**
	 * The key size in bytes.
	 */
	protected int keySize;
	/**
	 * The name of the cipher instance.
	 */
	protected String name;
	/**
	 * The number of rounds.
	 */
	protected int numRounds;
	/**
	 * The internal state.
	 */
	protected ByteArray state;
	/**
	 * The block/internal-state size in bytes
	 */
	protected int stateSize;
	/**
	 * The secret key which is used by the cipher for encryption. 
	 */
	protected ByteArray secretKey;
	/**
	 * A logger.
	 */
	protected Logger logger = Logger.getLogger();
	
	public ByteArray addRoundKey(int round, ByteArray state) {
		ByteArray roundKey = getRoundKey(round);
		state.xor(roundKey, 0, stateSize);
		return state;
	}
	
	public ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		return null;
	}
	
	public ByteArray computeKeyPart(ByteArray expandedKey, int round) {
		int from = (round - 1) * stateSize;
		
		if (hasKeyInjectionInRound(0)) {
			from += stateSize;
		}
		
		int numBytesToCopy = keySize;
		
		if (from + numBytesToCopy > expandedKey.length()) {
			from = expandedKey.length() - numBytesToCopy;
		}
		
		ByteArray result = new ByteArray(keySize);
		result.copyBytes(expandedKey, from, 0, numBytesToCopy);
		return result;
	}
	
	public ByteArray decrypt(ByteArray block) {
		return decryptRounds(block, 1, numRounds);
	}
	
	public ByteArray decryptRound(ByteArray block, int round) {
		return decryptRounds(block, round, round);
	}
	
	public ByteArray encrypt(ByteArray block) {
		return encryptRounds(block, 1, numRounds);
	}
	
	public ByteArray encryptRound(ByteArray block, int round) {
		return encryptRounds(block, round, round);
	}
	
	public ByteArray getExpandedKey() {
		return secretKey;
	}
	
	public int getKeySize() {
		return keySize;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * {@inheritDoc}
	 * @return Returns default values for byte-wise and nibble-wise operating ciphers. 
	 * numRounds * 2 * stateSize for byte-wise, numRounds * stateSize for nibble-wise ciphers.
	 */
	public int getNumActiveComponentsInEncryption(int numRounds) {
		if (operatesBytewise()) {
			if (operatesNibblewise()) {
				return numRounds * 2 * stateSize;
			} else {
				return numRounds * stateSize;
			}
		} else {
			return 0;
		}
	}
	
	public int getNumActiveComponentsInKeySchedule() {
		return 0;
	}
	
	public int getNumRounds() {
		return numRounds;
	}
	
	public int getStateSize() {
		return stateSize;
	}
	
	public boolean hasKeyInjectionInRound(int round) {
		return round >= 0 && round <= numRounds;
	}
	
	public boolean injectsKeyAtRoundBegin(int round) {
		return false;
	}
	
	public boolean injectsKeyAtRoundEnd(int round) {
		return false;
	}
	
	public boolean operatesBytewise() {
		return true;
	}
	
	public boolean operatesNibblewise() {
		return false;
	}
	
	public boolean operatesColumnwise() {
		return true;
	}
	
}
