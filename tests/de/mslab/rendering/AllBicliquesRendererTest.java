package de.mslab.rendering;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.text.DocumentException;

import de.mslab.ciphers.CipherFactory;
import de.mslab.ciphers.CipherFactory.CipherName;
import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.Biclique;
import de.mslab.utils.BicliqueXMLParser;
import de.mslab.utils.Logger;

public class AllBicliquesRendererTest {
	
	private static final Logger logger = Logger.getLogger();
	private static final BicliqueXMLParser parser = new BicliqueXMLParser();
	private static final File xmlsFolder = new File("results/xml");
	private static final File pdfsFolder = new File("results/biclique");
	
	private static BicliqueRenderer renderer;
	private static RoundBasedBlockCipher cipher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		renderer = new BicliqueRenderer();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		renderer = null;
	}
	
	@Test
	public void testRenderingBicliques() throws JAXBException, IOException, DocumentException {
		String pdfName;
		CipherName cipherName;
		
		if (!pdfsFolder.exists()) {
			pdfsFolder.mkdir();
		}
		
		for (File file : xmlsFolder.listFiles()) {
	        if (!file.isDirectory()) {
	        	pdfName = xmlNameToPDFName(file.getName());
	        	
	        	if (pdfName.indexOf("SQUARE") == 0) {
		        	Biclique biclique = parser.parseXML(file);
		        	logger.info(biclique);
		        	
		        	cipherName = CipherFactory.toCipherName(biclique.cipherName);
		        	cipher = CipherFactory.createCipher(cipherName);
		        	logger.info(pdfsFolder.getPath() + "/" + pdfName);
		        	
		        	renderer.renderBiclique(pdfsFolder.getPath() + "/" + pdfName, biclique, cipher);
	        	}
	        }
	    }
	}
	
	private String xmlNameToPDFName(String xmlName) {
		return xmlName.substring(0, xmlName.length() - 4) + ".pdf";
	}
	
}
