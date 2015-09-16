package com.belief.crawler;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 爬虫之页面下载器抽象
 * 
 * @author yudongwei
 *
 */
public abstract class PageDownLoader {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	public CloseableHttpClient httpclient;
	public static RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).setConnectionRequestTimeout(10000).build();

	public RequestConfig config;

	public PageDownLoader() {
		httpclient = HttpClients.createDefault();
		// 设置代理
		HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
		config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(5000).setSocketTimeout(100000).build();
	}

	public abstract String GetHtml(String url);
}
