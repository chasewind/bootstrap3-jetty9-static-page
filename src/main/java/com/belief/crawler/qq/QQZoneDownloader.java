package com.belief.crawler.qq;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.belief.crawler.PageDownLoader;

public class QQZoneDownloader extends PageDownLoader {
	private String cookies = "pgv_pvid=6560434264; qm_username=1078993760; qm_sid=5bc1b91e0d09f85d68bf33f31d91028c,qVlVQY0RONlpNcXgqSUNtVWd3OGIzQ3FIV1duUzNlWEJYRUNvb2E2T1FTd18.; pt2gguin=o1078993760; uin=o1078993760; skey=@edXxeTHc3; ptisp=ctc; RK=vhVKhNi4MY; qzone_check=1078993760_1441264022; ptcz=20e23cf2369fd726186cadb4c2ab478954fd97089c762001d7fa649e91ed440f; Loading=Yes; p_skey=nk97zTLDAT00iyhmrpzVlAWwhIp4YSbvpzGRFYs8dlg_; pt4_token=6DY7ykzoO28AADYLuNmRwg__; qz_screen=1366x768; pgv_info=ssid=s7321407980; QZ_FE_WEBP_SUPPORT=1; cpu_performance_v8=25; rv2=809FB69206A8BE51B5AF718A7D1C8F84BB5774520E7D82DC6D; property20=E081485C850CEC92F1363FB96D8D741055B27A4150164F79D8DA9D6655F59019A95584E5542BB1F6; __Q_w_s__QZN_TodoMsgCnt=1; g_ut=2; 3g_pt_cvdata=hAol1441265304963; qzspeedup=sdch";
	public String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.11 Safari/537.36";
	@Override
	public String GetHtml(String url) {
		String html = null;
		HttpGet request = new HttpGet(url);

		RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).build();

		request.setConfig(requestConfig);
		request.setHeader("Cookie", cookies);
		request.setHeader(HttpHeaders.USER_AGENT, userAgent);
		CloseableHttpResponse response = null;
		try {
			HttpContext httpContext = new BasicHttpContext();
			response = httpclient.execute(request, httpContext);
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
