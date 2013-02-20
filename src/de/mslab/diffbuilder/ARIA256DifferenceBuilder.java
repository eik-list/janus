package de.mslab.diffbuilder;

import de.mslab.ciphers.ARIA;

/**
 * <p>
 * ARIA-256 uses the round function from the encryption process in its key schedule. 
 * The diffusion layer in ARIA's round function has a strong diffusion, where
 * a difference with only a single active byte affects seven bytes in one round round.
 * This makes it impossible to construct bicliques over more than a single round 
 * with our generic approach. 
 * </p>
 * <p>
 * CX12 uses a dedicated approach which exploits the key schedule of ARIA  
 * to construct two-round bicliques on ARIA-256 and mount an independent-biclique attack.
 * This class is used to reconstruct differences as in CX12 in order to create two-round bicliques.
 * <p>
 * 
 */
public class ARIA256DifferenceBuilder extends BytewiseDifferenceBuilder {
	
	public ARIA cipher;
	
	protected DifferenceIterator storeNextValue() {
		return new ARIA256DifferenceIterator(value, dimension, bitPositions, cipher);
	}
	
}
