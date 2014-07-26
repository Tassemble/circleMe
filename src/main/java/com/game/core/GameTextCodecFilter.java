package com.game.core;

import java.nio.charset.Charset;

import org.apache.mina.filter.codec.textline.TextLineCodecFactory;


public class GameTextCodecFilter extends TextLineCodecFactory {

	
	public GameTextCodecFilter() {
		super(Charset.forName("UTF-8"));
		this.setDecoderMaxLineLength(1024 * 1000);//1MB
		this.setEncoderMaxLineLength(1024 * 1000); //1MB
	}
}
