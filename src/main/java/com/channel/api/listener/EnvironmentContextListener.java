package com.channel.api.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class EnvironmentContextListener implements ServletContextListener {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String CAT_HOME = this.getClass().getResource("/").getPath() + "META-INF/";
        System.setProperty("CAT_HOME", CAT_HOME);
        String CAT_HOME_TEMP;
        if (StringUtils.isEmpty(System.getProperty("catalina.base"))) {
            CAT_HOME_TEMP = "/data/applogs/cat";
        } else {
            CAT_HOME_TEMP = System.getProperty("catalina.base") + "/logs/";
        }

        System.setProperty("CAT_HOME_TEMP", CAT_HOME_TEMP);
        logger.info("init cat_home path:{},cat_home_temp path:{}", CAT_HOME, CAT_HOME_TEMP);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
