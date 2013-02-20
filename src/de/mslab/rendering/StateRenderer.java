package de.mslab.rendering;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;

import de.mslab.core.ByteArray;

public abstract class StateRenderer {
	
	protected Point bounds;
	protected int cellSize;
	
	public StateRenderer() {
		
	}
	
	public abstract Point getBounds();
	
	public void renderState(PdfContentByte contentByte, ByteArray state, Point position) {
		renderState(contentByte, state, position, BaseColor.GRAY);
	}
	
	public abstract void renderState(PdfContentByte contentByte, ByteArray state, Point position, BaseColor activeColor);
	public abstract void setGrid(int numColumns, int numRows, int cellSize);
	
}
