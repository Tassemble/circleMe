package com.game.bomb.mobile.dto;

import com.game.core.bomb.play.dto.PlayRoomDto;

public class MobRoomDto {

	private String	id;
	private Integer	playersNum;

	private String	roomStatus;

	public MobRoomDto(PlayRoomDto room) {
		this.id = room.getId();
		this.playersNum = room.getUsers().size();
		this.roomStatus = room.getRoomStatus();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getPlayersNum() {
		return playersNum;
	}

	public void setPlayersNum(Integer playersNum) {
		this.playersNum = playersNum;
	}

	public String getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(String roomStatus) {
		this.roomStatus = roomStatus;
	}

}
