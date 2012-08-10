package com.gozap.sm.help;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class GozapJavaJson implements GozapJson {

	private Log logger = LogFactory.getLog(this.getClass());

	private static String CHARSET = "ISO-8859-1";

	public Object parseObject(String json) {
		if (null == json) {
			return null;
		}
		Object t = null;
		try {
			byte[] by = json.getBytes(CHARSET);

			if (by == null)
				throw new NullPointerException();

			ByteArrayInputStream bis = new ByteArrayInputStream(by);
			ObjectInputStream stream = new ObjectInputStream(bis);
			t = stream.readObject();
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
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(bos);
			stream.writeObject(t);
			return bos.toString(CHARSET);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("can not serialize object " + t);
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public List<Object> parseArray(String json) {
		return (List<Object>) parseObject(json);
	}

}
