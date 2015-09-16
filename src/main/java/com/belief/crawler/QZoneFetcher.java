package com.belief.crawler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.belief.utils.JsonUtils;

public class QZoneFetcher {
    private static CloseableHttpClient httpclient = HttpClients.custom().build();
    private static String getplcount =
            "http://snsapp.qzone.qq.com/cgi-bin/qzonenext/getplcount.cgi?hostuin=%s";
    static String userAgent =
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; InfoPath.2)";

    static String cookies = "uin=o1078993760; skey=@M8HcuZwTh";
    private static final Pattern p_json = Pattern.compile("^_[a-zA-Z]+\\(([\\S\\s]*?)\\);$",
            Pattern.CASE_INSENSITIVE);
	private static int qq_len = 5;

	// 10 0000
    public static void main(String[] args) throws IOException {
        long min = getMinCode();
        long max = getMaxCode();
		// 100000 per thread
		long count = (max - min) / 100000;
        for (long i = 0; i < count; i++) {
			long start = min + i * 100000;
			// System.out.println(start);
			new Thread(new FetchThread(start)).start();
        }
        // getHtmlContent("1078993760");
    }

    public static long getMinCode() {
        return (long) Math.pow(10, qq_len);
    }

    public static long getMaxCode() {
        return (long) Math.pow(10, (qq_len + 1));
    }

    public static void getHtmlContent(String uid) {
        try {
            String url = String.format(getplcount, uid);
            HttpGet request = new HttpGet(url);
            RequestConfig requestConfig =
                    RequestConfig.copy(RequestConfig.custom().build()).build();
            request.addHeader("User-Agent", userAgent);
            request.addHeader("Cookie", cookies);
            request.setConfig(requestConfig);
            HttpContext httpContext = new BasicHttpContext();
            CloseableHttpResponse response = httpclient.execute(request, httpContext);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                // System.out.println(content);
                Matcher m = p_json.matcher(content);
                if (m.find()) {
                    String json = m.group(1);
                    CounterInfo counterInfo = JsonUtils.json2Object(json, CounterInfo.class);
                    if (counterInfo != null && counterInfo.count != null) {
						if (counterInfo.count.SS > 100 && counterInfo.count.RZ > 0) {
							System.out.println(uid + " " + counterInfo.count.SS + " " + counterInfo.count.RZ);
						}
                    }
                }
            }
        } catch (IOException e) {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e1) {
            }
        }
    }
}


class FetchThread implements Runnable {
    long start;

    public FetchThread(long start) {
        this.start = start;
    }

    @Override
    public void run() {
		long end = start + 100000;
        for (long i = start; i < end; i++) {
            QZoneFetcher.getHtmlContent(i + "");
        }
    }

}
