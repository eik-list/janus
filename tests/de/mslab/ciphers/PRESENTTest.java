package de.mslab.ciphers;


import org.junit.Test;

import de.mslab.utils.Formatter;
import de.mslab.utils.Logger;


public class PRESENTTest {
	
	private static int[] INVERSE_PERMUTATION;
	private static int[] INVERSE_SBOX;
	private static int[] PERMUTATION = PRESENT.PERMUTATION;
	private static int[] SBOX = PRESENT.SBOX;

	protected static Logger logger = Logger.getLogger();
	
	@Test
	public void computeInverseSBox() {
		INVERSE_SBOX = new int[SBOX.length];
		int substitute;
		
		for (int i = 0; i < SBOX.length; i++) {
			substitute = SBOX[i];
			INVERSE_SBOX[substitute] = i;
		}
		
		logTable("INVERSE_SBOX", INVERSE_SBOX);
	}
	
	@Test
	public void computeInversePermutation() {
		INVERSE_PERMUTATION = new int[PERMUTATION.length];
		int substitute;
		
		for (int i = 0; i < PERMUTATION.length; i++) {
			substitute = PERMUTATION[i];
			INVERSE_PERMUTATION[substitute] = i;
		}
		
		logTable("INVERSE_PERMUTATION", INVERSE_PERMUTATION);
	}
	
	private void logTable(String name, int[] array) {
		String message = "private static final int[] " + name + " = { \n" +  Formatter.byteArrayToHexStrings(array) + "};";
		logger.info(message);
	}
	
}
