package de.mslab.ciphers;

public class ARIA128 extends ARIA {
	
	public static final int NUM_ROUNDS = 12;
	public static final int NUM_BYTES_IN_128_BIT = 128 / Byte.SIZE;
	
	public ARIA128() {
		super();
		name = "ARIA128";
		numRounds = NUM_ROUNDS;
		keySize = NUM_BYTES_IN_128_BIT; 
		
		keyExpansionConstant1 = C1;
		keyExpansionConstant2 = C2;
		keyExpansionConstant3 = C3;
	}
	
}
