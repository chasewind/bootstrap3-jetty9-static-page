package com.belief.crawler;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.belief.utils.JsonUtils;

/**
 * 新浪微博爬取
 * 
 * @author yudongwei
 *
 */
public class SinaTask extends TaskHandlerCenter<SinaTargetInfo, SinaResultInfo> {

	/** 需要解析的脚本标志性开头 */
	private static final String S_SCRIPT_START_FM = "FM.view(";
	private static final String S_NS = "ns";
	/** 用户信息认证等级相关 */
	private static final String S_NS_TITLE = "pl.header.head.index";

	private static final String S_DOMID_KEY = "domid";
	private static final String S_DOMID_VALUE = "plc_main";
	private static final String S_DOMID_VALUE_FIND = "Pl_Core_T8CustomTriColumn__";

	private static final String CONTENT_HTML = "html";
	private static final String PROFILE_VERIFY_BLUE = "W_icon icon_pf_approve_co";
	private static final String PROFILE_VERIFY_YELLOW = "W_icon icon_pf_approve";
	private static final String PROFILE_VERIFY_CLUB = "W_icon icon_pf_club";

	@Override
	protected void init() {
	}

	@Override
	protected List<SinaTargetInfo> getAllTarget() {
		return SinaTargetFactory.getAllTarget();
	}

	@Override
	protected SinaResultInfo fetchData(SinaTargetInfo target) {
		String url = target.getUrl();
		if (StringUtils.isNoneBlank(url)) {
			String htmlContent = downLoader.GetHtml(url);
			SinaResultInfo info = analyseHtml(url, htmlContent);
			return info;
		}
		return null;
	}

	@Override
	protected void process(SinaResultInfo result) {

		// TODO 对基本结果进行分析过滤，持久化一类操作
	}

	private SinaResultInfo analyseHtml(String url, String htmlContent) {

		Document doc = Jsoup.parse(htmlContent);
		System.out.println(doc.title());
		Elements scripts = doc.select("script");
		for (Element script : scripts) {
			String html = script.html();
			// 如果不是以指定元素开头的，直接跳过
			if (!html.startsWith(S_SCRIPT_START_FM)) {
				continue;
			}
			// 截取解析为JSON格式数据
			String json = html.substring(S_SCRIPT_START_FM.length(), html.length() - 1);
			@SuppressWarnings("unchecked")
			Map<String, Object> map = JsonUtils.json2Object(json, Map.class);
			Object value = null;

			// 如果获取的内容为空，跳过
			if (map == null) {
				continue;
			}
			value = map.get(S_NS);
			// 命名空间为空
			if (value == null || StringUtils.isBlank(value.toString())) {
				handleNullNameSpace(map);
			} else {
				handleNotNullNameSpace(map, value);
			}

		}
		return null;
	}

	private void handleNotNullNameSpace(Map<String, Object> map, Object value) {
		String valueStr = value.toString();
		String realContent = null;
		if (StringUtils.equalsIgnoreCase(S_NS_TITLE, valueStr)) {
			realContent = (String) map.get(CONTENT_HTML);
			// System.out.println(Jsoup.parse(realContent));
			if (realContent.contains(PROFILE_VERIFY_BLUE)) {
				System.out.println("认证：蓝色级别");
			} else if (realContent.contains(PROFILE_VERIFY_YELLOW)) {
				System.out.println("认证：黄色级别");
			} else if (realContent.contains(PROFILE_VERIFY_CLUB)) {
				System.out.println("认证：club级别");
			} else {
				System.out.println("认证：未认证");
			}
		}

	}

	private void handleNullNameSpace(Map<String, Object> map) {
		Object value = map.get(S_DOMID_KEY);
		if (value == null) {
		}
		String valueStr = value.toString();
		String realContent = null;

		// if (StringUtils.equalsIgnoreCase(valueStr, S_DOMID_VALUE)) {
		// System.out.println("==============");
		// realContent = (String) map.get(CONTENT_HTML);
		// System.out.println(realContent);
		// }

		String tmp = (String) map.get(CONTENT_HTML);
		if (tmp != null && tmp.contains("关注")) {
			System.out.println(tmp);
		}
		if (valueStr.startsWith(S_DOMID_VALUE_FIND)) {
			System.out.println("==============" + valueStr);
			realContent = (String) map.get(CONTENT_HTML);
			System.out.println(realContent);
		}

	}
}
