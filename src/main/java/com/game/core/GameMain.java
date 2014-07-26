package com.game.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GameMain {

	private static final Logger LOG = LoggerFactory.getLogger(GameMain.class);
	
	public static ApplicationContext ctx;
	
	static {
		ctx = new ClassPathXmlApplicationContext(
                "applicationContext-bo.xml");
	}
	
	public static void main(String[] args) throws IOException {
        startWithSpring();
	}

	/**
	 * 
	 */
	public static void startWithSpring() {
		IoAcceptor acceptor = (IoAcceptor) ctx.getBean("ioAcceptor");
		try {
			while (acceptor.isActive()) {
				TimeUnit.SECONDS.sleep(1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			acceptor.unbind();
		}
	}

	/**
	 * @throws IOException
	 */
	public static void start() throws IOException {
		IoAcceptor acceptor = new NioSocketAcceptor();

		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

		acceptor.setHandler(new GameProtocolHandlerProxy());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(8888));

		try {
			while (acceptor.isActive()) {
				TimeUnit.SECONDS.sleep(1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			acceptor.unbind();
		}
	}

}
