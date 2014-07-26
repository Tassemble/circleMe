package com.game.base.commons.service.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;

import com.netease.dbsupport.transaction.IDBTransactionManager;

/*
 * @author hzfjd@corp.netease.com
 * @date 2012-9-20
 */
@Aspect
public class TransactionAspect implements Ordered {

    private static final Log      log          = LogFactory.getLog(EduTransactionAdvice.class);
    private IDBTransactionManager idbTransactionManager;
    private boolean               autoRollBack = false;
    private int                   order        = LOWEST_PRECEDENCE;
    //com.wenxiong.blog.service.impl
    //com.wenxiong.blog.service.impl

    
    //WPPostServiceImpl
    //com.wenxiong.blog.commons.service.impl.BaseServiceImpl.add
    @Pointcut("execution(* com.game..*.service..*.add*(..)) || execution(* com.game..*.service..*.insert*(..))||execution(* com.game..*.service..*.create*(..))||execution(* com.game..*.service..*.update*(..))||execution(* com.game..*.service..*.modify*(..))||execution(* com.game..*.service..*.delete*(..))||execution(* com.game..*.service..*.remove*(..))")
    public void transaction() {
    }

    @Around("transaction()")
    public Object aroundTransaction(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = null;
        
        log.info("start transaction!!");

        if (getIdbTransactionManager().getAutoCommit()) {

            if (log.isDebugEnabled()) {
                log.debug(joinPoint.getSignature().getName() + " begin to in transaction...");
            }

            try {
                getIdbTransactionManager().setAutoCommit(false);
                result = joinPoint.proceed();
                if (autoRollBack) {
                    getIdbTransactionManager().rollback();
                } else {
                    getIdbTransactionManager().commit();
                }
                getIdbTransactionManager().setAutoCommit(true);
                if (log.isDebugEnabled()) {
                    log.debug(joinPoint.getSignature().getName() + " 'transaction commited.");
                }
            } catch (Exception e) {
            	log.info("rollback!!");
                if (log.isDebugEnabled()) {
                    log.debug(joinPoint.getSignature().getName() + " begin to rollback...");
                }
                getIdbTransactionManager().rollback();
                getIdbTransactionManager().setAutoCommit(true);
                if (log.isDebugEnabled()) {
                    log.debug(joinPoint.getSignature().getName() + " 'transaction  rollbacked.");
                }
                throw e;
            }

        } else {
            result = joinPoint.proceed();
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

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
