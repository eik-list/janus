package de.mslab.ciphers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ARIA192Test extends AbstractCipherTest {
	
	private static final int[] keyArray = {
		0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 
		0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17
	};
	private static final int[] plaintextArray = {
		0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88, 0x99, 0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff
	};
	private static final int[] ciphertextArray = {
		0x26, 0x44, 0x9c, 0x18, 0x05, 0xdb, 0xe7, 0xaa, 0x25, 0xa4, 0x68, 0xce, 0x26, 0x3a, 0x9e, 0x79
	};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cipher = new ARIA192();
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

}
