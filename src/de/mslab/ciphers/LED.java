package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidKeySizeError;

/**
 * Implements the LED block cipher, designed 2011 by Guo, Peyrin and al.  
 * 
 */
public abstract class LED extends AbstractRoundBasedBlockCipher {
	
	public static final int[] INVERSE_SBOX = PRESENT.INVERSE_SBOX;
	public static final int NUM_BYTES_IN_64_BIT = 64 / Byte.SIZE;
	public static final int NUM_BYTES_IN_128_BIT = 128 / Byte.SIZE;
	public static final int[] ROUND_CONSTANTS = {
		0x01, 0x03, 0x07, 0x0F, 0x1F, 0x3E, 0x3D, 0x3B, 0x37, 0x2F,
		0x1E, 0x3C, 0x39, 0x33, 0x27, 0x0E, 0x1D, 0x3A, 0x35, 0x2B,
		0x16, 0x2C, 0x18, 0x30, 0x21, 0x02, 0x05, 0x0B, 0x17, 0x2E,
		0x1C, 0x38, 0x31, 0x23, 0x06, 0x0D, 0x1B, 0x36, 0x2D, 0x1A,
		0x34, 0x29, 0x12, 0x24, 0x08, 0x11, 0x22, 0x04
	};
	
	public static final int[] SBOX = PRESENT.SBOX;
	
	private static final int[] XTIMES_02 = { 
		0x00,0x02,0x04,0x06,0x08,0x0a,0x0c,0x0e,0x03,0x01,0x07,0x05,0x0b,0x09,0x0f,0x0d
	};
	private static final int[] XTIMES_03 = { 
		0x00,0x03,0x06,0x05,0x0c,0x0f,0x0a,0x09,0x0b,0x08,0x0d,0x0e,0x07,0x04,0x01,0x02
	};
	private static final int[] XTIMES_04 = { 
		0x00,0x04,0x08,0x0c,0x03,0x07,0x0b,0x0f,0x06,0x02,0x0e,0x0a,0x05,0x01,0x0d,0x09
	};
	private static final int[] XTIMES_05 = { 
		0x00,0x05,0x0a,0x0f,0x07,0x02,0x0d,0x08,0x0e,0x0b,0x04,0x01,0x09,0x0c,0x03,0x06
	};
	private static final int[] XTIMES_06 = { 
		0x00,0x06,0x0c,0x0a,0x0b,0x0d,0x07,0x01,0x05,0x03,0x09,0x0f,0x0e,0x08,0x02,0x04
	};
	private static final int[] XTIMES_07 = { 
		0x00,0x07,0x0e,0x09,0x0f,0x08,0x01,0x06,0x0d,0x0a,0x03,0x04,0x02,0x05,0x0c,0x0b
	};
	private static final int[] XTIMES_08 = { 
		0x00,0x08,0x03,0x0b,0x06,0x0e,0x05,0x0d,0x0c,0x04,0x0f,0x07,0x0a,0x02,0x09,0x01
	};
	private static final int[] XTIMES_09 = { 
		0x00,0x09,0x01,0x08,0x02,0x0b,0x03,0x0a,0x04,0x0d,0x05,0x0c,0x06,0x0f,0x07,0x0e
	};
	private static final int[] XTIMES_0A = { 
		0x00,0x0a,0x07,0x0d,0x0e,0x04,0x09,0x03,0x0f,0x05,0x08,0x02,0x01,0x0b,0x06,0x0c
	};
	private static final int[] XTIMES_0B = { 
		0x00,0x0b,0x05,0x0e,0x0a,0x01,0x0f,0x04,0x07,0x0c,0x02,0x09,0x0d,0x06,0x08,0x03
	};
	private static final int[] XTIMES_0C = { 
		0x00,0x0c,0x0b,0x07,0x05,0x09,0x0e,0x02,0x0a,0x06,0x01,0x0d,0x0f,0x03,0x04,0x08
	};
	private static final int[] XTIMES_0D = { 
		0x00,0x0d,0x09,0x04,0x01,0x0c,0x08,0x05,0x02,0x0f,0x0b,0x06,0x03,0x0e,0x0a,0x07
	};
	private static final int[] XTIMES_0E = { 
		0x00,0x0e,0x0f,0x01,0x0d,0x03,0x02,0x0c,0x09,0x07,0x06,0x08,0x04,0x0a,0x0b,0x05
	};
	private static final int[] XTIMES_0F = { 
		0x00,0x0f,0x0d,0x02,0x09,0x06,0x04,0x0b,0x01,0x0e,0x0c,0x03,0x08,0x07,0x05,0x0a
	};
	
	protected int cellSize = 4;
	protected int numRoundsPerKeyInjection = 4;
	protected int numColumns = 4;
	protected int numRows = 4;
	
	protected ByteArray internalSecretKey;
	
	public boolean canInvertKeySchedule() {
		return true;
	}
	
	public ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		return keyPart;
	}
	
	public ByteArray decryptRounds(ByteArray block, int fromRound, int toRound) {
		state = twoNibblesPerByteToSingleNibblePerByte(block);
		
		for (int round = toRound; round >= fromRound; round--) {
			if (round != 1 && hasKeyInjectionInRound(round)) {
				state = internalAddRoundKey(round, state);
			}
			
			state = invertMixColumnsSerial(state, round);
			state = invertShiftRows(state, round);
			state = invertSubCells(state, round);
			state = addConstant(state, round);
		}
		
		if (fromRound == 1) {
			state = internalAddRoundKey(0, state);
		}
		
		return singleNibblesPerByteToTwoNibblesPerByte(state);
	}
	
	public ByteArray encryptRounds(ByteArray block, int fromRound, int toRound) {
		state = twoNibblesPerByteToSingleNibblePerByte(block);
		
		if (fromRound == 1) {
			state = internalAddRoundKey(0, state);
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			state = addConstant(state, round);
			state = subCells(state, round);
			state = shiftRows(state, round);
			state = mixColumnsSerial(state, round);
			
			if (round != 1 && hasKeyInjectionInRound(round)) {
				state = internalAddRoundKey(round, state);
			}
		}
		
		return singleNibblesPerByteToTwoNibblesPerByte(state);
	}
	
	public int getNumActiveComponentsInKeySchedule() {
		return 0;
	}
	
	public ByteArray getRoundKey(int round) {
		return secretKey;
	}
	
	public boolean hasKeyInjectionInRound(int round) {
		if (round <= numRounds) {
			return round % numRoundsPerKeyInjection == 0;
		} else {
			return false;
		}
	}
	
	public boolean injectsKeyAtRoundEnd(int round) {
		return true;
	}
	
	public void setKey(ByteArray key) {
		setExpandedKey(key);
	}
	
	public void setExpandedKey(ByteArray expandedKey) {
		checkKeySize(expandedKey.length());
		secretKey = expandedKey.clone();
		internalSecretKey = twoNibblesPerByteToSingleNibblePerByte(expandedKey);
	}
	
	public boolean operatesColumnwise() {
		return false;
	}
	
	public boolean operatesNibblewise() {
		return true;
	}
	
	protected ByteArray addConstant(ByteArray state, int round) {
		state.set(4, state.get(4) ^ 1);
		state.set(8, state.get(8) ^ 2);
		state.set(12, state.get(12) ^ 3);
		
		int constant = ROUND_CONSTANTS[round - 1];
		int rc543 = (constant >> 3) & 0x7; 
		int rc210 = constant & 0x7; 
		
		state.set(1, state.get(1) ^ rc543);
		state.set(5, state.get(5) ^ rc210);
		state.set(9, state.get(9) ^ rc543);
		state.set(13, state.get(13) ^ rc210);
		
		return state;
	}
	
	protected void checkKeySize(int length) {
		if (length != keySize) {
			throw new InvalidKeySizeError(length, new int[]{ keySize });
		}
	}
	
	protected ByteArray internalAddRoundKey(int round, ByteArray state) {
		ByteArray roundKey = internalGetRoundKey(round);
		state.xor(roundKey);
		return state;
	}
	
	protected ByteArray internalGetRoundKey(int round) {
		return internalSecretKey;
	}
	
	protected ByteArray invertMixColumnsSerial(ByteArray state, int round) {
		ByteArray newState = new ByteArray(state.length());
		
		newState.set(0, XTIMES_0C[state.get(0)] ^ XTIMES_0C[state.get(4)] ^ XTIMES_0D[state.get(8)] ^ XTIMES_04[state.get(12)]);
		newState.set(1, XTIMES_0C[state.get(1)] ^ XTIMES_0C[state.get(5)] ^ XTIMES_0D[state.get(9)] ^ XTIMES_04[state.get(13)]);
		newState.set(2, XTIMES_0C[state.get(2)] ^ XTIMES_0C[state.get(6)] ^ XTIMES_0D[state.get(10)] ^ XTIMES_04[state.get(14)]);
		newState.set(3, XTIMES_0C[state.get(3)] ^ XTIMES_0C[state.get(7)] ^ XTIMES_0D[state.get(11)] ^ XTIMES_04[state.get(15)]);
		
		newState.set(4, XTIMES_03[state.get(0)] ^ XTIMES_08[state.get(4)] ^ XTIMES_04[state.get(8)] ^ XTIMES_05[state.get(12)]);
		newState.set(5, XTIMES_03[state.get(1)] ^ XTIMES_08[state.get(5)] ^ XTIMES_04[state.get(9)] ^ XTIMES_05[state.get(13)]);
		newState.set(6, XTIMES_03[state.get(2)] ^ XTIMES_08[state.get(6)] ^ XTIMES_04[state.get(10)] ^ XTIMES_05[state.get(14)]);
		newState.set(7, XTIMES_03[state.get(3)] ^ XTIMES_08[state.get(7)] ^ XTIMES_04[state.get(11)] ^ XTIMES_05[state.get(15)]);
		
		newState.set(8, XTIMES_07[state.get(0)] ^ XTIMES_06[state.get(4)] ^ XTIMES_02[state.get(8)] ^ XTIMES_0E[state.get(12)]);
		newState.set(9, XTIMES_07[state.get(1)] ^ XTIMES_06[state.get(5)] ^ XTIMES_02[state.get(9)] ^ XTIMES_0E[state.get(13)]);
		newState.set(10, XTIMES_07[state.get(2)] ^ XTIMES_06[state.get(6)] ^ XTIMES_02[state.get(10)] ^ XTIMES_0E[state.get(14)]);
		newState.set(11, XTIMES_07[state.get(3)] ^ XTIMES_06[state.get(7)] ^ XTIMES_02[state.get(11)] ^ XTIMES_0E[state.get(15)]);
		
		newState.set(12, XTIMES_0D[state.get(0)] ^ XTIMES_09[state.get(4)] ^ XTIMES_09[state.get(8)] ^ XTIMES_0D[state.get(12)]);
		newState.set(13, XTIMES_0D[state.get(1)] ^ XTIMES_09[state.get(5)] ^ XTIMES_09[state.get(9)] ^ XTIMES_0D[state.get(13)]);
		newState.set(14, XTIMES_0D[state.get(2)] ^ XTIMES_09[state.get(6)] ^ XTIMES_09[state.get(10)] ^ XTIMES_0D[state.get(14)]);
		newState.set(15, XTIMES_0D[state.get(3)] ^ XTIMES_09[state.get(7)] ^ XTIMES_09[state.get(11)] ^ XTIMES_0D[state.get(15)]);
		
		return newState;
	}
	
	protected ByteArray invertShiftRows(ByteArray state, int round) {
		ByteArray newState = new ByteArray(state.length());
		
		for (int i = 0; i < 4; i++) {
			newState.set(i, state.get(i));
			newState.set(i + 4, state.get(4 + ((i + 3) % 4)));
			newState.set(i + 8, state.get(8 + ((i + 2) % 4)));
			newState.set(i + 12, state.get(12 + ((i + 1) % 4)));
		}
		
		return newState;
	}
	
	protected ByteArray invertSubCells(ByteArray state, int round) {
		for (int i = 0; i < state.length(); i++) {
			state.set(i, INVERSE_SBOX[state.get(i)]);
		}
		
		return state;
	}
	
	protected ByteArray mixColumnsSerial(ByteArray state, int round) {
		ByteArray newState = new ByteArray(state.length());
		
		newState.set(0, XTIMES_04[state.get(0)] ^ state.get(4) ^ XTIMES_02[state.get(8)] ^ XTIMES_02[state.get(12)]);
		newState.set(1, XTIMES_04[state.get(1)] ^ state.get(5) ^ XTIMES_02[state.get(9)] ^ XTIMES_02[state.get(13)]);
		newState.set(2, XTIMES_04[state.get(2)] ^ state.get(6) ^ XTIMES_02[state.get(10)] ^ XTIMES_02[state.get(14)]);
		newState.set(3, XTIMES_04[state.get(3)] ^ state.get(7) ^ XTIMES_02[state.get(11)] ^ XTIMES_02[state.get(15)]);
		
		newState.set(4, XTIMES_08[state.get(0)] ^ XTIMES_06[state.get(4)] ^ XTIMES_05[state.get(8)] ^ XTIMES_06[state.get(12)]);
		newState.set(5, XTIMES_08[state.get(1)] ^ XTIMES_06[state.get(5)] ^ XTIMES_05[state.get(9)] ^ XTIMES_06[state.get(13)]);
		newState.set(6, XTIMES_08[state.get(2)] ^ XTIMES_06[state.get(6)] ^ XTIMES_05[state.get(10)] ^ XTIMES_06[state.get(14)]);
		newState.set(7, XTIMES_08[state.get(3)] ^ XTIMES_06[state.get(7)] ^ XTIMES_05[state.get(11)] ^ XTIMES_06[state.get(15)]);
		
		newState.set(8, XTIMES_0B[state.get(0)] ^ XTIMES_0E[state.get(4)] ^ XTIMES_0A[state.get(8)] ^ XTIMES_09[state.get(12)]);
		newState.set(9, XTIMES_0B[state.get(1)] ^ XTIMES_0E[state.get(5)] ^ XTIMES_0A[state.get(9)] ^ XTIMES_09[state.get(13)]);
		newState.set(10, XTIMES_0B[state.get(2)] ^ XTIMES_0E[state.get(6)] ^ XTIMES_0A[state.get(10)] ^ XTIMES_09[state.get(14)]);
		newState.set(11, XTIMES_0B[state.get(3)] ^ XTIMES_0E[state.get(7)] ^ XTIMES_0A[state.get(11)] ^ XTIMES_09[state.get(15)]);
		
		newState.set(12, XTIMES_02[state.get(0)] ^ XTIMES_02[state.get(4)] ^ XTIMES_0F[state.get(8)] ^ XTIMES_0B[state.get(12)]);
		newState.set(13, XTIMES_02[state.get(1)] ^ XTIMES_02[state.get(5)] ^ XTIMES_0F[state.get(9)] ^ XTIMES_0B[state.get(13)]);
		newState.set(14, XTIMES_02[state.get(2)] ^ XTIMES_02[state.get(6)] ^ XTIMES_0F[state.get(10)] ^ XTIMES_0B[state.get(14)]);
		newState.set(15, XTIMES_02[state.get(3)] ^ XTIMES_02[state.get(7)] ^ XTIMES_0F[state.get(11)] ^ XTIMES_0B[state.get(15)]);
		
		return newState;
	}
	
	protected ByteArray shiftRows(ByteArray state, int round) {
		ByteArray newState = new ByteArray(state.length());
		
		for (int i = 0; i < 4; i++) {
			newState.set(i, state.get(i));
			newState.set(i + 4, state.get(4 + ((i + 1) % 4)));
			newState.set(i + 8, state.get(8 + ((i + 2) % 4)));
			newState.set(i + 12, state.get(12 + ((i + 3) % 4)));
		}
		
		return newState;
	}
	
	protected ByteArray subCells(ByteArray state, int round) {
		for (int i = 0; i < state.length(); i++) {
			state.set(i, SBOX[state.get(i)]);
		}
		
		return state;
	}
	
	protected ByteArray singleNibblesPerByteToTwoNibblesPerByte(ByteArray block) {
		int numBytes = block.length() / 2;
		ByteArray result = new ByteArray(numBytes);
		int index = 0;
		int value;
		
		for (int i = 0; i < numBytes; i++) {
			value = block.get(index) << 4;
			++index;
			value |= block.get(index);
			result.set(i, value);
			++index;
		}
		
		return result;
	}
	
	protected ByteArray twoNibblesPerByteToSingleNibblePerByte(ByteArray block) {
		int numNibbles = 2 * block.length();
		ByteArray result = new ByteArray(numNibbles);
		int index = 0;
		int value;
		
		for (int i = 0; i < numNibbles; i++) {
			value = block.get(index);
			
			if ((i & 1) == 0) {
				value = (value & 0xF0) >> 4;
			} else {
				value = value & 0x0F;
				index++;
			}
			
			result.set(i, value);
		}
		
		return result;
	}
	
}
