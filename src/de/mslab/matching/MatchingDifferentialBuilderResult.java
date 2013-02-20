package de.mslab.matching;

import de.mslab.core.Differential;

/**
 * This class encapsulates the differentials of a matching of an independent-biclique attack.
 * It is used by the {@link MatchingDifferentialBuilder} to store the results of its calculation. 
 */
public class MatchingDifferentialBuilderResult {
	
	public int bestMatchingRound;
	public int bestMatchingByte;
	public int dimension;
	public int matchingFromRound = -1;
	public int matchingToRound = -1;
	public int minNumActiveBytes;
	public int numBicliqueRounds;
	public int numRounds;
	
	public Differential p_to_v;
	public Differential s_to_v;
	public Differential v_to_p;
	public Differential v_to_s;
	public Differential s_mergedto_v;
	public Differential p_mergedto_v;
	
}
