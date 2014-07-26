package org.tassemble.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.tassemble.base.commons.service.impl.BaseServiceImpl;
import org.tassemble.member.dao.CircleMemberDao;
import org.tassemble.member.domain.CircleMember;
import org.tassemble.member.service.CircleMemberService;


@Service
public class CircleMemberServiceImpl extends BaseServiceImpl<CircleMemberDao, CircleMember> implements CircleMemberService {
	private CircleMemberDao dao;

    public CircleMemberDao getDao() {
        return dao;
    }

    @Autowired
    public void setCircleMemberDao(CircleMemberDao dao) {
        super.setBaseDao(dao);
        this.dao = dao;
    }
    
}
