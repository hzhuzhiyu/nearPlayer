package com.hfour.base.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hfour.nearplayer.R;
/**
 * 基础的PAGE页
 * @author Tony
 */
public abstract class BasePage {
	protected RootActivity parentActivity = null;
	private LinearLayout rootView = null;
	private View waitView = null;
	private View contentView = null;
	private boolean isNeedAddWaitingView;
	private View reloadView;
	
	public BasePage(RootActivity activity) {
		this.parentActivity = activity;
	}
	/**
	 * 创建PAGE的view
	 * @return
	 */
	public abstract View onCreateView();
	/**
	 * 滑动到当前页
	 */
	public abstract void onResume();
	/**
	 * 离开当前页
	 */
	public abstract void onPause();
	/**
	 * 被创建时
	 */
	public abstract void onActivityCreated();
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {};
	
	public void onSaveInstanceState(Bundle outState) {};
	
	/**
	 * page销毁的时候，必须调用，对activity解除引用
	 */
	public void onDestroy(){
		parentActivity = null;
	}

	public View getView() {
		if (rootView == null) {
			rootView = new LinearLayout(parentActivity);
			rootView.setOrientation(LinearLayout.VERTICAL);
			contentView = onCreateView();
			if (isNeedAddWaitingView) {
				waitView = addWaitingView(rootView);
				waitView.setVisibility(View.GONE);
			}
			rootView.addView(contentView,
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			onActivityCreated();
		}if (rootView == null) {
			rootView = new LinearLayout(parentActivity);
			rootView.setOrientation(LinearLayout.VERTICAL);
			contentView = onCreateView();
			if (isNeedAddWaitingView) {
				waitView = addWaitingView(rootView);
				waitView.setVisibility(View.GONE);
			}
			rootView.addView(contentView,
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			onActivityCreated();
		}
		return rootView;
	}

	/**
	 * 是否需要添加等待框
	 * 
	 * @param isNeedAddWaitingView
	 */
	public void setNeedAddWaitingView(boolean isNeedAddWaitingView) {
		this.isNeedAddWaitingView = isNeedAddWaitingView;
	}

	/**
	 * 添加等待框
	 * 
	 * @param root
	 */
	private View addWaitingView(ViewGroup root) {
		View waitView = LayoutInflater.from(parentActivity).inflate(
				R.layout.activity_inner_waiting, null);
		root.addView(waitView, LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		waitView.setVisibility(View.GONE);
		return waitView;
	}

	/**
	 * 显示加载界面，contentView 加载界面
	 */
	public void showWaiting() {
		if (waitView == null)
			return;
		waitView.setVisibility(View.VISIBLE);
		contentView.setVisibility(View.GONE);
	}

	/**
	 * 隐藏等待框， contentView 隐藏加载界面
	 */
	public void hideWaiting() {
		if (waitView == null)
			return;
		//注：不需要判断waitView.isShown(),因为有可能page没有被focus，导致waitView判断还未显示，滑动到当前页时，会导致一直加载中
		waitView.setVisibility(View.GONE);
		contentView.setVisibility(View.VISIBLE);
	}

	/**
	 * 重新加载数据
	 * @author Tony
	 *
	 */
	public interface ReloadFunction{
		void reload();
	}
	/**
	 * 加载失败
	 */
	public void loadingFailed(final ReloadFunction reload){
		hideWaiting();
		if(null == rootView){
			return;
		}
		if(null == reloadView){
			reloadView = LayoutInflater.from(parentActivity).inflate(R.layout.reload_layout,
					null);
			Button reloadBtn = (Button) reloadView.findViewById(R.id.reload_btn);
			reloadBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					reloadView.setVisibility(View.GONE);
					contentView.setVisibility(View.VISIBLE);
					showWaiting();
					reload.reload();
				}
			});
			rootView.addView(reloadView, 
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT); 
		}else {
			reloadView.setVisibility(View.VISIBLE);
		}
		contentView.setVisibility(View.GONE);
	}
}
