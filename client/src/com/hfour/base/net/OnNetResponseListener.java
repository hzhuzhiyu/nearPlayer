package com.hfour.base.net;

/**
 * 网络数据监听<br/>
 * 网络层由此数据层发送消息，注意此时并未在UI线程
 */
public interface OnNetResponseListener<T> {

	/***
	 * 网络层由此数据层发送消息，注意此时并未在UI线程<br/>
	 * {@link #WHAT_SUCCESS} 成功<br/>
	 * {@link #WHAT_NETERR} 失败 <br/>
	 * {@link #WHAT_NOT_LOGIN} Session 过期
	 * 
	 * @param what : WHAT_SUCCESS
	 * @param t :resp data
	 * @param error: 服务器返回的错误字符
	 */
	public void sendNetResponseMsg(int what, T data, String error);
}
