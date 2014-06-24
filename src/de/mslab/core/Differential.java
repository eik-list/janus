package de.mslab.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * This class represents a differential, that is a sequence of state and key differences.
 * 
 */
public class Differential implements Externalizable,Cloneable {
	
	@XmlAttribute
	/**
	 * latThe start round of the differential. The default value is <code>0</code>.align
	 */
	public int fromRound = 0;
	@XmlAttribute
	/**
	 * The end round of the differential. The default value is <code>0</code>.
	 */
	public int toRound = 0;
	
	@XmlElement
	/**
	 * A list of key differences. The i-th element represents the differences in 
	 * the round keys of round i.  
	 */
	public Vector<Difference> keyDifferences = new Vector<Difference>();
	@XmlElement
	/**
	 * A list of state differences. The i-th element represents the differences in 
	 * the states after round i.  
	 */
	public Vector<Difference> stateDifferences = new Vector<Difference>();
	@XmlElement
	/**
	 * A list of intermediate state differences. The i-th element represents the differences in 
	 * the states before the key injection in round i.  
	 */
	public Vector<Difference> intermediateStateDifferences = new Vector<Difference>();
	@XmlElement
	/**
	 * The first secretKey used for creation of this differential.
	 */
	public ByteArray firstSecretKey;
	@XmlElement
	public ByteArray keyDifference;
	@XmlElement
	/**
	 * The second secretKey used for creation of this differential.
	 */
	public ByteArray secondSecretKey;
	
	/**
	 * The default constructor is used by JAXB to create a differential from a serialized xml.
	 * This constructor should not be used.
	 */
	public Differential() {
		
	}
	
	/**
	 * This constructor should be used to create a differential. 
	 * The lists for key, state and intermediate state differences are initialized from the given rounds.
	 */
	public Differential(int fromRound, int toRound) {
		this.fromRound = fromRound;
		this.toRound = toRound;
		
		int maxRound = Math.max(fromRound, toRound);
		this.keyDifferences.setSize(maxRound + 2); // 1 added for post-whitening
		this.intermediateStateDifferences.setSize(maxRound + 2);
		this.stateDifferences.setSize(maxRound + 1);
	}

	public Differential and(Differential other) {
		final int numKeys = keyDifferences.size();
		final int numStates = stateDifferences.size();
		final int numIntermediateStates = intermediateStateDifferences.size();
		
		for (int round = 0; round < numKeys; round++) {
			andElementsIfNotZeroAt(keyDifferences, other.keyDifferences, round);
		}
		
		for (int round = 0; round < numStates; round++) {
			andElementsIfNotZeroAt(stateDifferences, other.stateDifferences, round);
		}
		
		for (int round = 0; round < numIntermediateStates; round++) {
			andElementsIfNotZeroAt(intermediateStateDifferences, other.intermediateStateDifferences, round);
		}
		
		return this;
	}
	
	/** 
	 * Creates a deep copy of this differential.
	 * @see java.lang.Object#clone()
	 */
	public Differential clone() {
		Differential copy = new Differential(this.fromRound, this.toRound);
		int maxRound = Math.max(fromRound, toRound);
		
		for (int round = 0; round <= maxRound; round++) {
			if (this.keyDifferences.get(round) != null) { 
				copy.keyDifferences.set(round, this.keyDifferences.get(round).clone());
			}
			
			if (this.stateDifferences.get(round) != null) {
				copy.stateDifferences.set(round, this.stateDifferences.get(round).clone());
			}
			
			if (this.intermediateStateDifferences.get(round) != null) {
				copy.intermediateStateDifferences.set(round, this.intermediateStateDifferences.get(round).clone());
			}
		}
		
		if (this.keyDifferences.get(toRound + 1) != null) { 
			copy.keyDifferences.set(toRound + 1, this.keyDifferences.get(toRound + 1).clone());
		}
		
		if (this.intermediateStateDifferences.get(toRound + 1) != null) {
			copy.intermediateStateDifferences.set(toRound + 1, this.intermediateStateDifferences.get(toRound + 1).clone());
		}
		
		return copy;
	}
	
	public boolean equals(Object object) {
		try {
			Differential other = (Differential)object;
			
			if (other == null) {
				return false;
			} else {
				return fromRound == other.fromRound
					&& toRound == other.toRound
					&& keyDifferences.equals(other.keyDifferences)
					&& intermediateStateDifferences.equals(other.intermediateStateDifferences)
					&& stateDifferences.equals(other.stateDifferences)
					&& firstSecretKey.equals(other.firstSecretKey)
					&& secondSecretKey.equals(other.secondSecretKey);
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public int hashCode() {
		assert false : "hashCode not implemented";
		return -1;
	}
	
	/**
	 * Returns the key difference for the given round.
	 */
	public Difference getKeyDifference(int round) {
		return keyDifferences.get(round);
	}
	
	/**
	 * Returns the state difference before the key injection in the given round.
	 */
	public Difference getIntermediateStateDifference(int round) {
		return intermediateStateDifferences.get(round);
	}
	
	/**
	 * Returns the state difference for the given round.
	 */
	public Difference getStateDifference(int round) {
		return stateDifferences.get(round);
	}
	
	/**
	 * Sets the state difference for the given round.
	 */
	public void setKeyDifference(int round, Difference difference) {
		keyDifferences.set(round, difference);
	}
	
	/**
	 * Sets the key difference for the given round.
	 */
	public void setKeyDifference(int round, ByteArray delta) {
		keyDifferences.set(round, new Difference(delta));
	}
	
	/**
	 * Returns the state difference before the key injection in the given round.
	 */
	public void setIntermediateStateDifference(int round, ByteArray delta) {
		intermediateStateDifferences.set(round, new Difference(delta));
	}
	
	/**
	 * Returns the state difference before the key injection in the given round.
	 */
	public void setIntermediateStateDifference(int round, Difference delta) {
		intermediateStateDifferences.set(round, delta);
	}
	
	/**
	 * Sets the state difference for the given round.
	 */
	public void setStateDifference(int round, Difference difference) {
		stateDifferences.set(round, difference);
	}
	
	/**
	 * Sets the state difference for the given round.
	 */
	public void setStateDifference(int round, ByteArray delta) {
		stateDifferences.set(round, new Difference(delta));
	}
	
	public Differential or(Differential other) {
		final int numKeys = keyDifferences.size();
		final int numStates = stateDifferences.size();
		final int numIntermediateStates = intermediateStateDifferences.size();
		
		for (int round = 0; round < numKeys; round++) {
			orElementsIfNotZeroAt(keyDifferences, other.keyDifferences, round);
		}
		
		for (int round = 0; round < numStates; round++) {
			orElementsIfNotZeroAt(stateDifferences, other.stateDifferences, round);
		}
		
		for (int round = 0; round < numIntermediateStates; round++) {
			orElementsIfNotZeroAt(intermediateStateDifferences, other.intermediateStateDifferences, round);
		}
		
		return this;
	}
	
	/**
	 * Returns a hex string representation of this differential. 
	 */
	public String toHexString() {
		String result = "";
		boolean hasKeyWrappingAtTheEnd = (keyDifferences.size() == toRound + 2);
		// For 0-th and (toRound + 1)-th element
		
		for (int round = fromRound - 1; round <= toRound; round++) {
			result += updateString(intermediateStateDifferences, round, " inter ");
			result += updateString(keyDifferences, round, " key   ");
			
			if (round < toRound) {
				result += updateString(stateDifferences, round, " state ");
			}
		}
		
		if (hasKeyWrappingAtTheEnd) {
			result += updateString(intermediateStateDifferences, toRound + 1, " inter ");
			result += updateString(keyDifferences, toRound + 1, " key   ");
			result += updateString(stateDifferences, toRound, " state ");
		} else {
			result += updateString(stateDifferences, toRound, " state ");
		}
		
		return result;
	}
	
	private String updateString(Vector<Difference> differences, int round, String identifier) {
		Difference difference = differences.get(round);
		String result = "";
		
		if (difference != null) {
			result = "round " + round + identifier + difference + "\n";
		}
		
		return result;
	}
	
	public String toString() {
		return "[" + toHexString() + "]";
	}
	
	/**
	 * XORs all state differences and key differences of a differential instance with the 
	 * related state differences and key differences of the given one. Modifies the original, 
	 * and leaves the <code>other</code> differential unchanged. 
	 */
	public Differential xor(Differential other) {
		final int numKeys = keyDifferences.size();
		final int numStates = stateDifferences.size();
		final int numIntermediateStates = intermediateStateDifferences.size();
		
		for (int round = 0; round < numKeys; round++) {
			xorElementsIfNotZeroAt(keyDifferences, other.keyDifferences, round);
		}
		
		for (int round = 0; round < numStates; round++) {
			xorElementsIfNotZeroAt(stateDifferences, other.stateDifferences, round);
		}
		
		for (int round = 0; round < numIntermediateStates; round++) {
			xorElementsIfNotZeroAt(intermediateStateDifferences, other.intermediateStateDifferences, round);
		}
		
		return this;
	}
	
	private void andElementsIfNotZeroAt(Vector<Difference> first, Vector<Difference> second, int index) {
		if (first.get(index) != null && second.get(index) != null) {
			first.get(index).and(second.get(index));
		}
	}
	
	private void orElementsIfNotZeroAt(Vector<Difference> first, Vector<Difference> second, int index) {
		if (first.get(index) != null && second.get(index) != null) {
			first.get(index).or(second.get(index));
		}
	}
	
	private void xorElementsIfNotZeroAt(Vector<Difference> first, Vector<Difference> second, int index) {
		if (first.get(index) != null && second.get(index) != null) {
			first.get(index).xor(second.get(index));
		}
	}
	
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
		this.fromRound = input.readInt();
		this.toRound = input.readInt();
		
		this.firstSecretKey = new ByteArray();
		this.secondSecretKey = new ByteArray();
		this.keyDifference = new ByteArray();
		this.firstSecretKey.readExternal(input);
		this.secondSecretKey.readExternal(input);
		this.keyDifference.readExternal(input);
		
		this.intermediateStateDifferences = readVector(input);
		this.keyDifferences = readVector(input);
		this.stateDifferences = readVector(input);
	}
	
	private Vector<Difference> readVector(ObjectInput input) throws IOException, ClassNotFoundException {
		final int length = input.readInt();
		Vector<Difference> result = new Vector<Difference>();
		result.setSize(length);
		boolean isNotNull;
		
		for (int i = 0; i < length; i++) {
			isNotNull = input.readBoolean();
			
			if (isNotNull) {
				Difference difference = new Difference();
				difference.readExternal(input);
				result.set(i, difference);
			}
		}
		
		return result;
	}

	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeInt(this.fromRound);
		output.writeInt(this.toRound);
		
		writeByteArray(this.firstSecretKey, output);
		writeByteArray(this.secondSecretKey, output);
		writeByteArray(this.keyDifference, output);
		
		writeVector(this.intermediateStateDifferences, output);
		writeVector(this.keyDifferences, output);
		writeVector(this.stateDifferences, output);
	}
	
	private void writeByteArray(ByteArray target, ObjectOutput output) throws IOException {
		if (target == null) {
			target = new ByteArray();
		}
		
		target.writeExternal(output);
	}
	
	private void writeVector(Vector<Difference> vector, ObjectOutput output) throws IOException {
		if (vector == null) {
			vector = new Vector<Difference>();
		}
		
		output.writeInt(vector.size());
		
		Difference difference;
		boolean isNotNull;
		
		for (int i = 0; i < vector.size(); i++) {
			difference = vector.get(i);
			isNotNull = (difference != null);
			output.writeBoolean(isNotNull);
			
			if (isNotNull) {
				difference.writeExternal(output);
			}
		}
	}
	
}
