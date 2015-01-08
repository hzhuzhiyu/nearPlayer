package com.hfour.base.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.hfour.base.constants.GlobalParams;
import com.hfour.base.uiutils.DialogUtils;
import com.hfour.base.utils.MLog;
import com.hfour.nearplayer.R;

/**
 * all app activity must extends RootActivity
 * @author Tony
 *
 */
public abstract class RootActivity extends FragmentActivity{

	/**
	 * 关闭所有Activity Broadcast Action
	 */
	public static final String FINISH_ACTION = "com.app.activity.finish";
	/**等待对话框*/
	private Dialog waitingDialog; 
	/***当前activity的View*/
	private View acitivtyView;
//	private static final boolean DEVELOPER_MODE = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		 if (DEVELOPER_MODE && (Build.VERSION.SDK_INT >= 9)) {  
//			 StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
//	             .detectDiskReads()  
//	             .detectDiskWrites()  
//	             .detectNetwork()  
//	             .penaltyLog()  
//	             .build());  
//			 StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
//	             .detectLeakedSqlLiteObjects()  
//	             .penaltyLog()  
//	             .penaltyDeath()  
//	             .build());   
//		    }  
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void setContentView(View view) {
		acitivtyView = view;
		super.setContentView(view);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		acitivtyView = LayoutInflater.from(this).inflate(layoutResID, null);
		super.setContentView(acitivtyView);
	}
	/**
	 * 获取当前activity的View
	 * @return
	 */
	public View getActivityView(){
		return acitivtyView;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		String activityName = getActivityName();
		if(null != activityName){
//			MobclickAgent.onPageStart(getActivityName()); //统计页面
			MLog.info("onPageStart : "+activityName);
		}
//		MobclickAgent.onResume(this);  //统计时长
		
		super.onResume();
		GlobalParams.gCurrentActivity = this;
	}
	
	@Override
	protected void onPause() {
		String activityName = getActivityName();
		if(null != activityName){
//			MobclickAgent.onPageEnd(activityName);  // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
			MLog.info("onPageEnd : "+activityName);
		}
//		MobclickAgent.onPause(this);
		super.onPause();
		cancelWaitingDlg();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
		waitingDialog = null;
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	private IntentFilter filter = new IntentFilter(FINISH_ACTION);
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}
	/**
	 * 简单的等待框，比如需要等待的时候
	 * @param strID : 自定义的文字，可以设置为0
	 */
	public void showWaitingDlg(int strID, boolean isCancelable){
		if(null == waitingDialog){
			waitingDialog = DialogUtils.createWaitingDialog(this, strID);
		}
		waitingDialog.setCancelable(isCancelable);
		waitingDialog.show();
		MLog.info("showWaitingDlg = "+waitingDialog);
	}
	/**
	 * 取消等待框
	 */
	public void cancelWaitingDlg(){
		MLog.info("cancelWaitingDlg = "+waitingDialog);
		if(null != waitingDialog){	//waitingDialog.isShowing() 不要添加，有时候PAUSE的时候，也是not showing, 等返回的时候，cancel不了还会显示DLG
			waitingDialog.cancel();
		}
	}
	
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	/**
	 * 获取activity的名称
	 * @return
	 */
	public abstract String getActivityName();

}
