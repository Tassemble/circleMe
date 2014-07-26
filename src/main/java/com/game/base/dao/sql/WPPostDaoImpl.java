package com.game.base.dao.sql;

import org.springframework.stereotype.Repository;

import com.game.base.WPPost;
import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.base.dao.WPPostDao;



@DomainMetadata(domainClass = WPPost.class, tableName = "wp_posts", policyIdProperty = "id")
@Repository("wPPostDao")
public class WPPostDaoImpl extends BaseDaoSqlImpl<WPPost> implements WPPostDao {

	
	@Override
	public void testInsert(String sql){
		this.getSqlManager().updateRecord(sql);
	}
}
