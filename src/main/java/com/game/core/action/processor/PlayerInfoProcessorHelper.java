package com.game.core.action.processor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.base.commons.utils.collection.OrderLimit;
import com.game.base.commons.utils.collection.PropertyExtractUtils;
import com.game.bomb.domain.FriendRelation;
import com.game.bomb.domain.User;
import com.game.bomb.mobile.dto.MobileUserDto;
import com.game.bomb.service.FriendRelationService;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.action.bomb.logic.BloodLogic;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.exception.NoAuthenticationException;
import com.google.common.collect.Lists;

@Component
public class PlayerInfoProcessorHelper {
	
	@Autowired
	FriendRelationService friendRelationService;
	@Autowired
	UserService userService;
	
	@Autowired
	BloodLogic bloodLogic;

	public void innerDownloadPlayerInfo(Map<String, Object> map) throws JsonParseException,
			JsonMappingException, IOException {
		OnlineUserDto onlineUser = GameMemory.getUser();
		if (onlineUser == null) {
			throw new NoAuthenticationException("downloadPlayerInfo");
		}
		User user = userService.getById(onlineUser.getId());

		if (user == null) {
			throw new NoAuthenticationException("downloadPlayerInfo");
		}

		bloodLogic.processBloodWithRealTime(user);
		
		MobileUserDto mobData = MobileUserDto.buildMobileUser(user);

		map.put("code", 200);
		map.put("self", mobData);
		// ~ put init
		map.put("friends", null);
		// ~ get friends
		OrderLimit ol = new OrderLimit();
		ol.setLimit(200);
		ol.setOffset(0);
		List<FriendRelation> relations =friendRelationService.getOrderLimitByCondition(ol, "user_id = ? and relation_status = ?", onlineUser.getId(), FriendRelation.STATUS_ACCEPTED);
		if (!CollectionUtils.isEmpty(relations)) {
			List<Long> friendIds = PropertyExtractUtils.getByPropertyValue(relations, "FriendId");
			List<User> friends = userService.getByIdList(friendIds);

			List<MobileUserDto> mobFriends = Lists.newArrayList();
			for (User friend : friends) {
				mobFriends.add(MobileUserDto.buildMobileUser(friend));
			}
			map.put("friends", mobFriends);
		}
	}
}
