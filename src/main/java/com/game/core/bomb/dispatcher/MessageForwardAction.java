package com.game.core.bomb.dispatcher;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;

import com.game.core.GameMemory;
import com.game.core.MessageSenderHelper;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.BaseActionDataDto;
import com.game.core.bomb.dto.GameSessionContext;
import com.game.core.bomb.dto.BaseActionDataDto.ForwardData;
import com.game.core.bomb.logic.RoomLogic;
import com.google.common.collect.Maps;


public class MessageForwardAction {

	
	public void doAction(IoSession session, BaseActionDataDto baseData) {
//		ForwardData data = (ForwardData) baseData;
//		if (!CollectionUtils.isEmpty(data.getFriendList())) {
//			Map<String, Object> map = Maps.newHashMap();
//			map.put("code", -1);
//			map.put("action", this.getAction());
//			map.put("msg", "forward to other clients error, friendList should be user ids, can't be username");
//			GameSessionContext context = GameMemory.LOCAL_SESSION_CONTEXT.get();
//			//self 
//			context.getSession().write(map);
//		} else {
//			RoomLogic.forwardMessageToOtherClientsInRoom(baseData);
//		}
	}

	

}
