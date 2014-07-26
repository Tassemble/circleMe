package com.game.bomb.config;



public class BombConfig {
	
	boolean debug = false;
	
	
	String verifyReceiptUrl = null;

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getVerifyReceiptUrl() {
		return verifyReceiptUrl;
	}

	public void setVerifyReceiptUrl(String verifyReceiptUrl) {
		this.verifyReceiptUrl = verifyReceiptUrl;
	}
}
