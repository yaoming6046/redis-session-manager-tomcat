package com.gozap.sm.help;

import java.util.List;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;

public class GozapFastJson implements GozapJson {

	private Log logger = LogFactory.getLog(this.getClass());

	public Object parseObject(String json) {
		if (null == json) {
			return null;
		}
		Object t = null;
		try {
			t = JSONObject.parse(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("can not parseObject with the string:" + json);
		}
		return t;
	}

	public String format(Object t) {

		if (null == t) {
			return null;
		}

		try {
			return JSONObject.toJSONString(t);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("can not serialize object " + t);
		}
		return "";
	}

	public List<Object> parseArray(String json) {
		if (null == json) {
			return null;
		}
		List<Object> t = null;
		try {
			t = JSONObject.parseArray(json);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("can not parseObject with the string:" + json);
		}
		return t;
	}
}
