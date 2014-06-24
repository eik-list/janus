package de.mslab.matching;

import java.io.IOException;

import org.junit.Test;

import com.itextpdf.text.DocumentException;

import de.mslab.ciphers.PRESENT128;
import de.mslab.ciphers.helpers.PRESENTDifferentialCleaner;
import de.mslab.ciphers.helpers.PRESENTHelper;
import de.mslab.core.ByteArray;
import de.mslab.rendering.DifferentialRenderer;
import de.mslab.rendering.PRESENTDifferentialRenderer;

public class PRESENT128MatchingTest extends AbstractMatcherTest { 

	PRESENTDifferentialCleaner cleaner;
	
	public void setUp() {
		xmlPathname = "results/xml/PRESENT128_29_31.xml";
		pdfPathname = "results/matching/PRESENT128_29_31.pdf";
		cipher = new PRESENT128();
		counter = new PRESENTHelper();
		
		super.setUp();
		
		matchingContext.matchingFromRound = 13;
		matchingContext.isMatchingFixed = true;
		matchingContext.matchingRound = 20;
		matchingContext.matchingStateDifference = new ByteArray(new int[]{0xf0,0x00,0xf0,0x00,0xf0,0x00,0xf0,0x00});
		
		DifferentialRenderer differentialRenderer = new PRESENTDifferentialRenderer();
		renderer.setDifferentialRenderer(differentialRenderer);
		
		cleaner = new PRESENTDifferentialCleaner();
	}
	
	@Test
	public void testMatch() throws IOException, DocumentException {
		MatchingFinderResult matchingResult = matchingDifferentialBuilder.findOptimalMatching(
			matchingContext
		);
		cleaner.cleanForwardDifferential(
			matchingResult.p_mergedto_v, 
			matchingResult.p_mergedto_v.fromRound,
			matchingResult.p_mergedto_v.toRound - 2, 
			cipher
		);
		cleaner.cleanBackwardDifferential(
			matchingResult.s_mergedto_v, 
			matchingResult.s_mergedto_v.fromRound + 2,
			matchingResult.s_mergedto_v.toRound, 
			cipher
		);
		complexityCalculationResult = complexityCalculator.computeComplexity(
			cipher, 
			matchingResult.dimension, 
			matchingResult.minRecomputedOperations, 
			matchingResult.numBicliqueRounds, 
			matchingResult.matchingToRound - matchingResult.matchingFromRound + 1, 
			matchingContext.numMatchingBits
		);
		logComplexity(matchingResult, complexityCalculationResult);
		renderMatchingDifferential(matchingResult, pdfPathname);
	}
	
}
