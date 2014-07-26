package org.tassemble.member.dao.sql;

import org.springframework.stereotype.Component;
import org.tassemble.base.commons.dao.annotation.DomainMetadata;
import org.tassemble.base.commons.dao.sql.BaseDaoSqlImpl;
import org.tassemble.member.domain.CircleMember;
import org.tassemble.member.dao.CircleMemberDao;


@Component
@DomainMetadata(domainClass= CircleMember.class)
public class CircleMemberDaoSqlImpl extends BaseDaoSqlImpl<CircleMember> implements CircleMemberDao {

}
