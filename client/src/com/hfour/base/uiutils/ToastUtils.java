package com.hfour.base.uiutils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hfour.nearplayer.R;

public class ToastUtils {
	private static Toast toast;
	private static TextView toastTV;

	private static Toast newCustomToast(Context ctx) {
		Toast customToast = new Toast(ctx);
		customToast.setGravity(Gravity.CENTER, 0, 0);
		customToast.setDuration(Toast.LENGTH_SHORT);
		View layout = LayoutInflater.from(ctx).inflate(R.layout.awifi_toast, null);
		toastTV = (TextView) layout.findViewById(R.id.toast_tv);
		customToast.setView(layout);
		
		return customToast;
	}

	public static void showToast(Context ctx, String str) {
		if (null == toast) {
			toast = newCustomToast(ctx);
		}
		toastTV.setText(str);
		toast.show();
	}

	public static void showToast(Context ctx, int strId) {
		if (null == toast) {
			toast = newCustomToast(ctx);
		}
		toastTV.setText(strId);
		toast.show();
	}
}
