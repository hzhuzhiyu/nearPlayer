package com.hfour.base.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author harry.wang
 * @date 2013-5-7
 */
public class SHA256Util {
	public static final String __PWD_EXTRA_CODE = "6a03ddb38094456383174254ec75b441";

	public static String sha_password_once(String data) {
		return sha256(data + __PWD_EXTRA_CODE);
	}

	public static String sha_password_text_by_salt(String data, String salt) {
		String onceStr = sha256(data + __PWD_EXTRA_CODE);
		return sha256(onceStr + "{" + salt + "}");
	}

	public static String sha_password_cipher_by_salt(String data, String salt) {
		return sha256(data + "{" + salt + "}");
	}

	private static String sha256(String password) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.update(password.getBytes());
			return bytesToHexString(digest.digest());
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	private static String bytesToHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
