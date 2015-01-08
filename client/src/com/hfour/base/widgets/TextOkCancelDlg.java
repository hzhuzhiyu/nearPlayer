package com.hfour.base.widgets;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hfour.nearplayer.R;

public class TextOkCancelDlg extends OKCancelDialog{
	
	private static final String TIP_MSG = "tip_msg";

	public TextOkCancelDlg(){
		
	}
	
	/**
	 * 无标题文本对话框
	 * 
	 * @param tipMsg ： 提示内容
	 * @param okStr ： 确认键显示， null 不显示
	 * @param cancelStr ： 取消键显示， null 不显示
	 * @param cancelable ： 点击外部，是否可以取消
	 * @return
	 */
	public static TextOkCancelDlg newTextOkCancelDlg(String tipMsg,
			String okStr, String cancelStr, boolean cancelable){
		TextOkCancelDlg dlg = new TextOkCancelDlg();
		
		Bundle bundle = new Bundle();
		bundle.putString(TextOkCancelDlg.TIP_MSG, tipMsg);
		bundle.putString(OKCancelDialog.CANCEL_BTN, cancelStr);
		bundle.putString(OKCancelDialog.OK_BTN, okStr);
		dlg.setArguments(bundle);
		dlg.setCancelable(cancelable);
		
		return dlg;
	}
	
	@Override
	protected void setContentView(ViewGroup content) {
		String value = getArguments().getString(TIP_MSG);
		if (value == null) {
			return;
		}
		// 消息
		View child = LayoutInflater.from(getActivity()).inflate(
				R.layout.dlg_ok_cancel_text, content);
		TextView tipTV = (TextView) child.findViewById(R.id.txtdlg_tv);
		tipTV.setText(value);
	}

}
