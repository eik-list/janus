package de.mslab.errors;

/**
 * Thrown if a cipher which does not require a key is given a key. 
 * 
 */
public class NoKeyRequiredError extends Error {
	
	private static final String NO_KEY_REQUIRED_MESSAGE = "No key required for this cipher or permutation";
	private static final long serialVersionUID = 17748502L;
	
	public NoKeyRequiredError() {
		super(NO_KEY_REQUIRED_MESSAGE);
	}
	
}
