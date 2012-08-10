package com.gozap.sm.redis;

import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisOperator {

	/**
	 * 获取session对应的map
	 * 
	 * @param pool
	 * @param sessionId
	 * @return
	 */
	public Map<String, String> hmget(JedisPool pool, String sessionId) {
		Jedis jedis = null;

		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			return null;
		}

		try {
			return jedis.hgetAll(sessionId);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 添加多个域到session中
	 * 
	 * @param pool
	 * @param sessionId
	 * @param field
	 */
	public void hmset(JedisPool pool, String sessionId, Map<String, String> field) {
		Jedis jedis = null;

		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
		}

		try {
			jedis.hmset(sessionId, field);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 保存信息
	 * 
	 * @param key
	 * @param value
	 */
	public void set(JedisPool pool, String sessionId, String sessionStr) {
		Jedis jedis = null;

		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
		}

		try {
			jedis.set(sessionId, sessionStr);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 保存某个域信息
	 * 
	 * @param field
	 * @param value
	 */
	public void hset(JedisPool pool, String sessionId, String field, String value) {
		Jedis jedis = null;

		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
		}

		try {
			jedis.hsetnx(sessionId, field, value);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 获取信息
	 * 
	 * @param key
	 * @return
	 */
	public String get(JedisPool pool, String sessionId) {
		Jedis jedis = null;

		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			return null;
		}

		try {
			return jedis.get(sessionId);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 获取某个field信息
	 * 
	 * @param key
	 * @return
	 */
	public String hget(JedisPool pool, String sessionId, String field) {
		Jedis jedis = null;

		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			return null;
		}

		try {
			return jedis.hget(sessionId, field);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 获取session中的某一个field
	 * 
	 * @param key
	 * @return
	 */
	public Long hdel(JedisPool pool, String sessionId, String field) {
		Jedis jedis = null;

		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
			return null;
		}

		try {
			return jedis.hdel(sessionId, field);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 查看是否存在该信息
	 * 
	 * @param key
	 * @return
	 */
	public boolean exist(JedisPool pool, String sessionId) {
		Jedis jedis = null;

		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
		}

		try {
			return jedis.exists(sessionId);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 移除信息
	 * 
	 * @param key
	 */
	public void remove(JedisPool pool, String sessionId) {
		Jedis jedis = null;

		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
		}

		try {
			jedis.del(sessionId);
		} finally {
			pool.returnResource(jedis);
		}
	}

	/**
	 * 设置过期时间
	 * 
	 * @param key
	 * @param seconds
	 */
	public void expire(JedisPool pool, String sessionId, int seconds) {
		Jedis jedis = null;

		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
			pool.returnBrokenResource(jedis);
		}

		try {
			jedis.expire(sessionId, seconds);
		} finally {
			pool.returnResource(jedis);
		}
	}

}
