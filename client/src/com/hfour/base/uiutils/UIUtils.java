package com.hfour.base.uiutils;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.widget.Toast;

import com.hfour.base.activity.RootActivity;
import com.hfour.base.constants.GlobalParams;
import com.hfour.nearplayer.activity.WebViewActivity;

/**
 * 实现UI的跳转，定义Activity之间跳转最简单接口 目前 重构之外的跳转代码分散到各个Activity中，这样重复劳动太多，容易出现错误
 * 
 * @author PanYingYun
 * 
 */
public class UIUtils {
	/**
	 * 通用的不带参数的界面跳转
	 * 
	 */
	public static void gotoActivity(Class<?> cls) {
		Intent intent = new Intent(GlobalParams.gCurrentActivity, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		GlobalParams.gCurrentActivity.startActivity(intent);
	}
	
	

	/**
	 * startActivityForResult
	 * 
	 * @param activity
	 * @param cls
	 * @param requestCode
	 */
	public static void gotoActivityForResult(Activity activity, Class<?> cls,
			int requestCode) {
		Intent intent = new Intent(activity, cls);
		activity.startActivityForResult(intent, requestCode);
	}
	
	/**
	 * 跳转到web页
	 */
	public static void gotoWeb(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		GlobalParams.gCurrentActivity.startActivity(intent);
	}

	public static void gotoMarket(String packageName, String gameCode) {
		try {
			Uri marketUri = Uri.parse("market://details?id=" + packageName);
			Intent intent = new Intent();
			intent.setData(marketUri);
			GlobalParams.gCurrentActivity.startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 分享文本
	 * 
	 * @param ctx
	 *            ：
	 * @param Content
	 *            ： 分享的内容
	 * @param subject
	 *            ：
	 * @param title
	 *            ： 分享的标题
	 */
	public static void shareText(Context ctx, String Content, String subject,
			String title) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		// intent.setComponent(new ComponentName("com.tencent.mm",
		// "com.tencent.mm.ui.tools.ShareImgUI")); //分享到微信，指定
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, Content);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(Intent.createChooser(intent, title)); // 普通分享
		// ctx.startActivity(intent); //分享到具体的应用
	}

	public static void shareImage(Context ctx, String Content, String subject,
			String title, String imageType, String path) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		// 图片分享
		intent.setType(imageType);// "image/png"
		// 添加图片
		File f = new File(path);
		Uri uri = Uri.fromFile(f);
		intent.putExtra(Intent.EXTRA_STREAM, uri);

		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, Content);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(Intent.createChooser(intent, title));
	}

	/**
	 * 仅供UI层显示
	 * @param resId
	 */
	public static void showToast(int resId){
		Toast toast = Toast.makeText(GlobalParams.gCurrentActivity, resId, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	public static void showToast(String toastString){
		Toast toast = Toast.makeText(GlobalParams.gCurrentActivity, toastString, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	
	public static void finishAllActivity(Context ctx){
		if(null == ctx){
			return;
		}
		Intent intent = new Intent();
		intent.setAction(RootActivity.FINISH_ACTION);
		ctx.sendBroadcast(intent);
	}


	/**
	 * 跳转到内嵌页
	 * @param Url
	 * @param title
	 * @param navigationBar : true 带导航栏
	 */
	public static void gotoWebViewActivity(String Url, String title, boolean navigationBar) {
		gotoWebViewActivity(Url, title, navigationBar, WebViewActivity.class);
	}
	
	public static void gotoWebViewActivity(String Url, String title, boolean navigationBar, Class<?> cls) {
		Intent intent = new Intent(GlobalParams.gCurrentActivity, cls);
		intent.putExtra(WebViewActivity.WEBVIEW_URL, Url);
		intent.putExtra(WebViewActivity.WEBVIEW_TITLE, title);
		intent.putExtra(WebViewActivity.WEBVIEW_NAVIGATION_BAR, navigationBar);
		
		GlobalParams.gCurrentActivity.startActivity(intent);
	}


	public static void gotoLogin() {
	}
}
