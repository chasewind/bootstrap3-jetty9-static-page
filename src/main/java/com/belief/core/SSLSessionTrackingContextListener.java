package com.belief.core;

import java.util.EnumSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.SessionTrackingMode;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class SSLSessionTrackingContextListener implements ServletContextListener {
    private static Logger logger = LoggerFactory.getLogger(SSLSessionTrackingContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // request.getAttribute("javax.servlet.request.ssl_session")
        logger.error("contextInitialized......");
        ServletContext context = sce.getServletContext();
        EnumSet<SessionTrackingMode> modes = EnumSet.of(SessionTrackingMode.SSL);
        context.setSessionTrackingModes(modes);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.error("contextDestroyed......");
    }

}
