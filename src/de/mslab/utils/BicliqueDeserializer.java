package de.mslab.utils;

import java.io.Externalizable;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import de.mslab.core.Biclique;

public class BicliqueDeserializer {
	
	/**
	 * Serializes a given biclique into a file using the {@link Externalizable} support.
	 * @param filepath The full qualified path to an input file.
	 * @return A biclique object 
	 * @throws Exception If writing the file fails.
	 */
	public Biclique deserializeBiclique(String filepath) throws Exception {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		Biclique result = null;
		
		try {
			fileInputStream = new FileInputStream(filepath);
			objectInputStream = new ObjectInputStream(fileInputStream);
			result = new Biclique();
			result.readExternal(objectInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
			
			if (objectInputStream != null) {
				objectInputStream.close();
			}
		}
		
		return result;
	}
	
}
