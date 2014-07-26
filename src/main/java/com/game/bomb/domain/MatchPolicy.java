package com.game.bomb.domain;

import com.game.base.commons.domain.BaseDo;
import com.netease.framework.dao.sql.annotation.DataProperty;

public class MatchPolicy extends BaseDo{

	
	
	public static final Integer DEFAULT_POLICY_LEVEL = 1;
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1666704892125011185L;
	
	private Integer pkLevel;
	private Integer winLow;
	private Integer winHigh;
	private Integer loseLow;
	private Integer loseHigh;
	
	@DataProperty(column="pk_level")
	public Integer getPkLevel() {
		return pkLevel;
	}
	public void setPkLevel(Integer pkLevel) {
		this.pkLevel = pkLevel;
	}
	
	@DataProperty(column="win_low")
	public Integer getWinLow() {
		return winLow;
	}
	public void setWinLow(Integer winLow) {
		this.winLow = winLow;
	}
	
	@DataProperty(column="win_high")
	public Integer getWinHigh() {
		return winHigh;
	}
	public void setWinHigh(Integer winHigh) {
		this.winHigh = winHigh;
	}
	
	@DataProperty(column="lose_low")
	public Integer getLoseLow() {
		return loseLow;
	}
	
	public void setLoseLow(Integer loseLow) {
		this.loseLow = loseLow;
	}
	
	@DataProperty(column="lose_high")
	public Integer getLoseHigh() {
		return loseHigh;
	}
	public void setLoseHigh(Integer loseHigh) {
		this.loseHigh = loseHigh;
	}
	
	
	
	
	

}
