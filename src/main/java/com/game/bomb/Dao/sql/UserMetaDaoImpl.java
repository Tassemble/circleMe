package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.bomb.Dao.UserMeta;
import com.game.bomb.Dao.UserMetaDao;

@Component
@DomainMetadata(domainClass=UserMeta.class, idColumn="id", idProperty="id", tableName="user_meta")
public class UserMetaDaoImpl extends BaseDaoSqlImpl<UserMeta> implements UserMetaDao{

}
