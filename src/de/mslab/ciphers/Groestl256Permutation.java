package de.mslab.ciphers;

public class Groestl256Permutation extends GroestlPermutation {
	
	public static final int NUM_BYTES_IN_256_BIT = 256 / Byte.SIZE;
	public static final int NUM_BYTES_IN_512_BIT = 512 / Byte.SIZE;
	public static final int NUM_ROUNDS_IN_256_BIT_VERSION = 10;
	public static final int NUM_COLUMNS_IN_STATE = 8;
	public static final int NUM_ROWS_IN_STATE = 8;
	
	public static final int[] P_SHIFT_CONSTANTS = { 0, 1, 2, 3, 4, 5, 6, 7 };
	public static final int[] Q_SHIFT_CONSTANTS = { 1, 3, 5, 7, 0, 2, 4, 6 };
	
	public Groestl256Permutation(boolean isPermutationP) {
		super();
		this.numColumns = NUM_COLUMNS_IN_STATE;
		this.numRounds = NUM_ROUNDS_IN_256_BIT_VERSION;
		this.numRows = NUM_ROWS_IN_STATE;
		this.stateSize = NUM_BYTES_IN_512_BIT;
		this.isPermutationP = isPermutationP;
		this.shiftConstants = isPermutationP ? P_SHIFT_CONSTANTS : Q_SHIFT_CONSTANTS;
	}
	
}
