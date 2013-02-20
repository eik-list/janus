package de.mslab.rendering;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;

import de.mslab.core.ByteArray;
import de.mslab.errors.InvalidArgumentError;

public class BitwiseStateRenderer extends StateRenderer {
	
	private int numRows;
	private int numColumns;
	
	public BitwiseStateRenderer(int stateSize, int cellSize) {
		super();
		createGrid(stateSize);
		setGrid(cellSize, numRows, numColumns);
	}
	
	public void setGrid(int cellSize, int numRows, int numColumns) {
		this.cellSize = cellSize;
		this.numRows = numRows;
		this.numColumns = numColumns;
		bounds = new Point(numColumns * cellSize, numRows * cellSize);
	}
	
	public Point getBounds() {
		return bounds;
	}
	
	public void renderState(PdfContentByte contentByte, ByteArray state, Point position, BaseColor activeColor) {
		short value;
		int index = 0;
		float bitSize = cellSize / (float)Byte.SIZE;
		
		position = position.getLocation();
		position.x -= bounds.x / 2;
		position.y += bounds.y / 2;
		
		contentByte.setColorStroke(BaseColor.BLACK);
		contentByte.setColorFill(BaseColor.GRAY);
		contentByte.setLineWidth(0.1f);
		
		for (int row = 1; row <= numRows; row++) {
			for (int column = 0; column < numColumns; column++) {
				value = state.get(index);
				index++;
				
				for (int bit = 0; bit < Byte.SIZE; ++bit) {
					if ((value & 0x80) == 0) {
						contentByte.setColorFill(BaseColor.WHITE);
					} else {
						contentByte.setColorFill(activeColor);
					}
					
					contentByte.rectangle(
						(float)position.x + column * cellSize + bit * bitSize, 
						(float)position.y - row * cellSize, 
						bitSize, cellSize
					);
					
					value <<= 1;
					contentByte.fillStroke();
				}
				
				contentByte.setLineWidth(1f);
				contentByte.rectangle(
					(float)position.x + column * cellSize, 
					(float)position.y - row * cellSize, 
					cellSize, cellSize
				);
				contentByte.stroke();
				contentByte.setLineWidth(0.1f);
			}
		}
		
		contentByte.setLineWidth(1f);
		contentByte.rectangle((float)position.x, (float)position.y - (float)bounds.y, (float)bounds.x, (float)bounds.y);
		contentByte.stroke();
		contentByte.setLineWidth(0.1f);
	}
	
	private void createGrid(int stateSize) {
		if (stateSize < 16) {
			numColumns = stateSize;
			numRows = 1;
		} else if (stateSize % 16 == 0) {
			numColumns = 16;
			numRows = stateSize / 16;
		} else {
			throw new InvalidArgumentError("State size should be a multiple of 16.");
		}
	}
	
}
