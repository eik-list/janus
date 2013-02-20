package de.mslab.ciphers;

public class ThreeFish256 extends ThreeFish {
	
	public static final long[] INITIAL_VALUE = {
		0xFC9DA860D048B449L, 0x2FCA66479FA7D833L, 0xB33BC3896656840FL, 0x6A54E920FDE8DA69L
	};
	public static final int NUM_BYTES_IN_256_BIT = 256 / Byte.SIZE;
	public static final int NUM_ROUNDS = 72;
	public static final int[][] MIX = {
		{14, 16}, {52, 57}, {23, 40}, { 5, 37}, {25, 33}, {46, 12}, {58, 22}, {32, 32}
		// { 5, 56 }, { 36, 28 }, { 13, 46 }, { 58, 44 }, { 26, 20 }, { 53, 35 }, { 11, 42 }, { 59, 50 }
	};
	public static final int[] PERMUTE = { 0, 3, 2, 1 };
	
	public ThreeFish256() {
		super();
		name = "ThreeFish256";
		keySize = NUM_BYTES_IN_256_BIT;
		numRounds = NUM_ROUNDS;
		stateSize = NUM_BYTES_IN_256_BIT;
		mixConstants = MIX;
		permuteConstants = PERMUTE;
	}
	
}
