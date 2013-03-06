package de.mslab.ciphers.tables;

import static org.junit.Assert.assertArrayEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.ciphers.AES;
import de.mslab.utils.GaloisField;

/**
 * Generates tables for multiplication in the Galois Field for the AES.  
 */
public class AESTablesGenerator extends CipherTablesGeneratorBase {
	
	private static GaloisField field;
	
	// x^8 + x^4 + x^3 + x + 1
	private static int polynomial = 256 + 16 + 8 + 2 + 1; 
	
	private static int[] XTIMES_02 = new int[256];
	private static int[] XTIMES_03 = new int[256];
	private static int[] XTIMES_09 = new int[256];
	private static int[] XTIMES_0B = new int[256];
	private static int[] XTIMES_0D = new int[256];
	private static int[] XTIMES_0E = new int[256];
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		field = new GaloisField(256, polynomial);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		field = null;
	}

	@Test
	public void computeTables() throws Exception {
		for (int i = 0; i < 256; i++) {
			XTIMES_02[i] = field.multiply(i, 0x02);
			XTIMES_03[i] = field.multiply(i, 0x03);
			XTIMES_09[i] = field.multiply(i, 0x09);
			XTIMES_0B[i] = field.multiply(i, 0x0B);
			XTIMES_0D[i] = field.multiply(i, 0x0D);
			XTIMES_0E[i] = field.multiply(i, 0x0E);
		}
		
		logTable("XTIMES_02", XTIMES_02);
		logTable("XTIMES_03", XTIMES_03);
		logTable("XTIMES_09", XTIMES_09);
		logTable("XTIMES_0B", XTIMES_0B);
		logTable("XTIMES_0D", XTIMES_0D);
		logTable("XTIMES_0E", XTIMES_0E);
		
		assertArrayEquals(AES.X_TIMES_2, XTIMES_02);
		assertArrayEquals(AES.X_TIMES_3, XTIMES_03);
		assertArrayEquals(AES.X_TIMES_9, XTIMES_09);
		assertArrayEquals(AES.X_TIMES_B, XTIMES_0B);
		assertArrayEquals(AES.X_TIMES_D, XTIMES_0D);
		assertArrayEquals(AES.X_TIMES_E, XTIMES_0E);
	}
	
}
