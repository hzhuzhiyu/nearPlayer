package com.hfour.base.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.hfour.nearplayer.R;

/**
 * 自定义进度条,当发起网络请求时显示该对话框，让用户等待
 */
public class WaitingDialog extends Dialog {

	private int strID = 0;

	public WaitingDialog(Context context) {
		super(context, R.style.DialogStyle);
	}

	// 设置等待对话框的内容内容
	public void setWaitInfo(int strID) {
		this.strID = strID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_waiting);
		setCanceledOnTouchOutside(false);
		if (strID > 0) {
			TextView tvContent = (TextView) findViewById(R.id.txt_custom_content);
			tvContent.setText(strID);
		}
	}

}
