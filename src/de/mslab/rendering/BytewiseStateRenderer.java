package de.mslab.rendering;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;

import de.mslab.core.ByteArray;

public class BytewiseStateRenderer extends StateRenderer {
	
	private int numColumns;
	private int numRows;
	private boolean rendersColumnwise = false;
	
	public BytewiseStateRenderer(int stateSize, int cellSize, boolean rendersColumnwise) {
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
		
		for (int column = 0; column < numColumns; column++) {
			for (int row = 0; row < numRows; row++) {
				value = state.get(index);
				index++;
				
				if (value == 0) {
					contentByte.setColorFill(BaseColor.WHITE);
				} else {
					contentByte.setColorFill(activeColor);
				}
				
				if (rendersColumnwise) {
					positionX = (float)position.x + column * cellSize;
					positionY = (float)position.y - (row + 1) * cellSize;
				} else {
					positionX = (float)position.x + row * cellSize;
					positionY = (float)position.y - (column + 1) * cellSize;
				}
				
				contentByte.rectangle(positionX, positionY, cellSize, cellSize);
				contentByte.fillStroke();
			}
		}
	}
	
	private void createGrid(int stateSize) {
		int[] squares = { 1, 4, 9, 16, 25, 36, 49, 64 };
		
		for (int i = 0; i < squares.length; i++) {
			if (stateSize == squares[i]) {
				numColumns = i + 1;
				numRows = i + 1;
				return;
			}
		}
		
		int[] bestNumColumns = { 4, 3, 2, 5, 7 };
		
		for (int i = 0; i < bestNumColumns.length; i++) {
			if (stateSize % bestNumColumns[i] == 0) {
				numColumns = bestNumColumns[i];
				numRows = stateSize / numColumns;
				return;
			}
		}
		
		numColumns = stateSize;
		numRows = 1;
	}
	
}
