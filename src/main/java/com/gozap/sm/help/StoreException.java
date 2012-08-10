package com.gozap.sm.help;

public class StoreException extends Exception {

	private static final long serialVersionUID = 1L;

	// private int code;

	public StoreException() {
		super();
	}

	public StoreException(String msg) {
		super(msg);
	}

	public StoreException(Throwable cause) {
		super(cause);
	}

	public StoreException(String message, Throwable cause) {
		super(message, cause);
	}

}
