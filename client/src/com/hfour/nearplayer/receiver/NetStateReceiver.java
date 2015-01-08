package com.hfour.nearplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hfour.base.constants.ConstantParams;
import com.hfour.base.device.ClientInfo;
import com.hfour.base.utils.MLog;
import com.hfour.nearplayer.MainApplication;

public class NetStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int netType = ClientInfo.networkType;
		//更新网络类型
		ClientInfo.getAPNType(context);
		if(netType == ClientInfo.networkType){
			//网络相同
		}else if(ClientInfo.WIFI == ClientInfo.networkType){
			// 网络发生了变化，且当前是WIFI网络
			MLog.info("NetStateReceiver Switch: ClientInfo.networkType is WIFI");
			MainApplication.ctx.sendBroadcast(new Intent(
					ConstantParams.BC_RECEIVE_WIFI_NET));
		}
	}
}