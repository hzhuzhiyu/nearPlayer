package com.hfour.base.utils;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.hfour.base.parser.BaseParserBean;
import com.hfour.base.parser.InputStreamParser;
import com.hfour.base.parser.OutputStreamParse;

/**
 * Http 工具类
 * 
 * 
 */
public class HttpUtils {

	/**
	 * post 请求
	 * 
	 * @param url
	 * @param params
	 * @param parser
	 * @return
	 */
	public static BaseParserBean post(String url,
			ArrayList<BasicNameValuePair> parms,
			InputStreamParser<? extends BaseParserBean> parser) {
		try {
			return basePost(url, new UrlEncodedFormEntity(parms, HTTP.UTF_8),
					parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * post 请求
	 * 
	 * @param url
	 * @param parms
	 * @param parser
	 * @return
	 */
	public static BaseParserBean post(String url, InputStreamEntity parms,
			InputStreamParser<? extends BaseParserBean> parser) {
		return basePost(url, parms, parser);
	}

	/**
	 * post 请求
	 * 
	 * @param url
	 * @param params
	 * @param parser
	 * @return
	 */
	public static BaseParserBean basePost(String url, HttpEntity params,
			InputStreamParser<? extends BaseParserBean> parser) {
		InputStream inputStream = null;
		HttpClient client = null;
		try {
			// 请求参数
			HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter("charset", HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(httpParams, 8 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 8 * 1000);

			client = new DefaultHttpClient(httpParams);
			HttpPost post = new HttpPost(url);
			post.addHeader("charset", HTTP.UTF_8);
			post.setEntity(params);

			HttpResponse response = client.execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity entity = response.getEntity();
				inputStream = entity.getContent();
				return parser.parser(inputStream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @return
	 */
	public static Object get(String url,
			InputStreamParser<? extends Object> parser) {
		if (url == null) {
			return null;
		}
		Object reslut = null;
		InputStream inputStream = null;
		HttpClient httpclient = null;
		try {
			HttpGet httpRequest = new HttpGet(url);
			httpRequest.setHeader("Connection", "Keep-Alive");
			httpclient = HttpClientUtils.getHttpClient();
			HttpResponse response = (HttpResponse) httpclient
					.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			reslut = parser.parser(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}
		return reslut;
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 */
	public static boolean upload(String url, Object data,
			OutputStreamParse<byte[]> parse) {
		DataInputStream dis = null;
		OutputStream os = null;
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url)
					.openConnection();
			con.setReadTimeout(20 * 1000);
			con.setConnectTimeout(20 * 1000);
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(true);

			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=******");

			/* 设置OutputStream */
			os = con.getOutputStream();
			parse.parser(os, (byte[]) data);
			os.flush();
			// 请求成功
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// 获取服务器返回的数据
				dis = new DataInputStream(con.getInputStream());
			}

			if (dis != null) {
				byte result = dis.readByte();
				if (result == 1) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dis != null)
					dis.close();
			} catch (Exception e) {
			} finally {
				try {
					if (os != null)
						os.close();
				} catch (Exception e) {
				}
			}
		}
		return false;
	}
}
