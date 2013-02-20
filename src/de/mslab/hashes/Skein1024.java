package de.mslab.hashes;

import de.mslab.ciphers.ThreeFish1024;

public class Skein1024 extends Skein {
	
	public Skein1024() {
		this.cipher = new ThreeFish1024();
		this.initialValue = ThreeFish1024.INITIAL_VALUE;
	}
	
}
