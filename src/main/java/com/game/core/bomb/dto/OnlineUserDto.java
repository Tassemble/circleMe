package com.game.core.bomb.dto;

import org.apache.mina.core.session.IoSession;

import com.game.bomb.domain.User;

public class OnlineUserDto {
	
	
	//
	/**
	 * 其实 我对状态的理解是这样的
	 * 它其实是个冗余，也是一个标识，表示在什么情况下该干什么事情，不该干什么事情
	 * 也不一定非要它不可，可以通过其他的方式来判断，但相比于用状态来判断实现起来会比较方便点
	 */
	public static final String STATUS_ONLINE = "online"; 
	public static final String STATUS_OFFLINE = "offline"; 
	public static final String STATUS_PLAYING = "playing"; 
	public static final String STATUS_IN_ROOM = "in_room"; 
	
	Long  id;
	String roomId;
	String username;
	String status;
	String nickname;
	Integer level;
	Integer portrait;
	Integer heartNum;
	Integer victoryNum;
	Integer loserNum;
	Integer runawayNum;
	Long				inGot;
	Long				gold;
	private String inUse;
	transient IoSession session;
	
	
	private transient TimeoutTaskWrapper timeoutTask;
	
	public OnlineUserDto() {}
	public OnlineUserDto(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.nickname = user.getNickName();
		this.heartNum = user.getHeartNum();
		this.level = user.getLevel();
		this.loserNum = user.getLoserNum();
		this.nickname = user.getNickName();
		this.portrait = user.getPortrait();
		this.runawayNum = user.getRunawayNum();
		this.victoryNum = user.getVictoryNum();
		this.inUse = user.getInUse();
		this.gold = user.getGold();
		this.inGot = user.getInGot();
	}
	
	
	public void interuptTimeoutTask() {
		if (timeoutTask != null) {
			timeoutTask.halt();
		}
	}
	
	public void refreshUser(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.nickname = user.getNickName();
		this.heartNum = user.getHeartNum();
		this.level = user.getLevel();
		this.loserNum = user.getLoserNum();
		this.nickname = user.getNickName();
		this.portrait = user.getPortrait();
		this.runawayNum = user.getRunawayNum();
		this.victoryNum = user.getVictoryNum();
		this.inUse = user.getInUse();
		this.gold = user.getGold();
		this.inGot = user.getInGot();
	}
	public Long getInGot() {
		return inGot;
	}
	public void setInGot(Long inGot) {
		this.inGot = inGot;
	}
	public Long getGold() {
		return gold;
	}
	public void setGold(Long gold) {
		this.gold = gold;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getPortrait() {
		return portrait;
	}

	public void setPortrait(Integer portrait) {
		this.portrait = portrait;
	}

	public Integer getHeartNum() {
		return heartNum;
	}

	public void setHeartNum(Integer heartNum) {
		this.heartNum = heartNum;
	}

	public Integer getVictoryNum() {
		return victoryNum;
	}

	public void setVictoryNum(Integer victoryNum) {
		this.victoryNum = victoryNum;
	}

	public Integer getLoserNum() {
		return loserNum;
	}

	public void setLoserNum(Integer loserNum) {
		this.loserNum = loserNum;
	}

	public Integer getRunawayNum() {
		return runawayNum;
	}

	public void setRunawayNum(Integer runawayNum) {
		this.runawayNum = runawayNum;
	}


	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OnlineUserDto other = (OnlineUserDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public String getInUse() {
		return inUse;
	}
	public void setInUse(String inUse) {
		this.inUse = inUse;
	}
	public TimeoutTaskWrapper getTimeoutTask() {
		return timeoutTask;
	}
	public void setTimeoutTask(TimeoutTaskWrapper timeoutTask) {
		this.timeoutTask = timeoutTask;
	}


	
	
	
	
}
