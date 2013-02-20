package de.mslab.ciphers;

import de.mslab.core.ByteArray;

/**
 * Interface for all symmetric ciphers which employ a round-based encryption or decryption.
 * 
 */
public interface RoundBasedBlockCipher extends BlockCipher {
	/**
	 * Performs a round-key injection on the state, that means, the state is XORed with the 
	 * round key for the passed round. The state is updated and returned. 
	 */
	ByteArray addRoundKey(int round, ByteArray state);
	/**
	 * Some ciphers, for instance the AES, can invert its key schedule. Returns <code>true</code>, 
	 * if this is the case for this cipher instance. Returns <code>false</code> otherwise.
	 */
	boolean canInvertKeySchedule();
	/**
	 * If the {@link RoundBasedBlockCipher#canInvertKeySchedule()} method returns true, 
	 * the cipher can invert the key schedule and reconstruct the secret key and all other round keys 
	 * from one given round key.  
	 * This method computes the expanded secret key from (a) given round key(s), and the mentioned round.
	 * @see RoundBasedBlockCipher#canInvertKeySchedule()
	 * @param keyPart A portion of the expanded key.  
	 * @param round The round, in which the portion of the expanded key starts. 
	 * Example: if the given round is 2, and the length of the given keyPart equals the stateSize of the
	 * cipher, then the keyPart is expected to hold the round key for round 2. The implementation of 
	 * this method should then reconstruct all missing round keys for rounds 1, 3, 4, ..., numRounds, and, 
	 * if needed, whitening keys for rounds 0, numRounds + 1.
	 * Another Example: if the given round is numRounds, and the keyPart extends the stateSize of the 
	 * cipher. Then the given keyPart is expected to hold the round key for the last round, and more 
	 * material, which is interpreted as key for round numRounds + 1, numRounds + 2, ... until its end. 
	 * Such additional material is (!) used to create round keys numRounds - 1, numRounds - 2, ... .
	 */
	ByteArray computeExpandedKey(ByteArray keyPart, int round);
	/**
	 * Returns the part of the key which was used as the difference from the given round.
	 */
	ByteArray computeKeyPart(ByteArray expandedKey, int round);
	/**
	 * Performs a decryption over some rounds from <code>toRound</code> to including <code>fromRound</code>
	 * of the given block with the current key.
	 */
	ByteArray decryptRounds(ByteArray block, int fromRound, int toRound);
	/**
	 * Performs a one-round decryption of the given block with the current key.
	 */
	ByteArray decryptRound(ByteArray block, int round);
	/**
	 * Performs an encryption over some rounds from <code>fromRound</code> to including <code>toRound</code>
	 * of the given block with the current key.
	 */
	ByteArray encryptRounds(ByteArray block, int fromRound, int toRound);
	/**
	 * Performs a one-round encryption of the given block with the current key.
	 */
	ByteArray encryptRound(ByteArray block, int round);
	/**
	 * Returns the number of active bits/bytes/nibbles in non-linear operations in a given number of rounds.
	 */
	int getNumActiveComponentsInEncryption(int numRounds);
	/**
	 * Returns the number of active bits/bytes/nibbles in non-linear operations in the key schedule.
	 */
	int getNumActiveComponentsInKeySchedule();
	/**
	 * Returns the number of rounds in the full encryption or decryption of the cipher.
	 */
	int getNumRounds();
	/**
	 * Returns the round key for the given round. 
	 * @param round The given round. 
	 */
	ByteArray getRoundKey(int round);
	/**
	 * Returns <code>true</code>, if the cipher has a key injection in the given round. 
	 * <code>False</code> otherwise.
	 */
	boolean hasKeyInjectionInRound(int round);
	/**
	 * Returns <code>true</code>, if the cipher injects the round keys always as the first
	 * operation in a round. <code>False</code> otherwise.
	 * @param round The given round. 
	 */
	boolean injectsKeyAtRoundBegin(int round);
	/**
	 * Returns <code>true</code>, if the cipher injects the round keys always as the last
	 * operation in a round. <code>False</code> otherwise.
	 * @param round The given round. 
	 */
	boolean injectsKeyAtRoundEnd(int round);
}
