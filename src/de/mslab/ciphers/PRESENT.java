package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidKeySizeError;

/**
 * Implements the PRESENT ultra-lightweight block cipher, designed 2007 by Bogdanov, Knudsen et al.
 * 
 */
public abstract class PRESENT extends AbstractRoundBasedBlockCipher {
	
	public static final int NUM_ROUNDS = 31;
	public static final int[] INVERSE_PERMUTATION = {
		 0, 16, 32, 48,  1, 17, 33, 49,  2, 18, 34, 50,  3, 19, 35, 51, 
		 4, 20, 36, 52,  5, 21, 37, 53,  6, 22, 38, 54,  7, 23, 39, 55, 
		 8, 24, 40, 56,  9, 25, 41, 57, 10, 26, 42, 58, 11, 27, 43, 59,
		12, 28, 44, 60, 13, 29, 45, 61, 14, 30, 46, 62, 15, 31, 47, 63
	};
	public static final int[] INVERSE_SBOX = { 
		0x05, 0x0E, 0x0F, 0x08, 0x0C, 0x01, 0x02, 0x0D, 0x0B, 0x04, 0x06, 0x03, 0x00, 0x07, 0x09, 0x0A
	};
	public static final int[] PERMUTATION = {
		0x00,0x04,0x08,0x0c,0x10,0x14,0x18,0x1c,0x20,0x24,0x28,0x2c,0x30,0x34,0x38,0x3c,
		0x01,0x05,0x09,0x0d,0x11,0x15,0x19,0x1d,0x21,0x25,0x29,0x2d,0x31,0x35,0x39,0x3d,
		0x02,0x06,0x0a,0x0e,0x12,0x16,0x1a,0x1e,0x22,0x26,0x2a,0x2e,0x32,0x36,0x3a,0x3e,
		0x03,0x07,0x0b,0x0f,0x13,0x17,0x1b,0x1f,0x23,0x27,0x2b,0x2f,0x33,0x37,0x3b,0x3f
	};
	public static final int[] SBOX = { 
		0x0C, 0x05, 0x06, 0x0B, 0x09, 0x00, 0x0A, 0x0D, 0x03, 0x0E, 0x0F, 0x08, 0x04, 0x07, 0x01, 0x02 
	};
	
	public PRESENT() {
		super();
		this.numRounds = NUM_ROUNDS;
	}
	
	public boolean canInvertKeySchedule() {
		return false;
	}
	
	public ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		checkKeySize(keyPart.length());
		secretKey = new ByteArray((numRounds + 1) * keySize);
		
		if (round <= numRounds + 1) {
			secretKey = expandKeyForwards(secretKey, keyPart.clone(), round);
		}
		
		if (round > 0) {
			secretKey = expandKeyBackwards(secretKey, keyPart.clone(), round);
		}
		
		return secretKey;
	}
	
	public ByteArray computeKeyPart(ByteArray expandedKey, int round) {
		ByteArray result = new ByteArray(keySize);
		result.copyBytes(expandedKey, (round - 1) * keySize, 0, keySize);
		return result;
	}
	
	public ByteArray decryptRounds(ByteArray block, int fromRound, int toRound) {
		state = block.clone();
		
		if (toRound == numRounds) {
			state = addRoundKey(numRounds + 1, state);
		}
		
		for (int round = toRound; round >= fromRound; round--) {
			state = invertPermute(state);
			state = invertSubBytes(state);
			state = addRoundKey(round, state);
		}
		
		return state;
	}
	
	public ByteArray encryptRounds(ByteArray block, int fromRound, int toRound) {
		state = block.clone();
		
		for (int round = fromRound; round <= toRound; round++) {
			state = addRoundKey(round, state);
			state = subBytes(state);
			state = permute(state);
		}
		
		if (toRound == numRounds) {
			state = addRoundKey(numRounds + 1, state);
		}
		
		return state;
	}
	
	public abstract int getNumActiveComponentsInKeySchedule();
	
	public ByteArray getRoundKey(int round) {
		int from = (round - 1) * keySize;
		int to = from + stateSize;
		return this.secretKey.splice(from, to);
	}
	
	public boolean hasKeyInjectionInRound(int round) {
		return round >= 1 && round <= numRounds + 1;
	}
	
	public boolean injectsKeyAtRoundBegin(int round) {
		return true;
	}
	
	public boolean operatesColumnwise() {
		return false;
	}
	
	public boolean operatesNibblewise() {
		return true;
	}
	
	public void setKey(ByteArray key) {
		checkKeySize(key.length());
		this.secretKey = expandKey(key.clone());
	}
	
	public void setExpandedKey(ByteArray expandedKey) {
		checkExpandedKeySize(expandedKey.length());
		this.secretKey = expandedKey.clone();
	}
	
	protected abstract ByteArray expandKeyBackwards(ByteArray secretKey, ByteArray key, int round);
	protected abstract ByteArray expandKeyForwards(ByteArray secretKey, ByteArray key, int round);
	
	private void checkKeySize(int length) {
		if (length != keySize) {
			throw new InvalidKeySizeError(length);
		}
	}
	
	private void checkExpandedKeySize(int length) {
		if (length != (numRounds + 1) * keySize) {
			throw new InvalidKeySizeError(length);
		}
	}
	
	private ByteArray expandKey(ByteArray key) {
		int numRoundKeys = numRounds + 1;
		secretKey = new ByteArray(numRoundKeys * keySize);
		secretKey = expandKeyForwards(secretKey, key, 1);
		return secretKey;
	}
	
	private ByteArray invertPermute(ByteArray state) {
		ByteArray newState = state.clone();
		
		for (int i = 0; i < Long.SIZE; i++) {
			newState.setBit(PERMUTATION[i], state.getBit(i));
		}
		
		return newState;
	}
	
	private ByteArray invertSubBytes(ByteArray state) {
		int value, msbNibble, lsbNibble;
		
		for (int i = 0; i < stateSize; i++) {
			value = state.get(i);
			lsbNibble = INVERSE_SBOX[value & 0x0F];
			msbNibble = INVERSE_SBOX[(value & 0xF0) >> 4] << 4;
			state.set(i, msbNibble | lsbNibble);
		}
		
		return state;
	}
	
	private ByteArray permute(ByteArray state) {
		ByteArray newState = state.clone();
		
		for (int i = 0; i < Long.SIZE; i++) {
			newState.setBit(i, state.getBit(PERMUTATION[i]));
		}
		
		return newState;
	}
	
	private ByteArray subBytes(ByteArray state) {
		int value, msbNibble, lsbNibble;
		
		for (int i = 0; i < stateSize; i++) {
			value = state.get(i);
			lsbNibble = SBOX[value & 0x0F];
			msbNibble = SBOX[(value & 0xF0) >> 4] << 4;
			state.set(i, msbNibble | lsbNibble);
		}
		
		return state;
	}
	
}
