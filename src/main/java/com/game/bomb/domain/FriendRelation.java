package com.game.bomb.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;

/**
 * @author CHQ
 * @since  1.0.0
 * @date   2013-8-6
 */
public class FriendRelation {
	public static final String STATUS_WAITING_ACCEPT = "waiting_accept";
	public static final String STATUS_ACCEPTED = "accepted";
	
	
	Long id;
	Long userId;
	Long friendId;
	String relationStatus;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@DataProperty(column="user_id")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@DataProperty(column="friend_id")
	public Long getFriendId() {
		return friendId;
	}
	public void setFriendId(Long friendId) {
		this.friendId = friendId;
	}
	
	@DataProperty(column="relation_status")
	public String getRelationStatus() {
		return relationStatus;
	}
	public void setRelationStatus(String relationStatus) {
		this.relationStatus = relationStatus;
	}
	
	
}
