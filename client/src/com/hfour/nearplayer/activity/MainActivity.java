package com.hfour.nearplayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.hfour.base.activity.RootActivity;
import com.hfour.base.datamgr.CheckVerisonDataMgr;
import com.hfour.base.uiutils.UIUtils;
import com.hfour.nearplayer.R;

public class MainActivity extends RootActivity {
	private static final int MSG_GOTO_HOMEPAGE = 1;
	private CheckVerisonDataMgr checkVersionDataMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		AlphaAnimation animi = new AlphaAnimation(0, 1);
		animi.setDuration(1500);
		animi.setFillAfter(true);
		ImageView logoView = (ImageView) findViewById(R.id.logo_iv);
		logoView.startAnimation(animi);
		handler.sendEmptyMessageDelayed(MSG_GOTO_HOMEPAGE, 2000);
		checkVersionDataMgr = new CheckVerisonDataMgr(null);
		checkVersionDataMgr.checkVersion();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}


	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GOTO_HOMEPAGE: {
				gotoHomePage();
			}
				break;
			default:
				break;
			}
		}

	};

	@Override
	public String getActivityName() {
		return "MainActivity";
	}

	private void gotoHomePage() {
		UIUtils.gotoActivity(HomePageActivity.class);
	}
}
