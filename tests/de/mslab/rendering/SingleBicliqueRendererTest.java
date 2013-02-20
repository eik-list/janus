package de.mslab.rendering;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.text.DocumentException;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.ciphers.ThreeFish1024;
import de.mslab.core.Biclique;
import de.mslab.utils.BicliqueXMLParser;
import de.mslab.utils.Logger;

public class SingleBicliqueRendererTest {
	
	private static final Logger logger = Logger.getLogger();
	private static final BicliqueXMLParser parser = new BicliqueXMLParser();
	
	private static BicliqueRenderer renderer;
	private static RoundBasedBlockCipher cipher;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		renderer = new BicliqueRenderer();
		renderer.setDifferentialRenderer(new ThreeFishDifferentialRenderer());
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		renderer = null;
		cipher = null;
	}
	
	@Test
	public void testRenderingSingleBiclique() throws JAXBException, IOException, DocumentException {
		cipher = new ThreeFish1024();
		int fromRound = 65;
		int toRound = 68;
		
		String pathName = cipher.getName() + "_" + fromRound + "_" + toRound;
		Biclique biclique = createBiclique("results/xml/" + pathName + ".xml");
		logger.info(biclique);
		renderer.renderBiclique("results/biclique/" + pathName + ".pdf", biclique, cipher);
	}
	
	private Biclique createBiclique(String pathname) throws JAXBException {
		File file = new File(pathname);
		return parser.parseXML(file);
	}
	
}
