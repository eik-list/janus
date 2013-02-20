package de.mslab.ciphers;

public class ThreeFish512 extends ThreeFish {
	
	public static final long[] INITIAL_VALUE = {
		0x4903ADFF749C51CEL, 0x0D95DE399746DF03L, 0x8FD1934127C79BCEL, 0x9A255629FF352CB1L, 
		0x5DB62599DF6CA7B0L, 0xEABE394CA9D5C3F4L, 0x991112C71A75B523L, 0xAE18A40B660FCC33L
	};
	public static final int NUM_BYTES_IN_512_BIT = 512 / 8;
	public static final int NUM_ROUNDS = 72;
	public static final int[][] MIX = {
		{46, 36, 19, 37}, {33, 27, 14, 42}, {17, 49, 36, 39}, {44,  9, 54, 56}, 
		{39, 30, 34, 24}, {13, 50, 10, 17}, {25, 29, 39, 43}, { 8, 35, 56, 22}
		//{ 38, 30, 50, 53 }, { 48, 20, 43, 31 }, { 34, 14, 15, 27 }, { 26, 12, 58, 7 }, 
		//{ 33, 49, 8, 42 }, { 39, 27, 41, 14 }, { 29, 26, 11, 9 }, { 33, 51, 39, 35 }
	};
	public static final int[] PERMUTE = { 2, 1, 4, 7, 6, 5, 0, 3 };
	
	public ThreeFish512() {
		super();
		name = "ThreeFish512";
		keySize = NUM_BYTES_IN_512_BIT;
		numRounds = NUM_ROUNDS;
		stateSize = NUM_BYTES_IN_512_BIT;
		mixConstants = MIX;
		permuteConstants = PERMUTE;
	}
	
}
