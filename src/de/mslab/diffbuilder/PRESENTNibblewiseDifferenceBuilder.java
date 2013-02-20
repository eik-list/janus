package de.mslab.diffbuilder;


/**
 * Computes in increasing manner all n-byte values with less or equal 
 * a maximum number of '1' bytes, while all other bytes are zero. 
 * 
 */
public class PRESENTNibblewiseDifferenceBuilder extends NibblewiseDifferenceBuilder {
	
	protected DifferenceIterator storeNextValue() {
		return new PRESENTNibblewiseDifferenceIterator(value, dimension, bitPositions);
	}
	
}
