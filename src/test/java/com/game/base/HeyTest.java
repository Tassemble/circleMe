package com.game.base;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.game.base.dao.BaseTestCase;
import com.game.base.dao.TestDao;

public class HeyTest extends BaseTestCase{

	
	@Autowired
	TestDao testDao;
	
	
	@Test
	public void test() {
		List<com.game.base.Test> records = testDao.getAll();
		System.out.println(records.get(0).getName());
		
		System.out.println(testDao.getFirstOneByCondition("id = ?", 1).getName());
		
	}
	
}
