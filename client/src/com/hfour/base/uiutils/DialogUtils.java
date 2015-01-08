package com.hfour.base.uiutils;

import android.content.Context;

import com.hfour.base.widgets.WaitingDialog;

/**
 * 功能：实现Dialog的类，用于创建通用对话框 例如 Yes/No对话框，等待对话框等等
 * 
 * @author PanYingYun
 * 
 */
public class DialogUtils {

	// 自定义提示的对话框
	public static WaitingDialog createWaitingDialog(Context ctx, int strID) {
		WaitingDialog dlg = new WaitingDialog(ctx);
		dlg.setWaitInfo(strID);
		return dlg;
	}
}
