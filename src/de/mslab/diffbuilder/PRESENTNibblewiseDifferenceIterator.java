package de.mslab.diffbuilder;

import de.mslab.core.ByteArray;

public class PRESENTNibblewiseDifferenceIterator extends NibblewiseDifferenceIterator {
	
	public PRESENTNibblewiseDifferenceIterator(ByteArray difference, int dimension, int[] bitPositions) {
		super(difference, dimension, bitPositions);
	}
	
	protected void updateDifference() {
		super.updateDifference();
		difference = rotateBy61(difference);
	}
	
	private ByteArray rotateBy61(ByteArray value) {
		int length = value.length();
		int v;
		ByteArray result = new ByteArray(length);
		
		for (int i = 0; i < length; i++) {
			v = ((value.get((i + 7) % length) << 5) & 0xE0)
				| ((value.get((i + 8) % length) >> 3) & 0x1F);
			result.set(i, v);
		}
		
		return result;
	}
	
	
}
