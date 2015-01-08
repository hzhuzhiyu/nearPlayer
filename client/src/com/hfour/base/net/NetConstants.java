package com.hfour.base.net;

/**
 * 网络常量<br>
 * 注意：取值1000以内
 * @author Tony
 *
 */
public class NetConstants {
	/** 成功 */
	public static final int WHAT_SUCCESS = 200;
	/** 网络失败 */
	public static final int WHAT_NETERR = 101;
	/**服务器返回失败*/
	public static final int WHAT_RESP_ERR = 110;
	/**session 过期*/
	public static final int WHAT_RESP_SESSION_TIMEOUT = 120;
	
	
	//请求方式
	public static final int METHOD_GET = 0;
	public static final int METHOD_POST = 1;
	public static final int METHOD_DELETE = 2;
	public static final int METHOD_PUT = 3;
	
	/**业务域名*/
	//现网地址
	public static final String AWIFI_APP_SERVER = "http://service.iwifi.9conn.net/";
	
}
