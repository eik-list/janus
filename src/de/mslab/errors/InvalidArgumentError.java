package de.mslab.errors;

/**
 * Indicates that a parameter for the maximum hamming weight was invalid, 
 * for instance if the maximum hamming weight limit of a value is higher than the 
 * number of bits in the value. Hence, the user is returned an error to prevent
 * a wrong
 * 
 */
public class InvalidArgumentError extends Error {
	
	private static final long serialVersionUID = -7503111651104238830L;

	public InvalidArgumentError(String message) {
		super(message);
	}
	
}
