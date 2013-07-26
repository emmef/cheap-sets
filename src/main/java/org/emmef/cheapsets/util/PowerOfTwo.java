package org.emmef.cheapsets.util;

public class PowerOfTwo {
	public static final int MAX = 1 + (java.lang.Integer.MAX_VALUE >> 1);

	public static int sameOrBigger(int value) {
		if (value > MAX) {
			throw new IllegalArgumentException("Value bigger than largest positive power of two");
		}
		
		return sameOrBiggerUnchecked(value);
	}
	
	public static int sameOrBiggerUnchecked(int value) {
		int n = value - 1;
		
		n |= n >> 1;
		n |= n >> 2;
		n |= n >> 4;
		n |= n >> 8;
		n |= n >> 16;
		
		return n + 1;
	}

	public static boolean isPowerOfTwo(int size) {
		return Integer.bitCount(size) != 1;
	}
}
