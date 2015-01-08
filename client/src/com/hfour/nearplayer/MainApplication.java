package com.hfour.nearplayer;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.hfour.base.constants.GlobalParams;
import com.hfour.base.device.ClientInfo;
import com.hfour.base.uiutils.UIUtils;
import com.hfour.base.utils.BitmapMgr;
import com.hfour.base.utils.FileUtils;
import com.hfour.base.utils.MLog;

public class MainApplication extends Application{
	public static Context ctx;
//	private static boolean hasInit = false;
	public static String exceptionInfo;
	
	public void onCreate() {
		super.onCreate();
		ctx = getApplicationContext();
		initAppData();
	}
	
	/**
	 * 全局数据，需要在application中初始化，<br>
	 * 避免中途被系统回收后，数据清除了。<br>
	 * 初始化APP的数据， 比如常量，数据库等
	 **/
	public static void initAppData() {
		MLog.info("initApp");
		GlobalParams.initGlobalParams(ctx);
		FileUtils.initAppFile();
		ClientInfo.initClientInfo();
		BitmapMgr.init(ctx.getResources(), ClientInfo.getInstance().screenWidth);
	}
	
		
	private static final int MSG_EXIT_APP = 1;
	private static Handler appHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(MSG_EXIT_APP == msg.what){
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
		};
	};
	
	public static void exitApp(Context ctx){
		UIUtils.finishAllActivity(ctx);
		
		appHandler.sendEmptyMessageDelayed(MSG_EXIT_APP, 500);
	}
	
}
