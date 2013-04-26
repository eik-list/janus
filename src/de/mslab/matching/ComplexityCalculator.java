package de.mslab.matching;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.utils.MathUtil;

/**
 * Calculates the complexity of a biclique attack.
 * 
 */
public class ComplexityCalculator {
	
	private RoundBasedBlockCipher cipher;
	
	/**
	 * Calculates the complexity of a biclique attack.
	 * @param cipher The cipher on which the attack runs is needed to determine state size and key size.
	 * @param dimension The dimension of the biclique is required to many parts in the calculation, 
	 * for instance for the number of key groups, number of precomputations, or number of recomputations.
	 * @param numActiveBytesInMatching The number of bytes in which one matches is required to 
	 * compute the complexity to eliminate false positives.
	 * @param numBicliqueRounds The number of rounds in the biclique is required to compute the complexity 
	 * for the biclique.
	 */
	public ComplexityCalculationResult computeComplexity(RoundBasedBlockCipher cipher, 
		int dimension, int numActiveBytesInMatching, int numBicliqueRounds, int numMatchingRounds,
		int numMatchingBits) {
		
		this.cipher = cipher;
		ComplexityCalculationResult result = new ComplexityCalculationResult();
		
		result.decryptionsComplexity = determineDecryptionsComplexity(dimension); // 2^{8}
		result.precomputationsComplexity = determinePrecomputationsComplexity(dimension, numBicliqueRounds, numMatchingRounds); // 2^{7.485}
		result.bicliqueComplexity = determineBicliqueComplexity(dimension, numBicliqueRounds, numMatchingRounds); // 2^{7.26}
		result.recomputationsComplexity = determineRecomputationsComplexity(cipher, dimension, numActiveBytesInMatching, numMatchingRounds + numBicliqueRounds); // 2^{14.14}
		result.falsePosComplexityLog = determineFalsePositiveComplexityLog(dimension, numMatchingBits);
		
		result.decryptionsComplexityLog = MathUtil.log2(result.decryptionsComplexity); // 8
		result.bicliqueComplexityLog = MathUtil.log2(result.bicliqueComplexity); // 7.26
		result.precomputationsComplexityLog = MathUtil.log2(result.precomputationsComplexity); // 7.485
		result.recomputationsComplexityLog = MathUtil.log2(result.recomputationsComplexity); // 14.14
		result.falsePosComplexity = Math.pow(2, result.falsePosComplexityLog);

		result.complexityPerBiclique = result.decryptionsComplexity 
			+ result.precomputationsComplexity 
			+ result.bicliqueComplexity 
			+ result.recomputationsComplexity
			+ result.falsePosComplexity; // 2^{14.18}
		
		result.complexityPerBicliqueLog = MathUtil.log2(result.complexityPerBiclique); // 14.18
		result.numBicliquesLog = computeNumBicliquesLog(result, dimension);
		result.totalComplexityLog = result.complexityPerBicliqueLog + (double)result.numBicliquesLog;
		
		this.cipher = null;
		return result;
	}
	
	private int computeNumBicliquesLog(ComplexityCalculationResult result, int dimension) {
		if (cipher.operatesBytewise()) {
			if (cipher.operatesNibblewise()) {
				return (cipher.getKeySize() * Byte.SIZE) - (2 * dimension); // (32 * 4) - (2 * 4) = 120
			} else {
				return (cipher.getKeySize() * Byte.SIZE) - (2 * dimension); // (16 * 8) - (2 * 8) = 112
			}
		} else {
			return (cipher.getKeySize() * Byte.SIZE) - (2 * dimension); // (32 * 8) - (2 * 6) = 244
		}
	}
	
	private double determineFalsePositiveComplexityLog(int dimension, int numMatchingBits) {
		return (2 * dimension) - numMatchingBits;
	}
	
	private double determineDecryptionsComplexity(int dimension) {
		return 1 << dimension; // d = 8 => 2^8 calls for P -> C or C -> P
	}
	
	private double determinePrecomputationsComplexity(int dimension, int numBicliqueRounds, int numMatchingRounds) {
		int numCalls = 1 << dimension; // d = 8 => 2^8!, because 2^8 * 2 rounds + 2^8 * 5 rounds = 2^8 * 7 rounds 
		double relativeFullEncryptions = (double)numMatchingRounds / (double)(numBicliqueRounds + numMatchingRounds); // 7 / 10
		return (double)numCalls * relativeFullEncryptions; // 2^8 * 0.7 = 179,2 = 2^{7.485}
	}
	
	private double determineBicliqueComplexity(int dimension, int numBicliqueRounds, int numRounds) {
		final int numCalls = 1 << (dimension + 1); // d = 8 => 2^9 calls
		final double relativeFullEncryptions = (double)numBicliqueRounds / (double)numRounds; // 3 / 10
		return (double)numCalls * relativeFullEncryptions; // 2^9 * 0.3 = 153,6 = 2^{7.26}
	}
	
	private double determineRecomputationsComplexity(RoundBasedBlockCipher cipher, 
		int dimension, int numActiveBytesInMatching, int numRounds) {
		int numActiveBytesInRegularEncryption = cipher.getNumActiveComponentsInEncryption(numRounds);
		int numActiveBytesInKeySchedule = cipher.getNumActiveComponentsInKeySchedule();
		
		// 55 / (10 * 16 + 2.5 * 16) = 55 / 200 = 0.275
		double relativeFullEncryptions = (double)numActiveBytesInMatching / (double)(numActiveBytesInRegularEncryption + numActiveBytesInKeySchedule);
		long numKeysPerBiclique = 1L << (2 * dimension);
		double numFullEncryptionsPerBiclique = (double)numKeysPerBiclique * relativeFullEncryptions; // 2^{16} * 0.275 = 18022,4
		return numFullEncryptionsPerBiclique; // log(18022,4) = 2^{14.14}
	}
	
}
