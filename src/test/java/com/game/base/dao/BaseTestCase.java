package com.game.base.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.game.bomb.domain.User;
import com.game.bomb.service.UserService;
import com.game.core.GameMemory;
import com.game.core.bomb.dto.GameSessionContext;
import com.game.core.bomb.dto.OnlineUserDto;
import com.game.dao.JunitTransactionSpringContextTest;

@ContextConfiguration(locations = { "classpath:/applicationContext-bo.xml",
		"classpath:/biz/applicationContext-framework-aop.xml",
		"classpath:/biz/applicationContext-framework-dao-base.xml" })
public class BaseTestCase extends JunitTransactionSpringContextTest {

	@Autowired
	UserService	service;

	public void setOnlineUser(Long id) {
		GameMemory.LOCAL_SESSION_CONTEXT.set(new GameSessionContext());
		User user = service.getById(id);
		OnlineUserDto onlineUser = new OnlineUserDto(user);
		GameMemory.setUser(onlineUser);
		GameMemory.ONLINE_USERS.put(user.getId(), onlineUser);
	}
}
