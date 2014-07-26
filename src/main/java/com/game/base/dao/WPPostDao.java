package com.game.base.dao;

import com.game.base.WPPost;
import com.game.base.commons.dao.BaseDao;

public interface WPPostDao extends BaseDao<WPPost> {

	void testInsert(String sql);

}
