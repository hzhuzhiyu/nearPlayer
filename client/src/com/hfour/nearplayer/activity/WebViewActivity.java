package com.hfour.nearplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hfour.base.activity.RootActivity;
import com.hfour.base.constants.GlobalParams;
import com.hfour.base.datamgr.AbstractDataManager.OnUiDataMgrListener;
import com.hfour.base.datamgr.CheckVerisonDataMgr;
import com.hfour.base.uiutils.ToastUtils;
import com.hfour.base.uiutils.UIUtils;
import com.hfour.base.utils.MLog;
import com.hfour.base.widgets.PopMenu;
import com.hfour.base.widgets.PopMenu.OnMenuItemClickListener;
import com.hfour.base.widgets.WebViewPanel;
import com.hfour.nearplayer.R;

public class WebViewActivity extends RootActivity{
	public WebViewPanel webView;
	public static final String WEBVIEW_URL = "url";
	public static final String WEBVIEW_TITLE = "title";
	public static final String WEBVIEW_NAVIGATION_BAR = "navigation_bar";
	private String Url;
	private CheckVerisonDataMgr checkVerDataMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		Intent intent = getIntent();
		Url = intent.getStringExtra(WEBVIEW_URL);
		if(null == Url){
			finish();
			return;
		}
		String title = intent.getStringExtra(WEBVIEW_TITLE);
		setTitle(title);
		
		boolean navigationBar = intent.getBooleanExtra(WEBVIEW_NAVIGATION_BAR, true);
		
		webView = (WebViewPanel) findViewById(R.id.app_webview);
		if(navigationBar){
			
		}else{
			webView.hideNavigationBar();
		}
		
		MLog.info("webviewactivity Url is "+Url);
		webView.loadUrl(Url);
		
		// 版本是否有升级
		checkVerDataMgr = new CheckVerisonDataMgr(versionOnBack);
		checkVerDataMgr.checkVersion();
	}
	
	private OnUiDataMgrListener versionOnBack = new OnUiDataMgrListener() {

		@Override
		public void onBack(int what, int arg1, int arg2, Object obj) {
			checkVersion();
		}
	};

	private long backPressTime;
	@Override
	public void onBackPressed() {
		if((System.currentTimeMillis()- backPressTime) < 2000){
			//连续按BACK 退出
			super.onBackPressed();
		}else{
			UIUtils.showToast(R.string.exit_tip);
			backPressTime = System.currentTimeMillis();
		}
	}

	@Override
	public String getActivityName() {
		return "WebViewActivity";
	}
	
	// /////////////////版本升级///////////////
		private void checkVersion() {
			int newVersion = 0;
			if (GlobalParams.versionInfo != null) {
				newVersion = GlobalParams.versionInfo.getServerVerInt();
			}
			if (newVersion > GlobalParams.apkVerCode) {
				// 提示升级
				showOptions();
			}
		}

		private void showOptions() {
			if (null == GlobalParams.versionInfo) {
				return;
			}

			PopMenu popMenu = null;
			String title = null;
			Integer[] menuItems = { R.string.update };

			title = GlobalParams.versionInfo.getTip();
			if (TextUtils.isEmpty(title)) {
				title = getString(R.string.update_ver_tip);
			}
			popMenu = new PopMenu(WebViewActivity.this, menuItems, listener, title,
					PopMenu.NONE_CHOOSE, false);

			if (null != popMenu) {
				popMenu.showPopMenu();
			}
		}

		private OnMenuItemClickListener listener = new OnMenuItemClickListener() {

			@Override
			public void onMenuClick(int stringId) {
				if (null == GlobalParams.versionInfo) {
					return;
				}
				if (R.string.update == stringId) {
					UIUtils.gotoWeb(GlobalParams.versionInfo.getDownloadUrl());
				} else {
					// 取消
					if (GlobalParams.versionInfo.getUpgradeCode() == 3) {
						// 1=不升级 2=提示升级 3=强制升级
						ToastUtils.showToast(getApplicationContext(),
								R.string.update_tip);
					}
				}
			}
		};

}
