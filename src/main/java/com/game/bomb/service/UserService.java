package com.game.bomb.service;

import java.util.Map;

import com.game.base.commons.service.BaseService;
import com.game.bomb.domain.User;
import com.game.core.bomb.dto.BaseActionDataDto.GameSignUpData;


/**
 * @author CHQ
 * @since 2013-8-1 
 */
public interface UserService extends BaseService<User>{

	void addNewUser(GameSignUpData data);
	
	
	void updateUserBloodWithLock(User update);


	void updateForExchangeCoinToHeart(Long id, int number, int gainHeart);


	void updateHeartNumber(int number, Integer gainHeart, Long uid);



	void updateGoldForMinus(int goldNum, Long uid);


	void updateGoldForPlus(Integer goldNum, Long id);


	void updateGoldNumber(int number, Integer gainGold, Long uid);


	void refreshMaxAddedGolds(User user);

	void updateForAwardNextDayAndResponse(Long id, Map<String, Object> map);

}
