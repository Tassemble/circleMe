package com.game.bomb.mobile.dto;

/**
 * @author CHQ
 * @date 2014年1月26日
 * @since 1.0
 */
public class DayAward {
	Integer	gold;
	Integer	heart;

	public Integer getGold() {
		return gold;
	}

	public void setGold(Integer gold) {
		this.gold = gold;
	}

	public Integer getHeart() {
		return heart;
	}

	public void setHeart(Integer heart) {
		this.heart = heart;
	}

	public DayAward(Integer heart, Integer gold) {
		super();
		this.gold = gold;
		this.heart = heart;
	}

	public DayAward() {
	}

}
