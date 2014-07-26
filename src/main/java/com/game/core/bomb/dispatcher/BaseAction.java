package com.game.core.bomb.dispatcher;

import org.apache.mina.core.session.IoSession;

import com.game.core.bomb.dto.BaseActionDataDto;

public interface BaseAction {
	
	
	public void doAction(IoSession session, BaseActionDataDto baseData) throws Exception;
	
	public String getAction();
	
}
