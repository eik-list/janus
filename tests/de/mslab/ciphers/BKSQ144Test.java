package de.mslab.ciphers;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BKSQ144Test extends AbstractCipherTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cipher = new BKSQ144();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		cipher = null;
	}
	
	@Test
	public void testDecryption() {
		int[] keyArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] plaintextArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] ciphertextArray = { 0xc2,0xe6,0xa9,0x52,0x3f,0xcc,0xcc,0xd2,0xa9,0x86,0x2a,0x36 };
		testDecryption(keyArray, plaintextArray, ciphertextArray);
	}
	
	@Test
	public void testEncryption() {
		int[] keyArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] plaintextArray = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] ciphertextArray = { 0xc2,0xe6,0xa9,0x52,0x3f,0xcc,0xcc,0xd2,0xa9,0x86,0x2a,0x36 };
		testEncryption(keyArray, plaintextArray, ciphertextArray);
	}
	
}
