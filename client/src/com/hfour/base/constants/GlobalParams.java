package com.hfour.base.constants;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.hfour.base.activity.RootActivity;
import com.hfour.base.device.ClientInfo;
import com.hfour.base.net.data.UpdateVersionBean;
import com.hfour.base.utils.DataStoreUtils;
/**
 * 全局参数:注意缓存
 * @author Tony
 *
 */
public class GlobalParams {
	private static final String TEMP_USER_ID = "tmpUserId";
	/**此处Context是当前页的activity**/
	public static Context gContext; 
	public static RootActivity gCurrentActivity;
	public static String appPkgName; 
	public static ClientInfo client;
	public static UpdateVersionBean versionInfo;
	public static int apkVerCode;
	public static String apkVerName;
	private static String tempUserId;
	
	/**
	 * 初始化常量
	 * @param ctx
	 */
	public static void initGlobalParams(Context ctx){
		gContext = ctx;
		appPkgName = ctx.getPackageName();
		ClientInfo.getAPNType(gContext); //初始化网络状态
		//如果用户已经登录了，从缓存中恢复
//		user = GsonUtil.parserObj(userJson, UserInfoBean.class);
		
		PackageInfo packageInfo;
		try {
			PackageManager packageManager = GlobalParams.gContext.getPackageManager();
			packageInfo = packageManager.getPackageInfo(GlobalParams.appPkgName, 0);
			apkVerCode = packageInfo.versionCode;
			apkVerName = packageInfo.versionName;
		} catch (Exception e) {
		}
		//读取本地的tmpuserId
		tempUserId = DataStoreUtils.readLocalInfo(TEMP_USER_ID);
	}


	public static String getTempUserId() {
		return tempUserId;
	}
	
	public static void saveTempUserId(String tmpUserId){
		if(TextUtils.isEmpty(tmpUserId)){
			return;
		}
		tempUserId = tmpUserId;
		DataStoreUtils.saveLocalInfo(TEMP_USER_ID, tmpUserId);
	}
	
}