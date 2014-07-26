package com.game.bomb.Dao;

import com.game.base.commons.dao.BaseDao;
import com.game.bomb.domain.User;


/**
 * @author CHQ
 * @since 2013-8-1 
 */
public interface UserDao extends BaseDao<User>{

	void updateForExchangeCoinToHeart(Long uid, int number, int gainHeart);
	
	
	
	void lockUser(Long uid);

}
