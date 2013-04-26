package de.mslab.matching;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.ciphers.helpers.RecomputedOperationsCounter;
import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;

/**
 * Context, which bundles the parameters for the MatchingDifferentialBuilder.
 * 
 */
public class MatchingContext {
	
	/**
	 * The target biclique, for which the matching phase shall be analyzed.  
	 * For the matching, the rounds of the bicliques and the keys are important. 
	 */
	public Biclique biclique;
	/**
	 * The target cipher, to compute differential trails for the matching phase.
	 */
	public RoundBasedBlockCipher cipher;
	/**
	 * Counts the number of active components in non-linear operations during the matching. 
	 */
	public RecomputedOperationsCounter counter;
	/**
	 * Used if the isMatchingFixed is set to true.
	 */
	public int matchingRound = -1;
	/**
	 * Used if the isMatchingFixed is set to true.
	 */
	public ByteArray matchingStateDifference;
	/**
	 * By default, the MatchingDifferentialBuilder iterates over all values for matching rounds and 
	 * bytes in the cipher state for partial matching in order to find a matching with minimal number
	 * of active components in non-linear operation. You can set this flag to <code>true</code>, 
	 * and set the values matchingRound and activeMatchingByte. Then, the MatchingDifferentialBuilder 
	 * will not iterate over rounds and bytes to decrease the computational effort. 
	 */
	public boolean isMatchingFixed = false;
	/**
	 * By default, the matching interval covers the rounds from start of the cipher to the first round 
	 * of the biclique (if the biclique is located at the end), or the rounds from after the last round
	 * of the biclique to the end of the cipher. For ciphers where the matching over the full 
	 * number of rounds would be inefficient, you can change the start of the matching by setting this
	 * parameter to mount a matching for a round reduced version of the cipher.  
	 */
	public int matchingFromRound = -1;
	/**
	 * By default, the matching interval covers the rounds from start of the cipher to the first round 
	 * of the biclique (if the biclique is located at the end), or the rounds from after the last round
	 * of the biclique to the end of the cipher. For ciphers where the matching over the full 
	 * number of rounds would be inefficient, you can change the end of the matching by setting this
	 * parameter to mount a matching for a round reduced version of the cipher.  
	 */
	public int matchingToRound = -1;
	/**
	 * The class MatchingDifferentialBuilder can create the differentials in the matching phases 
	 * in multiple iterations with randomized key differences each.   
	 */
	public int numIterations = 50;
	/**
	 * Set this value to influence the number of bytes, which are used at the matching state for a partial 
	 * matching. The more bytes are used, the more components may need to be recomputed. 
	 * Thus, the more bytes are used, the higher the computational complexity.  
	 */
	public int numMatchingBits = Byte.SIZE;
	/**
	 * The current iteration of the MatchingDifferentialBuilder.
	 */
	protected int iteration;
	
	public MatchingContext(Biclique biclique, RoundBasedBlockCipher cipher,
		RecomputedOperationsCounter counter) {
		this.biclique = biclique;
		this.cipher = cipher;
		this.counter = counter;
	}
	
	public MatchingContext(Biclique biclique, RoundBasedBlockCipher cipher,
		RecomputedOperationsCounter counter, int matchingRound, ByteArray matchingStateDifference) {
		this.biclique = biclique;
		this.cipher = cipher;
		this.counter = counter;
		this.matchingRound = matchingRound;
		this.matchingStateDifference = matchingStateDifference;
	}
	
}
