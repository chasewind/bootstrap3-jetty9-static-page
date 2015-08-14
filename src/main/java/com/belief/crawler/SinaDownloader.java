package com.belief.crawler;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class SinaDownloader extends PageDownLoader {

	// 构建访问所需的必要条件
	public String cookies = "SINAGLOBAL=7220685752108.693.1438589613998; YF-Page-G0=da70d782c04c64a28bb478593d9dc2d3; SUHB=0WGNKxrhqdl5vo; _s_tentry=login.sina.com.cn; Apache=9466523104347.29.1439516256892; ULV=1439516256906:12:12:11:9466523104347.29.1439516256892:1439516023058; YF-V5-G0=a906819fa00f96cf7912e89aa1628751; myuid=2358831780; YF-Ugrow-G0=ea90f703b7694b74b62d38420b5273df; SUB=_2AkMikcmVdcNhrAFSnfAdz2rlZI1Wywjzsd_4M0naF1NCbTccg1NnqCZqthF-XYyjjyy6sNDjjSVws2_tW86peZ1_fr-lWd0y; SUBP=0033WrSXqPxfM72wWs9jqgMF55529P9D9WFJ_lx.zIygi66pwhuqh0ch5JpV2K27S0n41KeNSoW5MP2Vqcv_; login_sid_t=c4b027dd3b44ba519fda32cb44cd6d21; UOR=zt.chuanke.com,widget.weibo.com,login.sina.com.cn";
	public String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.11 Safari/537.36";

	@Override
	public String GetHtml(String url) {
		String html = null;
		HttpGet request = new HttpGet(url);
		request.setConfig(config);
		request.setHeader("Cookie", cookies);
		request.setHeader(HttpHeaders.USER_AGENT, userAgent);

		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				html = EntityUtils.toString(entity);
			}
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return html;
	}

}
