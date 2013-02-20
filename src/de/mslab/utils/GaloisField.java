package de.mslab.utils;

/**
 * Represents a galois field (GF) and defines modular addition and multiplication in it.
 * A field of GF(N) requires to specify a reduction polynomial and a field size N.
 * 
 */
public class GaloisField {
	
	/**
	 * A field size of GF(2^8) is the most often used field size for byte based system
	 */
	public static final int DEFAULT_FIELD_SIZE = 256;
	/**
	 * Represents a primitive polynomial 1 + X^2 + X^3 + X^4 + X^8
	 */
	public static final int DEFAULT_PRIMITIVE_POLYNOMIAL = 285;
	
	private int fieldSize;
	private int primitivePolynomial;
	
	/**
	 * Default constructor. Initializes a galois field GF(N) with the default field size 
	 * and the default polynomial. 
	 */
	public GaloisField() {
		this(DEFAULT_FIELD_SIZE, DEFAULT_PRIMITIVE_POLYNOMIAL);
	}
	
	/**
	 * Constructor. Initializes a galois field GF(N) with the given field size N
	 * and the default polynomial.
	 * @param fieldSize
	 */
	public GaloisField(int fieldSize) {
		this(fieldSize, DEFAULT_PRIMITIVE_POLYNOMIAL);
	}
	
	/**
	 * Constructor. Initializes a galois field GF(N) with the given field size N and a reduction polynomial. 
	 * @param fieldSize
	 * @param primitivePolynomial 
	 * @see GaloisField#getPrimitivePolynomial()
	 * @see GaloisField#getFieldSize()
	 */
	public GaloisField(int fieldSize, int primitivePolynomial) {
		this.fieldSize = fieldSize;
		this.primitivePolynomial = primitivePolynomial;
	}
	
	/**
	 * An integer which represents the field size N of the given galois field GF(N).
	 */
	public int getFieldSize() {
		return fieldSize;
	}
	
	/**
	 * An integer which represents a polynomial in bitwise fashion, 
	 * e. g. 285 stands for the polynomial 1 + X^2 + X^3 + X^4 + X^8.
	 */
	public int getPrimitivePolynomial() {
		return primitivePolynomial;
	}
	
	/**
	 * Adds two integers in the galois field, where addition in galois field 
	 * is XOR. 
	 * @param x Expected <code>x >= 0</code> and <code>x &lt; fieldSize</code>.
	 * @param y Expected <code>x >= 0</code> and <code>x &lt; fieldSize</code>.
	 * @return The XOR of x and y.
	 */
	public int add(int x, int y) {
		assert (x >= 0 && x < getFieldSize() && y >= 0 && y < getFieldSize());
		return x ^ y;
	}
	
	/**
	 * Multiplies two integers in the galois field. 
	 * @param x
	 * @param y
	 * @return The product of x and y.
	 */
	public int multiply(int x, int y) {
		assert (x >= 0 && x < getFieldSize() && y >= 0 && y < getFieldSize());
		/*
		 
		 
		01010111 * 10000011 	= 10101101111001
		=
		 10000011
		   10000011
		     10000011
		      10000011
		       10000011
		----------------
		010101101111001
		*/
		
		int minimum = x > y ? y : x; // min = 01010111
		int maximum = x > y ? x : y; // max = 10000011
		int product = 0;
		int index = 0;
		
		while (minimum != 0) {
			if ((minimum & 1) == 1) {
				product ^= maximum << index;
			}
			
			minimum >>= 1;
			index++;
		}
		
		return mod(product, primitivePolynomial);
	}
	
	/**
	 * Reduces two integers in the galois field x mod y. 
	 * @param x
	 * @param y
	 * @return int
	 */
	public int mod(int x, int y) {
		/*
		     10101101111001 mod 100011011 = 
			 100011011
			   100000011001
			---------------
			   100011011
			---------------
			   000011000001
		*/
		if (x == y) {
			return 0;
		}
		
		if (x > y) {
			int position = 1;
			
			while (x > y) {
				while (x > (y << position)) {
					position++;
				}
				
				position--;
				
				x ^= (y << position);
			}
		}
		
		if (Integer.highestOneBit(x) < Integer.highestOneBit(y)) {
			return x;
		} else {
			return x ^ y;
		}
	}
	
}
