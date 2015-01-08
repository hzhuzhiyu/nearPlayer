package com.hfour.base.download;




/**
 * 同UI交互
 *
 * @author Tony
 *
 */
public abstract class UIDownLoadListener {
	public void handleDownloadState(int state, int errorCode, String pkgName){
		switch(state){
		case DownloadState.STATE_DOWNLOAD_START_LOADING:
			onStart(pkgName);
			break;
		case DownloadState.STATE_DOWNLOAD_PAUSE:
			onPause(pkgName);
			break;
		case DownloadState.STATE_DOWNLOAD_ERROR:
			onError(pkgName);
			onErrorCode(pkgName, errorCode);
			break;
		case DownloadState.STATE_DOWNLOAD_SUCESS:
			onFinish(pkgName);
			break;
		case DownloadState.STATE_DOWNLOAD_WAIT:
			onReady(pkgName);
			break;
		case DownloadState.STATE_DOWNLOAD_INSTALLED:
			onInstallSucess(pkgName);
			break;
		case DownloadState.STATE_DOWNLOAD_UPDATE_PROGRESS:
			onUpdateProgress(pkgName);
			break;
		case DownloadState.STATE_DOWNLOAD_MODE_INIT:
			//下载模块初始化完成
			onInitDownloadMode();
			return;
		case DownloadState.STATE_DOWNLOAD_CANCEL:
			onStop(pkgName);
			break;
		}
		
		onRefreshUI(pkgName);
	}
	
	protected void onUpdateProgress(String pkgName){
		
	}
	
	protected void onReady(String pkgName){
		
	}
	protected void onFinish(String pkgName){
	}
	
	protected void onStart(String pkgName){
	}

	protected void onStop(String pkgName) {
	}

	protected void onInstallSucess(String pkgName) {
	}
	
	protected void onPause(String pkgName) {
	}
	/**
	 * 下载模块初始完成
	 */
	protected void onInitDownloadMode(){
		
	}
	/**
	 * 下载出错，UI的处理
	 * @param pkgName
	 */
	protected void onError(String pkgName) {
		
	}
	/**
	 * 处理错误的code,
	 * 可能因为网络超时、导致超时、没有SD卡、没有网络、网络设置不对等原因，实现类如果需要关心错误值,实现该类
	 * @param pkgName
	 * @param errorCode
	 */
	protected void onErrorCode(String pkgName, int errorCode) {
	}
	/**
	 * 刷UI
	 * @param pkgName
	 */
	public abstract void onRefreshUI(String pkgName);

	
}
