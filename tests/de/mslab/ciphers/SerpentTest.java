package de.mslab.ciphers;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.ByteArray;

public class SerpentTest extends AbstractCipherTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cipher = new Serpent();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		cipher = null;
	}
	
	@Test
	public void testDecrypt() {
    	final int[] key = { 
    		0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f, 
    		0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x1a,0x1b,0x1c,0x1d,0x1e,0x1f 
    	};
		final int[] plaintext = { 0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f };
		final int[] ciphertext = { 0xde,0x26,0x9f,0xf8,0x33,0xe4,0x32,0xb8,0x5b,0x2e,0x88,0xd2,0x70,0x1c,0xe7,0x5c };
		testDecryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testDecryptEmptyValues() {
		final int[] key = { 
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00 
		};
		final int[] plaintext = { 0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00 };
		final int[] ciphertext = { 0x49,0x67,0x2b,0xa8,0x98,0xd9,0x8d,0xf9,0x50,0x19,0x18,0x04,0x45,0x49,0x10,0x89 };
		testDecryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testEncrypt() {
    	final int[] key = { 
    		0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f, 
    		0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x1a,0x1b,0x1c,0x1d,0x1e,0x1f 
    	};
		final int[] plaintext = { 0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f };
		final int[] ciphertext = { 0xde,0x26,0x9f,0xf8,0x33,0xe4,0x32,0xb8,0x5b,0x2e,0x88,0xd2,0x70,0x1c,0xe7,0x5c };
		testEncryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testEncryptEmptyValues() {
		final int[] key = { 
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00 
		};
		final int[] plaintext = { 0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00 };
		final int[] ciphertext = { 0x49,0x67,0x2b,0xa8,0x98,0xd9,0x8d,0xf9,0x50,0x19,0x18,0x04,0x45,0x49,0x10,0x89 };
		testEncryption(key, plaintext, ciphertext);
	}
	
	@Test
	public void testEncryptionSpeed() {
		final ByteArray key = new ByteArray(new int[]{ 
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00 
		});
		ByteArray plaintextShouldBe = new ByteArray(new int[]{ 
			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00 
		});
		final ByteArray ciphertextShouldBe = new ByteArray(new int[]{ 
			0x49,0x67,0x2b,0xa8,0x98,0xd9,0x8d,0xf9,0x50,0x19,0x18,0x04,0x45,0x49,0x10,0x89 
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
	
	@Test
	public void testComputeExpandedKey() {
		final ByteArray key = new ByteArray(new int[]{ 
    		0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f, 
    		0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x1a,0x1b,0x1c,0x1d,0x1e,0x1f 
    	});
		cipher.setKey(key);
		final ByteArray expandedKey = cipher.getExpandedKey();
		ByteArray keyPart, actualExpandedKey;
    	
		for (int i = 0; i < 100; i++) {
			for (int round = 1; round <= cipher.getNumRounds(); round++) {
				keyPart = cipher.computeKeyPart(expandedKey, round);
				actualExpandedKey = cipher.computeExpandedKey(keyPart, round);
				assertTrue(expandedKey.equals(actualExpandedKey));
			}
		}
	}
	
}



