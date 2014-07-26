package com.game.core.bomb.dto;

import org.apache.mina.core.session.IoSession;

public class GameSessionContext {
	
	IoSession session;
	
	String action;

	
	//may be null
	OnlineUserDto onlineUser;

	
	public boolean hasLogon() {
		
		if (onlineUser == null) {
			return false;
		}
		return true;
	}
	
	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public OnlineUserDto getOnlineUser() {
		return onlineUser;
	}

	public void setOnlineUser(OnlineUserDto onlineUser) {
		this.onlineUser = onlineUser;
	}
	
	
	
}
