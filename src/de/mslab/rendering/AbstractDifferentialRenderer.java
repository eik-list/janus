package de.mslab.rendering;

import java.io.IOException;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.Differential;
import de.mslab.utils.Logger;


abstract class AbstractDifferentialRenderer implements DifferentialRenderer {

	protected static final Logger logger = Logger.getLogger();
	
	protected int cellSize = 10;
	protected BaseColor activeKeyColor;
	protected BaseColor activeStateColor;
	
	protected int offset = 10;
	protected RoundBasedBlockCipher cipher;
	protected PdfContentByte contentByte;
	
	protected Differential differential; 
	protected StateRenderer stateRenderer;
	
	protected Point currentStatePosition;
	protected Point nextStatePosition;
	
	protected float defaultLineWidth = 0.1f;
	protected boolean renderOnlyActiveTrails = false;
	
	public PdfContentByte getContentByte() {
		return contentByte;
	}
	
	public void setContentByte(PdfContentByte contentByte) {
		this.contentByte = contentByte;
		this.contentByte.setLineWidth(defaultLineWidth );
	}
	
	public StateRenderer getStateRenderer() {
		return stateRenderer;
	}
	
	public void setStateRenderer(StateRenderer stateRenderer) {
		this.stateRenderer = stateRenderer;
	}
	
	public boolean getRenderOnlyActiveTrails() {
		return renderOnlyActiveTrails;
	}
	
	public void setRenderOnlyActiveTrails(boolean renderOnlyActiveTrails) {
		this.renderOnlyActiveTrails = renderOnlyActiveTrails;
	}
	
	public void setUp(RoundBasedBlockCipher cipher) {
		this.cipher = cipher;
		
		if (this.stateRenderer == null) {
			if (cipher.operatesBytewise()) {
				if (cipher.operatesNibblewise()) {
					this.stateRenderer = new NibblewiseStateRenderer(cipher.getStateSize(), cellSize, cipher.operatesColumnwise());
				} else {
					this.stateRenderer = new BytewiseStateRenderer(cipher.getStateSize(), cellSize, cipher.operatesColumnwise());
				}
			} else {
				this.stateRenderer = new BitwiseStateRenderer(cipher.getStateSize(), 2 * cellSize);
			}
		}
	}
	
	public Rectangle determineSize(Differential differential) {
		return determineSize(differential, differential.toRound);
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
			result.y += boundsY + 2 * offset; // space for intermediate state + offsets
		}
		
		for (int round = fromRound; round <= toRound; round++) {
			if (cipher.hasKeyInjectionInRound(round)) {
				result.y += boundsY + 2 * offset; // space for intermediate state + offsets
			}
			
			result.y += boundsY + 2 * offset; // space for state
		}
		
		result.x = 2 * boundsX + offset; // space for state + key
		return result;
	}
	
	public Rectangle determineSize(Differential p_mergedto_v, int intermediateMatchingRound, boolean renderBeginning) {
		return determineSize(p_mergedto_v, intermediateMatchingRound);
	}
	
	public void renderDifferential(Differential differential, Point position, String label, 
		BaseColor activeStateColor, BaseColor activeKeyColor) throws DocumentException, IOException {
		
		this.differential = differential;
		this.currentStatePosition = (Point)position.clone();
		this.nextStatePosition = (Point)position.clone();
		this.activeStateColor = activeStateColor;
		this.activeKeyColor = activeKeyColor;
	}
	
	public void tearDown() {
		contentByte = null;
		differential = null;
		cipher = null;
		currentStatePosition = null;
		nextStatePosition = null;
	}
	
}
