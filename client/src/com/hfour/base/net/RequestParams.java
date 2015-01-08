package com.hfour.base.net;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.hfour.base.device.ClientInfo;
import com.hfour.base.utils.MLog;



/**
 * 基本的请求信息
 * @author Tony
 *
 */
public class RequestParams{
	/**NetConstants.METHOD_GET, NetConstants.METHOD_POST*/
	private int method; 
	/**请求地址*/
	private String Url;
	/**post  的数据*/
	private HashMap<String, Object> params;
	
	
	public RequestParams(int httpMethod){
		method = httpMethod;
	}
	
	public int getMethod() {
		return method;
	}


	public String getUrl() {
		MLog.info("getUrl = "+Url);
		return Url;
	}
	/**
	 * 注意,先设置URL,再设置参数,GET的时候,需要对URL进行修改
	 * @param url
	 */
	public void setUrl(String url) {
		Url = url;
	}

	public HashMap<String, Object> getParams() {
		return params;
	}

	public void setParams(HashMap<String, Object> params) {
		if(null == params){
			params = new HashMap<String, Object>();
		}
		
		if(NetConstants.METHOD_GET == method){
			StringBuilder sBuilder = new StringBuilder(Url);
			//常量参数添加
			sBuilder.append("?os=android");
			sBuilder.append("&id=");
			sBuilder.append(ClientInfo.getInstance().channelCode); //渠道号
			
			Object value = null;
			String valueEncode = null;
			for(Map.Entry<String, Object> e: params.entrySet()){
				value = e.getValue();
				if(value != null){
					sBuilder.append("&");
					sBuilder.append(e.getKey());
					sBuilder.append("=");
					try {
						//中文情况，进行编码
						valueEncode = URLEncoder.encode(String.valueOf(value) ,"UTF-8");
					} catch (Exception e1) {
					}
					sBuilder.append(valueEncode);
					valueEncode = null;
				}
			}
			Url = sBuilder.toString();
			MLog.info("GET URL is  "+ Url);
		}else{
			params.put("os", "android");
			params.put("id", ClientInfo.getInstance().channelCode); // 渠道号
			
			this.params = params;
			MLog.info("POST params = "+params.toString());
		}
	}
}
