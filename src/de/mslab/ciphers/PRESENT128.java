package de.mslab.ciphers;

import de.mslab.core.ByteArray;


public class PRESENT128 extends PRESENT {
	
	public static final int NUM_BYTES_IN_64_BITS = 64 / Byte.SIZE;
	public static final int NUM_BYTES_IN_128_BITS = 128 / Byte.SIZE;
	
	public PRESENT128() {
		super();
		name = "PRESENT128";
		this.stateSize = NUM_BYTES_IN_64_BITS;
		this.keySize = NUM_BYTES_IN_128_BITS;
	}
	
	public int getNumActiveComponentsInKeySchedule() {
		return 2 * numRounds;
	}
	
	protected ByteArray expandKeyBackwards(ByteArray expandedKey, ByteArray key, int round) {
		long[] k = key.readLongs(); // k.length = 2
		long[] temp = new long[2];
		long value;
		long mask60 = 0x0FFFFFFFFFFFFFFFL;
		long mask56 = 0xF0FFFFFFFFFFFFFFL;
		
		int positionInExpandedKey = (round - 1) * keySize;
		round--;
		
        for (; round >= 1; --round) {
        	// 4. k[66k65k64k63k62] = k[66k65k64k63k62] ^ round_counter
        	value = (long)round;
			k[0] ^= (value & 0x1c) >>> 2;
			k[1] ^= (value & 0x3) << 62;
			
			// 3. k[123k122k121k120] = S[k[123k122k121k120]]
			value = (k[0] >>> 56) & 0xF;
			value = (long)INVERSE_SBOX[(int)value];
			k[0] &= mask56;
			k[0] |= value << 56;
			
			// 2. k[127k126k125k124] = S[k[127k126k125k124]]
			value = (k[0] >>> 60) & 0xF;
			value = (long)INVERSE_SBOX[(int)value];
			k[0] &= mask60;
			k[0] |= value << 60;
			
        	// 1. k <<<= 61
			// [k127k126...k1k0] = [k66k65..k68k67]
			temp[0] = (k[0] >>> 61) | (k[1] << 3);
			temp[1] = (k[1] >>> 61) | (k[0] << 3);
			k[0] = temp[0];
			k[1] = temp[1];
			
        	// Extract key
        	positionInExpandedKey -= keySize;
			expandedKey.writeLong(positionInExpandedKey, k[0]);
			expandedKey.writeLong(positionInExpandedKey + (keySize / 2), k[1]);
        }
        
		return secretKey;
	}
	
	protected ByteArray expandKeyForwards(ByteArray expandedKey, ByteArray key, int round) {
		long[] k = key.readLongs(); // k.length = 2
		long[] temp = new long[2];
		long value;
		long mask60 = 0x0FFFFFFFFFFFFFFFL;
		long mask56 = 0xF0FFFFFFFFFFFFFFL;
		
		int positionInExpandedKey = (round - 1) * keySize;
		expandedKey.copyBytes(key, 0, positionInExpandedKey, keySize);
		positionInExpandedKey += keySize;
		
		for (; round <= numRounds; round++) {
			// 1. k <<<= 61
			// [k127k126...k1k0] = [k66k65..k68k67]
			temp[0] = (k[0] << 61) | (k[1] >>> 3);
			temp[1] = (k[1] << 61) | (k[0] >>> 3);
			k[0] = temp[0];
			k[1] = temp[1];
			
			// 2. k[127k126k125k124] = S[k[127k126k125k124]]
			value = (k[0] >>> 60) & 0xF;
			value = (long)SBOX[(int)value];
			k[0] &= mask60;
			k[0] |= value << 60;
			
			// 3. k[123k122k121k120] = S[k[123k122k121k120]]
			value = (k[0] >>> 56) & 0xF;
			value = (long)SBOX[(int)value];
			k[0] &= mask56;
			k[0] |= value << 56;
			
			// 4. k[66k65k64k63k62] = k[66k65k64k63k62] ^ round_counter
			value = (long)round;
			k[0] ^= (value & 0x1c) >>> 2;
			k[1] ^= (value & 0x3) << 62;
			
        	// Extract key
			expandedKey.writeLong(positionInExpandedKey, k[0]);
			expandedKey.writeLong(positionInExpandedKey + (keySize / 2), k[1]);
			positionInExpandedKey += keySize;
		}
		
        return expandedKey;
	}
	
}
