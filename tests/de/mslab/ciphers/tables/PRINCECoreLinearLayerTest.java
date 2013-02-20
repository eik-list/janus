package de.mslab.ciphers.tables;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.ciphers.PRINCECore;
import de.mslab.utils.Formatter;
import de.mslab.utils.Logger;

/**
 * Generates tables for multiplication in the Galois Field for Prince.  
 */
public class PRINCECoreLinearLayerTest {
	
	private static final byte[][] M0 = {{0,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};  
	private static final byte[][] M1 = {{1,0,0,0},{0,0,0,0},{0,0,1,0},{0,0,0,1}};
	private static final byte[][] M2 = {{1,0,0,0},{0,1,0,0},{0,0,0,0},{0,0,0,1}};
	private static final byte[][] M3 = {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,0}};
	
	private static final byte[][][][] _M_0 = {{M0,M1,M2,M3},{M1,M2,M3,M0},{M2,M3,M0,M1},{M3,M0,M1,M2}};
	private static final byte[][][][] _M_1 = {{M1,M2,M3,M0},{M2,M3,M0,M1},{M3,M0,M1,M2},{M0,M1,M2,M3}};
	
	private static int[] M_0;
	private static int[] M_1;
	
	private static Logger logger = Logger.getLogger();
	private static SimplePRINCECore cipher;
	
	@BeforeClass
	public static void setUp() {
		cipher = new SimplePRINCECore();
		
		M_0 = build16x16Matrix(_M_0);
		M_1 = build16x16Matrix(_M_1);
		
		final int[] expectedM_0 = { 
			0x0888,0x4044,0x2202,0x1110,0x8880,0x0444,0x2022,0x1101,
			0x8808,0x4440,0x0222,0x1011,0x8088,0x4404,0x2220,0x0111
		};
		final int[] expectedM_1 = {
			0x8880,0x0444,0x2022,0x1101,0x8808,0x4440,0x0222,0x1011,
			0x8088,0x4404,0x2220,0x0111,0x0888,0x4044,0x2202,0x1110
		};
		
		assertArrayEquals(expectedM_0, M_0);
		assertArrayEquals(expectedM_1, M_1);
		
		M_0 = PRINCECore.createTable(M_0);
		M_1 = PRINCECore.createTable(M_1);
	}
	
	@AfterClass
	public static void tearDown() {
		cipher = null;
		M_0 = null;
		M_1 = null;
	}
	
	@Test
	public void testM0Table() {
		int[] inputs = { 
			0x0000, 0x0001, 0x0015, 0x0011, 0x0100, 0xFFF0, 0xFFFF, 0xBBBB
		};
		int[] expectedOutputs = { 
			0x0000, 0x0111, 0x5504, 0x1100, 0x1101, 0x1842, 0xFFFF, 0xBBBB
		};
		testTable(M_0, inputs, expectedOutputs);
	}
	
	@Test
	public void testM1Table() {
		int[] inputs = { 
			0x0000, 0x0001, 0x0015, 0x0011, 0x0100, 0xFFF0, 0xFFFF, 0xBBBB
		};
		int[] expectedOutputs = { 
			0x0000, 0x1110, 0x5045, 0x1001, 0x1011, 0x8421, 0xFFFF, 0xBBBB
		};
		testTable(M_1, inputs, expectedOutputs);
	}
	
	private void testTable(int[] table, int[] inputs, int[] expectedOutputs) {
		int input, output, expected;
		
		for (int i = 0; i < expectedOutputs.length; i++) {
			input = inputs[i];
			output = table[input];
			expected = expectedOutputs[i];
			assertEquals(expected, output);
		}
	}
	
	@Test
	public void testHowManyValuesPassM0Unchanged() {
		final int numResults = 1 << 16;
		int numEqualOutputsExpected = 256;
		testHowManyValuesPassTableUnchanged(PRINCECore.M_0, numResults, numEqualOutputsExpected);
	}
	
	@Test
	public void testHowManyValuesPassM1Unchanged() {
		final int numResults = 1 << 16;
		int numEqualOutputsExpected = 256;
		testHowManyValuesPassTableUnchanged(PRINCECore.M_1, numResults, numEqualOutputsExpected);
	}

	private void testHowManyValuesPassTableUnchanged(int[] table, int numResults, int numEqualOutputsExpected) {
		int output;
		int numEqualOutputs = 0;
		
		for (int input = 0; input < numResults; input++) {
			output = table[input];
			
			if (input == output) {
				numEqualOutputs++;
			}
		}
		
		assertEquals(numEqualOutputsExpected, numEqualOutputs);
	}
	
	@Test
	public void testHowManyValuesPassM0WithOnlyOneActiveNibble() {
		final int numResults = 1 << 16;
		final int numNibbles = 4;
		int[] masks = { 0x0FFF, 0xF0FF, 0xFF0F, 0xFFF0 };
		int count;
		
		for (int i = 0; i < numNibbles; i++) {
			count = testHowManyValuesPassTableWithOnlyOneActiveNibble(PRINCECore.INVERSE_M_0, numResults, masks[i]);
			logger.info("i {0} count {1}", i, count);
		}
	}

	@Test
	public void testHowManyValuesPassM1WithOnlyOneActiveNibble() {
		final int numResults = 1 << 16;
		final int numNibbles = 4;
		int[] masks = { 0x0FFF, 0xF0FF, 0xFF0F, 0xFFF0 };
		int count;
		
		for (int i = 0; i < numNibbles; i++) {
			count = testHowManyValuesPassTableWithOnlyOneActiveNibble(PRINCECore.INVERSE_M_1, numResults, masks[i]);
			logger.info("i {0} count {1}", i, count);
		}
	}
	
	private int testHowManyValuesPassTableWithOnlyOneActiveNibble(int[] table, int numResults, int mask) {
		int value, count = 0;
		
		for (int i = 0; i < numResults; i++) {
			value = table[i];
			
			if ((value ^ i) == 0) {
				logger.info("i {0} value {1}", Formatter.intToHexString(i), Formatter.intToHexString(value));
				count++;
			}
		}
		
		return count;
	}
	
	@Test
	public void testThatM0IsDifferencePreserving() {
		final long alpha = 0xc0ac29b7c97c50ddL;
		final int numTestValues = 1 << 16;
		final long alphaShifted = cipher.$shiftRows(alpha);
		final int differenceForColumn0 = (int)((alphaShifted >>> 48) & 0xFFFF);
		final int differenceForColumn3 = (int)(alphaShifted & 0xFFFF);
		logger.info("alpha(0, 3) {0} {1}", differenceForColumn0, differenceForColumn3);
		testThatTableIsDifferencePreserving(PRINCECore.INVERSE_M_0, numTestValues, differenceForColumn0);
		testThatTableIsDifferencePreserving(PRINCECore.INVERSE_M_0, numTestValues, differenceForColumn3);
	}
	
	@Test
	public void testThatM1IsDifferencePreserving() {
		final long alpha = 0xc0ac29b7c97c50ddL;
		final int numTestValues = 1 << 16;
		final long alphaShifted = cipher.$shiftRows(alpha);
		final int differenceForColumn1 = (int)((alphaShifted >>> 32) & 0xFFFF);
		final int differenceForColumn2 = (int)((alphaShifted >>> 16) & 0xFFFF);
		testThatTableIsDifferencePreserving(PRINCECore.INVERSE_M_1, numTestValues, differenceForColumn1);
		testThatTableIsDifferencePreserving(PRINCECore.INVERSE_M_1, numTestValues, differenceForColumn2);
	}
	
	@Test
	public void testThatM0IsAlwaysDifferencePreserving() {
		final int numTestValues = 1 << 16;
		final int numTestDifferences = 1 << 6;
		int difference = 0;
		
		for (int i = 0; i < numTestDifferences; i++) {
			difference = (int)(Math.random() * numTestValues);
			testThatTableIsDifferencePreserving(M_0, numTestValues, difference);
		}
	}

	@Test
	public void testThatM1IsAlwaysDifferencePreserving() {
		final int numTestValues = 1 << 16;
		final int numTestDifferences = 1 << 6;
		int difference = 0;
		
		for (int i = 0; i < numTestDifferences; i++) {
			difference = (int)(Math.random() * numTestValues);
			testThatTableIsDifferencePreserving(M_1, numTestValues, difference);
		}
	}
	
	private void testThatTableIsDifferencePreserving(int[] table, int numTestValues, int inputDifference) {
		int input1, input2, output1, output2, outputDifference;
		int firstOutputDifference = 0;
		
		for (int i = 0; i < numTestValues; i++) {
			input1 = i;
			input2 = input1 ^ inputDifference;
			output1 = table[input1];
			output2 = table[input2];
			outputDifference = output1 ^ output2;
			
			if (i == 0) {
				firstOutputDifference = outputDifference;
			} else {
				assertEquals(firstOutputDifference, outputDifference);
			}
		}
		
		logger.info("{1} <-(M')- {0}", inputDifference, firstOutputDifference);
	}
	
	@Test
	public void testThatM0MapsDifferencesUniquely() {
		// SR^{-1}(alpha) = (51581 || 10716 || 49319 || 20668) = f
		// M'^{-1}(f) = 	(17059 || 13674 || 23866 ||  4067)
		final int column0Input = 51581;
		final int column0Output = 17059;
		final int column3Input = 20668;
		final int column3Output = 4067;
		testThatTableMapsDifferencesUniquely(PRINCECore.INVERSE_M_0, column0Input, column0Output);
		testThatTableMapsDifferencesUniquely(PRINCECore.INVERSE_M_0, column3Input, column3Output);
	}
	
	@Test
	public void testThatM1MapsDifferencesUniquely() {
		// SR^{-1}(alpha) = (51581 || 10716 || 49319 || 20668) = f
		// M'^{-1}(f) = 	(17059 || 13674 || 23866 ||  4067)
		final int column1Input = 10716;
		final int column1Output = 13674;
		final int column2Input = 49319;
		final int column2Output = 23866;
		testThatTableMapsDifferencesUniquely(PRINCECore.INVERSE_M_1, column1Input, column1Output);
		testThatTableMapsDifferencesUniquely(PRINCECore.INVERSE_M_1, column2Input, column2Output);
	}
	
	private void testThatTableMapsDifferencesUniquely(int[] table, int expectedInputDifference, int expectedOutputDifference) {
		// should be 2^{16}, but we have run this and checked this once (takes ~40sec per run)
		// for other runs, 1 << 13 is enough; it is more important that all tests should run fast in TDD
		final int numTestedValues = 1 << 13; 
		int output1, output2, outputDifference, inputDifference;
		
		for (int i = 0; i < numTestedValues; i++) {
			for (int j = 0; j < numTestedValues; j++) {
				output1 = table[i];
				output2 = table[j];
				outputDifference = output1 ^ output2;
				
				if (outputDifference == expectedOutputDifference) {
					inputDifference = i ^ j;
					assertEquals(expectedInputDifference, inputDifference);
				}
			}
		}
	}
	
	@Test
	public void testSBoxDifferences() {
		final int numTestValues = 16;
		int[] differenceOccurencesMap = null;
		int output1, output2, outputDifference, maxNumOccurences;
		int numPossibleDifferences, totalNumPossibleDifferences = 0;
		
		for (int inputDifference = 0; inputDifference < numTestValues; inputDifference++) {
			differenceOccurencesMap = new int[numTestValues];
			maxNumOccurences = 0;
			numPossibleDifferences = 0;
			
			for (int i = 0; i < numTestValues; i++) {
				output1 = PRINCECore.INVERSE_SBOX[i];
				output2 = PRINCECore.INVERSE_SBOX[i ^ inputDifference];
				outputDifference = output1 ^ output2;
				differenceOccurencesMap[outputDifference]++;
				
				if (differenceOccurencesMap[outputDifference] > maxNumOccurences) {
					maxNumOccurences = differenceOccurencesMap[outputDifference];
				}
			}
			
			for (int i = 0; i < differenceOccurencesMap.length; i++) {
				if (differenceOccurencesMap[i] != 0) {
					numPossibleDifferences++;
					totalNumPossibleDifferences++;
				}
			}
			
			logger.info("{0} -> {1}: {2} possible", inputDifference, Arrays.toString(differenceOccurencesMap), numPossibleDifferences);
		}
		
		logger.info("num possible: {0}", totalNumPossibleDifferences);
	}
	
	@Test
	public void testSBoxInputs() {
		// Given: two differences beta, gamma with gamma -(S)-> beta
		// Task: how many values i,j = i \oplus gamma exist with S[i] ^ S[j] = beta 
		// These would be value candidates for the values before the S-box layer 
		final int numTestValues = 16;
		int j;
		int differenceOccurences = 0;
		int[] map = new int[17];
		
		for (int inputDifference = 0; inputDifference < numTestValues; inputDifference++) {
			for (int outputDifference = 0; outputDifference < numTestValues; outputDifference++) {
				differenceOccurences = 0;
				
				for (int i = 0; i < numTestValues; i++) {
					j = i ^ inputDifference;
					if ((PRINCECore.SBOX[i] ^ PRINCECore.SBOX[j]) == outputDifference) {
						differenceOccurences++;
					}
				}
				
				if (differenceOccurences != 0) {
					map[differenceOccurences]++;
				}
				
			}
		}
		
		logger.info("Num occurences of {0}", Arrays.toString(map));
	}
	
	@Test
	/**
	 * Tests if alpha can be obtained after the middle part, this would be great 
	 * to skip one entire round, but not working, since 0x50dd is not a difference output
	 * of M0
	 */
	public void testObtainAlphaDifferenceAfterMiddlePart() {
		final int numTestValues = 1 << 16;
		final int[] m0ResultsMap = new int[numTestValues];
		final int[] m1ResultsMap = new int[numTestValues];
		int value;
		
		for (int i = 0; i < numTestValues; i++) {
			value = PRINCECore.SBOX[(i >>> 12) & 0xF] << 12
				| PRINCECore.SBOX[(i >>> 8) & 0xF] << 8
				| PRINCECore.SBOX[(i >>> 4) & 0xF] << 4
				| PRINCECore.SBOX[i & 0xF];
			value = PRINCECore.M_0[value];
			value = PRINCECore.INVERSE_SBOX[(value >>> 12) & 0xF] << 12
				| PRINCECore.INVERSE_SBOX[(value >>> 8) & 0xF] << 8
				| PRINCECore.INVERSE_SBOX[(value >>> 4) & 0xF] << 4
				| PRINCECore.INVERSE_SBOX[value & 0xF];
			m0ResultsMap[value ^ i]++;
		}
		
		for (int i = 0; i < numTestValues; i++) {
			value = PRINCECore.SBOX[(i >>> 12) & 0xF] << 12
				| PRINCECore.SBOX[(i >>> 8) & 0xF] << 8
				| PRINCECore.SBOX[(i >>> 4) & 0xF] << 4
				| PRINCECore.SBOX[i & 0xF];
			value = PRINCECore.M_1[value];
			value = PRINCECore.INVERSE_SBOX[(value >>> 12) & 0xF] << 12
				| PRINCECore.INVERSE_SBOX[(value >>> 8) & 0xF] << 8
				| PRINCECore.INVERSE_SBOX[(value >>> 4) & 0xF] << 4
				| PRINCECore.INVERSE_SBOX[value & 0xF];
			m1ResultsMap[value ^ i]++;
		}
		
		int numResultsForColumn = 0;
		int totalNumResults = 1;
		numResultsForColumn = 0;
		
		for (int i = 0; i < 16; i++) {
			numResultsForColumn += m0ResultsMap[0x00ac | (i << 12)];
			logger.info("M0 {0} {1} {2}", i, 0x00ac | (i << 12), m0ResultsMap[0x00ac | (i << 12)]);
		}
		
		logger.info("num differences for column {0}: {1}", 0, numResultsForColumn);
		totalNumResults *= numResultsForColumn;
		numResultsForColumn = 0;
		
		for (int i = 0; i < 16; i++) {
			numResultsForColumn += m1ResultsMap[0x29b0 | i];
			logger.info("M1 {0} {1} {2}", i, 0x29b0  | i, m1ResultsMap[0x29b0 | i]);
		}
		
		logger.info("num differences for column {0}: {1}", 1, numResultsForColumn);
		totalNumResults *= numResultsForColumn;
		numResultsForColumn = 0;
		
		for (int i = 0; i < 16; i++) {
			numResultsForColumn += m1ResultsMap[0xc90c | (i << 4)];
			logger.info("M1 {0} {1} {2}", i, 0xc90c | (i << 4), m1ResultsMap[0xc90c | (i << 4)]);
		}
		
		logger.info("num differences for column {0}: {1}", 2, numResultsForColumn);
		totalNumResults *= numResultsForColumn;
		numResultsForColumn = 0;
		
		for (int i = 0; i < 16; i++) {
			numResultsForColumn += m0ResultsMap[0x50dd | (i << 8)];
			logger.info("M1 {0} {1} {2}", i, 0x50dd | (i << 8), m0ResultsMap[0x50dd | (i << 8)]);
		}
		
		logger.info("num differences for column {0}: {1}", 3, numResultsForColumn);
		totalNumResults *= numResultsForColumn;
		logger.info("total num results {0}", totalNumResults);
	}
	
	@Test
	/**
	 * How many values x exist, which have a difference
	 * 0 0 0 x 		x 0 0 0		0 x 0 0		0 0 x 0
	 * 0 0 x 0 		0 0 0 x		x 0 0 0		0 x 0 0
	 * 0 x 0 0 or	0 0 x 0 or	0 0 0 x or	x 0 0 0
	 * x 0 0 0 		0 x 0 0		0 0 x 0		0 0 0 x
	 * after the middle part.
	 */
	public void testFindNumTrailsForAlmostAlphaDifferenceAfterMiddlePart() {
		final int numColumns = 4;
		final int numTestValues = 1 << 16;
		final int[] alpha = { 0xc0ac, 0x29b7, 0xc97c, 0x50dd };
		final int[][] masks = {
			{ 0xFFF0, 0xFF0F, 0xF0FF, 0x0FFF }, 
			{ 0x0FFF, 0xFFF0, 0xFF0F, 0xF0FF },
			{ 0xF0FF, 0x0FFF, 0xFFF0, 0xFF0F }, 
			{ 0xFF0F, 0xF0FF, 0x0FFF, 0xFFF0 }
		};
		int firstColumn, secondColumn, thirdColumn, fourthColumn;
		int[] numSolutionsForColumns;
		int numSolutions = 1;
		
		for (int j = 0; j < 4; j++) {
			numSolutionsForColumns = new int[numColumns];
			
			for (int i = 0; i < numTestValues; i++) {
				firstColumn = applySboxToColumn(i);
				firstColumn = PRINCECore.M_0[firstColumn];
				firstColumn = applyInverseSboxToColumn(firstColumn);
				firstColumn ^= i; // build difference 
				
				if ((firstColumn & masks[j][0]) == (alpha[0] & masks[j][0])) {
					numSolutionsForColumns[0]++;
				}
				
				secondColumn = applySboxToColumn(i);
				secondColumn = PRINCECore.M_1[secondColumn];
				secondColumn = applyInverseSboxToColumn(secondColumn);
				secondColumn ^= i;
				
				if ((secondColumn & masks[j][1]) == (alpha[1] & masks[j][1])) {
					numSolutionsForColumns[1]++;
				}
				
				thirdColumn = applySboxToColumn(i);
				thirdColumn = PRINCECore.M_1[thirdColumn];
				thirdColumn = applyInverseSboxToColumn(thirdColumn);
				thirdColumn ^= i; // build difference 
				
				if ((thirdColumn & masks[j][2]) == (alpha[2] & masks[j][2])) {
					numSolutionsForColumns[2]++;
				}
				
				fourthColumn = applySboxToColumn(i);
				fourthColumn = PRINCECore.M_0[fourthColumn];
				fourthColumn = applyInverseSboxToColumn(fourthColumn);
				fourthColumn ^= i;
				
				if ((fourthColumn & masks[j][3]) == (alpha[3] & masks[j][3])) {
					numSolutionsForColumns[3]++;
				}
			}
			
			numSolutions = 1;
			
			for (int i = 0; i < numColumns; i++) {
				numSolutions *= numSolutionsForColumns[i];
				logger.info("trial {2} column {1} {0} solutions", numSolutionsForColumns[i], i, j);
			}
			
			logger.info("trial {0} {1} solutions", j, numSolutions);
		}
	}
	
	@Test
	public void testFindNumPossibleTrailsForTwoRoundAttack() {
		final int numColumns = 4;
		final int numSBoxInputs = 16;
		final int numTestValues = 1 << 16;
		final int[][] matrices = { 
			PRINCECore.INVERSE_M_0, PRINCECore.INVERSE_M_1, PRINCECore.INVERSE_M_1, PRINCECore.INVERSE_M_0, 
		};
		final int[] shifts = { 4, 0, 12, 8 };
		final int[] alpha = { 0xc0ac, 0x29b7, 0xc97c, 0x50dd };
		final int numValidDifferencesExpected = 16;
		
		int[][] a = new int[numColumns][numTestValues];
		int sboxDifference, numValidDifferencesForColumn, gamma = 0;
		int[][] validDifferences = new int[numColumns][numValidDifferencesExpected];
		
		// Four columns go in the inverse matrix multiplications:
		// (00x0, 000x, x000, 0x00) -> (gamma^1, gamma^2, gamma^3, gamma^4);
		for (int k = 0; k < numColumns; k++) {
			sboxDifference = 0;
			numValidDifferencesForColumn = 0;
			
			for (int j = 0; j < numSBoxInputs; j++) {
				for (int i = 0; i < numTestValues; i++) {
					gamma = matrices[k][i ^ alpha[k]] ^ matrices[k][i | sboxDifference];
					// Count valid differences per column
					if (a[k][gamma] == 0) {
						validDifferences[k][numValidDifferencesForColumn] = gamma;
						numValidDifferencesForColumn++;
					}
					
					a[k][gamma]++;
				}
				
				sboxDifference += 1 << shifts[k];
			}
			
			logger.info("Num valid differences for column {0}: {1}", k, numValidDifferencesForColumn);
		}
		
		// Get numbers of solutions for inverse S-box
		final int[][] inverseSboxDifferenceOccurenceMap = new int[numSBoxInputs][numSBoxInputs];
		int b = 0;
		int[] numSolutionsForInverseSBox = new int[numSBoxInputs];
		
		for (int i = 0; i < numSBoxInputs; i++) {
			for (int j = 0; j < numSBoxInputs; j++) {
				b = PRINCECore.INVERSE_SBOX[j] ^ PRINCECore.INVERSE_SBOX[j ^ i];
				inverseSboxDifferenceOccurenceMap[i][b]++;
			}
			
			for (int j = 0; j < numSBoxInputs; j++) {
				if (inverseSboxDifferenceOccurenceMap[i][j] > 0) {
					numSolutionsForInverseSBox[i]++;
				}
			}
			
			logger.info("{0} {1}, {2} solutions", i, 
				Arrays.toString(inverseSboxDifferenceOccurenceMap[i]), 
				numSolutionsForInverseSBox[i]);
		}
		
		// Determine number of possible differences out of inverse s-box
		int differenceOutOfMatrixLayer;
		int numSolutionsForColumn, numSolutionsForColumnAndInput;
		
		for (int k = 0; k < numColumns; k++) {
			numSolutionsForColumn = 0;
			for (int i = 0; i < numValidDifferencesExpected; i++) {
				differenceOutOfMatrixLayer = validDifferences[k][i];
				numSolutionsForColumnAndInput = numSolutionsForInverseSBox[(differenceOutOfMatrixLayer >>> 12) & 0xF]
					* numSolutionsForInverseSBox[(differenceOutOfMatrixLayer >>> 8) & 0xF]
					* numSolutionsForInverseSBox[(differenceOutOfMatrixLayer >>> 4) & 0xF]
					* numSolutionsForInverseSBox[differenceOutOfMatrixLayer & 0xF];
				
				logger.info("diff out of M'^{-1} {0}: num solutions {1}", 
					Formatter.intToHexString(differenceOutOfMatrixLayer), numSolutionsForColumnAndInput);
				numSolutionsForColumn += numSolutionsForColumnAndInput;
			}
			logger.info("num solutions for column {0}", numSolutionsForColumn);
		}
		
	}
	
	@Test
	/**
	 * Differential trail:
	 * -(RC3_/RC_4 \circ k_1)-> a,b = alpha -(S)-> a,b = beta -(M')-> a,b = gamma
	 * -(SR)-> a,b = gamma -(RC_5/RC_6 \circ k_1)-> g,h
	 * -(S \circ M' \circ S^{-1})-> x,y
	 */
	public void testFindNumPossibleTrailsForThreeRoundAttack() {
		final int numTestValues = 1 << 16;
		final int numColumns = 4;
		final int[] alpha = { 0xc0ac, 0x29b7, 0xc97c, 0x50dd };
		
		int[][] a = new int[numColumns][numTestValues];
		int[][] b = new int[numColumns][numTestValues];
		
		// Fill columns of a,b = alpha
		for (int j = 0; j < numColumns; j++) {
			for (int i = 0; i < numTestValues; i++) {
				a[j][i] = i;
				b[j][i] = i ^ alpha[j];
			}
		}
		
		// Apply sbox
		for (int j = 0; j < numColumns; j++) {
			for (int i = 0; i < numTestValues; i++) {
				a[j][i] = applySboxToColumn(a[j][i]);
				b[j][i] = applySboxToColumn(b[j][i]);
			}
		}
		
		// Apply M'
		for (int i = 0; i < numTestValues; i++) {
			a[0][i] = PRINCECore.M_0[a[0][i]];
			a[1][i] = PRINCECore.M_1[a[1][i]];
			a[2][i] = PRINCECore.M_1[a[2][i]];
			a[3][i] = PRINCECore.M_0[a[3][i]];
			b[0][i] = PRINCECore.M_0[b[0][i]];
			b[1][i] = PRINCECore.M_1[b[1][i]];
			b[2][i] = PRINCECore.M_1[b[2][i]];
			b[3][i] = PRINCECore.M_0[b[3][i]];
		}
		
		int[][] c = new int[numColumns][numTestValues];
		int[][] d = new int[numColumns][numTestValues];
		
		// Apply SR
		for (int i = 0; i < numTestValues; i++) {
			c[0][i] = a[0][i] & 0xF000 | a[1][i] & 0x0F00 | a[2][i] & 0x00F0 | a[3][i] & 0x000F;
			c[1][i] = a[0][i] & 0x000F | a[1][i] & 0xF000 | a[2][i] & 0x0F00 | a[3][i] & 0x00F0;
			c[2][i] = a[0][i] & 0x00F0 | a[1][i] & 0x000F | a[2][i] & 0xF000 | a[3][i] & 0x0F00;
			c[3][i] = a[0][i] & 0x0F00 | a[1][i] & 0x00F0 | a[2][i] & 0x000F | a[3][i] & 0xF000;
			d[0][i] = b[0][i] & 0xF000 | b[1][i] & 0x0F00 | b[2][i] & 0x00F0 | b[3][i] & 0x000F;
			d[1][i] = b[0][i] & 0x000F | b[1][i] & 0xF000 | b[2][i] & 0x0F00 | b[3][i] & 0x00F0;
			d[2][i] = b[0][i] & 0x00F0 | b[1][i] & 0x000F | b[2][i] & 0xF000 | b[3][i] & 0x0F00;
			d[3][i] = b[0][i] & 0x0F00 | b[1][i] & 0x00F0 | b[2][i] & 0x000F | b[3][i] & 0xF000;
		}
		
		// XOR alpha and k_1 to it
		for (int j = 0; j < numColumns; j++) {
			for (int i = 0; i < numTestValues; i++) {
				d[j][i] ^= alpha[j];
			}
		}
		
		// Fill a[0][i] with i
		// Fill a[1][i] with i
		// Fill b[0] with results of S \circ M' \circ S^{-1} of M0
		// Fill b[1] with results of S \circ M' \circ S^{-1} of M1
		for (int i = 0; i < numTestValues; i++) {
			a[0][i] = i;
			b[0][i] = applySboxToColumn(i);
			b[0][i] = PRINCECore.M_0[b[0][i]];
			b[0][i] = applyInverseSboxToColumn(b[0][i]);
			
			a[1][i] = i;
			b[1][i] = applySboxToColumn(i);
			b[1][i] = PRINCECore.M_1[b[1][i]];
			b[1][i] = applyInverseSboxToColumn(b[1][i]);
		}
		
		// How many matches can we get?
		for (int i = 0; i < numTestValues; i++) {
			
		}
	}
	
	private int applySboxToColumn(int value) {
		return PRINCECore.SBOX[(value >>> 12) & 0xF] << 12
			| PRINCECore.SBOX[(value >>> 8) & 0xF] << 8
			| PRINCECore.SBOX[(value >>> 4) & 0xF] << 4
			| PRINCECore.SBOX[value & 0xF];
	}

	private int applyInverseSboxToColumn(int value) {
		return PRINCECore.INVERSE_SBOX[(value >>> 12) & 0xF] << 12
			| PRINCECore.INVERSE_SBOX[(value >>> 8) & 0xF] << 8
			| PRINCECore.INVERSE_SBOX[(value >>> 4) & 0xF] << 4
			| PRINCECore.INVERSE_SBOX[value & 0xF];
	}
	
	/**
	 * From a given 2-dimensional matrix with 4x4 2-dimensional cells of bits:
	 * this method will create the same structure, with integers for the individual rows.   
	 * @param _16x16matrix
	 */
	private static int[] build16x16Matrix(byte[][][][] _16x16matrix) {
		int[] result = new int[16];
		byte[][] mi;
		
		int row, column;
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				mi = _16x16matrix[i][j];
				row = i * 4;
				column = j * 4;
				
				for (int k = 0; k < 4; k++) {
					for (int n = 0; n < 4; n++) {
						result[row] |= mi[k][n] << (15 - column);
						column++;
					}
					
					row++;
					column = j * 4;
				}
			}
		}
		
		return result;
	}
	
}

class SimplePRINCECore extends PRINCECore {

	public long $invertShiftRows(long state) {
		return super.invertShiftRows(state);
	}
	
	public long $shiftRows(long state) {
		return super.shiftRows(state);
	}
	
}
