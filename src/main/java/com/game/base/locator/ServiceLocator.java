package com.game.base.locator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/***
 * 不要在类的静态域中引用ServiceLocator.getContext()，除非当前class不会在spring中托管（也无法保证当前class为lazy_initialized_bean），否则会引起应用启动的循环依赖。
 * 
 * @author owner
 */

public class ServiceLocator {

    private static ClassPathXmlApplicationContext ctx = null;

    private static Log                            log = LogFactory.getLog(ServiceLocator.class);

    static {
        try {
            ctx = new ClassPathXmlApplicationContext(getConfigFiles());
        } catch (Exception e) {
            log.error("init ClassPathXmlApplicationContext error !", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取配置文件列表@return
     */
    private static String[] getConfigFiles() {
        String[] configFiles = { "classpath:/applicationContext-aop-base.xml", "classpath:/applicationContext-dao.xml",
                "classpath:/applicationContext-dao-base.xml", "classpath:/applicationContext-service.xml",
                "classpath:/applicationContext-bo.xml", "classpath:/applicationContext-remote.xml",
                "classpath:/applicationContext-midware.xml", "classpath:/applicationContext-bo.xml",
                "classpath:/applicationContext-jms.xml", "classpath:/biz/applicationContext-framework-aop.xml",
                "classpath:/biz/applicationContext-framework-dao-base.xml",
                "classpath:/biz/applicationContext-message-dao.xml",
                "classpath:/biz/applicationContext-message-service.xml",
                "classpath:/biz/applicationContext-mail-dao.xml", "classpath:/biz/applicationContext-mail-service.xml" };
        return configFiles;
    }

    /***
     * 不要在类的静态域中引用ServiceLocator.getContext()，除非当前class不会在spring中托管（也无法保证当前class为lazy_initialized_bean），否则会引起应用启动的循环依赖。
     * 
     * @author owner
     */
    public static ApplicationContext getContext() {
        return ctx;
    }
}
