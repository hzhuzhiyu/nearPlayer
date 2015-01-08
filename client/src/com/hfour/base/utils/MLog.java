package com.hfour.base.utils;

import android.util.Log;

import com.hfour.base.constants.GlobalParams;

/**
 * 统一的打印日志入口，便于后面维护
 * myLog
 */
public class MLog {
	// 日志开关，默认打开
	public static final boolean isDebug = true;

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.d(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (isDebug)
			Log.w(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.e(tag, msg);
	}
	
	public static void info(String msg) {
		if (isDebug)
			Log.d(GlobalParams.appPkgName, msg);
	}
}
