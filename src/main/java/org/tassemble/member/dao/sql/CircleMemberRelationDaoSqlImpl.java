package org.tassemble.member.dao.sql;

import org.springframework.stereotype.Component;
import org.tassemble.base.commons.dao.annotation.DomainMetadata;
import org.tassemble.base.commons.dao.sql.BaseDaoSqlImpl;
import org.tassemble.member.domain.CircleMemberRelation;
import org.tassemble.member.dao.CircleMemberRelationDao;


@Component
@DomainMetadata(domainClass= CircleMemberRelation.class)
public class CircleMemberRelationDaoSqlImpl extends BaseDaoSqlImpl<CircleMemberRelation> implements CircleMemberRelationDao {

}
