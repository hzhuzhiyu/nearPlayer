package com.hfour.base.jsonutils;

import com.alibaba.fastjson.JSON;
/**
 * fastJson使用的时候需要注意：
严格按照JavaBean规范来定义Bean
即：get，set方法，必须都要有，get, set后的字母必须的大写如：getXxx 和 setXxx
Bean 可以没有构造函数，如果有构造函数，必须有一个空的构造函数。
 * @author Tony
 *
 */
public class JsonUtil {
	/**
	 * 从jsonString 中转换为 类型为T的Object
	 * @param jsonString
	 * @param classofT : class type
	 * @return
	 */
	public static <T> T parserObj(String jsonString, Class<T> classofT){
		
		if(null == jsonString){
			return null;
		}
		T objT = null;
		try {
			objT = JSON.parseObject(jsonString, classofT);
		} catch (Exception e) {
		}
		return objT;
	}
	
	/**
	 * 转换成jsonString
	 * @param req
	 * @return
	 */
	public static String objToJson(Object obj){
		String json = "";
		try {
			json = JSON.toJSONString(obj);
		} catch (Exception e) {
			json = "";
		}
		return json;
	}
}
