package com.game.bomb.thirdaccount.service;

import com.game.bomb.domain.User;

public interface SinaWeiboService {
	public User validateAndGetWeiboUser(String token, String username);
}
