package de.mslab.ciphers;

public class KLEIN80 extends KLEIN {
	
	public static final int NUM_BYTES_IN_80_BIT = 80 / Byte.SIZE;
	public static final int NUM_ROUNDS = 16;
	
	public KLEIN80() {
		super();
		stateSize = NUM_BYTES_IN_64_BIT;
		keySize = NUM_BYTES_IN_80_BIT;
		numRounds = NUM_ROUNDS;
		name = "KLEIN80";
	}
	
}
