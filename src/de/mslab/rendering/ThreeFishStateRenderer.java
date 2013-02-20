package de.mslab.rendering;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;

import de.mslab.core.ByteArray;

public class ThreeFishStateRenderer extends StateRenderer {
	
	private int numRows;
	private int numColumns;
	private int aspectRatio = 2;
	
	public ThreeFishStateRenderer(int stateSize, int cellSize) {
		super();
		createGrid(stateSize);
		setGrid(cellSize, numRows, numColumns);
	}
	
	public void setGrid(int cellSize, int numRows, int numColumns) {
		this.cellSize = cellSize;
		this.numRows = numRows;
		this.numColumns = numColumns;
		bounds = new Point(aspectRatio * numColumns * cellSize, numRows * cellSize);
	}
	
	public Point getBounds() {
		return bounds;
	}
	
	public void renderState(PdfContentByte contentByte, ByteArray state, Point position, BaseColor activeColor) {
		long leftValue, rightValue;
		int index = 0;
		
		position = position.getLocation();
		position.x -= bounds.x / 2;
		position.y += bounds.y / 2;
		
		contentByte.setColorStroke(BaseColor.BLACK);
		contentByte.setColorFill(BaseColor.GRAY);
		contentByte.setLineWidth(0.1f);
		
		long[] array = state.readLongs();
		
		for (int row = 0; row < numRows; row++) {
			for (int column = 0; column < numColumns; column += 2) {
				leftValue = array[index++];
				rightValue = array[index++];
				
				if (leftValue == 0 && rightValue == 0) {
					contentByte.setColorFill(BaseColor.WHITE);
				} else {
					contentByte.setColorFill(activeColor);
				}
				
				contentByte.rectangle(
					(float)position.x + column * aspectRatio * cellSize, 
					(float)position.y - (row + 1) * cellSize, 
					aspectRatio * cellSize, 
					cellSize
				);

				contentByte.rectangle(
					(float)position.x + (column + 1) * aspectRatio * cellSize, 
					(float)position.y - (row + 1) * cellSize, 
					aspectRatio * cellSize, 
					cellSize
				);
				
				contentByte.fillStroke();
			}
		}
		
		contentByte.stroke();
	}
	
	private void createGrid(int stateSize) {
		int numBytesPerWord = Long.SIZE / Byte.SIZE;
		
		if (stateSize == 256 / Byte.SIZE) {
			numColumns = 4;
		} else {
			numColumns = 8;
		}
		
		numRows = stateSize / (numBytesPerWord * numColumns);
	}
	
}
