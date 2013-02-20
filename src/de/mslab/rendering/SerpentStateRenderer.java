package de.mslab.rendering;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;

import de.mslab.core.ByteArray;

public class SerpentStateRenderer extends NibblewiseStateRenderer {
	
	public SerpentStateRenderer(int cellSize) {
		super(16, cellSize, true);
	}
	
	public void renderState(PdfContentByte contentByte, ByteArray state, Point position, BaseColor activeColor) {
		int value;
		position = position.getLocation();
		position.x -= bounds.x / 2;
		position.y += bounds.y / 2;
		
		contentByte.setColorStroke(BaseColor.BLACK);
		contentByte.setColorFill(BaseColor.GRAY);
		contentByte.setLineWidth(0.1f);

		for (int column = 0; column < numColumns; column++) {
			value = state.getNibble(column);
			
			for (int row = 1; row <= numRows; row++) {
				if ((value & 0x8) == 0) {
					contentByte.setColorFill(BaseColor.WHITE);
				} else {
					contentByte.setColorFill(activeColor);
				}
					
				contentByte.rectangle(
					(float)position.x + column * cellSize, 
					(float)position.y - row * cellSize, 
					cellSize, cellSize
				);
				contentByte.fillStroke();
				contentByte.setLineWidth(0.1f);
				value <<= 1;
			}
		}
		
		contentByte.setLineWidth(1f);
		contentByte.rectangle((float)position.x, (float)position.y - (float)bounds.y, (float)bounds.x, (float)bounds.y);
		contentByte.stroke();
		contentByte.setLineWidth(0.1f);
	}
	
	public void setGrid(int numColumns, int numRows, int cellSize) {
		this.cellSize = cellSize;
		this.numColumns = numColumns;
		this.numRows = numRows;
		bounds = new Point(numColumns * cellSize, numRows * cellSize);
	}
	
	protected void createGrid(int stateSize) {
		this.numColumns = 32;
		this.numRows = 4;
	}
	
}
