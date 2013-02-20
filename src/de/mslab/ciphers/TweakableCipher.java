package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidTweakSizeError;

/**
 * Interface for round-based symmetric ciphers which employ a tweak as an additional parameter
 * for encryption or decryption, such as {@link ThreeFish}.
 * 
 */
public interface TweakableCipher extends RoundBasedBlockCipher {
	/**
	 * Returns the currently used tweak.
	 */
	ByteArray getTweak();
	/**
	 * Returns the size in bytes of the tweak.
	 */
	int getTweakSize();
	/**
	 * Sets the tweak used as a parameter in encryption or decryption. 
	 * The tweak size is checked if it is equal to the expected size.
	 * @throws InvalidTweakSizeError The given tweak has an invalid size. 
	 * @see TweakableCipher#getTweakSize()
	 */
	void setTweak(ByteArray tweak);
}
