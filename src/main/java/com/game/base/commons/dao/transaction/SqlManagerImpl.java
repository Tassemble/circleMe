package com.game.base.commons.dao.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

import com.netease.dbsupport.IConnectionManager;
import com.netease.dbsupport.transaction.IDBTransactionManager;
import com.netease.framework.dbsupport.SqlManager;
import com.netease.framework.dbsupport.callback.DBListHandler;
import com.netease.framework.dbsupport.callback.DBObjectHandler;
import com.netease.framework.dbsupport.impl.DBResource;
import com.netease.framework.dbsupport.impl.SqlManagerBase;
import com.netease.framework.stat.RuntimeStatCounter;

public class SqlManagerImpl extends SqlManagerBase implements SqlManager {
	private static final Logger		logger		= Logger.getLogger(SqlManagerImpl.class);
	private IConnectionManager		connectionPool;
	private IDBTransactionManager	transactionManager;
	private RuntimeStatCounter		runtimeStatCounter;
	public static final long		TIME_LIMIT	= 2000L;

	public long allocateRecordId(String tableName) {
		Connection con = getConnection();
		DBResource dbr = new DBResource(con, null, null);
		try {
			Statement stmt =con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Auto_increment FROM information_schema.tables WHERE table_name = '" + tableName + "'");
			rs.next();
			long id = rs.getLong(1);
			rs.close();
			stmt.close();
			return id;
		} catch (Throwable e) {
			logger.error("genID " + tableName, e);
			throw new RuntimeException(e);
		} finally {
			closeDBResource(dbr);
		}
	}
	

	public void closeDBResource(DBResource dbr) {
		List<Throwable> exes = new ArrayList<Throwable>();
		if (dbr != null) {
			if (dbr.getResultSet() != null) {
				try {
					dbr.getResultSet().close();
				} catch (Throwable e) {
					logger.error("close ResultSet", e);
					getRuntimeStatCounter().incrFailCount();
					exes.add(e);
				}
			}

			if (dbr.getStatement() != null) {
				try {
					dbr.getStatement().close();
				} catch (Throwable e) {
					logger.error("close statement", e);
					getRuntimeStatCounter().incrFailCount();
					exes.add(e);
				}
			}

			if ((((getTransactionManager() == null) || (getTransactionManager().getAutoCommit())))
					&& (dbr.getConnection() != null)) {
				try {
					if (logger.isInfoEnabled()) {
						logger.info("close connection!");
					}
					dbr.getConnection().close();
				} catch (Throwable e) {
					logger.error("close conection", e);
					getRuntimeStatCounter().incrFailCount();
					exes.add(e);
				}
			}
		}

		if (exes.size() != 0)
			throw new RuntimeException("close DBResource", (Throwable) exes.get(0));
	}

	public DBResource executeQuery(String sql, List<Object> params) {
		StopWatch watch = new StopWatch();
		watch.start();
		Connection con = getConnection();

		if (logger.isInfoEnabled()) {
			logger.info("query:" + sql + " params:" + params);
		}

		DBResource dbr = null;
		try {
			if (hasParams(params)) {
				PreparedStatement ps = getPreparedStatement(con, sql, params);
				ResultSet rs = ps.executeQuery();
				dbr = new DBResource(con, ps, rs);
			} else {
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				dbr = new DBResource(con, stmt, rs);
			}
		} catch (Throwable e) {
			String msg = "sql:" + sql + ",params:" + params;
			logger.error(msg, e);
			getRuntimeStatCounter().incrFailCount();
			throw new RuntimeException(msg, e);
		}
		watch.stop();
		if (watch.getTime() > 2000L) {
			logger.warn("query:" + sql + ",params:" + params + ",time:" + watch.getTime());

			getRuntimeStatCounter().incrDoneCount();
		}
		return dbr;
	}

	public Connection getConnection() {
		Connection con = null;
		if ((getTransactionManager() == null) || (getTransactionManager().getAutoCommit())) {
			if (logger.isInfoEnabled()) {
				logger.info("open connection with no transaction");
			}
			con = getConnectionPool().getConnection();
		} else {
			con = getTransactionManager().getConnection();
			if (con == null) {
				if (logger.isInfoEnabled()) {
					logger.info("open connection with transaction");
				}
				con = getConnectionPool().getConnection();
				getTransactionManager().setConnection(con);
			}
		}

		if (con == null) {
			logger.error("获得数据库连接失败");
			getRuntimeStatCounter().incrFailCount();
		}

		return con;
	}

	public IConnectionManager getConnectionPool() {
		return this.connectionPool;
	}

	private <T> List<T> getListFromRs(ResultSet rs, DBObjectHandler<T> handler) {
		List<T> objs = new ArrayList<T>();
		try {
			while (rs.next()) {
				T obj = handler.handleResultSet(rs);
				if (obj != null)
					objs.add(obj);
			}
		} catch (Throwable e) {
			logger.error(e);
			getRuntimeStatCounter().incrFailCount();
			throw new RuntimeException(e);
		}
		return objs;
	}

	private PreparedStatement getPreparedStatement(Connection con, String sql, List<Object> params) {
		PreparedStatement stat = null;
		int i;
		Iterator<Object> i$;
		try {
			stat = con.prepareStatement(sql);
			i = 1;
			for (i$ = params.iterator(); i$.hasNext();) {
				Object param = i$.next();
				stat.setObject(i++, param);
			}
		} catch (Throwable e) {
			String msg = "sql:" + sql + ",params:" + params;
			logger.error(msg, e);
			getRuntimeStatCounter().incrFailCount();
			throw new RuntimeException(msg, e);
		}
		return stat;
	}

	public IDBTransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	private boolean hasParams(List<Object> params) {
		return ((params != null) && (params.size() != 0));
	}

	public Long queryCount(String sql, List<Object> params) {
		DBResource dbr = null;
		try {
			dbr = executeQuery(sql, params);
			ResultSet rs = dbr.getResultSet();
			try {
				if (rs.next()) {
					Long localLong = Long.valueOf(rs.getLong(1));

					closeDBResource(dbr);
					return localLong;
				}
			} catch (Throwable e) {
				String msg = "sql:" + sql + ",params:" + params;
				logger.error(msg, (Throwable) e);
				getRuntimeStatCounter().incrFailCount();
				throw new RuntimeException(msg, (Throwable) e);
			}
			return Long.valueOf(0L);
		} finally {
			closeDBResource(dbr);
		}
	}

	public <T> T queryObject(String sql, DBObjectHandler<T> handler, List<Object> params) {
		DBResource srs = null;
		try {
			srs = executeQuery(sql, params);
			if (null == srs) {
				logger.error("error in query object, get dbresource null, sql:" + sql + " params:" + params);

				getRuntimeStatCounter().incrFailCount();
				return null;
			}
			ResultSet rs = srs.getResultSet();
			try {
				if (rs.next()) {
					T localObject2 = handler.handleResultSet(rs);

					closeDBResource(srs);

					return localObject2;
				}
			} catch (Throwable e) {
				String msg = "sql:" + sql + ",params:" + params;
				logger.error(msg);
				getRuntimeStatCounter().incrFailCount();
				throw new RuntimeException(msg, e);
			}
			return null;
		} finally {
			closeDBResource(srs);
		}
	}

	public Long queryObjectId(String sql, List<Object> params) {
		DBResource dbr = null;
		try {
			dbr = executeQuery(sql, params);
			ResultSet rs = dbr.getResultSet();
			try {
				if (rs.next()) {
					Long localLong = Long.valueOf(rs.getLong(1));

					closeDBResource(dbr);
					return localLong;
				}
			} catch (Throwable e) {
				String msg = "sql:" + sql + ",params:" + params;
				logger.error(msg);
				getRuntimeStatCounter().incrFailCount();
				throw new RuntimeException(msg, (Throwable) e);
			}
			return -1L;
		} finally {
			closeDBResource(dbr);
		}
	}

	public Long[] queryObjectIds(String sql, List<Object> params) {
		DBResource dbr = null;
		try {
			dbr = executeQuery(sql, params);
			ResultSet rs = dbr.getResultSet();
			List<Long> ids = new ArrayList<Long>();
			try {
				while (rs.next()) {
					ids.add(Long.valueOf(rs.getLong(1)));
				}
			} catch (Throwable e) {
				String msg = "sql:" + sql + ",params:" + params;
				logger.error(msg);
				getRuntimeStatCounter().incrFailCount();
				throw new RuntimeException(msg, e);
			}

			return (Long[]) ids.toArray(new Long[0]);
		} finally {
			closeDBResource(dbr);
		}
	}

	public void setConnectionPool(IConnectionManager connectionPool) {
		this.connectionPool = connectionPool;
	}

	public void setTransactionManager(IDBTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public int updateRecords(String sql, List<Object> params) {
		StopWatch watch = new StopWatch();
		watch.start();
		Connection con = getConnection();

		if (logger.isInfoEnabled()) {
			logger.info("updateSql: " + sql + " params: " + params);
		}

		if (hasParams(params)) {
			PreparedStatement ps = getPreparedStatement(con, sql, params);
			try {
				int i = ps.executeUpdate();

				return i;
			} catch (Throwable e) {
				String msg = "sql:" + sql;

				throw new RuntimeException(msg, e);
			} finally {
				closeDBResource(new DBResource(con, ps, null));
				watch.stop();
				if (watch.getTime() > 2000L) {
					logger.warn("query:" + sql + ",params:" + params + ",time:" + watch.getTime());

					getRuntimeStatCounter().incrDoneCount();
				}
			}
		} else {
			Statement stmt = null;
			try {
				stmt = con.createStatement();
				int j = stmt.executeUpdate(sql);

				return j;
			} catch (Throwable e) {
				String msg = "sql:" + sql;

				throw new RuntimeException(msg, e);
			} finally {
				closeDBResource(new DBResource(con, stmt, null));
				watch.stop();
				if (watch.getTime() > 2000L) {
					logger.warn("query:" + sql + ",params:" + params + ",time:" + watch.getTime());

					getRuntimeStatCounter().incrDoneCount();
				}
			}
		}
	}

	public Set<String> getColumns(String tableName) {
		String sql = "SELECT * FROM " + tableName + " LIMIT 0";
		DBResource srs = null;
		Set<String> columns = new HashSet<String>();
		try {
			srs = executeQuery(sql, new Object[0]);
			ResultSet rs = srs.getResultSet();
			ResultSetMetaData metaData = rs.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); ++i) {
				columns.add(metaData.getColumnName(i).toLowerCase());
			}
			return columns;
		} catch (Throwable e) {
			String msg = "sql:" + sql;

			throw new RuntimeException(msg, e);
		} finally {
			closeDBResource(srs);
		}
	}

	public String querySingleColInOneRecord(String sql, List<Object> params) {
		DBResource dbr = null;
		try {
			dbr = executeQuery(sql, params);
			ResultSet rs = dbr.getResultSet();
			try {
				if (rs.next()) {
					String str1 = rs.getString(1);

					closeDBResource(dbr);
					return str1;
				}
			} catch (Throwable e) {
				String msg = "sql:" + sql + ",params:" + params;
				logger.error(msg);
				getRuntimeStatCounter().incrFailCount();
				throw new RuntimeException(msg, e);
			}

			return null;
		} finally {
			closeDBResource(dbr);
		}
	}

	@Override
	public String querySingleColInOneRecord(String _sql, Object... _paras) {
		return querySingleColInOneRecord(_sql, Arrays.asList(_paras));
	}

	/** @deprecated */
	public boolean addRecord(String _sql, List<Object> _paras) {
		return (updateRecords(_sql, _paras) > 0);
	}

	/** @deprecated */
	public boolean addRecord(String _sql, Object... _paras) {
		return (updateRecords(_sql, _paras) > 0);
	}

	public boolean existRecord(String sql, List<Object> params) {
		DBResource dbr = null;
		try {
			dbr = executeQuery(sql, params);
			ResultSet rs = dbr.getResultSet();
			try {
				boolean bool = rs.next();

				closeDBResource(dbr);
				return bool;
			} catch (Throwable e) {
				String msg = "sql:" + sql + ",params:" + params;
				logger.error(msg);
				getRuntimeStatCounter().incrFailCount();
				throw new RuntimeException(msg, e);
			}
		} finally {
			closeDBResource(dbr);
		}
	}

	@Deprecated
	public boolean updateRecord(String sql, List<Object> params) {
		return (updateRecords(sql, params) > 0);
	}

	@Deprecated
	public boolean updateRecord(String sql, Object... params) {
		return updateRecord(sql, Arrays.asList(params));
	}

	public <T> List<T> queryList(String sql, DBObjectHandler<T> handler, DBListHandler<T> listHandler,
			List<Object> params) {
		DBResource dbr = null;
		try {
			dbr = executeQuery(sql, params);
			List<T> objs = getListFromRs(dbr.getResultSet(), handler);
			if (listHandler != null) {
				listHandler.handDBList(objs);
			}

			return objs;
		} finally {
			closeDBResource(dbr);
		}
	}

	public RuntimeStatCounter getRuntimeStatCounter() {
		return this.runtimeStatCounter;
	}

	public void setRuntimeStatCounter(RuntimeStatCounter runtimeStatCounter) {
		this.runtimeStatCounter = runtimeStatCounter;
	}

	public DBResource executeQuery(String sql, Object... params) {
		return executeQuery(sql, Arrays.asList(params));
	}
}
