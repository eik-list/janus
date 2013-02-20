package de.mslab.ciphers.tables;

import de.mslab.utils.Formatter;
import de.mslab.utils.Logger;

public class CipherTablesGeneratorBase {
	
	protected Logger logger = new Logger();
	
	protected void logTable(String name, int[] array) {
		logger.info("private static final int[] {0} = { \n{1}};", name, Formatter.byteArrayToHexStrings(array));
	}
	
}
