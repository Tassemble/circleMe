package com.game.core.bomb.dispatcher;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.core.GameMemory;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.BaseActionDataDto;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.logic.RoomLogic;
import com.game.core.bomb.play.dto.PlayRoomDto;
import com.game.core.exception.ActionFailedException;
import com.game.core.exception.NoAuthenticationException;



@Component
public class QuitGameAction implements BaseAction{

	@Autowired
	RoomLogic roomLogic;
	
	@Override
	public void doAction(IoSession session, BaseActionDataDto baseData) throws Exception {
		validateUserStatus(baseData);
		
		OnlineUserDto user = GameMemory.getUser();
		PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		
		roomLogic.doUserQuit(room, user.getId());
	}
	
	
	private void validateUserStatus(BaseActionDataDto baseData) {
		OnlineUserDto user = GameMemory.getUser();
		
		if (user == null) {
			throw new NoAuthenticationException(baseData.getAction());
		}
		PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		
		if (room == null) {
			throw new ActionFailedException(baseData.getAction());
		}
		
		 if (!(OnlineUserDto.STATUS_PLAYING.equals(user.getStatus()) || OnlineUserDto.STATUS_IN_ROOM.equals(user.getStatus()))) {
			 throw new ActionFailedException(baseData.getAction());
		 }
	}

	@Override
	public String getAction() {
		return ActionNameEnum.QUIT_GAME.getAction();
	}

}
