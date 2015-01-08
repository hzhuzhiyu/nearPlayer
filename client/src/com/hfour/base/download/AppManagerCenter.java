package com.hfour.base.download;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.hfour.base.utils.FileUtils;
import com.hfour.base.utils.PackageUtils;
import com.hfour.nearplayer.MainApplication;
import com.hfour.nearplayer.net.data.GameBean;

/**
 * APP管理：安装，下载，删除等
 */

public class AppManagerCenter {
	
	// ------------------ APP状态 Start ------------------
	/** * 应用不存在 */
	public static final int APP_STATE_UNEXIST = 0x1000;
	/** * 应用正在被下载 */
	public static final int APP_STATE_DOWNLOADING = APP_STATE_UNEXIST + 1;
	/** * 应用下载被暂停 */
	public static final int APP_STATE_DOWNLOAD_PAUSE = APP_STATE_DOWNLOADING + 1;
	/** * 应用已完成下载，但尚未安装 */
	public static final int APP_STATE_DOWNLOADED = APP_STATE_DOWNLOAD_PAUSE + 1;
	/** * 应用已被安装 */
	public static final int APP_STATE_INSTALLED = APP_STATE_DOWNLOADED + 1;
	/** * 应用需要更新 */
	public static final int APP_STATE_UPDATE = APP_STATE_INSTALLED + 1;
	/** * 应用等待下载 */
	public static final int APP_STATE_WAIT = APP_STATE_UPDATE + 1;
	/** 应用正在被安装（仅静默安装时使用） **/
	public static final int APP_STATE_INSTALLING = APP_STATE_WAIT + 1;
	// ------------------ APP状态 End ------------------


	private static final Context context = MainApplication.ctx;
	protected static final String TAG = "AppManagerCenter";
	private static HashSet<String> staticInstallPkg = new HashSet<String>();

	
	/**
	 * 是否存在该游戏
	 * 
	 * @param appPackage
	 * @return
	 */
	public static boolean isAppExist(String appPackage) {
		try {
			MainApplication.ctx.getPackageManager().getPackageInfo(appPackage, 0);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 
	 * @param packageName
	 * @param version
	 *            : 版本号，用来判断是否要更新
	 * @param gameId
	 *            : 用来查询是否存在APK或下载中的临时文件
	 * @return
	 */
	public static int getGameAppState(String packageName, String gameCode,
			int versionCode) {
		
		if (null == gameCode) {
			gameCode = "";
		}
		if (null == packageName) {
			return APP_STATE_UNEXIST;
		}
		int appState = APP_STATE_UNEXIST; //默认不存在
	
		do {
			if (staticInstallPkg.contains(packageName)) {
				appState = APP_STATE_INSTALLING;
				break;
			}
			
			//先判断是否存在 和 是否更新
			if (isAppExist(packageName)){
				appState = APP_STATE_INSTALLED;
				if(appIsNeedUpate(packageName, versionCode)) {
					// 还需要更新
					appState = APP_STATE_UPDATE;
				}
			}
			//需要判断是否有TASK，原因：山寨游戏是最新版本，但被下载替换中，故要判断是否下载。
			DownloadTask task = DownloadTaskMgr.getInstance().getDownloadTask(packageName);
			if(null == task){
				//没有下载，do nothing, 或者是后台任务，不处理
			}else{
				switch(task.gameDownloadState){
					case DownloadState.STATE_DOWNLOAD_WAIT:
						appState = APP_STATE_WAIT;
						break; 
					case DownloadState.STATE_DOWNLOAD_START_LOADING:
					case DownloadState.STATE_DOWNLOAD_UPDATE_PROGRESS:
						appState = APP_STATE_DOWNLOADING;
						break;
					case DownloadState.STATE_DOWNLOAD_PAUSE:
					case DownloadState.STATE_DOWNLOAD_ERROR:	//错误下载，需要重新下载，设置成暂停状态
						appState = APP_STATE_DOWNLOAD_PAUSE;
						break;
					case DownloadState.STATE_DOWNLOAD_SUCESS:{
						if(APP_STATE_UPDATE == appState){
							//下载的版本和线上的版本一致
							appState = APP_STATE_DOWNLOADED; //下载成功，提示安装
						}else if(APP_STATE_INSTALLED == appState){
							//已安装
						}else{
							appState = APP_STATE_DOWNLOADED; //默认下载成功，提示安装
						}
					}
						break;
					default:
						//do nothing
						break;
				}
			}
		} while (false);
		return appState;
	}


	/**
	 * 安装下载下来的APK包
	 * 
	 * @param pkgName
	 *            : 用来统计数据
	 */
	public static void installGameApk(GameBean gameApp){
		installGameApk(gameApp,true);
	}
	/**
	 * 
	 * @param gameApp
	 * @param isAddToMygame : false  不添加到我玩中，用于 A 替换成 B，避免A还存在
	 */
	public static void installGameApk(GameBean gameApp, boolean isAddToMygame) {
		if(null == gameApp){
			return;
		}
		
		String packageName = gameApp.getGamePkgName();
		if (staticInstallPkg.contains(packageName)) {
			// 已经在安装
			return;
		}
		String gameAPKFilePath = FileUtils.getGameAPKFilePath(gameApp.getGamePkgName());
		File gameAPKFile = new File(gameAPKFilePath);
		if (gameAPKFile.exists()) {
			installRoot(gameAPKFilePath, packageName);
		}else {
			//文件被删除了,重新下载
			startDownload(gameApp);
		}
	}

	

	private static void refreshUI() {
		DownloadTaskMgr.getInstance().notifyRefreshUI(DownloadState.STATE_DOWNLOAD_REFRESH);
	}

	/**
	 * 静默安装
	 * 
	 * @param path
	 * @param msgwhat
	 */
	public static void installRoot(final String path, final String pkg) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				staticInstallPkg.add(pkg);
				refreshUI();
				PackageUtils.install(context, path);
				staticInstallPkg.remove(pkg);
				refreshUI();
			}
		};
		new Thread(task).start();
	}


	/**
	 * 卸载游戏
	 * 
	 * @param packageName
	 */
	public static void uninstallGameApp(final String packageName) {
		PackageUtils.uninstallNormal(MainApplication.ctx, packageName);
		//不希望用户卸载
//		if (isAppExist(packageName)) {
//			Runnable task = new Runnable() {
//				@Override
//				public void run() {
//					PackageUtils.uninstall(BaseApplication.curContext, packageName);
//					refreshUI();
//				}
//			};
//			new Thread(task).start();
//		}
	}

	/**
	 * 判断是否要更新版本，根据versionCode来判断
	 * 
	 * @param packageName
	 * @param versionCode
	 * @return
	 */

	public static boolean appIsNeedUpate(String packageName, int versionCode) {
		try {
			PackageInfo packageInfo = MainApplication.ctx
					.getPackageManager().getPackageInfo(packageName,
							PackageManager.GET_META_DATA);

			if (packageInfo != null) {
				if (packageInfo.versionCode < versionCode) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	
	/////////////////////////////////////以下代码为下载API////////////////////////////////////////////////////////
	/**
	 * 开始下载
	 * 
	 * @param game
	 * @param isBackground :  是否是后台任务      
	 */
	private static void startDownload(GameBean game) {
		if (null == game || null == game.getDownloadUrl() || null == game.getGamePkgName()) {
			return;
		}
		
		DownloadTaskMgr.getInstance().startDownload(game);
	}
	/**
	 * 暂停下载
	 * 
	 * @param context
	 * @param gameCode
	 * @param isUserPressed :用户主动停止
	 */

	public static void pauseDownload(GameBean game, boolean isUserPressed) {
		DownloadTaskMgr.getInstance().pauseDownload(game, isUserPressed);
	}

	/**
	 * 取消下载，会删除已下载的文件，从数据库中删除下载信息
	 * 
	 * @param context
	 * @param gameCode
	 */
	public static void cancelDownload(GameBean game) {
		DownloadTaskMgr.getInstance().cancelDownload(game);
	}

	/**
	 * 删除下载的APK包或下载的临时文件
	 * 
	 * @param gameCode
	 */
	public static void deleteDownloadGameApk(GameBean game) {
		DownloadTaskMgr.getInstance().cancelDownload(game);
	}

	/**
	 * 继续下载所有下载中任务
	 */
	public static void continueAllDownload() {
		DownloadTaskMgr.getInstance().continueAllDownload();
	}

	/**
	 * 暂停所有下载中任务
	 */
	public static void pauseAllDownload() {
		DownloadTaskMgr.getInstance().pauseAllDownload();
	}

	/**
	 * 根据pkgName查询游戏的下载进度
	 * 
	 * @param pkgName
	 * @return
	 */
	public static int getDownloadProgress(String pkgName) {
		return DownloadTaskMgr.getInstance().getDownloadProgress(pkgName);
	}

	/**
	 * UI对download状态的监听.
	 * 注意：当UI界面销毁，或者的被置于后台的时候，移除监听。避免重复多次的刷新数据和UI
	 * 记得删除,否则会引起内存泄露
	 * @param refreshHanle
	 */
	public static void setDownloadRefreshHandle(UIDownLoadListener refreshHanle) {
		DownloadTaskMgr.getInstance().setUIDownloadListener(refreshHanle);
	}

	/**
	 * 删除下载监听句柄
	 * 
	 * @param refreshHanle
	 */
	public static void removeDownloadRefreshHandle(UIDownLoadListener refreshHanle) {
		DownloadTaskMgr.getInstance().removeUIDownloadListener(refreshHanle);
	}
	
	public static boolean hasDownloadingApp() {
		return DownloadTaskMgr.getInstance().hasDownloadingTask();
	}
/////////////////////////////////////////下载的接口 end/////////////////////////////////////////////////////////////////////
	public static final String OLD_PKG_NAME = "com.socogame.ppc";

	/**
	 * 判定是否存在旧版的版本
	 * 
	 * @return
	 */
	public static boolean isOldVersionExist() {
		List<PackageInfo> pkgs = context.getPackageManager()
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		int pkgSize = pkgs.size();
		for (int i = 0; i < pkgSize; i++) {
			PackageInfo pkgInfo = pkgs.get(i);
			if (OLD_PKG_NAME.equalsIgnoreCase(pkgInfo.packageName)) {
				if (isSystemApp(pkgInfo) || isSystemUpdateApp(pkgInfo)) {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 是否是系统应用
	 * 
	 * @param pInfo
	 * @return
	 */
	public static boolean isSystemApp(PackageInfo pInfo) {
		return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	}

	public static boolean isSystemApp(String packageName) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(
					packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 是否是系统应用更新
	 * 
	 * @param pInfo
	 * @return
	 */
	public static boolean isSystemUpdateApp(PackageInfo pInfo) {
		return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}


	

	private static final String SCHEME = "package";
	/**
	* 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
	*/
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	/**
	* 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
	*/
	private static final String APP_PKG_NAME_22 = "pkg";
	/**
	* InstalledAppDetails所在包名
	*/

	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	/**
	* InstalledAppDetails类名
	*/

	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	/**
	* 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
	* 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
	* @param context
	* @param packageName
	* 应用程序的包名
	*/
	public static void showInstalledAppDetails(Context context, String packageName) {
		if(!AppManagerCenter.isAppExist(packageName)){
			//不存在的应用，退出
			return;
		}
		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts(SCHEME, packageName, null);
			intent.setData(uri);
		} else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
			// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
			intent.putExtra(appPkgName, packageName);
		}
		context.startActivity(intent);
	}  
}
