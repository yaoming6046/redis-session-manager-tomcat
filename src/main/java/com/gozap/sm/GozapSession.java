package com.gozap.sm;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardSession;
import org.apache.tomcat.util.ExceptionUtils;

import com.gozap.sm.redis.GozapSessionManagerRedis;

public class GozapSession extends StandardSession {

	private static final long serialVersionUID = 1L;

	public GozapSession(Manager manager) {
		super(manager);
		if (manager instanceof GozapSessionManagerBase) {
			throw new RuntimeException("the manager is not GozapSessionManagerBase");
		}
	}

	public void setAttribute(Map<String, Object> map) {
		this.attributes = map;
	}

	public Map<String, Object> getAttribute() {
		return attributes;
	}

	@Override
	public void setAttribute(String name, Object value, boolean notify) {
		if (((GozapSessionManagerRedis) manager).isUseDefault()) {
			setSessionWithRedisAttribute(name, value, notify);
		} else {
			setRedisAttribute(name, value, notify);
		}
	}

	private void setSessionWithRedisAttribute(String name, Object value, boolean notify) {
		super.setAttribute(name, value, notify);
		if (value != null) {
			((GozapSessionManagerRedis) manager).addAttribute(getId(), name, value);
		}
	}

	private void setRedisAttribute(String name, Object value, boolean notify) {

		if (null == name) {
			return;
		}
		if (null == value) {
			removeAttribute(name, notify);
			return;
		}
		setAttributeEvent(name, value, notify);
	}

	private void setAttributeEvent(String name, Object value, boolean notify) {
		// Construct an event with the new value
		HttpSessionBindingEvent event = null;

		// Call the valueBound() method if necessary
		if (notify && value instanceof HttpSessionBindingListener) {
			// Don't call any notification if replacing with the same value
			Object oldValue = attributes.get(name);
			if (value != oldValue) {
				event = new HttpSessionBindingEvent(getSession(), name, value);
				try {
					((HttpSessionBindingListener) value).valueBound(event);
				} catch (Throwable t) {
					manager.getContainer().getLogger()
							.error(sm.getString("gozapSession.bindingEvent"), t);
				}
			}
		}

		// Replace or add this attribute
		Object unbound = attributes.put(name, value);

		// Call the valueUnbound() method if necessary
		if (notify && (unbound != null) && (unbound != value)
				&& (unbound instanceof HttpSessionBindingListener)) {
			try {
				((HttpSessionBindingListener) unbound).valueUnbound(new HttpSessionBindingEvent(
						getSession(), name));
			} catch (Throwable t) {
				ExceptionUtils.handleThrowable(t);
				manager.getContainer().getLogger()
						.error(sm.getString("gozapSession.bindingEvent"), t);
			}
		}

		if (!notify)
			return;

		// Notify interested application event listeners
		Context context = (Context) manager.getContainer();
		Object listeners[] = context.getApplicationEventListeners();
		if (listeners == null)
			return;
		for (int i = 0; i < listeners.length; i++) {
			if (!(listeners[i] instanceof HttpSessionAttributeListener))
				continue;
			HttpSessionAttributeListener listener = (HttpSessionAttributeListener) listeners[i];
			try {
				if (unbound != null) {
					fireContainerEvent(context, "beforeSessionAttributeReplaced", listener);
					if (event == null) {
						event = new HttpSessionBindingEvent(getSession(), name, unbound);
					}
					listener.attributeReplaced(event);
					fireContainerEvent(context, "afterSessionAttributeReplaced", listener);
				} else {
					fireContainerEvent(context, "beforeSessionAttributeAdded", listener);
					if (event == null) {
						event = new HttpSessionBindingEvent(getSession(), name, value);
					}
					listener.attributeAdded(event);
					fireContainerEvent(context, "afterSessionAttributeAdded", listener);
				}
			} catch (Throwable t) {
				ExceptionUtils.handleThrowable(t);
				try {
					if (unbound != null) {
						fireContainerEvent(context, "afterSessionAttributeReplaced", listener);
					} else {
						fireContainerEvent(context, "afterSessionAttributeAdded", listener);
					}
				} catch (Exception e) {
					// Ignore
				}
				manager.getContainer().getLogger()
						.error(sm.getString("gozapSession.attributeEvent"), t);
			}
		}
	}

	@Override
	public void removeAttribute(String name, boolean notify) {
		if (((GozapSessionManagerRedis) manager).isUseDefault()) {
			removeSessionWithRedisAttribute(name, notify);
		} else {
			removeRedisAttribute(name, notify);
		}
	}

	public void removeRedisAttribute(String name, boolean notify) {

		if (null == name) {
			return;
		}
		Object value = ((GozapSessionManagerRedis) manager).removeAttribute(getId(), name);
		if (null == value) {
			return;
		}
		removeAttributeEvent(name, value);
	}

	private void removeAttributeEvent(String name, Object value) {
		// Notify interested application event listeners
		Context context = (Context) manager.getContainer();
		Object listeners[] = context.getApplicationEventListeners();
		if (listeners == null)
			return;

		// Call the valueUnbound() method if necessary
		HttpSessionBindingEvent event = null;
		if (value instanceof HttpSessionBindingListener) {
			event = new HttpSessionBindingEvent(getSession(), name, value);
			((HttpSessionBindingListener) value).valueUnbound(event);
		}
		for (int i = 0; i < listeners.length; i++) {
			if (!(listeners[i] instanceof HttpSessionAttributeListener))
				continue;
			HttpSessionAttributeListener listener = (HttpSessionAttributeListener) listeners[i];
			try {
				fireContainerEvent(context, "beforeSessionAttributeRemoved", listener);
				if (event == null) {
					event = new HttpSessionBindingEvent(getSession(), name, value);
				}
				listener.attributeRemoved(event);
				fireContainerEvent(context, "afterSessionAttributeRemoved", listener);
			} catch (Throwable t) {
				ExceptionUtils.handleThrowable(t);
				try {
					fireContainerEvent(context, "afterSessionAttributeRemoved", listener);
				} catch (Exception e) {
					// Ignore
				}
				manager.getContainer().getLogger()
						.error(sm.getString("gozapSession.attributeEvent"), t);
			}
		}
	}

	public void removeSessionWithRedisAttribute(String name, boolean notify) {
		super.removeAttribute(name, notify);
		((GozapSessionManagerRedis) manager).removeAttribute(getId(), name);
	}

	protected boolean isAttributeDistributable(String name, Object value) {
		return value instanceof Serializable;
	}
}
