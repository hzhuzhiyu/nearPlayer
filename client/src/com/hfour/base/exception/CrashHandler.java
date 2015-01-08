package com.hfour.base.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

import com.hfour.base.net.AbstractNetSource;
import com.hfour.base.utils.HttpClientUtils;

/**
 * 功能：App其他地方未捕获的异常在该处捕获并上报，重启应用
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler instance;

	private Thread.UncaughtExceptionHandler mDefaultHandler = null;

	private CrashHandler() {
	}

	public static synchronized CrashHandler getInstance() {
		if (instance == null) {
			instance = new CrashHandler();
		}
		return instance;
	}

	public void init(Context ctx) {
		// 以下用来捕获程序崩溃异常
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (mDefaultHandler != null && !handlerException(ex)) {
			// 如果用户没有进行异常处理就让系统默认的处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	
	private boolean handlerException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		ex.printStackTrace();
		return false;
	}

	public static void exitApp() {
		AbstractNetSource.onStop();
		HttpClientUtils.release();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
}