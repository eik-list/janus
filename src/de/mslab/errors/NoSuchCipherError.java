package de.mslab.errors;

import de.mslab.applications.BicliqueFinderApplication;
import de.mslab.ciphers.CipherFactory.CipherName;

/**
 * Thrown in case there is no cipher in the {@link CipherName} enumeration with the proposed name.
 * For instance, this exception is thrown by the {@link BicliqueFinderApplication}.
 * 
 */
public class NoSuchCipherError extends Error {
	
	private static final long serialVersionUID = 5421480010405325341L;
	
	public NoSuchCipherError(String proposedCipherName) {
		super("No such cipher: " + proposedCipherName);
	}
	
}
