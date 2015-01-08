package com.hfour.base.net.data;
/**
	{
       "code":200,//200=服务正常，其他服务不正常
       "err":"错误提示信息",
       "result":{
            "upgradeCode":1,//1=不升级 2=提示升级 3=强制升级
            "serverVer":"3.10.0018",//服务端版本号,提示给用户看的版本号
            "serverVerInt":3100000,//服务端版本号 int类型,客户端做参考
            "downloadUrl":"http://cdn.joloplay.cn/a.apk",//下载地址，如果不升级，此项可不填写
            "tip":"您好，版本更新，此版本优化了什么功能..."//提示语     
       }  
     } 
 * @author Tony
 *
 */
public class UpdateVersionBean {
	private int serverVerInt;
	private String downloadUrl;
	private String tip;
	private String serverVer;
	private int upgradeCode;
	
	public int getServerVerInt() {
		return serverVerInt;
	}
	public void setServerVerInt(int serverVerInt) {
		this.serverVerInt = serverVerInt;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
	public String getServerVer() {
		return serverVer;
	}
	public void setServerVer(String serverVer) {
		this.serverVer = serverVer;
	}
	public int getUpgradeCode() {
		return upgradeCode;
	}
	public void setUpgradeCode(int upgradeCode) {
		this.upgradeCode = upgradeCode;
	}
}
