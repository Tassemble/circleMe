package com.game.dao;

import java.lang.reflect.Method;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.game.base.commons.service.aop.EduTransactionAdvice;
import com.netease.dbsupport.transaction.IDBTransactionManager;

/*
 * @author hzfjd@corp.netease.com
 * @date 2012-5-3
 */
public class EduDdbTestExecutionListener extends AbstractTestExecutionListener {

    private EduTransactionAdvice  eduTransactionAdvice;

    private IDBTransactionManager dbTransactionManager;

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        // TODO Auto-generated method stub


        if (dbTransactionManager == null) {
            dbTransactionManager = (IDBTransactionManager) testContext.getApplicationContext().getBean("mysqlTransactionManagerAdapter");
        }

    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        // TODO Auto-generated method stub

        super.prepareTestInstance(testContext);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        final Method testMethod = testContext.getTestMethod();
        Rollback rb = testMethod.getAnnotation(Rollback.class);

        if (rb != null && rb.value()) {
            dbTransactionManager.setAutoCommit(false);
        }

    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        final Method testMethod = testContext.getTestMethod();
        Rollback rb = testMethod.getAnnotation(Rollback.class);

        if (rb != null && rb.value()) {
            dbTransactionManager.rollback();
            dbTransactionManager.setAutoCommit(true);
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
    }

}
