package de.mslab.diffbuilder;

import de.mslab.ciphers.ARIA;
import de.mslab.core.ByteArray;

public class ARIA256DifferenceIterator extends AbstractDifferenceIterator {

	/**
	 * The constant for the key schedule for the 256-bit version is {@link ARIA#C3}. 
	 */
	private static final ByteArray CK1 = new ByteArray(ARIA.C3);
	
	public ARIA cipher;
	
	private ByteArray kr;
	private ByteArray kr2;
	private ByteArray kl;
	
	public ARIA256DifferenceIterator(ByteArray difference, int dimension, int[] activePositions, ARIA cipher) {
		super(difference, dimension, activePositions);
		this.cipher = cipher;
	}
	
	protected void updateDifference() {
		index++;
		difference = new ByteArray(cipher.getKeySize());
		int shift = 0;
		int mask = 0xff;
		
		for (int i = 0; i < activePositions.length; i++) {
			difference.set(activePositions[i], (short)((index >>> shift) & mask));
			shift += Byte.SIZE;
		}
		
		kl = difference.splice(0, 16);
		
		if (!kl.equals(0)) {
			kr = cipher.oddKeyExpansionFunction(new ByteArray(16), CK1);
			kr2 = cipher.oddKeyExpansionFunction(kl, CK1);
			kr2.xor(kr);
			difference.copyBytes(kr2, 0, 16);
		}
	}
	
}
