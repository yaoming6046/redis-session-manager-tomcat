package com.gozap.sm.redis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.gozap.sm.GozapSession;
import com.gozap.sm.GozapSessionManagerBase;
import com.gozap.sm.help.HashStr;

public class GozapSessionManagerRedis extends GozapSessionManagerBase implements Lifecycle {

	private final Log log = LogFactory.getLog(getClass());

	// ===================不需要注入====================
	private String className = "GozapSessionManagerRedis";
	private TreeMap<Long, JedisPoolExt> select;
	private HashStr ketamaHash = HashStr.KETAMA_HASH;

	public void changeSessionId(Session session) {
		super.changeSessionId(session);
		log.info("invoke changeSessionId");
	}

	public Session findSession(String id) throws IOException {
		log.info("invoke findSession");
		if (null == id) {
			return null;
		}

		JedisPool pool = selectPool(id);
		redis.expire(pool, id, expireTime);

		Session s = null;
		if (isUseDefault()) {
			s = super.findSession(id);
		}

		if (s != null && s.isValid()) {
			return s;
		}

		Map<String, String> map = redis.hmget(pool, id);

		Map<String, Object> field = convertToObject(map);

		GozapSession session = (GozapSession) createSession(id);

		if (null != field) {
			session.setAttribute(field);
		}

		sessions.put(id, session);

		return session;
	}

	/** 将String类型的map转化成object类型 */
	private Map<String, Object> convertToObject(Map<String, String> map) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.putAll(map);

		return result;
	}

	public void expire(String sessionId, int seconds) {
		if (null != sessionId) {
			JedisPool pool = selectPool(sessionId);
			redis.expire(pool, sessionId, seconds);
		}
	}

	public Session[] findSessions() {
		log.info("invoke findSessions");
		return null;
	}

	public static final String DEFAULT_VALUE_STRING = "GOZAP_DEFAULT_NULL_STRING";

	public Object addAttribute(String id, String name, Object value) {

		JedisPool pool = selectPool(id);
		if (!(value instanceof String)) {
			throw new IllegalArgumentException("labi session 只接受string类型的信息");
		}
		redis.hset(pool, id, name, (String) value);

		return value;
	}

	public Object getAttribute(String id, String name) {

		JedisPool pool = selectPool(id);

		return redis.hget(pool, id, name);
	}

	public Object removeAttribute(String id, String name) {

		JedisPool pool = selectPool(id);

		Map<String, String> map = redis.hmget(pool, id);

		String value = map.get(name);

		redis.hdel(pool, id, name);

		return value;
	}

	@Override
	public void add(Session session) {
		log.info("invoke add");

		if (isUseDefault()) {
			super.add(session);
		}

		if (null != session) {

			String id = session.getId();
			JedisPool pool = selectPool(id);

			GozapSession s = (GozapSession) session;
			Map<String, Object> map = s.getAttribute();

			if (null != map && !map.isEmpty()) {
				Map<String, String> field = convertToString(map);
				redis.hmset(pool, id, field);
			}
			addDefault(pool, id);
		}

	}

	/** 将object类型的map转化成String类型 */
	private Map<String, String> convertToString(Map<String, Object> map) {
		Map<String, String> result = new HashMap<String, String>();
		if (map != null) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {

				if (!(entry.getValue() instanceof String)) {
					throw new IllegalArgumentException("labi session 只接受string类型的信息");
				}

				result.put(entry.getKey(), (String) entry.getValue());
			}
		}
		return result;
	}

	@Override
	public void remove(Session session) {
		log.info("invoke remove");

		remove(session, false);
	}

	@Override
	public void remove(Session session, boolean update) {
		log.info("invoke removeupdate");
		if (isUseDefault()) {
			super.remove(session, update);
		}

		String id = session.getId();
		JedisPool pool = selectPool(id);
		redis.remove(pool, id);
	}

	@Override
	public Session createEmptySession() {
		log.info("invoke createEmptySession");
		if (isUseDefault()) {
			super.createEmptySession();
		}
		return new GozapSession(this);
	}

	@Override
	protected String generateSessionId() {
		return "YM" + super.generateSessionId() + (getJvmRoute() == null ? "" : getJvmRoute());
	}

	@Override
	public Session createSession(String sessionId) {

		Session s = null;
		if (isUseDefault()) {
			s = super.createSession(sessionId);
		} else {
			s = new GozapSession(this);
			s.setId(generateSessionId());
		}

		log.info("invoke createSession");
		JedisPool pool = selectPool(s.getId());
		addDefault(pool, s.getId());
		return s;
	}

	/** 添加默认的值 */
	private void addDefault(JedisPool pool, String sessionId) {
		// 添加默认的值
		redis.hset(pool, sessionId, isActive, "1");
	}

	public static final String isActive = "g_s_active";

	// ==============================================================

	/** 根据sessionid获取pool */
	public JedisPoolExt selectPool(String sessionId) {

		byte[] digest = ketamaHash.computeMd5(sessionId);
		long hash = ketamaHash.hash(digest, 0);

		Long key = select.ceilingKey(hash);
		if (null == key) {
			key = select.firstKey();
		}
		JedisPoolExt pool = select.get(key);

		return pool;
	}

	@Override
	public void startInternal() throws LifecycleException {

		if (LifecycleState.STARTING == getState()) {
			return;
		}
		super.startInternal();

		log.info(getName() + " start the internal");
		setState(LifecycleState.STARTING);

		connectStore();

		setDistributable(true);
	}

	public void connectStore() throws LifecycleException {
		// 初始化db连接,初始化选择算法
		log.info("init the redis pool");
		List<JedisPoolExt> pools = initJedisPools(initConfig());
		select = new TreeMap<Long, JedisPoolExt>();
		log.info("init the select map");
		for (JedisPoolExt t : pools) {
			for (int i = 0; i < 32; i++) {
				byte[] digest = ketamaHash.computeMd5(t.getName());
				for (int h = 0; h < 4; h++) {
					long m = ketamaHash.hash(digest, h);
					select.put(m, t);
				}
			}
		}
	}

	@Override
	public void stopInternal() throws LifecycleException {
		log.info(getName() + " stop the internal");
		setState(LifecycleState.STOPPING);

		destoryConnect();
		super.stop();
	}

	public void destoryConnect() {
		for (Map.Entry<Long, JedisPoolExt> entry : select.entrySet()) {
			entry.getValue().destroy();
		}
	}

	@Override
	public void backgroundProcess() {
		// log.info(getName() + " backgroundProcess");
	}

	public String getInfo() {
		log.info("invoke getInfo");
		return "GozapSessionManagerRedis/1.0";
	}

	public String getName() {
		// log.info("invoke getName");
		return className;
	}

	/** 初始化jedis的配置 */
	private JedisPoolConfig initConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(getMaxActive());
		config.setMaxIdle(getMaxIdle());
		config.setMinIdle(getMinIdle());
		config.setMaxWait(getMaxWait());
		return config;
	}

	private int toInt(String str, int defaultValue) {
		int i = defaultValue;
		try {
			i = Integer.valueOf(str);
		} catch (Exception e) {
		}
		return i;
	}

	private List<JedisPoolExt> initJedisPools(JedisPoolConfig config) throws LifecycleException {
		List<JedisPoolExt> pools = new ArrayList<JedisPoolExt>();
		if (null != getHostPorts()) {
			if (defaultHostPort.equals(getHostPorts())) {
				log.info("init the jedis pool with the default host and port");
			}

			String[] arr = getHostPorts().split(",");
			if (arr.length > 0) {
				String[] hps;
				// String port, host;
				int port;
				for (String redis : arr) {
					hps = redis.split(":");
					if (hps.length >= 2) {
						port = toInt(hps[1], -1);
						JedisPoolExt pool = new JedisPoolExt(config, hps[0], port);
						if (hps.length >= 3) {
							pool.setPoolName(hps[2]);
						}
						pools.add(pool);
					}
				}
			}
		}

		if (pools.size() == 0) {
			throw new LifecycleException("init redis failed,please check the config file...");
		}
		return pools;
	}

	public int getExpireTime() {
		log.info("invoke getExpireTime");
		return expireTime;
	}

	public void setExpireTime(int expireTime) {
		log.info("invoke setExpireTime");
		this.expireTime = expireTime;
	}

	public Log getLog() {
		log.info("invoke getLog");
		return log;
	}

	public void load() throws ClassNotFoundException, IOException {
		log.info(getName() + "load");
	}

	public void unload() throws IOException {
		log.info(getName() + "unload");
	}

}
