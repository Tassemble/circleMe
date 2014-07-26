package org.tassemble.member.dao.sql;

import org.springframework.stereotype.Component;
import org.tassemble.base.commons.dao.annotation.DomainMetadata;
import org.tassemble.base.commons.dao.sql.BaseDaoSqlImpl;
import org.tassemble.member.domain.Member;
import org.tassemble.member.dao.MemberDao;


@Component
@DomainMetadata(domainClass= Member.class)
public class MemberDaoSqlImpl extends BaseDaoSqlImpl<Member> implements MemberDao {

}
