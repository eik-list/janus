package de.mslab.bicliquesearch;

import org.junit.Before;
import org.junit.BeforeClass;

import de.mslab.ciphers.AES192;
import de.mslab.ciphers.helpers.AES192Helper;
import de.mslab.diffbuilder.BytewiseDifferenceBuilder;

public class AES192BicliqueFinderTest extends AbstractBicliqueFinderTest {
	
	@BeforeClass
	public static void setUpClass() {
		
	}
	
	@Before
	public void setUp() {
		super.setUp();
		
		finderContext.cipher = new AES192();
		finderContext.stopAfterFoundFirstBiclique = false;
		finderContext.dimension = 8;
		finderContext.differenceBuilder = new BytewiseDifferenceBuilder();
		finderContext.comparator = new AES192Helper();
		
		maxNumBicliqueRounds = 5;
	}
	
}
