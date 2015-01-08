package com.hfour.base.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hfour.base.device.ClientInfo;
import com.hfour.base.uiutils.UIUtils;
import com.hfour.nearplayer.R;

/**
 * 游戏特殊属性面板
 * 
 * @author LiuChaoJun
 * 
 */
public class WebViewPanel extends RelativeLayout {
	private WebView webView;
	private LinearLayout loadLayout;
	private ImageView backIv;
	private ImageView refreshIv;
	private ImageView forwardIv;
	private Context ctx;
	private WebSettings webSettings;
	private HandleUrlLoadingCB loadingUrlCB;
	private LinearLayout navigationBarLL;
	private ImageView shareIV;
	private String shareUrl;

	public WebViewPanel(Context context) {
		super(context);
		init(context);
	}
	
	public WebViewPanel(Context context, AttributeSet attrs){
		super(context, attrs);
		init(context);
	}
	
	public void setHandleUrlLoadingCB(HandleUrlLoadingCB cb){
		loadingUrlCB = cb;
	}

	private void init(Context context) {
		if(null == context){
			return;
		}
		ctx = context;
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.webview_panel, this);
		webView = (WebView) findViewById(R.id.panel_webview);
		loadLayout = (LinearLayout) findViewById(R.id.loading_ll);
		backIv = (ImageView) findViewById(R.id.back);
		refreshIv = (ImageView) findViewById(R.id.refresh);
		forwardIv = (ImageView) findViewById(R.id.forward);
		navigationBarLL = (LinearLayout) findViewById(R.id.navigation_bar);
		shareIV = (ImageView)findViewById(R.id.share_iv);
		shareIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String Content = ctx.getString(R.string.share_content)+shareUrl;
				String subject = null;
				String title = ctx.getString(R.string.share_title);
				UIUtils.shareText(ctx, Content, subject, title);
			}
		});
		// 设置监听
		// 后退
		backIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (webView.canGoBack()) {
					webView.goBack();
				}
			}
		});
		// 刷新
		refreshIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				webView.reload();
			}
		});
		// 前进
		forwardIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (webView.canGoForward()) {
					webView.goForward();
				}
			}
		});
		
		setWebView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView(){
		// 设置支持JavaScript脚本
		webSettings = webView.getSettings();
		// 设置可以访问文件
		webSettings.setAllowFileAccess(true);
		// 设置支持缩放
		webSettings.setBuiltInZoomControls(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDefaultTextEncodingName("utf-8");
		// 网页自适应屏幕
		webSettings.setUseWideViewPort(true); 
		webSettings.setLoadWithOverviewMode(true);

		// 设置缓存模式
		// 在模式LOAD_DEFAULT下，无论如何都会从网络上取数据，如果没有网络，就会出现错误页面；在LOAD_CACHE_ELSE_NETWORK模式下，无论是否有网络，只要本地有缓存，都使用缓存。本地没有缓存时才从网络上获取。
		if (ClientInfo.networkType == ClientInfo.NONET) {
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		} else {
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		}
		
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return doOverrideUrlLoading(view, url);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				loadLayout.setVisibility(View.GONE);
				setBackImg();
				setForwardImg();
				shareUrl = url;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				loadLayout.setVisibility(View.VISIBLE);
			}
		}); 
		webView.setDownloadListener(new MyWebViewDownLoadListener());
	}
	/**
	 * 设置webview
	 * 
	 * @param url
	 */
	public void loadUrl(String url) {
		webView.loadUrl(url);
	}
	
	private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            ctx.startActivity(intent);
        }
    }

	/**
	 * 修改返回图片
	 */
	public void setBackImg() {
		if (webView.canGoBack()) {
			backIv.setImageResource(R.drawable.left);
		} else {
			backIv.setImageResource(R.drawable.left_2);
		}
	}
	/**
	 * 修改前进图片
	 */
	public void setForwardImg() {
		if (webView.canGoForward()) {
			forwardIv.setImageResource(R.drawable.right);
		} else {
			forwardIv.setImageResource(R.drawable.right_2);
		}
	}
	/**
	 * 自己处理超链接
	 * 
	 * @param view
	 * @param url
	 */
	public boolean doOverrideUrlLoading(WebView view, String url) {
		// 处理特殊URL后，返回true
		if(null == loadingUrlCB){
			return false;
		}else {
			return loadingUrlCB.handleUrlLoading(url);
		}
	}
	
	public interface HandleUrlLoadingCB{
		public boolean handleUrlLoading(String url);
	}
	
	public void hideNavigationBar(){
		navigationBarLL.setVisibility(View.GONE);
	}
}
