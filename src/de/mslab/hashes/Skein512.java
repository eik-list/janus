package de.mslab.hashes;

import de.mslab.ciphers.ThreeFish512;

public class Skein512 extends Skein {
	
	public Skein512() {
		this.cipher = new ThreeFish512();
		this.initialValue = ThreeFish512.INITIAL_VALUE;
	}
	
}
