package de.mslab.ciphers.tables;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.utils.Formatter;
import de.mslab.utils.GaloisField;
import de.mslab.utils.Logger;

/**
 * Generates tables for multiplication in the Galois Field for LED.  
 */
public class LEDTablesGenerator extends CipherTablesGeneratorBase {
	
	private static Logger logger = new Logger();
	private static GaloisField field;
	
	// x^4 + x + 1
	private static int polynomial = 16 + 2 + 1; 
	
	private static int[] XTIMES_02 = new int[16];
	private static int[] XTIMES_03 = new int[16];
	private static int[] XTIMES_04 = new int[16];
	private static int[] XTIMES_05 = new int[16];
	private static int[] XTIMES_06 = new int[16];
	private static int[] XTIMES_07 = new int[16];
	private static int[] XTIMES_08 = new int[16];
	private static int[] XTIMES_09 = new int[16];
	private static int[] XTIMES_0A = new int[16];
	private static int[] XTIMES_0B = new int[16];
	private static int[] XTIMES_0C = new int[16];
	private static int[] XTIMES_0D = new int[16];
	private static int[] XTIMES_0E = new int[16];
	private static int[] XTIMES_0F = new int[16];
	
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
		for (int i = 0; i < 16; i++) {
			XTIMES_02[i] = field.multiply(i, 0x02);
			XTIMES_03[i] = field.multiply(i, 0x03);
			XTIMES_04[i] = field.multiply(i, 0x04);
			XTIMES_05[i] = field.multiply(i, 0x05);
			XTIMES_06[i] = field.multiply(i, 0x06);
			XTIMES_07[i] = field.multiply(i, 0x07);
			XTIMES_08[i] = field.multiply(i, 0x08);
			XTIMES_09[i] = field.multiply(i, 0x09);
			XTIMES_0A[i] = field.multiply(i, 0x0A);
			XTIMES_0B[i] = field.multiply(i, 0x0B);
			XTIMES_0C[i] = field.multiply(i, 0x0C);
			XTIMES_0D[i] = field.multiply(i, 0x0D);
			XTIMES_0E[i] = field.multiply(i, 0x0E);
			XTIMES_0F[i] = field.multiply(i, 0x0F);
		}
		
		logTable("XTIMES_02", XTIMES_02);
		logTable("XTIMES_03", XTIMES_03);
		logTable("XTIMES_04", XTIMES_04);
		logTable("XTIMES_05", XTIMES_05);
		logTable("XTIMES_06", XTIMES_06);
		logTable("XTIMES_07", XTIMES_07);
		logTable("XTIMES_08", XTIMES_08);
		logTable("XTIMES_09", XTIMES_09);
		logTable("XTIMES_0A", XTIMES_0A);
		logTable("XTIMES_0B", XTIMES_0B);
		logTable("XTIMES_0C", XTIMES_0C);
		logTable("XTIMES_0D", XTIMES_0D);
		logTable("XTIMES_0E", XTIMES_0E);
		logTable("XTIMES_0F", XTIMES_0F);
	}
	
	@Test
	public void testComputeRoundConstants() {
		int rc = 0;
		int rc0 = 0;
		int numRounds = 48;
		int[] constants = new int[numRounds];
		String s = "";
		
		for (int round = 0; round < numRounds; round++) {
			rc0 = ((rc & 0x20) >> 5) ^ ((rc & 0x10) >> 4) ^ 1;
			rc = (rc << 1) & 0x3F;
			rc |= rc0 & 1;
			constants[round] = rc & 0x3F;
			s += rc + ", ";
		}
		
		logger.info("{0}", s);
	}
	
	@Test
	public void testComputeState() {
		int[] state = { 0xc, 0xc, 0xc, 0xc, 5, 0xc, 0xc, 5, 0xc, 0xc, 6, 0xc, 0xc, 0xb, 5, 0xc };
		int[] newState = new int[16];
		
		newState[0] = XTIMES_04[state[0]] ^ state[4] ^ XTIMES_02[state[8]] ^ XTIMES_02[state[12]];
		newState[1] = XTIMES_08[state[0]] ^ XTIMES_06[state[4]] ^ XTIMES_05[state[8]] ^ XTIMES_06[state[12]];
		newState[2] = XTIMES_0B[state[0]] ^ XTIMES_0E[state[4]] ^ XTIMES_0A[state[8]] ^ XTIMES_09[state[12]];
		newState[3] = XTIMES_02[state[0]] ^ XTIMES_02[state[4]] ^ XTIMES_0F[state[8]] ^ XTIMES_0B[state[12]];
		
		logger.info("state {0}", Formatter.byteArrayToHexStrings(newState));
	}
	
	
}
