package com.hfour.base.activity;

import android.view.View;
import android.widget.TabHost.TabContentFactory;

public abstract class BaseTab implements TabContentFactory{
	private BasePage tabPage;
	private RootActivity activity;
	
	public BaseTab(RootActivity thisActivity){
		activity = thisActivity;
	}
	/**
	 * 创建TabPage
	 * @return
	 */
	public abstract BasePage newTabPage(RootActivity activity);
	
	@Override
	public View createTabContent(String tag) {
		if(null == tabPage){
			tabPage = newTabPage(activity);
		}
		return tabPage.getView();
	}
	
	/**
	 * 被选中
	 */
	public void onResume() {
		if(null == tabPage){
			tabPage = newTabPage(activity);
			tabPage.getView(); //执行onResume时，需要初始化下View
		}
		tabPage.onResume();
	}
	
	public void onPause() {
		if(null == tabPage){
			return;
		}
		tabPage.onPause();
	}
}
