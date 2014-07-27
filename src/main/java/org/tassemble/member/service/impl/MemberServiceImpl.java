package org.tassemble.member.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tassemble.member.dao.MemberDao;
import org.tassemble.member.domain.Member;
import org.tassemble.member.service.MemberService;

import com.game.base.commons.service.impl.BaseServiceImpl;
import com.game.core.bomb.dto.BaseActionDataDto.GameSignUpData;


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
    
    
//    
    @Override
    public void addNewUser(GameSignUpData signUpData) {
        Member member = new Member();
        member.setGmtCreate(System.currentTimeMillis());
        member.setGmtModified(System.currentTimeMillis());
        member.setLoginType(signUpData.getLoginType());
        member.setNickname(signUpData.getNickname());
        member.setPassword(DigestUtils.md5Hex(signUpData.getPassword()));
        member.setUsername(signUpData.getUsername()); 
        member.setSex(signUpData.getSex());
        member.setPhone("");
        this.add(member);
    }
    
}
