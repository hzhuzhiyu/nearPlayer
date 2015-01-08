package com.hfour.base.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * 图形/图像处理类，专门实现图片图像的处理。 例如 拉伸、缩小、裁剪、倒影等效果
 * 
 * 
 */
public class ImageUtils {

	/**
	 * 获取bitmap
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmapByPath(String filePath) {
		return getBitmapByPath(filePath, null);
	}

	public static Bitmap getBitmapByPath(String filePath,
			BitmapFactory.Options opts) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis, null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}


	/*** 
	* 图片的缩放方法 
	* 
	* @param inBitmap 
	* ：源图片资源 
	* @param newWidth 
	* ：缩放后宽度 
	* @param newHeight 
	* ：缩放后高度 
	* @return 
	*/  
	public static Bitmap zoomImage(Bitmap inBitmap, int newWidth, int newHeight) {  
		// 获取这个图片的宽和高  
		int width = inBitmap.getWidth();  
		int height = inBitmap.getHeight();  
		// 创建操作图片用的matrix对象  
		Matrix matrix = new Matrix();  
		// 计算缩放率，新尺寸除原始尺寸  
		float scaleWidth = ((float) newWidth) / width;  
		float scaleHeight = ((float) newHeight) / height;  
		// 缩放图片动作  
		matrix.postScale(scaleWidth, scaleHeight);  
		Bitmap bitmap = Bitmap.createBitmap(inBitmap, 0, 0, width, height, matrix, true);  
		return bitmap;  
	}  

	/**
	 * 缩放图片
	 * 
	 * @param bitmap
	 * @param newWidth
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int newWidth) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		float scaleWidth = ((float) newWidth) / width;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}

	/**
	 * 缩放图片
	 * 
	 * @param drawable
	 * @param newWidth
	 * @return
	 */
	public static Drawable zoomDrawable(Resources res, Drawable drawable,
			int newWidth) {
		if (drawable == null) {
			return null;
		}
		Bitmap bitmap = drawableToBitmap(drawable);
		return new BitmapDrawable(res, zoomBitmap(bitmap, newWidth));
	}

	// 创建倒影Bitmap
	public static Drawable createReflectedDrawable(Resources res,
			Drawable originalDrawable) {
		Bitmap originalBitmap = drawableToBitmap(originalDrawable);
		final int reflectionGap = 1;
		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(originalBitmap, 0,
				height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height
				+ height / 4 + reflectionGap), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(originalBitmap, 0, 0, null);

		Paint deafaultPaint = new Paint();
		deafaultPaint.setColor(Color.WHITE);
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalBitmap.getHeight(), 0, bitmapWithReflection.getHeight()
						+ reflectionGap, 0x30ffffff, 0x00ffffff, TileMode.CLAMP);

		paint.setShader(shader);

		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		reflectionImage.recycle();
		return new BitmapDrawable(res, bitmapWithReflection);
	}

	/**
	 * Drawable 转化Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
	
	
	/**  
     * 将彩色图转换为灰度图  
     * @param img 位图  
     * @return  返回转换好的位图  
     */    
    public static Bitmap convertGreyImg(Bitmap img) {    
        int width = img.getWidth();         //获取位图的宽    
        int height = img.getHeight();       //获取位图的高    
            
        int []pixels = new int[width * height]; //通过位图的大小创建像素点数组    
            
        img.getPixels(pixels, 0, width, 0, 0, width, height);    
        int alpha = 0xFF << 24;     
        for(int i = 0; i < height; i++)  {    
            for(int j = 0; j < width; j++) {    
                int grey = pixels[width * i + j];    
                    
                int red = ((grey  & 0x00FF0000 ) >> 16);    
                int green = ((grey & 0x0000FF00) >> 8);    
                int blue = (grey & 0x000000FF);    
                    
                grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);    
                grey = alpha | (grey << 16) | (grey << 8) | grey;    
                pixels[width * i + j] = grey;    
            }    
        }    
        Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);    
        result.setPixels(pixels, 0, width, 0, 0, width, height);    
        return result;    
    }  
    
    
  /**
   * 质量压缩方法
   * @param image
   * @return
   */
    public static Bitmap compressImage(Bitmap image) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;
        while ( (baos.toByteArray().length / 1024) > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    }  
    /**
     * 根据文件路径压缩 长OR宽 MaxScaleWH（取其中的大值）
     * @param srcPath
     * @return
     */
    public static Bitmap getimage(String srcPath, int MaxScaleWH) { 
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空  
          
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  

        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        int maxWH = Math.max(w, h);
        be = (int) (maxWH/MaxScaleWH);
        
        if (be <= 0)  {
        	 be = 1; 
        }
        
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
        
        return bitmap;//压缩好比例大小后再进行质量压缩  
    }  
    /**
     * 根据bitmap 压缩 长OR宽 MaxScaleWH（取其中的大值）
     * @param image
     * @return
     */
    public static Bitmap comp(Bitmap image, int MaxScaleWH) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();         
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        int maxwh = Math.max(w, h);
        be = maxwh/MaxScaleWH;
        
        if (be <= 0){
            be = 1;
        }
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        isBm = new ByteArrayInputStream(baos.toByteArray());  
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
    }  
    
    
   
    /**
     * 获取圆角的图
     * @param source
     * @param roundPx
     * @return
     */
	public static Bitmap getRoundedCornerBitmap(Bitmap source, float roundPx) {
		Bitmap outBmp = null;
		if (null == source) {
			return null;
		}
		int width = source.getWidth();
		int height = source.getHeight();
		try {
			outBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(outBmp);
			Paint paint = new Paint();
			Rect rect = new Rect(0, 0, width, height);
			RectF rectF = new RectF(rect);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(0xFF424242);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(source, rect, rect, paint);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			// if (null != source) {
			// source.recycle();
			// source = null;
			// }
		}

		return outBmp;
	}
	

	public static Bitmap resampleBitmap(Bitmap bmpt, int maxDim) throws Exception {
		Matrix m = new Matrix();
		// if (bmpt.getWidth() > maxDim || bmpt.getHeight() > maxDim) {
		BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(),
				bmpt.getHeight(), maxDim);
		m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
				(float) optsScale.outHeight / (float) bmpt.getHeight());
		// }

		// int sdk =  Integer.valueOf(Build.VERSION.SDK_INT).intValue();
		// if (sdk > 4) {
		// int rotation = ExifUtils.getExifRotation(path);
		// if (rotation != 0) {
		// m.postRotate(rotation);
		// }
		// }

		return Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(), bmpt.getHeight(), m, true);
	}

	
	/**
	 * 获取缩放的宽和高
	 * @param cx
	 * @param cy
	 * @param max
	 * @return
	 */
	private static BitmapFactory.Options getResampling(int cx, int cy, int max) {
		float scaleVal = 1.0f;
		int maxXY = Math.max(cx, cy);
		
		if(0 != maxXY){
			scaleVal = max/maxXY;
		}
			
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		
		bfo.outWidth = (int) (cx * scaleVal + 0.5f);
		bfo.outHeight = (int) (cy * scaleVal + 0.5f);
		return bfo;
	}
	/**
	 * 获取缩放采样的值
	 * @param cx
	 * @param cy
	 * @param maxDim
	 * @return
	 */
	private static int getClosestResampleSize(int cx, int cy, int maxDim) {
		int max = Math.max(cx, cy);
		if(maxDim != 0){
			int resample = max/maxDim;
	
			if (resample > 0) {
				return resample;
			}
		}
		return 1;
	}

//	public static BitmapFactory.Options getBitmapDims(String path)
//			throws Exception {
//		BitmapFactory.Options bfo = new BitmapFactory.Options();
//		bfo.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(path, bfo);
//		return bfo;
//	}

	public static Bitmap scaleToSizeOrign(Bitmap src, int width, int height) {
		if (src == null){
			return null;
		}
		int bitmapW = src.getWidth();
		int bitmapH = src.getHeight();
		
		if (bitmapW > width && bitmapH > height) {
			if (bitmapW / (double) width >= bitmapH / (double) height) {
				height = (int) ((double) bitmapH * width / bitmapW);
			} else {
				width = (int) ((double) bitmapW * height / bitmapH);
			}
		} else if (bitmapW > width && bitmapH < height) {
			height = (int) ((double) bitmapH * width / bitmapW);
		} else if (bitmapW < width && bitmapH > height) {
			width = (int) ((double) bitmapW * height / bitmapH);
		} else {
			if ((double) width / bitmapW >= (double) height / bitmapH) {
				width = (int) (bitmapW * (double) height / bitmapH);
			} else {
				height = (int) (bitmapH * (double) width / bitmapW);
			}
		}

		return zoomImage(src, width, height);
	}
	
	/**
	 * 对图片进行缩放，根据传入的横宽最大值进行缩放
	 * @param path : 图片文件路径
	 * @param maxDim : 缩放横宽的最大值
	 * @return
	 * @throws Exception
	 */
	 public static Bitmap resampleImage(String path, int maxDim) throws Exception {
			BitmapFactory.Options bfo = new BitmapFactory.Options();
			bfo.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, bfo);
			
			BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
			optsDownSample.inSampleSize = getClosestResampleSize(bfo.outWidth, bfo.outHeight, maxDim);
			
			Bitmap bmpt = BitmapFactory.decodeFile(path, optsDownSample);
			Matrix m = new Matrix();
			
			BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(), bmpt.getHeight(), maxDim);
			m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
					(float) optsScale.outHeight / (float) bmpt.getHeight());

			int sdk = Integer.valueOf(Build.VERSION.SDK_INT).intValue();
			if (sdk > 4) {
				int rotation = ExifUtils.getExifRotation(path);
				if (rotation != 0) {
					m.postRotate(rotation);
				}
			}
			
			Bitmap ret = Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(), bmpt.getHeight(), m, true);
			if(ret != bmpt){//CreateBitmap可能会和原图一致。链接：http://developer.android.com/reference/android/graphics/Bitmap.html#createBitmap%28android.graphics.Bitmap,%20int,%20int,%20int,%20int%29
				if(null != bmpt && !bmpt.isRecycled()){
					bmpt.recycle();
				}
			}
			return ret;
		}
	    
	    public static void resampleImageAndSaveToNewLocation(String pathInput,
				String pathOutput) throws Exception {
			Bitmap bmp = resampleImage(pathInput, 640);
			OutputStream out = new FileOutputStream(pathOutput);
			bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
			out.flush();
			out.close();
		}
	    
	public static boolean sacleSave800(Bitmap src, String filePath){
		return scaleSave(src, filePath, 800);
	}
	/**
	 * 等比缩小、压缩并保存
	 * @param src 原图片
	 * @param filePath 保存路径
	 * @param maxScaleWH 缩放横宽的最大值
	 * @return 保存是否成功
	 */
	public static boolean scaleSave(Bitmap src, String filePath, int maxScaleWH){
		if(null == src){
			return false;
		}
		Bitmap tmp = src;
		int sWidth = src.getWidth();
		int sHeight = src.getHeight();
		int maxBitmapWH = Math.max(sWidth, sHeight);
		int minBitmapWH = Math.min(sWidth, sHeight);
		
		if(maxBitmapWH > maxScaleWH){
			//原图比较大
			int minScaleWH = minBitmapWH*maxScaleWH/maxBitmapWH;
			if(sWidth > sHeight){
				//例如：bitmap 是: 800*480
				tmp = zoomImage(src, maxScaleWH, minScaleWH);
			}else {
				//例如：bitmap 是: 480*800
				tmp = zoomImage(src, minScaleWH, maxScaleWH);
			}
		}
		
		try {
			File file = new File(filePath);
			FileOutputStream fout = new FileOutputStream(file);
			tmp.compress(Bitmap.CompressFormat.JPEG, 70, fout);
			fout.flush();
			fout.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			tmp = null;
		}	   
	}	


	public static void drawNinepath(Context context, Canvas c, int id, Rect rect) {
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
		NinePatch patch = new NinePatch(bmp, bmp.getNinePatchChunk(), null);
		patch.draw(c, rect);
	}
	
	/** 
	* 图片透明度处理 
	* 
	* @param sourceImg 
	*            原始图片 
	* @param number 
	*            透明度 
	* @return 
	*/  
	public static Bitmap setAlpha(Bitmap sourceImg, int number) {  
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];  
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值  
		
		number = number * 255 / 100;  
		for (int i = 0; i < argb.length; i++) {  
			argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);// 修改最高2位的值  
		}  
		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Config.ARGB_8888);  
		return sourceImg;  
	}  

	/**
	 * PNG装Bitmap
	 * @param path
	 * @return
	 */
	public static Bitmap PNGToBitmap(String path) {
		File file = new File(path);
		if (!file.exists()) {
			MLog.w("FileUtils", path + " is not exits");
			return null;
		}
		Bitmap bm = null;
		BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
		bfoOptions.inDither = false;
		bfoOptions.inPurgeable = true;
		bfoOptions.inInputShareable = true;
		bfoOptions.inTempStorage = new byte[16 * 1024];

		FileInputStream fs = null;
		try {
			fs = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			MLog.w("FileUtils", path + " is not exits");
		}

		try {
			if (fs != null)
				bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
						bfoOptions);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}

		return bm;
	}
	
	/**
	 * bmp保存成PNG
	 * @param bmp
	 * @param filepath
	 * @return
	 */
	public static boolean Bitmap2PNG(Bitmap bmp, String filepath) {
		if (bmp == null || filepath == null)
			return false;
		OutputStream stream = null;
		try {
			File file = new File(filepath);
			File dir = new File(file.getParent());
			if (!dir.exists()){
				dir.mkdirs();
			}
			
			if (file.exists()){
				file.delete();
			}

			stream = new FileOutputStream(filepath);
			if (bmp.compress(Bitmap.CompressFormat.PNG, 85, stream)) {
				stream.flush();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (Exception e) {
			}
		}
		return false;
	}
	
	
	/**
	 * bmp保存成JEPG
	 * @param bmp
	 * @param filepath
	 * @return
	 */
	public static boolean Bitmap2JEPG(Bitmap bmp, String filepath) {
		if (bmp == null || filepath == null)
			return false;
		OutputStream stream = null;
		try {
			File file = new File(filepath);
			File dir = new File(file.getParent());
			if (!dir.exists()){
				dir.mkdirs();
			}
			
			if (file.exists()){
				file.delete();
			}

			stream = new FileOutputStream(filepath);
			if (bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream)) {
				stream.flush();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (Exception e) {
			}
		}
		return false;
	}

	/**
     * 截图当前acitivy
     * @param activity
     * @return
     */
    public static Bitmap takeScreenShot(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b = view.getDrawingCache();

		return b;
	}
    /**
     * 从raw resource读取图片
     * @param context
     * @param id
     * @return
     */
    public static Bitmap getBitmap(Context context, int id) {
		InputStream is = context.getResources().openRawResource(id);
		Bitmap bitmap = BitmapFactory.decodeStream(is);

		return bitmap;
	}
		
}
