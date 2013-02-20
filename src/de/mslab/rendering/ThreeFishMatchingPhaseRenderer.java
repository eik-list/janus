package de.mslab.rendering;

import java.io.IOException;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.matching.MatchingDifferentialBuilderResult;


public class ThreeFishMatchingPhaseRenderer extends MatchingPhaseRenderer {
	
	public ThreeFishMatchingPhaseRenderer() {
		differentialRenderer = new ThreeFishDifferentialRenderer();
	}

	public void renderMatchingPhase(String pathname, MatchingDifferentialBuilderResult result, RoundBasedBlockCipher cipher) 
	throws IOException, DocumentException {
		setSize(result, cipher);
		createPDF(pathname);
		renderDifferentials(result, cipher);
		closePDF();
	}
	
	protected void renderDifferentials(MatchingDifferentialBuilderResult result, RoundBasedBlockCipher cipher) throws DocumentException, IOException {
		differentialRenderer.setUp(cipher);
		differentialRenderer.setContentByte(contentByte);
		
		Point position = new Point(differentialBounds.x - padding, document.getPageSize().getHeight() - padding);
		differentialRenderer.renderDifferential(
			result.s_mergedto_v, position, BACKWARD_MATCHING, simpleDifferentialActiveBytesColor, backwardDifferentialActiveBytesColor
		);
		position.x += differentialBounds.x;
		differentialRenderer.renderDifferential(
			result.p_mergedto_v, position, FORWARD_MATCHING, simpleDifferentialActiveBytesColor, forwardDifferentialActiveBytesColor
		);
		
		differentialRenderer.tearDown();
	}
	
	protected void setSize(MatchingDifferentialBuilderResult result, RoundBasedBlockCipher cipher) {
		differentialRenderer.setUp(cipher);
		
		if (result.bestMatchingRound - result.matchingFromRound < result.matchingToRound - result.bestMatchingRound) {
			differentialBounds = differentialRenderer.determineSize(result.s_mergedto_v, result.bestMatchingRound);
		} else {
			differentialBounds = differentialRenderer.determineSize(result.p_mergedto_v, result.bestMatchingRound);
		}
		
		differentialBounds.x += padding;
		differentialBounds.y += padding;
		
		float sizeX = (float)differentialBounds.x * numDifferentials + 2 * padding;
		pageSize = new Rectangle(sizeX, (float)differentialBounds.y);
	}
	
}
