package de.mslab.ciphers.helpers;

/**
 * Marker interface, so that we can combine the functionality of DifferentialComparator 
 * and DifferentialActiveComponentsCounter in a single CipherHelper class for each cipher.
 */
public interface CipherHelper extends DifferentialComparator,RecomputedOperationsCounter {
	
}
