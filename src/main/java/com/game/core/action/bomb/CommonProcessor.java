package com.game.core.action.bomb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tassemble.circle.domain.dto.json.CirclePointDto;
import org.tassemble.circle.domain.dto.json.CirclePointReadMode;
import org.tassemble.circle.logic.MongoGeoLogic;
import org.tassemble.member.domain.CircleMemberRelation;
import org.tassemble.member.domain.Member;
import org.tassemble.member.service.CircleMemberRelationService;
import org.tassemble.member.service.MemberService;

import com.game.base.commons.utils.collection.FieldComparator;
import com.game.base.commons.utils.sql.SqlBuilder;
import com.game.base.commons.utils.text.JsonUtils;
import com.game.bomb.Dao.UserDao;
import com.game.bomb.Dao.UserMeta;
import com.game.bomb.Dao.UserMetaDao;
import com.game.bomb.config.BombConfig;
import com.game.bomb.constant.BombConstant;
import com.game.bomb.domain.User;
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
import com.game.core.bomb.logic.RoomLogic;
import com.game.core.bomb.play.dto.PlayRoomDto;
import com.game.core.exception.ActionFailedException;
import com.game.core.exception.BombException;
import com.game.core.exception.MessageNullException;
import com.game.core.exception.NoAuthenticationException;
import com.game.core.utils.CellLocker;
import com.game.utils.CommonUtils;
import com.game.utils.HttpClientUtils;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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
	
    @Autowired
    CellLocker<String>          locker;
	
	static ObjectMapper mapper = new ObjectMapper();
	
	

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
		String updatedValue = CommonUtils.toJson(json);

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
	
	
	
	public DBCollection getDefaultCollection() {
	    return MongoGeoLogic.getDefaultCollection();
	}
	
	
	@Autowired
	MongoGeoLogic MongoGeoLogic;
	
	public DB getDefaultDB() {
	    return MongoGeoLogic.getDefaultDB();
    }
	
	@ActionAnnotation(action = "queryPeopleAroundMe")
    public Map<String, Object> queryPeopleAroundMe(Object message, Map<String, Object> map) throws Exception {
	    map.put("action", "queryPeopleAroundMe");
	    map.put("code", 200);
	    final Long uid = GameMemory.getUser().getId();
	    
	    HashMap<Object, Object> parameters = mapper.readValue(String.valueOf(message), HashMap.class);
	    
	    
	    Object meterObj = parameters.get("meter");
	    
	    
	    if (meterObj == null) {
            throw new BombException(-1, "queryPeopleAroundMe", "one of them[longitude, latitude, meter] is null, please check");
        }
	    
	    
	    
	    Double meter= Double.valueOf(String.valueOf(meterObj));
	    
	    
	   
	    
	    DBObject iLocationQuery = new BasicDBObject();
	    iLocationQuery.put("memberInfo.memberId", uid);
	    DBObject dbResult = getDefaultCollection().findOne(iLocationQuery);
	    CirclePointDto point = null;
	    if (dbResult != null) {
	        point = CommonUtils.getFromJson(dbResult.toString(), CirclePointDto.class);
	    }
	    
	    if (point == null || point.getCoordinate() == null 
	            || point.getCoordinate().getLatitude() == null 
	            || point.getCoordinate().getLongitude() == null )
	    {
	        throw new BombException(-2001, "queryPeopleAroundMe", "please upload your location");
	    }
	    
	    
	    
	    DBObject dbObject = new BasicDBObject();
        dbObject.put("geoNear", "location");
        dbObject.put("near", Arrays.asList(point.getCoordinate().getLongitude(), point.getCoordinate().getLatitude()));
        dbObject.put("spherical", "true");
        dbObject.put("maxDistance", meter / 6371000);
        dbObject.put("num", 300);
        dbObject.put("distanceMultiplier", 6371000);
        DBObject queryObject = new BasicDBObject();
        queryObject.put("memberInfo.live", true);
        dbObject.put("query", queryObject);
        
        
	    
        CommandResult result = getDefaultDB().command(dbObject);
        String results = result.getString("results");
        
        if (StringUtils.isBlank(results)) {
            map.put("code", 200);
            map.put("pointsList", CollectionUtils.EMPTY_COLLECTION);
            return map;
        }
        
        
        List<CirclePointReadMode> points = CommonUtils.fromJson(results, new TypeToken<List<CirclePointReadMode>>(){}.getType());
        List<CirclePointDto> pointsList = new ArrayList<CirclePointDto>();
        
       
        
        
        if (CollectionUtils.isNotEmpty(points)) {
            for (CirclePointReadMode circlePointReadMode : points) {
                CirclePointDto writeModePoint = new CirclePointDto(circlePointReadMode);
                pointsList.add(writeModePoint);
            }
            //point.getCoordinate().getLongitude(), point.getCoordinate().getLatitude()
            
            
            //circle point
            double longitude = point.getCoordinate().getLongitude();
            double latitude = point.getCoordinate().getLatitude();
            
            double y1 = latitude;
            double x1 = longitude;
            
//            circlePoint 
            for (Iterator<CirclePointDto> iter = pointsList.iterator();iter.hasNext();) {
                try {
                    CirclePointDto circlePoint = iter.next();
                    
                    if (circlePoint.getMemberInfo() != null) {
                        if (uid.equals(circlePoint.getMemberInfo().getMemberId())) {
                            
                            map.put("me", circlePoint);
                            iter.remove();//self
                            continue;
                        }
                    }
                    
                    //k > 0  第一第三象限 如果y点比中心点的y大 则 为第一象限，否则是第三象限
                    //k < 0 第二第四象限  如果y点比中心点的y大 则 为第二象限，否则是第四象限
                    //x1 - x2 = 0 x轴  如果y点比中心点的y大 则为x轴第一象限  否则为x轴第三象限
                    //
                    double y2 = circlePoint.getCoordinate().getLatitude();
                    double x2 = circlePoint.getCoordinate().getLongitude();
                    double k = 0;
                    double angle = 0;
                    if (Math.abs(x1 - x2) > 0.01) {
                        k = (y1 - y2) / (x1 - x2);
                        if (k > 0) {
                            if (y2 - y1 > 0) {
                                angle = Math.atan(k);
                            } else {
                                angle = Math.atan(k) + 180;
                            } 
                        } else if (k < 0) {
                            if (y2 - y1 >= 0) {
                                angle = 180 + Math.atan(k);
                            } else {
                                angle = 360 + Math.atan(k);
                            }
                        } else {
                            if (x2 - x1 > 0) {
                                angle = 0;
                            } else {
                                angle = 180;
                            }
                        }
                    } else {
                        if (y2 - y1 >= 0) {
                            angle = 90;
                        } else {
                            angle = 270;
                        }
                    }
                   
                    circlePoint.setDegree(angle);
                } catch (Exception e) {
                    iter.remove();
                    LOG.error(e.getMessage(), e);
                }
            }
            
        }
        
        
        if (CollectionUtils.isNotEmpty(pointsList)) {
            List<Long> memberIds = new ArrayList<Long>();
            for (CirclePointDto circlePointDto : pointsList) {
                memberIds.add(circlePointDto.getMemberInfo().getMemberId());
            }
            
            List<Member> members = MemberService.getByIdList(memberIds);
            Map<Long, Member> mapping = CommonUtils.makeMapByProperty(members, "id");
            for (CirclePointDto pointDto : pointsList) {
                Member m = mapping.get(pointDto.getMemberInfo().getMemberId());
                pointDto.getMemberInfo().setNickname(m.getNickname());
            }
            
            List<CircleMemberRelation> relations = 
                    CircleMemberRelationService.getByCondition("self_id = ? and " +SqlBuilder.inSql("partner_id", memberIds), uid);
            if (CollectionUtils.isNotEmpty(relations)) {
                Map<Long, CircleMemberRelation> relatoinMapping = CommonUtils.makeMapByProperty(relations, "partnerId");
                for (CirclePointDto pointDto : pointsList) {
                    CircleMemberRelation relation = relatoinMapping.get(pointDto.getMemberInfo().getMemberId());
                    pointDto.setColor(relation.getRelativeColor());
                }
            }
        }
        
        
        map.put("pointsList", pointsList);
	    return map;
	}
	
	@Autowired
	CircleMemberRelationService CircleMemberRelationService;

	
	@ActionAnnotation(action = "uploadGeoInfo")
    public String uploadGeoInfo(Object message, Map<String, Object> map) throws Exception {
	    Long uid = GameMemory.getUser().getId();
	    
	    final int UPDATE_LIMIT_IN_SECONDS = 2; 
	    
	    String key = "updateGeoInfo_" + uid;
	    try {
    	    locker.lock("",  key);
    	    //每个用户限制为每秒一次上传机会，超过了丢弃
    	    Long expireTime = GameMemory.locationUpdate.get(uid);
    	    if (expireTime == null || expireTime < System.currentTimeMillis()) {
    	        //please exec
    	        //update expireTime
    	        expireTime = System.currentTimeMillis() + UPDATE_LIMIT_IN_SECONDS * 1000;
    	        GameMemory.locationUpdate.put(uid, expireTime);
    	    } else {
    	        //reject it;
    	        return ReturnConstant.OK;
    	    }
    	    
	    } finally {
	        locker.unLock("", key);
	    }
	    
	    HashMap<Object, Object> parameters = mapper.readValue(String.valueOf(message), HashMap.class);
        Object longitudeObj = parameters.get("longitude");
        Object latitudeObj = parameters.get("latitude");
        
        if (longitudeObj == null || latitudeObj == null) {
            throw new BombException(-2002, "one of them[longitude, latitude] is null, please check");
        }
        
        
        updateUserLocation(uid, longitudeObj, latitudeObj);
        
        return ReturnConstant.OK;
    }



	
	@Autowired
	MemberService MemberService;

    private void updateUserLocation(Long uid, Object longitudeObj, Object latitudeObj) {
        double longitude = Double.valueOf(String.valueOf(longitudeObj));
        double latitude = Double.valueOf(String.valueOf(latitudeObj));
        DBObject query = new BasicDBObject();
        query.put("memberInfo.memberId", uid);
        
        
        Member member = MemberService.getById(uid);
        
        DBCursor cursor = getDefaultCollection().find(query);
        if (cursor.hasNext()) {
            DBObject dbObject = cursor.next();
            if (dbObject == null) {
                return;
            }
            DBObject object = new BasicDBObject();
            Map<String, Object> coordinate = new HashMap<String, Object>();
            coordinate.put("longitude", longitude);
            coordinate.put("latitude", latitude);
            
            object.put("coordinate", coordinate);
            
            DBObject memberInfo = new BasicDBObject();
            memberInfo.put("memberId", uid);
            memberInfo.put("sex", member.getSex());
            memberInfo.put("live", true);
            object.put("memberInfo", memberInfo);
            getDefaultCollection().update(query, object);
        } else {
            DBObject object = new BasicDBObject();
            Map<String, Object> coordinate = new HashMap<String, Object>();
            coordinate.put("longitude", longitude);
            coordinate.put("latitude", latitude);
            
            
            DBObject memberInfo = new BasicDBObject();
            memberInfo.put("memberId", uid);
            memberInfo.put("sex", member.getSex());
            memberInfo.put("live", true);
            object.put("coordinate", coordinate);
            object.put("memberInfo", memberInfo);
            
            
            
            
            
            getDefaultCollection().insert(object);
        }
    }
	
	
	@ActionAnnotation(action = "markColor")
    public Map<String, Object> markColor(Object message, Map<String, Object> map) {
        
        
	    return null;
    }

	@ActionAnnotation(action = "userMove")
    public Map<String, Object> userMove(Object message, Map<String, Object> map) {
        //最大30公里
	    //
	    
	    
	    
        
	    return null;
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
		
		
		String result = "{ \"_id\" : { \"$oid\" : \"53d6641003648388c3456de9\"} , \"coordinate\" : { \"longitude\" : 130.4915 , \"latitude\" : 36.23333933} , \"memberInfo\" : { \"memberId\" : 891027 , \"sex\" : 1}}";
		
		
		CirclePointDto point = CommonUtils.getFromJson(result, CirclePointDto.class);
		System.out.println(result);
		System.out.println(CommonUtils.toJson(point));
	}
	
	
	
	

}
