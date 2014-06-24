package de.mslab.bicliquesearch.helpers;

import de.mslab.core.Biclique;

public class DefaultBicliqueRater implements BicliqueRater {
	
	/**
	 * <p>
	 * Assigns a score to a given biclique according to its data complexity. The higher the complexity, 
	 * i.e., the number of required plaintext-ciphertext pairs for an attack which uses this biclique,
	 * the lower the score.
	 * </p>
	 * <p>
	 * If the {@link Differential#fromRound} is 1 (the biclique is located at the
	 * beginning rounds of a cipher), the active bits in the beginning state of the nabla 
	 * differential are counted. Else, the active bits in the end state of the delta 
	 * differential are counted. 
	 * </p>
	 * @param biclique 
	 * @return The statesize minus the logarithm of the maximum number of required plaintext-ciphertext pairs.
	 */
	public int determineScoreForBiclique(Biclique biclique) {
		final int numBits = biclique.deltaDifferential.getStateDifference(biclique.deltaDifferential.fromRound).length() 
			* Byte.SIZE;
		return numBits - determineDataComplexity(biclique);
	}
	
	protected int determineDataComplexity(Biclique biclique) {
		if (biclique.deltaDifferential.fromRound == 1) {
			return biclique.nablaDifferential.getStateDifference(0) // Plaintext
				.getDelta().countNumActiveBits(); // Determine data complexity
		} else {
			// Ciphertext
			return biclique.deltaDifferential.getStateDifference(biclique.deltaDifferential.toRound)
				.getDelta().countNumActiveBits(); // Determine data complexity 
		}
	}
	
}
