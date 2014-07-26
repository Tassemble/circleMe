package com.game.core.exception;

public class GamePlayException extends BombException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1834293719285005513L;

	
	int code;
	String message;
	
	public GamePlayException(int code, String message) {
		super(code, message);
		this.code = code;
		this.message = message;
	}
	

	
}
