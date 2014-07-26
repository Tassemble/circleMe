package com.game.core.action.bomb;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.mina.core.session.IoSession;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.base.commons.utils.collection.FieldComparator;
import com.game.base.commons.utils.text.JsonUtils;
import com.game.bomb.Dao.UserDao;
import com.game.bomb.Dao.UserMeta;
import com.game.bomb.Dao.UserMetaDao;
import com.game.bomb.config.BombConfig;
import com.game.bomb.constant.BombConstant;
import com.game.bomb.domain.User;
import com.game.bomb.mobile.dto.DayAward;
import com.game.bomb.mobile.dto.MobileUserDto;
import com.game.bomb.service.FriendRelationService;
import com.game.bomb.service.TransactionService;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.action.processor.ActionAnotationProcessor;
import com.game.core.action.processor.PlayerInfoProcessorHelper;
import com.game.core.annotation.ActionAnnotation;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.core.bomb.dto.ReturnConstant;
import com.game.core.bomb.dto.ReturnDto;
import com.game.core.bomb.logic.RoomLogic;
import com.game.core.bomb.play.dto.PlayRoomDto;
import com.game.core.exception.ActionFailedException;
import com.game.core.exception.BombException;
import com.game.core.exception.MessageNullException;
import com.game.core.exception.NoAuthenticationException;
import com.game.utils.GsonUtils;
import com.game.utils.HttpClientUtils;
import com.google.common.collect.Lists;

@Component
public class CommonProcessor implements ActionAnotationProcessor {

	final static String			action		= "action";
	final static String			CODE_NAME	= "code";
	@Autowired
	UserService					userService;

	@Autowired
	FriendRelationService		friendRelationService;

	@Autowired
	UserMetaDao					userMetaDao;

	@Autowired
	PlayerInfoProcessorHelper	playerInfoProcessorHelper;
	
	@Autowired
	RoomLogic roomLogic;

	private static final Logger	LOG			= LoggerFactory.getLogger(CommonProcessor.class);
	private static final Logger	LOG_TRADE			= LoggerFactory.getLogger("transaction");
	
	@Autowired
	BombConfig bombConfig;
	
	
	@Autowired
	HttpClientUtils httpClientUtils;
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	UserDao userDao;
	
	
	//{"action":"AIStart"} 
	@ActionAnnotation(action = "AIStart") 
	public void AIStart(Object message, Map<String, Object> map) {
		OnlineUserDto user = GameMemory.getUser();
		
		User userInDB = userService.getById(user.getId());
		if (userInDB.getHeartNum() == null || userInDB.getHeartNum() <= 0) {
			map.put("code", BombConstant.NOT_ENOUGH_HEART_CODE);
			map.put("action", "AIStart");
			map.put("message", "no heart, you can wait or buy it");
			GameMemory.getCurrentSession().write(map);
			return;
		}
		
		User update = new User();
		update.setId(user.getId());
		update.setHeartNum(userInDB.getHeartNum() - 1);
		update.setGmtModified(new Date());
		userService.updateSelectiveById(update);
		
		map.put("code", 200);
		map.put("action", "AIStart");
		GameMemory.getCurrentSession().write(map);
	}
	
	

	
	// 登陆奖赏
	//{"action":"everydayAward"}
	//response:{"action":"everydayAward","code":200,"gold":5,"heart":5,"days":7}
	//{"action":"everydayAward","code":201}
	@ActionAnnotation(action = "everydayAward") 
	public void everydayAward(Object message, Map<String, Object> map) {
		OnlineUserDto user = GameMemory.getUser();
		
		//should locker
		userService.updateForAwardNextDayAndResponse(user.getId(), map);
		GameMemory.reloadUser();
		GameMemory.getCurrentSession().write(map);
	}
	
	
	
	//{"action":"feedback":msg":"base64(msg)"}
	@ActionAnnotation(action = "feedback") 
	public void feedback(Object message, Map<String, Object> map) {
		String msg = JSONObject.fromObject(message).getString("msg");
		OnlineUserDto user = GameMemory.getUser();
		LOG.info("feedback from :id" + user.getId() +"username:" + user.getUsername() 
				+ ", msg:" + new String(Base64.decodeBase64(msg))) ;
		map.put("action", "feedback");
		map.put("code", 200);
		GameMemory.getCurrentSession().write(map);
	}
	
	
	
	
	//{“action”:"AIAward","gold":'"0~5”}
	@ActionAnnotation(action = "AIAward") 
	public void AIAward(Object message, Map<String, Object> map) {
//		Integer goldNum = JSONObject.fromObject(message).getInt("gold");
		userService.updateGoldForPlus(BombConstant.AIAWARD_AFTER_WIN, GameMemory.getUser().getId());
		map.put("action", "AIAward");
		map.put("code", 200);
		GameMemory.getCurrentSession().write(map);
	}
	
	
	//{"action":"buyEquip","gold":"100”}
	@ActionAnnotation(action = "buyEquip") 
	public void buyEquip(Object message, Map<String, Object> map) {
		Integer goldNum = JSONObject.fromObject(message).getInt("gold");
		userService.updateGoldForMinus(goldNum, GameMemory.getUser().getId());
		map.put("action", "buyEquip");
		map.put("code", 200);
		GameMemory.getCurrentSession().write(map);
	}
	
	
	@ActionAnnotation(action = "forward") 
	public void forward(Object message, Map<String, Object> map) {
		RoomLogic.forwardMessageToOtherClientsInRoom(message);
	}
	
	//{"action":"exchangeInGotToGold", "inGot":20}
	@ActionAnnotation(action = "exchangeInGotToGold")
	public void exchangeInGotToGold(Object message, Map<String, Object> map) {
		// 20个金币换一颗红心
		String action = "exchangeInGotToGold";
		JSONObject jsonRoot = JSONObject.fromObject(message);
		int number = jsonRoot.getInt("inGot");
		Integer gainGold = BombConstant.EXCHANGE_INGOT_TO_GOLD_MAPPING.get(number);
		if (gainGold == null) {
			map.put("action", action);
			map.put("code", -4);
			map.put("message",
					"make sure enter inGot is right number, right number is "
							+ JsonUtils.toJson(BombConstant.EXCHANGE_INGOT_TO_GOLD_MAPPING.keySet()));
			GameMemory.getCurrentSession().write(map);
			return;
		}

		OnlineUserDto onlineUser = GameMemory.getUser();
		userService.updateGoldNumber(number, gainGold, onlineUser.getId());
		map.put("action", action);
		map.put("code", 200);
		GameMemory.getCurrentSession().write(map);
		return;

	}
		
	
	//{"action":"exchangeCoinToHeart", "inGot":20}
	@ActionAnnotation(action = "exchangeInGotToHeart")
	public void exchangeCoinToHeart(Object message, Map<String, Object> map) {
		//20个金币换一颗红心
		String action = "exchangeInGotToHeart";
		JSONObject jsonRoot = JSONObject.fromObject(message);
		int number = jsonRoot.getInt("inGot");
		Integer gainHeart = BombConstant.EXCHANGE_INGOT_TO_HEART_MAPPING.get(number);
		if (gainHeart == null) {
			map.put("action", action);
			map.put("code", -3);
			map.put("message", "make sure enter inGot is right number, right number is " + JsonUtils.toJson(BombConstant.EXCHANGE_INGOT_TO_HEART_MAPPING.keySet()));
			GameMemory.getCurrentSession().write(map);
			return;
		}
		
		OnlineUserDto onlineUser = GameMemory.getUser();
		userService.updateHeartNumber(number, gainHeart, onlineUser.getId());
		map.put("action", action);
		map.put("code", 200);
		GameMemory.getCurrentSession().write(map);
		return;
		
	}


	
	
	@ActionAnnotation(action = "createRoom")
	public void createRoom(Object message, Map<String, Object> map) {
		//创建房间
		JSONObject jsonRoot = JSONObject.fromObject(message);
		//判断他是否有权限、是否在创建房间的条件内
		//条件为：1. 用户是空闲状态，2. 普通用户即可
		checkUserLegalToCreateOrJoinRoom();
		
		int priviledge = 1;
		try {
			priviledge = jsonRoot.getInt("priviledge");
		} catch (Exception e) {
		}
		
		if (!(priviledge == 1 || priviledge == 0)) {
			throw new BombException(-1, "priviledge is ilegall");
		}
		
		PlayRoomDto room = null;
		if (priviledge == 0) {//如果是私有的房间
			String authCode = String.valueOf(System.currentTimeMillis()) + "-" + String.valueOf(RandomUtils.nextLong(((new Random(System.currentTimeMillis())))));
			room = new PlayRoomDto(GameMemory.getUser(), priviledge);
			map.put("authCode", authCode);
			//创建一个房间
			//设定他自己为房主
		} else {
			room = new PlayRoomDto(GameMemory.getUser(), priviledge);
			map.put("authCode",""); 
		}
		map.put("roomId", room.getId());
		map.put("code", 200);
	}
	
	
	
	@ActionAnnotation(action = "joinRoom")
	public void joinRoom(Object message, Map<String, Object> map) {
		
		//需要验证是否加入房间 条件是用户是idle的状态
		checkUserLegalToCreateOrJoinRoom();
//		roomLogic.doUserJoin(room, username);
		//
	}
	
	
	
	
	
	private void checkUserLegalToCreateOrJoinRoom() {
		if (GameMemory.hasLogin()) {
			if (GameMemory.getUser().getStatus().equals(OnlineUserDto.STATUS_ONLINE)) {
				return;
			}
			
		}
		throw new BombException(-1, "user is not idle, can't create room!");
	}


	//验证receipt
	@ActionAnnotation(action = "receiptData")
	public void verifyReceiptData(Object message, Map<String, Object> map) {
		JSONObject jsonRoot = JSONObject.fromObject(message);
		final String data = jsonRoot.getString("data");
		if (StringUtils.isBlank(data)) {
			throw new ActionFailedException("data is empty!!", jsonRoot.getString(action));
		}
		
		if (LOG.isDebugEnabled()) { 
			LOG.info("verify data:" + data);
		}
		
		transactionService.createAfterVerified(data, map);	
		
		//reload user for online user
		GameMemory.reloadUser();
		GameMemory.getCurrentSession().write(map);
		
		
		return;
	}
	
	//lose
	//runaway
	@ActionAnnotation(action = "runaway")
	public void runaway(Object message, Map<String, Object> map) throws Exception {
		validateUserStatus("runaway");
		
		OnlineUserDto user = GameMemory.getUser();
		PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		roomLogic.doUserQuit(room, user.getId());
		
		return;
	}
	
	@ActionAnnotation(action = "lose")
	public void lose(Object message, Map<String, Object> map) throws Exception {
		validateUserStatus("lose");
		OnlineUserDto user = GameMemory.getUser();
		PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		roomLogic.doUserQuit(room, user.getId());
	}
	
	private void validateUserStatus(String action) {
		OnlineUserDto user = GameMemory.getUser();
		
		if (user == null) {
			throw new NoAuthenticationException(action);
		}
		PlayRoomDto room = GameMemory.getRoomByRoomId(user.getRoomId());
		
		if (room == null) {
			throw new ActionFailedException(action);
		}
		
		 if (!(OnlineUserDto.STATUS_PLAYING.equals(user.getStatus()) || OnlineUserDto.STATUS_IN_ROOM.equals(user.getStatus()))) {
			 throw new ActionFailedException(action);
		 }
	}
	
	
	
	// ~ 如果是mock 请不要调用改接口
	@SuppressWarnings("unchecked")
	@ActionAnnotation(action = "downloadPlayerInfo")
	public void downloadPlayerInfo(Object message, Map<String, Object> map) throws Exception {
		playerInfoProcessorHelper.innerDownloadPlayerInfo(map);
		IoSession session = GameMemory.getCurrentSession();
		session.write(map);
	}
	
	@ActionAnnotation(action = "getOnlineUserList")
	public Map<String, Object> getOnlineUserList(Object message, Map<String, Object> map) throws Exception {
		// ~ 老代码 需要移植到新的逻辑上去
			List<OnlineUserDto> users = Lists.newArrayList();
			int limit = 100;
			for (Entry<Long, OnlineUserDto> entry : GameMemory.ONLINE_USERS.entrySet()) {
				limit--;
				if (limit == 0) {
					break;
				}
				users.add(entry.getValue());
			}
			
			Collections.sort(users, new FieldComparator<OnlineUserDto>("level", false));
			List<MobileUserDto> mUsers = Lists.newArrayList();
			for (OnlineUserDto u : users) {
				MobileUserDto mUser = new MobileUserDto(u);
				mUsers.add(mUser);
			}
			map.put("code", 200);
			map.put("result", mUsers);
			return map;
	}
	
	
				

	@ActionAnnotation(action = "uploadPlayerInfo")
	public String uploadPlayerInfo(Object message, Map<String, Object> map) {
		String jsonString = (String) message;

		if (StringUtils.isBlank(jsonString)) {
			throw new MessageNullException("user upload no message, uid:" + GameMemory.getUser().getId());
		}

		
		JSONObject jsonRoot = JSONObject.fromObject(jsonString);
		JSONObject json = jsonRoot.getJSONObject("playerInfo");

		if (json == null) {
			throw new MessageNullException("user upload no message, uid:" + GameMemory.getUser().getId());
		}

		OnlineUserDto onlineUser = GameMemory.getUser();
		User update = new User();

		if (json.containsKey("nickname")) {
			String nickName = json.getString("nickname");
			update.setNickName(nickName);
			
		}
//		if (!StringUtils.isBlank(nickName)) {
//			if (isNickNameExist(nickName)) {
//				throw new ActionFailedException(ExceptionConstant.NICKNAME_EXIST_CODE, "nickname exist",
//						onlineUser.getAction());
//			}
//		}
		update.setGmtModified(new Date());
		if (json.containsKey("in_use")) {
			update.setInUse(json.getString("in_use"));
		}
		if (json.containsKey("portrait")) {
			update.setPortrait(getValue(json.get("portrait")));
		}
		update.setId(onlineUser.getId());
		userService.updateSelectiveById(update);
		return ReturnConstant.OK;
	}

	public Integer getValue(Object o) {
		if (o == null)
			return null;
		return Integer.valueOf(o.toString());
	}

	public boolean isNickNameExist(String nickName) {
		User query = new User();
		query.setNickName(nickName);
		List<User> us = userService.getByDomainObjectSelective(query);

		if (!CollectionUtils.isEmpty(us)) {
			return true;
		}

		return false;
	}

	
	
	// uploadHearts
	@ActionAnnotation(action = "uploadHearts")
	public Map<String, Object> uploadHearts(Object message, Map<String, Object> map) {
		JSONObject json = null;
		try {
			json = JSONObject.fromObject(message);
			json.discard(action);

			OnlineUserDto onlineUser = GameMemory.getUser();
			UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
					onlineUser.getId(), UserMeta.HEART_NUM);
			UserMeta newItem = new UserMeta();
			newItem.setKey(UserMeta.HEART_NUM);
			newItem.setValue(String.valueOf(json));
			newItem.setUserId(onlineUser.getId());
			if (userMeta == null) {
				userMetaDao.add(newItem);
			} else {
				userMetaDao.updateSelectiveByCondition(newItem, "user_id = ? and user_key = ?",
						onlineUser.getId(), UserMeta.HEART_NUM);
			}
			map.put("code", 200);
			map.put("message", "uploadHearts successfully");
			return map;
		} catch (Exception e) {
			map.put("code", -1);
			map.put("message", "uploadHearts failed:" + message);
			return map;
		}
	}

	@ActionAnnotation(action = "downloadHearts")
	public Map<String, Object> downloadHearts(Object message, Map<String, Object> map) {
		OnlineUserDto onlineUser = GameMemory.getUser();
		UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
				onlineUser.getId(), UserMeta.HEART_NUM);
		if (userMeta == null || StringUtils.isBlank(userMeta.getValue())) {
			map.put("code", 200);
			map.put("message", "you have nothing!");
			return map;
		} else {
			JSONObject json = JSONObject.fromObject(userMeta.getValue());
			json.discard(action);
			json.discard(CODE_NAME);
			json.accumulate(action, "downloadHearts");
			json.accumulate(CODE_NAME, 200);
			GameMemory.getCurrentSession().write(json);
			// return null due to not to write
			return null;
		}
	}

	@ActionAnnotation(action = "uploadInventoryItem")
	public Map<String, Object> uploadInventoryItem(Object message, Map<String, Object> map) {
		JSONObject json = null;
		try {
			json = JSONObject.fromObject(message);
			String items = json.getString("items");
			if (StringUtils.isBlank(items)) {
				map.put("code", -1);
				map.put("message", "update failed, because no items existed:" + message);
				return map;
			}
			json.discard(action);
			updateMemberMetaItem(json, UserMeta.USER_INVENTORY_ITEM);
			map.put("code", 200);
			map.put("message", "uploadInventoryItem successfully");
			return map;
		} catch (Exception e) {
			map.put("code", -20);
			map.put("message", "uploadInventoryItem failed:" + message);
			return map;
		}
	}
	
	
	@ActionAnnotation(action = "uploadMedals")
	public Map<String, Object> uploadMedals(Object message, Map<String, Object> map) {
		JSONObject json = null;
		try {
			json = JSONObject.fromObject(message);
			String items = json.getString("medal");
			if (StringUtils.isBlank(items)) {
				map.put("code", -1);
				map.put("message", "update failed, because no items existed:" + message);
				return map;
			}
			json.discard(action);
			updateMemberMetaItem(json, UserMeta.USER_MEDAL);
			map.put("code", 200);
			map.put("message", "uploadMedals successfully");
			return map;
		} catch (Exception e) {
			map.put("code", -20);
			map.put("message", "uploadMedals failed:" + message);
			return map;
		}
	}

	
	@ActionAnnotation(action = "downloadMedals")
	public Map<String, Object> downloadMedals(Object message, Map<String, Object> map) {
		OnlineUserDto onlineUser = GameMemory.getUser();
		UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
				onlineUser.getId(), UserMeta.USER_MEDAL);
		if (userMeta == null || StringUtils.isBlank(userMeta.getValue())) {
			map.put("code", 200);
			map.put("message", "you have nothing!");
			return map;
		} else {
			JSONObject json = JSONObject.fromObject(userMeta.getValue());
			json.discard(action);
			json.discard(CODE_NAME);
			json.accumulate(action, "downloadMedals");
			json.accumulate(CODE_NAME, 200);
			GameMemory.getCurrentSession().write(json);
			return null;
		}
	}
	
	
	private void updateMemberMetaItem(JSONObject json, String key) {
		String updatedValue = GsonUtils.toJson(json);

		OnlineUserDto onlineUser = GameMemory.getUser();
		UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
				onlineUser.getId(), key);
		UserMeta newItem = new UserMeta();
		newItem.setKey(key);
		newItem.setValue(updatedValue);
		newItem.setUserId(onlineUser.getId());
		if (userMeta == null) {
			userMetaDao.add(newItem);
		} else {
			userMetaDao.updateSelectiveByCondition(newItem, "user_id = ? and user_key = ?",
					onlineUser.getId(), key);
		}
	}

	@ActionAnnotation(action = "downloadInventoryItem")
	public Map<String, Object> downloadInventoryItem(Object message, Map<String, Object> map) {
		OnlineUserDto onlineUser = GameMemory.getUser();
		UserMeta userMeta = userMetaDao.getFirstOneByCondition("user_id = ? and user_key = ?",
				onlineUser.getId(), UserMeta.USER_INVENTORY_ITEM);
		if (userMeta == null || StringUtils.isBlank(userMeta.getValue())) {
			map.put("code", 200);
			map.put("message", "you have nothing!");
			return map;
		} else {
//			HashMap<Object, Object> map = new mapper.readValue(json, HashMap.class);
			JSONObject json = JSONObject.fromObject(userMeta.getValue());
			json.discard(action);
			json.discard(CODE_NAME);
			json.accumulate(action, "downloadInventoryItem");
			json.accumulate(CODE_NAME, 200);
			GameMemory.getCurrentSession().write(json);
			// return null due to not to write
			return null;
		}
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, JSONException {

		
		org.codehaus.jettison.json.JSONObject json =
				new org.codehaus.jettison.json.JSONObject("{\"a\":\"b\", \"d\":{\"a\":\"b\"}}");
		System.out.println(json.has("hah"));
		//		String json = "{\"a\":\"b\", \"d\":{\"a\":\"b\"}}";

//		long time = System.currentTimeMillis();
//
//		for (int i = 0; i < 1000000; i++) {
//			
//			ObjectMapper mapper = new ObjectMapper();
//		}
//		System.out.println(System.currentTimeMillis() - time );
//
//		HashMap<Object, Object> map = mapper.readValue(json, HashMap.class);
//		Map o = (Map) map.get("d");
//		System.out.println(o.get("a"));

	}

}
