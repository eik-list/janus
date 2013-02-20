package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidKeySizeError;

public abstract class MCrypton extends AbstractRoundBasedBlockCipher {
	
	public static final int NUM_BYTES_IN_64_BIT = 64 / Byte.SIZE;
	public static final int NUM_ROUNDS = 12;
	public static final int EXPANDED_KEY_LENGTH = (NUM_ROUNDS + 1) * NUM_BYTES_IN_64_BIT;
	
	protected static final int[] CONSTANTS = {
		0x1111, 0x2222, 0x4444, 0x8888, 0x3333, 0x6666, 0xcccc, 0xbbbb, 0x5555, 0xaaaa, 0x7777, 0xeeee, 0xffff
	};
	protected static final int[] M = {
		0xE, 0xD, 0xB, 7
	};
	protected static final int[][] SBOXES = {
		{ 4, 15, 3, 8, 13, 10, 12, 0, 11, 5, 7, 14, 2, 6, 1, 9 }, // S0
		{ 1, 12, 7, 10, 6, 13, 5, 3, 15, 11, 2, 0, 8, 4, 9, 14 }, // S1
		{ 7, 14, 12, 2, 0, 9, 13, 10, 3, 15, 5, 8, 6, 4, 11, 1 }, // S0^{-1}
		{ 11, 0, 10, 7, 13, 6, 4, 2, 12, 14, 3, 9, 1, 5, 15, 8 }  // S1^{-1}
	};
	protected static final int[] SBOX_ORDER = {
		0, 1, 2, 3,  
		1, 2, 3, 0,  
		2, 3, 0, 1, 
		3, 0, 1, 2
	};
	protected static final int[] INVERSE_SBOX_ORDER = {
		2, 3, 0, 1,
		3, 0, 1, 2, 
		0, 1, 2, 3, 
		1, 2, 3, 0
	};
	
	public MCrypton() {
		super();
		numRounds = NUM_ROUNDS;
		stateSize = NUM_BYTES_IN_64_BIT;
	}
	
	public boolean canInvertKeySchedule() {
		return true;
	}
	
	/**
	 * Inverts the key schedule, and computes the expanded key from a given state of the internal 
	 * key register of mcrypton 64. Note, for other ciphers, one or multiple round keys are expected for 
	 * keyParts. For mCrypton, we expect it to be a state of the key register.
	 * Following the specification of the key schedule of mcrypton, 
	 * the key register U is initialized with the secret key K at the beginning: 
	 * U = (K[0], K[1], K[2], ...). This is the expected state, when the given round parameter is 0.
	 * Then, after every iteration, U is updated to become U = (U[1], U[2], ...).
	 * This permutation is simply inverted to initial state, and then, the regular key schedule of 
	 * mCrypton is performed and the result is returned.  
	 * 
	 * @param keyPart Expects to be the state of the key register after iteration <code>round</round>. 
	 * @param round The round in which the given key register should be. 
	 * @throws InvalidKeySizeError In case that the length of keyPart is not equivalent to the keySize.
	 */
	public ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		throw new Error("The subclasses of MCrypton need to implement the computeExpandedKey method. " +
			"The class you instantiate seems to have not implemented it.");
	}
	
	public ByteArray computeKeyPart(ByteArray expandedKey, int round) {
		return expandedKey.splice(0, keySize);
	}
	
	public ByteArray decryptRounds(ByteArray block, int fromRound, int toRound) {
		state = block.clone();
		
		if (toRound == numRounds) {
			tau(state);
			pi(state);
			tau(state);
		}
		
		for (int round = toRound; round >= fromRound; round--) {
			state = addRoundKey(round, state);
			tau(state);
			pi(state);
			invertGamma(state);
		}
		
		if (fromRound == 1) {
			state = addRoundKey(0, state);
		}
		
		return state;
	}
	
	public ByteArray encryptRounds(ByteArray block, int fromRound, int toRound) {
		state = block.clone();
		
		if (fromRound == 1) {
			state = addRoundKey(0, state);
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			gamma(state);
			pi(state);
			tau(state);
			state = addRoundKey(round, state);
		}
		
		if (toRound == numRounds) {
			tau(state);
			pi(state);
			tau(state);
		}
		
		return state;
	}
	
	public int getNumActiveComponentsInKeySchedule() {
		return (numRounds + 1) * 4;
	}
	
	public ByteArray getRoundKey(int round) {
		int from = round * stateSize;
		int to = from + stateSize;
		return this.secretKey.splice(from, to);
	}
	
	public boolean hasKeyInjectionInRound(int round) {
		return round >= 0 && round <= numRounds;
	}
	
	public boolean injectsKeyAtRoundBegin(int round) {
		return round == 1;
	}
	
	public boolean injectsKeyAtRoundEnd(int round) {
		return true;
	}
	
	public boolean operatesColumnwise() {
		return false;
	}
	
	public boolean operatesNibblewise() {
		return true;
	}
	
	public void setKey(ByteArray key) {
		checkKeyLength(key.length());
		secretKey = expandKey(key);
	}
	
	public void setExpandedKey(ByteArray expandedKey) {
		checkExpandedKeyLength(expandedKey.length());
		secretKey = expandedKey;
	}
	
	protected abstract ByteArray expandKey(ByteArray key);
	
	protected void checkExpandedKeyLength(int providedKeyLength) {
		if (providedKeyLength != EXPANDED_KEY_LENGTH) {
			throw new InvalidKeySizeError(providedKeyLength, new int[]{ EXPANDED_KEY_LENGTH });
		}
	}
	
	protected void checkKeyLength(int providedKeyLength) {
		if (providedKeyLength != keySize) {
			throw new InvalidKeySizeError(providedKeyLength, new int[]{ keySize });
		}
	}
	
	/**
	 * S-box substitution.
	 * @param state
	 */
	private void gamma(ByteArray state) {
		int value, lsb, msb, index = 0;
		
		for (int i = 0; i < stateSize; i++) {
			value = state.get(i);
			lsb = (value & 0xF);
			msb = (value >> 4) & 0xF;
			
			lsb = SBOXES[SBOX_ORDER[index++]][lsb];
			msb = SBOXES[SBOX_ORDER[index++]][msb];
			value = lsb | (msb << 4);
			state.set(i, value);
		}
	}
	
	/**
	 * Inverse S-box substitution.
	 * @param state
	 */
	private void invertGamma(ByteArray state) {
		int value, lsb, msb, index = 0;
		
		for (int i = 0; i < stateSize; i++) {
			value = state.get(i);
			lsb = (value & 0xF);
			msb = (value >> 4) & 0xF;
			
			lsb = SBOXES[INVERSE_SBOX_ORDER[index++]][lsb];
			msb = SBOXES[INVERSE_SBOX_ORDER[index++]][msb];
			value = lsb | (msb << 4);
			state.set(i, value);
		}
	}
	
	/**
	 * Column-wise permutation.
	 * 
	 *	m_0 = 1110, m_1 = 1101, m_2 = 1011, m_3 = 0111
		
		i = 0:
		j = 0: b_0 = (m_0 & a_0) ^ (m_1 & a_1) ^ (m_2 & a_2) ^ (m_3 & a_3)
		j = 1: b_1 = (m_1 & a_0) ^ (m_2 & a_1) ^ (m_3 & a_2) ^ (m_0 & a_3)
		j = 2: b_2 = (m_2 & a_0) ^ (m_3 & a_1) ^ (m_0 & a_2) ^ (m_1 & a_3)
		j = 3: b_3 = (m_3 & a_0) ^ (m_0 & a_1) ^ (m_1 & a_2) ^ (m_2 & a_3)
		
		i = 1:
		j = 0: b_0 = (m_1 & a_0) ^ (m_2 & a_1) ^ (m_3 & a_2) ^ (m_0 & a_3)
		j = 1: b_1 = (m_2 & a_0) ^ (m_3 & a_1) ^ (m_0 & a_2) ^ (m_1 & a_3)
		j = 2: b_2 = (m_3 & a_0) ^ (m_0 & a_1) ^ (m_1 & a_2) ^ (m_2 & a_3)
		j = 3: b_3 = (m_0 & a_0) ^ (m_1 & a_1) ^ (m_2 & a_2) ^ (m_3 & a_3)
		
		i = 2:
		j = 0: b_0 = (m_2 & a_0) ^ (m_3 & a_1) ^ (m_0 & a_2) ^ (m_1 & a_3)
		j = 1: b_1 = (m_3 & a_0) ^ (m_0 & a_1) ^ (m_1 & a_2) ^ (m_2 & a_3)
		j = 2: b_2 = (m_0 & a_0) ^ (m_1 & a_1) ^ (m_2 & a_2) ^ (m_3 & a_3)
		j = 3: b_3 = (m_1 & a_0) ^ (m_2 & a_1) ^ (m_3 & a_2) ^ (m_0 & a_3)
		
		i = 3:
		j = 0: b_0 = (m_3 & a_0) ^ (m_0 & a_1) ^ (m_1 & a_2) ^ (m_2 & a_3)
		j = 1: b_1 = (m_0 & a_0) ^ (m_1 & a_1) ^ (m_2 & a_2) ^ (m_3 & a_3)
		j = 2: b_2 = (m_1 & a_0) ^ (m_2 & a_1) ^ (m_3 & a_2) ^ (m_0 & a_3)
		j = 3: b_3 = (m_2 & a_0) ^ (m_3 & a_1) ^ (m_0 & a_2) ^ (m_1 & a_3)
	 * @param state
	 */
	private void pi(ByteArray state) {
		final int numColumns = 4;
		final int numRows = 4;
		final int numNibbles = 2 * stateSize;
		final int[] a = new int[numNibbles];
		final int[] b = new int[numNibbles];
		
		// a and b need to be indexed column-wise as below, not row-wise as the state
		// 0 4 8 12
		// 1 5 9 13
		// 2 6 10 14
		// 3 7 11 15
		
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				a[4 * i + j] = state.getNibble(j * 4 + i);
			}
		}
		
		for (int i = 0; i < numColumns; i++) { 
			// Process one column (b_0, b_1, b_2, b_3)
			for (int j = 0; j < numRows; j++) {
				// Generate b_i
				for (int k = 0; k < numColumns; k++) {
					b[numRows * i + j] ^= M[(i + j + k) & 3] & a[numRows * i + k];
				}
			}
		}
		
		// Transpose b and store in state
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				state.setNibble(4 * i + j, b[j * 4 + i]);
			}
		}
	}
	
	/**
	 * Transposes the 4-x-4 state matrix.
	 * @param state
	 */
	private void tau(ByteArray state) {
		// (0|1) (2|3)		 (0|4) (8|12)
		// (4|5) (6|7)		 (1|5) (9|13)
		// (8|9) (10|11)  => (2|6) (10|14)
		// (12|13)(14|15)	 (3|7) (11|15)
		final int numColumns = 4;
		final int numRows = 4;
		final int numNibbles = 2 * stateSize;
		final int[] a = new int[numNibbles];
		
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				a[4 * i + j] = state.getNibble(j * 4 + i);
			}
		}
		
		for (int i = 0; i < numNibbles; i++) {
			state.setNibble(i, a[i]);
		}
	}
	
}
