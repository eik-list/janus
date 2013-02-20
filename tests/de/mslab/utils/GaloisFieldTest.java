package de.mslab.utils;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GaloisFieldTest {
	
	private static GaloisField field;
	private static int[][] aesMatrix = {
		{ 2, 3, 1, 1 },
		{ 1, 2, 3, 1 },
		{ 1, 1, 2, 3 },
		{ 3, 1, 1, 2 }
	};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		field = new GaloisField(256, 283);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		field = null;
	}
	
	@Test
	public void testAESRow() {
		int[] input = { 0xdb, 0x13, 0x53, 0x45 };
		int[] shouldBe = { 0x8e, 0x4d, 0xa1, 0xbc };
		int value;
		
		for (int row = 0; row < 4; row++) {
			value = 0;
			
			for (int column = 0; column < 4; column++) {
				value = field.add(value, field.multiply(aesMatrix[row][column], input[column]));
			}
			
			assertEquals(shouldBe[row], value);
		}
	}
	
	@Test
	public void testAESState() {
		int[][] input = { 
			{ 0xd4, 0xbf, 0x5d, 0x30 }, 
			{ 0xe0, 0xb4, 0x52, 0xae }, 
			{ 0xb8, 0x41, 0x11, 0xf1 }, 
			{ 0x1e, 0x27, 0x98, 0xe5 }
		};
		int[][] shouldBe = { 
			{ 0x04, 0x66, 0x81, 0xe5 }, 
			{ 0xe0, 0xcb, 0x19, 0x9a }, 
			{ 0x48, 0xf8, 0xd3, 0x7a }, 
			{ 0x28, 0x06, 0x26, 0x4c }
		};
		int value;
		int product;
		
		for (int column = 0; column < input[0].length; column++) {
			for (int row = 0; row < input.length; row++) {
				value = 0;
				
				for (int c = 0; c < input.length; c++) {
					product = field.multiply(input[column][c], aesMatrix[row][c]);
					value = field.add(product, value);
				}
				
				assertEquals(shouldBe[column][row], value);
			}
		}
	}
	
	@Test
	public void testAdd() {
		assertEquals(217, field.add(182, 111));
	}
	
	@Test 
	public void testMultiply() {
		assertEquals(193, field.multiply(87, 131));
	}
	
}
