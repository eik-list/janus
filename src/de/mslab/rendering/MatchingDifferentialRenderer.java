package de.mslab.rendering;

import java.io.IOException;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;

import de.mslab.core.ByteArray;
import de.mslab.core.Differential;

public class MatchingDifferentialRenderer extends AbstractDifferentialRenderer {
	
	public Rectangle determineSize(Differential differential, int intermediateRound) {
		double boundsX = stateRenderer.getBounds().x;
		double boundsY = stateRenderer.getBounds().y;
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;
		Rectangle result = new Rectangle();
		
		result.x += boundsX + 2 * offset; // space for initial state
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			result.x += boundsX + 2 * offset; // space for intermediate state + offsets
		}
		
		if (intermediateRound != toRound) {
			if (fromRound - intermediateRound > toRound - intermediateRound) {
				toRound = intermediateRound;
			} else {
				fromRound = intermediateRound + 1;
			}
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (cipher.hasKeyInjectionInRound(round)) {
				result.x += boundsX + 2 * offset; // space for intermediate state + offsets
			}
			
			result.x += boundsX + 2 * offset; // space for state
		}
		
		result.y = 2 * boundsY + 2 * offset; // space for state + key
		return result;
	}
	
	public void renderDifferential(Differential differential, Point position, String label, 
		BaseColor activeStateColor, BaseColor activeKeyColor) throws DocumentException, IOException {
		super.renderDifferential(differential, position, label, activeStateColor, activeKeyColor);
		
		int fromRound = differential.fromRound;
		int toRound = differential.toRound;
		
		renderTopLabel(label);
		renderInitialState();
		
		if (fromRound == 1 && cipher.hasKeyInjectionInRound(0)) {
			renderKeyInjectionAndIntermediateState(0);
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			renderRound(round);
		}
	}
	
	private void renderRound(int round) throws DocumentException, IOException {
		renderRoundStructures(round);
		
		if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundBegin(round)) { 
			renderKeyInjectionAndIntermediateState(round);
		}
		
		currentStatePosition.x += stateRenderer.getBounds().x / 2 + offset;
		renderRoundLabel(round);
		
		if (cipher.hasKeyInjectionInRound(round) && cipher.injectsKeyAtRoundEnd(round)) { 
			renderIntermediateStateAndKeyInjection(round);
		}
		
		currentStatePosition.x += stateRenderer.getBounds().x / 2 + offset;
		
		if (round == cipher.getNumRounds() && cipher.hasKeyInjectionInRound(round + 1)) {
			renderIntermediateState(round + 1);
			currentStatePosition.x += stateRenderer.getBounds().x / 2 + offset;
			renderKeyInjection(round + 1);
			currentStatePosition.x += stateRenderer.getBounds().x / 2 + offset;
		}
		
		renderState(round);
	}
	
	private void renderInitialState() {
		renderState(differential.fromRound - 1);
	}
	
	private void renderKeyInjection(int round) {
		Point xorPosition = currentStatePosition.getLocation();
		RenderUtil.renderXOR(contentByte, xorPosition);
		
		Point keyPosition = xorPosition.getLocation();
		keyPosition.y -= stateRenderer.getBounds().y;
		keyPosition.y -= offset;
		
		RenderUtil.renderLine(contentByte, keyPosition, xorPosition);
		
		if (differential.getKeyDifference(round) != null) {
			ByteArray key = differential.getKeyDifference(round).getDelta();
			stateRenderer.renderState(contentByte, key, keyPosition, activeKeyColor);
		}
	}
	
	private void renderIntermediateStateAndKeyInjection(int round) {
		currentStatePosition.x += stateRenderer.getBounds().x / 2 + offset;
		renderIntermediateState(round);
		currentStatePosition.x += stateRenderer.getBounds().x / 2 + offset;
		renderKeyInjection(round);
	}
	
	private void renderKeyInjectionAndIntermediateState(int round) {
		Point lineStartPosition = currentStatePosition.getLocation();
		lineStartPosition.x += stateRenderer.getBounds().x / 2;
		nextStatePosition = currentStatePosition.getLocation();
		nextStatePosition.x += stateRenderer.getBounds().x + 2 * offset;
		RenderUtil.renderLine(contentByte, lineStartPosition, nextStatePosition);
		
		currentStatePosition.x += stateRenderer.getBounds().x / 2 + offset;
		renderKeyInjection(round);
		currentStatePosition.x += stateRenderer.getBounds().x / 2 + offset;
		renderIntermediateState(round);
	}
	
	private void renderRoundLabel(int round) throws DocumentException, IOException {
		String text = "Round " + round;
		Point textPosition = currentStatePosition.getLocation();
		textPosition.y += stateRenderer.getBounds().y / 2 + offset;
		textPosition.x += offset / 2;
		RenderUtil.renderText(contentByte, textPosition, text);
	}
	
	private void renderRoundStructures(int round) {
		if (cipher.hasKeyInjectionInRound(round)) {
			nextStatePosition.x = currentStatePosition.x + 2 * stateRenderer.getBounds().x + 4 * offset;
		} else {
			nextStatePosition.x = currentStatePosition.x + stateRenderer.getBounds().x + 2 * offset;
		}
		
		Point lineStartPosition = currentStatePosition.getLocation();
		lineStartPosition.x += stateRenderer.getBounds().x / 2;
		RenderUtil.renderLine(contentByte, lineStartPosition, nextStatePosition);
	}
	
	private void renderIntermediateState(int round) {
		if (differential.getIntermediateStateDifference(round) != null) {
			ByteArray state = differential.getIntermediateStateDifference(round).getDelta();
			stateRenderer.renderState(contentByte, state, currentStatePosition, activeStateColor);
		}
	}
	
	private void renderState(int round) {
		ByteArray state = differential.getStateDifference(round).getDelta();
		stateRenderer.renderState(contentByte, state, currentStatePosition, activeStateColor);
	}
	
	private void renderTopLabel(String label) throws DocumentException, IOException {
		Point textPosition = currentStatePosition.getLocation();
		textPosition.x -= stateRenderer.getBounds().x / 2 + offset;
		textPosition.y += 50;
		RenderUtil.renderText(contentByte, textPosition, label);
	}
	
}
