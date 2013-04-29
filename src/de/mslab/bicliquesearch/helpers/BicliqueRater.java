package de.mslab.bicliquesearch.helpers;

import de.mslab.core.Biclique;

public interface BicliqueRater {
	
	int determineScoreForBiclique(Biclique biclique);
	
}
