package com.gozap.sm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.util.SessionIdGenerator;
import org.junit.Test;

import com.gozap.sm.redis.GozapSessionManagerRedis;
import com.gozap.sm.redis.JedisPoolExt;

public class TestSelectPool {
	GozapSessionManagerRedis manager = new GozapSessionManagerRedis();

	@Test
	public void testSelectPool() throws Exception {
		manager.setHostPorts("172.16.2.5:6379:one1,172.16.2.7:6379:two2,172.16.2.7:6379:three3,172.16.2.7:6379:four4");
		// manager.setHostPorts("172.16.2.5:6379,172.16.2.5:16379,172.16.2.5:26379");
		manager.connectStore();

		Map<String, Integer> map = new HashMap<String, Integer>();
		String key;
		JedisPoolExt ext;
		List<String> allStr = getAllStrings();
		Integer c;
		for (int i = 0; i < EXE_TIMES; i++) {
			key = allStr.get(i);
			ext = manager.selectPool(key);
			c = map.get(ext.getName());
			if (null == c) {
				c = 0;
			}
			c++;
			map.put(ext.getName(), c);
		}

		// show the result

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey() + "  " + entry.getValue() + "\t"
					+ (float) entry.getValue() / EXE_TIMES * 100 + "%");
		}

	}

	public int EXE_TIMES = 10000;
	SessionIdGenerator generator = new SessionIdGenerator();

	private List<String> getAllStrings() {

		List<String> allStrings = new ArrayList<String>(EXE_TIMES);

		for (int i = 0; i < EXE_TIMES; i++) {
			String sessionId = "ym_gozap_rsm_" + generator.generateSessionId();

			allStrings.add(sessionId);
		}

		return allStrings;
	}
}
