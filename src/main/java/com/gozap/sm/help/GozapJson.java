package com.gozap.sm.help;

import java.util.List;

/**
 * 序列化方式,默认采用的是java自带的序列化方式,如果采用默认的序列化方式请在外部转成相关类型
 * 
 * @author yaoming
 * 
 */
public interface GozapJson {
	/**
	 * 将字符串反序列化成对象,请确保传入不为空，否则抛出异常，并返回null
	 * 
	 * @param <T>
	 * @param cls
	 * @param comments
	 * @return
	 * @auther yaoming
	 */
	public Object parseObject(String json);

	/**
	 * 将对象序列化成字符串
	 * 
	 * @param t
	 * @return
	 * @auther yaoming
	 */
	public String format(Object t);

	/**
	 * 从json中获取数组对象，如果传入json为空则返回null
	 * 
	 * @param <T>
	 * @param json
	 * @param key
	 *            在json中的key值
	 * @param cls
	 *            转换为数组类型时的每个对象类型
	 * @return
	 * @auther yaoming
	 */
	public List<Object> parseArray(String json);

}
