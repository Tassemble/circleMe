package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.bomb.Dao.UserDao;
import com.game.bomb.domain.User;

/**
 * @author CHQ
 * @since 2013-8-1 
 */
@Component
@DomainMetadata(domainClass=User.class, idColumn="id", idProperty="id", tableName="user")
public class UserDaoImpl extends BaseDaoSqlImpl<User> implements UserDao{

	@Override
	public void updateForExchangeCoinToHeart(Long uid, int number, int heart) {
		this.getSqlManager().updateRecords("update " + this.getTableName()  
				+ " set gmt_modified = ?, in_got = in_got - ?, heart_num = ? where id = ? " , 
				System.currentTimeMillis(), number, heart, uid);
	}

	@Override
	public void lockUser(Long uid) {
		this.getSqlManager().executeQuery("select * from user where id = ? for update", uid); 
	}

}
