package com.hfour.base.net;

import com.hfour.base.device.ClientInfo;
import com.hfour.base.net.NetSourceTask.OnReslutListener;
import com.hfour.base.threads.DataSourceThreadPool;
import com.hfour.base.utils.MLog;
/**
 * 
 * @author Tony
 *
 * @param <Q> 返回的数据类型，用于json的反序列化
 */
public abstract class AbstractNetSource<T extends BaseReq, Q extends BaseResp>
		implements OnReslutListener {

	private static DataSourceThreadPool mMessageManager;
	private OnNetResponseListener<Q> listener = null;
	/***是否正在请求*/
	private boolean isRequesting = false;
	private NetSourceTask requstingTask;
	private int httpMethod;
	private static UserAgent ua;
	
	public AbstractNetSource(OnNetResponseListener<Q> dataListener, int httpMethod){
		listener = dataListener;
		this.httpMethod = httpMethod;
	}
	
	/**取消请求*/
	public void cancelRequest(){
		if(null != requstingTask){
			requstingTask.cancelRequest();
		}
		requstingTask = null;
		listener = null;
		requestDone();
	}

	/** 请求URL，如果需要添加URL参数，在此处添加 */
	public abstract String getUrl();

	public abstract Q parsResp(String jsonString);
	protected Q respData = null;

	/**
	 * 请求结束
	 */
	private void requestDone(){
		MLog.info("AbstractNetSource requestDone");
		isRequesting = false;
		requstingTask = null;
	}
	
	@Override
	public final void onSucess(byte[] bytes) {
		requestDone();
		if (null != listener) {
			if(null == bytes || 0 == bytes.length){
				//failed
				onFailed(NetConstants.WHAT_RESP_ERR, null);
				return;
			}
			
			String respString = new String(bytes); //json字符串
			MLog.info("resp json string : "+respString);
			respData = parsResp(respString); //解析出baseResp的值
			if(null == respData){
				onFailed(NetConstants.WHAT_RESP_ERR, null);
			}else {
				if(NetConstants.WHAT_SUCCESS == respData.code){//根据返回的code判断失败还是成功
					if (null != listener) {
						listener.sendNetResponseMsg(NetConstants.WHAT_SUCCESS, respData, null);
					}
				}else if(NetConstants.WHAT_RESP_SESSION_TIMEOUT == respData.code){
					onFailed(NetConstants.WHAT_RESP_SESSION_TIMEOUT, null);
				}else {
					//failed
					onFailed(NetConstants.WHAT_RESP_ERR, respData.err);
				}
			}
		}
	}

	@Override
	public final void onFailed(int errorId, String error) {
		requestDone();
		if (null != listener) {
			listener.sendNetResponseMsg(errorId, null, error);
		}
	}

	/**
	 * msg.what 取值范围
	 * 
	 * @param handler
	 */
	public void setListener(OnNetResponseListener<Q> listener) {
		this.listener = listener;
	}

	public final void doRequest() {
		if(isRequesting){
			return;
		}
		
		mMessageManager = DataSourceThreadPool.getInstance();
		isRequesting = true;
		T reqParams = getRequest();
		if(null == ua){
			ua = getUA();
		}
		reqParams.setUserAgent(ua);
		requstingTask = new NetSourceTask(getUrl(), httpMethod, this, reqParams);
		mMessageManager.execute(requstingTask);
		MLog.info("AbstractNetSource doRequest =" +getUrl());
	}


	public abstract T getRequest();

	public final Q getData() {
		return respData;
	}


	public static void onStop(){
		if(mMessageManager != null){
			mMessageManager.stop();
			mMessageManager = null;
		}
	}
	
	private final UserAgent getUA() {
		if (ua == null) {
			ua = new UserAgent();
			ClientInfo clientInfo = ClientInfo.getInstance();
			ua.setAndroidSystemVer(clientInfo.androidVer);
			ua.setApkVer(clientInfo.apkVerName);
			ua.setApkverInt(clientInfo.apkVerCode);
			ua.setCpu(clientInfo.cpu);
			ua.setHsman(clientInfo.hsman);
			ua.setHstype(clientInfo.hstype);
			ua.setImei(clientInfo.imei);
			ua.setImsi(clientInfo.imsi);
			ua.setNetworkType(ClientInfo.networkType);
			ua.setPackegeName(clientInfo.packageName);
			ua.setProvider(clientInfo.provider);
			ua.setChannelCode(clientInfo.channelCode);
			ua.setRamSize(clientInfo.ramSize);
			ua.setRomSize(clientInfo.romSize);
			ua.setScreenSize(clientInfo.screenSize);
			ua.setDpi(clientInfo.dpi);
			ua.setMac(clientInfo.mac);
		}else {
			ua.setNetworkType(ClientInfo.networkType); //网络类型经常发生变化,每次要重新设置
		}
		
		return ua;
	}
}
