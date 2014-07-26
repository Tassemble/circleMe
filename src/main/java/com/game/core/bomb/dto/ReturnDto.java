package com.game.core.bomb.dto;


public class ReturnDto {
	public static final int ALREADY_LOGON_CODE = -100;
	public static final int ALREADY_LOGON_CODE_WITH_OTHER_REMOTE_CLIENT = -101;
	
	
	Object extAttrs = null;
	String action;
	int code;
	String message;
	Object result;
	
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
	public ReturnDto(int code, String action, String message) {
		super();
		this.action = action;
		this.code = code;
		this.message = message;
	}
	public ReturnDto(int code, String message) {
		super();
		this.code = code;
		this.message = message;
		this.action = "unkonw";
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public Object getExtAttrs() {
		return extAttrs;
	}
	public void setExtAttrs(Object extAttrs) {
		this.extAttrs = extAttrs;
	}
	
	
	
	
	
	
	
	
}
