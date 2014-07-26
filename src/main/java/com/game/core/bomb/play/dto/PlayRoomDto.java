package com.game.core.bomb.play.dto;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.core.GameMemory;
import com.game.core.bomb.dto.OnlineUserDto;

/**
 * @author CHQ
 * @since 2013-7-28
 */
public class PlayRoomDto {
	final static transient Logger		LOG					= LoggerFactory.getLogger(PlayRoomDto.class);

	public static final String			ROOM_STATUS_OPEN	= "open";
	public static final String			ROOM_STATUS_CLOSED	= "closed";
	private static transient AtomicLong	idGenerator			= new AtomicLong(1);

	private transient Object			roomEnterLock		= new Object();

	transient List<OnlineUserDto>		users;
	transient AtomicInteger				cntNow				= new AtomicInteger(0);
	transient ExecutorService			executorService;

	String								id;
	private Integer						roomNumLimit;

	volatile String						roomStatus;

	private PlayersOfRoomStart			playerInfoAfterGameStart;

	// 1 表示公开 0 表示私有
	private Integer						priviledge;

	private OnlineUserDto				roomOwner;

	
	//快速加入到这个房间的时候，需要匹配到合适的level才能快速，如
	//果不是处于同一level的玩家则不能进行匹配
	//具体规则请查看文档玩家PK匹配相关
	private Integer						pkLevel;

	public static final int				DEFAULT_ROOM_NUM	= 4;

	public PlayRoomDto(int roomNumLimit, OnlineUserDto user) {
		priviledge = 1;
		executorService = Executors.newFixedThreadPool(roomNumLimit);
		this.roomNumLimit = roomNumLimit;
		increaseReadyNum();

		roomStatus = PlayRoomDto.ROOM_STATUS_OPEN;
		this.id = PlayRoomDto.generateRoomId();

		user.setRoomId(this.id);
		user.setStatus(OnlineUserDto.STATUS_IN_ROOM);
		roomOwner = user;
		this.users = new CopyOnWriteArrayList<OnlineUserDto>();
		users.add(user);
	}
	
	public PlayRoomDto(int roomNumLimit, OnlineUserDto user, Integer pkLevel) {
		this(roomNumLimit, user);
		this.pkLevel = pkLevel;
	}

	public PlayRoomDto(OnlineUserDto user, int priviledge) {
		this.priviledge = priviledge;
		executorService = Executors.newFixedThreadPool(roomNumLimit);
		this.roomNumLimit = DEFAULT_ROOM_NUM;
		increaseReadyNum();

		roomStatus = PlayRoomDto.ROOM_STATUS_OPEN;
		this.id = PlayRoomDto.generateRoomId();

		user.setRoomId(this.id);
		user.setStatus(OnlineUserDto.STATUS_IN_ROOM);

		roomOwner = user;
		this.users = new CopyOnWriteArrayList<OnlineUserDto>();
		users.add(user);
	}

	public void setCntNow(AtomicInteger cntNow) {
		this.cntNow = cntNow;
	}

	public Object getRoomLock() {
		return roomEnterLock;
	}

	public void setRoomLock(Object roomLock) {
		this.roomEnterLock = roomLock;
	}

	public Future<?> addUserCallback(Runnable task) {
		return executorService.submit(task);
	}

	public String getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(String roomStatus) {
		this.roomStatus = roomStatus;

	}

	public void startGame() {
		setRoomStatus(PlayRoomDto.ROOM_STATUS_CLOSED);
		playerInfoAfterGameStart = new PlayersOfRoomStart(this.getUsers());
		//interrupt all user waiting status
		for (OnlineUserDto userDto : this.getUsers()) {
			//这个中断会把自己给中断（如果存在阻塞操作的话)˚
			//do not interrupt self
			if (userDto.getTimeoutTask() != null) {
				if (userDto.getTimeoutTask().getThreadId().equals(Thread.currentThread().getId())) {
					if (LOG.isDebugEnabled()) {
						LOG.info("current thread won't interrupt!");
					}
					continue;//超时前运行的就是回调线程
				}
			}
			userDto.interuptTimeoutTask();
		}
	}

	public List<OnlineUserDto> getUsers() {
		return users;
	}

	public int getReadyNumNow() {
		return cntNow.get();
	}

	public void setUsers(List<OnlineUserDto> users) {
		this.users = users;
	}
	
	
	public boolean hasMinPlayersToStartGame() {
		if (cntNow.get() >= 2) { //最低游戏开始人数
			return true;
		}
		return false;
	}

	public void increaseReadyNum() {
		if (cntNow.get() < roomNumLimit) {
			cntNow.addAndGet(1);
		} else {
			throw new RuntimeException("enter room failed");
		}
	}

	public void decreaseReadyNum() {
		synchronized (roomEnterLock) {
			if (cntNow.get() >= 1) {
				cntNow.addAndGet(-1);
			}
			if (cntNow.get() < 0) {
				cntNow.set(0);
			}
		}
	}
	
	public void removeUser(OnlineUserDto user) {
		//必须是在未开始游戏的时候可以这样
		if (!this.getRoomStatus().equals(PlayRoomDto.ROOM_STATUS_OPEN)) {
			return;
		}
		synchronized (roomEnterLock) {
			users.remove(user);
			decreaseReadyNum();
		}
	}
	public Integer getRoomNumLimit() {
		return roomNumLimit;
	}

	public void setRoomNumLimit(Integer playersNum) {
		this.roomNumLimit = playersNum;
	}

	public String getRoomId() {
		return id;
	}

	public static String generateRoomId() {
		return String.valueOf(idGenerator.getAndAdd(1L));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isFull() {
		return cntNow.get() == roomNumLimit;
	}

	public boolean isReadyToStart() {
		return cntNow.get() == roomNumLimit;
	}

	public boolean isEmpty() {
		return cntNow.get() == 0;
	}

	public PlayersOfRoomStart getPlayerInfoAfterGameStart() {
		return playerInfoAfterGameStart;
	}

	public void setPlayerInfoAfterGameStart(PlayersOfRoomStart playerInfoAfterGameStart) {
		this.playerInfoAfterGameStart = playerInfoAfterGameStart;
	}

	public Integer getPriviledge() {
		return priviledge;
	}

	public void setPriviledge(Integer priviledge) {
		this.priviledge = priviledge;
	}

	public OnlineUserDto getRoomOwner() {
		return roomOwner;
	}

	public void setRoomOwner(OnlineUserDto roomOwner) {
		this.roomOwner = roomOwner;
	}

	public Integer getPkLevel() {
		return pkLevel;
	}

	public void setPkLevel(Integer pkLevel) {
		this.pkLevel = pkLevel;
	}

}
