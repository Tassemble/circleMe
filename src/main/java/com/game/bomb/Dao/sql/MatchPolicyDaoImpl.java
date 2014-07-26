package com.game.bomb.Dao.sql;

import org.springframework.stereotype.Component;

import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;
import com.game.bomb.Dao.MatchPolicyDao;
import com.game.bomb.domain.MatchPolicy;


@Component
@DomainMetadata(domainClass=MatchPolicy.class)
public class MatchPolicyDaoImpl extends BaseDaoSqlImpl<MatchPolicy> implements MatchPolicyDao{

}
