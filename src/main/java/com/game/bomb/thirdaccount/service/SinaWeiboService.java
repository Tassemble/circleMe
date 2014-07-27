package com.game.bomb.thirdaccount.service;

import org.tassemble.member.domain.Member;

public interface SinaWeiboService {
	public Member validateAndGetWeiboUser(String token, String username);
}
