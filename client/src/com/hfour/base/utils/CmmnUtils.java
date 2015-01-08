package com.hfour.base.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * 一些通用的接口
 * 
 * @author Tony
 * 
 */
public class CmmnUtils {
	/**
	 * 返回的格式["1.jpg","2.jpg","3.jpg"]
	 * 
	 * @param strings
	 * @return
	 */
	public static String getArrayListParams(ArrayList<String> strings) {
		if (null == strings) {
			return null;
		}
		StringBuilder paramsBuilder = new StringBuilder();
		boolean isFirst = true;
		paramsBuilder.append("[");
		for (String str : strings) {
			if (isFirst) {
				isFirst = false;
			} else {
				paramsBuilder.append(",");
			}
			paramsBuilder.append("\"");
			paramsBuilder.append(str);
			paramsBuilder.append("\"");
		}
		paramsBuilder.append("]");
		String params = paramsBuilder.toString();
		return params;

	}

	/**
	 * 检测邮箱地址是否合法
	 * 
	 * @param email
	 * @return true合法 false不合法
	 */
	public static boolean isEmail(String email) {
		if (TextUtils.isEmpty(email)) {
			return false;
		}
		// Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
		Pattern p = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}
}
