package com.hfour.base.utils;

import java.io.ByteArrayOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author harry.wang
 * @date 2013-2-22
 */
public class EncryptAes {
	//
	public static String encryptPassword(String password) {
		byte[] pwd_data = encrypt(password.getBytes(), temp_key);
		String passport = Base64.encode(pwd_data);
		return passport;
	}

	public static String decryptPassword(String passport) {
		byte[] pwd_data = Base64.decode(passport);
		String password = new String(decrypt(pwd_data, temp_key));
		return password;
	}

	private static String temp_key = "4e6f-9759=c9578e";

	public static byte[] encrypt(byte[] src_data, String sKey) {
		if (null == src_data || null == sKey) {
			return null;
		}
		// the key is must 16 bits long
		if (sKey.length() != 16) {
			return null;
		}
		byte[] raw = sKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher;
		byte[] encrypted = null;
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			src_data = fillZero(src_data);

			encrypted = cipher.doFinal(src_data);
		} catch (Exception e) {
		}

		return encrypted;
	}

	//
	public static byte[] decrypt(byte[] src_data, String sKey) {
		try {
			if (null == src_data || null == sKey) {
				return null;
			}
			// the key is must 16 bits long
			if (sKey.length() != 16) {
				return null;
			}
			byte[] raw = sKey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// ("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			try {
				byte[] original = cipher.doFinal(src_data);
				original = filterZero(original);
				return original;
			} catch (Exception e) {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	private static byte[] fillZero(byte[] data) {
		int remain = data.length % 16;
		if (remain > 0) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(data, 0, data.length);
			for (int i = 16; i > remain; i--) {
				bout.write(0);
			}
			data = bout.toByteArray();
		}
		return data;
	}

	private static byte[] filterZero(byte[] data) {
		int unit = data.length - 16;
		int i = data.length - 1;
		for (; i > unit; i--) {
			if (0 != data[i]) {
				break;
			}
		}
		if (i < data.length - 1) {
			int size = i + 1;

			byte[] temp_data = new byte[size];
			System.arraycopy(data, 0, temp_data, 0, size);
			data = temp_data;
		}
		return data;
	}

}
