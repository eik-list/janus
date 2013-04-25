package de.mslab.utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.mslab.ciphers.CipherFactory;
import de.mslab.ciphers.CipherFactory.CipherName;
import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.Biclique;
import de.mslab.core.Differential;

/**
 * Parses an XML which contains a serialized biclique and reconstructs the biclique. 
 * 
 */
public class BicliqueXMLParser {
	
	/**
	 * Parses an XML which contains a serialized biclique and reconstructs the biclique. 
	 * @param file A file which references an XML file containing a biclique.
	 * @throws JAXBException
	 */
	public Biclique parseXML(File file) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Biclique.class);
	    Unmarshaller unmarshaller = context.createUnmarshaller();
	    Biclique biclique = (Biclique)unmarshaller.unmarshal(file);
	    
	    CipherName cipherName = CipherFactory.toCipherName(biclique.cipherName);
	    RoundBasedBlockCipher cipher = CipherFactory.createCipher(cipherName);
	    correctBiclique(biclique, cipher);
	    return biclique;
	}
	
	/**
	 * Calls {@link #correctDifferential(Differential, RoundBasedBlockCipher)} on delta 
	 * and nabla differentials of the biclique.
	 * @param biclique The biclique created by JAXB. 
	 * @param cipher The instantiated cipher, created from the cipher name in the biclique. 
	 */
	private void correctBiclique(Biclique biclique, RoundBasedBlockCipher cipher) {
		biclique.deltaDifferential = correctDifferential(biclique.deltaDifferential, cipher);
		biclique.nablaDifferential = correctDifferential(biclique.nablaDifferential, cipher);
	}
	
	/**
	 * Sets the key, state, and intermediate state differences at the correct rounds, as the 
	 * XML file does not store the correct array indices. 
	 * @param differential
	 * @param cipher
	 * @return A new corrected differential. The original is not modified.
	 */
	private Differential correctDifferential(Differential differential, RoundBasedBlockCipher cipher) {
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;
		int numRounds = toRound - fromRound; // 10 - 8 = 2
		int stateIndex = 0;
		int keyIndex = 0;
		
		Differential result = new Differential(differential.fromRound, differential.toRound);
		result.firstSecretKey = differential.firstSecretKey;
		result.secondSecretKey = differential.secondSecretKey;
		result.keyDifference = differential.keyDifference;
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			result.setKeyDifference(0, differential.getKeyDifference(keyIndex));
			result.setIntermediateStateDifference(1, differential.getIntermediateStateDifference(keyIndex));
			keyIndex++;
		}
		
		for (int round = fromRound; round <= toRound; round++, stateIndex++) { // round = 8, 9, 10; i = 0, 1, 2
			if (cipher.hasKeyInjectionInRound(round)) {
				result.setKeyDifference(round, differential.getKeyDifference(keyIndex));
				result.setIntermediateStateDifference(round, differential.getIntermediateStateDifference(keyIndex));
				keyIndex++;
			}
			
			result.setStateDifference(round - 1, differential.getStateDifference(stateIndex));
		}
		
		result.setStateDifference(toRound, differential.getStateDifference(numRounds + 1));
		return result;
	}
	
}
