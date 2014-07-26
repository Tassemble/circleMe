package com.game.bomb.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.base.commons.dao.BaseDao;
import com.game.base.commons.service.impl.BaseServiceImpl;
import com.game.base.commons.utils.text.JsonUtils;
import com.game.bomb.Dao.UserDao;
import com.game.bomb.Dao.WealthBudgetDao;
import com.game.bomb.constant.BombConstant;
import com.game.bomb.domain.User;
import com.game.bomb.domain.WealthBudget;
import com.game.bomb.mobile.dto.DayAward;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.action.bomb.logic.BloodLogic;
import com.game.core.bomb.dto.BaseActionDataDto.GameSignUpData;
import com.game.core.exception.BombException;

/**
 * @author CHQ
 * @since 2013-8-1 
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<BaseDao<User>, User> implements UserService{

	UserDao userDao;
	
	
	private static final Logger	LOG_TRADE			= LoggerFactory.getLogger("transaction");
	
	
	@Autowired
	BloodLogic bloodLogic;
	
	
	@Autowired
	WealthBudgetDao wealthBudgetDao;

	public UserDao getUserDao() {
		return userDao;
	}

	
	@Autowired
	public void setUserDao(UserDao userDao) {
		super.setBaseDao(userDao);
		this.userDao = userDao;
	}
	
	


	@Override
	public void addNewUser(GameSignUpData data) {
		User newItem = new User();
		Date now = new Date();
		
		newItem.setLoginType(data.getLoginType());
		newItem.setUsername(data.getUsername());
		if (StringUtils.isNotBlank(data.getPassword())) {
			newItem.setMd5Password(DigestUtils.md5Hex(data.getPassword()));
		} else {
			newItem.setMd5Password("");
		}
		newItem.setNickName(data.getNickname());
		newItem.setHeartNum(User.CONSTANT_FULL_HEART);
		newItem.setLevel(1);
		newItem.setLoserNum(0);
		newItem.setPortrait(0);
		newItem.setRunawayNum(0);
		newItem.setVictoryNum(0);
		newItem.setGmtCreate(now);
		newItem.setGmtModified(now);
		newItem.setFullHeart(User.CONSTANT_FULL_HEART);
		newItem.setAwardDays(0); //
		newItem.setBloodTime(new Date());
		newItem.setEnable(true);
		newItem.setGold(WealthBudget.DEFAULT_WEALTH);
		newItem.setInGot(WealthBudget.DEFAULT_WEALTH_INGOT);
		add(newItem);
		
		User qUser = new User();
		qUser.setUsername(data.getUsername());
		qUser.setLoginType(data.getLoginType());
		User userFromDB = getByDomainObjectSelective(qUser).get(0);
		
		if (WealthBudget.DEFAULT_WEALTH_INGOT > 0) {
			WealthBudget wealth = new WealthBudget();
			wealth.setBudgetType(WealthBudget.BUDGET_TYPE_SIGNUP);
			wealth.setGmtCreate(now);
			wealth.setGmtModified(now);
			wealth.setOrderId(0L);
			wealth.setQuantity(WealthBudget.DEFAULT_WEALTH_INGOT);
			wealth.setUid(userFromDB.getId());
			wealthBudgetDao.add(wealth);
		}
	}


	@Override
	public void updateUserBloodWithLock(User user) {
		
		//try to get locker
		this.getByCondition("id = ? for update", user.getId());
		updateSelectiveById(user);
	}

	@Override
	public void updateGoldNumber(int number, Integer gainGold, Long uid) {
		this.getUserDao().lockUser(uid);
		User user = getById(uid);
		if (user.getInGot() == null || user.getInGot() < number) {
			throw new BombException(-1000, "not enough inGot, only "+ user.getInGot());
		}
		long nowGold = gainGold + user.getGold();
		
		//record
		WealthBudget wealthBudget = new WealthBudget();
		wealthBudget.setBudgetType(WealthBudget.BUDGET_TYPE_EXCHANGE_GOLD);
		wealthBudget.setGmtCreate(new Date());
		wealthBudget.setGmtModified(new Date());
		wealthBudget.setOrderId(0L);
		wealthBudget.setQuantity(-(long)number);
		wealthBudget.setUid(uid);
		wealthBudgetDao.add(wealthBudget);
		
		LOG_TRADE.info("user[ " + JsonUtils.toJson(user) +" ] exchange gold using in_got " + number);
		
		User update = new User();
		update.setGold(nowGold);
		update.setInGot(user.getInGot() - number);
		update.setId(uid);
		update.setGmtModified(new Date());
		this.updateSelectiveById(update);
	}
	
	
	@Override
	public void updateHeartNumber(int number, Integer gainHeart, Long uid) {
		this.getUserDao().lockUser(uid);
		User user = getById(uid);
		if (user.getInGot() == null || user.getInGot() < number) {
			throw new BombException(-1000, "not enough inGot, only "+ user.getInGot());
		}
		int nowHeart = gainHeart + user.getHeartNum();
		
		//record
		WealthBudget wealthBudget = new WealthBudget();
		wealthBudget.setBudgetType(WealthBudget.BUDGET_TYPE_EXCHANGE_HEART);
		wealthBudget.setGmtCreate(new Date());
		wealthBudget.setGmtModified(new Date());
		wealthBudget.setOrderId(0L);
		wealthBudget.setQuantity(-(long)number);
		wealthBudget.setUid(uid);
		wealthBudgetDao.add(wealthBudget);
		
		LOG_TRADE.info("user[ " + JsonUtils.toJson(user) +" ] exchange heart using in_got " + number);
		updateForExchangeCoinToHeart(uid, number, nowHeart);
	}

	@Override
	public void updateForExchangeCoinToHeart(Long uid, int number, int gainHeart) {
		userDao.updateForExchangeCoinToHeart(uid, number, gainHeart);
	}


	
	
	@Override
	public void updateGoldForMinus(int goldNum, Long uid) {
		
		userDao.lockUser(GameMemory.getUser().getId());
		
		User user = getById(uid);
		if (user.getGold() == null || user.getGold() < goldNum) {
			throw new BombException(-1001, "not enough gold, only "+ user.getInGot());
		}
		
		User update = new User();
		update.setGold(user.getGold() - goldNum);
		update.setId(uid);
		updateSelectiveById(update);
	}

	@Override
	public void refreshMaxAddedGolds(User user) {
		Long now = System.currentTimeMillis();
		if (user.getLastGoldsaddedTime() == null) {
			user.setLastGoldsaddedTime(0L);
		}
		
		Calendar cal = Calendar.getInstance(); 
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.set(Calendar.SECOND, 0); 
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.MILLISECOND, 0); 
		Long todayStartTime = cal.getTimeInMillis();
		
		if (user.getLastGoldsaddedTime() < todayStartTime) {//说明是昨天更新的，这时候将gold又可以继续增加
			User update = new User();
			update.setMaxAddedGoldsDay(User.CONSTANT_MAX_ADDED_GOLDS_DAY);
			update.setLastGoldsaddedTime(now);
			update.setId(user.getId());
			updateSelectiveById(update);
			user.setMaxAddedGoldsDay(User.CONSTANT_MAX_ADDED_GOLDS_DAY);
		}
	}

	@Override
	public void updateGoldForPlus(Integer goldNum, Long uid) {
		userDao.lockUser(GameMemory.getUser().getId());
		
		User user = getById(uid);
		refreshMaxAddedGolds(user);
		
		if (user.getMaxAddedGoldsDay() == null || user.getMaxAddedGoldsDay() < goldNum) {
			throw new BombException(-1005, "award gold has reach max :"+ user.getMaxAddedGoldsDay());
		}
		User update = new User();
		update.setHeartNum(user.getHeartNum() + 1);
		update.setVictoryNum(user.getVictoryNum() + 1);
		update.setGold(user.getGold() + goldNum);
		update.setMaxAddedGoldsDay(user.getMaxAddedGoldsDay() - goldNum);
		update.setLastGoldsaddedTime(System.currentTimeMillis());
		update.setId(uid);
		updateSelectiveById(update);
	}


	@Override
	public void updateForAwardNextDayAndResponse(Long id,  Map<String, Object> map) {
		map.put("action", "everydayAward");
		map.put("code", 201); // award days end!!!, please attend activities
		
		if (id == null) {
			return;
		}
		
		userDao.lockUser(id);
		
		// get awardDays if it is largher than max_award_days then return code 201
		User userInDB = userDao.getById(id);
		if (userInDB == null) {
			return;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.set(Calendar.SECOND, 0); 
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.MILLISECOND, 0); 
		Long todayStartTime = cal.getTimeInMillis();
		
		if (userInDB.getLastAwardTime() == null || userInDB.getLastAwardTime() < todayStartTime) { //如果比今天的时间要小 那就更新一下
			
			bloodLogic.processBloodWithRealTime(userInDB);
			
			int awardDay = userInDB.getAwardDays() + 1;
			
			Long now = System.currentTimeMillis();
			User update = new User();
			update.setAwardDays(awardDay);
			update.setLastAwardTime(now);
			update.setGmtModified(new Date(now));
			update.setId(userInDB.getId());
			
			DayAward award = BombConstant.EVERYDAY_AWARDS.get(awardDay);
			//检查是否能获得奖品
			if (award != null) {
				update.setGold(userInDB.getGold() + award.getGold());
				update.setHeartNum(userInDB.getHeartNum() + award.getHeart());
				
				//response
				map.put("days", awardDay);
				map.put("heart", award.getHeart());
				map.put("gold", award.getGold());
				map.put("code", 200);
			} else {
				//nothing to do, you can't award
			}
			this.updateSelectiveById(update);
		}
	}
	
	
	
	
}
