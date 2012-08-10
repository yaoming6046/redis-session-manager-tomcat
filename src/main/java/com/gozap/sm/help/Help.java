package com.gozap.sm.help;

public class Help {

	public static boolean isEmpty(String s) {
		if (null == s || "".equals(s.trim())) {
			return true;
		}
		return false;
	}
}
