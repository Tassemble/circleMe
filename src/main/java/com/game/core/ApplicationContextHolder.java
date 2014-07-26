package com.game.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 
 * @author CHQ
 * @date Nov 25, 2013
 * @since 1.0
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

	
	public static ApplicationContext ctx = null;
	
	public static ApplicationContext get() {
		if (ctx == null) {
			throw new RuntimeException("返回应用程序上下文为空");
		}
		return ctx;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext paramApplicationContext) throws BeansException {
		ctx = paramApplicationContext;
	}

}
