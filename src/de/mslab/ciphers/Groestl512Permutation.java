package de.mslab.ciphers;


public class Groestl512Permutation extends GroestlPermutation {
	
	public static final int NUM_BYTES_IN_512_BIT = 512 / Byte.SIZE;
	public static final int NUM_BYTES_IN_1024_BIT = 1024 / Byte.SIZE;
	public static final int NUM_ROUNDS_IN_512_BIT_VERSION = 14;
	
	public static final int NUM_COLUMNS_IN_STATE = 16;
	public static final int NUM_ROWS_IN_STATE = 8;
	
	public static final int[] P_SHIFT_CONSTANTS = { 0, 1, 2, 3, 4, 5, 6, 11 };
	public static final int[] Q_SHIFT_CONSTANTS = { 1, 3, 5, 11, 0, 2, 4, 6 };
	
	public Groestl512Permutation(boolean isPermutationP) {
		super();
		this.numColumns = NUM_COLUMNS_IN_STATE;
		this.numRounds = NUM_ROUNDS_IN_512_BIT_VERSION;
		this.numRows = NUM_ROWS_IN_STATE;
		this.stateSize = NUM_BYTES_IN_1024_BIT;
		this.isPermutationP = isPermutationP;
		this.shiftConstants = isPermutationP ? P_SHIFT_CONSTANTS : Q_SHIFT_CONSTANTS;
	}
	
}
