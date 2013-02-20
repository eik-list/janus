package de.mslab.core;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.utils.Formatter;
import de.mslab.utils.Logger;

public class ByteTest {
	
	private static Logger logger = Logger.getLogger();
	private static int[] masks;
	private static int[] sbox = { 3, 8, 15, 1, 10, 6, 5, 11, 14, 13, 4, 2, 7, 0, 9, 12 };
	
	@BeforeClass
	public static void setup() {
		masks = new int[32];
		int mask;
		
		for (int i = 0; i < 32; i++) {
			mask = 1 << i;
			masks[31 - i] = mask;
		}
	}
	
	@Test
	public void testByteToIntConversion() {
		final int maximum = 256;
		byte b;
		int i;
		
		for (int j = 0; j < maximum; j++) {
			b = (byte)j;
			i = (int)(b & 0xFF);
			
			logger.info("byte {0} {1} int {2} {3}", 
				Formatter.byteToHexString(b), b, Formatter.intToHexString(i), i
			);
			
			assertEquals(Formatter.byteToHexString(b), Formatter.byteToHexString(i));
			assertEquals(j, i);
		}
	}
	
	@Test
	public void testGetBit() {
		int[] state = new int[4];
		int word, mask;
		
		for (int i = 0; i < 4; i++) {
			state[i] = (int)(Math.random() * 0xFFFFFFFF) & 0xFFFFFFFF;
		}
		
		for (int i = 0; i < 32; i++) {
			mask = masks[i];
			word = ((state[0] & mask) >>> i)
			| ((state[1] & mask) >>> (i - 1))
			| ((state[2] & mask) >>> (i - 2))
			| ((state[3] & mask) >>> (i - 3));
			word = sbox[word];
		}
	}
	
}












