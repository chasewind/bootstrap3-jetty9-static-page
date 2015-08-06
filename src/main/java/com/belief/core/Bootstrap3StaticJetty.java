package com.belief.core;

import java.util.Arrays;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 静态页面访问，保证项目的正常运行，该类需放入main，放入test会503错误
 * 
 * @author 于东伟
 *
 */
public class Bootstrap3StaticJetty {
	private static Logger logger = LoggerFactory.getLogger(Bootstrap3StaticJetty.class);

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		WebAppContext webapp = new WebAppContext();
		webapp.setResourceBase("src/main/webapp");
		webapp.setContextPath("/");
		webapp.setOverrideDescriptors(Arrays.asList(new String[] { Bootstrap3StaticJetty.class.getResource("/") + "web_override_development.xml" }));
		server.setHandler(webapp);
		logger.info("start......");
		server.start();
		logger.debug("join thread pool......");
		server.join();
	}
}
