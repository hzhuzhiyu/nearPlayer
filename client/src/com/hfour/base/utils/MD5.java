package com.hfour.base.utils;

/************************************************
 MD5 算法的Java Bean
 @author:ZHL
 Last Modified:10,Mar,2008
 *************************************************/

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.text.TextUtils;

public class MD5 {

	public static char[] num_chars = new char[] { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private MD5() {
	}

	public static String toMD5String(String input) {
		if (TextUtils.isEmpty(input)) {
			return null;
		}
		return toMD5String(input.getBytes());
	}
	
	public static String toMD5String(byte[] data){
		if(null == data || 0 == data.length){
			return null;
		}
		final char[] output = new char[32];
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data);
			byte[] by = md.digest();
			for (int i = 0; i < by.length; i++) {
				output[2 * i] = num_chars[(by[i] & 0xf0) >> 4];
				output[2 * i + 1] = num_chars[by[i] & 0xf];
			}
		} catch (NoSuchAlgorithmException e) {
		}
		return new String(output);
	}
}
