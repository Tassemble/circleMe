package com.game.core.exception;

/**
 * @author CHQ
 * @since  1.0.0
 * @date   2013-7-28
 */
public class ActionFailedException extends BombException{

	

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5369529207416028888L;

	int code;
	
	String message;
	String action;
	
	public ActionFailedException(int code, String message, String action) {
		super(code, message);
		this.code = code;
		this.message = message;
		this.action = action;
	}
	
	
	public ActionFailedException(String message, String action) {
		super(ExceptionConstant.ACTION_FAILED_CODE, action, message);
		this.code = ExceptionConstant.ACTION_FAILED_CODE;
		this.message = message;
		this.action = action;
	}
	
	
	public ActionFailedException(String action) {
		super(ExceptionConstant.ACTION_FAILED_CODE, action, "");
		this.code = ExceptionConstant.ACTION_FAILED_CODE;
		this.action = action;
		this.message = "please refresh friends list";
	}
}
