package com.game.base.service.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.dbsupport.transaction.IDBTransactionManager;
import com.netease.memcache.transaction.IMemcacheTransactionManager;

public class EduTransactionMemcachedAdvice implements MethodInterceptor {

	private static final Log log = LogFactory
			.getLog(EduTransactionMemcachedAdvice.class);
	private IDBTransactionManager idbTransactionManager;

	private IMemcacheTransactionManager imemcacheTransactionManager;

	public Object invoke(MethodInvocation arg0) throws Throwable {

		Object result = null;
		if (getIdbTransactionManager().getAutoCommit()) {

			if (log.isDebugEnabled()) {
				log.debug(arg0.getMethod().getName()
						+ " begin to in transaction...");
			}

			try {
				getIdbTransactionManager().setAutoCommit(false);
				getImemcacheTransactionManager().setAutoCommit(false);
				result = arg0.proceed();
				getIdbTransactionManager().commit();
				getImemcacheTransactionManager().commit();
				getIdbTransactionManager().setAutoCommit(true);
				getImemcacheTransactionManager().setAutoCommit(true);
				if (log.isDebugEnabled()) {
					log.debug(arg0.getMethod().getName()
							+ " 'transaction commited.");
				}
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug(arg0.getMethod().getName()
							+ " begin to rollback...");
				}
				getIdbTransactionManager().rollback();
				getIdbTransactionManager().setAutoCommit(true);
				getImemcacheTransactionManager().rollback();
				getImemcacheTransactionManager().setAutoCommit(true);
				if (log.isDebugEnabled()) {
					log.debug(arg0.getMethod().getName()
							+ " 'transaction  rollbacked.");
				}
				throw e;
			}

		} else {
			result = arg0.proceed();
		}

		return result;
	}

	public IMemcacheTransactionManager getImemcacheTransactionManager() {
		return imemcacheTransactionManager;
	}

	public void setImemcacheTransactionManager(
			IMemcacheTransactionManager imemcacheTransactionManager) {
		this.imemcacheTransactionManager = imemcacheTransactionManager;
	}

	public IDBTransactionManager getIdbTransactionManager() {
		return idbTransactionManager;
	}

	public void setIdbTransactionManager(
			IDBTransactionManager idbTransactionManager) {
		this.idbTransactionManager = idbTransactionManager;
	}

}
