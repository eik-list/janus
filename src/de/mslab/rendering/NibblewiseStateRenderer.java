package de.mslab.rendering;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;

import de.mslab.core.ByteArray;

public class NibblewiseStateRenderer extends StateRenderer {
	
	protected int numColumns;
	protected int numRows;
	protected boolean rendersColumnwise = false;
	
	public NibblewiseStateRenderer(int stateSize, int cellSize, boolean rendersColumnwise) {
		super();
		createGrid(stateSize);
		setGrid(numColumns, numRows, cellSize);
		this.rendersColumnwise = rendersColumnwise;
	}
	
	public boolean doesRenderColumnwise() {
		return rendersColumnwise;
	}
	
	public void setGrid(int numColumns, int numRows, int cellSize) {
		this.cellSize = cellSize;
		this.numColumns = numColumns;
		this.numRows = numRows;
		bounds = new Point(numColumns * cellSize / 2, numRows * cellSize);
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
				
				positionX = (float)position.x + column * cellSize / 2;
				positionY = (float)position.y - (row + 1) * cellSize;
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
		
		contentByte.rectangle(positionX, positionY, cellSize / 2, cellSize);
		contentByte.fillStroke();
	}
	
	protected void createGrid(int stateSize) {
		int[] squares = { 1, 4, 9, 16, 25, 36, 49, 64 };
		
		for (int i = 0; i < squares.length; i++) {
			if (stateSize == squares[i]) {
				numColumns = 2 * (i + 1);
				numRows = i + 1;
				return;
			}
		}
		
		int[] bestNumColumns = { 4, 3, 2, 5, 7 };
		
		for (int i = 0; i < bestNumColumns.length; i++) {
			if (stateSize % bestNumColumns[i] == 0) {
				numColumns = 2 * bestNumColumns[i];
				numRows = stateSize / bestNumColumns[i];
				return;
			}
		}
		
		numColumns = 2 * stateSize;
		numRows = 1;
	}
	
}
