package org.tassemble.member.dao.sql;

import org.springframework.stereotype.Component;
import org.tassemble.member.dao.CircleMemberDao;
import org.tassemble.member.domain.CircleMember;

import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;


@Component
@DomainMetadata(domainClass= CircleMember.class)
public class CircleMemberDaoSqlImpl extends BaseDaoSqlImpl<CircleMember> implements CircleMemberDao {

}
