package com.gozap.sm.redis;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import com.gozap.sm.help.Help;
import com.gozap.sm.help.StoreException;

public class JedisPoolExt extends JedisPool {

	private String host;
	private int port = Protocol.DEFAULT_PORT;
	private String poolName;

	public JedisPoolExt(final GenericObjectPool.Config poolConfig, final String host) {
		super(poolConfig, host);
		this.host = host;
	}

	public JedisPoolExt(String host, int port) {
		super(host, port);
		this.host = host;
		this.port = port;
	}

	public JedisPoolExt(final Config poolConfig, final String host, int port, int timeout, final String password) {
		super(poolConfig, host, port, timeout, password);
		this.host = host;
		this.port = port;
	}

	public JedisPoolExt(final GenericObjectPool.Config poolConfig, final String host, final int port) {
		super(poolConfig, host, port);
		this.host = host;
		this.port = port;
	}

	public JedisPoolExt(final GenericObjectPool.Config poolConfig, final String host, final int port, final int timeout) {
		super(poolConfig, host, port, timeout);
		this.host = host;
		this.port = port;
	}

	public Jedis getResources() throws StoreException {
		Jedis jedis = null;
		try {
			jedis = super.getResource();
			return jedis;
		} catch (Exception e) {
			e.printStackTrace();
			super.returnBrokenResource(jedis);
			throw new StoreException(e);
		}
	}

	public void returnResources(Jedis resource) {
		super.returnResource(resource);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		if (Help.isEmpty(getPoolName())) {
			return port + ":" + host;
		}
		return getPoolName();
	}

	public void setPoolName(String name) {
		this.poolName = name;
	}

	public String getPoolName() {
		return poolName;
	}
}
