package de.mslab.matching;

/**
 * This class encapsulates the complexity summands for the computational complexity of independent-biclique attack.
 * It is used by the {@link ComplexityCalculator} to store the results of its calculation.
 */
public class ComplexityCalculationResult {
	
	public double bicliqueComplexity;
	public double bicliqueComplexityLog;
	public double decryptionsComplexity;
	public double decryptionsComplexityLog;
	public double falsePosComplexity;
	public double falsePosComplexityLog;
	public double precomputationsComplexity;
	public double precomputationsComplexityLog;
	public double recomputationsComplexity;
	public double recomputationsComplexityLog;
	
	public double complexityPerBiclique;
	public double complexityPerBicliqueLog;
	public double numBicliquesLog;
	public double totalComplexityLog;
	
}
