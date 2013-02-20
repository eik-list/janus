package de.mslab.errors;

import java.util.Arrays;

/**
 * Thrown if a cipher is given a secret key with an invalid size.
 * 
 */
public class InvalidKeySizeError extends Error {
	
	private static final long serialVersionUID = 10348501L;
	
	private String message;
	
	public InvalidKeySizeError(int invalidKeySize) {
		super();
		this.message = "Invalid key size: " + invalidKeySize;
	}
	
	public InvalidKeySizeError(int invalidKeySize, int[] correctKeySizes) {
		String message = "Invalid key size: " + invalidKeySize;
		
		if (correctKeySizes != null) {
			message += ". Correct sizes are " + Arrays.toString(correctKeySizes);
		}
		
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
}
