package com.gozap.sm;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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

	private void afterInvoke(Request request, Response response, Context context) {
		HttpSession session = request.getSession();
		if (session != null) {
			String id = session.getId();

			GozapSessionManagerRedis manager = (GozapSessionManagerRedis) context.getManager();

			manager.expire(id, manager.getExpireTime());
		}
	}

	/** 设置cookie的属性 */
	private void beforeInvoke(Request request, Response response, Context context) {

		GozapSessionManagerRedis manager = (GozapSessionManagerRedis) context.getManager();

		context.getServletContext().getSessionCookieConfig().setMaxAge(manager.getCookieMaxAge());

		boolean hasCookie = false;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("gpsd".equals(cookie.getName())) {
					hasCookie = true;
				}
			}
		}
		if (!hasCookie) {

			Cookie cookie = new Cookie("gpsd", "gozap_rsm_" + UUID.randomUUID().toString().replaceAll("-", ""));

			cookie.setMaxAge(manager.getCookieMaxAge());

			String contextPath = context.getSessionCookiePath();
			if (contextPath == null) {
				contextPath = context.getSessionCookiePath();

				if (contextPath == null || contextPath.length() == 0) {
					contextPath = context.getEncodedPath();
				}
				if (context.getSessionCookiePathUsesTrailingSlash()) {
					if (!contextPath.endsWith("/")) {
						contextPath = contextPath + "/";
					}
				} else {
					if (contextPath.length() == 0) {
						contextPath = "/";
					}
				}
			}

			cookie.setPath(contextPath);

			// if (context.getSessionCookieDomain() != null) {
			// cookie.setDomain(context.getSessionCookieDomain());
			// } else {
			// if (context.getSessionCookieDomain() == null) {
			//
			// } else {
			// cookie.setDomain(context.getSessionCookieDomain());
			// }
			// }
			response.addSessionCookieInternal(cookie);
		}
	}
}
