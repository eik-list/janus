package de.mslab.ciphers.helpers;

import de.mslab.ciphers.CipherFactory.CipherName;
import de.mslab.errors.NoSuchCipherError;

public class CipherHelperFactory {

	/**
	 * Creates a cipher helper instance from the given name. 
	 * Valid names are those in the {@link CipherName} enumeration.
	 * @return A cipher helper which implements the {@link CipherHelper} interface.
	 * @throws NoSuchCipherError If there is no cipher with the given name.
	 */
	public static CipherHelper createCipherHelper(CipherName cipherName) {
		switch(cipherName) {
			case AES128: return new AES128Helper();
			case AES192: return new AES192Helper();
			case AES256: return new AES256Helper();
			case ARIA128: return new ARIA128Helper();
			case ARIA192: return new ARIA192Helper();
			
			case ARIA256: return new ARIA256Helper();
			case BKSQ96: return new BKSQ96Helper();
			case BKSQ144: return new BKSQ144Helper();
			case BKSQ192: return new BKSQ192Helper();
			case KHAZAD: return new KhazadHelper();
			
			case KLEIN64: return new KLEIN64Helper();
			case KLEIN80: return new KLEIN80Helper();
			case KLEIN96: return new KLEIN96Helper();
			case LBLOCK: return new LBlockHelper();
			
			case LED64: return new LEDHelper();
			case LED80: return new LEDHelper();
			case LED96: return new LEDHelper();
			case LED112: return new LEDHelper();
			case LED128: return new LEDHelper();
			
			case PRESENT80: return new PRESENTHelper();
			case PRESENT128: return new PRESENTHelper();
			case PRINCE: return new PRINCECoreHelper();
			case PRINCECORE: return new PRINCECoreHelper();
			case SQUARE: return new SQUAREHelper();
			
			case THREEFISH256: return new ThreeFishHelper();
			case THREEFISH512: return new ThreeFishHelper();
			case THREEFISH1024: return new ThreeFishHelper();
			case WHIRLPOOLCIPHER: return new WhirlpoolCipherHelper();
			default:
				throw new NoSuchCipherError(cipherName.toString());
		}
	}
	
}
