package com.hfour.base.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hfour.base.widgets.SlideLinearLayout;
import com.hfour.nearplayer.R;
/**
 * 需要actionBar的activity继承该类
 * @author Tony
 *
 */
public abstract class ActionBarActivity extends RootActivity {
	protected SlideLinearLayout rootSliedLayout;

	private View waitView = null;
	protected View contentView = null;
	private View reloadView = null;
	private boolean isNeedAddWaitingView;
	/** 标题 */
	private TextView title = null;
	private ImageView titleImg;
	/**左侧按钮*/
	private Button leftBtn;
	private ImageButton leftImgBtn;
	/**右侧按钮*/
	private Button rightBtn;
	private ImageButton rightImgBtn;

	@Override
	protected void onResume() {
		super.onResume();
	}
	/***业务数据保存*/
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	/**业务数据恢复*/
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	protected void initActionBar() {
		findViewById(R.id.action_bar_RL).setVisibility(View.VISIBLE);
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = v.getId();
				switch (id) {
					case R.id.right_btn :
					case R.id.right_imgbtn:
						rightBtnOnClick();
						break;
					case R.id.left_imgbtn:
					case R.id.left_btn:
						leftBtnOnClick();
						break;
				}
			}
		};
		//增加点击返回的灵敏度
		leftImgBtn = (ImageButton) findViewById(R.id.left_imgbtn);
		leftImgBtn.setOnClickListener(onClickListener);
		leftBtn = (Button) findViewById(R.id.left_btn);
		leftBtn.setOnClickListener(onClickListener);
		
		rightBtn = (Button) findViewById(R.id.right_btn);
		rightBtn.setOnClickListener(onClickListener);
		rightImgBtn = (ImageButton) findViewById(R.id.right_imgbtn);
		rightImgBtn.setOnClickListener(onClickListener);
		
		title = (TextView) findViewById(R.id.action_bar_title);
		titleImg = (ImageView) findViewById(R.id.action_bar_title_img);
	}
	protected abstract void rightBtnOnClick();
	/**
	 * 默认是返回<br>
	 * 如果需要可以重写该方法
	 */
	protected void leftBtnOnClick(){
		onBackPressed();
	}
	/**
	 * 设置标题栏
	 * 
	 * @param title
	 */
	public void setTitle(int titleTxtResId) {
		this.title.setText(titleTxtResId);
		titleImg.setVisibility(View.GONE);
		title.setVisibility(View.VISIBLE);
	}
	/**
	 * 设置图片标题
	 * @param titleImgResId
	 */
	public void setTitleImg(int titleImgResId){
		titleImg.setImageResource(titleImgResId);
		titleImg.setVisibility(View.VISIBLE);
		title.setVisibility(View.GONE);
	}
	
	/**
	 * 设置标题栏
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title.setText(title);
		titleImg.setVisibility(View.GONE);
		this.title.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(createRootView(LayoutInflater.from(this).inflate(
				layoutResID, null)));
		initActionBar();
	}
	
	public void setContentView(View view){
		super.setContentView(createRootView(view));
		initActionBar();
	}
	/**
	 * 设置右上角
	 * @param title : 0 消失
	 */
	public void setRightBtnText(int txtResId){
		if(0 == txtResId){
			rightBtn.setVisibility(View.GONE);
		}else {
			rightBtn.setVisibility(View.VISIBLE);
			rightBtn.setText(txtResId);
		}
		rightImgBtn.setVisibility(View.GONE);
	}
	
	public void setRightBtnImg(int imgResId){
		if(0 == imgResId){
			rightImgBtn.setVisibility(View.GONE);
		}else{
			rightImgBtn.setVisibility(View.VISIBLE);
			rightImgBtn.setImageResource(imgResId);
		}
		
		rightBtn.setVisibility(View.GONE);
	}
	/**
	 * 设置左上角图片
	 * @param imgSrcId 0:消失
	 */
	public void setLeftImgBtnSrc(int imgSrcId){
		if(0 == imgSrcId){
			leftImgBtn.setVisibility(View.GONE);
		}else{
			leftImgBtn.setVisibility(View.VISIBLE);
			leftImgBtn.setImageResource(imgSrcId);
		}
		leftBtn.setVisibility(View.GONE);
	}
	
	public void setLeftBtnText(int txtResId){
		if(0 == txtResId){
			leftBtn.setVisibility(View.GONE);
		}else {
			leftBtn.setVisibility(View.VISIBLE);
			leftBtn.setText(txtResId);
		}
		leftImgBtn.setVisibility(View.GONE);
	}

	/**
	 * 创建根View
	 * 
	 * @param view : 内容
	 * @return
	 */
	protected SlideLinearLayout createRootView(View view) {
		contentView = view;
		rootSliedLayout = new SlideLinearLayout(this);
		rootSliedLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutInflater.from(this).inflate(R.layout.action_bar, rootSliedLayout);
		if (isNeedAddWaitingView) {
			waitView = addWaitingView(rootSliedLayout);
			waitView.setVisibility(View.GONE);
		}
		rootSliedLayout.addView(view, LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		rootSliedLayout.enableSlide(false); //shop版本默认false
		
		return rootSliedLayout;
	}

	/**
	 * 是否需要添加等待框
	 * setContentView 之前调用
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
		View waitView = LayoutInflater.from(this).inflate(R.layout.activity_inner_waiting,
				null);
		root.addView(waitView,
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		return waitView;
	}

	/**
	 * 显示等待框
	 */
	public void showWaiting() {
		if (waitView == null)
			return;
		waitView.setVisibility(View.VISIBLE);
		contentView.setVisibility(View.GONE);
	}

	/**
	 * 隐藏等待框
	 */
	public void hideWaiting() {
		if (waitView == null)
			return;
	//注：不需要判断waitView.isShown(),可能ACTIVITY PAUSE中，等恢复的时候，就一直加载中了
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
		if(null == reload){
			return;
		}
		hideWaiting();
		
		if(null == reloadView){
			reloadView = LayoutInflater.from(this).inflate(R.layout.reload_layout,
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
			rootSliedLayout.addView(reloadView, 
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT); 
		}else {
			reloadView.setVisibility(View.VISIBLE);
		}
		contentView.setVisibility(View.GONE);
	}
	/**
	 * 关闭滑动退出
	 * 在setContentView之后调用
	 * @param enabled
	 */
	protected void enableSlideLayout(boolean enabled) {
		rootSliedLayout.enableSlide(enabled);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelWaitingDlg();
		hideWaiting();
	}
}
