package de.mslab.ciphers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.ByteArray;
import de.mslab.utils.Formatter;
import static org.junit.Assert.*;

public class ARIA128Test extends AbstractCipherTest {
	
	private static final int[] keyArray = {
		0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
	};
	private static final int[] plaintextArray = {
		0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88, 0x99, 0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff
	};
	private static final int[] ciphertextArray = {
		0xd7, 0x18, 0xfb, 0xd6, 0xab, 0x64, 0x4c, 0x73, 0x9d, 0xa9, 0x5f, 0x3b, 0xe6, 0x45, 0x17, 0x78
	};

	private static ARIA aria;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cipher = new ARIA128();
		aria = (ARIA)cipher;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		cipher = null;
	}
	
	@Test
	public final void testDecryptRounds() {
		testDecryption(keyArray, plaintextArray, ciphertextArray);
	}

	@Test
	public final void testEncryptRounds() {
		testEncryption(keyArray, plaintextArray, ciphertextArray);
	}
	
	@Test
	public void testEncryptionKeys() {
		cipher.setKey(new ByteArray(keyArray));
		long[][] shouldBe = {
			null, 
			null, 
			{ 0xd415a75c794b85c5L, 0xe0d2a0b3cb793bf6L }, 
			{ 0x369c65e4b11777abL, 0x713a3e1e6601b8f4L }, 
			{ 0x0368d4f13d14497bL, 0x6529ad7ac809e7d0L }, 
			{ 0xc644552b549a263fL, 0xb8d0b50906229eecL },
			{ 0x5f9c434951f2d2efL, 0x342787b1a781794cL }, 
			{ 0xafea2c0ce71db6deL, 0x42a47461f4323c54L }, 
			{ 0x324286db44ba4db6L, 0xc44ac306f2a84b2cL }, 
			{ 0x7f9fa93574d842b9L, 0x101a58063771eb7bL }, 
			{ 0xaab9c57731fcd213L, 0xad5677458fcfe6d4L }, 
			{ 0x2f4423bb06465abaL, 0xda5694a19eb88459L }, 
			{ 0x9f8772808f5d580dL, 0x810ef8ddac13abebL }, 
			{ 0x8684946a155be77eL, 0xf810744847e35fadL }, 
			{ 0x0f0aa16daee61bd7L, 0xdfee5a599970fb35L }
		};
		
		for (int roundIndex = 2; roundIndex <= cipher.getNumRounds() + 1; roundIndex++) {
			long[] roundKey = aria.encryptionKeys[roundIndex];
			logger.info("enckey round {0} {1}", roundIndex, Formatter.longArrayToHexStrings(roundKey));
			assertArrayEquals(shouldBe[roundIndex], roundKey);
		}
	}
	
	@Test
	public void testRotateLeft() {
		long[] inputs = { 0xc000000000000000L, 0xf000000000000000L };
		long[] results = aria.rotateLeft(inputs, 3);
		
		assertEquals(0xc000000000000000L, inputs[0]);
		assertEquals(0xf000000000000000L, inputs[1]);
		
		assertEquals(0x7, results[0]);
		assertEquals(0x8000000000000006L, results[1]);
	}
	
	@Test
	public void testRotateRight() {
		ARIA aria = (ARIA)cipher;
		long[] inputs = { 0x000000000000000f, 0x0000000000000003 };
		long[] results = aria.rotateRight(inputs, 3);
		
		assertEquals(0x000000000000000fL, inputs[0]);
		assertEquals(0x0000000000000003L, inputs[1]);
		
		assertEquals(0x6000000000000001L, results[0]);
		assertEquals(0xe000000000000000L, results[1]);
	}
	
	@Test
	public void testRotationRight() {
		long[] inputs = { 0x00000000ffff0000L, 0 };
		long[] results = aria.rotateRight(inputs, 24);
		
		assertEquals(0xff, results[0]);
		assertEquals(0xff00000000000000L, results[1]);
	}
	
	@Test
	public void testRotationRightOverflow() {
		long[] inputs = { 0x000000000000f000L, 0 };
		long[] results = aria.rotateRight(inputs, 14);
		
		assertEquals(0x3, results[0]);
		assertEquals(0xc000000000000000L, results[1]);
	}
	
	@Test
	public void testRotationRightOver32Bit() {
		long[] inputs = { 0xf000000000000000L, 0 };
		long[] results = aria.rotateRight(inputs, 35);
		
		assertEquals(0x000000001e000000L, results[0]);
		assertEquals(0, results[1]);
	}
	
	@Test
	public void testRotationLeft() {
		long[] inputs = { 0x0f00000000000000L, 0 };
		long[] results = aria.rotateLeft(inputs, 7);
		
		assertEquals(0x8000000000000000L, results[0]);
		assertEquals(0x7, results[1]);
	}
	
}
