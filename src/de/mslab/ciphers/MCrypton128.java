package de.mslab.ciphers;

import de.mslab.core.ByteArray;

public class MCrypton128 extends MCrypton {
	
	private static final String NAME = "mCrypton128";
	public static final int NUM_BYTES_IN_128_BIT = 128 / Byte.SIZE;
	
	public MCrypton128() {
		super();
		keySize = NUM_BYTES_IN_128_BIT;
		name = NAME;
	}
	
	public ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		checkKeyLength(keyPart.length());
		
		// U = (U[5], U[6], U[7], U[0] <<< 3, U[1], U[2], U[3], U[4] <<< 8)
		// Inverted: U = (U[3] >>> 3, U[4], U[5], U[6], U[7] >>> 8, U[0], U[1], U[2])
		if (round > 0) {
			final int[] U = new int[keySize / 2];
			int temp1, temp2;
			
			for (int i = 0; i < keySize / 2; i++) {
				U[i] = (keyPart.get(2 * i) << 8) | (keyPart.get(2 * i + 1));
			}
			
			for (int i = round; i > 0; i--) {
				temp1 = ((U[3] >> 3) & 0x1FFF) | ((U[3] << 13) & 0xE000);
				temp2 = ((U[7] >> 8) & 0x00FF) | ((U[7] << 8) & 0xFF00);
				U[7] = U[2];
				U[3] = U[6];
				U[6] = U[1];
				U[2] = U[5];
				U[5] = U[0];
				U[1] = U[4];
				U[4] = temp2;
				U[0] = temp1;
			}
			
			for (int i = 0; i < keySize / 2; i++) {
				keyPart.set(2 * i, (U[i] >> 8) & 0xFF);
				keyPart.set(2 * i + 1, U[i] & 0xFF);
			}
		}
		
		return expandKey(keyPart);
	}
	
	protected ByteArray expandKey(ByteArray key) {
		final int[] U = new int[keySize / 2];
		final int[] temp = new int[keySize / 2];
		final int[] K = new int[stateSize / 2];
		int T = 0;
		ByteArray expandedKey = new ByteArray(EXPANDED_KEY_LENGTH);
		
		for (int i = 0; i < keySize / 2; i++) {
			U[i] = (key.get(2 * i) << 8) | (key.get(2 * i + 1));
		}
		
		for (int round = 0; round <= numRounds; round++) {
			T = (SBOXES[0][(U[0] >> 12) & 0xF] << 12) 
				| (SBOXES[0][(U[0] >> 8) & 0xF] << 8)
				| (SBOXES[0][(U[0] >> 4) & 0xF] << 4)
				| (SBOXES[0][U[0] & 0xF]);
			T ^= CONSTANTS[round];
			
			K[0] = U[1] ^ (T & 0xF000);
			K[1] = U[2] ^ (T & 0x0F00);
			K[2] = U[3] ^ (T & 0x00F0);
			K[3] = U[4] ^ (T & 0x000F);
			
			for (int i = 0; i < stateSize / 2; i++) {
				expandedKey.set(round * stateSize + 2 * i, (K[i] >> 8) & 0xFF);
				expandedKey.set(round * stateSize + 2 * i + 1, K[i] & 0xFF);
			}
			
			for (int i = 0; i < U.length; i++) {
				temp[i] = U[i];
			}
			
			U[0] = temp[5];
			U[1] = temp[6];
			U[2] = temp[7];
			U[3] = ((temp[0] << 3) & 0xFFF8) | ((temp[0] >> 13) & 0x0007);
			U[4] = temp[1];
			U[5] = temp[2];
			U[6] = temp[3];
			U[7] = ((temp[4] << 8) & 0xFF00) | ((temp[4] >> 8) & 0x00FF);
		}
		
		return expandedKey;
	}
	
}
