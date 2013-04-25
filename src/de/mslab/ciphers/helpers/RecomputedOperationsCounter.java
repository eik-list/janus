package de.mslab.ciphers.helpers;

import de.mslab.core.Differential;

/**
 * Count the number of active bits or bytes in non-linear operations 
 * in the matching phase for the individual block ciphers. The point of non-linear operations differs
 * depending on the cipher and where non-linear operations occur in the round functions.
 * 
 */
public interface RecomputedOperationsCounter {
	/**
	 * Counts the number of operations which need recomputation in a differential 
	 * using a matching-with-precomputations part.
	 * @param differential The target differential.
	 * @return The number of operations which need recomputation.
	 */
	int countRecomputedOperations(Differential differential);
	/**
	 * Counts the number of operations which need recomputation in a matching-with-precomputations part 
	 * of an independent-biclique attack.
	 * @param stateDifferential A differential, in which only the states should be considered.
	 * @param keyDifferential A differential, in which only the round keys should be considered.
	 * @return The number of operations which need recomputation.
	 */
	int countRecomputedOperations(Differential stateDifferential, Differential keyDifferential);
}
