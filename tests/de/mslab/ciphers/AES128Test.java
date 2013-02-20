package de.mslab.ciphers;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.ByteArray;

public class AES128Test extends AbstractCipherTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cipher = new AES128();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		cipher = null;
	}
	
	@Test
	public void test128BitDefaultVectors() {
		int[] key = {
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
		}; 
		int[] plaintext = {
			0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88, 0x99, 0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff
		};
		int[] ciphertext = {
			0x69, 0xc4, 0xe0, 0xd8, 0x6a, 0x7b, 0x04, 0x30, 0xd8, 0xcd, 0xb7, 0x80, 0x70, 0xb4, 0xc5, 0x5a
		};
		
		testEncryption(key, plaintext, ciphertext);
		testDecryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void test128BitTestVectors() {
		int[] key = {
			0x2b, 0x7e, 0x15, 0x16, 0x28, 0xae, 0xd2, 0xa6, 0xab, 0xf7, 0x15, 0x88, 0x09, 0xcf, 0x4f, 0x3c
		};
		int[] plaintext = {
			0x32, 0x43, 0xf6, 0xa8, 0x88, 0x5a, 0x30, 0x8d, 0x31, 0x31, 0x98, 0xa2, 0xe0, 0x37, 0x07, 0x34
		};
		int[] ciphertext = {
			0x39, 0x25, 0x84, 0x1d, 0x02, 0xdc, 0x09, 0xfb, 0xdc, 0x11, 0x85, 0x97, 0x19, 0x6a, 0x0b, 0x32
		};
		
		testEncryption(key, plaintext, ciphertext);
		testDecryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testComputeSecretKey() {
		int round = 8;
		int value = 33792;
		int numBytes = AES128.NUM_BYTES_IN_128_BIT_VERSION;
		ByteArray roundKey = new ByteArray(value, numBytes);
		roundKey.set(13, 1);
		
		ByteArray expandedKey = cipher.computeExpandedKey(roundKey, round);
		
		int byteFrom = round * AES128.NUM_BYTES_IN_STATE;
		int byteTo = byteFrom + AES128.NUM_BYTES_IN_STATE;
		ByteArray roundKeyInExpandedKey = expandedKey.splice(byteFrom, byteTo); 
		
		assertTrue(roundKey.equals(roundKeyInExpandedKey));
	}
	
	@Test
	public void testExpandKey() {
		int[] key = {
			0x2b, 0x7e, 0x15, 0x16, 
			0x28, 0xae, 0xd2, 0xa6,
			0xab, 0xf7, 0x15, 0x88, 
			0x09, 0xcf, 0x4f, 0x3c
		};
		
		ByteArray secretKey = new ByteArray(key);
		
		cipher.setKey(secretKey);
		
		ByteArray roundKey = cipher.getRoundKey(2);
		int[] roundKeyShouldBe = {
			0xf2,0xc2,0x95,0xf2,0x7a,0x96,0xb9,0x43,
			0x59,0x35,0x80,0x7a,0x73,0x59,0xf6,0x7f
		};
		
		assertTrue(roundKey.equals(roundKeyShouldBe));
	}
	
	@Test
	public void testExpandRoundKey() {
		ByteArray roundKey8 = new ByteArray(new int[]{0,0,0,0, 0,0,0,0, 1,0,0,0, 1,0,0,0});
		ByteArray roundKey0 = new ByteArray(new int[]{0x2e,0x18,0x5b,0x85,0x5e,0xf4,0x56,0xcf,0xcd,0x00,0xeb,0x15,0x11,0x98,0x73,0x45});
		
		ByteArray firstExpandedKey = cipher.computeExpandedKey(roundKey8, 8);
		ByteArray secondExpandedKey = cipher.computeExpandedKey(roundKey0, 0);
		assertTrue(firstExpandedKey.equals(secondExpandedKey));
	}

	@Test
	public void testEncryptionSpeed() {
		final ByteArray key = new ByteArray(new int[]{ 
			0x2b, 0x7e, 0x15, 0x16, 0x28, 0xae, 0xd2, 0xa6, 0xab, 0xf7, 0x15, 0x88, 0x09, 0xcf, 0x4f, 0x3c 
		});
		ByteArray plaintextShouldBe = new ByteArray(new int[]{ 
			0x32, 0x43, 0xf6, 0xa8, 0x88, 0x5a, 0x30, 0x8d, 0x31, 0x31, 0x98, 0xa2, 0xe0, 0x37, 0x07, 0x34 
		});
		final ByteArray ciphertextShouldBe = new ByteArray(new int[]{ 
			0x39, 0x25, 0x84, 0x1d, 0x02, 0xdc, 0x09, 0xfb, 0xdc, 0x11, 0x85, 0x97, 0x19, 0x6a, 0x0b, 0x32 
		});
		final long numEncryptions = 1000;
		final long numIterations = 10;
		ByteArray plaintext = plaintextShouldBe;
		ByteArray ciphertext = null;
		cipher.setKey(key);
		long startTime, endTime, sum = 0;
		
		for (int j = 0; j < numIterations; j++) {
			startTime = System.nanoTime();
			for (long i = 0; i < numEncryptions; i++) {
				ciphertext = cipher.encrypt(plaintext);
				plaintext = cipher.decrypt(ciphertext);
			}
			endTime = System.nanoTime();
			sum += (endTime - startTime);
		}
		
		double average = (double)(sum) / (1000000.0 * numEncryptions * numIterations);
		logger.info("Time needed: {0} s per 1000 encryptions + decryptions", average);
		sum = 0;
		
		for (int j = 0; j < numIterations; j++) {
			startTime = System.nanoTime();
			for (int i = 0; i < numEncryptions; i++) {
				cipher.setKey(key);
			}
			endTime = System.nanoTime();
			sum += (endTime - startTime);
		}

		average = (double)(sum) / (1000000.0 * numEncryptions * numIterations);
		logger.info("Time needed: {0} s per 1000 keyschedules", average);
		
		assertTrue(ciphertextShouldBe.equals(ciphertext));
		assertTrue(plaintextShouldBe.equals(plaintext));
	}
	
}
