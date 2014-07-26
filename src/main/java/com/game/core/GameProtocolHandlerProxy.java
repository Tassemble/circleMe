package com.game.core;

import net.sf.json.JSONObject;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.game.core.bomb.BombMessageHandler;
import com.game.core.exception.BombException;

/**
 * @author CHQ
 * @since 1.0.0
 * @date 2013-7-28
 */
public class GameProtocolHandlerProxy implements IoHandler {

	@Autowired
	BombMessageHandler	bombMessageHandler;

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		bombMessageHandler.sessionCreated(new JsonSessionWrapper(session));
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		bombMessageHandler.sessionOpened(new JsonSessionWrapper(session));
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		bombMessageHandler.sessionClosed(new JsonSessionWrapper(session));
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		bombMessageHandler.sessionIdle(new JsonSessionWrapper(session), status);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		bombMessageHandler.exceptionCaught(new JsonSessionWrapper(session), cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		String action = null;
		try {
			JSONObject json = JSONObject.fromObject(message);
			action = json.getString("action");
		} catch (Exception e) {

		}
		try {
			bombMessageHandler.messageReceived(new JsonSessionWrapper(session), message);
		} catch (Exception e) {
			if (BombException.class.isAssignableFrom(e.getClass())) {
				BombException exception = (BombException) e;
				exception.setAction(action);
				throw exception;
			} else {
				throw e;
			}
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		bombMessageHandler.messageSent(new JsonSessionWrapper(session), message);
	}

}
