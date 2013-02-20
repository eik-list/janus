package de.mslab.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Vector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.core.Difference;
import de.mslab.core.Differential;

public class BicliqueSerializerTest {
	
	private static BicliqueDeserializer deserializer;
	private static Logger logger;
	private static BicliqueSerializer serializer;
	private static BicliqueXMLSerializer xmlSerializer;
	
	@BeforeClass
	public static void setUp() {
		deserializer = new BicliqueDeserializer();
		logger = new Logger();
		serializer = new BicliqueSerializer();
		xmlSerializer = new BicliqueXMLSerializer();
	}
	
	@AfterClass
	public static void tearDown() {
		deserializer = null;
		logger = null;
		serializer = null;
		xmlSerializer = null;
	}
	
	@Test
	public void testSerializeObject() throws Exception {
		int fromRound = 5;
		int toRound = 7;
		Differential deltaDifferential = fillDifferential(fromRound, toRound);
		Differential nablaDifferential = fillDifferential(fromRound, toRound);
		Biclique biclique = new Biclique(deltaDifferential, nablaDifferential);
		biclique.cipherName = "aes128";
		biclique.dimension = 8;
		String filePath = "test.txt";
		
		serializer.serializeBiclique(biclique, filePath);
		Biclique deserializedBiclique = deserializer.deserializeBiclique(filePath);
		
		assertEquals(biclique.toString(), deserializedBiclique.toString());
	}
	
	@Test
	public void testSerializationSpeed() throws Exception {
		int fromRound = 5;
		int toRound = 7;
		Differential deltaDifferential = fillDifferential(fromRound, toRound);
		Differential nablaDifferential = fillDifferential(fromRound, toRound);
		Biclique biclique = new Biclique(deltaDifferential, nablaDifferential);
		biclique.cipherName = "aes128";
		biclique.dimension = 8;
		String filePath = "test.txt";
		File file = new File(filePath);
		long startTime = System.nanoTime();
		
		for (int i = 0; i < 100; i++) {
			serializer.serializeBiclique(biclique, filePath);
		}
		
		long endTime = System.nanoTime();
		logger.info((endTime - startTime) / 1000000);
		startTime = System.nanoTime();
		
		for (int i = 0; i < 100; i++) {
			xmlSerializer.serialize(biclique, file);
		}
		
		endTime = System.nanoTime();
		logger.info((endTime - startTime) / 1000000);
	}
	
	private Differential fillDifferential(int fromRound, int toRound) {
		Differential differential = new Differential(fromRound, toRound);
		fillDifferencesVector(fromRound, toRound, differential.intermediateStateDifferences);
		fillDifferencesVector(fromRound, toRound, differential.keyDifferences);
		fillDifferencesVector(fromRound, toRound, differential.stateDifferences);
		return differential;
	}
	
	private void fillDifferencesVector(int fromRound, int toRound, Vector<Difference> differences) {
		for (int i = fromRound; i <= toRound; i++) {
			ByteArray delta = new ByteArray(16);
			delta.randomize();
			Difference difference = new Difference(delta);
			differences.set(i, difference);
		}
	}
	
}
