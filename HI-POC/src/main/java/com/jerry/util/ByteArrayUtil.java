package com.jerry.util;

import java.util.Arrays;

public class ByteArrayUtil {

	public static int parseInt(byte[] bs) {
		return Integer.parseInt(parseString(bs));
	}

	public static void fillWhiteSpace(byte[] byteArray) {
		Arrays.fill(byteArray, (byte)' ');
	}
	public static void fillZero(byte[] byteArray) {
		Arrays.fill(byteArray, (byte)' ');
	}

	public static String parseString(byte[] bs) {
		return new String(bs);
	}

}
