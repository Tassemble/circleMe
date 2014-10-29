package com.game.dao;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/*
 * @author hzfjd@
 * @date 2012-10-17
 */
@TestExecutionListeners({ EduDdbTestExecutionListener.class })
public class JunitTransactionSpringContextTest extends AbstractJUnit4SpringContextTests {

}
