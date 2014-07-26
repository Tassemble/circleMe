package com.game.bomb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.base.commons.dao.BaseDao;
import com.game.base.commons.service.impl.BaseServiceImpl;
import com.game.bomb.Dao.FriendRelationDao;
import com.game.bomb.domain.FriendRelation;
import com.game.bomb.service.FriendRelationService;


@Service
public class FriendRelationServiceImpl extends BaseServiceImpl<BaseDao<FriendRelation>, FriendRelation> implements FriendRelationService{

	
	FriendRelationDao friendRelationDao;

	public FriendRelationDao getFriendRelationDao() {
		return friendRelationDao;
	}

	
	@Autowired
	public void setFriendRelationDao(FriendRelationDao friendRelationDao) {
		super.setBaseDao(friendRelationDao);
		this.friendRelationDao = friendRelationDao;
	}
	
	
}
