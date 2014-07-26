package com.game.core.exception;

public class BombException extends RuntimeException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7387519885663281215L;

	
	
	int code;
	String message;
	private String action;
	
	
	public BombException(int code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}
	public BombException(int code, String action, String message) {
		super(message);
		this.code = code;
		this.message = message;
		this.action = action;
	}


	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}
	
	
	
}
