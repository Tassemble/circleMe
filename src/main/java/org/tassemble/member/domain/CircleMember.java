package org.tassemble.member.domain;

import com.netease.framework.dao.sql.annotation.DataProperty;
import java.math.BigDecimal;
import com.game.base.commons.domain.BaseDomain;

public class CircleMember extends BaseDomain{

private static final long serialVersionUID = 1L;

	private Integer circleColor;
	private Integer role;
	private Integer playerRole;
	private Long playerRoleExpired;
	
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



}
