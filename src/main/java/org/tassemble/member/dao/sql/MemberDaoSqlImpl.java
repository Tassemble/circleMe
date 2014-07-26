package org.tassemble.member.dao.sql;

import org.springframework.stereotype.Component;
import org.tassemble.member.dao.MemberDao;
import org.tassemble.member.domain.Member;

import com.game.base.commons.dao.annotation.DomainMetadata;
import com.game.base.commons.dao.sql.BaseDaoSqlImpl;


@Component
@DomainMetadata(domainClass= Member.class)
public class MemberDaoSqlImpl extends BaseDaoSqlImpl<Member> implements MemberDao {

}
