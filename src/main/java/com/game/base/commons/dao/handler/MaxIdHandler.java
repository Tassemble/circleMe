package com.game.base.commons.dao.handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.netease.framework.dbsupport.callback.DBObjectHandler;

public class MaxIdHandler implements DBObjectHandler<Long> {

    private static MaxIdHandler s_instance = new MaxIdHandler();

    public static MaxIdHandler getInstance() {
        return s_instance;
    }

    @Override
    public Long handleResultSet(ResultSet arg0) throws SQLException {
        return arg0.getLong("max(id)");
    }

}
