package com.game.base.dao.sql;

import org.springframework.stereotype.Repository;

import com.game.base.User;
import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.base.dao.UserDao;


@DomainMetadata(domainClass = User.class, tableName = "wp_users", idColumn="ID", idProperty="id")
@Repository("userDao")
public class UserDaoImpl extends BaseDaoSqlImpl<User> implements UserDao{

}
