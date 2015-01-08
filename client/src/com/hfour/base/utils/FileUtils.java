package com.hfour.base.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.hfour.base.constants.GlobalParams;
/**
 * 读取data/data目录下使用的方法：<br>
 * context.getDir, openFileOutput, openFileInput
 * @author Tony
 *
 */
public class FileUtils {
	/*** SD卡根目录 */
	public final static String CFG_PATH_SDCARD = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	/*** APP 的 SD卡根目录 */
	public final static String CFG_PATH_SDCARD_APP_DIR = CFG_PATH_SDCARD
			+ File.separator + "AWIFI";
	/*** SD卡上image目录 */
	public final static String CFG_PATH_SDCARD_APP_IMAGE = CFG_PATH_SDCARD_APP_DIR
			+ File.separator + "image";
	/**SD卡上的缓存目录*/
	public final static String CFG_PATH_SDCARD_APP_CACHE = CFG_PATH_SDCARD_APP_DIR
			+ File.separator + "cache";
	/*** SD卡上database目录 */
	public final static String CFG_PATH_SDCARD_DATABASE_DIR = CFG_PATH_SDCARD_APP_DIR
			+ File.separator + "database";

	public static final String CFG_APP_CRASH_FILE = CFG_PATH_SDCARD_APP_DIR+ File.separator + "appCrash.log";
	

	/**
	 * 初始化应用的路径
	 */
	public static void initAppFile(){
		if(!IsCanUseSdCard()){
			return;
		}
		File appFile = new File(CFG_PATH_SDCARD_APP_DIR);
		if(!appFile.exists()){
			appFile.mkdir();
		}
		
		File imgFile = new File(CFG_PATH_SDCARD_APP_IMAGE);
		if(!imgFile.exists()){
			imgFile.mkdir();
		}
	}
	/**
	 * 获取图片缓存目录
	 * 
	 * @return
	 */
	public static String getImageCachePath() {
		String cachePath;
		if (IsCanUseSdCard()) {
			File file = new File(CFG_PATH_SDCARD_APP_IMAGE);
			if (!file.exists()) {
				file.mkdirs();
			}
			cachePath = file.getPath();
		} else {
			cachePath = GlobalParams.gContext.getCacheDir().getPath()
					+ File.separator + "image";
		}
		return cachePath;
	}


	/**
	 * 通过Url获取对于缓存文件名，一个url唯一对应一个缓存文件名
	 */
	public static String getImageCacheFileName(String imageUrl) {
		String imagePath;
		String fileName = "";
		if (imageUrl != null && imageUrl.length() != 0) {
			fileName = MD5.toMD5String(imageUrl);
		}
		imagePath = getImageCachePath();
		imagePath += File.separator + fileName;
		return imagePath;
	}

	/**
	 * 将文件保存到Data目录
	 * 
	 * @param context
	 * @param inStream
	 * @param fileName
	 * @return
	 */
	public static boolean saveToData(InputStream inStream, String fileName) {
		FileOutputStream fos = null;
		try {
			fos = GlobalParams.gContext.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			return true;
		} catch (Exception e) {
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
		return false;
	}

	/**
	 * 将obj 存储到data目录
	 * 
	 * @param obj
	 * @param fileName
	 */
	public static void saveObjToData(Object obj, String fileName) {
		ObjectOutputStream oos = null;
		try {
			FileOutputStream fis = GlobalParams.gContext.openFileOutput(
					fileName, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fis);
			oos.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * data 目录下去输入流 <BR/>
	 * 调用者需要关闭流
	 * 
	 * @param fileName
	 * @return
	 */
	public static FileInputStream getFromData(String fileName) {
		FileInputStream is = null;
		try {
			is = GlobalParams.gContext.openFileInput(fileName);
		} catch (IOException e) {
		}
		return is;
	}

	/**
	 * 将Data目录下的图片取出
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmapFromData(String fileName) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		try {
			fis = getFromData(fileName);
			bitmap = BitmapFactory.decodeStream(fis);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 判断sd卡是否有用
	 * @return true : SD卡可用
	 */
	public static boolean IsCanUseSdCard() {
		try {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * 获取SD卡有效空间
	 * 
	 * @return
	 */
	public static double getSDAvailaleSize() {
		StatFs stat = new StatFs(CFG_PATH_SDCARD);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;// 额外预留1M
	}

	/**
	 * 把原始PNG数据流保存到图片缓存路径
	 * 
	 * @param pngOS
	 * @param imageUrl
	 */
	public static void savePng(ByteArrayOutputStream pngOS, String imageUrl) {
		if (null == pngOS) {
			return;
		}
		String fileName = FileUtils.getImageCacheFileName(imageUrl);
		OutputStream outStream = null;
		File pngFile = new File(fileName);
		if (pngFile.exists()) {
			pngFile.delete();
		}

		try {
			outStream = new FileOutputStream(fileName);
			byte[] bytes = pngOS.toByteArray();
			outStream.write(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != outStream) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}



	/**
	 * 从URI转换成filepath
	 * @param contentUri
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String UriToFilePath(Uri contentUri){
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT; 
		
		String filePath = null;
		
		if(isKitKat && (DocumentsContract.isDocumentUri(GlobalParams.gContext, contentUri))){
		    String wholeID = DocumentsContract.getDocumentId(contentUri);
		    String id = wholeID.split(":")[1];
		    String[] column = { MediaStore.Images.Media.DATA };
		    String sel = MediaStore.Images.Media._ID + "=?";
		    Cursor cursor = GlobalParams.gContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
		            sel, new String[] { id }, null);
		    int columnIndex = cursor.getColumnIndex(column[0]);
		    if (cursor.moveToFirst()) {
		        filePath  = cursor.getString(columnIndex);
		    }
		    cursor.close();
		}else{
		    String[] projection = { MediaStore.Images.Media.DATA };
		    Cursor cursor = GlobalParams.gContext.getContentResolver().query(contentUri, projection, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    filePath = cursor.getString(column_index);
		}
		return filePath;
	}
	/**
	 * 图片文件
	 * @param context
	 * @param filePath
	 * @return
	 */
	public static Uri imageFileToContentUri(String filePath) {  
		File imgFile = new File(filePath);
		if(!imgFile.exists()){
			return null;
		}
		
		Cursor cursor = GlobalParams.gContext.getContentResolver().query(  
               MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  
               new String[] { MediaStore.Images.Media._ID },  
               MediaStore.Images.Media.DATA + "=? ",  
               new String[] { filePath }, null);  
		
		if (cursor != null && cursor.moveToFirst()) {  
           int id = cursor.getInt(cursor  
                   .getColumnIndex(MediaStore.MediaColumns._ID));  
           Uri baseUri = Uri.parse("content://media/external/images/media");  
           return Uri.withAppendedPath(baseUri, "" + id);  
		} else {
			if (imgFile.exists()) {  
               ContentValues values = new ContentValues();  
               values.put(MediaStore.Images.Media.DATA, filePath);  
               return GlobalParams.gContext.getContentResolver().insert(  
                       MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
           } else {  
               return null;  
           }  
       }  
   }
	
	
	/**
	 * 把简单的内容写入文本
	 * @param content
	 * @param path
	 */
	public static void saveString(String content, String path){
		if(null == content || null == path){
			return;
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(path);
			fw.write(content);
			fw.flush();
		} catch (Exception e) {
		}finally{
			try {
				if(null != fw){
					fw.close();
				} 
			}catch (Exception e) {
			}
		}
	}
	
	public static String readString(String path){
		if(null == path){
			return null;
		}
		FileInputStream fileInS = null;
		InputStreamReader inRead = null;
		BufferedReader fr = null;
		String content = null;
		try {
			fileInS = new FileInputStream(new File(path));
			inRead = new InputStreamReader(fileInS);
			fr = new BufferedReader(inRead);
			String line = null;
			StringBuilder sBuilder = new StringBuilder();
			while((line = fr.readLine()) != null){
				sBuilder.append(line);
				sBuilder.append("\n");
			}
			content = sBuilder.toString();
		} catch (Exception e) {
		}finally{
			try {
				if(null != fileInS){
					fileInS.close();
				}
				if(null != inRead){
					inRead.close();
				}
				if(null != fr){
					fr.close();
				}
			} catch (Exception e2) {
			}
		}
		return content;
	}
	
	/**
	 * 获取下载是临时文件
	 * 
	 * @param gameCode
	 * @return
	 */
	public static String getDownloadTmpFilePath(String pkgname) {
		return CFG_PATH_SDCARD_APP_CACHE + File.separator + pkgname
				+ ".jolo";
	}

	/**
	 * 获取本地已下载的游戏APK文件
	 * 
	 * @param gameCode
	 * @return
	 */
	public static String getGameAPKFilePath(String pkgname) {
		return CFG_PATH_SDCARD_APP_CACHE + File.separator + pkgname
				+ ".apk";
	}
}
