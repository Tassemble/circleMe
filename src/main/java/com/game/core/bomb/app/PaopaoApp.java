package com.game.core.bomb.app;

import java.util.concurrent.TimeUnit;

import org.apache.mina.core.service.IoAcceptor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.game.core.GameMemory;

/**
 * @author CHQ
 * @date 2014年1月30日
 * @since 1.0
 */
public class PaopaoApp {

	
	
	static ClassPathXmlApplicationContext  APPLICATION_CONTEXT = new ClassPathXmlApplicationContext();
	
	public static void main(String[] args) {
		
		APPLICATION_CONTEXT.setConfigLocations(new String[] { 
				"classpath:/applicationContext-bo.xml",
				"classpath:/biz/applicationContext-framework-aop.xml",
				"classpath:/biz/applicationContext-framework-dao-base.xml" });
		APPLICATION_CONTEXT.refresh();
		
		GameMemory.bizContext.put(GameMemory.CONTEXT_NAME, APPLICATION_CONTEXT);
		
		IoAcceptor acceptor = (IoAcceptor) APPLICATION_CONTEXT.getBean("ioAcceptor");
		acceptor.getSessionConfig().setBothIdleTime(30 * 60); //半小时后不操作就关闭了
//		acceptor.getSessionConfig().setReaderIdleTime(60);
//		System.out.println(acceptor.getSessionConfig().getWriterIdleTimeInMillis());
		try {
			while (acceptor.isActive()) {
				TimeUnit.SECONDS.sleep(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			acceptor.unbind();
		}
	}
	
	
	
}
