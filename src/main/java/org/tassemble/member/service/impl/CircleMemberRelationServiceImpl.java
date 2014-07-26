package org.tassemble.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tassemble.member.dao.CircleMemberRelationDao;
import org.tassemble.member.domain.CircleMemberRelation;
import org.tassemble.member.service.CircleMemberRelationService;

import com.game.base.commons.service.impl.BaseServiceImpl;


@Service
public class CircleMemberRelationServiceImpl extends BaseServiceImpl<CircleMemberRelationDao, CircleMemberRelation> implements CircleMemberRelationService {
	private CircleMemberRelationDao dao;

    public CircleMemberRelationDao getDao() {
        return dao;
    }

    @Autowired
    public void setCircleMemberRelationDao(CircleMemberRelationDao dao) {
        super.setBaseDao(dao);
        this.dao = dao;
    }
    
}
