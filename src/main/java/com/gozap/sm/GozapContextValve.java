package com.gozap.sm;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import com.gozap.sm.redis.GozapSessionManagerRedis;

public class GozapContextValve extends ValveBase {

	private static final String info = "com.gozap.sm.redis.GozapContextValve/1.0";

	public GozapContextValve() {
		super(true);
	}

	public String getInfo() {
		return (info);
	}

	public void invoke(Request request, Response response) throws IOException, ServletException {

		Context context = (Context) getContainer();

		beforeInvoke(request, response, context);
		getNext().invoke(request, response);
		afterInvoke(request, response, context);
	}

	private void beforeInvoke(Request request, Response response, Context context) {
		HttpSession session = request.getSession();
		if (session != null) {
			String id = session.getId();

			GozapSessionManagerRedis manager = (GozapSessionManagerRedis) context.getManager();

			manager.expire(id, manager.getExpireTime());
		}
	}

	/** 设置cookie的属性 */
	private void afterInvoke(Request request, Response response, Context context) {

		GozapSessionManagerRedis manager = (GozapSessionManagerRedis) context.getManager();

		context.getServletContext().getSessionCookieConfig().setMaxAge(manager.getCookieMaxAge());

	}
}
