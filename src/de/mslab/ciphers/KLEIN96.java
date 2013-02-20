package de.mslab.ciphers;

public class KLEIN96 extends KLEIN {
	
	public static final int NUM_BYTES_IN_96_BIT = 96 / Byte.SIZE;
	public static final int NUM_ROUNDS = 20;
	
	public KLEIN96() {
		super();
		stateSize = NUM_BYTES_IN_64_BIT;
		keySize = NUM_BYTES_IN_96_BIT;
		numRounds = NUM_ROUNDS;
		name = "KLEIN96";
	}
	
}
