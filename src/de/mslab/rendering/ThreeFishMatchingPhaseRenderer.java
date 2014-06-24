package de.mslab.rendering;

import java.io.IOException;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.matching.MatchingFinderResult;


public class ThreeFishMatchingPhaseRenderer extends MatchingPhaseRenderer {
	
	public ThreeFishMatchingPhaseRenderer() {
		differentialRenderer = new ThreeFishDifferentialRenderer();
	}

	public void renderMatchingPhase(String pathname, MatchingFinderResult result, RoundBasedBlockCipher cipher) 
	throws IOException, DocumentException {
		setSize(result, cipher);
		createPDF(pathname);
		renderDifferentials(result, cipher);
		closePDF();
	}
	
	protected void renderDifferentials(MatchingFinderResult result, RoundBasedBlockCipher cipher) throws DocumentException, IOException {
		differentialRenderer.setUp(cipher);
		differentialRenderer.setContentByte(contentByte);
		
		Point position = new Point(
			padding + firstDifferentialBounds.x, 
			document.getPageSize().getHeight() - padding
		);
		differentialRenderer.renderDifferential(
			result.s_mergedto_v, position, BACKWARD_MATCHING, simpleDifferentialActiveBytesColor, backwardDifferentialActiveBytesColor
		);
		position.x += padding + secondDifferentialBounds.x;
		differentialRenderer.renderDifferential(
			result.p_mergedto_v, position, FORWARD_MATCHING, simpleDifferentialActiveBytesColor, forwardDifferentialActiveBytesColor
		);
		
		differentialRenderer.tearDown();
	}
	
	protected void setSize(MatchingFinderResult result, RoundBasedBlockCipher cipher) {
		differentialRenderer.setUp(cipher);
		firstDifferentialBounds = differentialRenderer.determineSize(result.s_mergedto_v, result.bestMatchingRound);
		secondDifferentialBounds = differentialRenderer.determineSize(result.p_mergedto_v, result.bestMatchingRound);
		
		float maxBoundsY = (firstDifferentialBounds.y >= secondDifferentialBounds.y) ?
			(float)firstDifferentialBounds.y :
			(float)secondDifferentialBounds.y;
		
		pageSize = new Rectangle(
			(float)(padding + firstDifferentialBounds.x + padding + secondDifferentialBounds.x + padding), 
			maxBoundsY
		);
	}
	
}
