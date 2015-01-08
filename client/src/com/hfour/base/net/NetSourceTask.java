package com.hfour.base.net;

import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.hfour.base.utils.HttpClientUtils;
import com.hfour.base.utils.MLog;
/**
 * 请求网络的任务，通过GET OR POST的方法，获取数据，并把数据返回
 * @author Tony
 */
public class NetSourceTask implements
		Runnable {
	private OnReslutListener listener = null;
	private BaseReq requestParams = null;
	private String url;
	/**是否有效*/
	private boolean isValid = true;
	private int httpMethod;

	private static final int MAX_TIMEOUT_TIME = 60*1000;
	private static final String GZIP_ENCODING = "gzip";

	public NetSourceTask(String url, int method, OnReslutListener listener, BaseReq reqParams) {
		this.listener = listener;
		this.url = url;
		this.requestParams = reqParams;
		this.httpMethod = method;
		isValid = true;
	}
	
	
	private byte[] getMethod() {
		HttpGet httpRequest = new HttpGet(url);

		return doHttpRequest(httpRequest);
	}
	
	private byte[] postMethod() {
		HttpPost httpRequest = new HttpPost(url);
		HttpEntity entity = null;
		
		try {
			String postContent = JSON.toJSONString(requestParams);
			MLog.info("postMethod json string : "+postContent+", url = "+url);
			httpRequest.setEntity(new StringEntity(postContent));
		} catch (Exception e) {
		}
		
		httpRequest.setEntity(entity);
		httpRequest.setHeader("content-type", "application/json");
		
		return doHttpRequest(httpRequest);
	}

	private byte[] doHttpRequest(HttpUriRequest httpRequest){
		if(null == httpRequest){
			return null;
		}
		
		httpRequest.setHeader("Accept-Encoding", GZIP_ENCODING);
		httpRequest.setHeader("Connection", "Keep-Alive");
		
		HttpClient httpClient = HttpClientUtils.getHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, MAX_TIMEOUT_TIME);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				MAX_TIMEOUT_TIME);
		HttpResponse httpResponse = null;
		int status = -1;
		GZIPInputStream inPutStream = null;
		ByteArrayInputStream gzipByteStream = null;
		
		try {
			httpResponse = httpClient.execute(httpRequest);
			status = httpResponse.getStatusLine().getStatusCode();
			MLog.info("doHttpRequest status = "+status);
			if (status == 200) {
				/** 判断是否是GZIP **/
				boolean isGzipEncoding = false;
				
				// 读取数据
				HttpEntity entity = httpResponse.getEntity();
				Header header = entity.getContentEncoding();
				if(null != header){
					String contentEncoding = header.getValue();
					if((null != contentEncoding) && contentEncoding.contains(GZIP_ENCODING)){
						isGzipEncoding = true;
					}
				}

				byte[] entityBytes = EntityUtils.toByteArray(entity);

				if (isGzipEncoding) {
					// 如果是GZIP压缩
					gzipByteStream = new ByteArrayInputStream(entityBytes);
					inPutStream = new GZIPInputStream(gzipByteStream);
					int size = (entityBytes.length << 1);
					ByteArrayBuffer buffer = new ByteArrayBuffer(size);
					byte[] readBuffer = new byte[1024];
					int len = 0;
					while ((len = inPutStream.read(readBuffer)) != -1) {
						buffer.append(readBuffer, 0, len);
					}

					return buffer.toByteArray();
				} else {
					return entityBytes;
				}
			}else {
				httpRequest.abort();
			}
		} catch (Exception e) {
		} finally {
			if (null != inPutStream) {
				try {
					inPutStream.close();
				} catch (Exception e) {
				}
			}

			if (null != gzipByteStream) {
				try {
					gzipByteStream.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}
	
	@Override
	public void run() {
		byte[] result;
		if(!isValid){
			//无效请求了
			MLog.info("NetSourceTask run isValid = false");
			return;
		}
		
		MLog.info("NetSourceTask run" );
		if(httpMethod == NetConstants.METHOD_GET){
			result = getMethod();
		}else {
			result = postMethod();
		}
		synchronized(requestParams){
			if(null != listener){
				if (null == result) {
					listener.onFailed(NetConstants.WHAT_RESP_ERR, null);
				} else {
					listener.onSucess(result);
				}
			}
		}
	}


	/**
	 * 结果监听
	 * 
	 * 
	 */
	public interface OnReslutListener {

		/**
		 * 成功 原始流
		 * 
		 * @param bytes
		 */
		void onSucess(byte[] bytes);

		/**
		 * 失败
		 * @param errorId
		 * @param errorCode
		 */
		void onFailed(int errorID, String errorCode);
	}


	public void cancelRequest() {
		isValid = false;
		synchronized(requestParams){
			listener = null;
		}
	}
}