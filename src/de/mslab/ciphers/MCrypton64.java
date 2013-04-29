package de.mslab.ciphers;



public class MCrypton64 extends MCrypton {
	
	private static final String NAME = "mCrypton64";
	
	public MCrypton64() {
		super();
		keySize = NUM_BYTES_IN_64_BIT;
		num16BitWordsInKey = keySize / 2;
		expandedKeySize = (numRounds + 1) * keySize;
		name = NAME;
	}
	
	protected void invertTransformKeyRegister(int[] K, int[] U, int round) {
		int temp = U[3];
		U[3] = U[2];
		U[2] = U[1];
		U[1] = U[0];
		U[0] = ((temp >> 3) & 0x1FFF) | ((temp << 13) & 0xE000);
		
		int T = (SBOXES[0][(U[0] >> 12) & 0xF] << 12) 
			| (SBOXES[0][(U[0] >> 8) & 0xF] << 8)
			| (SBOXES[0][(U[0] >> 4) & 0xF] << 4)
			| (SBOXES[0][U[0] & 0xF]);
		T ^= CONSTANTS[round];
		
		K[0] = U[1] ^ (T & 0xF000);
		K[1] = U[2] ^ (T & 0x0F00);
		K[2] = U[3] ^ (T & 0x00F0);
		K[3] = U[0] ^ (T & 0x000F);
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
		K[3] = U[0] ^ (T & 0x000F);
		
		int temp = U[0];
		U[0] = U[1];
		U[1] = U[2];
		U[2] = U[3];
		U[3] = ((temp << 3) & 0xFFF8) | ((temp >> 13) & 0x0007);
	}
	
}
