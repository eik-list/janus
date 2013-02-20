package de.mslab.ciphers;

public class KLEIN64 extends KLEIN {
	
	public static final int NUM_ROUNDS = 12;
	
	public KLEIN64() {
		super();
		stateSize = NUM_BYTES_IN_64_BIT;
		keySize = NUM_BYTES_IN_64_BIT;
		numRounds = NUM_ROUNDS;
		name = "KLEIN64";
	}
	
}
