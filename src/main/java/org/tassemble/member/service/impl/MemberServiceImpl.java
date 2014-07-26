package org.tassemble.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tassemble.member.dao.MemberDao;
import org.tassemble.member.domain.Member;
import org.tassemble.member.service.MemberService;

import com.game.base.commons.service.impl.BaseServiceImpl;


@Service
public class MemberServiceImpl extends BaseServiceImpl<MemberDao, Member> implements MemberService {
	private MemberDao dao;

    public MemberDao getDao() {
        return dao;
    }

    @Autowired
    public void setMemberDao(MemberDao dao) {
        super.setBaseDao(dao);
        this.dao = dao;
    }
    
}
