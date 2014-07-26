package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.bomb.Dao.FriendRelationDao;
import com.game.bomb.domain.FriendRelation;


@Component
@DomainMetadata(tableName="friend_relation", domainClass=FriendRelation.class, idColumn="id", idProperty="id")
public class FriendRelationDaoImpl extends BaseDaoSqlImpl<FriendRelation> implements FriendRelationDao{

}
