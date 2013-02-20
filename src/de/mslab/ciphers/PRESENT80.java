package de.mslab.ciphers;

import de.mslab.core.ByteArray;


public class PRESENT80 extends PRESENT {
	
	public static final int NUM_BYTES_IN_64_BITS = 64 / Byte.SIZE;
	public static final int NUM_BYTES_IN_80_BITS = 80 / Byte.SIZE;
	
	public PRESENT80() {
		super();
		name = "PRESENT80";
		this.stateSize = NUM_BYTES_IN_64_BITS;
		this.keySize = NUM_BYTES_IN_80_BITS;
	}

	public int getNumActiveComponentsInKeySchedule() {
		return numRounds;
	}
	
	protected ByteArray expandKeyBackwards(ByteArray expandedKey, ByteArray key, int round) {
		int positionInExpandedKey = (round - 1) * keySize;
		int v1, v2; // temp vars
		ByteArray tempKey = new ByteArray(keySize);
		round--;
		
        for (; round >= 1; --round) {
        	// 1. XOR the round
        	// [k19k18k17k16k15] = [k19k18k17k16k15] ^ round
        	// --------------------------------------------------
        	// We model K[7] = ....k19k18k17k16 ^ (round >> 1) & 0xF
        	// We model K[8] = k15....... 		^ (round & 1) << 7
        	// k79 -> k71 -> k63 -> k55 -> k47 -> k39 -> k31 -> k23 -> k15 -> k7
        	v1 = key.get(7); // k23 .. k16
        	v1 ^= (round >> 1) & 0xF;
        	key.set(7, v1);
        	
        	v2 = key.get(8);
        	v2 ^= (round & 1) << 7;
        	key.set(8, v2);
        	
        	// 2. INVERSE_SBOX the four MSBs
        	// [k79k78k77k76] = INVERSE_SBOX[k79k78k77k76]
        	v1 = key.get(0);
        	v1 = (INVERSE_SBOX[(v1 >> 4)] << 4) | (v1 & 0xF);
        	key.set(0, v1);
        	
        	// 3. key <<<= 19 
        	// [k79k78 . . . k1k0] = [k18k17 . . . k20k19]
        	// --------------------------------------------------
        	// OLD = 10011100 11111100 11100000 00000000 00000000 00000000 00000000 00000000 00011111 11100000
        	// NEW = 00000000 00000000 00000000 00000000 00000000 00000000 11111111 00000100 11100111 11100111
        	// NEW[0] = (K[2] << 5) | (K[3] >> 3)
        	// NEW[1] = (K[3] << 5) | (K[4] >> 3)
        	// NEW[2] = (K[4] << 5) | (K[5] >> 3)
        	for (int i = 0; i < keySize; i++) { // 0 .. 9
        		v1 = (key.get((i + 2) % keySize) & 0x1F) << 3;
        		v2 = (key.get((i + 3) % keySize) & 0xE0) >> 5;
        		tempKey.set(i, v1 | v2);
			}
        	
        	for (int i = 0; i < keySize; i++) {
				key.set(i, tempKey.get(i));
			}
        	
        	// Extract key
        	positionInExpandedKey -= keySize;
        	expandedKey.copyBytes(key, 0, positionInExpandedKey, keySize);
        }
        
		return expandedKey;
	}
	
	protected ByteArray expandKeyForwards(ByteArray expandedKey, ByteArray key, int round) { 
		int positionInExpandedKey = (round - 1) * keySize;
		int v1, v2; // temp vars
		ByteArray tempKey = new ByteArray(keySize);
		
		expandedKey.copyBytes(key, 0, positionInExpandedKey, keySize);
		positionInExpandedKey += keySize;
		
        for (; round <= numRounds; ++round) {
        	// 1. key <<<= 61 
        	// [k79k78 . . . k1k0] = [k18k17 . . . k20k19]
        	// --------------------------------------------------
        	// OLD = 00000000 00000000 00000000 00000000 00000000 00000000 11111111 00000100 11100111 11100111
        	// NEW = 10011100 11111100 11100000 00000000 00000000 00000000 00000000 00000000 00011111 11100000
        	// NEW[0] = (K[7] << 5) | (K[8] >> 3)
        	// NEW[1] = (K[8] << 5) | (K[9] >> 3)
        	// NEW[2] = (K[9] << 5) | (K[0] >> 3)
        	for (int i = 0; i < keySize; i++) { // 0 .. 9
        		v1 = (key.get((i + 7) % keySize) & 0x7) << 5;
        		v2 = (key.get((i + 8) % keySize) & 0xF8) >> 3;
        		tempKey.set(i, v1 | v2);
			}
        	
        	for (int i = 0; i < keySize; i++) {
				key.set(i, tempKey.get(i));
			}
        	
        	// 2. SBOX the four MSBs
        	// [k79k78k77k76] = SBOX[k79k78k77k76]
        	v1 = key.get(0);
        	v1 = (SBOX[(v1 >> 4)] << 4) | (v1 & 0xF);
        	key.set(0, v1);
        	
        	// 3. XOR the round
        	// [k19k18k17k16k15] = [k19k18k17k16k15] ^ round
        	// --------------------------------------------------
        	// We model K[7] = ....k19k18k17k16 ^ (round >> 1) & 0xF
        	// We model K[8] = k15....... 		^ (round & 1) << 7
        	// k79 -> k71 -> k63 -> k55 -> k47 -> k39 -> k31 -> k23 -> k15 -> k7
        	v1 = key.get(7); // k23 .. k16
        	v1 ^= (round >> 1) & 0xF;
        	key.set(7, v1);
        	
        	v2 = key.get(8);
        	v2 ^= (round & 1) << 7;
        	key.set(8, v2);
        	
        	// Extract key
        	expandedKey.copyBytes(key, 0, positionInExpandedKey, keySize);
        	positionInExpandedKey += keySize;
        }
        
        return expandedKey;
	}
	
}
