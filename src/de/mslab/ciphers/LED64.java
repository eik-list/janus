package de.mslab.ciphers;



public class LED64 extends LED {
	
	public LED64() {
		super();
		stateSize = 8;
		keySize = 8;
		numRounds = 32;
		name = "LED64";
	}
	
}
