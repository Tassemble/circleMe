package org.tassemble.member.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;
import java.math.BigDecimal;

public class CircleMember {

private static final long serialVersionUID = 1L;

	private String id;
	private Integer circleColor;
	private Integer role;
	private Integer playerRole;
	private Long playerRoleExpired;
	private Long gmtCreate;
	private Long gmtModified;
	
	public void setId(String id) {
		this.id = id;
	}
	
	@DataProperty(column = "id")	
	public String getId() {
		return id;
	}	

	
	public void setCircleColor(Integer circleColor) {
		this.circleColor = circleColor;
	}
	
	@DataProperty(column = "circle_color")	
	public Integer getCircleColor() {
		return circleColor;
	}	

	
	public void setRole(Integer role) {
		this.role = role;
	}
	
	@DataProperty(column = "role")	
	public Integer getRole() {
		return role;
	}	

	
	public void setPlayerRole(Integer playerRole) {
		this.playerRole = playerRole;
	}
	
	@DataProperty(column = "player_role")	
	public Integer getPlayerRole() {
		return playerRole;
	}	

	
	public void setPlayerRoleExpired(Long playerRoleExpired) {
		this.playerRoleExpired = playerRoleExpired;
	}
	
	@DataProperty(column = "player_role_expired")	
	public Long getPlayerRoleExpired() {
		return playerRoleExpired;
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
