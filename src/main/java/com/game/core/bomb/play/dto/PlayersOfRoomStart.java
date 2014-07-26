package com.game.core.bomb.play.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.game.core.bomb.dto.OnlineUserDto;


/**
 * 开始一场游戏后，游戏中玩家的活动信息，它是游戏开始后的组成部分
 * @author CHQ
 * @date Oct 26, 2013
 * @since 1.0
 */
public class PlayersOfRoomStart {

	//已经死亡的人
	Set<OnlineUserDto> diedPlayers = new HashSet<OnlineUserDto>();
	
	//逃跑的人
	Set<OnlineUserDto> runawayPlayers = new HashSet<OnlineUserDto>();
	
	//正在玩的人
	Set<OnlineUserDto> playingPlayers = new HashSet<OnlineUserDto>();;
	
	
	//还在游戏室里的人
	Set<OnlineUserDto> playersInRoom = new HashSet<OnlineUserDto>();
	
	
	//在这次游戏里的所有人的信息
	Set<OnlineUserDto> allPlayersInThisPlaying = new HashSet<OnlineUserDto>();

	//以后可以在这里添加groupPK的信息
	//List<PlayerGroup> groups;
	
	
	public PlayersOfRoomStart(List<OnlineUserDto> allPlayers) {
		if (CollectionUtils.isEmpty(allPlayers)) {
			throw new RuntimeException("game started but all players is empty after game start");
		}
		
		for (OnlineUserDto player : allPlayers) {
			player.setStatus(OnlineUserDto.STATUS_PLAYING);
			allPlayersInThisPlaying.add(player);
			playersInRoom.add(player);
		}
		
		
	}


	//逃跑行为缠身
	public void runaway(OnlineUserDto onlineUser) {
		playingPlayers.remove(onlineUser);
		runawayPlayers.add(onlineUser);
	}


	public Set<OnlineUserDto> getDiedPlayers() {
		return diedPlayers;
	}


	public void setDiedPlayers(Set<OnlineUserDto> diedPlayers) {
		this.diedPlayers = diedPlayers;
	}


	public Set<OnlineUserDto> getRunawayPlayers() {
		return runawayPlayers;
	}


	public void setRunawayPlayers(Set<OnlineUserDto> runawayPlayers) {
		this.runawayPlayers = runawayPlayers;
	}


	public Set<OnlineUserDto> getPlayingPlayers() {
		return playingPlayers;
	}


	public void setPlayingPlayers(Set<OnlineUserDto> playingPlayers) {
		this.playingPlayers = playingPlayers;
	}


	public Set<OnlineUserDto> getPlayersInRoom() {
		return playersInRoom;
	}


	public void setPlayersInRoom(Set<OnlineUserDto> playersInRoom) {
		this.playersInRoom = playersInRoom;
	}


	public Set<OnlineUserDto> getAllPlayersInThisPlaying() {
		return allPlayersInThisPlaying;
	}


	public void setAllPlayersInThisPlaying(Set<OnlineUserDto> allPlayersInThisPlaying) {
		this.allPlayersInThisPlaying = allPlayersInThisPlaying;
	}
	
	
	
	
	
	
	
}
