package com.game.core.test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.service.IoAcceptor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.game.base.dao.BaseTestCase;
import com.game.bomb.Dao.UserMetaDao;
import com.game.bomb.domain.User;
import com.game.bomb.service.UserService;
import com.game.bomb.thirdaccount.service.SinaWeiboService;
import com.game.core.GameMemory;
import com.game.core.exception.ActionFailedException;
import com.game.core.exception.BombException;
import com.game.utils.GsonUtils;

public class GameMainTest extends BaseTestCase {

	@Autowired
	UserService			userService;

	@Autowired
	ApplicationContext	ctx;
	@Autowired
	SinaWeiboService sinaWeiboService;
	
	@Test
	public void testWeibo() {
		sinaWeiboService.validateAndGetWeiboUser("2.00Uk7pWDVaYNQBcbec6c7ed1vUJoyB", "1772403527");
	}

	@Test
	public void testRun() throws IOException {

		GameMemory.bizContext.put("ctx", ctx);
		IoAcceptor acceptor = (IoAcceptor) ctx.getBean("ioAcceptor");
		try {
			while (acceptor.isActive()) {
				TimeUnit.SECONDS.sleep(1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			acceptor.unbind();
		}
	}
	
	public static void main(String[] args) {
		ActionFailedException failed = new ActionFailedException(0, "msg", "action");
		
		if (BombException.class.isAssignableFrom(failed.getClass())) {
			Assert.assertEquals(true, true);
			System.out.println(true);
		} else {
			System.out.println(false);
			Assert.assertEquals(true, false);
		}
		
	}

	@Autowired
	UserMetaDao uesrMetaDao;
	
//	public static void main(String[] args) {
//		String json = "{\\\"action\\\":\\\"downloadInventoryItem\\\"}";
//		json = StringUtils.replace(json, "\"", "");
//		System.out.println(json);
//	}
//	
	@Test
	public void testDao() {
		uesrMetaDao.getAll();
	}

	@Test
	public void testDecodeMsg() {
		System.out
				.println(GsonUtils
						.toJson("{\"action\":\"forward\",\"code\":200,\"message\":\"AQAAAGxqZW1oamVsZmZmZWxkZGZmZWVobW1nbWVtZGZobWxpbGRmZGtkZWVnaGZoZGtsZWxsZWVnamVtZmpkZG1sZW1qbW1pZm1nZmVtZmlkZWVlbGdqZWhmbWZlbGhmZm1qZm1laGZsZ2loaGtnZ2lsZGRkZmxtZWdkaGhmZW1kaGZnZW1oZmRrZWRqZGZtbG1tZ2xsZW1nZWZpbGVlZGxoZWhnbGxlaWhkZWs=\"}"));
	}

	@Test
	public void testAddUser() {
		User u = new User();
		u.setId(userService.getId());
		u.setMd5Password(DigestUtils.md5Hex("FirstUser"));
		u.setUsername("CHQ");
		u.setNickName("CHQ");
		userService.add(u);

		User query = new User();
		query.setMd5Password(DigestUtils.md5Hex("FirstUser"));
		query.setUsername("CHQ");
		List<User> users = userService.getByDomainObjectSelective(query);
		if (!CollectionUtils.isEmpty(users)) {
			System.out.println("find CHQ");
		}

	}
}
