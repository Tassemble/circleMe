package com.game.core.bomb.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.Test;

import com.game.core.GameProtocolHandlerProxy;

/**
 * @author CHQ
 * @since 2013-7-28
 */
public class GameClient {

	public Properties readProperties() throws IOException {
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream("test.properties");
		prop.load(stream);
		return prop;
	}

	@Test
	public void start() throws IOException {
		Properties prop = readProperties();
		IoConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.setHandler(new GameClientHandler());
		ConnectFuture future = connector.connect(new InetSocketAddress(prop.getProperty("host"), 8888));
		future.awaitUninterruptibly();
		IoSession session = future.getSession();

		// signup
		String account = "test_account" + RandomUtils.nextInt();
		signup(session, prop.getProperty("signup"), account);

		// logon action
		logon(session, prop.getProperty("login"), account);
		
		

		session.getCloseFuture().awaitUninterruptibly();
		connector.dispose();

	}

	private void signup(IoSession session, String signup, String account) throws IOException {
		validate(session);
		JSONObject json = JSONObject.fromObject(signup);

		// get a random account
		json.discard("username").accumulate("username", account);
		session.write(json.toString());

	}

	private void validate(IoSession session) throws IOException {
		if (!session.isConnected()) {
			throw new IOException("session close exception！！");
		}
		// TODO Auto-generated method stub
		
	}

	public void logon(IoSession session, String msg, String account) throws IOException {
		validate(session);
		JSONObject json = JSONObject.fromObject(msg);

		// get a random account
		json.discard("username").accumulate("username", account);
		session.write(msg);
	}

}
