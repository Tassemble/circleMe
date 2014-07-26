package com.game.base.commons.dao.transaction;

import com.netease.dbsupport.exception.DBSupportRuntimeException;
import com.netease.dbsupport.transaction.IDBTransactionManager;
import java.sql.Connection;
import java.sql.SQLException;

public class MysqlTransactionManagerAdapter implements IDBTransactionManager {
	private ThreadLocal<Boolean>	m_autoCommit	= new ThreadLocal();

	private ThreadLocal<Connection>	m_connection	= new ThreadLocal();

	public void clear() {
		Connection con = getConnection();
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				throw new DBSupportRuntimeException(e);
			}
			setConnection(null);
		}
		setAutoCommit(true);
	}

	public void commit() {
		try {
			if (getConnection() == null)
				return;
			getConnection().commit();
		} catch (SQLException e) {
			throw new DBSupportRuntimeException(e);
		} finally {
			clear();
		}
	}

	public boolean getAutoCommit() {
		if (this.m_autoCommit.get() == null) {
			return true;
		}
		return ((Boolean) this.m_autoCommit.get()).booleanValue();
	}

	public Connection getConnection() {
		return ((Connection) this.m_connection.get());
	}

	public void rollback() {
		try {
			if (getConnection() == null)
				return;
			getConnection().rollback();
		} catch (SQLException e) {
			throw new DBSupportRuntimeException(e);
		} finally {
			clear();
		}
	}

	public void setAutoCommit(boolean _isAutoCommit) {
		this.m_autoCommit.set(Boolean.valueOf(_isAutoCommit));
		Connection con = getConnection();
		if (con == null)
			return;
		try {
			con.setAutoCommit(_isAutoCommit);
		} catch (SQLException e) {
			throw new DBSupportRuntimeException(e);
		}
	}

	public void setConnection(Connection _connection) {
		this.m_connection.set(_connection);
		if (_connection == null)
			return;
		try {
			_connection.setAutoCommit(getAutoCommit());
		} catch (SQLException e) {
			throw new DBSupportRuntimeException(e);
		}
	}

	public void releaseConnection() {
		Connection con = getConnection();
		if (con == null)
			return;
		try {
			con.close();
		} catch (SQLException e) {
			throw new DBSupportRuntimeException(e);
		}
		setConnection(null);
	}
}
