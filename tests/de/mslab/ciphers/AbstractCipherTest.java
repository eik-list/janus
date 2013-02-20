package de.mslab.ciphers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import de.mslab.core.ByteArray;
import de.mslab.utils.Formatter;
import de.mslab.utils.Logger;

public class AbstractCipherTest {
	
	protected static RoundBasedBlockCipher cipher;
	protected static boolean logEnabled = false;
	protected static Logger logger = Logger.getLogger();
	
	protected void testDecryption(int[] keyArray, int[] plaintextArray, int[] ciphertextArray) {
		ByteArray key = new ByteArray(keyArray);
		ByteArray ciphertext = new ByteArray(ciphertextArray);
		ByteArray plaintextShouldBe = new ByteArray(plaintextArray);
		testDecryption(key, plaintextShouldBe, ciphertext);
	}
	
	protected void testDecryption(String keyString, String plaintextString, String ciphertextString) {
		ByteArray key = Formatter.hexStringToByteArray(keyString);
		ByteArray ciphertext = Formatter.hexStringToByteArray(ciphertextString);
		ByteArray plaintextShouldBe = Formatter.hexStringToByteArray(plaintextString);
		testDecryption(key, plaintextShouldBe, ciphertext);
	}
	
	protected void testDecryption(ByteArray key, ByteArray plaintextShouldBe, ByteArray ciphertext) {
		logger.info("Decrypting {0}", cipher.getName());
		cipher.setKey(key);
		
		int numRounds = cipher.getNumRounds();
		ByteArray plaintext = ciphertext.clone();
		
		for (int round = numRounds; round >= 1; round--) {
			if (cipher.hasKeyInjectionInRound(round)) {
				//logger.info("subkey: {0} {1}", round, cipher.getRoundKey(round).toHexString());
			}
			logger.info("round : {0} {1}", round, plaintext.toHexString());
			plaintext = cipher.decryptRounds(plaintext, round, round);
		}
		
		compareByteArray(plaintextShouldBe, plaintext);
	}

	protected void testEncryption(int[] keyArray, int[] plaintextArray, int[] ciphertextArray) {
		ByteArray key = new ByteArray(keyArray);
		ByteArray ciphertextShouldBe = new ByteArray(ciphertextArray);
		ByteArray plaintext = new ByteArray(plaintextArray);
		testEncryption(key, plaintext, ciphertextShouldBe);
	}
	
	protected void testEncryption(String keyString, String plaintextString, String ciphertextString) {
		ByteArray key = Formatter.hexStringToByteArray(keyString);
		ByteArray ciphertextShouldBe = Formatter.hexStringToByteArray(ciphertextString);
		ByteArray plaintext = Formatter.hexStringToByteArray(plaintextString);
		testEncryption(key, plaintext, ciphertextShouldBe);
	}
	
	protected void testEncryption(ByteArray key, ByteArray plaintext, ByteArray ciphertextShouldBe) {
		logger.info("Encrypting {0}", cipher.getName());
		
		cipher.setKey(key);
		int numRounds = cipher.getNumRounds();
		ByteArray ciphertext = plaintext.clone();
		
		logger.info("round : {0} {1}", 0, ciphertext.toHexString());
		
		for (int round = 1; round <= numRounds; round++) {
			ciphertext = cipher.encryptRounds(ciphertext, round, round);
			logger.info("round : {0} {1}", round, ciphertext.toHexString());
		}
		
		compareByteArray(ciphertextShouldBe, ciphertext);
	}
	
	protected void compareByteArray(ByteArray shouldBe, ByteArray toTest) {
		logger.info("Value:    {0}", toTest);
		logger.info("Should be {0}", shouldBe);
		
		if (shouldBe == null || toTest == null) {
			fail();
		} else if (shouldBe.length() != toTest.length()) {
			fail();
		} else {
			assertTrue(shouldBe.equals(toTest));
		}
	}
	
}
