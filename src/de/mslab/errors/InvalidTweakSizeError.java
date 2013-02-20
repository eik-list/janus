package de.mslab.errors;

/**
 * Thrown if a cipher is given a secret Tweak with an invalid size.
 * 
 */
public class InvalidTweakSizeError extends Error {
	
	private static final long serialVersionUID = 10348501L;
	
	private String message;
	
	public InvalidTweakSizeError(int invalidTweakSize) {
		super();
		this.message = "Invalid tweak size: " + invalidTweakSize;
	}
	
	public InvalidTweakSizeError(int invalidTweakSize, int correctTweakSize) {
		this.message = "Invalid tweak size: " + invalidTweakSize + ". The correct size is " + correctTweakSize;
	}
	
	public String getMessage() {
		return this.message;
	}
	
}
