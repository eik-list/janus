package de.mslab.ciphers;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BKSQ192Test extends AbstractCipherTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cipher = new BKSQ192();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		cipher = null;
	}
	
	@Test
	public void testDecryption() {
		int[] keyArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] plaintextArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] ciphertextArray = { 0x24,0x0f,0x88,0x5a,0x68,0x91,0xf8,0x11,0xb7,0xe8,0x0f,0x98 };
		testDecryption(keyArray, plaintextArray, ciphertextArray);
	}
	
	@Test
	public void testEncryption() {
		int[] keyArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] plaintextArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] ciphertextArray = { 0x24,0x0f,0x88,0x5a,0x68,0x91,0xf8,0x11,0xb7,0xe8,0x0f,0x98 };
		testEncryption(keyArray, plaintextArray, ciphertextArray);
	}
	
}
