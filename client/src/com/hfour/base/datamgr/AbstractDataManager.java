package com.hfour.base.datamgr;

import android.os.Handler;
import android.os.Message;

import com.hfour.base.net.NetConstants;
import com.hfour.base.uiutils.UIUtils;
import com.hfour.nearplayer.R;


/**
 * 所有的dataManager都必须继承该类<br>
 * @author Tony
 * 
 */
public abstract class AbstractDataManager {
	protected OnUiDataMgrListener uiCallback;
	protected Handler dataMgrHandler = new Handler(){
		public void handleMessage(Message msg) {
			if(NetConstants.WHAT_RESP_SESSION_TIMEOUT == msg.what){
				//timeout
//				UIUtils.showToast(R.string.login_again);
				UIUtils.gotoLogin();
				return;
			}else{
				if(NetConstants.WHAT_NETERR == msg.what){
					UIUtils.showToast(R.string.net_err);
					msg.what = NetConstants.WHAT_RESP_ERR; //方便UI统一处理
				}
					
				sendMsgToUI(msg.what, msg.arg1, msg.arg2, msg.obj);
			}
		};
	};
	
	public AbstractDataManager(OnUiDataMgrListener cb){
		uiCallback = cb;
	}
	
	/**
	 * 把数据传给UI线程
	 * @param what ：可以自定义的消息
	 * @param obj : 要传递的数据
	 * @param error: 返回的错误信息
	 */
	protected void sendDataToUIThread(int what, Object data, String error){
		Message msg = Message.obtain();
		
		msg.what = what;
		if(null != data){
			msg.obj = data;
		}else{
			msg.obj = error;
		}
		
		dataMgrHandler.sendMessageAtTime(msg, 200);
	}
	
	/**
	 * 处理传入的参数——由网络层返回的消息和数据<br>
	 * 发消息给UI层，仅限于UI线程调用
	 * @param what : 网络层的消息(建议：自定义，用于区别不同协议的消息)
	 */
	protected abstract void sendMsgToUI(int what, int arg1, int arg2, Object obj);
	/**取消网络请求，有些退出后，请求就不必要了*/
	public abstract void cancelNetRequest();
	/**释放资源, activity onDestroy 时必须free, 把注册的回调反注册<br>
	 * 在该方法里，释放相关资源*/
	public void onDestroy(){
		uiCallback = null;
	}
	
	
	public interface OnUiDataMgrListener {
		/**
		 * 用于DataManager 回调
		 * 
		 * @param what
		 * @param arg1
		 * @param arg2
		 * @param obj
		 */
		public void onBack(int what, int arg1, int arg2, Object obj);
	}
}
