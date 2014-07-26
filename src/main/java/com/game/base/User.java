package com.game.base;

import java.sql.Timestamp;

import com.netease.framework.dao.sql.annotation.DataProperty;

public class User {
	private Long id;
	private String userLogin;
	private String userPass;
	private String userNicename;
	private String userEmail;
	private Timestamp userRegister;
	private Integer userStatus;
	private String displayName;
	
	@DataProperty(column="ID")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@DataProperty(column="user_login")
	public String getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	
	@DataProperty(column="user_pass")
	public String getUserPass() {
		return userPass;
	}
	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}
	
	@DataProperty(column="user_nicename")
	public String getUserNicename() {
		return userNicename;
	}
	public void setUserNicename(String userNicename) {
		this.userNicename = userNicename;
	}
	
	@DataProperty(column="user_email")
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	@DataProperty(column="user_registered")
	public Timestamp getUserRegister() {
		return userRegister;
	}
	public void setUserRegister(Timestamp userRegister) {
		this.userRegister = userRegister;
	}
	
	@DataProperty(column="user_status")
	public Integer getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}
	
	@DataProperty(column="display_name")
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	
	
	
}
