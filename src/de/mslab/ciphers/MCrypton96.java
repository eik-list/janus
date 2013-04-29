package de.mslab.ciphers;


public class MCrypton96 extends MCrypton {
	
	private static final String NAME = "mCrypton96";
	public static final int NUM_BYTES_IN_96_BIT = 96 / Byte.SIZE;
	
	public MCrypton96() {
		super();
		keySize = NUM_BYTES_IN_96_BIT;
		expandedKeySize = (numRounds + 1) * keySize;
		num16BitWordsInKey = keySize / 2;
		name = NAME;
	}
	
	protected void invertTransformKeyRegister(int[] K, int[] U, int round) {
		int temp = U[0];
		U[0] = ((U[1] >> 3) & 0x1FFF) | ((U[1] << 13) & 0xE000);
		U[1] = U[2];
		U[2] = U[3];
		U[3] = ((U[4] >> 8) & 0x00FF) | ((temp << 8) & 0xFF00);
		U[4] = U[5];
		U[5] = temp;
		
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
		
		int temp = U[5];
		U[5] = U[4];
		U[4] = ((U[3] << 8) & 0xFF00) | ((U[3] >> 8) & 0x00FF);
		U[3] = U[2];
		U[2] = U[1];
		U[1] = ((U[0] << 3) & 0xFFF8) | ((U[0] >> 13) & 0x0007);
		U[0] = temp;
	}
	
}
