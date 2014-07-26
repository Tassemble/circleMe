package org.tassemble.member.dao.sql;

import org.springframework.stereotype.Component;
import org.tassemble.member.dao.CircleMemberRelationDao;
import org.tassemble.member.domain.CircleMemberRelation;

import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;


@Component
@DomainMetadata(domainClass= CircleMemberRelation.class)
public class CircleMemberRelationDaoSqlImpl extends BaseDaoSqlImpl<CircleMemberRelation> implements CircleMemberRelationDao {

}
