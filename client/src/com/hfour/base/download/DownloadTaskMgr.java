package com.hfour.base.download;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.hfour.base.device.ClientInfo;
import com.hfour.base.utils.FileUtils;
import com.hfour.nearplayer.net.data.GameBean;

public class DownloadTaskMgr {
	/**下载的游戏*/
	private HashMap<String, DownloadTask> loadGametasks;
	/**下载模块的状态监听，通下载线程交互*/
	private DownloadState loadListener;
	/**下载模块的handler，和UI交互**/
	private Handler loadHandler;
	/**通知UI下载的状态*/
	private HashSet<UIDownLoadListener> uiListners;
	private HashSet<UIDownLoadListener> loopListners; //专门用户循环
	private long lastRefreshUI = 0;
	private static DownloadTaskMgr instance;
	
	private DownloadTaskMgr(){
		//不允许外部实例化
		initDownloadAppMode();
	}
	/**
	 * 只通知UI,不做其他逻辑处理
	 * @param state
	 * @param errorCode
	 * @param pkgName
	 */
	private void notifyUIDownloadState(int state, int errorCode, String pkgName){
		if(null == uiListners || 0 == uiListners.size()){
			return;
		}
		if(null == loopListners){
			loopListners = new HashSet<UIDownLoadListener>();
		}
		loopListners.clear();
		loopListners.addAll(uiListners);
		
		for(UIDownLoadListener listener:loopListners){
			listener.handleDownloadState(state, errorCode, pkgName);
		}
		//最后通知的时间
		lastRefreshUI = System.currentTimeMillis();
		loopListners.clear();
	}
	
	
	/**
	 * 提供 appManagerCenter，静默安装的时候，状态变化刷新
	 */
	public void notifyRefreshUI(int state){
		notifyDLTaskUIMsgToHandler(state, null, 0);
	}
	
	private void notifyDLTaskUIMsgToHandler(int state, String pkgname, int errorCode){
		notifyDLTaskUIMsgToHandler(state, pkgname, errorCode, 0);
	}
	/**
	 * 通知task的状态给Handler，发送给UI的监听.只通知UI,不做其他逻辑处理
	 * @param state
	 * @param pkgname
	 * @param errorCode
	 */
	private synchronized void notifyDLTaskUIMsgToHandler(int state, String pkgname, int errorCode, long delayMillis){
		Message msg = Message.obtain();
		msg.what = state;
		msg.arg1 = errorCode;
		msg.obj = pkgname;
		
		loadHandler.sendMessageDelayed(msg, delayMillis);
	}
	
	public static DownloadTaskMgr getInstance(){
		if(null == instance){
			instance = new DownloadTaskMgr();
		}
		return instance;
	}
	/**
	 * 初始化洗澡模块,UI线程调用
	 */
	private void initDownloadAppMode(){
		//下载模块的handler，和UI交互
		loadHandler = new Handler(){
			public void handleMessage(Message msg) {
				loadHandler.removeMessages(DownloadState.STATE_DOWNLOAD_UPDATE_PROGRESS); //避免过度刷屏
				notifyUIDownloadState(msg.what, msg.arg1, (String) msg.obj);
			};
		};
		
		//下载模块的状态监听，通下载线程交互
		loadListener = new DownloadState() {
			@Override
			public void onDownloadState(int state, String pkgname, int errorCode) {
				DownloadTask task = loadGametasks.get(pkgname);
				
				if(null == task){
					return;
				}
				//先更新task的状态，后续执行，会和状态相关
				task.gameDownloadState = state;
				
				if(DownloadState.STATE_DOWNLOAD_SUCESS == state){
					task.resetDownloadRunnable();//移除下载任务线程，否则pause全部的时候，状态会被置成暂停
					if(checkDownloadSucess(pkgname)){
					}else{
						//下载成功后，发现文件出错，重新下载
						task.gameDownloadState = DownloadState.STATE_DOWNLOAD_ERROR;
						startDownload(task.loadGame);
						return;
					}
				}else if((DownloadState.STATE_DOWNLOAD_PAUSE == state) || (DownloadState.STATE_DOWNLOAD_ERROR == state)){
					//下载任务线程被结束或暂停了
					task.resetDownloadRunnable();
					if((DownloadState.ERROR_CODE_TIME_OUT == errorCode) || (DownloadState.ERROR_CODE_HTTP == errorCode)
							|| (DownloadState.ERROR_CODE_URL_ERROR == errorCode)){
						//如果是超时，或者网络连接失败，重试
						task.gameDownloadState = state;
						startDownload(task.loadGame);
						
						return;
					}
					
				}
				notifyDLTaskUIMsgToHandler(state, pkgname, errorCode);
			}

			@Override
			public void updateDownloadProgress(String pkgname,
					int downloadFileSize, int downloadPosition) {
				//task的更新进度
				DownloadTask task = loadGametasks.get(pkgname);
				if(null == task){
					return;
				}
				//判断刷新的频率，防止过度刷屏
				long current = System.currentTimeMillis();
				if((current - lastRefreshUI) < 1000){
					//如果900ms内已经刷新
				}else {
					notifyDLTaskUIMsgToHandler(DownloadState.STATE_DOWNLOAD_UPDATE_PROGRESS, pkgname, DownloadState.ERROR_NONE);
				}
				
				task.setDownloadSize(downloadFileSize, downloadPosition);
			}
		};
		//下载游戏的数据池
		loadGametasks = new HashMap<String, DownloadTask>();
	}
	
	
	/**
	 * 查看网络状态，是否有SD卡
	 * @param force
	 * @return 错误值 : DownloadState.ERROR_CODE_NO_NET, DownloadState.ERROR_CODE_NOT_WIFI,DownloadState.ERROR_CODE_NO_SDCARD
	 */
	private int checkNetAndSpace(){
		int netType = ClientInfo.networkType;
		int errorCode = DownloadState.ERROR_NONE;
		
		if (netType == ClientInfo.NONET) {
			// 通知网络错误
			errorCode = DownloadState.ERROR_CODE_NO_NET;
		} else if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			// 通知无SD卡
			errorCode = DownloadState.ERROR_CODE_NO_SDCARD;
		}
		return errorCode;
	}
	
	private DownloadTask newDownloadTask(GameBean game){
		DownloadTask loadGameTask = null;
		
		String pkgnameString = game.getGamePkgName();
		//新的下载
		DownloadTask.deleteTmpDownloadFile(game.getGamePkgName());
		loadGameTask = new DownloadTask(game);			
		loadGametasks.put(pkgnameString, loadGameTask);
		return loadGameTask;
	}
	
	/**
	 * 下载接口
	 * @param game
	 * @param force
	 * @return 
	 */
	public void startDownload(GameBean game){
		DownloadTask loadGameTask = null;
		if(null == game || null == game.getGamePkgName() || null == game.getDownloadUrl()){
			return;
		}
		String pkgnameString = game.getGamePkgName();
		
		synchronized(loadGametasks){
			loadGameTask = loadGametasks.get(pkgnameString);
			
			//check net and space
			int checkResult = checkNetAndSpace();
			if(DownloadState.ERROR_NONE == checkResult){
				
			}else {
				//生成下载任务
				if(null == loadGameTask){
					loadGameTask = newDownloadTask(game);
				}
				//通知错误
				notifyDLTaskUIMsgToHandler(DownloadState.STATE_DOWNLOAD_ERROR, pkgnameString, checkResult);
				
				return;
			}
			
			//网络正常情况下
			if(null == loadGameTask){
				//新的下载
				loadGameTask = newDownloadTask(game);
			}else {
				//continue download.
				if(DownloadState.STATE_DOWNLOAD_SUCESS == loadGameTask.gameDownloadState){
//					JLog.info("startDownload DownloadState.STATE_DOWNLOAD_SUCESS is "+loadGameTask.loadgame.getGamePkgName());
					//下载的版本已经最新，并已下载成功.
					//判断文件是否存在，如果不存在，重新下载. -----用户删除了文件，或者更换了SD卡
					File apkFile = new File(FileUtils.getGameAPKFilePath(loadGameTask.loadGame.getGamePkgName()));
					if(apkFile.exists()){
						AppManagerCenter.installGameApk(loadGameTask.loadGame);
						notifyDLTaskUIMsgToHandler(DownloadState.STATE_DOWNLOAD_SUCESS, loadGameTask.loadGame.getGamePkgName(), 0, 1000);
						return;
					}else{
						loadGameTask.gameDownloadState = DownloadState.STATE_DOWNLOAD_ERROR;
					}
				}
			}
			//start task
			startDownloadTask(loadGameTask);
			
			return;
		}
	}
	
	
	
	/**
	 * cancel downloadtask and delete from database
	 * @param game
	 */
	public void cancelDownload(GameBean game){
		if(null == game || null == game.getGamePkgName()){
			return;
		}
		
		DownloadTask loadGameTask = removeTask(game.getGamePkgName());
		if(null != loadGameTask){
			//stop task
			loadGameTask.cancelTask();
			notifyDLTaskUIMsgToHandler(DownloadState.STATE_DOWNLOAD_CANCEL, game.getGamePkgName(), 0);
		}
	}
	/**
	 * 删除下载任务，和cancel的区别，直接删除，不通知
	 * @param game
	 */
	public void deleteDownloadTask(GameBean game) {
		if(null == game || null == game.getGamePkgName()){
			return;
		}
		
		DownloadTask loadGameTask = removeTask(game.getGamePkgName());
		if(null != loadGameTask){
			loadGameTask.cancelTask();
		}
	}
	/**
	 * pause Download ，停止下载，必须把执行线程置成null
	 * @param game
	 */
	public void pauseDownload(GameBean game, boolean isUser){
		if(null == game || null == game.getGamePkgName()){
			return;
		}
		DownloadTask loadGameTask = loadGametasks.get(game.getGamePkgName());
		//pause task
		pauseDownloadTask(loadGameTask, isUser);
	}
	/**
	 * 
	 * @param loadGameTask
	 * @param isUser
	 */
	private void pauseDownloadTask(DownloadTask loadGameTask, boolean isUser){
		if(null == loadGameTask){
			return;
		}
		loadGameTask.pauseTask(isUser);
	}
	
	/**
	 * 继续所有下载
	 */
	public void continueAllDownload() {
		if(null == loadGametasks || 0 == loadGametasks.size()){
			return;
		}
		
		int checkResult = checkNetAndSpace();
		if(DownloadState.ERROR_NONE == checkResult){
			
		}else {
			//通知错误
			return;
		}
		
		synchronized(loadGametasks){
			Iterator<Entry<String, DownloadTask>> ite = loadGametasks.entrySet().iterator();
			Entry<String, DownloadTask> entity = null;
			DownloadTask task = null;
			while(ite.hasNext()){
				entity = ite.next();
				task = entity.getValue();
				if(DownloadState.STATE_DOWNLOAD_SUCESS != task.gameDownloadState){
					//只要还没下载完成，或者安装的，都恢复下载
					if(task.isUserPause()){
						
					}else {
						startDownloadTask(task);
					}
				}
			}
		}
	}
	/**
	 * 启动下载
	 * @param task
	 */
	private void startDownloadTask(DownloadTask task){
		if(null == task){
			return;
		}
		task.startTask(loadListener);
	}
	/**
	 * 停止所有下载
	 */
	public void pauseAllDownload() {
		if(null == loadGametasks || 0 == loadGametasks.size()){
			return;
		}
		synchronized(loadGametasks){
			Iterator<Entry<String, DownloadTask>> ite = loadGametasks.entrySet().iterator();
			Entry<String, DownloadTask> entity = null;
			DownloadTask task = null;
			while(ite.hasNext()){
				entity = ite.next();
				task = entity.getValue();
				pauseDownloadTask(task, task.isUserPause());
			}
		}
	}
	/**
	 * 判断是否有正在下载的任务
	 */
	public boolean hasDownloadingTask() {
		if(null == loadGametasks || 0 == loadGametasks.size()){
			return false;
		}
		synchronized(loadGametasks){
			Iterator<Entry<String, DownloadTask>> ite = loadGametasks.entrySet().iterator();
			Entry<String, DownloadTask> entity = null;
			DownloadTask task = null;
			while(ite.hasNext()){
				entity = ite.next();
				task = entity.getValue();
				if(task.taskIsLoading()){
					return true;
				}
			}
		}
		
		return false;
	}

	public int getDownloadProgress(String pkgName) {
		DownloadTask task = loadGametasks.get(pkgName);
		if(null == task){
			return 0;
		}
		return task.progress;
	}

	public void setUIDownloadListener(UIDownLoadListener refreshHanle) {
		if(null == uiListners){
			uiListners = new HashSet<UIDownLoadListener>();
		}
		uiListners.add(refreshHanle);
	}

	public  void removeUIDownloadListener(UIDownLoadListener refreshHanle) {
		if(null == uiListners){
			return;
		}
		uiListners.remove(refreshHanle);
	}
	
	public GameBean getDownloadGameByPkgname(String pkgName){
		DownloadTask task = loadGametasks.get(pkgName);
		if(null == task){
			return null;
		}
		return task.loadGame;
	}
	/**
	 * 下载，并安装成功游戏
	 * @param packageName
	 */
	public void installedGame(String packageName) {
		DownloadTask task = loadGametasks.get(packageName);
		if(null == task){
			return;
		}
		notifyDLTaskUIMsgToHandler(DownloadState.STATE_DOWNLOAD_INSTALLED, packageName, DownloadState.ERROR_NONE);
	}
	
	private DownloadTask removeTask(String packageName){
		if(null == packageName){
			return null;
		}
		DownloadTask task = null;
		synchronized(loadGametasks){
			task = loadGametasks.remove(packageName);
			//remove from DB
			return task;
		}
	}
	
	public DownloadTask getDownloadTask(String pkgName){
		return loadGametasks.get(pkgName);
	}
	
	public boolean DownloadTaskCotain(String pkgName){
		return (loadGametasks.get(pkgName) != null);
	}
	
	private boolean checkDownloadSucess(String pkgname){
		if(null == pkgname){
			return false;
		}
		DownloadTask task = loadGametasks.get(pkgname);
		if(null == task){
			return false;
		}
		// 下载成功，rename tmp download file
		try {
			// 文件重命名
			String pkgName = task.loadGame.getGamePkgName();
			
			if(renameDownloadGameAPP(pkgName)){
				AppManagerCenter.installGameApk(task.loadGame);
				return true;
			}else{
				//文件意外删除了
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 将临时文件重命名
	 * 
	 * @param gameCode
	 * @return
	 * @throws IOException
	 */
	private boolean renameDownloadGameAPP(String gameCode)
			throws IOException {
		File oldfile = new File(FileUtils.getDownloadTmpFilePath(gameCode));
		File newfile = new File(FileUtils.getGameAPKFilePath(gameCode));

		if (newfile.exists()) {
			newfile.delete();
		}

		if (oldfile.exists()) {
			oldfile.renameTo(newfile);
			return true;
		} else {
			//文件意外删除了
			return false;
		}
	}
}
