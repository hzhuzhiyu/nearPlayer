package com.hfour.nearplayer.net.data;

public class GameBean {
	private String gamePkgName;
	private String downloadUrl;
	private int gameSize;

	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getGamePkgName() {
		return gamePkgName;
	}
	public void setGamePkgName(String gamePkgName) {
		this.gamePkgName = gamePkgName;
	}
	public int getGameSize() {
		return gameSize;
	}
	public void setGameSize(int gameSize) {
		this.gameSize = gameSize;
	}
}
