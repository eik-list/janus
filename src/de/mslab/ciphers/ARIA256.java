package de.mslab.ciphers;

public class ARIA256 extends ARIA {
	
	public static final int NUM_ROUNDS = 16;
	public static final int NUM_BYTES_IN_256_BIT = 256 / Byte.SIZE;
	
	public ARIA256() {
		super();
		name = "ARIA256";
		numRounds = NUM_ROUNDS;
		keySize = NUM_BYTES_IN_256_BIT; 
		
		keyExpansionConstant1 = C3;
		keyExpansionConstant2 = C1;
		keyExpansionConstant3 = C2;
	}
	
}
