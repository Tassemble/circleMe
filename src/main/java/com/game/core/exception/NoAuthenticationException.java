package com.game.core.exception;

/**
 * @author CHQ
 * @since 2013-7-28
 */
public class NoAuthenticationException extends BombException {

	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2847432031651378260L;

	int code;
	
	String message;
	String action;
	
	public NoAuthenticationException(int code, String message, String action) {
		super(code, message);
		this.code = code;
		this.message = message;
		this.action = action;
	}
	
	
	public NoAuthenticationException(String message, String action) {
		super(ExceptionConstant.NO_AUTHENTICATION_CODE, message);
		this.code = ExceptionConstant.NO_AUTHENTICATION_CODE;
		this.message = message;
		this.action = action;
	}
	
	
	public NoAuthenticationException(String action) {
		super(ExceptionConstant.NO_AUTHENTICATION_CODE, "action is :" + action);
		this.code = ExceptionConstant.NO_AUTHENTICATION_CODE;
		this.action = action;
	}
	
}
