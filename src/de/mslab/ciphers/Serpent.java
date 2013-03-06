package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidArgumentError;
import de.mslab.errors.InvalidKeySizeError;

public class Serpent extends AbstractRoundBasedBlockCipher {
	
	public static final String NAME = "Serpent";
	public static final int NUM_ROUNDS = 32;
	
	private static final int NUM_BYTES_IN_256_BITS = 256 / Byte.SIZE;
	private static final int NUM_BYTES_IN_128_BITS = 128 / Byte.SIZE;
	private static final int PHI = 0x9e3779b9;
	private static final int EXPANDED_KEY_SIZE = (NUM_ROUNDS + 1) * NUM_BYTES_IN_128_BITS;
	
	private static final int[][] SBOX = {
		{ 3, 8, 15, 1, 10, 6, 5, 11, 14, 13, 4, 2, 7, 0, 9, 12 },  
		{ 15, 12, 2, 7, 9, 0, 5, 10, 1, 11, 14, 8, 6, 13, 3, 4 },  
		{ 8, 6, 7, 9, 3, 12, 10, 15, 13, 1, 14, 4, 0, 11, 5, 2 },  
		{ 0, 15, 11, 8, 12, 9, 6, 3, 13, 1, 2, 4, 10, 7, 5, 14 },  
		{ 1, 15, 8, 3, 12, 0, 11, 6, 2, 5, 4, 10, 9, 14, 7, 13 },  
		{ 15, 5, 2, 11, 4, 10, 9, 12, 0, 3, 14, 8, 13, 6, 7, 1 },  
		{ 7, 2, 12, 5, 8, 4, 6, 11, 14, 9, 1, 15, 13, 3, 10, 0 },  
		{ 1, 13, 15, 0, 14, 8, 2, 11, 7, 4, 12, 10, 9, 3, 5, 6 }
	};
	private static final int[][] INVERSE_SBOX = {
		{ 13, 3, 11, 0, 10, 6, 5, 12, 1, 14, 4, 7, 15, 9, 8, 2 },  
		{ 5, 8, 2, 14, 15, 6, 12, 3, 11, 4, 7, 9, 1, 13, 10, 0 },  
		{ 12, 9, 15, 4, 11, 14, 1, 2, 0, 3, 6, 13, 5, 8, 10, 7 },  
		{ 0, 9, 10, 7, 11, 14, 6, 13, 3, 5, 12, 2, 4, 8, 15, 1 },  
		{ 5, 0, 8, 3, 10, 9, 7, 14, 2, 12, 11, 6, 4, 15, 13, 1 },  
		{ 8, 15, 2, 9, 4, 1, 13, 14, 11, 6, 5, 3, 7, 12, 10, 0 },  
		{ 15, 10, 1, 13, 5, 3, 6, 0, 4, 9, 14, 7, 2, 12, 8, 11 },  
		{ 3, 0, 6, 13, 9, 14, 15, 8, 5, 12, 11, 7, 10, 1, 4, 2 }
	};
	
	private int[] internalExpandedKey;
	
	public Serpent() {
		super();
		keySize = NUM_BYTES_IN_256_BITS;
		name = NAME;
		numRounds = NUM_ROUNDS;
		stateSize = NUM_BYTES_IN_128_BITS;
	}
	
	public boolean canInvertKeySchedule() {
		return true;
	}
	
	/**
	 * Serpent requires 256 bits = 32 bytes (two consecutive round keys) of material to reconstruct
	 * the expanded key.
	 * @param keyPart 256-bit array with two consecutive round keys for (round) and (round + 1)
	 * @param round The round to which the first key in the key part belongs. Can be 1 to numRounds.  
	 * @throws InvalidKeySizeError if the length of the given keyPart is not 32 bytes. 
	 * @throws InvalidArgumentError if the round is less than 1 or more than numRounds. 
	 */
	public ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		if (round < 1 || round > numRounds) {
			throw new InvalidArgumentError("Given invalid round: " + round);
		}
		
		if (keyPart.length() != NUM_BYTES_IN_256_BITS) {
			throw new InvalidKeySizeError(keyPart.length());
		}
		
		final int numWords = 4 * (numRounds + 1); // 33 * 4 = 132 words
        final int numKeyWords = keyPart.length() / 4; // 32 / 4 = 8 int words
		final int[] expandedKey = new int[numWords];
		int i = (round - 1) * 4;
		
		// Fill the two round keys into the array
		for (int j = 0; j < numKeyWords; j++, i++) {
			expandedKey[i] = keyPart.readUInt(4 * j);
		}
		
		invertSBoxOnKeyPart(expandedKey, round);
		
		// Compute in forward direction
		// If round = 31, 
		if (round < numRounds) { 
			computeNextRoundKeys(expandedKey, round + 2, numWords);
		}
		
		// Compute in backward direction
		if (round > 1) {
			computePreviousRoundKeys(expandedKey, round - 1, numWords);
		}
		
        applySBoxToKeyWords(expandedKey);
		
		final ByteArray result = new ByteArray(EXPANDED_KEY_SIZE);
		result.writeUInts(expandedKey);
		return result;
	}
	
	private void applySBoxToKeyWords(int[] words) {
		// Apply the Sbox bitwise
        // w[0..3] = S_3(w[0..3])
        // w[4..7] = S_2(w[4..7])
        // w[8..11] = S_1(w[8..11])
        // w[12..15] = S_0(w[12..15])
        // w[16..19] = S_7(w[16..19])
        // ...
        int[] x = new int[4];
        int[] y = new int[4];
        int[] whichSBox;
        int sboxOutput, bit;
        final int LAST_BIT = 1;
        
        // w0 = ..1.....
        // w1 = ..1.....
        // w2 = ..0....
        // w3 = ..1.....
        // lead to sboxOutput = S_3(1011) = 0100 which leads to 
        // w0 = ..0.....
        // w1 = ..0.....
        // w2 = ..1.....
        // w3 = ..0.....
        for (int i = 0; i < numRounds + 1; i++) {
            x[0] = words[4 * i    ];
            x[1] = words[4 * i + 1];
            x[2] = words[4 * i + 2];
            x[3] = words[4 * i + 3];
            y[0] = 0;
            y[1] = 0;
            y[2] = 0;
            y[3] = 0;
            
            whichSBox = SBOX[(numRounds + 3 - i) % SBOX.length];
            
            for (bit = 0; bit < 32; bit++) {
                sboxOutput = whichSBox[((x[0] >>> bit) & LAST_BIT)
					| ((x[1] >>> bit) & LAST_BIT) << 1
					| ((x[2] >>> bit) & LAST_BIT) << 2
					| ((x[3] >>> bit) & LAST_BIT) << 3];
                y[0] |= ( sboxOutput        & LAST_BIT) << bit;
                y[1] |= ((sboxOutput >>> 1) & LAST_BIT) << bit;
                y[2] |= ((sboxOutput >>> 2) & LAST_BIT) << bit;
                y[3] |= ((sboxOutput >>> 3) & LAST_BIT) << bit;
            }
            
            words[4 * i    ] = y[0];
            words[4 * i + 1] = y[1];
            words[4 * i + 2] = y[2];
            words[4 * i + 3] = y[3];
        }
	}

	private void invertSBoxOnKeyPart(int[] expandedKey, int round) {
		// Invert the S-box
		// Apply the Sbox bitwise
        // w[0..3] = S_3(w[0..3])
        // w[4..7] = S_2(w[4..7])
        // w[8..11] = S_1(w[8..11])
        // w[12..15] = S_0(w[12..15])
        // w[16..19] = S_7(w[16..19])
        // ...
        int[] x = new int[4];
        int[] y = new int[4];
        int[] whichSBox;
        int sboxOutput, bit;
        final int LAST_BIT = 1;
        
        // w0 = ..1.....
        // w1 = ..1.....
        // w2 = ..0....
        // w3 = ..1.....
        // lead to sboxOutput = S_3^{-1}(1011) = 0100 which leads to 
        // w0 = ..0.....
        // w1 = ..0.....
        // w2 = ..1.....
        // w3 = ..0.....
        for (int i = (round - 1); i < (round + 1); i++) {
            x[0] = expandedKey[4 * i    ];
            x[1] = expandedKey[4 * i + 1];
            x[2] = expandedKey[4 * i + 2];
            x[3] = expandedKey[4 * i + 3];
            y[0] = 0;
            y[1] = 0;
            y[2] = 0;
            y[3] = 0;
            
            whichSBox = INVERSE_SBOX[(numRounds + 3 - i) % SBOX.length];
            
            for (bit = 0; bit < 32; bit++) {
                sboxOutput = whichSBox[((x[0] >>> bit) & LAST_BIT)
					| ((x[1] >>> bit) & LAST_BIT) << 1
					| ((x[2] >>> bit) & LAST_BIT) << 2
					| ((x[3] >>> bit) & LAST_BIT) << 3];
                y[0] |= ( sboxOutput        & LAST_BIT) << bit;
                y[1] |= ((sboxOutput >>> 1) & LAST_BIT) << bit;
                y[2] |= ((sboxOutput >>> 2) & LAST_BIT) << bit;
                y[3] |= ((sboxOutput >>> 3) & LAST_BIT) << bit;
            }
            
            expandedKey[4 * i    ] = y[0];
            expandedKey[4 * i + 1] = y[1];
            expandedKey[4 * i + 2] = y[2];
            expandedKey[4 * i + 3] = y[3];
        }
	}
	
	public ByteArray computeKeyPart(ByteArray expandedKey, int round) {
		final int from = (round - 1) * stateSize;
		final int to = (round + 1) * stateSize;
		return expandedKey.splice(from, to);
	}
	
	public ByteArray decryptRounds(ByteArray block, int fromRound, int toRound) {
		final int[] state = blockToInts(block);
		
		if (toRound == numRounds) {
			addRoundKeyWords(toRound + 1, state);
			invertSubByte(toRound, state);
			addRoundKeyWords(toRound, state);
			toRound--;
		} else {
			invertLinearTransformation(state);
			invertSubByte(toRound, state);
			addRoundKeyWords(toRound, state);
			toRound--;
		}
		
		for (int round = toRound; round >= fromRound; round--) {
			invertLinearTransformation(state);
			invertSubByte(round, state);
			addRoundKeyWords(round, state);
		}
		
		return intsToBlock(state);
	}
	
	public ByteArray encryptRounds(ByteArray block, int fromRound, int toRound) {
		final int[] state = blockToInts(block);
		
		for (int round = fromRound; round < toRound; round++) {
			addRoundKeyWords(round, state);
			subByte(round, state);
			linearTransformation(state);
		}
		
		if (toRound == numRounds) {
			addRoundKeyWords(toRound, state);
			subByte(toRound, state);
			addRoundKeyWords(toRound + 1, state);
		} else {
			addRoundKeyWords(toRound, state);
			subByte(toRound, state);
			linearTransformation(state);
		}
		
		return intsToBlock(state);
	}
	
	public int getNumActiveComponentsInEncryption(int numRounds) {
		if (numRounds == this.numRounds) {
			return (numRounds + 1) * 32;
		} else {
			return numRounds * 32;
		}
	}
	
	public ByteArray getRoundKey(int round) {
		int from = (round - 1) * stateSize;
		int to = from + stateSize;
		return secretKey.splice(from, to);
	}
	
	public boolean hasKeyInjectionInRound(int round) {
		return round >= 1 && round <= numRounds + 1;
	}
	
	public boolean injectsKeyAtRoundBegin(int round) {
		return true;
	}
	
	public boolean injectsKeyAtRoundEnd(int round) {
		if (round == numRounds + 1) {
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
		checkKeySize(key.length());
		internalExpandedKey = expandKey(key);
		secretKey = new ByteArray(EXPANDED_KEY_SIZE);
		secretKey.writeUInts(internalExpandedKey);
	}
	
	public void setExpandedKey(ByteArray expandedKey) {
		checkExpandedKeySize(expandedKey.length());
		secretKey = expandedKey.clone();
		internalExpandedKey = secretKey.readUInts();
	}
	
	private void addRoundKeyWords(int round, int[] state) {
		int[] key = getRoundKeyWords(round);
		
		for (int i = 0; i < state.length; i++) {
			state[i] ^= key[i];
		}
	}
	
	private void checkExpandedKeySize(int keyLength) {
		if (keyLength != EXPANDED_KEY_SIZE) {
			throw new InvalidKeySizeError(keyLength, new int[]{ EXPANDED_KEY_SIZE });
		}
	}
	
	private void checkKeySize(int keyLength) {
		if (keyLength < NUM_BYTES_IN_128_BITS || keyLength > NUM_BYTES_IN_256_BITS) {
			throw new InvalidKeySizeError(keyLength);
		}
	}
	
	private void computeNextRoundKeys(int[] words, int round, int numWords) {
        for (int i = (round - 1) * 4; i < numWords; i++) {
            words[i] = words[i-8] ^ words[i-5] ^ words[i-3] ^ words[i-1] ^ PHI ^ i;
            words[i] = Integer.rotateLeft(words[i], 11);
        }
	}
	
	private void computePreviousRoundKeys(int[] words, int round, int numWords) {
        for (int i = (round - 1) * 4 + 3; i > -1; i--) {
            words[i] = Integer.rotateRight(words[i+8], 11) 
            	^ words[i+5] ^ words[i+3] ^ words[i+7] ^ PHI ^ (i + 8);
        }
	}
	
	private int[] expandKey(ByteArray key) {
		final int numWords = 4 * (numRounds + 1);
        final int numKeyWords = key.length() / 4;
        final int[] words = new int[numWords];
        int i, j;
        int offset = 0;
        
        // Fill words with key
        for (i = 0; i < numKeyWords; i++) {
            words[i] = (key.get(offset++) & 0xFF)
            	| (key.get(offset++) & 0xFF) <<  8
            	| (key.get(offset++) & 0xFF) << 16
            	| (key.get(offset++) & 0xFF) << 24;
        }
        
        // Apply 10* padding
        if (i < 8) {
            words[i++] = 1;
        }
        
        // Generate the first eight words w[0..7] in w[8..15]
        for (i = 8, j = 0; i < 16; i++, j++) {
            words[i] = words[j] ^ words[i-5] ^ words[i-3] ^ words[i-1] ^ PHI ^ j;
            words[i] = Integer.rotateLeft(words[i], 11);
        }
        
        // Move the first eight words from w[8..15] to w[0..7]
        for (i = 0, j = 8; i < 8; i++, j++) { 
        	words[i] = words[j];
        }
        
        // Compute the remaining words w[8..131]
        for (i = 8; i < numWords; i++) {
            words[i] = words[i-8] ^ words[i-5] ^ words[i-3] ^ words[i-1] ^ PHI ^ i;
            words[i] = Integer.rotateLeft(words[i], 11);
        }
        
        applySBoxToKeyWords(words);
		return words;
	}
	
	private int[] getRoundKeyWords(int round) {
		int[] key = new int[4];
		int i = (round - 1) * 4;
		
		for (int j = 0; j < 4; j++, i++) {
			key[j] = internalExpandedKey[i];
		}
		
		return key;
	}
	
	private void invertLinearTransformation(int[] x) {
		x[2] = Integer.rotateRight(x[2], 22);
		x[0] = Integer.rotateRight(x[0], 5);
		x[2] = x[2] ^ x[3] ^ (x[1] << 7);
		x[0] = x[0] ^ x[1] ^ x[3];
		x[3] = Integer.rotateRight(x[3], 7);
		x[1] = Integer.rotateRight(x[1], 1);
		x[3] = x[3] ^ x[2] ^ (x[0] << 3);
		x[1] = x[1] ^ x[0] ^ x[2];
		x[2] = Integer.rotateRight(x[2], 3);
		x[0] = Integer.rotateRight(x[0], 13);
	}
	
	private void invertSubByte(int round, int[] x) {
		final int whichSBox = (round - 1) % SBOX.length;
        final int[] y = new int[4];
		int t01, t02, t03, t04, t05, t06, t07, t08, t09, t10;
		int t11, t12, t13, t14, t15, t16, t17, t18;
		
		if (whichSBox == 0) {
			t01 = x[2]  ^ x[3] ;
			t02 = x[0]  | x[1] ;
			t03 = x[1]  | x[2] ;
			t04 = x[2]  & t01;
			t05 = t02 ^ t01;
			t06 = x[0]  | t04;
			y[2]  =     ~ t05;
			t08 = x[1]  ^ x[3] ;
			t09 = t03 & t08;
			t10 = x[3]  | y[2] ;
			y[1]  = t09 ^ t06;
			t12 = x[0]  | t05;
			t13 = y[1]  ^ t12;
			t14 = t03 ^ t10;
			t15 = x[0]  ^ x[2] ;
			y[3]  = t14 ^ t13;
			t17 = t05 & t13;
			t18 = t14 | t17;
			y[0]  = t15 ^ t18;
		} else if (whichSBox == 1) {
			t01 = x[0]  ^ x[1] ;
			t02 = x[1]  | x[3] ;
			t03 = x[0]  & x[2] ;
			t04 = x[2]  ^ t02;
			t05 = x[0]  | t04;
			t06 = t01 & t05;
			t07 = x[3]  | t03;
			t08 = x[1]  ^ t06;
			t09 = t07 ^ t06;
			t10 = t04 | t03;
			t11 = x[3]  & t08;
			y[2]  =     ~ t09;
			y[1]  = t10 ^ t11;
			t14 = x[0]  | y[2] ;
			t15 = t06 ^ y[1] ;
			y[3]  = t01 ^ t04;
			t17 = x[2]  ^ t15;
			y[0]  = t14 ^ t17;
		} else if (whichSBox == 2) {
			t01 = x[0]  ^ x[3] ;
			t02 = x[2]  ^ x[3] ;
			t03 = x[0]  & x[2] ;
			t04 = x[1]  | t02;
			y[0]  = t01 ^ t04;
			t06 = x[0]  | x[2] ;
			t07 = x[3]  | y[0] ;
			t08 =     ~ x[3] ;
			t09 = x[1]  & t06;
			t10 = t08 | t03;
			t11 = x[1]  & t07;
			t12 = t06 & t02;
			y[3]  = t09 ^ t10;
			y[1]  = t12 ^ t11;
			t15 = x[2]  & y[3] ;
			t16 = y[0]  ^ y[1] ;
			t17 = t10 ^ t15;
			y[2]  = t16 ^ t17;
		} else if (whichSBox == 3) {
			t01 = x[2]  | x[3] ;
			t02 = x[0]  | x[3] ;
			t03 = x[2]  ^ t02;
			t04 = x[1]  ^ t02;
			t05 = x[0]  ^ x[3] ;
			t06 = t04 & t03;
			t07 = x[1]  & t01;
			y[2]  = t05 ^ t06;
			t09 = x[0]  ^ t03;
			y[0]  = t07 ^ t03;
			t11 = y[0]  | t05;
			t12 = t09 & t11;
			t13 = x[0]  & y[2] ;
			t14 = t01 ^ t05;
			y[1]  = x[1]  ^ t12;
			t16 = x[1]  | t13;
			y[3]  = t14 ^ t16;
		} else if (whichSBox == 4) {
			t01 = x[1]  | x[3] ;
			t02 = x[2]  | x[3] ;
			t03 = x[0]  & t01;
			t04 = x[1]  ^ t02;
			t05 = x[2]  ^ x[3] ;
			t06 =     ~ t03;
			t07 = x[0]  & t04;
			y[1]  = t05 ^ t07;
			t09 = y[1]  | t06;
			t10 = x[0]  ^ t07;
			t11 = t01 ^ t09;
			t12 = x[3]  ^ t04;
			t13 = x[2]  | t10;
			y[3]  = t03 ^ t12;
			t15 = x[0]  ^ t04;
			y[2]  = t11 ^ t13;
			y[0]  = t15 ^ t09;
		} else if (whichSBox == 5) {
			t01 = x[0]  & x[3] ;
			t02 = x[2]  ^ t01;
			t03 = x[0]  ^ x[3] ;
			t04 = x[1]  & t02;
			t05 = x[0]  & x[2] ;
			y[0]  = t03 ^ t04;
			t07 = x[0]  & y[0] ;
			t08 = t01 ^ y[0] ;
			t09 = x[1]  | t05;
			t10 =     ~ x[1] ;
			y[1]  = t08 ^ t09;
			t12 = t10 | t07;
			t13 = y[0]  | y[1] ;
			y[3]  = t02 ^ t12;
			t15 = t02 ^ t13;
			t16 = x[1]  ^ x[3] ;
			y[2]  = t16 ^ t15;
		} else if (whichSBox == 6) {
			t01 = x[0]  ^ x[2] ;
			t02 =     ~ x[2] ;
			t03 = x[1]  & t01;
			t04 = x[1]  | t02;
			t05 = x[3]  | t03;
			t06 = x[1]  ^ x[3] ;
			t07 = x[0]  & t04;
			t08 = x[0]  | t02;
			t09 = t07 ^ t05;
			y[1]  = t06 ^ t08;
			y[0]  =     ~ t09;
			t12 = x[1]  & y[0] ;
			t13 = t01 & t05;
			t14 = t01 ^ t12;
			t15 = t07 ^ t13;
			t16 = x[3]  | t02;
			t17 = x[0]  ^ y[1] ;
			y[3]  = t17 ^ t15;
			y[2]  = t16 ^ t14;
		} else { // if (whichSBox == 7) {
			t01 = x[0]  & x[1] ;
			t02 = x[0]  | x[1] ;
			t03 = x[2]  | t01;
			t04 = x[3]  & t02;
			y[3]  = t03 ^ t04;
			t06 = x[1]  ^ t04;
			t07 = x[3]  ^ y[3] ;
			t08 =     ~ t07;
			t09 = t06 | t08;
			t10 = x[1]  ^ x[3] ;
			t11 = x[0]  | x[3] ;
			y[1]  = x[0]  ^ t09;
			t13 = x[2]  ^ t06;
			t14 = x[2]  & t11;
			t15 = x[3]  | y[1] ;
			t16 = t01 | t10;
			y[0]  = t13 ^ t15;
			y[2]  = t14 ^ t16;

		}
		
		x[0] = y[0];
		x[1] = y[1];
		x[2] = y[2];
		x[3] = y[3];
	}
	
	private void linearTransformation(int[] x) {
		x[0] = Integer.rotateLeft(x[0], 13);
		x[2] = Integer.rotateLeft(x[2], 3);
		x[1] = x[1] ^ x[0] ^ x[2];
		x[3] = x[3] ^ x[2] ^ (x[0] << 3);
		x[1] = Integer.rotateLeft(x[1], 1);
		x[3] = Integer.rotateLeft(x[3], 7);
		x[0] = x[0] ^ x[1] ^ x[3];
		x[2] = x[2] ^ x[3] ^ (x[1] << 7);
		x[0] = Integer.rotateLeft(x[0], 5);
		x[2] = Integer.rotateLeft(x[2], 22);
	}
	
	private void subByte(int round, int[] x) {
		final int whichSBox = (round - 1) % SBOX.length;
        final int[] y = new int[4];
		int t01, t02, t03, t04, t05, t06, t07, t08, t09, t10;
		int t11, t12, t13, t14, t15, t16, t17, t18;
		
		if (whichSBox == 0) {
			t01 = x[1]  ^ x[2] ;
			t02 = x[0]  | x[3] ;
			t03 = x[0]  ^ x[1] ;
			y[3]  = t02 ^ t01;
			t05 = x[2]  | y[3] ;
			t06 = x[0]  ^ x[3] ;
			t07 = x[1]  | x[2] ;
			t08 = x[3]  & t05;
			t09 = t03 & t07;
			y[2]  = t09 ^ t08;
			t11 = t09 & y[2] ;
			t12 = x[2]  ^ x[3] ;
			t13 = t07 ^ t11;
			t14 = x[1]  & t06;
			t15 = t06 ^ t13;
			y[0]  =     ~ t15;
			t17 = y[0]  ^ t14;
			y[1]  = t12 ^ t17;
		} else if (whichSBox == 1) {
			t01 = x[0]  | x[3] ;
			t02 = x[2]  ^ x[3] ;
			t03 =     ~ x[1] ;
			t04 = x[0]  ^ x[2] ;
			t05 = x[0]  | t03;
			t06 = x[3]  & t04;
			t07 = t01 & t02;
			t08 = x[1]  | t06;
			y[2]  = t02 ^ t05;
			t10 = t07 ^ t08;
			t11 = t01 ^ t10;
			t12 = y[2]  ^ t11;
			t13 = x[1]  & x[3] ;
			y[3]  =     ~ t10;
			y[1]  = t13 ^ t12;
			t16 = t10 | y[1] ;
			t17 = t05 & t16;
			y[0]  = x[2]  ^ t17;
		} else if (whichSBox == 2) {
			t01 = x[0]  | x[2] ;
			t02 = x[0]  ^ x[1] ;
			t03 = x[3]  ^ t01;
			y[0]  = t02 ^ t03;
			t05 = x[2]  ^ y[0] ;
			t06 = x[1]  ^ t05;
			t07 = x[1]  | t05;
			t08 = t01 & t06;
			t09 = t03 ^ t07;
			t10 = t02 | t09;
			y[1]  = t10 ^ t08;
			t12 = x[0]  | x[3] ;
			t13 = t09 ^ y[1] ;
			t14 = x[1]  ^ t13;
			y[3]  =     ~ t09;
			y[2]  = t12 ^ t14;
		} else if (whichSBox == 3) {
			t01 = x[0]  ^ x[2] ;
			t02 = x[0]  | x[3] ;
			t03 = x[0]  & x[3] ;
			t04 = t01 & t02;
			t05 = x[1]  | t03;
			t06 = x[0]  & x[1] ;
			t07 = x[3]  ^ t04;
			t08 = x[2]  | t06;
			t09 = x[1]  ^ t07;
			t10 = x[3]  & t05;
			t11 = t02 ^ t10;
			y[3]  = t08 ^ t09;
			t13 = x[3]  | y[3] ;
			t14 = x[0]  | t07;
			t15 = x[1]  & t13;
			y[2]  = t08 ^ t11;
			y[0]  = t14 ^ t15;
			y[1]  = t05 ^ t04;
		} else if (whichSBox == 4) {
			t01 = x[0]  | x[1] ;
			t02 = x[1]  | x[2] ;
			t03 = x[0]  ^ t02;
			t04 = x[1]  ^ x[3] ;
			t05 = x[3]  | t03;
			t06 = x[3]  & t01;
			y[3]  = t03 ^ t06;
			t08 = y[3]  & t04;
			t09 = t04 & t05;
			t10 = x[2]  ^ t06;
			t11 = x[1]  & x[2] ;
			t12 = t04 ^ t08;
			t13 = t11 | t03;
			t14 = t10 ^ t09;
			t15 = x[0]  & t05;
			t16 = t11 | t12;
			y[2]  = t13 ^ t08;
			y[1]  = t15 ^ t16;
			y[0]  =     ~ t14;
		} else if (whichSBox == 5) {
			t01 = x[1]  ^ x[3] ;
			t02 = x[1]  | x[3] ;
			t03 = x[0]  & t01;
			t04 = x[2]  ^ t02;
			t05 = t03 ^ t04;
			y[0]  =     ~ t05;
			t07 = x[0]  ^ t01;
			t08 = x[3]  | y[0] ;
			t09 = x[1]  | t05;
			t10 = x[3]  ^ t08;
			t11 = x[1]  | t07;
			t12 = t03 | y[0] ;
			t13 = t07 | t10;
			t14 = t01 ^ t11;
			y[2]  = t09 ^ t13;
			y[1]  = t07 ^ t08;
			y[3]  = t12 ^ t14;
		} else if (whichSBox == 6) {
			t01 = x[0]  & x[3] ;
			t02 = x[1]  ^ x[2] ;
			t03 = x[0]  ^ x[3] ;
			t04 = t01 ^ t02;
			t05 = x[1]  | x[2] ;
			y[1]  =     ~ t04;
			t07 = t03 & t05;
			t08 = x[1]  & y[1] ;
			t09 = x[0]  | x[2] ;
			t10 = t07 ^ t08;
			t11 = x[1]  | x[3] ;
			t12 = x[2]  ^ t11;
			t13 = t09 ^ t10;
			y[2]  =     ~ t13;
			t15 = y[1]  & t03;
			y[3]  = t12 ^ t07;
			t17 = x[0]  ^ x[1] ;
			t18 = y[2]  ^ t15;
			y[0]  = t17 ^ t18;
		} else { // if (whichSBox == 7) {
			t01 = x[0]  & x[2] ;
			t02 =     ~ x[3] ;
			t03 = x[0]  & t02;
			t04 = x[1]  | t01;
			t05 = x[0]  & x[1] ;
			t06 = x[2]  ^ t04;
			y[3]  = t03 ^ t06;
			t08 = x[2]  | y[3] ;
			t09 = x[3]  | t05;
			t10 = x[0]  ^ t08;
			t11 = t04 & y[3] ;
			y[1]  = t09 ^ t10;
			t13 = x[1]  ^ y[1] ;
			t14 = t01 ^ y[1] ;
			t15 = x[2]  ^ t05;
			t16 = t11 | t13;
			t17 = t02 | t14;
			y[0]  = t15 ^ t17;
			y[2]  = x[0]  ^ t16;
		}
		
		x[0] = y[0];
		x[1] = y[1];
		x[2] = y[2];
		x[3] = y[3];
	}
	
	private int[] blockToInts(ByteArray block) {
		int[] result = new int[4];
		short[] array = block.getArray();
		int offset = 0;
		int mask = 0xFF;

		for (int i = 0; i < 4; i++) {
			result[i] = (array[offset++] & mask)
				| (array[offset++] & mask) << 8
				| (array[offset++] & mask) << 16
				| (array[offset++] & mask) << 24;
		}
		
        return result;
	}
	
	private ByteArray intsToBlock(int[] x) {
        return new ByteArray(new int[]{
            x[0], x[0] >>> 8, x[0] >>> 16, x[0] >>> 24,
            x[1], x[1] >>> 8, x[1] >>> 16, x[1] >>> 24,
            x[2], x[2] >>> 8, x[2] >>> 16, x[2] >>> 24,
            x[3], x[3] >>> 8, x[3] >>> 16, x[3] >>> 24
        });
	}
	
}


