package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidKeySizeError;


public class LBlock extends AbstractRoundBasedBlockCipher {
	
	private static final int KEY_SIZE = 80 / Byte.SIZE;
	private static final String NAME = "LBlock";
	private static final int NUM_ROUNDS = 32;
	private static final int STATE_SIZE = 64 / Byte.SIZE;
	private static final int ROUND_KEY_SIZE = 32 / Byte.SIZE;
	private static final int[][] SBOX = new int[][]{ 
		{14, 9, 15, 0, 13, 4, 10, 11, 1, 2, 8, 3, 7, 6, 12, 5}, 
		{4, 11, 14, 9, 15, 13, 0, 10, 7, 12, 5, 6, 2, 8, 1, 3}, 
		{1, 14, 7, 12, 15, 13, 0, 6, 11, 5, 9, 3, 2, 4, 8, 10}, 
		{7, 6, 8, 11, 0, 15, 3, 14, 9, 10, 12, 13, 5, 2, 4, 1}, 
		{14, 5, 15, 0, 7, 2, 12, 13, 1, 8, 4, 9, 11, 10, 6, 3}, 
		{2, 13, 11, 12, 15, 14, 0, 9, 7, 10, 6, 3, 1, 8, 4, 5}, 
		{11, 9, 4, 14, 0, 15, 10, 13, 6, 12, 5, 7, 3, 8, 1, 2}, 
		{13, 10, 15, 0, 14, 4, 9, 11, 2, 1, 8, 3, 7, 5, 12, 6}, 
		{8, 7, 14, 5, 15, 13, 0, 6, 11, 12, 9, 10, 2, 4, 1, 3}, 
		{11, 5, 15, 0, 7, 2, 9, 13, 4, 8, 1, 12, 14, 10, 3, 6}
	};
	private static final int[][] INVERSE_SBOX = new int[][]{ 
		{ 3, 8, 9, 11, 5, 15, 13, 12, 10, 1, 6, 7, 14, 4, 0, 2 },
		{ 6, 14, 12, 15, 0, 10, 11, 8, 13, 3, 7, 1, 9, 5, 2, 4 },
		{ 6, 0, 12, 11, 13, 9, 7, 2, 14, 10, 15, 8, 3, 5, 1, 4 },
		{ 4, 15, 13, 6, 14, 12, 1, 0, 2, 8, 9, 3, 10, 11, 7, 5 },
		{ 3, 8, 5, 15, 10, 1, 14, 4, 9, 11, 13, 12, 6, 7, 0, 2 },
		{ 6, 12, 0, 11, 14, 15, 10, 8, 13, 7, 9, 2, 3, 1, 5, 4 },
		{ 4, 14, 15, 12, 2, 10, 8, 11, 13, 1, 6, 0, 9, 7, 3, 5 },
		{ 3, 9, 8, 11, 5, 13, 15, 12, 10, 6, 1, 7, 14, 0, 4, 2 },
		{ 6, 14, 12, 15, 13, 3, 7, 1, 0, 10, 11, 8, 9, 5, 2, 4 },
		{ 3, 10, 5, 14, 8, 1, 15, 4, 9, 6, 13, 0, 11, 7, 12, 2 }
	};
	
	private int roundKeySize;
	
	public LBlock() {
		super();
		name = NAME;
		keySize = KEY_SIZE;
		numRounds = NUM_ROUNDS;
		stateSize = STATE_SIZE;
		roundKeySize = ROUND_KEY_SIZE;
	}
	
	public ByteArray addRoundKey(int round, ByteArray state) {
		state = state.clone();
		state.xor(getRoundKey(round));
		return state;
	}
	
	public boolean canInvertKeySchedule() {
		return true;
	}
	
	public ByteArray computeExpandedKey(ByteArray keyPart, int round) { // round 1
		checkKeySize(keyPart.length());
		secretKey = new ByteArray(numRounds * keySize);
		secretKey.copyBytes(keyPart, 0, (round - 1) * keySize); // 0,0
		
		if (round < numRounds) {
			secretKey = expandKeyForwards(secretKey, keyPart.clone(), round); // 1
		}
		
		if (round > 1) {
			secretKey = expandKeyBackwards(secretKey, keyPart.clone(), round - 1); // 1
		}
		
		return secretKey;
	}
	
	public ByteArray computeKeyPart(ByteArray expandedKey, int round) {
		final int from = (round - 1) * keySize;
		final int to = from + keySize;
		return expandedKey.splice(from, to);
	}
	
	public ByteArray decryptRounds(ByteArray block, int fromRound, int toRound) {
		ByteArray left = block.splice(0,4);
		ByteArray right = block.splice(4);
		
		for (int round = toRound; round >= fromRound; round--) {
			block = function(left, round);
			block.xor(right);
			right = shiftLeft(block);
			
			if (round > 1) {
				right = left;
				left = block;
			}
		}
		
		left.concat(right);
		return left;
	}
	
	public ByteArray encryptRounds(ByteArray block, int fromRound, int toRound) {
		ByteArray left = block.splice(0, 4);
		ByteArray right = block.splice(4);
		
		for (int round = fromRound; round <= toRound; round++) {
			block = function(left, round);
			right = shiftRight(right);
			block.xor(right);
			
			if (round < numRounds) {
				right = left;
				left = block;
			} else {
				right = block;
			}
		}
		
		left.concat(right);
		return left;
	}
	
	public int getNumActiveComponentsInEncryption(int numRounds) {
		return numRounds * 8;
	}
	
	public int getNumActiveComponentsInKeySchedule() {
		return numRounds * 2;
	}
	
	public ByteArray getRoundKey(int round) {
		int from = (round - 1) * keySize;
		int to = from + roundKeySize;
		return this.secretKey.splice(from, to);
	}
	
	public boolean hasKeyInjectionInRound(int round) {
		return round >= 1 && round <= numRounds;
	}
	
	public boolean injectsKeyAtRoundBegin(int round) {
		return true;
	}
	
	public boolean injectsKeyAtRoundEnd(int round) {
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
	
	private void checkKeySize(int length) {
		if (length != keySize) {
			throw new InvalidKeySizeError(length);
		}
	}
	
	private void checkExpandedKeySize(int length) {
		if (length != numRounds * keySize) {
			throw new InvalidKeySizeError(length);
		}
	}
	
	private ByteArray expandKey(ByteArray key) {
		secretKey = new ByteArray(numRounds * keySize);
		secretKey.copyBytes(key);
		secretKey = expandKeyForwards(secretKey, key, 1);
		return secretKey;
	}
	
	private ByteArray expandKeyBackwards(ByteArray expandedKey, ByteArray key, int round) {
		for (; round >= 1; round--) {
			addRoundCounter(key, round);
			invertSubByteKey(key);
			rotateRightBy29(key);
			expandedKey.copyBytes(key, 0, (round - 1) * keySize);
		}
		
		return expandedKey;
	}
	
	private ByteArray expandKeyForwards(ByteArray expandedKey, ByteArray key, int round) {
		for (; round < numRounds; round++) {
			rotateLeftBy29(key);
			subByteKey(key);
			addRoundCounter(key, round);
			expandedKey.copyBytes(key, 0, round * keySize);
		}
		
		return expandedKey;
	}
	
	private void addRoundCounter(ByteArray key, int round) {
		int value = key.get(3) ^ (round >> 2);
		key.set(3, value);
		
		value = key.get(4) ^ ((round << 6) & 0xFF);
		key.set(4, value);
	}

	private void invertSubByteKey(ByteArray key) {
		int value = (INVERSE_SBOX[9][(key.get(0) >> 4) & 0xF] << 4) 
			| INVERSE_SBOX[8][key.get(0) & 0xF];
		key.set(0, value);
	}
	
	private void subByteKey(ByteArray key) {
		int value = (SBOX[9][(key.get(0) >> 4) & 0xF] << 4) 
			| SBOX[8][key.get(0) & 0xF];
		key.set(0, value);
	}
	
	private void rotateLeftBy29(ByteArray key) {
		ByteArray temp = key.clone();
		int value;
		
		for (int i = 0; i < keySize; i++) {
			value = ((temp.get((i + 3) % keySize) << 5) & 0xFF) 
				| ((temp.get((i + 4) % keySize) >> 3) & 0xFF);
			key.set(i, value);
		}
	}
	
	private void rotateRightBy29(ByteArray key) {
		ByteArray temp = key.clone();
		int value;
		
		for (int i = 0; i < keySize; i++) {
			value = ((temp.get((i + keySize - 3) % keySize) >> 5) & 0x07) 
				| ((temp.get((i + keySize - 4) % keySize) << 3) & 0xF8);
			key.set(i, value);
		}
	}
	
	private ByteArray function(ByteArray block, int round) {
		block = addRoundKey(round, block);
		block = subBytes(block);
		block = permute(block);
		return block;
	}
	
	private ByteArray permute(ByteArray block) {
		final int oldValue = block.readUInt(0);
		int newValue = 0;
		// 7 6 5 4 3 2 1 0 => 6 4 7 5 2 0 3 1
		
		newValue = ((oldValue >>> 24) & 0xf) << 28
			| ((oldValue >>> 16) & 0xf) << 24
			| ((oldValue >>> 28) & 0xf) << 20
			| ((oldValue >>> 20) & 0xf) << 16
			| ((oldValue >>> 8) & 0xf) << 12
			| (oldValue & 0xf) << 8
			| ((oldValue >>> 12) & 0xf) << 4
			| ((oldValue >>> 4) & 0xf);
		
		block.writeUInt(newValue);
		return block;
	}

	private ByteArray shiftLeft(ByteArray right) {
		short temp = right.get(3);
		
		for (int i = 3; i > 0; i--) {
			right.set(i, right.get(i - 1));
		}
		
		right.set(0, temp);
		return right;
	}
	
	private ByteArray shiftRight(ByteArray right) {
		short temp = right.get(0);
		
		for (int i = 0; i < 3; i++) {
			right.set(i, right.get(i + 1));
		}
		
		right.set(3, temp);
		return right;
	}
	
	private ByteArray subBytes(ByteArray block) {
		int value = block.readUInt(0);
		
		value = (SBOX[7][(value & 0xf0000000) >>> 28] << 28)
			| (SBOX[6][(value & 0x0f000000) >>> 24] << 24)
			| (SBOX[5][(value & 0x00f00000) >>> 20] << 20)
			| (SBOX[4][(value & 0x000f0000) >>> 16] << 16)
			| (SBOX[3][(value & 0x0000f000) >>> 12] << 12)
			| (SBOX[2][(value & 0x00000f00) >>> 8] << 8)
			| (SBOX[1][(value & 0x000000f0) >>> 4] << 4)
			| (SBOX[0][(value & 0x0000000f)]);
		
		block.writeUInt(value);
		return block;
	}
	
}
