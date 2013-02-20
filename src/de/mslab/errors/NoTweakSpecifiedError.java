package de.mslab.errors;

import de.mslab.ciphers.TweakableCipher;

/**
 * Thrown by instances of type {@link TweakableCipher}, in case the <code>encrypt</code> or 
 * <code>decrypt</code> methods were invoked before the cipher was given a tweak. 
 * 
 */
public class NoTweakSpecifiedError extends Error {

	private static final String NO_TWEAK_SPECIFIED_MESSAGE = "No tweak specified.";
	private static final long serialVersionUID = 10348503L;
	
	public NoTweakSpecifiedError() {
		super(NO_TWEAK_SPECIFIED_MESSAGE);
	}
	
}
