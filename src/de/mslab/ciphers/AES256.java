package de.mslab.ciphers;

import de.mslab.core.ByteArray;

public class AES256 extends AES {
	
	public static final int NUM_BYTES_IN_256_BIT_VERSION = 32;
	public static final int NUM_ROUNDS_IN_256_BIT_VERSION = 14;
	
	public AES256() {
		super();
		name = "AES256";
		numRounds = NUM_ROUNDS_IN_256_BIT_VERSION;
		keySize = NUM_BYTES_IN_256_BIT_VERSION;
	}
	
	public void createKeyColumnForwards(ByteArray key, int columnIndex, int numColumnsPerKey) {
		int previousColumnStart = NUM_ROWS_IN_STATE * (columnIndex - 1);
		
		int row0 = key.get(previousColumnStart + 0);
		int row1 = key.get(previousColumnStart + 1);
		int row2 = key.get(previousColumnStart + 2);
		int row3 = key.get(previousColumnStart + 3);
		int row_temp;
		
		if (columnIndex % numColumnsPerKey == 0) {
			row_temp = row3;
			row3 = SBOX[row0];
			row0 = SBOX[row1] ^ RCON[columnIndex / numColumnsPerKey];
			row1 = SBOX[row2];
			row2 = SBOX[row_temp];
		} else if (columnIndex % numColumnsPerKey == 4) {
			row0 = SBOX[row0];
			row1 = SBOX[row1];
			row2 = SBOX[row2];
			row3 = SBOX[row3];
		}
		
		int columnStart = NUM_ROWS_IN_STATE * columnIndex;
		int previousKeyColumnStart = NUM_ROWS_IN_STATE * (columnIndex - numColumnsPerKey);
		
		key.set(columnStart + 0, key.get(previousKeyColumnStart + 0) ^ row0);
		key.set(columnStart + 1, key.get(previousKeyColumnStart + 1) ^ row1);
		key.set(columnStart + 2, key.get(previousKeyColumnStart + 2) ^ row2);
		key.set(columnStart + 3, key.get(previousKeyColumnStart + 3) ^ row3);
	}
	
	public int getNumActiveComponentsInKeySchedule() {
		return (int)(3.25 * (double)stateSize);
	}
	
	public void createKeyColumnBackwards(ByteArray key, int columnIndex, int numColumnsPerKey) {
		int nextColumnStart = NUM_ROWS_IN_STATE * (columnIndex + numColumnsPerKey - 1);
		
		int row0 = key.get(nextColumnStart + 0);
		int row1 = key.get(nextColumnStart + 1);
		int row2 = key.get(nextColumnStart + 2);
		int row3 = key.get(nextColumnStart + 3);
		int row_temp;
		
		if (columnIndex % numColumnsPerKey == 0) {
			row_temp = row3;
			row3 = SBOX[row0];
			row0 = SBOX[row1] ^ RCON[(columnIndex / numColumnsPerKey) + 1];
			row1 = SBOX[row2];
			row2 = SBOX[row_temp];
		} else if (columnIndex % numColumnsPerKey == 4) {
			row0 = SBOX[row0];
			row1 = SBOX[row1];
			row2 = SBOX[row2];
			row3 = SBOX[row3];
		}
		
		int columnStart = NUM_ROWS_IN_STATE * columnIndex;
		int nextKeyColumnStart = NUM_ROWS_IN_STATE * (columnIndex + numColumnsPerKey);
		
		key.set(columnStart + 0, key.get(nextKeyColumnStart + 0) ^ row0);
		key.set(columnStart + 1, key.get(nextKeyColumnStart + 1) ^ row1);
		key.set(columnStart + 2, key.get(nextKeyColumnStart + 2) ^ row2);
		key.set(columnStart + 3, key.get(nextKeyColumnStart + 3) ^ row3);
	}
	
}
