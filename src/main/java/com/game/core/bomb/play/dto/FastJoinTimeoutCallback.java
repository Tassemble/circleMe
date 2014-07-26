package com.game.core.bomb.play.dto;

import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.game.core.GameMemory;
import com.game.core.bomb.dto.ActionNameEnum;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnDto;
import com.game.core.bomb.logic.RoomLogic;

public class FastJoinTimeoutCallback extends Thread {

	final static transient Logger		LOG					= LoggerFactory.getLogger(FastJoinTimeoutCallback.class);
	
	
	int			timeoutInSeconds	= 0;
	Long		userId;
	RoomLogic	roomLogic;
	volatile    long currentThreadId = -1L;
	
	
	
	

	public FastJoinTimeoutCallback(Long userId, int timeoutInSeconds) {
		super();
		this.userId = userId;
		this.timeoutInSeconds = timeoutInSeconds;
		ApplicationContext cxt = (ApplicationContext) GameMemory.bizContext.get(GameMemory.CONTEXT_NAME);
		roomLogic = cxt.getBean(RoomLogic.class);
	}

	
	public void halt() {
		this.interrupt();
	}

	@Override
	public void run() {
		currentThreadId = Thread.currentThread().getId();
		try {
			TimeUnit.SECONDS.sleep(timeoutInSeconds);
			OnlineUserDto user = GameMemory.getUserById(this.userId);
			if (user == null) {
				return;
			}
			PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
			if (room == null) {
				return;
			}
			if (PlayRoomDto.ROOM_STATUS_OPEN.equals(room.getRoomStatus())) {
				
				//判断是否满足最低游戏开始人数 如果满足2人以上 就直接开始 否则超时
				if (room.hasMinPlayersToStartGame()) {
					Thread.currentThread().setName("user(" +user.getId()+")-room(" + room.getId() + ")");
					//force start if force start failed then goto timeout
					boolean success = roomLogic.forceStartGame(room, ActionNameEnum.FAST_JOIN.getAction());
					if (!success) { //如果强制执行开始失败，就显示超时信息，可能的原因是即将开始的时候有人退出了
						roomLogic.exitRoomWhenWaiting(user);
					} else {
						return;//success
					}
				} else {
					roomLogic.exitRoomWhenWaiting(user); //如果不满足最低开始人数要求 就显示超时信息
				}
				IoSession session = GameMemory.getSessionById(this.userId);
				session.write(new ReturnDto(-20, ActionNameEnum.FAST_JOIN.getAction(), "fast-join timeout"));
			} else {
			}
			// LOG.info("game is started");
		} catch(InterruptedException ie) {
			LOG.info(ie.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}


	public long getCurrentThreadId() {
		return currentThreadId;
	}


	public void setCurrentThreadId(long currentThreadId) {
		this.currentThreadId = currentThreadId;
	}
	
	
	
}
