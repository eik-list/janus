package de.mslab.ciphers;

import de.mslab.core.ByteArray;

public class BKSQ192 extends BKSQ {

	public static final int NUM_BYTES_IN_96_BIT = 96 / Byte.SIZE;
	public static final int NUM_BYTES_IN_192_BIT = 192 / 8;
	public static final int NUM_ROUNDS = 18;
	
	public BKSQ192() {
		super();
		name = "BKSQ192";
		numRounds = NUM_ROUNDS;
		keySize = NUM_BYTES_IN_192_BIT;
		stateSize = NUM_BYTES_IN_96_BIT;
	}
	
	protected void expandKeyColumnBackwards(ByteArray key, int columnIndex) {
		int numColumnsPerKey = keySize / NUM_ROWS_IN_STATE;
		int nextColumn = NUM_ROWS_IN_STATE * (columnIndex + numColumnsPerKey) - 1;
		int roundConstantIndex = (columnIndex / numColumnsPerKey) + 1;
		
		int row2 = key.get(nextColumn--);
		int row1 = key.get(nextColumn--);
		int row0 = key.get(nextColumn--);
		int row_temp;
		
		if (columnIndex % numColumnsPerKey == 0) {
			row_temp = row0;
			row0 = SBOX[row1] ^ RCON[roundConstantIndex];
			row1 = SBOX[row2];
			row2 = SBOX[row_temp];
		} else if (columnIndex % numColumnsPerKey == 4) {
			row0 = SBOX[row0];
			row1 = SBOX[row1];
			row2 = SBOX[row2];
		}

		int columnStart = NUM_ROWS_IN_STATE * (columnIndex + 1) - 1;
		int nextKeyColumnStart = NUM_ROWS_IN_STATE * (columnIndex + numColumnsPerKey + 1) - 1;
		
		key.set(columnStart--, key.get(nextKeyColumnStart--) ^ row2);
		key.set(columnStart--, key.get(nextKeyColumnStart--) ^ row1);
		key.set(columnStart--, key.get(nextKeyColumnStart--) ^ row0);
	}
	
	protected void expandKeyColumnForwards(ByteArray key, int columnIndex) {
		int previousColumn = NUM_ROWS_IN_STATE * (columnIndex - 1);
		
		int row0 = key.get(previousColumn++);
		int row1 = key.get(previousColumn++);
		int row2 = key.get(previousColumn++);
		int row_temp;
		int numColumnsPerKey = keySize / NUM_ROWS_IN_STATE;
		
		if (columnIndex % numColumnsPerKey == 0) {
			row_temp = row0;
			row0 = SBOX[row1] ^ RCON[columnIndex / numColumnsPerKey];
			row1 = SBOX[row2];
			row2 = SBOX[row_temp];
		} else if (columnIndex % numColumnsPerKey == 4) {
			row0 = SBOX[row0];
			row1 = SBOX[row1];
			row2 = SBOX[row2];
		}
		
		int columnPosition = columnIndex * NUM_ROWS_IN_STATE;
		int previousKeyColumn = (columnIndex - numColumnsPerKey) * NUM_ROWS_IN_STATE;
		
		key.set(columnPosition++, key.get(previousKeyColumn++) ^ row0);
		key.set(columnPosition++, key.get(previousKeyColumn++) ^ row1);
		key.set(columnPosition++, key.get(previousKeyColumn++) ^ row2);
	}
	
	public int getNumActiveComponentsInKeySchedule() {
		return (int)(4.25 * (double)stateSize);
	}
	
}
