package de.mslab.ciphers;

import de.mslab.core.ByteArray;
import de.mslab.errors.NoKeyRequiredError;

/**
 * Base class of the permutations P and Q, used in the Groestl hash function. 
 * 
 */
public class GroestlPermutation extends AbstractRoundBasedBlockCipher {
	
	protected static final int[] INVERSE_SBOX = AES.INVERSE_SBOX;
	protected static final int[] SBOX = AES.SBOX;
	
	private static final int[] XTIMES_02 = { 
		0x00,0x02,0x04,0x06,0x08,0x0a,0x0c,0x0e,0x10,0x12,0x14,0x16,0x18,0x1a,0x1c,0x1e,
		0x20,0x22,0x24,0x26,0x28,0x2a,0x2c,0x2e,0x30,0x32,0x34,0x36,0x38,0x3a,0x3c,0x3e,
		0x40,0x42,0x44,0x46,0x48,0x4a,0x4c,0x4e,0x50,0x52,0x54,0x56,0x58,0x5a,0x5c,0x5e,
		0x60,0x62,0x64,0x66,0x68,0x6a,0x6c,0x6e,0x70,0x72,0x74,0x76,0x78,0x7a,0x7c,0x7e,
		0x80,0x82,0x84,0x86,0x88,0x8a,0x8c,0x8e,0x90,0x92,0x94,0x96,0x98,0x9a,0x9c,0x9e,
		0xa0,0xa2,0xa4,0xa6,0xa8,0xaa,0xac,0xae,0xb0,0xb2,0xb4,0xb6,0xb8,0xba,0xbc,0xbe,
		0xc0,0xc2,0xc4,0xc6,0xc8,0xca,0xcc,0xce,0xd0,0xd2,0xd4,0xd6,0xd8,0xda,0xdc,0xde,
		0xe0,0xe2,0xe4,0xe6,0xe8,0xea,0xec,0xee,0xf0,0xf2,0xf4,0xf6,0xf8,0xfa,0xfc,0xfe,
		0x1b,0x19,0x1f,0x1d,0x13,0x11,0x17,0x15,0x0b,0x09,0x0f,0x0d,0x03,0x01,0x07,0x05,
		0x3b,0x39,0x3f,0x3d,0x33,0x31,0x37,0x35,0x2b,0x29,0x2f,0x2d,0x23,0x21,0x27,0x25,
		0x5b,0x59,0x5f,0x5d,0x53,0x51,0x57,0x55,0x4b,0x49,0x4f,0x4d,0x43,0x41,0x47,0x45,
		0x7b,0x79,0x7f,0x7d,0x73,0x71,0x77,0x75,0x6b,0x69,0x6f,0x6d,0x63,0x61,0x67,0x65,
		0x9b,0x99,0x9f,0x9d,0x93,0x91,0x97,0x95,0x8b,0x89,0x8f,0x8d,0x83,0x81,0x87,0x85,
		0xbb,0xb9,0xbf,0xbd,0xb3,0xb1,0xb7,0xb5,0xab,0xa9,0xaf,0xad,0xa3,0xa1,0xa7,0xa5,
		0xdb,0xd9,0xdf,0xdd,0xd3,0xd1,0xd7,0xd5,0xcb,0xc9,0xcf,0xcd,0xc3,0xc1,0xc7,0xc5,
		0xfb,0xf9,0xff,0xfd,0xf3,0xf1,0xf7,0xf5,0xeb,0xe9,0xef,0xed,0xe3,0xe1,0xe7,0xe5
	};
	private static final int[] XTIMES_03 = { 
		0x00,0x03,0x06,0x05,0x0c,0x0f,0x0a,0x09,0x18,0x1b,0x1e,0x1d,0x14,0x17,0x12,0x11,
		0x30,0x33,0x36,0x35,0x3c,0x3f,0x3a,0x39,0x28,0x2b,0x2e,0x2d,0x24,0x27,0x22,0x21,
		0x60,0x63,0x66,0x65,0x6c,0x6f,0x6a,0x69,0x78,0x7b,0x7e,0x7d,0x74,0x77,0x72,0x71,
		0x50,0x53,0x56,0x55,0x5c,0x5f,0x5a,0x59,0x48,0x4b,0x4e,0x4d,0x44,0x47,0x42,0x41,
		0xc0,0xc3,0xc6,0xc5,0xcc,0xcf,0xca,0xc9,0xd8,0xdb,0xde,0xdd,0xd4,0xd7,0xd2,0xd1,
		0xf0,0xf3,0xf6,0xf5,0xfc,0xff,0xfa,0xf9,0xe8,0xeb,0xee,0xed,0xe4,0xe7,0xe2,0xe1,
		0xa0,0xa3,0xa6,0xa5,0xac,0xaf,0xaa,0xa9,0xb8,0xbb,0xbe,0xbd,0xb4,0xb7,0xb2,0xb1,
		0x90,0x93,0x96,0x95,0x9c,0x9f,0x9a,0x99,0x88,0x8b,0x8e,0x8d,0x84,0x87,0x82,0x81,
		0x9b,0x98,0x9d,0x9e,0x97,0x94,0x91,0x92,0x83,0x80,0x85,0x86,0x8f,0x8c,0x89,0x8a,
		0xab,0xa8,0xad,0xae,0xa7,0xa4,0xa1,0xa2,0xb3,0xb0,0xb5,0xb6,0xbf,0xbc,0xb9,0xba,
		0xfb,0xf8,0xfd,0xfe,0xf7,0xf4,0xf1,0xf2,0xe3,0xe0,0xe5,0xe6,0xef,0xec,0xe9,0xea,
		0xcb,0xc8,0xcd,0xce,0xc7,0xc4,0xc1,0xc2,0xd3,0xd0,0xd5,0xd6,0xdf,0xdc,0xd9,0xda,
		0x5b,0x58,0x5d,0x5e,0x57,0x54,0x51,0x52,0x43,0x40,0x45,0x46,0x4f,0x4c,0x49,0x4a,
		0x6b,0x68,0x6d,0x6e,0x67,0x64,0x61,0x62,0x73,0x70,0x75,0x76,0x7f,0x7c,0x79,0x7a,
		0x3b,0x38,0x3d,0x3e,0x37,0x34,0x31,0x32,0x23,0x20,0x25,0x26,0x2f,0x2c,0x29,0x2a,
		0x0b,0x08,0x0d,0x0e,0x07,0x04,0x01,0x02,0x13,0x10,0x15,0x16,0x1f,0x1c,0x19,0x1a
	};
	private static final int[] XTIMES_04 = { 
		0x00,0x04,0x08,0x0c,0x10,0x14,0x18,0x1c,0x20,0x24,0x28,0x2c,0x30,0x34,0x38,0x3c,
		0x40,0x44,0x48,0x4c,0x50,0x54,0x58,0x5c,0x60,0x64,0x68,0x6c,0x70,0x74,0x78,0x7c,
		0x80,0x84,0x88,0x8c,0x90,0x94,0x98,0x9c,0xa0,0xa4,0xa8,0xac,0xb0,0xb4,0xb8,0xbc,
		0xc0,0xc4,0xc8,0xcc,0xd0,0xd4,0xd8,0xdc,0xe0,0xe4,0xe8,0xec,0xf0,0xf4,0xf8,0xfc,
		0x1b,0x1f,0x13,0x17,0x0b,0x0f,0x03,0x07,0x3b,0x3f,0x33,0x37,0x2b,0x2f,0x23,0x27,
		0x5b,0x5f,0x53,0x57,0x4b,0x4f,0x43,0x47,0x7b,0x7f,0x73,0x77,0x6b,0x6f,0x63,0x67,
		0x9b,0x9f,0x93,0x97,0x8b,0x8f,0x83,0x87,0xbb,0xbf,0xb3,0xb7,0xab,0xaf,0xa3,0xa7,
		0xdb,0xdf,0xd3,0xd7,0xcb,0xcf,0xc3,0xc7,0xfb,0xff,0xf3,0xf7,0xeb,0xef,0xe3,0xe7,
		0x36,0x32,0x3e,0x3a,0x26,0x22,0x2e,0x2a,0x16,0x12,0x1e,0x1a,0x06,0x02,0x0e,0x0a,
		0x76,0x72,0x7e,0x7a,0x66,0x62,0x6e,0x6a,0x56,0x52,0x5e,0x5a,0x46,0x42,0x4e,0x4a,
		0xb6,0xb2,0xbe,0xba,0xa6,0xa2,0xae,0xaa,0x96,0x92,0x9e,0x9a,0x86,0x82,0x8e,0x8a,
		0xf6,0xf2,0xfe,0xfa,0xe6,0xe2,0xee,0xea,0xd6,0xd2,0xde,0xda,0xc6,0xc2,0xce,0xca,
		0x2d,0x29,0x25,0x21,0x3d,0x39,0x35,0x31,0x0d,0x09,0x05,0x01,0x1d,0x19,0x15,0x11,
		0x6d,0x69,0x65,0x61,0x7d,0x79,0x75,0x71,0x4d,0x49,0x45,0x41,0x5d,0x59,0x55,0x51,
		0xad,0xa9,0xa5,0xa1,0xbd,0xb9,0xb5,0xb1,0x8d,0x89,0x85,0x81,0x9d,0x99,0x95,0x91,
		0xed,0xe9,0xe5,0xe1,0xfd,0xf9,0xf5,0xf1,0xcd,0xc9,0xc5,0xc1,0xdd,0xd9,0xd5,0xd1
	};
	private static final int[] XTIMES_05 = { 
		0x00,0x05,0x0a,0x0f,0x14,0x11,0x1e,0x1b,0x28,0x2d,0x22,0x27,0x3c,0x39,0x36,0x33,
		0x50,0x55,0x5a,0x5f,0x44,0x41,0x4e,0x4b,0x78,0x7d,0x72,0x77,0x6c,0x69,0x66,0x63,
		0xa0,0xa5,0xaa,0xaf,0xb4,0xb1,0xbe,0xbb,0x88,0x8d,0x82,0x87,0x9c,0x99,0x96,0x93,
		0xf0,0xf5,0xfa,0xff,0xe4,0xe1,0xee,0xeb,0xd8,0xdd,0xd2,0xd7,0xcc,0xc9,0xc6,0xc3,
		0x5b,0x5e,0x51,0x54,0x4f,0x4a,0x45,0x40,0x73,0x76,0x79,0x7c,0x67,0x62,0x6d,0x68,
		0x0b,0x0e,0x01,0x04,0x1f,0x1a,0x15,0x10,0x23,0x26,0x29,0x2c,0x37,0x32,0x3d,0x38,
		0xfb,0xfe,0xf1,0xf4,0xef,0xea,0xe5,0xe0,0xd3,0xd6,0xd9,0xdc,0xc7,0xc2,0xcd,0xc8,
		0xab,0xae,0xa1,0xa4,0xbf,0xba,0xb5,0xb0,0x83,0x86,0x89,0x8c,0x97,0x92,0x9d,0x98,
		0xb6,0xb3,0xbc,0xb9,0xa2,0xa7,0xa8,0xad,0x9e,0x9b,0x94,0x91,0x8a,0x8f,0x80,0x85,
		0xe6,0xe3,0xec,0xe9,0xf2,0xf7,0xf8,0xfd,0xce,0xcb,0xc4,0xc1,0xda,0xdf,0xd0,0xd5,
		0x16,0x13,0x1c,0x19,0x02,0x07,0x08,0x0d,0x3e,0x3b,0x34,0x31,0x2a,0x2f,0x20,0x25,
		0x46,0x43,0x4c,0x49,0x52,0x57,0x58,0x5d,0x6e,0x6b,0x64,0x61,0x7a,0x7f,0x70,0x75,
		0xed,0xe8,0xe7,0xe2,0xf9,0xfc,0xf3,0xf6,0xc5,0xc0,0xcf,0xca,0xd1,0xd4,0xdb,0xde,
		0xbd,0xb8,0xb7,0xb2,0xa9,0xac,0xa3,0xa6,0x95,0x90,0x9f,0x9a,0x81,0x84,0x8b,0x8e,
		0x4d,0x48,0x47,0x42,0x59,0x5c,0x53,0x56,0x65,0x60,0x6f,0x6a,0x71,0x74,0x7b,0x7e,
		0x1d,0x18,0x17,0x12,0x09,0x0c,0x03,0x06,0x35,0x30,0x3f,0x3a,0x21,0x24,0x2b,0x2e
	};
	private static final int[] XTIMES_07 = { 
		0x00,0x07,0x0e,0x09,0x1c,0x1b,0x12,0x15,0x38,0x3f,0x36,0x31,0x24,0x23,0x2a,0x2d,
		0x70,0x77,0x7e,0x79,0x6c,0x6b,0x62,0x65,0x48,0x4f,0x46,0x41,0x54,0x53,0x5a,0x5d,
		0xe0,0xe7,0xee,0xe9,0xfc,0xfb,0xf2,0xf5,0xd8,0xdf,0xd6,0xd1,0xc4,0xc3,0xca,0xcd,
		0x90,0x97,0x9e,0x99,0x8c,0x8b,0x82,0x85,0xa8,0xaf,0xa6,0xa1,0xb4,0xb3,0xba,0xbd,
		0xdb,0xdc,0xd5,0xd2,0xc7,0xc0,0xc9,0xce,0xe3,0xe4,0xed,0xea,0xff,0xf8,0xf1,0xf6,
		0xab,0xac,0xa5,0xa2,0xb7,0xb0,0xb9,0xbe,0x93,0x94,0x9d,0x9a,0x8f,0x88,0x81,0x86,
		0x3b,0x3c,0x35,0x32,0x27,0x20,0x29,0x2e,0x03,0x04,0x0d,0x0a,0x1f,0x18,0x11,0x16,
		0x4b,0x4c,0x45,0x42,0x57,0x50,0x59,0x5e,0x73,0x74,0x7d,0x7a,0x6f,0x68,0x61,0x66,
		0xad,0xaa,0xa3,0xa4,0xb1,0xb6,0xbf,0xb8,0x95,0x92,0x9b,0x9c,0x89,0x8e,0x87,0x80,
		0xdd,0xda,0xd3,0xd4,0xc1,0xc6,0xcf,0xc8,0xe5,0xe2,0xeb,0xec,0xf9,0xfe,0xf7,0xf0,
		0x4d,0x4a,0x43,0x44,0x51,0x56,0x5f,0x58,0x75,0x72,0x7b,0x7c,0x69,0x6e,0x67,0x60,
		0x3d,0x3a,0x33,0x34,0x21,0x26,0x2f,0x28,0x05,0x02,0x0b,0x0c,0x19,0x1e,0x17,0x10,
		0x76,0x71,0x78,0x7f,0x6a,0x6d,0x64,0x63,0x4e,0x49,0x40,0x47,0x52,0x55,0x5c,0x5b,
		0x06,0x01,0x08,0x0f,0x1a,0x1d,0x14,0x13,0x3e,0x39,0x30,0x37,0x22,0x25,0x2c,0x2b,
		0x96,0x91,0x98,0x9f,0x8a,0x8d,0x84,0x83,0xae,0xa9,0xa0,0xa7,0xb2,0xb5,0xbc,0xbb,
		0xe6,0xe1,0xe8,0xef,0xfa,0xfd,0xf4,0xf3,0xde,0xd9,0xd0,0xd7,0xc2,0xc5,0xcc,0xcb
	};
	private static final int[] XTIMES_99 = { 
		0x00,0x99,0x29,0xb0,0x52,0xcb,0x7b,0xe2,0xa4,0x3d,0x8d,0x14,0xf6,0x6f,0xdf,0x46,
		0x53,0xca,0x7a,0xe3,0x01,0x98,0x28,0xb1,0xf7,0x6e,0xde,0x47,0xa5,0x3c,0x8c,0x15,
		0xa6,0x3f,0x8f,0x16,0xf4,0x6d,0xdd,0x44,0x02,0x9b,0x2b,0xb2,0x50,0xc9,0x79,0xe0,
		0xf5,0x6c,0xdc,0x45,0xa7,0x3e,0x8e,0x17,0x51,0xc8,0x78,0xe1,0x03,0x9a,0x2a,0xb3,
		0x57,0xce,0x7e,0xe7,0x05,0x9c,0x2c,0xb5,0xf3,0x6a,0xda,0x43,0xa1,0x38,0x88,0x11,
		0x04,0x9d,0x2d,0xb4,0x56,0xcf,0x7f,0xe6,0xa0,0x39,0x89,0x10,0xf2,0x6b,0xdb,0x42,
		0xf1,0x68,0xd8,0x41,0xa3,0x3a,0x8a,0x13,0x55,0xcc,0x7c,0xe5,0x07,0x9e,0x2e,0xb7,
		0xa2,0x3b,0x8b,0x12,0xf0,0x69,0xd9,0x40,0x06,0x9f,0x2f,0xb6,0x54,0xcd,0x7d,0xe4,
		0xae,0x37,0x87,0x1e,0xfc,0x65,0xd5,0x4c,0x0a,0x93,0x23,0xba,0x58,0xc1,0x71,0xe8,
		0xfd,0x64,0xd4,0x4d,0xaf,0x36,0x86,0x1f,0x59,0xc0,0x70,0xe9,0x0b,0x92,0x22,0xbb,
		0x08,0x91,0x21,0xb8,0x5a,0xc3,0x73,0xea,0xac,0x35,0x85,0x1c,0xfe,0x67,0xd7,0x4e,
		0x5b,0xc2,0x72,0xeb,0x09,0x90,0x20,0xb9,0xff,0x66,0xd6,0x4f,0xad,0x34,0x84,0x1d,
		0xf9,0x60,0xd0,0x49,0xab,0x32,0x82,0x1b,0x5d,0xc4,0x74,0xed,0x0f,0x96,0x26,0xbf,
		0xaa,0x33,0x83,0x1a,0xf8,0x61,0xd1,0x48,0x0e,0x97,0x27,0xbe,0x5c,0xc5,0x75,0xec,
		0x5f,0xc6,0x76,0xef,0x0d,0x94,0x24,0xbd,0xfb,0x62,0xd2,0x4b,0xa9,0x30,0x80,0x19,
		0x0c,0x95,0x25,0xbc,0x5e,0xc7,0x77,0xee,0xa8,0x31,0x81,0x18,0xfa,0x63,0xd3,0x4a
	};
	private static final int[] XTIMES_C4 = { 
		0x00,0xc4,0x93,0x57,0x3d,0xf9,0xae,0x6a,0x7a,0xbe,0xe9,0x2d,0x47,0x83,0xd4,0x10,
		0xf4,0x30,0x67,0xa3,0xc9,0x0d,0x5a,0x9e,0x8e,0x4a,0x1d,0xd9,0xb3,0x77,0x20,0xe4,
		0xf3,0x37,0x60,0xa4,0xce,0x0a,0x5d,0x99,0x89,0x4d,0x1a,0xde,0xb4,0x70,0x27,0xe3,
		0x07,0xc3,0x94,0x50,0x3a,0xfe,0xa9,0x6d,0x7d,0xb9,0xee,0x2a,0x40,0x84,0xd3,0x17,
		0xfd,0x39,0x6e,0xaa,0xc0,0x04,0x53,0x97,0x87,0x43,0x14,0xd0,0xba,0x7e,0x29,0xed,
		0x09,0xcd,0x9a,0x5e,0x34,0xf0,0xa7,0x63,0x73,0xb7,0xe0,0x24,0x4e,0x8a,0xdd,0x19,
		0x0e,0xca,0x9d,0x59,0x33,0xf7,0xa0,0x64,0x74,0xb0,0xe7,0x23,0x49,0x8d,0xda,0x1e,
		0xfa,0x3e,0x69,0xad,0xc7,0x03,0x54,0x90,0x80,0x44,0x13,0xd7,0xbd,0x79,0x2e,0xea,
		0xe1,0x25,0x72,0xb6,0xdc,0x18,0x4f,0x8b,0x9b,0x5f,0x08,0xcc,0xa6,0x62,0x35,0xf1,
		0x15,0xd1,0x86,0x42,0x28,0xec,0xbb,0x7f,0x6f,0xab,0xfc,0x38,0x52,0x96,0xc1,0x05,
		0x12,0xd6,0x81,0x45,0x2f,0xeb,0xbc,0x78,0x68,0xac,0xfb,0x3f,0x55,0x91,0xc6,0x02,
		0xe6,0x22,0x75,0xb1,0xdb,0x1f,0x48,0x8c,0x9c,0x58,0x0f,0xcb,0xa1,0x65,0x32,0xf6,
		0x1c,0xd8,0x8f,0x4b,0x21,0xe5,0xb2,0x76,0x66,0xa2,0xf5,0x31,0x5b,0x9f,0xc8,0x0c,
		0xe8,0x2c,0x7b,0xbf,0xd5,0x11,0x46,0x82,0x92,0x56,0x01,0xc5,0xaf,0x6b,0x3c,0xf8,
		0xef,0x2b,0x7c,0xb8,0xd2,0x16,0x41,0x85,0x95,0x51,0x06,0xc2,0xa8,0x6c,0x3b,0xff,
		0x1b,0xdf,0x88,0x4c,0x26,0xe2,0xb5,0x71,0x61,0xa5,0xf2,0x36,0x5c,0x98,0xcf,0x0b
	};
	private static final int[] XTIMES_46 = { 
		0x00,0x46,0x8c,0xca,0x03,0x45,0x8f,0xc9,0x06,0x40,0x8a,0xcc,0x05,0x43,0x89,0xcf,
		0x0c,0x4a,0x80,0xc6,0x0f,0x49,0x83,0xc5,0x0a,0x4c,0x86,0xc0,0x09,0x4f,0x85,0xc3,
		0x18,0x5e,0x94,0xd2,0x1b,0x5d,0x97,0xd1,0x1e,0x58,0x92,0xd4,0x1d,0x5b,0x91,0xd7,
		0x14,0x52,0x98,0xde,0x17,0x51,0x9b,0xdd,0x12,0x54,0x9e,0xd8,0x11,0x57,0x9d,0xdb,
		0x30,0x76,0xbc,0xfa,0x33,0x75,0xbf,0xf9,0x36,0x70,0xba,0xfc,0x35,0x73,0xb9,0xff,
		0x3c,0x7a,0xb0,0xf6,0x3f,0x79,0xb3,0xf5,0x3a,0x7c,0xb6,0xf0,0x39,0x7f,0xb5,0xf3,
		0x28,0x6e,0xa4,0xe2,0x2b,0x6d,0xa7,0xe1,0x2e,0x68,0xa2,0xe4,0x2d,0x6b,0xa1,0xe7,
		0x24,0x62,0xa8,0xee,0x27,0x61,0xab,0xed,0x22,0x64,0xae,0xe8,0x21,0x67,0xad,0xeb,
		0x60,0x26,0xec,0xaa,0x63,0x25,0xef,0xa9,0x66,0x20,0xea,0xac,0x65,0x23,0xe9,0xaf,
		0x6c,0x2a,0xe0,0xa6,0x6f,0x29,0xe3,0xa5,0x6a,0x2c,0xe6,0xa0,0x69,0x2f,0xe5,0xa3,
		0x78,0x3e,0xf4,0xb2,0x7b,0x3d,0xf7,0xb1,0x7e,0x38,0xf2,0xb4,0x7d,0x3b,0xf1,0xb7,
		0x74,0x32,0xf8,0xbe,0x77,0x31,0xfb,0xbd,0x72,0x34,0xfe,0xb8,0x71,0x37,0xfd,0xbb,
		0x50,0x16,0xdc,0x9a,0x53,0x15,0xdf,0x99,0x56,0x10,0xda,0x9c,0x55,0x13,0xd9,0x9f,
		0x5c,0x1a,0xd0,0x96,0x5f,0x19,0xd3,0x95,0x5a,0x1c,0xd6,0x90,0x59,0x1f,0xd5,0x93,
		0x48,0x0e,0xc4,0x82,0x4b,0x0d,0xc7,0x81,0x4e,0x08,0xc2,0x84,0x4d,0x0b,0xc1,0x87,
		0x44,0x02,0xc8,0x8e,0x47,0x01,0xcb,0x8d,0x42,0x04,0xce,0x88,0x41,0x07,0xcd,0x8b
	};
	private static final int[] XTIMES_36 = { 
		0x00,0x36,0x6c,0x5a,0xd8,0xee,0xb4,0x82,0xab,0x9d,0xc7,0xf1,0x73,0x45,0x1f,0x29,
		0x4d,0x7b,0x21,0x17,0x95,0xa3,0xf9,0xcf,0xe6,0xd0,0x8a,0xbc,0x3e,0x08,0x52,0x64,
		0x9a,0xac,0xf6,0xc0,0x42,0x74,0x2e,0x18,0x31,0x07,0x5d,0x6b,0xe9,0xdf,0x85,0xb3,
		0xd7,0xe1,0xbb,0x8d,0x0f,0x39,0x63,0x55,0x7c,0x4a,0x10,0x26,0xa4,0x92,0xc8,0xfe,
		0x2f,0x19,0x43,0x75,0xf7,0xc1,0x9b,0xad,0x84,0xb2,0xe8,0xde,0x5c,0x6a,0x30,0x06,
		0x62,0x54,0x0e,0x38,0xba,0x8c,0xd6,0xe0,0xc9,0xff,0xa5,0x93,0x11,0x27,0x7d,0x4b,
		0xb5,0x83,0xd9,0xef,0x6d,0x5b,0x01,0x37,0x1e,0x28,0x72,0x44,0xc6,0xf0,0xaa,0x9c,
		0xf8,0xce,0x94,0xa2,0x20,0x16,0x4c,0x7a,0x53,0x65,0x3f,0x09,0x8b,0xbd,0xe7,0xd1,
		0x5e,0x68,0x32,0x04,0x86,0xb0,0xea,0xdc,0xf5,0xc3,0x99,0xaf,0x2d,0x1b,0x41,0x77,
		0x13,0x25,0x7f,0x49,0xcb,0xfd,0xa7,0x91,0xb8,0x8e,0xd4,0xe2,0x60,0x56,0x0c,0x3a,
		0xc4,0xf2,0xa8,0x9e,0x1c,0x2a,0x70,0x46,0x6f,0x59,0x03,0x35,0xb7,0x81,0xdb,0xed,
		0x89,0xbf,0xe5,0xd3,0x51,0x67,0x3d,0x0b,0x22,0x14,0x4e,0x78,0xfa,0xcc,0x96,0xa0,
		0x71,0x47,0x1d,0x2b,0xa9,0x9f,0xc5,0xf3,0xda,0xec,0xb6,0x80,0x02,0x34,0x6e,0x58,
		0x3c,0x0a,0x50,0x66,0xe4,0xd2,0x88,0xbe,0x97,0xa1,0xfb,0xcd,0x4f,0x79,0x23,0x15,
		0xeb,0xdd,0x87,0xb1,0x33,0x05,0x5f,0x69,0x40,0x76,0x2c,0x1a,0x98,0xae,0xf4,0xc2,
		0xa6,0x90,0xca,0xfc,0x7e,0x48,0x12,0x24,0x0d,0x3b,0x61,0x57,0xd5,0xe3,0xb9,0x8f
	};
	private static final int[] XTIMES_1E = { 
		0x00,0x1e,0x3c,0x22,0x78,0x66,0x44,0x5a,0xf0,0xee,0xcc,0xd2,0x88,0x96,0xb4,0xaa,
		0xfb,0xe5,0xc7,0xd9,0x83,0x9d,0xbf,0xa1,0x0b,0x15,0x37,0x29,0x73,0x6d,0x4f,0x51,
		0xed,0xf3,0xd1,0xcf,0x95,0x8b,0xa9,0xb7,0x1d,0x03,0x21,0x3f,0x65,0x7b,0x59,0x47,
		0x16,0x08,0x2a,0x34,0x6e,0x70,0x52,0x4c,0xe6,0xf8,0xda,0xc4,0x9e,0x80,0xa2,0xbc,
		0xc1,0xdf,0xfd,0xe3,0xb9,0xa7,0x85,0x9b,0x31,0x2f,0x0d,0x13,0x49,0x57,0x75,0x6b,
		0x3a,0x24,0x06,0x18,0x42,0x5c,0x7e,0x60,0xca,0xd4,0xf6,0xe8,0xb2,0xac,0x8e,0x90,
		0x2c,0x32,0x10,0x0e,0x54,0x4a,0x68,0x76,0xdc,0xc2,0xe0,0xfe,0xa4,0xba,0x98,0x86,
		0xd7,0xc9,0xeb,0xf5,0xaf,0xb1,0x93,0x8d,0x27,0x39,0x1b,0x05,0x5f,0x41,0x63,0x7d,
		0x99,0x87,0xa5,0xbb,0xe1,0xff,0xdd,0xc3,0x69,0x77,0x55,0x4b,0x11,0x0f,0x2d,0x33,
		0x62,0x7c,0x5e,0x40,0x1a,0x04,0x26,0x38,0x92,0x8c,0xae,0xb0,0xea,0xf4,0xd6,0xc8,
		0x74,0x6a,0x48,0x56,0x0c,0x12,0x30,0x2e,0x84,0x9a,0xb8,0xa6,0xfc,0xe2,0xc0,0xde,
		0x8f,0x91,0xb3,0xad,0xf7,0xe9,0xcb,0xd5,0x7f,0x61,0x43,0x5d,0x07,0x19,0x3b,0x25,
		0x58,0x46,0x64,0x7a,0x20,0x3e,0x1c,0x02,0xa8,0xb6,0x94,0x8a,0xd0,0xce,0xec,0xf2,
		0xa3,0xbd,0x9f,0x81,0xdb,0xc5,0xe7,0xf9,0x53,0x4d,0x6f,0x71,0x2b,0x35,0x17,0x09,
		0xb5,0xab,0x89,0x97,0xcd,0xd3,0xf1,0xef,0x45,0x5b,0x79,0x67,0x3d,0x23,0x01,0x1f,
		0x4e,0x50,0x72,0x6c,0x36,0x28,0x0a,0x14,0xbe,0xa0,0x82,0x9c,0xc6,0xd8,0xfa,0xe4
	};
	private static final int[] XTIMES_D4 = { 
		0x00,0xd4,0xb3,0x67,0x7d,0xa9,0xce,0x1a,0xfa,0x2e,0x49,0x9d,0x87,0x53,0x34,0xe0,
		0xef,0x3b,0x5c,0x88,0x92,0x46,0x21,0xf5,0x15,0xc1,0xa6,0x72,0x68,0xbc,0xdb,0x0f,
		0xc5,0x11,0x76,0xa2,0xb8,0x6c,0x0b,0xdf,0x3f,0xeb,0x8c,0x58,0x42,0x96,0xf1,0x25,
		0x2a,0xfe,0x99,0x4d,0x57,0x83,0xe4,0x30,0xd0,0x04,0x63,0xb7,0xad,0x79,0x1e,0xca,
		0x91,0x45,0x22,0xf6,0xec,0x38,0x5f,0x8b,0x6b,0xbf,0xd8,0x0c,0x16,0xc2,0xa5,0x71,
		0x7e,0xaa,0xcd,0x19,0x03,0xd7,0xb0,0x64,0x84,0x50,0x37,0xe3,0xf9,0x2d,0x4a,0x9e,
		0x54,0x80,0xe7,0x33,0x29,0xfd,0x9a,0x4e,0xae,0x7a,0x1d,0xc9,0xd3,0x07,0x60,0xb4,
		0xbb,0x6f,0x08,0xdc,0xc6,0x12,0x75,0xa1,0x41,0x95,0xf2,0x26,0x3c,0xe8,0x8f,0x5b,
		0x39,0xed,0x8a,0x5e,0x44,0x90,0xf7,0x23,0xc3,0x17,0x70,0xa4,0xbe,0x6a,0x0d,0xd9,
		0xd6,0x02,0x65,0xb1,0xab,0x7f,0x18,0xcc,0x2c,0xf8,0x9f,0x4b,0x51,0x85,0xe2,0x36,
		0xfc,0x28,0x4f,0x9b,0x81,0x55,0x32,0xe6,0x06,0xd2,0xb5,0x61,0x7b,0xaf,0xc8,0x1c,
		0x13,0xc7,0xa0,0x74,0x6e,0xba,0xdd,0x09,0xe9,0x3d,0x5a,0x8e,0x94,0x40,0x27,0xf3,
		0xa8,0x7c,0x1b,0xcf,0xd5,0x01,0x66,0xb2,0x52,0x86,0xe1,0x35,0x2f,0xfb,0x9c,0x48,
		0x47,0x93,0xf4,0x20,0x3a,0xee,0x89,0x5d,0xbd,0x69,0x0e,0xda,0xc0,0x14,0x73,0xa7,
		0x6d,0xb9,0xde,0x0a,0x10,0xc4,0xa3,0x77,0x97,0x43,0x24,0xf0,0xea,0x3e,0x59,0x8d,
		0x82,0x56,0x31,0xe5,0xff,0x2b,0x4c,0x98,0x78,0xac,0xcb,0x1f,0x05,0xd1,0xb6,0x62
	};
	private static final int[] XTIMES_66 = { 
		0x00,0x66,0xcc,0xaa,0x83,0xe5,0x4f,0x29,0x1d,0x7b,0xd1,0xb7,0x9e,0xf8,0x52,0x34,
		0x3a,0x5c,0xf6,0x90,0xb9,0xdf,0x75,0x13,0x27,0x41,0xeb,0x8d,0xa4,0xc2,0x68,0x0e,
		0x74,0x12,0xb8,0xde,0xf7,0x91,0x3b,0x5d,0x69,0x0f,0xa5,0xc3,0xea,0x8c,0x26,0x40,
		0x4e,0x28,0x82,0xe4,0xcd,0xab,0x01,0x67,0x53,0x35,0x9f,0xf9,0xd0,0xb6,0x1c,0x7a,
		0xe8,0x8e,0x24,0x42,0x6b,0x0d,0xa7,0xc1,0xf5,0x93,0x39,0x5f,0x76,0x10,0xba,0xdc,
		0xd2,0xb4,0x1e,0x78,0x51,0x37,0x9d,0xfb,0xcf,0xa9,0x03,0x65,0x4c,0x2a,0x80,0xe6,
		0x9c,0xfa,0x50,0x36,0x1f,0x79,0xd3,0xb5,0x81,0xe7,0x4d,0x2b,0x02,0x64,0xce,0xa8,
		0xa6,0xc0,0x6a,0x0c,0x25,0x43,0xe9,0x8f,0xbb,0xdd,0x77,0x11,0x38,0x5e,0xf4,0x92,
		0xcb,0xad,0x07,0x61,0x48,0x2e,0x84,0xe2,0xd6,0xb0,0x1a,0x7c,0x55,0x33,0x99,0xff,
		0xf1,0x97,0x3d,0x5b,0x72,0x14,0xbe,0xd8,0xec,0x8a,0x20,0x46,0x6f,0x09,0xa3,0xc5,
		0xbf,0xd9,0x73,0x15,0x3c,0x5a,0xf0,0x96,0xa2,0xc4,0x6e,0x08,0x21,0x47,0xed,0x8b,
		0x85,0xe3,0x49,0x2f,0x06,0x60,0xca,0xac,0x98,0xfe,0x54,0x32,0x1b,0x7d,0xd7,0xb1,
		0x23,0x45,0xef,0x89,0xa0,0xc6,0x6c,0x0a,0x3e,0x58,0xf2,0x94,0xbd,0xdb,0x71,0x17,
		0x19,0x7f,0xd5,0xb3,0x9a,0xfc,0x56,0x30,0x04,0x62,0xc8,0xae,0x87,0xe1,0x4b,0x2d,
		0x57,0x31,0x9b,0xfd,0xd4,0xb2,0x18,0x7e,0x4a,0x2c,0x86,0xe0,0xc9,0xaf,0x05,0x63,
		0x6d,0x0b,0xa1,0xc7,0xee,0x88,0x22,0x44,0x70,0x16,0xbc,0xda,0xf3,0x95,0x3f,0x59
	};
	private static final int[] XTIMES_75 = { 
		0x00,0x75,0xea,0x9f,0xcf,0xba,0x25,0x50,0x85,0xf0,0x6f,0x1a,0x4a,0x3f,0xa0,0xd5,
		0x11,0x64,0xfb,0x8e,0xde,0xab,0x34,0x41,0x94,0xe1,0x7e,0x0b,0x5b,0x2e,0xb1,0xc4,
		0x22,0x57,0xc8,0xbd,0xed,0x98,0x07,0x72,0xa7,0xd2,0x4d,0x38,0x68,0x1d,0x82,0xf7,
		0x33,0x46,0xd9,0xac,0xfc,0x89,0x16,0x63,0xb6,0xc3,0x5c,0x29,0x79,0x0c,0x93,0xe6,
		0x44,0x31,0xae,0xdb,0x8b,0xfe,0x61,0x14,0xc1,0xb4,0x2b,0x5e,0x0e,0x7b,0xe4,0x91,
		0x55,0x20,0xbf,0xca,0x9a,0xef,0x70,0x05,0xd0,0xa5,0x3a,0x4f,0x1f,0x6a,0xf5,0x80,
		0x66,0x13,0x8c,0xf9,0xa9,0xdc,0x43,0x36,0xe3,0x96,0x09,0x7c,0x2c,0x59,0xc6,0xb3,
		0x77,0x02,0x9d,0xe8,0xb8,0xcd,0x52,0x27,0xf2,0x87,0x18,0x6d,0x3d,0x48,0xd7,0xa2,
		0x88,0xfd,0x62,0x17,0x47,0x32,0xad,0xd8,0x0d,0x78,0xe7,0x92,0xc2,0xb7,0x28,0x5d,
		0x99,0xec,0x73,0x06,0x56,0x23,0xbc,0xc9,0x1c,0x69,0xf6,0x83,0xd3,0xa6,0x39,0x4c,
		0xaa,0xdf,0x40,0x35,0x65,0x10,0x8f,0xfa,0x2f,0x5a,0xc5,0xb0,0xe0,0x95,0x0a,0x7f,
		0xbb,0xce,0x51,0x24,0x74,0x01,0x9e,0xeb,0x3e,0x4b,0xd4,0xa1,0xf1,0x84,0x1b,0x6e,
		0xcc,0xb9,0x26,0x53,0x03,0x76,0xe9,0x9c,0x49,0x3c,0xa3,0xd6,0x86,0xf3,0x6c,0x19,
		0xdd,0xa8,0x37,0x42,0x12,0x67,0xf8,0x8d,0x58,0x2d,0xb2,0xc7,0x97,0xe2,0x7d,0x08,
		0xee,0x9b,0x04,0x71,0x21,0x54,0xcb,0xbe,0x6b,0x1e,0x81,0xf4,0xa4,0xd1,0x4e,0x3b,
		0xff,0x8a,0x15,0x60,0x30,0x45,0xda,0xaf,0x7a,0x0f,0x90,0xe5,0xb5,0xc0,0x5f,0x2a
	};
	
	protected int numColumns;
	protected int numRows;
	protected int[][] roundConstants;
	protected int[] shiftConstants;
	protected boolean isPermutationP = false;
	
	public boolean canInvertKeySchedule() {
		return false;
	}
	
	public ByteArray computeExpandedKey(ByteArray keyPart, int round) {
		throw new NoKeyRequiredError();
	}
	
	public ByteArray decryptRounds(ByteArray block, int fromRound, int toRound) {
		ByteArray state = block.clone();
		
		for (int round = toRound; round >= fromRound; round--) {
			state = invertMixBytes(state);
			state = invertShiftBytes(state);
			state = invertSubBytes(state);
			state = addRoundConstant(round, state);
		}
		
		return state;
	}
	
	public ByteArray encryptRounds(ByteArray block, int fromRound, int toRound) {
		ByteArray state = block.clone();
		// String p = isPermutationP ? "P" : "Q";
		
		for (int round = fromRound; round <= toRound; round++) {
			state = addRoundConstant(round, state);
			state = subBytes(state);
			state = shiftBytes(state);
			state = mixBytes(state);
		}
		
		return state;
	}
	
	public ByteArray getRoundKey(int round) {
		throw new NoKeyRequiredError();
	}
	
	public void setKey(ByteArray key) {
		throw new NoKeyRequiredError();
	}
	
	public void setExpandedKey(ByteArray expandedKey) {
		throw new NoKeyRequiredError();
	}
	
	private ByteArray addRoundConstant(int round, ByteArray state) {
		int position = 0;
		int count = 0;
		round--;
		
		if (isPermutationP) {
			for (int i = 0; i < numColumns; i++) {
				state.set(position, state.get(position) ^ round ^ count);
				position += numRows;
				count += 0x10;
			}
		} else {
			count = 0xff;
			
			for (int i = 0; i < this.stateSize; i++) {
				if ((i + 1) % this.numRows == 0) {
					state.set(i, state.get(i) ^ round ^ count);
					count -= 0x10;
				} else {
					state.set(i, state.get(i) ^ 0xFF);
				}
			}
		}
		
		return state;
	}
	
	private ByteArray invertMixBytes(ByteArray state) {
		short[] oldState = state.getArray();
		ByteArray newState = new ByteArray(stateSize);
		
		// 153 196  70  54  30 212 102 117
		// 117 153 196  70  54  30 212 102
		// 102 117 153 196  70  54  30 212
		// 212 102 117 153 196  70  54  30
		//  30 212 102 117 153 196  70  54
		//  54  30 212 102 117 153 196  70
		//  70  54  30 212 102 117 153 196
		// 196  70  54  30 212 102 117 153

		//   153 196  70  54  30 212 102 117
		//    99  C4  46  36  1E  D4  66  75
		int value = 0;
		int offset = 0;

		for (int column = 0; column < numColumns; column++) {
			offset = column * numRows;
			
			for (int row = 0; row < numRows; row++) {
				value = XTIMES_99[oldState[offset + (row % numRows)]] ^ 
					XTIMES_C4[oldState[offset + ((row + 1) % numRows)]] ^ 
					XTIMES_46[oldState[offset + ((row + 2) % numRows)]] ^ 
					XTIMES_36[oldState[offset + ((row + 3) % numRows)]] ^ 
					XTIMES_1E[oldState[offset + ((row + 4) % numRows)]] ^ 
					XTIMES_D4[oldState[offset + ((row + 5) % numRows)]] ^ 
					XTIMES_66[oldState[offset + ((row + 6) % numRows)]] ^ 
					XTIMES_75[oldState[offset + ((row + 7) % numRows)]];
				
				newState.set(offset + row, value);
			}
		}
		
		state = newState.clone();
		return newState;
	}
	
	private ByteArray invertShiftBytes(ByteArray state) {
		ByteArray newstate = state.clone();
		int destination, source;
		
		for (int row = 0; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++) {
				source = ((column + numColumns - row) % numColumns) * numRows + row;
				destination = column * numRows + row;
				newstate.set(destination, state.get(source));
			}
		}
		
		return newstate;
	}
	
	private ByteArray invertSubBytes(ByteArray state) {
		for (int index = 0; index < stateSize; index++) {
			state.set(index, INVERSE_SBOX[state.get(index)]);
		}
		
		return state;
	}
	
	private ByteArray mixBytes(ByteArray state) {
		short[] oldState = state.getArray();
		ByteArray newState = new ByteArray(this.stateSize);
		
		// 02 02 03 04 05 03 05 07
		// 07 02 02 03 04 05 03 05
		// 05 07 02 02 03 04 05 03
		// 03 05 07 02 02 03 04 05
		// 05 03 05 07 02 02 03 04
		// 04 05 03 05 07 02 02 03
		// 03 04 05 03 05 07 02 02
		// 02 03 04 05 03 05 07 02
		
		int value = 0;
		int offset = 0;
		
		for (int column = 0; column < numColumns; column++) {
			offset = column * numRows;
			
			for (int row = 0; row < numRows; row++) {
				value = XTIMES_02[oldState[offset + (row % numRows)]] ^ 
					XTIMES_02[oldState[offset + ((row + 1) % numRows)]] ^ 
					XTIMES_03[oldState[offset + ((row + 2) % numRows)]] ^ 
					XTIMES_04[oldState[offset + ((row + 3) % numRows)]] ^ 
					XTIMES_05[oldState[offset + ((row + 4) % numRows)]] ^ 
					XTIMES_03[oldState[offset + ((row + 5) % numRows)]] ^ 
					XTIMES_05[oldState[offset + ((row + 6) % numRows)]] ^ 
					XTIMES_07[oldState[offset + ((row + 7) % numRows)]];
				
				newState.set(offset + row, value);
			}
		}
		
		state = newState.clone();
		return newState;
	}
	
	private ByteArray shiftBytes(ByteArray state) {
		ByteArray newstate = state.clone();
		int destination, source, shiftBy;
		
		for (int row = 0; row < numRows; row++) {
			shiftBy = this.shiftConstants[row];
			
			for (int column = 0; column < numColumns; column++) {
				source = ((column + shiftBy) % numColumns) * numRows + row;
				destination = (column * numRows) + row;
				newstate.set(destination, state.get(source));
			}
		}
		
		return newstate;
	}
	
	private ByteArray subBytes(ByteArray state) {
		for (int index = 0; index < stateSize; index++) {
			state.set(index, SBOX[state.get(index)]);
		}
		
		return state;
	}
	
}
