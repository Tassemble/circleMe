package org.tassemble.member.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;
import java.math.BigDecimal;
import com.game.base.commons.domain.BaseDomain;

public class Member extends BaseDomain{

private static final long serialVersionUID = 1L;

	private String username;
	private String loginType;
	private String phone;
	private String nickname;
	private String password;
	private Integer sex;
	
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



}
