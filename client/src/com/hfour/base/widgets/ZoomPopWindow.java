package com.hfour.base.widgets;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout.LayoutParams;
import android.widget.PopupWindow;

import com.hfour.base.activity.RootActivity;
import com.hfour.nearplayer.R;

public class ZoomPopWindow extends PopupWindow{
	protected Context context;
	private RootActivity activity;
	private boolean isAnim;
	
	public ZoomPopWindow(Context context){
		super(context);
	}
	
	public void initZoomPopWindow(RootActivity inActivity, View contentView, boolean isAnim){
		activity = inActivity;
		context = activity.getApplicationContext();
		this.isAnim = isAnim;
		
		int bgColor = context.getResources().getColor(R.color.transparent);
		this.setContentView(contentView);  
        this.setWidth(LayoutParams.MATCH_PARENT);  
        this.setHeight(LayoutParams.MATCH_PARENT);  
        this.setBackgroundDrawable(new ColorDrawable(bgColor));// 设置TabMenu菜单背景  
        this.setAnimationStyle(R.style.PopupAnimation);  
        this.setFocusable(true);// menu菜单获得焦点 如果没有获得焦点menu菜单中的控件事件无法响应 
	}
	
	
	@Override
	public void dismiss() {
		if(null == activity){
			return;
		}
		if(isAnim){
			View rootView = activity.getActivityView();
			Animation animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_out);
	    	animation.setFillAfter(true);
	    	rootView.startAnimation(animation);
		}
    	
		super.dismiss();
	}
	
	public void showPopWin(){
		View rootView = activity.getActivityView();
				
		if(isAnim){
			Animation animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_in);
	    	animation.setFillAfter(true);
	    	rootView.startAnimation(animation);
		}
    	
		this.showAtLocation(rootView, Gravity.CENTER, 0, 0);
		this.update();
	}

	public void onDestroy() {
		dismiss();
		activity = null;
	}
}
