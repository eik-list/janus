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
import de.mslab.core.Biclique;

public class BicliqueAllInOneRenderer implements IBicliqueRenderer {
	
	private static final BaseColor forwardDifferentialActiveBytesColor = new BaseColor(0xFF, 0, 0); //0xC1, 0x9E, 0xD9); 
	private static final BaseColor backwardDifferentialActiveBytesColor = new BaseColor(0x2B, 0x6D, 0xAA); 
	
	private DifferentialRenderer differentialRenderer;
	private com.itextpdf.awt.geom.Rectangle differentialBounds;
	private Document document;
	private FileOutputStream fileOutputStream;
	
	private int padding = 50;
	private PdfWriter writer;
	private PdfContentByte contentByte;
	private Rectangle pageSize;
	
	public DifferentialRenderer getDifferentialRenderer() {
		return differentialRenderer;
	}
	
	/**
	 * Returns the padding in pixels from top and left to the leftmost differential. 
	 */
	public int getPadding() {
		return padding;
	}
	
	/**
	 * Sets the padding in pixels.
	 */
	public void setPadding(int padding) {
		this.padding = padding;
	}
	
	public void setDifferentialRenderer(DifferentialRenderer differentialRenderer) {
		this.differentialRenderer = differentialRenderer;
	}
	
	public void renderBiclique(String pathname, Biclique biclique, RoundBasedBlockCipher cipher) 
		throws IOException, DocumentException {
		
		if (this.differentialRenderer == null) {
			createDifferentialRenderer(cipher);
		}
		
		setSize(biclique, cipher);
		createPDF(pathname);
		renderDifferentials(biclique, cipher);
		closePDF();
	}
	
	private void createDifferentialRenderer(RoundBasedBlockCipher cipher) {
		 if (cipher.operatesBytewise()) {
			 differentialRenderer = new BicliqueDifferentialRenderer();
		 } else {
			 differentialRenderer = new BitwiseDifferentialRenderer();
		 }
	}
	
	private void setSize(Biclique biclique, RoundBasedBlockCipher cipher) {
		differentialRenderer.setUp(cipher);
		differentialBounds = differentialRenderer.determineSize(biclique.deltaDifferential);
		differentialBounds.x += 2 * padding;
		differentialBounds.y += 2 * padding;
		pageSize = new Rectangle((float)differentialBounds.x, (float)differentialBounds.y);
	}
	
	private void renderDifferentials(Biclique biclique, RoundBasedBlockCipher cipher) throws DocumentException, IOException {
		differentialRenderer.setContentByte(contentByte);
		
		Point position = new Point(
			padding, 
			document.getPageSize().getHeight() - padding
		);
		differentialRenderer.setRenderOnlyActiveTrails(false);
		differentialRenderer.renderDifferential(
			biclique.deltaDifferential, position, "", forwardDifferentialActiveBytesColor, forwardDifferentialActiveBytesColor
		);
		differentialRenderer.setRenderOnlyActiveTrails(true);
		differentialRenderer.renderDifferential(
			biclique.nablaDifferential, position, "", backwardDifferentialActiveBytesColor, backwardDifferentialActiveBytesColor
		);
		
		differentialRenderer.tearDown();
	}
	
	private void closePDF() {
		document.close();
	}
	
	private void createPDF(String pathname) throws IOException, DocumentException {
		String directory = getDirectoryPath(pathname);
		File file = new File(directory);
		
		if (!file.exists()) {
			file.mkdir();
		}
		
		file = new File(pathname);
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		fileOutputStream = new FileOutputStream(pathname);
		document = new Document(pageSize);
		writer = PdfWriter.getInstance(document, fileOutputStream);
		document.open();
		contentByte = writer.getDirectContent();
	}
	
	private String getDirectoryPath(String pathname) {
		int lastSeparator = pathname.lastIndexOf("/");
		
		if (lastSeparator < 0) {
			return ".";
		} else {
			return pathname.substring(0, lastSeparator);
		}
	}
	
}
