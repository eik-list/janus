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
import de.mslab.core.ByteArray;
import de.mslab.core.Difference;
import de.mslab.core.Differential;

public class BicliqueRenderer implements IBicliqueRenderer {
	
	private static final String BASE_COMPUTATION_LABEL = "Base computation";
	private static final String DELTA_DIFFERENTIAL_LABEL = "Forward differential";
	private static final String NABLA_DIFFERENTIAL_LABEL = "Backward differential";
	
	private static final BaseColor simpleDifferentialActiveBytesColor = BaseColor.GRAY; 
	private static final BaseColor forwardDifferentialActiveBytesColor = new BaseColor(0xC1, 0x9E, 0xD9); 
	private static final BaseColor backwardDifferentialActiveBytesColor = new BaseColor(0x2B, 0x6D, 0xAA); 
	
	private static final int numDifferentials = 3;
	
	private DifferentialRenderer differentialRenderer;
	private com.itextpdf.awt.geom.Rectangle differentialBounds;
	private Document document;
	private FileOutputStream fileOutputStream;
	
	private int padding = 50;
	private PdfWriter writer;
	private PdfContentByte contentByte;
	private Rectangle pageSize;
	
	public BicliqueRenderer() {
		
	}
	
	/* (non-Javadoc)
	 * @see de.mslab.rendering.IBicliqueRenderer#getDifferentialRenderer()
	 */
	@Override
	public DifferentialRenderer getDifferentialRenderer() {
		return differentialRenderer;
	}
	
	/* (non-Javadoc)
	 * @see de.mslab.rendering.IBicliqueRenderer#getPadding()
	 */
	@Override
	public int getPadding() {
		return padding;
	}
	
	/* (non-Javadoc)
	 * @see de.mslab.rendering.IBicliqueRenderer#setPadding(int)
	 */
	@Override
	public void setPadding(int padding) {
		this.padding = padding;
	}
	
	/* (non-Javadoc)
	 * @see de.mslab.rendering.IBicliqueRenderer#setDifferentialRenderer(de.mslab.rendering.DifferentialRenderer)
	 */
	@Override
	public void setDifferentialRenderer(DifferentialRenderer differentialRenderer) {
		this.differentialRenderer = differentialRenderer;
	}
	
	/* (non-Javadoc)
	 * @see de.mslab.rendering.IBicliqueRenderer#renderBiclique(java.lang.String, de.mslab.core.Biclique, de.mslab.ciphers.RoundBasedBlockCipher)
	 */
	@Override
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
		differentialBounds.x += padding;
		differentialBounds.y += padding;
		
		final float sizeX = (float)differentialBounds.x * numDifferentials + 2 * padding;
		final float sizeY = (float)differentialBounds.y + padding;
		pageSize = new Rectangle(sizeX, sizeY);
	}
	
	private void renderDifferentials(Biclique biclique, RoundBasedBlockCipher cipher) throws DocumentException, IOException {
		differentialRenderer.setContentByte(contentByte);
		
		Point position = new Point(
			padding, 
			document.getPageSize().getHeight() - padding
		);
		Differential emptyDifferential = createEmptyDifferential(biclique.deltaDifferential, cipher);
		differentialRenderer.renderDifferential(
			emptyDifferential, position, BASE_COMPUTATION_LABEL, simpleDifferentialActiveBytesColor, simpleDifferentialActiveBytesColor
		);
		position.x += differentialBounds.x;
		differentialRenderer.renderDifferential(
			biclique.deltaDifferential, position, DELTA_DIFFERENTIAL_LABEL, forwardDifferentialActiveBytesColor, forwardDifferentialActiveBytesColor
		);
		position.x += differentialBounds.x; 
		differentialRenderer.renderDifferential(
			biclique.nablaDifferential, position, NABLA_DIFFERENTIAL_LABEL, backwardDifferentialActiveBytesColor, backwardDifferentialActiveBytesColor
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
	
	private Differential createEmptyDifferential(Differential differential, RoundBasedBlockCipher cipher) {
		Differential emptyDifferential = new Differential(differential.fromRound, differential.toRound);
		Difference difference = clear(differential.getStateDifference(differential.fromRound - 1));
		emptyDifferential.setStateDifference(differential.fromRound - 1, difference);
		
		for (int round = differential.fromRound - 1; round <= differential.toRound; round++) {
			difference = clear(differential.getStateDifference(round));
			emptyDifferential.setStateDifference(round, difference);
			difference = clear(differential.getIntermediateStateDifference(round));
			emptyDifferential.setIntermediateStateDifference(round, difference);
			difference = clear(differential.getKeyDifference(round));
			emptyDifferential.setKeyDifference(round, difference);
		}
		
		if (differential.toRound == cipher.getNumRounds()
			&& cipher.hasKeyInjectionInRound(differential.toRound + 1)) {
			int round = differential.toRound + 1;
			difference = clear(differential.getIntermediateStateDifference(round));
			emptyDifferential.setIntermediateStateDifference(round, difference);
			difference = clear(differential.getKeyDifference(round));
			emptyDifferential.setKeyDifference(round, difference);
		}
		
		return emptyDifferential;
	}
	
	private Difference clear(Difference difference) {
		if (difference == null) {
			return null;
		} else {
			ByteArray delta = new ByteArray(difference.getDelta().length());
			return new Difference(delta);
		}
	}
	
}
