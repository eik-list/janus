package de.mslab.errors;

/**
 * Thrown by the <code>encrypt</code> or <code>decrypt</code> methods of block ciphers,
 * in case the cipher has not been initialized with a secret key before.  
 * 
 */
public class NoKeySpecifiedError extends Error {
	
	private static final String NO_KEY_SPECIFIED_MESSAGE = "No key specified.";
	private static final long serialVersionUID = 10348502L;
	
	public NoKeySpecifiedError() {
		super(NO_KEY_SPECIFIED_MESSAGE);
	}
	
}
