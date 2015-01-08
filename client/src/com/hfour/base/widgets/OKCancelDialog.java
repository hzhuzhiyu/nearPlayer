package com.hfour.base.widgets;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hfour.base.activity.RootActivity;
import com.hfour.nearplayer.R;

public abstract class OKCancelDialog extends DialogFragment implements
		OnClickListener {

	/** 取消键 */
	public static final String CANCEL_BTN = "cancel_btn";
	/** 确认键 */
	public static final String OK_BTN = "ok_btn";

	/** 点击区域外消失 */
	public static final String CANCELED_ON_TOUCH_OUTSIDE = "canceled_on_touch_outside";

	private Button ok;
	private Button cancel;
	private OnClickBtnListener onClickBtn;
	public static final int DLG_OK = 1;
	public static final int DLG_CANCEL = 2;
	
	@Override
	public final Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		String positiveValue = bundle.getString(OK_BTN);
		String negativeValue = bundle.getString(CANCEL_BTN);
		boolean canceledOnTouchOutside = bundle
				.getBoolean(CANCELED_ON_TOUCH_OUTSIDE);

		Dialog dialog = new Dialog(getActivity(), R.style.DialogStyle);
		dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
		dialog.setContentView(R.layout.dlg_frame);
		// 内容区域
		ViewGroup content = (ViewGroup) dialog
				.findViewById(R.id.dlg_content_rl);
		setContentView(content);
		// 按钮1
		ok = (Button) dialog.findViewById(R.id.dlg_ok_btn);
		ok.setOnClickListener(this);
		// 按钮2
		cancel = (Button) dialog.findViewById(R.id.dlg_cancel_btn);
		cancel.setOnClickListener(this);
		setButtonText(positiveValue, negativeValue);
		return dialog;
	}

	/**
	 * 设置按钮文字,可以为null
	 * 
	 * @param positiveValue
	 * @param negativeValue
	 */
	public void setButtonText(String positiveValue, String negativeValue) {
		if (TextUtils.isEmpty(negativeValue)) {
			cancel.setVisibility(View.GONE);
		} else {
			cancel.setText(negativeValue);
			cancel.setVisibility(View.VISIBLE);
		}
		if (TextUtils.isEmpty(positiveValue)) {
			ok.setVisibility(View.GONE);
		} else {
			ok.setText(positiveValue);
			ok.setVisibility(View.VISIBLE);
		}
	}
	/**
	 * 设置监听回调
	 * @param clickBtnListener
	 */
	public void setOnclickBtnListener(OnClickBtnListener clickBtnListener){
		onClickBtn = clickBtnListener;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		RootActivity activity= (RootActivity) getActivity();
		if(null == activity){
			return;
		}
		int dlgID = 0;
		
		switch (id) {
		case R.id.dlg_ok_btn:
			dismissAllowingStateLoss();
			dlgID = DLG_OK;
			break;
		case R.id.dlg_cancel_btn:
			dismissAllowingStateLoss();
			dlgID = DLG_CANCEL;
			break;
		}
		if(null != onClickBtn){
			onClickBtn.onClickButton(dlgID);
		}
	}

	public interface OnClickBtnListener{
		/**
		 * 按键的回调
		 * @param id ： 
		 */
		void onClickButton(int id);
	}
	/**
	 * 设置 对话框显示内容<br/>
	 * 把要显示的内容 添加到 content 即可
	 * 
	 * @param content
	 */
	protected abstract void setContentView(ViewGroup content);

	public void showDlg(RootActivity activity, String dlgTag){
		FragmentManager sf = activity.getSupportFragmentManager();
		show(sf, dlgTag);
	}
	
	public void dismissDlg(){
		dismissAllowingStateLoss();
	}
}
