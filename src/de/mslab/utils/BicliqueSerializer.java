package de.mslab.utils;

import java.io.Externalizable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import de.mslab.core.Biclique;

public class BicliqueSerializer {

	/**
	 * Serializes a given biclique into a file using the {@link Externalizable} support.
	 * @param biclique A biclique.
	 * @param filePath The full qualified path to an output file. 
	 * @throws Exception If writing the file fails.
	 */
	public void serializeBiclique(Biclique biclique, String filePath) throws Exception {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		
		try {
			fileOutputStream = new FileOutputStream(filePath);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			biclique.writeExternal(objectOutputStream);
			objectOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
			
			if (objectOutputStream != null) {
				objectOutputStream.close();
			}
		}
	}
	
}
