package de.mslab.rendering;

import java.io.IOException;

import com.itextpdf.awt.geom.Point;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;

public class RenderUtil {
	
	public static final int DEFAULT_ADDITION_SIZE = 10;
	public static final int DEFAULT_XOR_RADIUS = 5;
	
	public static void renderAddition(PdfContentByte contentByte, Point position) {
		renderAddition(contentByte, (float)position.x, (float)position.y);
	}
	
	public static void renderAddition(PdfContentByte contentByte, float x, float y) {
		renderAddition(contentByte, x, y, DEFAULT_ADDITION_SIZE);
	}
	
	public static void renderAddition(PdfContentByte contentByte, float x, float y, int size) {
		int sizeHalf = size / 2;
		contentByte.setColorFill(BaseColor.WHITE);
		contentByte.rectangle(x - sizeHalf, y - sizeHalf, size, size);
		renderLine(contentByte, x - sizeHalf, y, x + sizeHalf, y);
		renderLine(contentByte, x, y - sizeHalf, x, y + sizeHalf);
	}
	
	public static void renderArrowDown(PdfContentByte contentByte, Point position, int height, int width) {
		Point first = position.getLocation();
		first.x += width / 2;
		first.y += height;

		Point second = position.getLocation();
		second.x -= width / 2;
		second.y += height;
		
		contentByte.moveTo((float)position.x, (float)position.y);
		contentByte.lineTo((float)first.x, (float)first.y);
		contentByte.lineTo((float)second.x, (float)second.y);
		contentByte.lineTo((float)position.x, (float)position.y);
		contentByte.fillStroke();
	}
	
	public static void renderLine(PdfContentByte contentByte, Point from, Point to) {
		renderLine(contentByte, (float)from.x, (float)from.y, (float)to.x, (float)to.y);
	}
	
	public static void renderLine(PdfContentByte contentByte, float fromX, float fromY, float toX, float toY) {
		contentByte.moveTo((float)fromX, (float)fromY);
		contentByte.lineTo((float)toX, (float)toY);
		contentByte.stroke();
	}
	
	public static void renderShapes(PdfContentByte contentByte) {
		contentByte.setColorStroke(BaseColor.BLACK);
		contentByte.setColorFill(BaseColor.GRAY);
		
		for (int row = 0; row < 4; row++) {
			for (int column = 0; column < 4; column++) {
				contentByte.rectangle(column * 10 + 50, row * 10 + 50, 10, 10);
				contentByte.fillStroke();
			}
		}
	}
	
	public static void renderText(PdfContentByte contentByte, Point position, String text) throws DocumentException, IOException {
		renderText(contentByte, position, text, 12);
	}
	
	public static void renderText(PdfContentByte contentByte, Point position, String text, int fontSize) throws DocumentException, IOException {
		renderText(contentByte, (float)position.x, (float)position.y, text, fontSize);
	}
	
	public static void renderText(PdfContentByte contentByte, float x, float y, String text, int fontSize) throws DocumentException, IOException {
		BaseFont font = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
		contentByte.setColorStroke(BaseColor.BLACK);
		contentByte.setColorFill(BaseColor.BLACK);
		contentByte.beginText();
		contentByte.setFontAndSize(font, fontSize);
		contentByte.setTextMatrix(x, y);
		contentByte.showText(text);
		contentByte.endText();
	}
	
	public static void renderXOR(PdfContentByte contentByte, Point position) {
		renderXOR(contentByte, (float)position.x, (float)position.y);
	}
	
	public static void renderXOR(PdfContentByte contentByte, float x, float y) {
		renderXOR(contentByte, x, y, DEFAULT_XOR_RADIUS);
	}
	
	public static void renderXOR(PdfContentByte contentByte, float x, float y, int radius) {
		contentByte.setColorFill(BaseColor.WHITE);
		contentByte.circle(x, y, radius);
		renderLine(contentByte, x - radius, y, x + radius, y);
		renderLine(contentByte, x, y - radius, x, y + radius);
	}
	
}
