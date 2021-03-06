package com.hfour.base.parser;

import java.io.InputStream;

public interface InputStreamParser<T> {

	/**
	 * 解析流
	 * 
	 * @param inputStream
	 *            实现中不需关系inputStream的关闭
	 * @return
	 */
	T parser(InputStream inputStream);

}
