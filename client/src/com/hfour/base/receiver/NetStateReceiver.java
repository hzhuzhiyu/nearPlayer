package com.hfour.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hfour.base.device.ClientInfo;

public class NetStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//更新网络类型
		ClientInfo.getAPNType(context);
	}
}
