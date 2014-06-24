package de.mslab.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
/**
 * A biclique, that is a delta (forward) differential and a nabla (backward) differential. 
 * 
 */
public class Biclique implements Externalizable {
	
	@XmlElement
	/**
	 * The name of the cipher over which the biclique is constructed. 
	 * The cipher name is required to know how to render a serialized biclique.  
	 */
	public String cipherName;
	@XmlElement
	/**
	 * The dimension of the biclique: A biclique of dimension d tests a group of 2^{2d} keys.  
	 */
	public int dimension = -1;
	@XmlElement
	/**
	 * The delta (forward) differential.
	 */
	public Differential deltaDifferential;
	@XmlElement
	/**
	 * The nabla (backward) differential.
	 */
	public Differential nablaDifferential;
	
	private static final long serialVersionUID = 140605814607823205L;
	
	public Biclique() {
		
	}
	
	public Biclique(Differential deltaDifferential, Differential nablaDifferential) {
		this.deltaDifferential = deltaDifferential;
		this.nablaDifferential = nablaDifferential;
	}
	
	public boolean equals(Object object) {
		try {
			Biclique other = (Biclique)object;
			
			if (other == null) {
				return false;
			} else {
				return cipherName.equals(other.cipherName)
					&& dimension == other.dimension
					&& deltaDifferential.equals(other.deltaDifferential)
					&& nablaDifferential.equals(other.nablaDifferential);
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public int hashCode() {
		assert false : "hashCode not needed";
		return -1;
	}
	
	/**
	 * Returns a string of delta and nabla differential.
	 */
	public String toHexString() {
		return "Delta: \n" + deltaDifferential.toHexString() + "\n"
			+ "Nabla: \n" + nablaDifferential.toHexString();
	}
	
	public String toString() {
		return "[Biclique\n" + toHexString() + "]";
	}

	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.cipherName = objectInput.readUTF();
		this.dimension = objectInput.readInt();
		
		this.deltaDifferential = new Differential();
		this.nablaDifferential = new Differential();
		this.deltaDifferential.readExternal(objectInput);
		this.nablaDifferential.readExternal(objectInput);
	}
	
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeUTF(this.cipherName);
		output.writeInt(this.dimension);
		
		writeDifferential(this.deltaDifferential, output);
		writeDifferential(this.nablaDifferential, output);
	}
	
	private void writeDifferential(Differential differential, ObjectOutput output) throws IOException {
		if (differential == null) {
			differential = new Differential();
		}
		
		differential.writeExternal(output);
	}
	
}
