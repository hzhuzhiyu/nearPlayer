package com.hfour.base.net;

import java.io.Serializable;

/**
 * 用户手机基本信息，即UA
 * @author jiangdehua 
 * @version 1.0 2013-2-4
 *
 */
public class UserAgent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4185384665421644824L;

	private String imsi;
	
	private String imei;
	
	private String androidSystemVer;
	
	private String screenSize;
	
	private Integer ramSize;
	
	private Integer romSize;
	
	private String cpu;
	
	private String hsman;
	
	private String hstype;
	
	private Byte networkType;
	
	private String provider;
	
	private String channelCode;
	
	private String packegeName;//游戏平台包名，可能存在不同的渠道包名不一致
	
	private String apkVer;//游戏平台版本
	
	private Short dpi;//屏幕密度
	
	private Integer apkverInt;//游戏平台版本Integer类型，优先通过这个匹配,老版本为null
	
	private String mac;//2013-05-10
	
	private String terminalId;//终端标识，没有传null 2013-09
	
	private Long firstVisitTime;//第一次访问时间,单位毫秒

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getAndroidSystemVer() {
		return androidSystemVer;
	}

	public void setAndroidSystemVer(String androidSystemVer) {
		this.androidSystemVer = androidSystemVer;
	}

	public String getScreenSize() {
		return screenSize;
	}

	public void setScreenSize(String screenSize) {
		this.screenSize = screenSize;
	}

	public Integer getRamSize() {
		return ramSize;
	}

	public void setRamSize(Integer ramSize) {
		this.ramSize = ramSize;
	}

	public Integer getRomSize() {
		return romSize;
	}

	public void setRomSize(Integer romSize) {
		this.romSize = romSize;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getHsman() {
		return hsman;
	}

	public void setHsman(String hsman) {
		this.hsman = hsman;
	}

	public String getHstype() {
		return hstype;
	}

	public void setHstype(String hstype) {
		this.hstype = hstype;
	}

	public Byte getNetworkType() {
		return networkType;
	}

	public void setNetworkType(Byte networkType) {
		this.networkType = networkType;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getPackegeName() {
		return packegeName;
	}

	public void setPackegeName(String packegeName) {
		this.packegeName = packegeName;
	}

	public String getApkVer() {
		return apkVer;
	}

	public void setApkVer(String apkVer) {
		this.apkVer = apkVer;
	}
	
	public Short getDpi() {
		return dpi;
	}

	public void setDpi(Short dpi) {
		this.dpi = dpi;
	}

	public Integer getApkverInt() {
		return apkverInt;
	}

	public void setApkverInt(Integer apkverInt) {
		this.apkverInt = apkverInt;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public Long getFirstVisitTime() {
		return firstVisitTime;
	}

	public void setFirstVisitTime(Long firstVisitTime) {
		this.firstVisitTime = firstVisitTime;
	}
}
