package de.mslab.ciphers;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.ByteArray;

public class PRESENT80Test extends AbstractCipherTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cipher = new PRESENT80();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		cipher = null;
	}
	
	@Test
	public void testDecrypt() {
		int[] key = 		{ 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
		int[] plaintext = 	{ 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
		int[] ciphertext = 	{ 0x33, 0x33, 0xDC, 0xD3, 0x21, 0x32, 0x10, 0xD2 };
		testDecryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testDecryptEmptyValues() {
		int[] key = 		{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		int[] plaintext = 	{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		int[] ciphertext = 	{ 0x55, 0x79, 0xC1, 0x38, 0x7B, 0x22, 0x84, 0x45 };
		testDecryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testDecryptEmptyPlaintext() {
		int[] key = 		{ 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
		int[] plaintext = 	{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		int[] ciphertext = 	{ 0xE7, 0x2C, 0x46, 0xC0, 0xF5, 0x94, 0x50, 0x49 };
		testDecryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testDecryptEmptyKey() {
		int[] key = 		{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		int[] plaintext = 	{ 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
		int[] ciphertext = 	{ 0xA1, 0x12, 0xFF, 0xC7, 0x2F, 0x68, 0x41, 0x7B };
		testDecryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testEncrypt() {
		int[] key = 		{ 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
		int[] plaintext = 	{ 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
		int[] ciphertext = 	{ 0x33, 0x33, 0xDC, 0xD3, 0x21, 0x32, 0x10, 0xD2 };
		testEncryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testEncryptEmptyValues() {
		int[] key = 		{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		int[] plaintext = 	{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		int[] ciphertext = 	{ 0x55, 0x79, 0xC1, 0x38, 0x7B, 0x22, 0x84, 0x45 };
		testEncryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testEncryptEmptyPlaintext() {
		int[] key = 		{ 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
		int[] plaintext = 	{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		int[] ciphertext = 	{ 0xE7, 0x2C, 0x46, 0xC0, 0xF5, 0x94, 0x50, 0x49 };
		testEncryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testEncryptEmptyKey() {
		int[] key = 		{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		int[] plaintext = 	{ 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
		int[] ciphertext = 	{ 0xA1, 0x12, 0xFF, 0xC7, 0x2F, 0x68, 0x41, 0x7B };
		testEncryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testExpandKey() {
		/*int[] expandedKeyShouldBe = { // Each line contains two 64 bit round keys
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xc0,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
			0x50,0x00,0x18,0x00,0x00,0x00,0x00,0x01,0x60,0x00,0x0a,0x00,0x03,0x00,0x00,0x01,
			0xb0,0x00,0x0c,0x00,0x01,0x40,0x00,0x62,0x90,0x00,0x16,0x00,0x01,0x80,0x00,0x2a,
			0x00,0x01,0x92,0x00,0x02,0xc0,0x00,0x33,0xa0,0x00,0xa0,0x00,0x32,0x40,0x00,0x5b,
			0xd0,0x00,0xd4,0x00,0x14,0x00,0x06,0x4c,0x30,0x01,0x7a,0x00,0x1a,0x80,0x02,0x84,
			0xe0,0x19,0x26,0x00,0x2f,0x40,0x03,0x55,0xf0,0x0a,0x1c,0x03,0x24,0xc0,0x05,0xed,
			0x80,0x0d,0x5e,0x01,0x43,0x80,0x64,0x9e,0x40,0x17,0xb0,0x01,0xab,0xc0,0x28,0x76,
			0x71,0x92,0x68,0x02,0xf6,0x00,0x35,0x7f,0x10,0xa1,0xce,0x32,0x4d,0x00,0x5e,0xc7,
			0x20,0xd5,0xe2,0x14,0x39,0xc6,0x49,0xa8,0xc1,0x7b,0x04,0x1a,0xbc,0x42,0x87,0x30,
			0xc9,0x26,0xb8,0x2f,0x60,0x83,0x57,0x81,0x6a,0x1c,0xd9,0x24,0xd7,0x05,0xec,0x19,
			0xbd,0x5e,0x0d,0x43,0x9b,0x24,0x9a,0xea,0x07,0xb0,0x77,0xab,0xc1,0xa8,0x73,0x6e,
			0x42,0x6b,0xa0,0xf6,0x0e,0xf5,0x78,0x3e,0x41,0xcd,0xa8,0x4d,0x74,0x1e,0xc1,0xd5,
			0xf5,0xe0,0xe8,0x39,0xb5,0x09,0xae,0x8f,0x2b,0x07,0x5e,0xbc,0x1d,0x07,0x36,0xad,
			0x86,0xba,0x25,0x60,0xeb,0xd7,0x83,0xad,0x8c,0xda,0xb0,0xd7,0x44,0xac,0x1d,0x77,
			0x1e,0x0e,0xb1,0x9b,0x56,0x1a,0xe8,0x9b,0xd0,0x75,0xc3,0xc1,0xd6,0x33,0x6a,0xcd,
			0x8b,0xa2,0x7a,0x0e,0xb8,0x78,0x3a,0xc9,0x6d,0xab,0x31,0x74,0x4f,0x41,0xd7,0x00
		};*/
		// Each line contains two 80 bit register states.
		// The leftmost 64 bits are used as the round key. 
		int[] fullKeyRegisterStates = {
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xc0,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x80,0x00,
			0x50,0x00,0x18,0x00,0x00,0x00,0x00,0x01,0x00,0x00,0x60,0x00,0x0a,0x00,0x03,0x00,0x00,0x01,0x80,0x00,
			0xb0,0x00,0x0c,0x00,0x01,0x40,0x00,0x62,0x00,0x00,0x90,0x00,0x16,0x00,0x01,0x80,0x00,0x2a,0x80,0x0c,
			0x00,0x01,0x92,0x00,0x02,0xc0,0x00,0x33,0x00,0x05,0xa0,0x00,0xa0,0x00,0x32,0x40,0x00,0x5b,0x80,0x06,
			0xd0,0x00,0xd4,0x00,0x14,0x00,0x06,0x4c,0x00,0x0b,0x30,0x01,0x7a,0x00,0x1a,0x80,0x02,0x84,0x80,0xc9,
			0xe0,0x19,0x26,0x00,0x2f,0x40,0x03,0x55,0x00,0x50,0xf0,0x0a,0x1c,0x03,0x24,0xc0,0x05,0xed,0x80,0x6a,
			0x80,0x0d,0x5e,0x01,0x43,0x80,0x64,0x9e,0x00,0xbd,0x40,0x17,0xb0,0x01,0xab,0xc0,0x28,0x76,0x8c,0x93,
			0x71,0x92,0x68,0x02,0xf6,0x00,0x35,0x7f,0x05,0x0e,0x10,0xa1,0xce,0x32,0x4d,0x00,0x5e,0xc7,0x86,0xaf,
			0x20,0xd5,0xe2,0x14,0x39,0xc6,0x49,0xa8,0x0b,0xd8,0xc1,0x7b,0x04,0x1a,0xbc,0x42,0x87,0x30,0x49,0x35,
			0xc9,0x26,0xb8,0x2f,0x60,0x83,0x57,0x81,0x50,0xe6,0x6a,0x1c,0xd9,0x24,0xd7,0x05,0xec,0x19,0xea,0xf0,
			0xbd,0x5e,0x0d,0x43,0x9b,0x24,0x9a,0xea,0xbd,0x83,0x07,0xb0,0x77,0xab,0xc1,0xa8,0x73,0x6e,0x13,0x5d,
			0x42,0x6b,0xa0,0xf6,0x0e,0xf5,0x78,0x3e,0x0e,0x6d,0x41,0xcd,0xa8,0x4d,0x74,0x1e,0xc1,0xd5,0x2f,0x07,
			0xf5,0xe0,0xe8,0x39,0xb5,0x09,0xae,0x8f,0xd8,0x3a,0x2b,0x07,0x5e,0xbc,0x1d,0x07,0x36,0xad,0xb5,0xd1,
			0x86,0xba,0x25,0x60,0xeb,0xd7,0x83,0xad,0xe6,0xd5,0x8c,0xda,0xb0,0xd7,0x44,0xac,0x1d,0x77,0x70,0x75,
			0x1e,0x0e,0xb1,0x9b,0x56,0x1a,0xe8,0x9b,0x83,0xae,0xd0,0x75,0xc3,0xc1,0xd6,0x33,0x6a,0xcd,0xdd,0x13,
			0x8b,0xa2,0x7a,0x0e,0xb8,0x78,0x3a,0xc9,0x6d,0x59,0x6d,0xab,0x31,0x74,0x4f,0x41,0xd7,0x00,0x87,0x59
		};
		
		ByteArray secretKey = new ByteArray(fullKeyRegisterStates);
		ByteArray keyPart;
		ByteArray computedExpandedKey;
		int from, to;
		int[] registerState;
		
		for (int roundIndex = 1; roundIndex <= cipher.getNumRounds(); roundIndex++) {
			from = (roundIndex - 1) * cipher.getKeySize();
			to = roundIndex * cipher.getKeySize();
			registerState = Arrays.copyOfRange(fullKeyRegisterStates, from, to);
			keyPart = new ByteArray(registerState);
			computedExpandedKey = cipher.computeExpandedKey(keyPart, roundIndex);
			assertTrue(secretKey.equals(computedExpandedKey));
		}
	}
	
}
