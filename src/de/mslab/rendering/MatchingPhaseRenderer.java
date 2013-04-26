package de.mslab.rendering;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.matching.MatchingFinderResult;


public class MatchingPhaseRenderer {
	
	protected static final String FORWARD_MATCHING = "Forward matching";
	protected static final String BACKWARD_MATCHING = "Backward matching";
	
	protected static final BaseColor simpleDifferentialActiveBytesColor = BaseColor.GRAY; 
	protected static final BaseColor backwardDifferentialActiveBytesColor = new BaseColor(0xC1, 0x9E, 0xD9); 
	protected static final BaseColor forwardDifferentialActiveBytesColor = new BaseColor(0x2B, 0x6D, 0xAA); 
	
	protected static final int numDifferentials = 2;
	protected static final int padding = 50;
	
	protected DifferentialRenderer differentialRenderer = new MatchingDifferentialRenderer();
	protected com.itextpdf.awt.geom.Rectangle differentialBounds;
	protected Document document;
	protected FileOutputStream fileOutputStream;
	protected PdfWriter writer;
	protected PdfContentByte contentByte;
	protected Rectangle pageSize;
	
	public MatchingPhaseRenderer() {
		
	}
	
	public DifferentialRenderer getDifferentialRenderer() {
		return differentialRenderer;
	}
	
	public void setDifferentialRenderer(DifferentialRenderer differentialRenderer) {
		this.differentialRenderer = differentialRenderer;
	}
	
	public void renderMatchingPhase(String pathname, MatchingFinderResult result, RoundBasedBlockCipher cipher) 
	throws IOException, DocumentException {
		setSize(result, cipher);
		createPDF(pathname);
		renderDifferentials(result, cipher);
		closePDF();
	}
	
	protected void setSize(MatchingFinderResult result, RoundBasedBlockCipher cipher) {
		differentialRenderer.setUp(cipher);
		Point maxBounds = new Point();
		differentialBounds = differentialRenderer.determineSize(result.p_mergedto_v, result.bestMatchingRound);
		maxBounds.x = differentialBounds.x;
		maxBounds.y = differentialBounds.y;
		differentialBounds = differentialRenderer.determineSize(result.s_mergedto_v, result.bestMatchingRound);
		
		if (differentialBounds.x < maxBounds.x) {
			differentialBounds.x = maxBounds.x;
		}
		
		if (differentialBounds.y < maxBounds.y) {
			differentialBounds.y = maxBounds.y;
		}
		
		differentialBounds.y += padding;
		differentialBounds.x += 2 * padding;
		
		float sizeY = (float)differentialBounds.y * numDifferentials + 2 * padding;
		pageSize = new Rectangle((float)differentialBounds.x, sizeY);
	}
	
	protected void renderDifferentials(MatchingFinderResult result, RoundBasedBlockCipher cipher) throws DocumentException, IOException {
		differentialRenderer.setUp(cipher);
		differentialRenderer.setContentByte(contentByte);
		
		Point position = new Point(padding, differentialBounds.y);
		differentialRenderer.renderDifferential(
			result.s_mergedto_v, position, BACKWARD_MATCHING, simpleDifferentialActiveBytesColor, backwardDifferentialActiveBytesColor
		);
		position.y += differentialBounds.y;
		differentialRenderer.renderDifferential(
			result.p_mergedto_v, position, FORWARD_MATCHING, simpleDifferentialActiveBytesColor, forwardDifferentialActiveBytesColor
		);
		
		differentialRenderer.tearDown();
	}
	
	protected void closePDF() {
		document.close();
	}
	
	protected void createPDF(String pathname) throws IOException, DocumentException {
		String directory = getDirectoryPath(pathname);
		File file = new File(directory);
		
		if (!file.exists()) {
			file.mkdir();
		}
		
		file = new File(pathname);
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		fileOutputStream = new FileOutputStream(file);
		document = new Document(pageSize);
		writer = PdfWriter.getInstance(document, fileOutputStream);
		document.open();
		contentByte = writer.getDirectContent();
	}
	
	protected String getDirectoryPath(String pathname) {
		int lastSeparator = pathname.lastIndexOf("/");
		
		if (lastSeparator < 0) {
			return ".";
		} else {
			return pathname.substring(0, lastSeparator);
		}
	}
	
}
