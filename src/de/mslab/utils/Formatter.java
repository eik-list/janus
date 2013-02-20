package de.mslab.utils;

import de.mslab.core.ByteArray;

/**
 * Offers utility methods to format integer, short, long or byte values and arrays 
 * and create a proper bitwise or hexadecimal string representation, and to print 
 * them properly on the console.
 */
public class Formatter {
	
	/**
	 * Prints each long value in the given array as a 64-bit-string. 
	 */
	public static String longArrayToBitStrings(long[] words) {
		String result = "";
		
		for (int i = 0; i < words.length; i++) {
			result += longToBitString(words[i]);
			
			if (i + 1 < words.length) {
				result += "\n";
			}
		}
		
		return result;
	}
	
	/**
	 * Prints one long value as a 64-bit-string. 
	 */
	public static String longToBitString(long word) {
		String s = Long.toBinaryString(word);
		String result = "";
		
		while(s.length() < 64) {
			s = "0" + s;
		}
		
		for (int i = 0; i < 64; i++) {
			result += s.charAt(i);
			
			if ((i - 7) % 8 == 0) {
				result += " ";
			} else if ((i - 7) % 4 == 0) {
				result += "";
			}
		}
		
		return result;
	}
	
	/**
	 * Prints each long value in the given array as a 16-characters string which represents its 64-bit hex value.  
	 */
	public static String longArrayToHexStrings(long[] words) {
		String result = "";
		
		for (int i = 0; i < words.length; i++) {
			result += longToHexString(words[i]) + " ";
		}
		
		return result;
	}
	
	/**
	 * Prints one long value as a 16-characters string which represents its 64-bit hex value.  
	 */
	public static String longToHexString(long word) {
		String s = Long.toHexString(word);
		String result = "";

		while(s.length() < 16) {
			s = "0" + s;
		}
		
		for (int i = 0; i < 16; i++) {
			result += s.charAt(i);
			
			if ((i - 7) % 8 == 0) {
				result += "";
			}
		}
		
		return "0x" + result + "L";
	}

	/**
	 * Creates a bit string representation of the byte array.
	 */
	public static String byteArrayToBitString(byte[] array) {
		if (array == null) {
			return "";
		}
		
		String output = "";
		String v = "";
		
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				if (i % 16 == 0) {
					output += "\n";
				} else if (i % 4 == 0) {
					output += ",";
				} else {
					output += ",";
				}
			}
			
			v = Integer.toBinaryString(array[i]);
			
			if (v.length() > 8) {
				v = v.substring(v.length() - 8);
			}
			
			while(v.length() < 8) {
				v = "0" + v;
			}
			
			output += v;
		}
		
		return output;
	}

	/**
	 * Prints each value in the given array as a 2-characters string which represents its 8-bit hex value.  
	 */
	public static String byteArrayToHexStrings(byte[] words) {
		return byteArrayToHexStrings(words, ",", 16);
	}
	
	/**
	 * Prints each value in the given array as a 2-characters string which represents its 8-bit hex value.  
	 */
	public static String byteArrayToHexStrings(short[] words) {
		return byteArrayToHexStrings(words, ",", 16);
	}
	
	/**
	 * Prints each value in the given array as a 2-characters string which represents its 8-bit hex value.  
	 */
	public static String byteArrayToHexStrings(int[] words) {
		return byteArrayToHexStrings(words, ",", 16);
	}
	
	/**
	 * Prints each value in the given array as a 2-characters string which represents its 8-bit hex value.
	 * @param delimiter A delimiter which is inserted after each value.
	 * @param rowlength A positive integer which implies how many words are printed in the string before one 
	 * line break is inserted.
	 */
	public static String byteArrayToHexStrings(short[] words, String delimiter, int rowlength) {
		String result = "";
		
		for (int i = 0; i < words.length; i++) {
			result += byteToHexString(words[i]);
			
			if (i + 1 < words.length) {
				result += delimiter;
			}
			
			if ((i + 1) % rowlength == 0) {
				result += "\n";
			}
		}
		
		return result;
	}
	
	/**
	 * {@link Formatter#byteArrayToHexStrings(short[], String, int)}
	 */
	public static String byteArrayToHexStrings(byte[] words, String delimiter, int rowlength) {
		String result = "";
		
		for (int i = 0; i < words.length; i++) {
			result += byteToHexString(words[i]);
			
			if (i + 1 < words.length) {
				result += delimiter;
			}
			
			if ((i + 1) % rowlength == 0) {
				result += "\n";
			}
		}
		
		return result;
	}
	
	/**
	 * {@link Formatter#byteArrayToHexStrings(short[], String, int)}
	 */
	public static String byteArrayToHexStrings(int[] words, String delimiter, int rowlength) {
		String result = "";
		
		for (int i = 0; i < words.length; i++) {
			result += byteToHexString(words[i]);
			
			if (i + 1 < words.length) {
				result += delimiter;
			}
			
			if ((i + 1) % rowlength == 0) {
				result += "\n";
			}
		}
		
		return result;
	}
	
	/**
	 * Prints a value in the given array as a 2-characters string which represents its 8-bit hex value.
	 */
	public static String byteToHexString(short value) {
		String s = Long.toHexString((long)value);

		if (s.length() > 2) {
			s = s.substring(s.length() - 2);
		}
		
		while(s.length() < 2) {
			s = "0" + s;
		}
		
		return "0x" + s;
	}
	
	/**
	 * {@link Formatter#byteToHexString(short)}
	 */
	public static String byteToHexString(int value) {
		String s = Long.toHexString((long)value);
		
		while(s.length() < 2) {
			s = "0" + s;
		}
		
		if (s.length() > 2) {
			s = s.substring(s.length() - 2);
		}
		
		return "0x" + s;
	}
	
	public static String intToHexString(int value) {
		String s = Long.toHexString((long)value);
		
		if (s.length() > 8) {
			s = s.substring(s.length() - 8);
		}
		
		while(s.length() < 8) {
			s = "0" + s;
		}
		
		return "0x" + s;
	}
	
	/**
	 * Prints each value in the given array as a number, delimited by a <code>,</code>
	 * (comma) and a line break after 16 values each.
	 */
	public static String intArrayToString(int[] array) {
		String result = "";
		
		for (int i = 0; i < array.length; i++) {
			result += array[i]; 
			
			if (i + 1 < array.length) {
				result += ",";
				
				if ((i + 1) % 16 == 0) {
					result += "\n";
				}
			}
		}
		
		return "[" + result + "]";
	}
	
	/**
	 * Prints each value in the given array as a number, delimited by a <code>,</code>
	 * (comma) and a line break after 16 values each.
	 */
	public static String intArrayToBitString(int[] array, int numBits) {
		String result = "";
		String s;
		
		for (int i = 0; i < array.length; i++) {
			s = Integer.toBinaryString(array[i]);
			
			while(s.length() < numBits) {
				s = "0" + s;
			}
			
			result += s + "\n";
		}
		
		return result;
	}
	
	/**
	 * Prints each value in the given array as a number, delimited by a <code>,</code>
	 * (comma) and a line break after 16 values each.
	 */
	public static String intArrayToHexString(int[] array) {
		String result = "";
		String s;
		
		for (int i = 0; i < array.length; i++) {
			if (i % 4 == 0 && i > 0) {
				result += "\n";
			}
			
			s = Integer.toHexString(array[i]);
			
			while(s.length() < 8) {
				s = "0" + s;
			}
			
			result += s + ",";
		}
		
		return result;
	}

	/**
	 * Prints each value in the given array as a number, delimited by a <code>,</code>
	 * (comma) and a line break after 16 values each.
	 */
	public static String intToBitString(int value, int numBits) {
		String s = Integer.toBinaryString(value);
		
		while(s.length() < numBits) {
			s = "0" + s;
		}
		
		return s;
	}
	
	/**
	 * Creates and returns an integer array, where every element i contains the ASCII value of the 
	 * character i of the messsage string.
	 */
	public static int[] stringToASCIICodes(String message) {
		int[] result = new int[message.length()];
		
		for (int i = 0; i < result.length; i++) {
			result[i] = message.charAt(i);
		}
		
		return result;
	}
	
	/**
	 * Creates a hex string from the given long array
	 */
	public static long[] hexStringToLongArray(String hexString) {
		int numCharsPerLong = Long.SIZE / 4;
		int resultLength = hexString.length() * (4 / Long.SIZE);
		long[] result = new long[resultLength];
		int segmentOffset = 0;
		
		for (int i = 0; i < resultLength; i++) {
			result[i] = Long.parseLong(hexString.substring(segmentOffset, segmentOffset + numCharsPerLong));
			segmentOffset += numCharsPerLong;
		}
		
		return result;
	}
	
	/**
	 * Creates and initializes a ByteArray with the byte representation of the values of the given string.
	 */
	public static ByteArray hexStringToByteArray(String hexString) {
		int numCharsPerByte = Byte.SIZE / 4;
		int resultLength = hexString.length() / 2;
		ByteArray result = new ByteArray(resultLength);
		int segmentOffset = 0;
		int value;
		
		for (int i = 0; i < resultLength; i++) {
			value = Integer.parseInt(hexString.substring(segmentOffset, segmentOffset + numCharsPerByte), 16) & 0xFF;
			result.set(i, value);
			segmentOffset += numCharsPerByte;
		}
		
		return result;
	}

}
