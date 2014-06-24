package de.mslab.rendering;

import java.io.IOException;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class BicliqueDifferentialRenderer extends AbstractDifferentialRenderer {
	
	public void renderDifferential(Differential differential, Point position, String label, 
		BaseColor activeStateColor, BaseColor activeKeyColor) throws DocumentException, IOException {
		super.renderDifferential(differential, position, label, activeStateColor, activeKeyColor);
		
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;
		
		renderTopLabel(label);
		renderInitialState();
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			renderRoundStructures(0);
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
			renderKeyInjection(0);
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
			renderIntermediateState(0);
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			renderRound(round);
		}
	}
	
	protected void renderRound(int round) throws DocumentException, IOException {
		renderRoundStructures(round);
		
		if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundBegin(round)) {
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
			renderKeyInjection(round);
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
			renderIntermediateState(round);
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
		} else {
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
		}
		
		renderRoundLabel(round);
		
		if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundEnd(round)) {
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
			renderIntermediateState(round);
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
			renderKeyInjection(round);
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
		} else {
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
		}
		
		if (round == cipher.getNumRounds()
			&& cipher.hasKeyInjectionInRound(round + 1)) {
			renderKeyInjection(round + 1);
			currentStatePosition.y -= stateRenderer.getBounds().y / 2 + offset;
		}
		
		renderState(round);
	}
	
	protected void renderInitialState() {
		renderState(differential.fromRound - 1);
	}
	
	protected void renderKeyInjection(int round) {
		Point xorPosition = currentStatePosition.getLocation();
		RenderUtil.renderXOR(contentByte, xorPosition);
		
		Point keyPosition = xorPosition.getLocation();
		keyPosition.x -= stateRenderer.getBounds().x;
		keyPosition.x -= offset;
		
		RenderUtil.renderLine(contentByte, keyPosition, xorPosition);
		
		ByteArray key = differential.getKeyDifference(round).getDelta();
		stateRenderer.renderState(contentByte, key, keyPosition, activeKeyColor);
	}
	
	protected void renderRoundLabel(int round) throws DocumentException, IOException {
		String text = "Round " + round;
		int textLengthInPixels = 30;
		Point textPosition = currentStatePosition.getLocation();
		textPosition.x -= stateRenderer.getBounds().x / 2 + offset + textLengthInPixels;
		textPosition.y -= offset / 2;
		RenderUtil.renderText(contentByte, textPosition, text);
	}
	
	protected void renderRoundStructures(int round) {
		if (cipher.hasKeyInjectionInRound(round)) {
			nextStatePosition.y = currentStatePosition.y - 2 * stateRenderer.getBounds().y - 4 * offset;
		} else {
			nextStatePosition.y = currentStatePosition.y - stateRenderer.getBounds().y - 2 * offset;
		}
		
		RenderUtil.renderLine(contentByte, currentStatePosition, nextStatePosition);
	}
	
	protected void renderIntermediateState(int round) {
		ByteArray state = differential.getIntermediateStateDifference(round).getDelta();
		stateRenderer.renderState(contentByte, state, currentStatePosition, activeStateColor);
	}
	
	protected void renderState(int round) {
		ByteArray state = differential.getStateDifference(round).getDelta();
		stateRenderer.renderState(contentByte, state, currentStatePosition, activeStateColor);
	}
	
	protected void renderTopLabel(String label) throws DocumentException, IOException {
		Point textPosition = currentStatePosition.getLocation();
		textPosition.y += stateRenderer.getBounds().y / 2 + offset;
		textPosition.x -= 50;
		RenderUtil.renderText(contentByte, textPosition, label);
	}
	
}
