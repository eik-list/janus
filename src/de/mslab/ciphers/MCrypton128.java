package de.mslab.ciphers;


public class MCrypton128 extends MCrypton {
	
	private static final String NAME = "mCrypton128";
	public static final int NUM_BYTES_IN_128_BIT = 128 / Byte.SIZE;
	
	public MCrypton128() {
		super();
		keySize = NUM_BYTES_IN_128_BIT;
		num16BitWordsInKey = keySize / 2;
		expandedKeySize = (numRounds + 1) * keySize;
		name = NAME;
	}

	protected void invertTransformKeyRegister(int[] K, int[] U, int round) {
		int temp0 = U[0];
		int temp1 = U[1];
		int temp2 = U[2];
		U[0] = ((U[3] >> 3) & 0x1FFF) | ((U[3] << 13) & 0xE000);
		U[1] = U[4];
		U[2] = U[5];
		U[3] = U[6];
		U[4] = ((U[7] << 8) & 0xFF00) | ((U[7] >> 8) & 0x00FF);
		U[5] = temp0;
		U[6] = temp1;
		U[7] = temp2;
		
		int T = (SBOXES[0][(U[0] >> 12) & 0xF] << 12) 
			| (SBOXES[0][(U[0] >> 8) & 0xF] << 8)
			| (SBOXES[0][(U[0] >> 4) & 0xF] << 4)
			| (SBOXES[0][U[0] & 0xF]);
		T ^= CONSTANTS[round];
		
		K[0] = U[1] ^ (T & 0xF000);
		K[1] = U[2] ^ (T & 0x0F00);
		K[2] = U[3] ^ (T & 0x00F0);
		K[3] = U[4] ^ (T & 0x000F);
	}
	
	protected void transformKeyRegister(int[] K, int[] U, int round) {
		int T = (SBOXES[0][(U[0] >> 12) & 0xF] << 12) 
			| (SBOXES[0][(U[0] >> 8) & 0xF] << 8)
			| (SBOXES[0][(U[0] >> 4) & 0xF] << 4)
			| (SBOXES[0][U[0] & 0xF]);
		T ^= CONSTANTS[round];
		
		K[0] = U[1] ^ (T & 0xF000);
		K[1] = U[2] ^ (T & 0x0F00);
		K[2] = U[3] ^ (T & 0x00F0);
		K[3] = U[4] ^ (T & 0x000F);
		
		int temp5 = U[5];
		int temp6 = U[6];
		int temp7 = U[7];
		U[7] = ((U[4] << 8) & 0xFF00) | ((U[4] >> 8) & 0x00FF);
		U[6] = U[3];
		U[5] = U[2];
		U[4] = U[1];
		U[3] = ((U[0] << 3) & 0xFFF8) | ((U[4] >> 13) & 0x0007);
		U[2] = temp7;
		U[1] = temp6;
		U[0] = temp5;
	}
	
}
