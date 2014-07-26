package com.game.core;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.core.bomb.dto.GameSessionContext;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnDto;
import com.game.core.bomb.play.dto.PlayRoomDto;
import com.game.utils.GsonUtils;

public class MessageSenderHelper {

	
	
	
	
	
	public static void forwardMessage(IoSession session, Object message) {
		if (message == null || StringUtils.isBlank(message.toString())) {
			return;
		}
		
		if (session == null){
			return;
		}
		session.write(message);
	}
	
	
	
	
}
