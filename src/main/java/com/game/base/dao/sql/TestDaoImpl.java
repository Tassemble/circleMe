package com.game.base.dao.sql;

import org.springframework.stereotype.Repository;

import com.game.base.Test;
import com.game.base.commons.dao.BaseDao;
import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.base.dao.TestDao;



@DomainMetadata(domainClass = Test.class, tableName = "test", policyIdProperty = "id")
@Repository("testDao")
public class TestDaoImpl extends BaseDaoSqlImpl<Test> implements TestDao {

}
