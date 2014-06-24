package de.mslab.ciphers;

import de.mslab.errors.NoSuchCipherError;

/**
 * Provides an API to create a cipher from name.
 * 
 */
public class CipherFactory {
	
	/**
	 * List of ciphers which can be specified by the user calling the resulting jar of the 
	 * <code>BicliqueFinderApplication</code> with the console.
	 */
	public static enum CipherName {
		AES128, AES192, AES256, ARIA128, ARIA192, 
		ARIA256, BKSQ96, BKSQ144, BKSQ192, KHAZAD, 
		KLEIN64, KLEIN80, KLEIN96, LBLOCK, 
		LED64, LED80, LED96, LED112, LED128, 
		PRESENT80, PRESENT128, 
		PRINCE, PRINCECORE, SERPENT, SQUARE, 
		THREEFISH256, THREEFISH512, THREEFISH1024, WHIRLPOOLCIPHER
	};
	
	/**
	 * Returns the enum equivalent in {@link CipherName} of the given cipher name string.
	 */
	public static CipherName toCipherName(String name) {
		return CipherName.valueOf(name.toUpperCase());
	}
	
	/**
	 * Creates a cipher instance from the given name. 
	 * Valid names are those in the {@link CipherName} enumeration.
	 * @return A cipher which implements the {@link RoundBasedBlockCipher} interface.
	 * @throws NoSuchCipherError If there is no cipher with the given name.
	 * @see BlockCipher#getName()
	 */
	public static RoundBasedBlockCipher createCipher(CipherName cipherName) {
		switch(cipherName) {
			case AES128: return new AES128();
			case AES192: return new AES192();
			case AES256: return new AES256();
			case ARIA128: return new ARIA128();
			case ARIA192: return new ARIA192();
			
			case ARIA256: return new ARIA256();
			case BKSQ96: return new BKSQ96();
			case BKSQ144: return new BKSQ144();
			case BKSQ192: return new BKSQ192();
			case KHAZAD: return new Khazad();
			
			case KLEIN64: return new KLEIN64();
			case KLEIN80: return new KLEIN80();
			case KLEIN96: return new KLEIN96();
			case LBLOCK: return new LBlock();
			
			case LED64: return new LED64();
			case LED80: return new LED80();
			case LED96: return new LED96();
			case LED112: return new LED112();
			case LED128: return new LED128();
			
			case PRESENT80: return new PRESENT80();
			case PRESENT128: return new PRESENT128();
			
			case PRINCE: return new PRINCE();
			case PRINCECORE: return new PRINCECore();
			case SERPENT: return new Serpent();
			case SQUARE: return new SQUARE();
			
			case THREEFISH256: return new ThreeFish256();
			case THREEFISH512: return new ThreeFish512();
			case THREEFISH1024: return new ThreeFish1024();
			case WHIRLPOOLCIPHER: return new WhirlpoolCipher();
			default:
				throw new NoSuchCipherError(cipherName.toString());
		}
	}
	
}
