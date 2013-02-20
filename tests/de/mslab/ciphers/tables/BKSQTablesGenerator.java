package de.mslab.ciphers.tables;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.utils.GaloisField;

/**
 * Generates tables for multiplication in the Galois Field for BKSQ.  
 */
public class BKSQTablesGenerator extends CipherTablesGeneratorBase {

	private static GaloisField field;
	
	// x^8 + x^4 + x^3 + x + 1
	private static int polynomial = 256 + 16 + 8 + 2 + 1; 
	
	private static int[] XTIMES_02 = new int[256];
	private static int[] XTIMES_03 = new int[256];
	private static int[] XTIMES_F6 = new int[256];
	private static int[] XTIMES_F7 = new int[256];
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		field = new GaloisField(256, polynomial);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		field = null;
	}

	@Test
	public void testComputeTables() throws Exception {
		for (int i = 0; i < 256; i++) {
			XTIMES_02[i] = field.multiply(i, 0x02);
			XTIMES_03[i] = field.multiply(i, 0x03);
			XTIMES_F6[i] = field.multiply(i, 0xF6);
			XTIMES_F7[i] = field.multiply(i, 0xF7);
		}
		
		logTable("XTIMES_02", XTIMES_02);
		logTable("XTIMES_03", XTIMES_03);
		logTable("XTIMES_F6", XTIMES_F6);
		logTable("XTIMES_F7", XTIMES_F7);
	}
	
}
