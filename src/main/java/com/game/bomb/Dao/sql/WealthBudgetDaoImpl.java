package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.bomb.Dao.WealthBudgetDao;
import com.game.bomb.domain.WealthBudget;

/**
 * @author CHQ
 * @since 2013-11-16 
 */
@Component
@DomainMetadata(domainClass=WealthBudget.class, idColumn="id", idProperty="id", tableName="wealth_budget")
public class WealthBudgetDaoImpl extends BaseDaoSqlImpl<WealthBudget> implements WealthBudgetDao{

}
