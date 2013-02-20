package de.mslab.ciphers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.ByteArray;
import de.mslab.utils.GaloisField;

public class BKSQ96Test extends AbstractCipherTest {
	
	// x^8 + x^4 + x^3 + x + 1
	private static int polynomial = 256 + 16 + 8 + 2 + 1; 
	private static GaloisField field = new GaloisField(256, polynomial);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cipher = new BKSQ96();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		cipher = null;
	}
	
	@Test
	public void testDecryption() {
		int[] keyArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] plaintextArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] ciphertextArray = { 0x03, 0xa4, 0x25, 0x7e, 0xe7, 0xf9, 0xf1, 0x7d, 0x40, 0x1a, 0x71, 0x44 };
		testDecryption(keyArray, plaintextArray, ciphertextArray);
	}
	
	@Test
	public void testEncryption() {
		int[] keyArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] plaintextArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] ciphertextArray = { 0x03, 0xa4, 0x25, 0x7e, 0xe7, 0xf9, 0xf1, 0x7d, 0x40, 0x1a, 0x71, 0x44 };
		testEncryption(keyArray, plaintextArray, ciphertextArray);
	}
	
	@Test
	public void testInvertTheta() {
		int[] before = {0x8e,0x5b,0xb2,0x15,0x09,0x25,0xe4,0xd9,0xf4,0xed,0xb1,0xf5};
		int[] after  = {0x40,0x95,0x7c,0x67,0x7b,0x57,0x6d,0x50,0x7d,0xa4,0xf8,0xbc};
		int[][] matrix = {{246, 247, 247}, {247, 246, 247}, {247, 247, 246}};
		int product0, product1, product2, sum;
		int[] newstate = new int[12];
		
		for (int column = 0; column < 4; column++) {
			for (int row = 0; row < 3; row++) {
				product0 = field.multiply(matrix[row][0], after[column * 3 + 0]);
				product1 = field.multiply(matrix[row][1], after[column * 3 + 1]);
				product2 = field.multiply(matrix[row][2], after[column * 3 + 2]);
				sum = field.add(field.add(product0, product1), product2);
				
				newstate[column * 3 + row] = sum;
			}
		}
		
		for (int i = 0; i < before.length; i++) {
			assertEquals(before[i], newstate[i]);
		}
	}
	
	@Test
	public void testTheta() {
		int[] before = {0x8e,0x5b,0xb2,0x15,0x09,0x25,0xe4,0xd9,0xf4,0xed,0xb1,0xf5};
		int[] after  = {0x40,0x95,0x7c,0x67,0x7b,0x57,0x6d,0x50,0x7d,0xa4,0xf8,0xbc};
		int[][] matrix = {{3, 2, 2}, {2, 3, 2}, {2, 2, 3}};
		int product0, product1, product2, sum;
		int[] newstate = new int[12];
		
		for (int column = 0; column < 4; column++) {
			for (int row = 0; row < 3; row++) {
				product0 = field.multiply(matrix[row][0], before[column * 3 + 0]);
				product1 = field.multiply(matrix[row][1], before[column * 3 + 1]);
				product2 = field.multiply(matrix[row][2], before[column * 3 + 2]);
				sum = field.add(field.add(product0, product1), product2);
				
				newstate[column * 3 + row] = sum;
			}
		}
		
		for (int i = 0; i < before.length; i++) {
			assertEquals(after[i], newstate[i]);
		}
	}
	
	@Test
	public void testExpandKey() {
		int[] expandedKeyShouldBe = { 
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x62,0x63,0x63,0x62,
			0x63,0x63,0x62,0x63,0x63,0x62,0x63,0x63,0x9b,0x98,0xc9,0xf9,0xfb,0xaa,0x9b,0x98,
			0xc9,0xf9,0xfb,0xaa,0x90,0x34,0x50,0x69,0xcf,0xfa,0xf2,0x57,0x33,0x0b,0xac,0x99,
			0x09,0xda,0x7b,0x60,0x15,0x81,0x92,0x42,0xb2,0x99,0xee,0x2b,0x31,0x2b,0x95,0x51,
			0x3e,0x14,0xc3,0x7c,0xa6,0x5a,0x92,0x8d,0x5e,0x76,0x2b,0x0f,0x48,0x3f,0xcc,0x34,
			0x99,0x96,0xa6,0x14,0x3a,0x8c,0xbb,0x35,0xc4,0x84,0xf9,0xf0,0x1d,0x6f,0x56,0x09,
			0x0b,0x8d,0x13,0x3e,0x49,0x97,0xc7,0xb9,0x8a,0xa8,0xef,0x83,0xcf,0x61,0xd1,0xf1,
			0x28,0x46,0x36,0x91,0xcc,0x9e,0x7e,0x4f,0x0a,0xe5,0xda,0xfb,0xcd,0x9c,0xcd,0x5c,
			0x50,0x53,0x22,0x1f
		};
		
		ByteArray secretKey = new ByteArray(expandedKeyShouldBe);
		ByteArray keyPart;
		ByteArray computedExpandedKey;
		int from, to;
		int[] roundKey;
		
		for (int roundIndex = 0; roundIndex <= cipher.getNumRounds(); roundIndex++) {
			from = roundIndex * cipher.getKeySize();
			to = (roundIndex + 1) * cipher.getKeySize();
			roundKey = Arrays.copyOfRange(expandedKeyShouldBe, from, to);
			keyPart = new ByteArray(roundKey);
			
			computedExpandedKey = cipher.computeExpandedKey(keyPart, roundIndex);
			assertTrue(secretKey.equals(computedExpandedKey));
		}
	}
	
}
