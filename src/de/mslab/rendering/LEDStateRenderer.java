package de.mslab.rendering;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;

import de.mslab.core.ByteArray;

public class LEDStateRenderer extends StateRenderer {
	
	private int numColumns;
	private int numRows;
	private boolean rendersColumnwise = false;
	
	public LEDStateRenderer(int cellSize) {
		super();
		setGrid(4, 4, cellSize);
	}
	
	public boolean doesRenderColumnwise() {
		return rendersColumnwise;
	}
	
	public void setGrid(int numColumns, int numRows, int cellSize) {
		this.cellSize = cellSize;
		this.numColumns = numColumns;
		this.numRows = numRows;
		bounds = new Point(numColumns * cellSize, numRows * cellSize);
	}
	
	public Point getBounds() {
		return bounds;
	}
	
	public void renderState(PdfContentByte contentByte, ByteArray state, Point position, BaseColor activeColor) {
		position = position.getLocation();
		position.x -= bounds.x / 2;
		position.y += bounds.y / 2;
		
		contentByte.setColorStroke(BaseColor.BLACK);
		contentByte.setLineWidth(1.5f);
		contentByte.rectangle((float)position.x, (float)(position.y - bounds.y), (float)bounds.x, (float)bounds.y);
		contentByte.stroke();
		contentByte.setLineWidth(0.1f);
		
		int index = 0;
		short value;
		float positionX, positionY;
		boolean isEven = true;
		
		for (int row = 0; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++) {
				value = state.get(index);
				
				if (isEven) {
					value = (short)(value & 0xF0);
				} else {
					value = (short)(value & 0x0F);
					index++;
				}
				
				if (rendersColumnwise) {
					positionX = (float)position.x + row * cellSize;
					positionY = (float)position.y - (column + 1) * cellSize;
				} else {
					positionX = (float)position.x + column * cellSize;
					positionY = (float)position.y - (row + 1) * cellSize;
				}
				
				renderNibble(contentByte, positionX, positionY, activeColor, value);
				isEven = !isEven;
			}
		}
	}
	
	private void renderNibble(PdfContentByte contentByte, float positionX, float positionY, BaseColor activeColor, short value) {
		if (value == 0) {
			contentByte.setColorFill(BaseColor.WHITE);
		} else {
			contentByte.setColorFill(activeColor);
		}
		
		contentByte.rectangle(positionX, positionY, cellSize, cellSize);
		contentByte.fillStroke();
	}
	
}
