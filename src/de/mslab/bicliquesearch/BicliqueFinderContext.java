package de.mslab.bicliquesearch;

import de.mslab.bicliquesearch.helpers.BicliqueRater;
import de.mslab.bicliquesearch.helpers.DefaultBicliqueRater;
import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.ciphers.helpers.DifferentialComparator;
import de.mslab.diffbuilder.DifferenceBuilder;

/**
 * Stores the context information for the biclique search.
 */
public class BicliqueFinderContext {
	
	/**
	 * A reference to a cipher instance. Required.
	 */
	public RoundBasedBlockCipher cipher;
	/**
	 * A difference builder which generates forward and backward differences for biclique
	 * search. Required.
	 */
	public DifferenceBuilder differenceBuilder;
	/**
	 * A comparator which compares two differentials each whether they share active non-linear
	 * components or not. Required.
	 */
	public DifferentialComparator comparator;
	/**
	 * The potential starting round of the biclique. Required.
	 */
	public int fromRound;
	/**
	 * The potential ending round of the biclique. Required.
	 */
	public int toRound;
	
	/**
	 * For nearly all ciphers testing all possible key differences would become impractical. 
	 * Thus, we set the differences to those keys with only a maximum active bits, nibbles or bytes, 
	 * as key differences with many active components are likely to deliver shorter independent bicliques.
	 * The optimal value differs due to the specific cipher under test, for example, a good value 
	 * for the AES is 8. This is a required parameter.
	 */
	public int dimension;
	/**
	 * The desired number of threads which will be used in the {@link BicliqueFinder}. Required.
	 */
	public int numThreads = 8;
	/**
	 * If the value of {@link #stopAfterFoundFirstBiclique} is set to true, this instance 
	 * is used to determine a score for a biclique. To reduce the memory costs, the {@link BicliqueFinder}
	 * keeps only those bicliques with a maximum score. The default value is null. If the user does not
	 * specify an instance, the {@link BicliqueFinder} will create and assign an instance of type 
	 * {@link DefaultBicliqueRater} for it after {@link BicliqueFinder#findBicliques()} was invoked.
	 */
	public BicliqueRater bicliqueRater;
	/**
	 * A flag which will stop the search for biciques in the given round range, if one biclique was found.
	 * <code>True</code> by default.
	 */
	public boolean stopAfterFoundFirstBiclique = true;
	/**
	 * All forward and backward differentials will be tested during biclique search whether they are 
	 * independent. Thus, the number of tests is the product of the amount of forward and backward differentials. 
	 * In case when a high number will be tested, the {@link BicliqueFinder} will log the progress of 
	 * testing after each n backward differentials are processed. You can set this n here. 
	 */
	public long logInterval = 10000;
	
	/**
	 * For the {@link BicliqueFinder} only.
	 * If not all forward and backward differentials can be stored in memory, the testing phase in the 
	 * {@link BicliqueFinder} is done in iterations. Stores the current iteration. 
	 */
	protected long iterationIndex;
	/**
	 * For the {@link BicliqueFinder} only.
	 * If not all forward and backward differentials can be stored in memory, the testing phase in the 
	 * {@link BicliqueFinder} is done in iterations. Stores the necessary number of iterations. 
	 */
	protected long numIterations;
	/**
	 * For the {@link BicliqueFinder} only.
	 * If not all forward and backward differentials can be stored in memory, the testing phase in the 
	 * {@link BicliqueFinder} is done in iterations. Stores the number of tested differentials per iteration.  
	 */
	protected long numDifferentialsPerIteration;
	/**
	 * For the {@link BicliqueFinder} only. Stores the total number of differentials to test. 
	 */
	protected long numDifferentialsToTest;
	
}
