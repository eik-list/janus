package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidKeySizeError;

public class PRINCECore extends AbstractRoundBasedBlockCipher {
	
	public static final int[] M_0 = createTable(new int[]{ 
		0x0888,0x4044,0x2202,0x1110,0x8880,0x0444,0x2022,0x1101,
		0x8808,0x4440,0x0222,0x1011,0x8088,0x4404,0x2220,0x0111
	});
	public static final int[] M_1 = createTable(new int[]{
		0x8880,0x0444,0x2022,0x1101,0x8808,0x4440,0x0222,0x1011,
		0x8088,0x4404,0x2220,0x0111,0x0888,0x4044,0x2202,0x1110
	});
	public static final int[] INVERSE_M_0 = invertTable(M_0); 
	public static final int[] INVERSE_M_1 = invertTable(M_1); 
	
	protected static final String NAME = "PRINCECore";
	protected static final int NUM_BYTES_IN_64_BIT = 64 / Byte.SIZE; 
	protected static final int NUM_ROUNDS = 11;
	
	public static final long[] ROUND_CONSTANTS = new long[]{
		0x0000000000000000L, 
		0x13198a2e03707344L, 
		0xa4093822299f31d0L, 
		0x082efa98ec4e6c89L, 
		0x452821e638d01377L, 
		0xbe5466cf34e90c6cL, 
		0, // the middle part S -> M' -> S^{-1} 
		0x7ef84f78fd955cb1L, 
		0x85840851f1ac43aaL, 
		0xc882d32f25323c54L, 
		0x64a51195e0e3610dL, 
		0xd3b5a399ca0c2399L, 
		0xc0ac29b7c97c50ddL
	};
	public static final int[] SBOX = new int[]{
		0xB,0xF,0x3,0x2,0xA,0xC,0x9,0x1,0x6,0x7,0x8,0x0,0xE,0x5,0xD,0x4
	};
	public static final int[] INVERSE_SBOX = new int[]{
		0xB,0x7,0x3,0x2,0xF,0xD,0x8,0x9,0xA,0x6,0x4,0x0,0x5,0xE,0xC,0x1
	};
	
	protected long expandedKey = 0L; 
	protected boolean isPostWhiteningEnabled = true;
	protected boolean isPreWhiteningEnabled = true;
	
	public PRINCECore() {
		super();
		keySize = NUM_BYTES_IN_64_BIT;
		stateSize = NUM_BYTES_IN_64_BIT;
		name = NAME;
		numRounds = NUM_ROUNDS;
	}

	/**
	 * Precomputes a table with all 2^{16} results of the multiplication with the given matrix.
	 * @param matrix The multiplied matrix. 
	 * @return The precomputed table.
	 */
	public static int[] createTable(int[] matrix) {
		final int numValues = 1 << 16;
		final int numBits = 16;
		final int[] table = new int[numValues];
		int value = 0;
		int tempvalue;
		int bit = 0;
		int shiftPositions;
		
		for (value = 0; value < numValues; value++) {
			shiftPositions = 15;
			
			for (int row = 0; row < numBits; row++) {
				tempvalue = value | 0;
				tempvalue &= matrix[row];
				
				bit = 0;
				
				for (int column = 0; column < numBits; column++) {
					bit ^= tempvalue & 1;
					tempvalue >>= 1;
				}
				
				table[value] |= (bit << shiftPositions);
				shiftPositions--;
			}
		}
		
		return table;
	}
	
	protected static int[] invertTable(int[] table) {
		final int numElements = table.length;
		final int[] inverseTable = new int[numElements];
		int value;
		
		for (int i = 0; i < numElements; i++) {
			value = table[i];
			inverseTable[value] = i;
		}
		
		return inverseTable;
	}
	
	public boolean canInvertKeySchedule() {
		return true;
	}
	
	public ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		return keyPart;
	}
	
	public ByteArray computeKeyPart(ByteArray expandedKey, int round) {
		return expandedKey;
	}
	
	public ByteArray decryptRounds(ByteArray block, int fromRound, int toRound) {
		ByteArray result = block.clone();
		long state = block.readLong(0);

		if (toRound == numRounds && isPostWhiteningEnabled) {
			state ^= expandedKey;
			state ^= ROUND_CONSTANTS[12];
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (round == 6) {
				state = subBytes(state);
				state = invertMiddleMixColumns(state);
				state = invertSubBytes(state);
			} else if (round <= 5) {
				state ^= expandedKey;
				state ^= ROUND_CONSTANTS[round];
				state = invertMixColumns(state);
				state = invertSubBytes(state);
			} else {
				state = subBytes(state);
				state = mixColumns(state);
				state ^= ROUND_CONSTANTS[round];
				state ^= expandedKey;
			}
		}
		
		if (fromRound == 1 && isPreWhiteningEnabled) {
			state ^= ROUND_CONSTANTS[0];
			state ^= expandedKey;
		}
		
		result.writeLong(state);
		return result;
	}
	
	public ByteArray encryptRounds(ByteArray block, int fromRound, int toRound) {
		ByteArray result = block.clone();
		long state = block.readLong(0);
		
		if (fromRound == 1 && isPreWhiteningEnabled) {
			state ^= expandedKey;
			state ^= ROUND_CONSTANTS[0];
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (round <= 5) {
				state = subBytes(state);
				state = mixColumns(state);
				state ^= ROUND_CONSTANTS[round];
				state ^= expandedKey;
			} else if (round == 6) {
				state = subBytes(state);
				state = middleMixColumns(state);
				state = invertSubBytes(state);
			} else {
				state ^= expandedKey;
				state ^= ROUND_CONSTANTS[round];
				state = invertMixColumns(state);
				state = invertSubBytes(state);
			}
		}
		
		if (toRound == numRounds && isPostWhiteningEnabled) {
			state ^= ROUND_CONSTANTS[12];
			state ^= expandedKey;
		}
		
		result.writeLong(state);
		return result;
	}
	
	public int getNumActiveComponentsInEncryption(int numRounds) {
		return numRounds * 16;
	}
	
	public ByteArray getRoundKey(int round) {
		return secretKey;
	}
	
	public boolean hasKeyInjectionInRound(int round) {
		return round >= 0 && round <= numRounds + 1 && round != 6;
	}
	
	public boolean injectsKeyAtRoundBegin(int round) {
		if (round >= 7 && round <= numRounds + 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean injectsKeyAtRoundEnd(int round) {
		if (round >= 0 && round <= 5) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean operatesBytewise() {
		return true;
	}
	
	public boolean operatesColumnwise() {
		return true;
	}
	
	public boolean operatesNibblewise() {
		return true;
	}
	
	public void setKey(ByteArray key) {
		checkKeyLength(key);
		expandKey(key);
	}
	
	public void setPostWhiteningEnabled(boolean value) {
		isPostWhiteningEnabled = value;
	}
	
	public void setPreWhiteningEnabled(boolean value) {
		isPreWhiteningEnabled = value;
	}
	
	public void setExpandedKey(ByteArray expandedKey) {
		checkKeyLength(expandedKey);
		expandKey(expandedKey);
	}
	
	protected void checkKeyLength(ByteArray key) {
		if (key.length() != keySize) {
			throw new InvalidKeySizeError(key.length(), new int[]{ keySize });
		}
	}
	
	protected void expandKey(ByteArray key) {
		this.secretKey = key.clone();
		this.expandedKey = key.readLong(0);
	}
	
	protected long invertMiddleMixColumns(long state) {
		final int mask = 0xFFFF; 
		return (long)INVERSE_M_0[(int)(state & mask)]
			| (long)INVERSE_M_1[(int)((state >>> 16) & mask)] << 16
			| (long)INVERSE_M_1[(int)((state >>> 32) & mask)] << 32
			| (long)INVERSE_M_0[(int)((state >>> 48) & mask)] << 48;
	}
	
	protected long invertMixColumns(long state) {
		state = invertShiftRows(state);
		return invertMiddleMixColumns(state);
	}
	
	/**
	 * Inverse AES shift rows
	 *  0  4  8 12		 0  4  8 12
	 *  1  5  9 13 =>	13  1  5  9
	 *  2  6 10 14		10 14  2  6
	 *  3  7 11 15		 7 11 15  3
	 */
	protected long invertShiftRows(long state) {
		return (state & 0xF000000000000000L)
			| (state & 0x0000000000000F00L) << 48
			| (state & 0x0000000000F00000L) << 32
			| (state & 0x0000000F00000000L) << 16
			| (state & 0x0000F00000000000L)
			| (state & 0x0F00000000000000L) >>> 16 
			| (state & 0x00000000000000F0L) << 32
			| (state & 0x00000000000F0000L) << 16
			| (state & 0x00000000F0000000L)
			| (state & 0x00000F0000000000L) >>> 16
			| (state & 0x00F0000000000000L) >>> 32
			| (state & 0x000000000000000FL) << 16
			| (state & 0x000000000000F000L)
			| (state & 0x000000000F000000L) >>> 16
			| (state & 0x000000F000000000L) >>> 32
			| (state & 0x000F000000000000L) >>> 48;
	}
	
	protected long invertSubBytes(long state) {
		long mask = 0xF;
		long result = 0;
		int shift = 0;
		int value;
		
		for (int i = 0; i < 16; i++) {
			value = (int)((state & mask) >>> shift);
			result |= ((long)INVERSE_SBOX[value] << shift);
			mask <<= 4;
			shift += 4;
		}
		
		return result;
	}
	
	protected long middleMixColumns(long state) {
		final int mask = 0xFFFF; 
		long result = (long)M_0[(int)(state & mask)]
			| (long)M_1[(int)((state >>> 16) & mask)] << 16
			| (long)M_1[(int)((state >>> 32) & mask)] << 32
			| (long)M_0[(int)((state >>> 48) & mask)] << 48;
		return result;
	}
	
	protected long mixColumns(long state) {
		state = middleMixColumns(state);
		return shiftRows(state);
	}
	
	/**
	 * AES shift rows
	 *  0  4  8 12		 0  4  8 12
	 *  1  5  9 13 =>	 5  9 13  1
	 *  2  6 10 14		10 14  2  6
	 *  3  7 11 15		15  3  7 11
	 */
	protected long shiftRows(long state) {
		return (state & 0xF000000000000000L)
			| (state & 0x00000F0000000000L) << 16
			| (state & 0x0000000000F00000L) << 32
			| (state & 0x000000000000000FL) << 48
			| (state & 0x0000F00000000000L)
			| (state & 0x000000000F000000L) << 16
			| (state & 0x00000000000000F0L) << 32
			| (state & 0x000F000000000000L) >>> 16
			| (state & 0x00000000F0000000L)
			| (state & 0x0000000000000F00L) << 16
			| (state & 0x00F0000000000000L) >>> 32
			| (state & 0x0000000F00000000L) >>> 16
			| (state & 0x000000000000F000L)
			| (state & 0x0F00000000000000L) >>> 48
			| (state & 0x000000F000000000L) >>> 32
			| (state & 0x00000000000F0000L) >>> 16;
	}
	
	protected long subBytes(long state) {
		long mask = 0xF;
		long result = 0;
		int shift = 0;
		int value;
		
		for (int i = 0; i < 16; i++) {
			value = (int)((state & mask) >>> shift);
			result |= ((long)SBOX[value] << shift);
			mask <<= 4;
			shift += 4;
		}
		
		return result;
	}
	
}
