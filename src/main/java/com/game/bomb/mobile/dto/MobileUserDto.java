package com.game.bomb.mobile.dto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.game.bomb.domain.User;
import com.game.core.GameMemory;
import com.game.core.action.bomb.logic.BloodLogic;
import com.game.core.bomb.dto.OnlineUserDto;
import com.google.gson.annotations.SerializedName;

/**
 * 需要具备如下属性： 玩家昵称（String) 玩家ID（String) 用户状态（离线，在线，游戏中）（String） 用户头像（unsigned
 * int） 玩家等级（unsigned int) 胜率(float)( [0.0 , 1.0] ) 逃跑率(float)( [0.0 , 1.0] )
 * 这些封装到对象Map中 使用中的勋章1（unsigned int) 使用中的勋章2（unsigned int) 使用中的勋章3（unsigned int)
 * 使用中的商品1（Dictionary)(unsigned int,unsigned int) 使用中的商品2（unsigned int)(unsigned
 * int,unsigned int) 使用中的商品3（unsigned int)(unsigned int,unsigned int)
 * 
 * @author CHQ
 * @date Oct 24, 2013
 * @since 1.0
 */
public class MobileUserDto {

	Long				id;
	String				username;
	String				nickName;
	Integer				portrait;
	Integer				level;
	Integer				win;
	Integer				runaway;
	Integer				lose;

	Long				inGot;
	Long				gold;
	Integer 			heart;
	

	// should set it alone
	String				status;
	
	@SerializedName("in_use")
	Map<Object, Object>	inUse;
	
	private Long leftTime; //ms 
	

	private MobileUserDto(User user) {
		this.id = user.getId();
		this.heart = user.getHeartNum();
		this.username = user.getUsername();
		this.nickName = user.getNickName();
		this.portrait = user.getPortrait();
		this.level = user.getLevel();
		this.win = user.getVictoryNum() == null ? 0 : user.getVictoryNum();
		this.runaway = user.getRunawayNum() == null ? 0 : user.getRunawayNum();
		this.lose = user.getLoserNum() == null ? 0 : user.getLoserNum();
		this.gold = user.getGold();
		this.inGot = user.getInGot();
	}

	@SuppressWarnings("unchecked")
	public MobileUserDto(OnlineUserDto user) throws Exception {
		this.id = user.getId();
		this.heart = user.getHeartNum();
		this.username = user.getUsername();
		this.nickName = user.getNickname();
		this.portrait = user.getPortrait();
		this.level = user.getLevel();
		this.win = user.getVictoryNum() == null ? 0 : user.getVictoryNum();
		this.runaway = user.getRunawayNum() == null ? 0 : user.getRunawayNum();
		this.lose = user.getLoserNum() == null ? 0 : user.getLoserNum();
		this.gold = user.getGold();
		this.inGot = user.getInGot();
		this.status = user.getStatus();
		if (!StringUtils.isBlank(user.getInUse())) {
			this.inUse = new ObjectMapper().readValue(user.getInUse(), HashMap.class);
		}
	}

	@SuppressWarnings("unchecked")
	public static MobileUserDto buildMobileUser(User user) throws JsonParseException, JsonMappingException,
			IOException {
		OnlineUserDto onlineUser = GameMemory.getUserById(user.getId());

		if (onlineUser == null) {
			return null;
		}
		MobileUserDto mobData = new MobileUserDto(user);
		mobData.setStatus(onlineUser.getStatus());
		if (user.getFullHeart() == null || user.getHeartNum() == null || user.getHeartNum() >= user.getFullHeart()) {
			mobData.setLeftTime(0L);
		} else {
			mobData.setLeftTime(BloodLogic.DefaultBloodRecoveryOfDuration - 
					(System.currentTimeMillis() - user.getBloodTime().getTime()));
			
			
			
		}
		if (!StringUtils.isBlank(user.getInUse())) {
			mobData.setInUse(new ObjectMapper().readValue(user.getInUse(), HashMap.class));
		}

		return mobData;
	}
	
	public static void main(String[] args) {
		//-103054
		System.out.println(BloodLogic.DefaultBloodRecoveryOfDuration);
		System.out.println(System.currentTimeMillis() - 1390835247647L);
		System.out.println( (System.currentTimeMillis() -1390835247647L)/ BloodLogic.DefaultBloodRecoveryOfDuration);
		System.out.println(BloodLogic.DefaultBloodRecoveryOfDuration - (System.currentTimeMillis() - 1390835247647L));
	}

	public Integer getWin() {
		return win;
	}

	public void setWin(Integer win) {
		this.win = win;
	}

	public Integer getRunaway() {
		return runaway;
	}

	public void setRunaway(Integer runaway) {
		this.runaway = runaway;
	}

	public Integer getLose() {
		return lose;
	}

	public void setLose(Integer lose) {
		this.lose = lose;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPortrait() {
		return portrait;
	}

	public void setPortrait(Integer portrait) {
		this.portrait = portrait;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Map<Object, Object> getInUse() {
		return inUse;
	}

	public void setInUse(Map<Object, Object> inUse) {
		this.inUse = inUse;
	}

	public Long getLeftTime() {
		return leftTime;
	}

	public void setLeftTime(Long leftTime) {
		this.leftTime = leftTime;
	}

}
