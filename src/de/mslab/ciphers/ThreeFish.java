package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidKeySizeError;
import de.mslab.errors.InvalidTweakSizeError;

/**
 * Implements the ThreeFish block cipher, which serves as the block cipher used in Skein, 
 * and was designed 2009 by Ferguson, Lucks, Schneier et al.  
 * 
 */
public abstract class ThreeFish extends AbstractRoundBasedBlockCipher implements TweakableCipher {
	
	/**
	 * Constant for round key.
	 */
	protected static final long C_240 = 0x1BD11BDAA9FC1A22L;
	protected static final long MASK_FOR_64_BIT = 0xFFFFFFFFFFFFFFFFL;
	
	protected int[][] mixConstants;
	protected int numRoundsPerKeyInjection = 4;
	protected int[] permuteConstants;
	
	protected long[][] expandedKey;
	protected long[] tweak;
	protected int tweakSize = 128 / Byte.SIZE;

	protected ThreeFish() {
		super();
	}
	
	public ByteArray addRoundKey(int round, ByteArray state) {
		long[] block = addRoundKey(round, state.readLongs());
		return new ByteArray(block);
	}
	
	public boolean canInvertKeySchedule() {
		return false;
	}
	
	public ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		return longArrayToByteArray(expandKey(keyPart));
	}
	
	public ByteArray computeKeyPart(ByteArray expandedKey, int round) {
		return expandedKey.splice(0, keySize);
	}
	
	public ByteArray decryptRounds(ByteArray block, int fromRound, int toRound) {
		long[] state = block.readLongs();
		
		for (int round = toRound; round >= fromRound; round--) {
			if (round % 4 == 0) {
				state = subtractRoundKey(round, state);
			}
			
			state = invertPermute(round, state);
			state = invertMix(round, state);
		}
		
		if (fromRound == 1) { // Whitening
			state = subtractRoundKey(0, state);
		}
		
		return new ByteArray(state);
	}
	
	public ByteArray encryptRounds(ByteArray block, int fromRound, int toRound) {
		long[] state = block.readLongs();
		
		if (fromRound == 1) { // Whitening
			state = addRoundKey(0, state);
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			state = mix(round, state);
			state = permute(round, state);
			
			if (round % 4 == 0) {
				state = addRoundKey(round, state);
			}
		}
		
		return new ByteArray(state);
	}
	
	public int getNumActiveComponentsInEncryption(int numRounds) {
		return numRounds * stateSize / 16;
	}
	
	public int getNumActiveComponentsInKeySchedule() {
		return 0;
	}
	
	public ByteArray getRoundKey(int round) {
		if (hasKeyInjectionInRound(round)) {
			long[] roundKeyWords = getRoundKeyArray(round);
			return new ByteArray(roundKeyWords);
		} else {
			return null;
		}
	}
	
	public long[] getRoundKeyArray(int round) {
		return this.expandedKey[round / numRoundsPerKeyInjection];
	}
	
	public ByteArray getTweak() {
		return new ByteArray(this.tweak);
	}
	
	public int getTweakSize() {
		return tweakSize;
	}
	
	public boolean hasKeyInjectionInRound(int round) {
		if ((round % 4) == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean injectsKeyAtRoundEnd(int round) {
		return true;
	}
	
	public boolean operatesBytewise() {
		return false;
	}
	
	public void setKey(ByteArray key) {
		checkKeyLength(key.length());
		this.expandedKey = expandKey(key);
		this.secretKey = longArrayToByteArray(expandedKey);
	}
	
	private ByteArray longArrayToByteArray(long[][] array) {
		int numRoundKeys = (numRounds / numRoundsPerKeyInjection) + 1; // (72 / 4) + 1 = Round + Whitening
		ByteArray result = new ByteArray(numRoundKeys * keySize);
		int position = 0;
		
		for (int round = 0; round <= numRounds; round++) {
			if (hasKeyInjectionInRound(round)) {
				result.writeLongs(position, array[round / numRoundsPerKeyInjection]);
				position += keySize;
			}
		}
		
		return result;
	}
	
	public void setExpandedKey(ByteArray expandedKey) {
		checkExpandedKeyLength(expandedKey.length());
		this.secretKey = expandedKey.clone();
		
		int numWordsPerKey = (int)((double)keySize * Byte.SIZE / Long.SIZE); // 32 * 8 / 64 = 4 words in 256 bit version
		int numRoundKeys = (numRounds / numRoundsPerKeyInjection) + 1; // (72 / 4) + 1 = Round + Whitening
		this.expandedKey = new long[numRoundKeys][numWordsPerKey]; // long[19][4]
		
		int position = 0;
		
		for (int keyIndex = 0; keyIndex < numRoundKeys; keyIndex++) {
			this.expandedKey[keyIndex] = secretKey.readLongs(position, numWordsPerKey);
			position += keySize;
		}
	}
	
	public void setTweak(ByteArray tweak) {
		if (tweak.length() != tweakSize) {
			throw new InvalidTweakSizeError(tweak.length(), tweakSize);
		}
		
		this.tweak = new long[3];
		this.tweak[0] = tweak.readLong(0);
		this.tweak[1] = tweak.readLong(8);
		this.tweak[2] = this.tweak[0] ^ this.tweak[1];
	}
	
	protected long[] addRoundKey(int round, long[] state) {
		long[] roundKey = getRoundKeyArray(round);
		
		for (int i = 0; i < roundKey.length; i++) {
			state[i] += roundKey[i] & MASK_FOR_64_BIT;
		}
		
		return state;
	}
	
	protected void checkExpandedKeyLength(int length) {
		int numRoundKeys = (numRounds / numRoundsPerKeyInjection) + 1;
		int expandedKeySize = numRoundKeys * keySize;
		
		if (length != expandedKeySize) {
			throw new InvalidKeySizeError(length, new int[]{ expandedKeySize });
		}
	}
	
	protected void checkKeyLength(int length) {
		if (length != keySize) {
			throw new InvalidKeySizeError(length);
		}
	}
	
	protected long[][] expandKey(ByteArray key) {
		long[] keyWords = key.readLongs();
		long[] k = new long[keyWords.length + 1];
		long extraWord = C_240;
		int numRoundsWithAddKey = (numRounds / numRoundsPerKeyInjection) + 1;
		long[][] expandedKeyWords = new long[numRoundsWithAddKey][keyWords.length];
		
		for (int i = 0; i < keyWords.length; i++) {
			extraWord ^= keyWords[i];
			k[i] = keyWords[i];
		}
		
		k[keyWords.length] = extraWord;
		
		for (int s = 0; s < numRoundsWithAddKey; s++) {
			for (int i = 0; i < keyWords.length; i++) {
				int index = (s + i) % (keyWords.length + 1);
				expandedKeyWords[s][i] = k[index];
				
				if (i == keyWords.length - 3) {
					expandedKeyWords[s][i] += tweak[s % 3];
				} else if (i == keyWords.length - 2) {
					expandedKeyWords[s][i] += tweak[(s + 1) % 3];
				} else if (i == keyWords.length - 1) {
					expandedKeyWords[s][i] += s;
				}
			}
		}
		
		return expandedKeyWords;
	}
	
	protected long[] invertMix(int round, long[] state) {
		int pairIndex = 0;
		long x0, x1, y0, y1;
		int rotation;
		
		for (int i = 0; i < state.length; i += 2) {
			y0 = state[i];
			y1 = state[i + 1];
			
			rotation = mixConstants[(round - 1) % 8][pairIndex];
			x1 = Long.rotateRight((y1 ^ y0), rotation);
			x0 = y0 - x1;
			
			state[i] = x0;
			state[i + 1] = x1;
			
			pairIndex++;
		}
		
		return state;
	}
	
	protected long[] invertPermute(int round, long[] state) {
		long[] newState = new long[state.length];
		
		for (int i = 0; i < state.length; i++) {
			newState[permuteConstants[i]] = state[i];
		}
		
		return newState;
	}
	
	protected long[] mix(int round, long[] state) {
		int pairIndex = 0;
		long x0, x1, y0, y1;
		int rotation;
		
		for (int i = 0; i < state.length; i += 2) {
			x0 = state[i];
			x1 = state[i + 1];
			
			rotation = mixConstants[(round - 1) % 8][pairIndex];
			y0 = (x0 + x1) & MASK_FOR_64_BIT;
			y1 = Long.rotateLeft(x1, rotation) ^ y0;
			
			state[i] = y0;
			state[i + 1] = y1;
			
			pairIndex++;
		}
		
		return state;
	}
	
	protected long[] permute(int round, long[] state) {
		long[] newState = new long[state.length];
		
		for (int i = 0; i < state.length; i++) {
			newState[i] = state[permuteConstants[i]];
		}
		
		return newState;
	}

	protected long[] subtractRoundKey(int round, long[] state) {
		long[] roundKey = getRoundKeyArray(round);
		
		for (int i = 0; i < roundKey.length; i++) {
			state[i] -= roundKey[i] & MASK_FOR_64_BIT;
		}
		
		return state;
	}
	
}
