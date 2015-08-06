package com.belief.core;

import java.util.Arrays;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLJettyServer {
	private static Logger logger = LoggerFactory.getLogger(Bootstrap3StaticJetty.class);

	public static void main(String[] args) throws Exception {
		Server server = new Server();
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(8443);
		http_config.setOutputBufferSize(32768);

		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath("./src/main/resources/server.keystore");
		sslContextFactory.setKeyStorePassword("chaseecho");
		sslContextFactory.setTrustStorePath("./src/main/resources/server.keystore");
		sslContextFactory.setTrustStorePassword("chaseecho");

		HttpConfiguration https_config = new HttpConfiguration(http_config);
		https_config.addCustomizer(new SecureRequestCustomizer());

		ServerConnector https = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https_config));
		https.setPort(8443);
		https.setIdleTimeout(500000);

		server.setConnectors(new Connector[] { https });

		WebAppContext webapp = new WebAppContext();
		webapp.setResourceBase("src/main/webapp");
		webapp.setContextPath("/");
		webapp.setOverrideDescriptors(Arrays.asList(new String[] { SSLJettyServer.class.getResource("/") + "web_override_development.xml" }));
		server.setHandler(webapp);
		logger.info("start......");
		server.start();
		logger.debug("join thread pool......");
		server.join();
	}
}
