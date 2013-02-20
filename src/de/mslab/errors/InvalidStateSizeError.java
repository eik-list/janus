package de.mslab.errors;

/**
 * Thrown if a cipher is given a message block with another size than the state size of that cipher. 
 * 
 */
public class InvalidStateSizeError extends Error {
	
	private static final long serialVersionUID = 10348502L;
	
	private String message;
	
	public InvalidStateSizeError(int invalidStateSize) {
		this.message = "Invalid state size: " + invalidStateSize;
	}
	
	public InvalidStateSizeError(int invalidStateSize, int correctStateSize) {
		this.message = "Invalid state size: " + invalidStateSize + ". Should be: " + correctStateSize + ".";
	}
	
	public String getMessage() {
		return this.message;
	}
	
}
