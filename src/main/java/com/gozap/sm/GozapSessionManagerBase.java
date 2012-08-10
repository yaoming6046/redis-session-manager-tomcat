package com.gozap.sm;

import org.apache.catalina.session.ManagerBase;

import com.gozap.sm.help.GozapJavaJson;
import com.gozap.sm.help.GozapJson;
import com.gozap.sm.redis.GozapFilter;
import com.gozap.sm.redis.RedisOperator;

abstract public class GozapSessionManagerBase extends ManagerBase {

	protected static final String defaultHostPort = "172.16.2.5:6379";

	// ===================必须注入=====================
	private int maxActive = 200;
	private int maxIdle = 100;
	private int minIdle = 50;
	private int maxWait = 10000;
	private String hostPorts = defaultHostPort;// 逗号分割redis,冒号分割ip,port.例如172.16.2.1:6379,172.16.2.1:6379

	// ===================非必须注入====================
	protected int expireTime = 30 * 60;// 默认session过期时间
	private GozapFilter filter;
	// cookie信息
	protected int cookieMaxAge = 30 * 60;
	protected RedisOperator redis = new RedisOperator();
	// ===================不需要注入====================
	protected GozapJson json = new GozapJavaJson();

	// +++++++++++++++++++++方法区++++++++++++++++
	public int getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	public int getCookieMaxAge() {
		return cookieMaxAge;
	}

	public void setCookieMaxAge(int cookieMaxAge) {
		this.cookieMaxAge = cookieMaxAge;
	}

	public GozapJson getJson() {
		return json;
	}

	public void setJson(GozapJson json) {
		this.json = json;
	}

	public GozapFilter getFilter() {
		return filter;
	}

	public void setFilter(GozapFilter filter) {
		this.filter = filter;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public String getHostPorts() {
		return hostPorts;
	}

	public void setHostPorts(String hostPorts) {
		this.hostPorts = hostPorts;
	}

}
