package de.mslab.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.mslab.core.Biclique;

/**
 * Serializes a given biclique into an XML file using JAXB.
 * 
 */
public class BicliqueXMLSerializer {
	
	/**
	 * Serializes a given biclique into an XML file using JAXB.
	 * @param biclique A biclique.
	 * @param file A reference to the output file.
	 * @throws JAXBException If the serialization process of the biclique fails.
	 * @throws IOException If writing the file fails.
	 */
	public void serialize(Biclique biclique, File file) throws JAXBException, IOException {
		JAXBContext context = JAXBContext.newInstance(Biclique.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(biclique, file);
	}
	
}
