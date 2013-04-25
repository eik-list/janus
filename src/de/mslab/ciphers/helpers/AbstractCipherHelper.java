package de.mslab.ciphers.helpers;

import de.mslab.core.Differential;

abstract class AbstractCipherHelper implements CipherHelper {
	
	public int countRecomputedOperations(Differential differential) {
		return countRecomputedOperations(differential, differential);
	}
	
	protected boolean checkKey(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.keyDifferences.get(round).sharesActiveBytesWith(
			nablaDifferential.keyDifferences.get(round)
		);
	}
	
	protected boolean checkIntermediateState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.intermediateStateDifferences.get(round).sharesActiveBytesWith(
			nablaDifferential.intermediateStateDifferences.get(round)
		);
	}
	
	protected boolean checkState(int round, Differential deltaDifferential, Differential nablaDifferential) {
		return deltaDifferential.stateDifferences.get(round).sharesActiveBytesWith(
			nablaDifferential.stateDifferences.get(round)
		);
	}
	
}
