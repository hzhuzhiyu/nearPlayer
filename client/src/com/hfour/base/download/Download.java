package com.hfour.base.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import com.hfour.base.utils.FileUtils;

public class Download implements Runnable {
	/** 下载缓冲区 */
	private static final int BUFFER_LEN = 1024;
	/** 更新数据库的频率 */
	private static final int UPDATE_SQL_NUM = 100;

	private File downloadTmpFile; // 下载文件备份
	private String downloadFilePath;
	private String downloadUrl;
	private String downloadPkgName;
	private DownloadClient mHttpClient;
	
	/*** 当前下载的位置 */
	private int downloadPosition;

	/** * 游戏的APK大小 */
	private int downloadFileSize;

	private boolean isRunning = false;

	private int downloadResult = 0;

	private DownloadState backListener;
	/**下载状态*/
	private int downloadState;

	public Download(String loadUrl, String pkgName,int apkSize,
			DownloadState listener) {
		downloadUrl = loadUrl;
		backListener = listener;
		downloadPkgName = pkgName;
		downloadFileSize = apkSize;
		downloadState = DownloadState.STATE_DOWNLOAD_WAIT;
	}
	/**
	 * 状态置成等待下载，并通知UI，下载准备中
	 */
	public void readyDownload(){
		notifyDownloadState(DownloadState.STATE_DOWNLOAD_WAIT);
	}
	
	public long getDownloadPosition() {
		return downloadPosition;
	}

	private void notifyDownloadState(int state){
		if(null != backListener){
			backListener.onDownloadState(state, downloadPkgName, downloadResult);
		}
		
		downloadState = state;
	}
	/**
	 * 停止当前任务
	 * @param result ：停止当前任务的原因
	 */
	public void stopDownloadByResult(int result) {
		isRunning = false;
		//只有是未知错误的时候，设置成错误下载，其他情况只是暂停。错误下载，会删除原数据重新下载
		if(DownloadState.ERROR_CODE_UNKOWN == result){
			notifyDownloadState(DownloadState.STATE_DOWNLOAD_ERROR);
		}else{
			notifyDownloadState(DownloadState.STATE_DOWNLOAD_PAUSE);
		}
		downloadResult = result;		
	}

	public void setDownloadResult(int result) {
		this.downloadResult = result;
	}
	
	@Override
	public void run() {
		if(DownloadState.STATE_DOWNLOAD_WAIT != downloadState){
			return;
		}
		if (isRunning) {
			return;
		}
		isRunning = true;
		notifyDownloadState(DownloadState.STATE_DOWNLOAD_START_LOADING);
		//通知下载开始
		mHttpClient = new DownloadClient(downloadUrl);

		try {
			// 创建临时文件*.jolo
			downloadFilePath = FileUtils.getDownloadTmpFilePath(downloadPkgName);
			downloadTmpFile = new File(downloadFilePath); // 下载的临时文件
			if (downloadTmpFile.exists()) {
				// 如果文件已经存在,断点续传
				downloadPosition = (int) downloadTmpFile.length();
			} else {
				File parentFile = downloadTmpFile.getParentFile();
				if (null != parentFile && !parentFile.exists()) {
					parentFile.mkdirs();
				}
				downloadTmpFile.createNewFile();
			}
			// 开始下载
			download(downloadPosition);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			stopDownloadByResult(DownloadState.ERROR_CODE_TIME_OUT);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			stopDownloadByResult(DownloadState.ERROR_CODE_HTTP);
		} catch (IOException e) {
			e.printStackTrace();
			stopDownloadByResult(DownloadState.ERROR_CODE_IO);
		} catch (Exception e) {
			e.printStackTrace();
			stopDownloadByResult(DownloadState.ERROR_CODE_UNKOWN);
		} finally {
			if (mHttpClient != null) {
				mHttpClient.close();
				mHttpClient = null;
			}
		}
	}

	/**
	 * 检查SD是否有足够空间
	 * 
	 * @param loadSize
	 * @return
	 */
	private boolean checkSDSpaceIsEnough(long loadSize) {
		if (FileUtils.getSDAvailaleSize() > (loadSize * 2)) {
			return true;
		}
		return false;
	}

	/**
	 * 断点续传
	 * 
	 * @param serverPos
	 * @throws SocketTimeoutException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void download(int serverPos) throws SocketTimeoutException,
			MalformedURLException, IOException {
		InputStream inputStream = null;
		RandomAccessFile randomAccessFile = null;
		
		try {
			inputStream = mHttpClient.getInputStream(serverPos);
		} catch (Exception e) {
		}
		
		try {
			randomAccessFile = new RandomAccessFile(downloadTmpFile, "rw");
			int size = mHttpClient.getContentLength();
			if(size > 0){
				downloadFileSize = size;
			}
			
			if (!checkSDSpaceIsEnough(downloadFileSize)) {
				// 空间不足
				stopDownloadByResult(DownloadState.ERROR_CODE_SD_NOSAPCE);
				return;
			}

			downloadFileSize = downloadFileSize + serverPos; // 文件是实际大小

			byte[] buf = new byte[BUFFER_LEN];// 从服务端读取的byte流
												// //缓存
			int len;// 从服务端读取的byte长度
			randomAccessFile.seek(downloadPosition);
			randomAccessFile.setLength(downloadPosition);
			
			int nowDownModNum = 0;
			while (isRunning && (-1 != (len = inputStream.read(buf)))) {
				randomAccessFile.write(buf, 0, len);
				downloadPosition += len;

				if (nowDownModNum == UPDATE_SQL_NUM) {
					nowDownModNum = 0;
					if(null != backListener){
						backListener.updateDownloadProgress(downloadPkgName, downloadFileSize, downloadPosition);
					}
				}
				nowDownModNum++;
			}
		} catch (IOException e) {
			
		} finally {
			try {
				if (randomAccessFile != null){
					randomAccessFile.close();
				}
			} catch (Exception ex) {
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (Exception exp) {
				}
			}
		}

		if (isRunning) {
			if (downloadPosition == downloadFileSize) {
				//download success
				backListener.updateDownloadProgress(downloadPkgName, downloadFileSize, downloadPosition);
				notifyDownloadState(DownloadState.STATE_DOWNLOAD_SUCESS);
			}
		}
	}

}
