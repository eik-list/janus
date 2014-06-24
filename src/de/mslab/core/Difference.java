package de.mslab.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.xml.bind.annotation.XmlElement;

/**
 * This class represents a difference, that is a {@link ByteArray}.
 * 
 */
public class Difference implements Externalizable,Cloneable {
	
	private ByteArray delta;
	
	public Difference() {
		this.delta = new ByteArray();
	}
	
	/**
	 * Creates a new difference which clones the given {@link ByteArray} as the internal difference. 
	 */
	public Difference(ByteArray delta) {
		this.delta = delta.clone();
	}
	
	/**
	 * Creates a new difference from the XOR difference of both given {@link ByteArray}s. 
	 */
	public Difference(ByteArray first, ByteArray second) {
		this.delta = first.getDifference(second);
	}
	
	/**
	 * Creates a new difference, treats all items in the given items as bytes of a difference.
	 */
	public Difference(int[] delta) {
		this.delta = new ByteArray(delta);
	}
	
	/**
	 * Creates a new difference, treats all items in the given items as bytes of a difference.
	 * The given values are placed in positions <code>position</code>..
	 * <code>position + delta.length</code>; the first <code>position</code> bytes are filled up
	 * with zeroes.
	 */
	public Difference(int[] delta, int position) {
		this.delta = new ByteArray(delta, position);
	}
	
	public Difference(int value, int numBytes) {
		this.delta = new ByteArray(value, numBytes);
	}
	
	public Difference(long value, int numBytes) {
		this.delta = new ByteArray(value, numBytes);
	}
	
	public Difference and(ByteArray other) {
		delta.and(other);
		return this;
	}
	
	public Difference and(Difference other) {
		delta.and(other.delta);
		return this;
	}
	
	/**
	 * Creates a deep copy.
	 */
	public Difference clone() {
		return new Difference(delta.clone());
	}
	
	public boolean equals(int value) {
		return delta.equals(value);
	}
	
	public boolean equals(int[] other) {
		return delta.equals(other);
	}
	
	public boolean equals(Difference other) {
		if (other == null) {
			return false;
		} else {
			return delta.equals(other.delta);
		}
	}
	
	public boolean equals(Object object) {
		try {
			return equals((Difference)object);
		} catch (Exception e) {
			return false;
		}
	}

	public int hashCode() {
		assert false : "hashCode not implemented";
		return -1;
	}
	
	@XmlElement
	public ByteArray getDelta() {
		return this.delta;
	}
	
	public int length() {
		return delta.length();
	}
	
	public Difference or(ByteArray other) {
		delta.or(other);
		return this;
	}
	
	public Difference or(Difference other) {
		delta.or(other.delta);
		return this;
	}
	
	public void setDelta(ByteArray delta) {
		this.delta = delta;
	}
	
	public void setValue(int value, int numBytes) {
		this.delta.setValue(value, numBytes);
	}
	
	public void setValue(long value, int numBytes) {
		this.delta.setValue(value, numBytes);
	}
	
	public boolean sharesActiveBitsWith(Difference compareWith) {
		return delta.sharesActiveBitsWith(compareWith.delta);
	}
	
	public boolean sharesActiveBytesWith(Difference compareWith) {
		return delta.sharesActiveBytesWith(compareWith.delta);
	}
	
	public boolean sharesActiveNibblesWith(Difference compareWith) {
		return delta.sharesActiveNibblesWith(compareWith.delta);
	}
	
	/**
	 * Returns a hex string representation of this differential. 
	 */
	public String toHexString() {
		return delta.toHexString();
	}

	/**
	 * Returns a hex string representation of this differential. 
	 */
	public String toString() {
		return "[" + toHexString() + "]";
	}
	
	/**
	 * XORs this difference with the given byte array. 
	 * Modifies the current difference, the passed parameter is not modified.
	 */
	public Difference xor(ByteArray xorWith) {
		delta.xor(xorWith);
		return this;
	}
	
	/**
	 * XORs this difference with the given difference. 
	 * Modifies the current difference, the passed parameter is not modified.
	 */
	public Difference xor(Difference xorWith) {
		delta.xor(xorWith.delta);
		return this;
	}
	
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
		this.delta.readExternal(input);
	}
	
	public void writeExternal(ObjectOutput output) throws IOException {
		this.delta.writeExternal(output);
	}
	
}
