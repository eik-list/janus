package de.mslab.rendering;

import java.io.IOException;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;

import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.core.Differential;

public interface DifferentialRenderer {
	public PdfContentByte getContentByte();
	public void setContentByte(PdfContentByte contentByte);
	public StateRenderer getStateRenderer();
	public void setStateRenderer(StateRenderer stateRenderer);
	public void setUp(RoundBasedBlockCipher cipher);
	public Rectangle determineSize(Differential differential);
	public Rectangle determineSize(Differential differential, int intermediateRound);
	public void renderDifferential(Differential differential, Point position, String label, BaseColor activeStateColor, BaseColor activeKeyColor) throws DocumentException, IOException;
	public void tearDown();
}
