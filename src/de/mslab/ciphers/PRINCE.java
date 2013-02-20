package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidArgumentError;
import de.mslab.errors.InvalidKeySizeError;

public class PRINCE extends AbstractRoundBasedBlockCipher {
	
	private static final String NAME = "PRINCE";
	private static final int NUM_BYTES_IN_64_BIT = 64 / Byte.SIZE; 
	private static final int NUM_BYTES_IN_128_BIT = 128 / Byte.SIZE;
	private static final int NUM_BYTES_IN_192_BIT = 192 / Byte.SIZE;
	
	private long[] expandedKey = new long[3];
	private PRINCECore princeCore;
	
	public PRINCE() {
		super();
		princeCore = new PRINCECore();
		keySize = NUM_BYTES_IN_128_BIT;
		stateSize = NUM_BYTES_IN_64_BIT;
		name = NAME;
		numRounds = princeCore.getNumRounds();
	}
	
	public boolean canInvertKeySchedule() {
		return princeCore.canInvertKeySchedule();
	}
	
	public ByteArray decryptRounds(ByteArray block, int fromRound, int toRound) {
		if (toRound == numRounds) {
			block.xor(getRoundKey(numRounds + 1));
		}
		
		block = princeCore.decryptRounds(block, fromRound, toRound);
		
		if (fromRound == 1) {
			block.xor(getRoundKey(0));
		}
		
		return block;
	}
	
	public ByteArray encryptRounds(ByteArray block, int fromRound, int toRound) {
		if (fromRound == 1) {
			block.xor(getRoundKey(0));
		}
		
		block = princeCore.encryptRounds(block, fromRound, toRound);
		
		if (toRound == numRounds) {
			block.xor(getRoundKey(numRounds + 1));
		}
		
		return block;
	}
	
	public int getNumActiveComponentsInEncryption(int numRounds) {
		return princeCore.getNumActiveComponentsInEncryption(numRounds);
	}
	
	public ByteArray getRoundKey(int round) {
		if (round == 0) {
			return secretKey.splice(0, 8);
		} else if (round == numRounds + 1) {
			return secretKey.splice(8, 16);
		} else if (round > 0) {
			return secretKey.splice(16);
		} else {
			throw new InvalidArgumentError("Invalid round: " + round);
		}
	}
	
	public boolean hasKeyInjectionInRound(int round) {
		return round >= 0 && round <= numRounds + 1;
	}
	
	public boolean injectsKeyAtRoundBegin(int round) {
		return princeCore.injectsKeyAtRoundBegin(round);
	}
	
	public boolean injectsKeyAtRoundEnd(int round) {
		return princeCore.injectsKeyAtRoundEnd(round);
	}
	
	public boolean operatesBytewise() {
		return princeCore.operatesBytewise();
	}
	
	public boolean operatesNibblewise() {
		return princeCore.operatesNibblewise();
	}
	
	public void setKey(ByteArray key) {
		checkKeyLength(key);
		expandKey(key);
	}
	
	public void setExpandedKey(ByteArray expandedKey) {
		checkExpandedKeyLength(expandedKey);
		secretKey = expandedKey.clone();
		
		final long k0 = expandedKey.readLong(0);
		final long k1 = expandedKey.readLong(8);
		final long k0_ = Long.rotateLeft(k0, 1) ^ (k0 >>> 63);
		this.expandedKey[0] = k0;
		this.expandedKey[1] = k0_;
		this.expandedKey[2] = k1;
	}
	
	private void checkExpandedKeyLength(ByteArray key) {
		if (key.length() != NUM_BYTES_IN_192_BIT) {
			throw new InvalidKeySizeError(key.length(), new int[]{ NUM_BYTES_IN_192_BIT });
		}
	}
	
	private void checkKeyLength(ByteArray key) {
		if (key.length() != NUM_BYTES_IN_128_BIT) {
			throw new InvalidKeySizeError(key.length(), new int[]{ NUM_BYTES_IN_128_BIT });
		}
	}
	
	private void expandKey(ByteArray key) {
		final long k0 = key.readLong(0);
		final long k1 = key.readLong(8);
		final long k0_ = Long.rotateLeft(k0, 1) ^ (k0 >>> 63);
		expandedKey[0] = k0;
		expandedKey[1] = k0_;
		expandedKey[2] = k1;
		secretKey = new ByteArray(NUM_BYTES_IN_192_BIT);
		secretKey.writeLongs(expandedKey);
	}
	
}
