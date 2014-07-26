package com.game.core.action.bomb.logic;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.base.commons.utils.text.JsonUtils;
import com.game.bomb.Dao.GameAttributeDao;
import com.game.bomb.domain.GameAttribute;
import com.game.bomb.domain.User;
import com.game.bomb.service.UserService;


@Component
public class BloodLogic {

	
	private static final Logger LOG = LoggerFactory.getLogger(BloodLogic.class);
	
	
	@Autowired
	GameAttributeDao gameAttributeDao;
	
	
	public volatile static Long DefaultBloodRecoveryOfDuration = 10 * 60 * 1000L;//ten minites;
	
	
	
	@Autowired
	UserService userService;
	
	/**
	 * 血量的设计：
	 * 1. 需要一个满血量
	 * 2. 需要一个剩余血量
	 * 3. 记录由满血到非满血的时间点(blood_time, 没有竞争条件，所以不够成并发)
	 * 4. 非满血状态在一定时间范围内可以恢复部分或者全部血量至剩余血量中(这里剩余血量构成竞争条件，需要并发控制)，
	 *    blood_time记录恢复的时间点
	 * 5. 记录一个恢复时长 在game_attribute表中   
	 */
	public void processBloodWithRealTime(User user) {
		if (user == null || user.getId() == null) {
			return;
		}
		/**
		 * 处理条件为当前血量小于总血量
		 * 
		 * 处理方式：
		 * 1. (当前时间-blood_time)%duration = 恢复的生命值，如果取模>1，则执行2，否则执行3
		 * 2. 将恢复的生命值加到剩余的生命值中(heartNum),同时，需要更新blood_time为当前时间，执行3
		 * 3. 返回给前段的时间为当前时间-blood_time_in_db，表示还需要多长时间才能就加生命
		 */
		if (user.getHeartNum() == null || user.getFullHeart() == null) {
			LOG.error("user message is error, heart num or full heart is null for user :"  +JsonUtils.toJson(user));
			return;
		}
		
		if (user.getHeartNum() >= user.getFullHeart()) { // 处理条件为当前血量小于总血量
			return;
		}
		
		Long curTime = System.currentTimeMillis();
		
		//容错
		if (user.getBloodTime() == null || user.getBloodTime().getTime() <= 0) {
			User update = new User();
			update.setId(user.getId());
			update.setBloodTime(new Date(curTime));
			update.setGmtModified(new Date(curTime));
			userService.updateSelectiveById(update);
			user.setBloodTime(new Date(curTime));
		}
		
		
		long heardGain = (curTime - user.getBloodTime().getTime()) / getBloodRecoveryOfDuration();
		
		if (heardGain >= 1) {
			
			int heartNow = user.getHeartNum() + (int)heardGain;
			if (heartNow > user.getFullHeart()) {
				heartNow = user.getFullHeart();
			}
			User update = new User();
			update.setId(user.getId());
			update.setBloodTime(new Date(curTime));
			update.setHeartNum(heartNow);
			update.setGmtModified(new Date(curTime));
			userService.updateUserBloodWithLock(update);
			user.setBloodTime(new Date(curTime));
			user.setHeartNum(heartNow);
		}
	}
	
	
	public Long getBloodRecoveryOfDuration() {
		return DefaultBloodRecoveryOfDuration;
//		GameAttribute attr = new GameAttribute();
//		attr.setAttrName(GameAttribute.KEY_DURATION_OF_RENEW_BLOOD);
//		List<GameAttribute> results = gameAttributeDao.getByDomainObjectSelective(attr);
//		if (CollectionUtils.isEmpty(results)) {
//			return DefaultBloodRecoveryOfDuration;
//		}
//		
//		try {
//			Long duration = Long.valueOf(results.get(0).getAttrValue());
//			if (!duration.equals(DefaultBloodRecoveryOfDuration)) {
//				DefaultBloodRecoveryOfDuration = duration;
//			}
//			return DefaultBloodRecoveryOfDuration;
//		} catch (NumberFormatException e) {
//			LOG.error(e.getMessage(), e);
//			return DefaultBloodRecoveryOfDuration;
//		}
		
	}
}
