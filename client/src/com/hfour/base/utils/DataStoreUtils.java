package com.hfour.base.utils;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;

import com.hfour.base.constants.GlobalParams;

public class DataStoreUtils {
	public static final String FILE_NAME = "awifi_sp";
	/**通用 true*/
	public static final String SP_TRUE = "true";
	/**通用 false*/
	public static final String SP_FALSE = "false";
	
	public static final String DEFAULT_VALUE = "";
	
	public static final String IS_UPDATE_APP = "update_app"; //true, false
	public static final String CHECK_VERSION_TIME = "check_vt";
	public static final String SP_SHORT_CUT = "shortcut";
	public static final String APP_VERSION = "app_version";
	
	
	/**
	 * 保存信息到 SharedPreferences
	 * @param name
	 * @param value
	 */
	public static void saveLocalInfo(String name, String value) {
		SharedPreferences share = GlobalParams.gContext
				.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);

		if (share != null) {
			share.edit().putString(name, value).apply();
		}
	}

	/**
	 * 从 SharedPreferences 中读取数据
	 * @param name
	 * @return
	 */
	public static String readLocalInfo(String name) {
		SharedPreferences share = GlobalParams.gContext
				.getSharedPreferences(FILE_NAME, 0);
		if (share != null) {
			return share.getString(name, DEFAULT_VALUE);
		}
		return DEFAULT_VALUE;
	}
	
	/**
	 * 从asserts 目录下读取图片文件
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static BitmapDrawable readImgFromAssert(Context context,
			String imgFileName) {
		InputStream inputStream = null;
		BitmapDrawable drawable = null;

		if (null == imgFileName) {
			return null;
		}

		try {
			inputStream = context.getResources().getAssets().open(imgFileName);
			drawable = new BitmapDrawable(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return drawable;
	}

}
