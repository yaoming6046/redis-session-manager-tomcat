package com.gozap.sm;

import java.io.Serializable;
import java.util.Map;

import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardSession;

import com.gozap.sm.redis.GozapSessionManagerRedis;

public class GozapSession extends StandardSession {

	private static final long serialVersionUID = 1L;

	public GozapSession(Manager manager) {
		super(manager);
	}

	public void setAttribute(Map<String, Object> map) {
		this.attributes = map;
	}

	public Map<String, Object> getAttribute() {
		return attributes;
	}

	// @Override
	// public void setAttribute(String name, Object value) {
	// setAttribute(name, value, true);
	// }

	// 覆盖父类方法，设置属性直接加入到redis中，而不是放入内从中
	// @Override
	// public void setAttribute(String name, Object value, boolean notify) {
	//
	// // Name cannot be null
	// if (name == null)
	// throw new
	// IllegalArgumentException(sm.getString("standardSession.setAttribute.namenull"));
	//
	// // Null value is the same as removeAttribute()
	// if (value == null) {
	// removeAttribute(name);
	// return;
	// }
	//
	// if ((manager != null) && manager.getDistributable() &&
	// !isAttributeDistributable(name, value))
	// throw new
	// IllegalArgumentException(sm.getString("standardSession.setAttribute.iae",
	// name));
	//
	// HttpSessionBindingEvent event = null;
	//
	// // Call the valueBound() method if necessary
	// if (notify && value instanceof HttpSessionBindingListener) {
	// // Don't call any notification if replacing with the same value
	// Object oldValue = ((GozapSessionManagerRedis)
	// manager).getAttribute(getId(), name);
	//
	// if (value != oldValue) {
	// event = new HttpSessionBindingEvent(getSession(), name, value);
	// try {
	// ((HttpSessionBindingListener) value).valueBound(event);
	// } catch (Throwable t) {
	// manager.getContainer().getLogger().error(sm.getString("standardSession.bindingEvent"),
	// t);
	// }
	// }
	// }
	//
	// // Replace or add this attribute
	// Object unbound = ((GozapSessionManagerRedis)
	// manager).addAttribute(getId(), name, value);
	//
	// // Call the valueUnbound() method if necessary
	// if (notify && (unbound != null) && (unbound != value) && (unbound
	// instanceof HttpSessionBindingListener)) {
	// try {
	// ((HttpSessionBindingListener) unbound).valueUnbound(new
	// HttpSessionBindingEvent(getSession(), name));
	// } catch (Throwable t) {
	// ExceptionUtils.handleThrowable(t);
	// manager.getContainer().getLogger().error(sm.getString("standardSession.bindingEvent"),
	// t);
	// }
	// }
	//
	// if (!notify)
	// return;
	//
	// // Notify interested application event listeners
	// Context context = (Context) manager.getContainer();
	// Object listeners[] = context.getApplicationEventListeners();
	// if (listeners == null)
	// return;
	// for (int i = 0; i < listeners.length; i++) {
	// if (!(listeners[i] instanceof HttpSessionAttributeListener))
	// continue;
	// HttpSessionAttributeListener listener = (HttpSessionAttributeListener)
	// listeners[i];
	// try {
	// if (unbound != null) {
	// context.fireContainerEvent("beforeSessionAttributeReplaced", listener);
	// if (event == null) {
	// event = new HttpSessionBindingEvent(getSession(), name, unbound);
	// }
	// listener.attributeReplaced(event);
	// context.fireContainerEvent("afterSessionAttributeReplaced", listener);
	// } else {
	// context.fireContainerEvent("beforeSessionAttributeAdded", listener);
	// if (event == null) {
	// event = new HttpSessionBindingEvent(getSession(), name, value);
	// }
	// listener.attributeAdded(event);
	// context.fireContainerEvent("afterSessionAttributeAdded", listener);
	// }
	// } catch (Throwable t) {
	// ExceptionUtils.handleThrowable(t);
	// try {
	// if (unbound != null) {
	// context.fireContainerEvent("afterSessionAttributeReplaced", listener);
	// } else {
	// context.fireContainerEvent("afterSessionAttributeAdded", listener);
	// }
	// } catch (Exception e) {
	// // Ignore
	// }
	// manager.getContainer().getLogger().error(sm.getString("standardSession.attributeEvent"),
	// t);
	// }
	// }
	//
	// }
	@Override
	public void setAttribute(String name, Object value, boolean notify) {
		super.setAttribute(name, value, notify);
		if (value != null) {
			((GozapSessionManagerRedis) manager).addAttribute(getId(), name, value);
		}

	}

	// 覆盖父类的removeattribute方法，父类方法直接从内从中删除
	// @Override
	// public void removeAttribute(String name, boolean notify) {
	//
	// // Avoid NPE
	// if (name == null)
	// return;
	//
	// // Remove this attribute from our collection
	// // Object value = attributes.remove(name);
	// Object value = ((GozapSessionManagerRedis)
	// manager).removeAttribute(getId(), name);
	//
	// // Do we need to do valueUnbound() and attributeRemoved() notification?
	// if (!notify || (value == null)) {
	// return;
	// }
	//
	// // Call the valueUnbound() method if necessary
	// HttpSessionBindingEvent event = null;
	// if (value instanceof HttpSessionBindingListener) {
	// event = new HttpSessionBindingEvent(getSession(), name, value);
	// ((HttpSessionBindingListener) value).valueUnbound(event);
	// }
	//
	// // Notify interested application event listeners
	// Context context = (Context) manager.getContainer();
	// Object listeners[] = context.getApplicationEventListeners();
	// if (listeners == null)
	// return;
	// for (int i = 0; i < listeners.length; i++) {
	// if (!(listeners[i] instanceof HttpSessionAttributeListener))
	// continue;
	// HttpSessionAttributeListener listener = (HttpSessionAttributeListener)
	// listeners[i];
	// try {
	// context.fireContainerEvent("beforeGozapSessionAttributeRemoved",
	// listener);
	// if (event == null) {
	// event = new HttpSessionBindingEvent(getSession(), name, value);
	// }
	// listener.attributeRemoved(event);
	// context.fireContainerEvent("afterGozapSessionAttributeRemoved",
	// listener);
	// } catch (Throwable t) {
	// ExceptionUtils.handleThrowable(t);
	// try {
	// context.fireContainerEvent("afterGozapSessionAttributeRemoved",
	// listener);
	// } catch (Exception e) {
	// // Ignore
	// }
	// manager.getContainer().getLogger().error(sm.getString("gozapSession.attributeEvent"),
	// t);
	// }
	// }
	//
	// }

	@Override
	public void removeAttribute(String name, boolean notify) {
		super.removeAttribute(name, notify);
		((GozapSessionManagerRedis) manager).removeAttribute(getId(), name);
	}

	protected boolean isAttributeDistributable(String name, Object value) {
		return value instanceof Serializable;
	}
}
