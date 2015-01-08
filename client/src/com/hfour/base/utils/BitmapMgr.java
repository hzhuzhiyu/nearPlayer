package com.hfour.base.utils;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.hfour.base.constants.GlobalParams;
import com.hfour.base.parser.InputStreamParser;
import com.hfour.base.threads.PriorityRunnable;
import com.hfour.base.threads.WorkThreadExecutor;

/**
 * 功能：定义统一的图片缓存入口，管理所有的图片缓存
 */
public class BitmapMgr {
	// 内存Cache,用于 URL和Cache的对应Map
	private static HashMap<String, SoftReference<Drawable>> cache = new HashMap<String, SoftReference<Drawable>>();
	// 用于存放ImageView和URL对应的Map
	private static Map<ImageView, String> views = new WeakHashMap<ImageView, String>();

	// 下载中的url, 如果cache中有, 不需要重复下载
	private static HashSet<String> downloadingCache = new HashSet<String>();

	// Resources
	private static Resources resources;

	public static Animation imgAnimation;

	private static AtomicInteger atomicInteger = new AtomicInteger();

	private static BitmapFactory.Options decodeOptions;
	private static int screenWith;
	
	public static void init(Resources res, int screenW) {
		resources = res;
		decodeOptions = new BitmapFactory.Options();
		decodeOptions.inPreferredConfig = Config.RGB_565;
		screenWith = screenW;
//		initImgAinmation();
	}

	 public static void initImgAinmation(){
		 imgAnimation = new AlphaAnimation(0, 1);
		 imgAnimation.setDuration(500);
	 }
	/**
	 * 设置动画接口，需要动画效果的ImageView，调用此接口
	 * 
	 * @param iv
	 */
	// public static void startAnimation(ImageView iv){
	// iv.startAnimation(imgAnimation);
	// }
	/**
	 * 加载图片 无默认图片
	 * 
	 * @param iv
	 * @param url
	 * @param defID
	 */
	public static void loadBitmap(ImageView iv, String url) {
		loadBitmap(iv, url, 0);
	}

	/**
	 * 加载图片,可设置默认图片
	 * 
	 * @param iv
	 * @param url
	 * @param defID
	 * @param isSmallIv
	 */
	public static void loadBitmap(final ImageView iv, final String url,
			final int defID) {
		Drawable drawable = null;
		if (TextUtils.isEmpty(url)) {
			setImage(iv, drawable, defID);
			return;
		}
		views.put(iv, url);
		
		ImageCallback cb = new ImageCallback() {

			@Override
			public void onLoad(Drawable drawable, String url) {
				if (drawable == null)
					return;

				String oldurl = views.get(iv);
				if (!TextUtils.isEmpty(oldurl) && oldurl.equals(url)) {
					setImage(iv, drawable, defID);
				}
			}
		};
		// 异步加载图片
		drawable = load(url, cb);
		// 设置图片
		setImage(iv, drawable, defID);
	}
	/**
	 * 定义图片加载回调接口
	 * 
	 */
	public interface ImageCallback {
		public void onLoad(Drawable drawable, String url);
	}
	/***
	 * 根据URL加载图片
	 * @param url
	 * @param callback
	 * @return
	 */
	private static Drawable load(final String url,
			final ImageCallback callback) {
		if (url == null) {
			return null;
		}
		// 从缓存中取
		SoftReference<Drawable> ref = null;
		ref = cache.get(url);
		Drawable drawable = (ref != null) ? ref.get() : null;
		if (drawable != null) {
			return drawable;
		}
		
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				callback.onLoad((Drawable) message.obj, url);
			}
		};

		WorkThreadExecutor.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				// 从文件缓存读取
				Drawable drawableFile = getBitmapFromFile(url);
				if (drawableFile == null) {
					// 文件缓存没有，从网络获取
					WorkThreadExecutor.getInstance().execute(
							new PriorityRunnable() {
								@Override
								public void run() {
									// 判定是否下载
									if (downloadingCache.contains(url)) {
										return;
									}
									Drawable drawableNet = getBitmapFromNet(
											url);
									Message msg = Message.obtain();
									msg.obj = drawableNet;
									handler.sendMessage(msg);
								}
							});
				} else {
					Message msg = Message.obtain();
					msg.obj = drawableFile;
					handler.sendMessage(msg);
				}
			}
		});
		return drawable;
	}
	/**
	 * 用于大图的下载
	 * @param url
	 * @param callback
	 * @return
	 */
	public static Drawable loadImage(final String url, final ImageCallback callback){
		Drawable drawable = load(url, callback);
		return drawable;
	}
	/**
	 * 保存缓存
	 * 
	 * @param isSmallIv
	 * @param url
	 * @param drawable
	 */
	private static void putCache(String url,
			Drawable drawable) {
		if (drawable != null) {
			cache.put(url, new SoftReference<Drawable>(drawable));
		}
	}

	/**
	 * 设置Imageview的显示
	 * 
	 * @param iv
	 * @param drawable
	 * @param defID
	 */
	private static void setImage(final ImageView iv, final Drawable drawable,
			final int defID) {
		// 如果是空，显示默认图片,设置背景色
		if (drawable == null && defID != 0) {
			iv.setImageResource(defID);
		} else if (drawable != null) {
			iv.setImageDrawable(drawable);
		}
	}

	/**
	 * 从文件获取Bitmap
	 * 
	 * @param url
	 * @param isSmallIv
	 * @return
	 */
	public static Drawable getBitmapFromFile(String url) {
		final String imagePath = FileUtils.getImageCacheFileName(url);
		Drawable drawable = getBitmapFromCachePath(imagePath);
		putCache(url, drawable);
		return drawable;
	}

	/**
	 * 加载图片到缓存中
	 * 
	 * @param url
	 * @param isSmallIv
	 */
	public static boolean loadBitmapToCache(String url) {
		if(null == url){
			return false;
		}
		
		Drawable drawable = null;
		SoftReference<Drawable> ref = null;
		ref = cache.get(url);
		drawable = (ref == null) ? null : cache.get(url).get();
		
		if (drawable == null) {
			if (!url.startsWith("http")) {
				drawable = getBitmapFromAssert(url);
			} else {
				if ((drawable = getBitmapFromFile(url)) == null) {
					drawable = getBitmapFromNet(url);
				}
			}
		}
		return drawable != null;
	}

	/**
	 * 加载Assert下的图片
	 * 
	 * @param fileName
	 * @return
	 */
	public static Drawable getBitmapFromAssert(String fileName) {
		InputStream input = null;
		try {
			input = GlobalParams.gContext.getResources().getAssets().open(fileName);
			Drawable drawable = new BitmapDrawable(resources,
					BitmapFactory.decodeStream(input, null, decodeOptions));

			putCache(fileName, drawable);
			return drawable;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}
	
	/**
	 * 从网络获取图片,并且保存到文件,做文件缓存
	 * 
	 * @param url
	 * @param isSmallIv
	 * @return
	 */
	public static Drawable getBitmapFromNet(final String url) {
		final String imagePath;
		if (downloadingCache.contains(url)) {
			//防止文件冲突
			imagePath = FileUtils.getImageCacheFileName(url)
					+ atomicInteger.incrementAndGet();
		} else {
			imagePath = FileUtils.getImageCacheFileName(url);
			downloadingCache.add(url);
		}
		Drawable drawable = getBitmapFromFile(url);
		if (drawable == null) {
			drawable = (Drawable) HttpUtils.get(url,
					new InputStreamParser<Drawable>() {
						@Override
						public Drawable parser(InputStream inputStream) {
							try {
								Bitmap bm = BitmapFactory
										.decodeStream(inputStream, null, decodeOptions);
								//对大图进行缩放
								if(bm.getWidth() > screenWith){
									bm = ImageUtils.zoomBitmap(bm, screenWith);
								}
								ImageUtils.Bitmap2PNG(bm, imagePath);
								BitmapDrawable drawable = new BitmapDrawable(
										resources, bm);

								return drawable;
							} catch (Exception e) {
								e.printStackTrace();
								MLog.e("debug", "failed url:" + url);
							} finally {
								downloadingCache.remove(url);
							}
							return null;
						}
					});
			putCache(url, drawable);
			downloadingCache.remove(url); // add by huzhiyu ：if httputils.get
											// 抛出异常，那么加载不成功的图片，永远无法加载了
		}
		return drawable;
	}

	/**
	 * 从文件中解码Bitmap
	 * 
	 * @param imagePath
	 * @return
	 */
	private static Drawable getBitmapFromCachePath(String imagePath) {
		Drawable drawable = null;
		File file = new File(imagePath);
		if (file.exists() && file.isFile()) {
			drawable = Drawable.createFromPath(file.getPath());
			if (drawable == null) {
				file.delete();
			} else {
				return drawable;
			}
		}
		return drawable;
	}
}
