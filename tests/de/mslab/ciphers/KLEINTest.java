package de.mslab.ciphers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.ByteArray;
import de.mslab.utils.Logger;

public class KLEINTest {
	
	private static KLEIN klein;
	private static Logger logger;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		klein = new CustomKLEIN();
		logger = new Logger();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		klein = null;
		logger = null;
	}
	
	@Test
	/**
	 * Tests if a state input difference of a 32-bit 0Y0Y0Y0Y input differences 
	 * leads to a 32-bit output difference 0X0X0X0X when applying inverse mix 
	 * nibbles, where X/Y in {0,...,15}: MN^{-1}(0Y0Y 0Y0Y) =? 0X0X0X0X.
	 */
	public void testIfInverseMixNibblesDifferencesHold() {
		int good = 0;
		int bad = 0;
		final int numIterations = 1 << 16;
		
		final ByteArray targetInput = new ByteArray(8);
		targetInput.randomize();
		final ByteArray targetOutput = klein.mixNibbles(targetInput);
		
		ByteArray input = targetInput.clone();
		ByteArray output;
		
		for (int i = 1; i < numIterations; i++) {
			input.set(0, targetInput.get(0) ^ ((i >>> 12) & 0xF));
			input.set(1, targetInput.get(1) ^ ((i >>> 8) & 0xF));
			input.set(2, targetInput.get(2) ^ ((i >>> 4) & 0xF));
			input.set(3, targetInput.get(3) ^ (i & 0xF));
			output = klein.mixNibbles(input);
			output.xor(targetOutput);
			
			if (((output.get(0) & 0xE0) == 0)
				|| ((output.get(1) & 0xE0) == 0)
				|| ((output.get(2) & 0xE0) == 0)
				|| ((output.get(3) & 0xE0) == 0)) {
				good++;
			} else {
				bad++;
			}
		}
		
		logger.info("MN - good: {0}, bad: {1}", good, bad);
	}
	
	@Test
	/**
	 * Tests if input differences in the LS three bits of S-box inputs  
	 * remain active only in these bits.  
	 */
	public void testSBox() {
		int good = 0;
		int bad = 0;
		short a, b, deltaA, deltaB;
		
		for (a = 0; a < 16; a++) {
			for (deltaA = 0; deltaA < 8; deltaA++) {
				b = KLEIN.SBOX[a];
				deltaB = KLEIN.SBOX[(short)(a ^ deltaA)];
				deltaB ^= b;
				
				if ((deltaB & 0x8) == 0) {
					good++;
				} else {
					bad++;
				}
			}
		}
		
		logger.info("S-Box - good: {0}, bad: {1}", good, bad);
	}
	
	@Test
	public void testMultiplicationMod2ToThe16Plus1() {
		final long arest = 119; //(long)(Math.random() * 256);
		final long brest = 165; //(long)(Math.random() * 256);
		final long modulo = (1 << 16) + 1;
		long a, b, c, d;
		int good = 0;
		int bad = 0;
		
		for (int i = 0; i < 1; i++) {
			a = (i << 8) ^ (arest & 255);
			
			for (int j = 0; j < 1; j++) {
				b = (j << 8) ^ (brest & 255);
				c = (a * b);
				d = c % modulo;
				
				if ((c & 255) == (d & 255)) {
					good++;
				} else {
					bad++;
					/*logger.info("p: {0}, mod 2^{16} + 1: {1}, XOR: {2} ADD: {3}",
						Long.toBinaryString(c & 255), 
						Long.toBinaryString(d & 255), 
						Long.toBinaryString((c ^ d) & 255), 
						Long.toBinaryString((c - d) & 255));*/
				}
			}
		}
		
		logger.info("Multiplication - good: {0}, bad: {1}", good, bad);
	}
	
}

class CustomKLEIN extends KLEIN {
	
	public ByteArray mixNibbles(ByteArray state) {
		return super.mixNibbles(state);
	}
	
	public ByteArray invertMixNibbles(ByteArray state) {
		return super.invertMixNibbles(state);
	}
	
}





