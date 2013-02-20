package de.mslab.utils;


import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.mslab.core.Biclique;
import de.mslab.core.ByteArray;
import de.mslab.core.Difference;
import de.mslab.core.Differential;


public class BicliqueXMLSerializerTest {
	
	private static Biclique biclique;
	private static BicliqueXMLSerializer serializer;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		serializer = new BicliqueXMLSerializer();
		
		int fromRound = 1;
		int toRound = 3;
		Differential deltaDifferential = new Differential(fromRound, toRound);
		Differential nablaDifferential = new Differential(fromRound, toRound);
		ByteArray delta;
		int numBytes = 16;
		
		for (int round = fromRound - 1; round <= toRound; round++) {
			delta = new ByteArray(round, numBytes);
			Difference deltaKeyDifference = new Difference(delta);
			Difference deltaStateDifference = new Difference(delta);
			Difference nablaKeyDifference = new Difference(delta);
			Difference nablaStateDifference = new Difference(delta);
			
			deltaDifferential.keyDifferences.set(round, deltaKeyDifference);
			deltaDifferential.stateDifferences.set(round, deltaStateDifference);
			nablaDifferential.keyDifferences.set(round, nablaKeyDifference);
			nablaDifferential.stateDifferences.set(round, nablaStateDifference);
		}
		
		biclique = new Biclique(deltaDifferential, nablaDifferential);
	}
	
	@Test
	public void testSerializeBiclique() throws JAXBException, IOException {
		String path = "tests/outputs/";
		String filename = "biclique.xml";
		File file = new File(path);
		
		if (!file.exists()) {
			file.mkdir();
		}
		
		file = new File(path + filename);
		serializer.serialize(biclique, file);
	}
	
}
