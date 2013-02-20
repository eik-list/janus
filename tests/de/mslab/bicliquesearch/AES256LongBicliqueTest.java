package de.mslab.bicliquesearch;


import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.ciphers.AES256;
import de.mslab.core.ByteArray;
import de.mslab.core.Difference;
import de.mslab.core.Differential;
import de.mslab.utils.Logger;

public class AES256LongBicliqueTest {
	
	private static AES256 aes;
	
	// Si are states after MixColumns in round 5
	private static  int[] S0 = { 
		0x40, 0x30, 0x34, 0xb8, 0x8a, 0x4a, 0xb6, 0xfe, 
		0xba, 0x10, 0x84, 0xaa, 0x52, 0x52, 0x52, 0x52
	};
	private static int[] S1 = {
		0x44, 0x32, 0x36, 0xb8, 0xd2, 0x34, 0xf4, 0xba, 
		0x66, 0x6e, 0xb0, 0x71, 0x7b, 0xf7, 0x7a, 0x3a
	};

	// Ci are ciphertexts after round 9
	private static int[] C0 = {
		0x79, 0x67, 0x2e, 0x3c, 0x18, 0xac, 0x39, 0xfd, 
		0xc0, 0x89, 0x52, 0x40, 0x8e, 0x9e, 0x84, 0x26
	};
	private static int[] C1 = {
		0x5d, 0xe5, 0xa0, 0x09, 0x08, 0xbd, 0xac, 0x6a, 
		0xb5, 0xd3, 0xd9, 0x55, 0xac, 0x54, 0x8a, 0x1e
	};
	
	private static int[] K00 = {
		0x7d, 0x12, 0x12, 0x58, 0x8a, 0xa8, 0x55, 0x66, 
		0xd8, 0xf9, 0xcd, 0xd8, 0xa4, 0x31, 0x0b, 0xcf, 
		0x30, 0x5a, 0x32, 0x54, 0xe8, 0x42, 0xd6, 0xf8, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
	}; 
	private static int[] K01 = {
		0x7d, 0x12, 0x12, 0x58, 0x8a, 0xa8, 0x55, 0x66, 
		0xd8, 0xf9, 0xcd, 0xd8, 0xa4, 0x31, 0x0b, 0xcf,
		0x34, 0x58, 0x30, 0x52, 0xec, 0x40, 0xd4, 0xfe, 
		0x04, 0x02, 0x02, 0x06, 0x04, 0x02, 0x02, 0x06
	};
	private static int[] K10 = {
		0x7d, 0x10, 0xab, 0x5a, 0x8a, 0xaa, 0xec, 0x64,
		0xd8, 0xf9, 0xcd, 0xd8, 0xa4, 0x31, 0x0b, 0xcf, 
		0x30, 0x5a, 0x32, 0x54, 0xe8, 0x42, 0xd6, 0xf8, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
	};
	private static int[] K11 = {
		0x7d, 0x10, 0xab, 0x5a, 0x8a, 0xaa, 0xec, 0x64,
		0xd8, 0xf9, 0xcd, 0xd8, 0xa4, 0x31, 0x0b, 0xcf,
		0x34, 0x58, 0x30, 0x52, 0xec, 0x40, 0xd4, 0xfe, 
		0x04, 0x02, 0x02, 0x06, 0x04, 0x02, 0x02, 0x06
	};
	
	private static Differential s0ToC0;
	private static Differential s0ToC1;
	private static Differential deltaDifferentialFromS0;
	
	private static Differential s1ToC0;
	private static Differential s1ToC1;
	private static Differential deltaDifferentialFromS1;
	
	private static Differential c0ToS0;
	private static Differential c0ToS1;
	private static Differential nablaDifferentialFromC0;

	private static Differential c1ToS0;
	private static Differential c1ToS1;
	private static Differential nablaDifferentialFromC1;
	
	private static Difference k00_k01;
	private static Difference k00_k10;
	private static Difference k11_k01;
	private static Difference k11_k10;
	private static Difference k00_k11;
	
	private static Logger logger = new Logger();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		aes = new AES256();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		aes = null;
	}
	
	@Test
	public void testOriginalBiclique() {
		computeBiclique();
		
		assertStateIsEqualTo(s0ToC0, C0, 9); // assert s0 -> c0
		assertStateIsEqualTo(s0ToC1, C1, 9); // assert s0 -> c1
		
		assertStateIsEqualTo(s1ToC0, C0, 9); // assert s1 -> c0
		assertStateIsEqualTo(s1ToC1, C1, 9); // assert s1 -> c1
		
		assertStateIsEqualTo(c0ToS0, S0, 4); // assert c0 -> s0
		assertStateIsEqualTo(c0ToS1, S1, 4); // assert c0 -> s1
		
		assertStateIsEqualTo(c1ToS0, S0, 4); // assert c1 -> s0
		assertStateIsEqualTo(c1ToS1, S1, 4); // assert c1 -> s1
	}
	
	@Test
	public void testNewBiclique() {
		int change = 0x01;
		K10[1] ^= change;
		K10[2] ^= change;
		K11[1] ^= change;
		K11[2] ^= change;
		/*
		int[] C0AfterRound7 = { 0x0c,0x52,0x52,0x52,0x52,0x6a,0x52,0x52,0x63,0x63,0x63,0x63,0x63,0x63,0x63,0x63 };
		int[] C1AfterRound7 = { 0x0c,0x52,0x52,0x52,0x52,0x6a,0x52,0x52,0x63,0x63,0x63,0x63,0x63,0x63,0x63,0x63 };
		int[] S0AfterRound5 = { 0x39,0xad,0x2d,0x66,0x52,0x52,0x52,0x52,0x52,0x52,0x52,0x52,0x52,0x52,0x52,0x52 };
		int[] S1AfterRound5 = { 0x39,0xad,0x2d,0x60,0x0a,0x2c,0x10,0x16,0x8e,0x2c,0x66,0x89,0x7b,0xf7,0x7a,0x3a };
		C0AfterRound7[0] ^= change;
		*/
		computeBiclique();
		
		K10[1] ^= change;
		K10[2] ^= change;
		K11[1] ^= change;
		K11[2] ^= change;
	}
	
	private static void computeBiclique() {
		s0ToC0 = computeDeltaDifferential(K00, S0);
		s0ToC1 = computeDeltaDifferential(K01, S0);
		deltaDifferentialFromS0 = s0ToC0.clone();
		deltaDifferentialFromS0.xor(s0ToC1);
		
		logger.info("S0 -> C0: {0}", s0ToC0);
		logger.info("S0 -> C1: {0}", s0ToC1);
		logger.info("S0 -> Ci: {0}", deltaDifferentialFromS0);
		
		s1ToC0 = computeDeltaDifferential(K10, S1);
		s1ToC1 = computeDeltaDifferential(K11, S1);
		deltaDifferentialFromS1 = s1ToC0.clone();
		deltaDifferentialFromS1.xor(s1ToC1);
		
		logger.info("S1 -> C0: {0}", s1ToC0);
		logger.info("S1 -> C1: {0}", s1ToC1);
		logger.info("S1 -> Ci: {0}", deltaDifferentialFromS1);
		
		c0ToS0 = computeNablaDifferential(K00, C0);
		c0ToS1 = computeNablaDifferential(K10, C0);
		nablaDifferentialFromC0 = c0ToS0.clone();
		nablaDifferentialFromC0.xor(c0ToS1);
		
		logger.info("C0 -> S0: {0}", c0ToS0);
		logger.info("C0 -> S1: {0}", c0ToS1);
		logger.info("C0 -> Si: {0}", nablaDifferentialFromC0);
		
		c1ToS0 = computeNablaDifferential(K01, C1);
		c1ToS1 = computeNablaDifferential(K11, C1);
		nablaDifferentialFromC1 = c1ToS0.clone();
		nablaDifferentialFromC1.xor(c1ToS1);
		
		logger.info("C1 -> S0: {0}", c1ToS0);
		logger.info("C1 -> S1: {0}", c1ToS1);
		logger.info("C1 -> Si: {0}", nablaDifferentialFromC1);
		
		ByteArray k00 = aes.computeExpandedKey(new ByteArray(K00), 6);
		ByteArray k01 = aes.computeExpandedKey(new ByteArray(K01), 6);
		ByteArray k10 = aes.computeExpandedKey(new ByteArray(K10), 6);
		ByteArray k11 = aes.computeExpandedKey(new ByteArray(K11), 6);
		
		k00_k01 = new Difference(k00, k01);
		k00_k10 = new Difference(k00, k10);
		k11_k01 = new Difference(k11, k01);
		k11_k10 = new Difference(k11, k10);
		k00_k11 = new Difference(k00, k11);

		logger.info("K[0,0] ^ K[0,1]: \n{0}", k00_k01);
		logger.info("K[0,0] ^ K[1,0]: \n{0}", k00_k10);
		logger.info("K[1,1] ^ K[0,1]: \n{0}", k11_k01);
		logger.info("K[1,1] ^ K[1,0]: \n{0}", k11_k10);
		logger.info("K[0,0] ^ K[1,1]: \n{0}", k00_k11);
	}
	
	private static Differential computeDeltaDifferential(int[] keyPart, int[] stateArray) {
		Differential differential = new Differential(5, 9);
		
		ByteArray secretKey = aes.computeExpandedKey(new ByteArray(keyPart), 6);
		aes.setExpandedKey(secretKey);
		
		ByteArray state = new ByteArray(stateArray);
		differential.setStateDifference(4, state);
		
		state = aes.addRoundKey(5, state);
		differential.setStateDifference(5, state);
		differential.setKeyDifference(5, aes.getRoundKey(5));
		
		for (int roundIndex = 6; roundIndex <= 9; roundIndex++) {
			state = aes.encryptRounds(state, roundIndex, roundIndex);
			differential.setStateDifference(roundIndex, state);
			differential.setKeyDifference(roundIndex, aes.getRoundKey(roundIndex));
		}
		
		return differential;
	}
	
	private static Differential computeNablaDifferential(int[] keyPart, int[] ciphertextArray) {
		Differential differential = new Differential(5, 9);
		
		ByteArray secretKey = aes.computeExpandedKey(new ByteArray(keyPart), 6);
		aes.setExpandedKey(secretKey);
		
		ByteArray state = new ByteArray(ciphertextArray);
		
		for (int roundIndex = 9; roundIndex >= 6; roundIndex--) {
			differential.setStateDifference(roundIndex, state);
			differential.setKeyDifference(roundIndex, aes.getRoundKey(roundIndex));
			state = aes.decryptRounds(state, roundIndex, roundIndex);
		}
		
		differential.setStateDifference(5, state);
		differential.setKeyDifference(5, aes.getRoundKey(5));
		
		state = aes.addRoundKey(5, state);
		differential.setStateDifference(4, state);
		
		return differential;
	}
	
	private void assertStateIsEqualTo(Differential differential, int[] compareWith, int round) {
		ByteArray fromState = differential.getStateDifference(round).getDelta();
		ByteArray compare = new ByteArray(compareWith);
		assertTrue(fromState.equals(compare));
	}
	
}
