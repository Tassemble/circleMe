package com.game.core.bomb.logging;

import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;

/**
 * @author CHQ
 * @date 2014年1月31日
 * @since 1.0
 */
public class BombLoggingFilter extends LoggingFilter {
	
	
	
	public LogLevel getMessageReceivedLevel() {
		return getMessageReceivedLogLevel();
	}

	public void setMessageReceivedLevel(LogLevel messageReceivedLevel) {
		super.setMessageReceivedLogLevel(messageReceivedLevel);
	}
	
	
	
}
