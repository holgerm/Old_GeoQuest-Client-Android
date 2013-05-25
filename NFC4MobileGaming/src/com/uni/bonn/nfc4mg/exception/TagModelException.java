package com.uni.bonn.nfc4mg.exception;

/**
 * Custom exception class defined by framework. This exception will be thrown in
 * case of tag is read-only.
 * 
 * @author shubham
 */
public class TagModelException extends Exception {

	private String message = null;

	public TagModelException() {
		super();
	}

	public TagModelException(String msg) {
		super(msg);
		this.message = msg;
	}

	public TagModelException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
