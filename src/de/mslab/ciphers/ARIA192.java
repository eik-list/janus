package de.mslab.ciphers;


public class ARIA192 extends ARIA {
	
	public static final int NUM_ROUNDS = 14;
	public static final int NUM_BYTES_IN_192_BIT = 192 / Byte.SIZE;
	
	public ARIA192() {
		super();
		name = "ARIA192";
		numRounds = NUM_ROUNDS;
		keySize = NUM_BYTES_IN_192_BIT; 
		
		keyExpansionConstant1 = C2;
		keyExpansionConstant2 = C3;
		keyExpansionConstant3 = C1;
	}
	
}
