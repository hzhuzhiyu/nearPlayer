package com.hfour.base.utils;

import android.content.Context;

import com.hfour.base.device.ClientInfo;

public class BlowfishLocal {
	private static byte local_blowfish_key[];
	private static Blowfish bf = new Blowfish();

	public static byte[] encrypt(Context context, byte[] b) {
		if (b == null) {
			return null;
		}
		if (local_blowfish_key == null) {
			local_blowfish_key = getLocalKey(context);
		}
		if (local_blowfish_key == null) {
			return null;
		}

		byte ret[] = new byte[b.length + local_blowfish_key.length];
		System.arraycopy(local_blowfish_key, 0, ret, 0,
				local_blowfish_key.length);
		System.arraycopy(b, 0, ret, local_blowfish_key.length, b.length);
		byte r[] = bf.encrypt(ret, 0, ret.length);

		return r;
	}

	public static byte[] decrypt(Context context, byte[] b) {
		if (b == null) {
			return null;
		}
		if (local_blowfish_key == null) {
			local_blowfish_key = getLocalKey(context);
		}
		if (local_blowfish_key == null) {
			return null;
		}

		byte ret[] = bf.decrypt(b, 0, b.length);
		if (ret == null) {
			return null;
		}

		if (ret.length <= local_blowfish_key.length) {
			return null;
		}

		// 检测前几个字节是否是加密串的头字节
		byte check[] = new byte[local_blowfish_key.length];
		System.arraycopy(ret, 0, check, 0, local_blowfish_key.length);
		for (int i = 0; i < local_blowfish_key.length; i++) {
			if (check[i] != local_blowfish_key[i]) {
				return null;
			}
		}
		byte r[] = new byte[ret.length - local_blowfish_key.length];
		System.arraycopy(ret, local_blowfish_key.length, r, 0, r.length);

		return r;
	}

	private static byte[] getLocalKey(Context context) {
		String mac = ClientInfo.getInstance().mac;
		byte local[] = mac.getBytes();
		return local;
	}
}
