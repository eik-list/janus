package de.mslab.rendering;

import java.io.IOException;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;

import de.mslab.ciphers.PRESENT;
import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class PRESENTDifferentialRenderer extends AbstractDifferentialRenderer {
	
	private static final int numBits = 64;
	private static final int numBitsPerSBox = 4;
	private static final int numSBoxes = 16;
	
	private static final int lineHorizontalPadding = 10;
	private static final int lineOffset = 10;
	private static final int lineHeight = 43;
	private static final int lineHorizontalSBoxOffset = 15;
	private static final int belowSBoxLineHeight = 7;
	
	private static final int permutationLineHeight = 70;
	private static final int sBoxOffset = 43;
	private static final int sBoxPadding = 5;
	private static final int sBoxWidth = 50;
	private static final int sBoxHeight = 16;
	
	private static final int xorOffset = 12;
	private static final int xorRadius = 3;
	
	public Rectangle determineSize(Differential differential) {
		final int numRounds = differential.toRound - differential.fromRound + 1;
		return determineSize(numRounds);
	}
	
	public Rectangle determineSize(Differential differential, int intermediateRound, boolean renderBeginning) {
		final int fromRound = renderBeginning ? 
			differential.fromRound : 
			intermediateRound;
		final int toRound = renderBeginning ? 
			intermediateRound : 
			differential.toRound;
		final int numRounds = toRound - fromRound + 1;
		return determineSize(numRounds);
	}
	
	private Rectangle determineSize(int numRounds) {
		final Rectangle size = new Rectangle();
		size.x = numSBoxes * sBoxWidth + ((numBits / numBitsPerSBox) - 1) * sBoxPadding;
		size.y = (lineHeight + belowSBoxLineHeight + permutationLineHeight) * numRounds;
		return size;
	}
	
	public void renderDifferential(Differential differential, Point position, String label, 
		BaseColor activeStateColor, BaseColor activeKeyColor) throws DocumentException, IOException {
		
		super.renderDifferential(differential, position, label, activeStateColor, activeKeyColor);
		
		contentByte.setColorStroke(BaseColor.GRAY);
		
		final int fromRound = differential.fromRound;
		final int toRound = differential.toRound;
		
		for (int round = fromRound; round <= toRound; round++) {
			renderRound(round);
		}
	}
	
	private void renderRound(int round) throws DocumentException, IOException {
		renderKeyInjection(round);
		
		if (!renderOnlyActiveTrails) {
			renderRoundLabel(round);
		}
		
		renderSBoxLayer(round);
		renderPermutationLayer(round);
		
		currentStatePosition.y -= lineHeight + belowSBoxLineHeight + permutationLineHeight;
		
		if (round == cipher.getNumRounds()) {
			renderKeyInjection(round + 1);
			renderRoundLabel(round + 1);
		}
	}
	
	private void renderKeyInjection(int round) {
		boolean isInputActive, isOutputActive;
		Point lineTopPosition = currentStatePosition.getLocation();
		Point lineBottomPosition = currentStatePosition.getLocation();
		Point xorPosition = currentStatePosition.getLocation();
		
		lineTopPosition.x += lineHorizontalPadding;
		lineBottomPosition.x = xorPosition.x = lineTopPosition.x;
		lineBottomPosition.y -= lineHeight - sBoxHeight;
		xorPosition.y -= xorOffset;
		
		ByteArray inputDifference = round > cipher.getNumRounds() ?
			differential.getIntermediateStateDifference(round).getDelta() : 
			differential.getStateDifference(round - 1).getDelta();
		ByteArray outputDifference = round > cipher.getNumRounds() ?
			differential.getStateDifference(round - 1).getDelta() :
			differential.getIntermediateStateDifference(round).getDelta();
		
		for (int i = 0; i < numBits; i++) {
			if (i != 0 && i % numBitsPerSBox == 0) {
				lineTopPosition.x += lineHorizontalSBoxOffset;
				lineBottomPosition.x = xorPosition.x = lineTopPosition.x;
			}
			
			if (!renderOnlyActiveTrails) {
				RenderUtil.renderXOR(contentByte, (float)xorPosition.x, (float)xorPosition.y, xorRadius);
			}
			
			isInputActive = inputDifference.getBit(i);
			
			if (isInputActive) {
				setActiveLineColorAndWidth();
			} 
			
			if (isInputActive || !renderOnlyActiveTrails) {
				RenderUtil.renderLine(contentByte, lineTopPosition, xorPosition);
			}
			
			isOutputActive = outputDifference.getBit(i);
			
			if (isOutputActive) {
				setActiveLineColorAndWidth();
			} 
			
			if (isOutputActive || !renderOnlyActiveTrails) {
				RenderUtil.renderLine(contentByte, xorPosition, lineBottomPosition);
			}
			
			resetLineColorAndWidth();
			
			lineTopPosition.x += lineOffset;
			lineBottomPosition.x = xorPosition.x = lineTopPosition.x;
		}
	}
	
	private void renderPermutationLayer(int round) {
		final double initialX = currentStatePosition.getLocation().x;
		
		Point smallLineStartPosition = currentStatePosition.getLocation();
		Point lineStartPosition = currentStatePosition.getLocation();
		Point lineEndPosition = currentStatePosition.getLocation();
		lineStartPosition.y -= lineHeight + belowSBoxLineHeight;
		lineEndPosition.y -= lineHeight + belowSBoxLineHeight + permutationLineHeight;
		
		int lineEndIndex;
		boolean isActive;
		
		ByteArray stateDifference = (round == cipher.getNumRounds()) ?
			differential.getIntermediateStateDifference(round + 1).getDelta() :
			differential.getStateDifference(round).getDelta();
		
		for (int i = 0; i < numBits; i++) {
			if (i != 0 && i % numBitsPerSBox == 0) {
				lineStartPosition.x += lineHorizontalSBoxOffset;
				lineEndPosition.x = lineStartPosition.x;
			}
			
			lineStartPosition.x += lineOffset;
			smallLineStartPosition.x = lineStartPosition.x;
			smallLineStartPosition.y = lineStartPosition.y + belowSBoxLineHeight;
			
			isActive = stateDifference.getBit(PRESENT.INVERSE_PERMUTATION[i]);
			
			if (isActive) {
				setActiveLineColorAndWidth();
			}
			
			if (isActive || !renderOnlyActiveTrails) {
				RenderUtil.renderLine(contentByte, smallLineStartPosition, lineStartPosition);
			}
			
			lineEndIndex = PRESENT.INVERSE_PERMUTATION[i];
			lineEndPosition.x = initialX 
				+ (lineEndIndex + 1) * lineOffset 
				+ (lineEndIndex / numBitsPerSBox) * lineHorizontalSBoxOffset;
			
			if (isActive || !renderOnlyActiveTrails) {
				RenderUtil.renderLine(contentByte, lineStartPosition, lineEndPosition);
			}
			
			resetLineColorAndWidth();
		}
	}
	
	private void renderRoundLabel(int round) throws DocumentException, IOException {
		Point textPosition = currentStatePosition.getLocation();
		textPosition.x -= 30; 
		textPosition.y -= 14;
		
		RenderUtil.renderText(contentByte, textPosition, "RK", 12);
		textPosition.x += 17;
		textPosition.y += 5;
		RenderUtil.renderText(contentByte, textPosition, Integer.toString(round), 9);
		contentByte.setColorFill(BaseColor.WHITE);
	}
	
	private void renderSBoxLayer(int round) throws DocumentException, IOException {
		Point sBoxPosition = currentStatePosition.getLocation();
		Point textPosition = currentStatePosition.getLocation();
		sBoxPosition.y -= sBoxOffset;
		
		ByteArray intermediateState = differential.getIntermediateStateDifference(round).getDelta();
		boolean isInputActive;
		
		resetFill();
		
		for (int i = 0; i < numSBoxes; i++) {
			isInputActive = intermediateState.getNibble(i) != 0;
			
			if (isInputActive || !renderOnlyActiveTrails) {
				contentByte.rectangle((float)sBoxPosition.x, (float)sBoxPosition.y, sBoxWidth, sBoxHeight);
			}
			
			sBoxPosition.x += sBoxWidth + sBoxPadding;
			
			if (isInputActive) {
				setActiveLineColorAndWidth();
				setActiveFill();
			}
			
			if (isInputActive || !renderOnlyActiveTrails) {
				contentByte.fillStroke();
				textPosition.x = sBoxPosition.x - 33; 
				textPosition.y = sBoxPosition.y + 4; 
				RenderUtil.renderText(contentByte, textPosition, "S");
			}
			
			resetLineColorAndWidth();
			resetFill();
		}
	}
	
	private void resetLineColorAndWidth() {
		contentByte.setColorStroke(BaseColor.GRAY);
		contentByte.setLineWidth(defaultLineWidth);
	}
	
	private void resetFill() {
		contentByte.setColorFill(BaseColor.WHITE);
		contentByte.setColorStroke(BaseColor.GRAY);
		contentByte.setLineWidth(defaultLineWidth);
	}
	
	private void setActiveLineColorAndWidth() {
		contentByte.setColorStroke(activeStateColor);
		contentByte.setLineWidth(2.0f);
	}

	private void setActiveFill() {
		contentByte.setColorFill(activeStateColor);
	}
	
}
