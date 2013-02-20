package de.mslab.rendering;

import java.io.IOException;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.DocumentException;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;


public class ThreeFishDifferentialRenderer extends BicliqueDifferentialRenderer {
	
	public ThreeFishDifferentialRenderer() {
		
	}

	public void setUp(RoundBasedBlockCipher cipher) {
		this.cipher = cipher;
		
		if (cipher.operatesBytewise()) {
			if (cipher.operatesNibblewise()) {
				this.stateRenderer = new NibblewiseStateRenderer(cipher.getStateSize(), cellSize, cipher.operatesColumnwise());
			} else {
				this.stateRenderer = new BytewiseStateRenderer(cipher.getStateSize(), cellSize, cipher.operatesColumnwise());
			}
		} else {
			this.stateRenderer = new ThreeFishStateRenderer(cipher.getStateSize(), 2 * cellSize);
		}
	}
	
	public Rectangle determineSize(Differential differential, int intermediateRound) {
		double boundsX = stateRenderer.getBounds().x;
		double boundsY = stateRenderer.getBounds().y;
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;
		
		if (intermediateRound != toRound) {
			if (intermediateRound - fromRound > toRound - intermediateRound) {
				toRound = intermediateRound;
			} else {
				fromRound = intermediateRound;
			}
		}
		
		Rectangle result = new Rectangle();
		result.y += boundsY + 2 * offset; // space for initial state
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			result.y += 2 * boundsY + 2 * offset; // space for intermediate state + offsets
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (cipher.hasKeyInjectionInRound(round)) {
				result.y += 2 * boundsY + 2 * offset; // space for intermediate state + offsets
			}
			
			result.y += boundsY + 2 * offset; // space for state
		}
		
		result.x = 1.5 * boundsX + 2 * offset; // space for state + key
		return result;
	}
	
	protected void renderKeyInjection(int round) {
		Point additionPosition = currentStatePosition.getLocation();
		RenderUtil.renderAddition(contentByte, additionPosition);
		
		Point keyPosition = additionPosition.getLocation();
		keyPosition.x -= stateRenderer.getBounds().x / 2;
		keyPosition.x -= offset;
		RenderUtil.renderLine(contentByte, keyPosition, additionPosition);
		
		ByteArray key = differential.getKeyDifference(round).getDelta();
		stateRenderer.renderState(contentByte, key, keyPosition, activeKeyColor);
	}
	
	protected void renderRound(int round) throws DocumentException, IOException {
		renderRoundStructures(round);
		
		if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundBegin(round)) {
			currentStatePosition.y -= stateRenderer.getBounds().y + offset;
			renderKeyInjection(round);
			currentStatePosition.y -= stateRenderer.getBounds().y + offset;
			renderIntermediateState(round);
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
		} else {
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
		}
		
		renderRoundLabel(round);
		
		if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundEnd(round)) {
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
			renderIntermediateState(round);
			currentStatePosition.y -= stateRenderer.getBounds().y + offset;
			renderKeyInjection(round);
			currentStatePosition.y -= stateRenderer.getBounds().y + offset;
		} else {
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
		}
		
		renderState(round);
	}
	
	protected void renderRoundStructures(int round) {
		if (cipher.hasKeyInjectionInRound(round)) {
			nextStatePosition.y = currentStatePosition.y - 3 * stateRenderer.getBounds().y - 4 * offset;
		} else {
			nextStatePosition.y = currentStatePosition.y - stateRenderer.getBounds().y - 2 * offset;
		}
		
		RenderUtil.renderLine(contentByte, currentStatePosition, nextStatePosition);
	}
	
}
