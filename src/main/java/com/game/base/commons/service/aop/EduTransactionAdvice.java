package com.game.base.commons.service.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.dbsupport.transaction.IDBTransactionManager;

/*
 * @author hzfjd@
 * @date 2012-5-3
 */
public class EduTransactionAdvice implements MethodInterceptor {

    private static final Log      log          = LogFactory.getLog(EduTransactionAdvice.class);
    private IDBTransactionManager idbTransactionManager;
    private boolean               autoRollBack = false;

    public Object invoke(MethodInvocation arg0) throws Throwable {
        Object result = null;

        if (getIdbTransactionManager().getAutoCommit()) {

            if (log.isDebugEnabled()) {
                log.debug(arg0.getMethod().getName() + " begin to in transaction...");
            }

            try {
                getIdbTransactionManager().setAutoCommit(false);
                result = arg0.proceed();
                if (autoRollBack) {
                    getIdbTransactionManager().rollback();
                } else {
                    getIdbTransactionManager().commit();
                }
                getIdbTransactionManager().setAutoCommit(true);
                if (log.isDebugEnabled()) {
                    log.debug(arg0.getMethod().getName() + " 'transaction commited.");
                }
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug(arg0.getMethod().getName() + " begin to rollback...");
                }
                getIdbTransactionManager().rollback();
                getIdbTransactionManager().setAutoCommit(true);
                if (log.isDebugEnabled()) {
                    log.debug(arg0.getMethod().getName() + " 'transaction  rollbacked.");
                }
                throw e;
            }

        } else {
            result = arg0.proceed();
        }

        return result;
    }

    public IDBTransactionManager getIdbTransactionManager() {
        return idbTransactionManager;
    }

    public void setIdbTransactionManager(IDBTransactionManager idbTransactionManager) {
        this.idbTransactionManager = idbTransactionManager;
    }

    public boolean isAutoRollBack() {
        return autoRollBack;
    }

    public void setAutoRollBack(boolean autoRollBack) {
        this.autoRollBack = autoRollBack;
    }

}
