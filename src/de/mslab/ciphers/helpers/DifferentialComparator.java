package de.mslab.ciphers.helpers;

import de.mslab.core.Differential;

/**
 * Compares two differentials for independency, that means, if they share active bits or 
 * active bytes. 
 * 
 */
public interface DifferentialComparator {
	boolean shareActiveNonLinearOperations(Differential deltaDifferential, Differential nablaDifferential);
}
