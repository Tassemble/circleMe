package org.tassemble.member.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;
import java.math.BigDecimal;

public class Member {

private static final long serialVersionUID = 1L;

	private String id;
	private String username;
	private String loginType;
	private String phone;
	private String nickname;
	private String password;
	private Integer sex;
	private Long gmtCreate;
	private Long gmtModified;
	
	public void setId(String id) {
		this.id = id;
	}
	
	@DataProperty(column = "id")	
	public String getId() {
		return id;
	}	

	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@DataProperty(column = "username")	
	public String getUsername() {
		return username;
	}	

	
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	
	@DataProperty(column = "login_type")	
	public String getLoginType() {
		return loginType;
	}	

	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@DataProperty(column = "phone")	
	public String getPhone() {
		return phone;
	}	

	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	@DataProperty(column = "nickname")	
	public String getNickname() {
		return nickname;
	}	

	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@DataProperty(column = "password")	
	public String getPassword() {
		return password;
	}	

	
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	
	@DataProperty(column = "sex")	
	public Integer getSex() {
		return sex;
	}	

	
	public void setGmtCreate(Long gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	
	@DataProperty(column = "gmt_create")	
	public Long getGmtCreate() {
		return gmtCreate;
	}	

	
	public void setGmtModified(Long gmtModified) {
		this.gmtModified = gmtModified;
	}
	
	@DataProperty(column = "gmt_modified")	
	public Long getGmtModified() {
		return gmtModified;
	}	



}
