package com.hfour.base.datamgr;

import android.text.TextUtils;

import com.hfour.base.constants.GlobalParams;
import com.hfour.base.net.NetConstants;
import com.hfour.base.net.OnNetResponseListener;
import com.hfour.base.net.UpdateVersionNet;
import com.hfour.base.net.protocols.UpdateVersionRes;
import com.hfour.base.utils.DataStoreUtils;

public class CheckVerisonDataMgr extends AbstractDataManager{
	private UpdateVersionNet updateVerNet;
	
	public CheckVerisonDataMgr(OnUiDataMgrListener cb) {
		super(cb);
		updateVerNet = new UpdateVersionNet(updateDataListener);
	}

	@Override
	protected void sendMsgToUI(int what, int arg1, int arg2, Object obj) {
		if(null == uiCallback){
			return;
		}
		switch (what) {
		case NetConstants.WHAT_SUCCESS:
			if(null != obj){
				UpdateVersionRes data = (UpdateVersionRes) obj;
				GlobalParams.versionInfo = data.getResult();
			}
			DataStoreUtils.saveLocalInfo(DataStoreUtils.CHECK_VERSION_TIME, String.valueOf(System.currentTimeMillis()));
			uiCallback.onBack(what, arg1, arg2, obj);
			break;
		case NetConstants.WHAT_RESP_ERR:
			break;
		default:
			break;
		}
	}

	private OnNetResponseListener<UpdateVersionRes> updateDataListener = new OnNetResponseListener<UpdateVersionRes>() {

		@Override
		public void sendNetResponseMsg(int what, UpdateVersionRes data,
				String error) {
			sendDataToUIThread(what, data, error);
		}
	};
	
	@Override
	public void cancelNetRequest() {
		
	}


	public void checkVersion() {
		String lastTimeStr = DataStoreUtils.readLocalInfo(DataStoreUtils.CHECK_VERSION_TIME);
		long current = System.currentTimeMillis();
		try {
			if(TextUtils.isEmpty(lastTimeStr)){
				
			}else{
				long lastTime = Long.valueOf(lastTimeStr);
				if((current - lastTime) < (24*60*60*1000)){
					return;
				}
			}
		} catch (Exception e) {
		}
		updateVerNet.doRequest();
	}

}
