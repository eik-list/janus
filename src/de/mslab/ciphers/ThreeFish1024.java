package de.mslab.ciphers;

public class ThreeFish1024 extends ThreeFish {
	
	public static final long[] INITIAL_VALUE = {
		0xD593DA0741E72355L, 0x15B5E511AC73E00CL, 0x5180E5AEBAF2C4F0L, 0x03BD41D3FCBCAFAFL, 
		0x1CAEC6FD1983A898L, 0x6E510B8BCDD0589FL, 0x77E2BDFDC6394ADAL, 0xC11E1DB524DCB0A3L, 
		0xD6D14AF9C6329AB5L, 0x6A9B0BFC6EB67E0DL, 0x9243C60DCCFF1332L, 0x1A1F1DDE743F02D4L, 
		0x0996753C10ED0BB8L, 0x6572DD22F2B4969AL, 0x61FD3062D00A579AL, 0x1DE0536E8682E539L
	};
	public static final int NUM_BYTES_IN_1024_BIT = 1024 / 8;
	public static final int NUM_ROUNDS = 80;
	public static final int[][] MIX = {
		{24, 13,  8, 47,  8, 17, 22, 37}, 
		{38, 19, 10, 55, 49, 18, 23, 52}, 
		{33,  4, 51, 13, 34, 41, 59, 17}, 
		{ 5, 20, 48, 41, 47, 28, 16, 25}, 
		{41,  9, 37, 31, 12, 47, 44, 30}, 
		{16, 34, 56, 51,  4, 53, 42, 41}, 
		{31, 44, 47, 46, 19, 42, 44, 25}, 
		{ 9, 48, 35, 52, 23, 31, 37, 20}
		/*{ 55, 43, 37, 40, 16, 22, 38, 12 }, 
		{ 25, 25, 46, 13, 14, 13, 52, 57 },
		{ 33, 8, 18, 57, 21, 12, 32, 54 }, 
		{ 34, 43, 25, 60, 44, 9, 59, 34 }, 
		{ 28, 7, 47, 48, 51, 9, 35, 41 },
		{ 17, 6, 18, 25, 43, 42, 40, 15 }, 
		{ 58, 7, 32, 45, 19, 18, 2, 56 }, 
		{ 47, 49, 27, 58, 37, 48, 53, 56 } */
	};
	public static final int[] PERMUTE = { 0, 9, 2, 13, 6, 11, 4, 15, 10, 7, 12, 3, 14, 5, 8, 1 };
	
	public ThreeFish1024() {
		super();
		name = "ThreeFish1024";
		keySize = NUM_BYTES_IN_1024_BIT;
		numRounds = NUM_ROUNDS;
		stateSize = NUM_BYTES_IN_1024_BIT;
		mixConstants = MIX;
		permuteConstants = PERMUTE;
	}
	
}
