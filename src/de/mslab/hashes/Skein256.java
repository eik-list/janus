package de.mslab.hashes;

import de.mslab.ciphers.ThreeFish256;

public class Skein256 extends Skein {
	
	public Skein256() {
		this.cipher = new ThreeFish256();
		this.initialValue = ThreeFish256.INITIAL_VALUE;
	}
	
}
