package com.game.bomb.Dao;

import com.netease.framework.dao.sql.annotation.DataProperty;

public class UserMeta {
	
	public static final String USER_INVENTORY_ITEM = "UserInventoryItem";
	public static final String HEART_NUM = "HEART_NUM";
	public static final String USER_MEDAL = "USER_MEDAL";
	
	Long id;
	Long userId;
	
	String key;
	String value;
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
	
	@DataProperty(column="user_key")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
