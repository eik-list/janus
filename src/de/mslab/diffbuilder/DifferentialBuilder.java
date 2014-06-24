package de.mslab.diffbuilder;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public interface DifferentialBuilder {
	
	RoundBasedBlockCipher getCipher();
	void setCipher(RoundBasedBlockCipher cipher);
	
	Differential computeBackwardDifferential(int fromRound, int toRound,
		DifferenceIterator keyDifferenceIterator, ByteArray firstExpandedKey, int keyDifferenceRound);
	Differential computeForwardDifferential(int fromRound, int toRound,
		DifferenceIterator keyDifferenceIterator, ByteArray firstExpandedKey, int keyDifferenceRound);
	
}