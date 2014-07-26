package com.game.base.commons.dao.utils;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.util.Assert;

import com.netease.dbsupport.impl.ConnectionManagerDDBImpl;

/**
 * hzfjd@corp.netease.com
 * 
 * @author owner
 * 
 */
public class NeteaseDataSourceAdapter extends ConnectionManagerDDBImpl
		implements DataSource {

	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException("getLogWriter");
	}

	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public void setLogWriter(PrintWriter arg0) throws SQLException {
		throw new UnsupportedOperationException("getLogWriter");

	}

	public void setLoginTimeout(int arg0) throws SQLException {
		throw new UnsupportedOperationException("getLogWriter");

	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return DataSource.class.equals(iface);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		Assert.notNull(iface, "Interface argument must not be null");
		if (!DataSource.class.equals(iface)) {
			throw new SQLException(
					"DataSource of type ["
							+ getClass().getName()
							+ "] can only be unwrapped as [javax.sql.DataSource], not as ["
							+ iface.getName());
		}
		return (T) this;
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		return getConnection();
	}

}
