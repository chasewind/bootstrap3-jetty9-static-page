package com.belief.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 新浪微博爬取
 * 
 * @author yudongwei
 *
 */
public class SinaTask extends TaskHandlerCenter<SinaTargetInfo, SinaResultInfo> {

	private List<SinaTargetInfo> nextPageList = new ArrayList<SinaTargetInfo>();
	public static String BASE_PATH = System.getProperty("user.dir");
	public static String BASE_URL = "http://weibo.com";
	/** 需要解析的脚本标志性开头 */
	private static final String S_SCRIPT_START_FM = "FM.view(";
	private static final String S_NS = "ns";
	/** 用户信息认证等级相关 */
	private static final String S_NS_TITLE = "pl.header.head.index";
	private static final String S_NS_LEVEL = "pl.content.homeFeed.index";
	private static final String S_NS_RELATION = "pl.content.miniTab.index";

	private static final String S_DOMID_KEY = "domid";
	private static final String S_DOMID_BASE_FIND = "Pl_Core_T8CustomTriColumn__";
	private static final String S_DOMID_LEVEL_FIND = "Pl_Core_UserInfo__";
	private static final String S_DOMID_ARTICLE_FIND = "Pl_Official_MyProfileFeed__";
	private static final String S_DOMID_USERGRID_FIND = "Pl_Core_UserGrid__";
	//

	private static final String TAG_PARENT = "ul li";
	private static final String TAG_LEVEL = "a span";
	private static final String TAG_PLACE = "ficon_cd_place";
	private static final String TAG_INFO = "ficon_pinfo";
	private static final String TAG_TAG = "ficon_cd_coupon";
	private static final String TAG_BIRTH = "ficon_constellation";

	private static final String CONTENT_HTML = "html";

	private static final String PROFILE_SEX_FEMALE = "i[class=W_icon icon_pf_female]";
	private static final String PROFILE_VERIFY_BLUE = "em[class=W_icon icon_pf_approve_co]";
	private static final String PROFILE_VERIFY_YELLOW = "em[class=W_icon icon_pf_approve]";
	private static final String PROFILE_VERIFY_CLUB = "em[class=W_icon icon_pf_club]";

	private static final String ELEMENT_STRONG = "strong";

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
	protected List<SinaTargetInfo> getNextPageTargets() {
		return nextPageList;
	}

	@Override
	protected int addTargetToNextPage(SinaTargetInfo target) {
		getNextPageTargets().add(target);
		return 0;
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
			String json = html.substring((S_SCRIPT_START_FM.length() + 1), html.length() - 3);

			Map<String, String> map = new HashMap<String, String>();
			try {
				convertToMap(json, map);
			} catch (IOException e) {
				e.printStackTrace();
			}

			String value = map.get(S_NS);
			// 命名空间为空
			if (StringUtils.isBlank(value)) {
				handleNullNameSpace(map);
			} else {
				handleNotNullNameSpace(map, value);
			}

		}

		return null;
	}

	private void convertToMap(String json, Map<String, String> map) throws IOException {
		// 以引号和逗号作为分隔符
		String[] array = json.split(",\"");
		int len = array.length;
		for (int i = 1; i < len; i++) {
			array[i] = "\"" + array[i];
		}
		for (String str : array) {
			if (StringUtils.isBlank(str)) {
				continue;
			}
			int idx = str.indexOf("\":");
			String key = str.substring(0, ++idx);
			String value = str.substring(++idx);

			if (StringUtils.isBlank(key)) {
				continue;
			}
			// 去掉括号
			key = key.substring(1, key.length() - 1);
			if (StringUtils.isNoneBlank(value) && value.length() >= 2 && !StringUtils.equals(value, "\"\"")) {
				value = value.substring(1, value.length() - 1);
				map.put(key, value);
			}
			// 格式化HTML结构
			String html = (String) map.get(CONTENT_HTML);
			if (StringUtils.isNoneBlank(html)) {
				html = html.replace("\\r\\n", "");
				html = html.replace("\\t", "");
				html = html.replace("\\", "");
				map.put(CONTENT_HTML, html);
			}
		}
	}

	private void handleNotNullNameSpace(Map<String, String> map, String value) {
		String realContent = null;
		Document doc = null;
		if (StringUtils.equalsIgnoreCase(S_NS_TITLE, value)) {
			readBaseInfo(map);
		} else if (StringUtils.equalsIgnoreCase(S_NS_LEVEL, value)) {
			realContent = (String) map.get(CONTENT_HTML);
			doc = Jsoup.parse(realContent);
			String domid = map.get(S_DOMID_KEY);
			if (domid.startsWith(S_DOMID_LEVEL_FIND)) {
				readProfileTags(doc);

			} else if (domid.startsWith(S_DOMID_ARTICLE_FIND)) {
				readArticles(doc, domid);
			}

		} else if (StringUtils.equalsIgnoreCase(S_NS_RELATION, value)) {
			realContent = (String) map.get(CONTENT_HTML);
			if (StringUtils.isNoneBlank(realContent)) {
				readRelations(map, realContent);
			}
		}

	}

	private void readRelations(Map<String, String> map, String realContent) {
		Document doc;
		String domid = map.get(S_DOMID_KEY);
		doc = Jsoup.parse(realContent);
		if (domid.startsWith(S_DOMID_USERGRID_FIND)) {
			Elements followsPerson = doc.select("div.m_wrap>div:nth-child(1)");
			if (followsPerson != null && followsPerson.size() > 0) {
				Elements hrefs = followsPerson.select("li a[class=S_txt1]");
				if (hrefs != null && hrefs.size() > 0) {
					for (Element href : hrefs) {
						String path = href.attr("href");
						if (!path.startsWith("http")) {
							String followUrl = BASE_URL + path;
							SinaTargetInfo info = new SinaTargetInfo();
							info.setUrl(followUrl);
							addTargetToNextPage(info);
							System.out.println("follow,href-------->" + followUrl);
							System.out.println("follow,title-------->" + href.attr("title"));
						}
					}
				}
			}
			Elements fansPerson = doc.select("div.m_wrap>div:nth-child(2)");
			if (fansPerson != null && fansPerson.size() > 0) {
				Elements hrefs = fansPerson.select("li a[class=S_txt1]");
				if (hrefs != null && hrefs.size() > 0) {
					for (Element href : hrefs) {
						String path = href.attr("href");
						if (!path.startsWith("http")) {
							String fanUrl = BASE_URL + path;
							SinaTargetInfo info = new SinaTargetInfo();
							info.setUrl(fanUrl);
							addTargetToNextPage(info);
							System.out.println("fans,href-------->" + fanUrl);
							System.out.println("fans,title-------->" + href.attr("title"));
						}
					}
				}
			}
		}
	}

	private void readBaseInfo(Map<String, String> map) {
		String realContent;
		Document doc;
		Elements eles;
		realContent = (String) map.get(CONTENT_HTML);
		doc = Jsoup.parse(realContent);
		eles = doc.select(PROFILE_SEX_FEMALE);
		if (eles.size() > 0) {
			System.out.println("获取该用户性别：女");
		} else {
			System.out.println("获取该用户性别：未知");
		}

		if (doc.select(PROFILE_VERIFY_BLUE).size() > 0) {
			System.out.println("认证：蓝色级别");
		} else if (doc.select(PROFILE_VERIFY_YELLOW).size() > 0) {
			System.out.println("认证：黄色级别");
		} else if (doc.select(PROFILE_VERIFY_CLUB).size() > 0) {
			System.out.println("认证：club级别");
		} else {
			System.out.println("认证：未认证");
		}
	}

	private void readArticles(Document doc, String domid) {
		Elements eles;

		eles = doc.getElementsByAttributeValue("action-type", "feed_list_item");
		if (eles != null && eles.size() >= 1) {
			for (Element ele : eles) {
				// 文章作者
				Elements datas = ele.getElementsByAttributeValue("node-type", "feed_list_originNick");
				if (datas != null && datas.size() >= 1) {
					for (Element data : datas) {
						System.out.println("author---->" + data.attr("title"));
					}
				}
				// 转发时间
				// node-type="feed_list_item_date"
				datas = ele.getElementsByAttributeValue("node-type", "feed_list_item_date");
				if (datas != null && datas.size() >= 1) {
					int len = datas.size();
					// 如果长度为一，表明是自己发表的
					// 如果长度为二，表明是一个为别人的文章转发时间，一个是自己转发的时间
					if (len == 1) {
						System.out.println("发表时间---->" + datas.attr("title"));
						System.out.println("发表时间---->" + datas.attr("date"));
					} else if (len == 2) {
						System.out.println("原作者发表时间---->" + datas.get(0).attr("title"));
						System.out.println("原作者发表时间---->" + datas.get(0).attr("date"));
						System.out.println("原文链接---->" + "http://weibo.com" + datas.get(0).attr("href"));

						// 转发，评论，点赞
						// 这里会获取六个，只有前三个有效,后面四个是二次操作中的收藏，评论转发和点赞
						// 空值没有处理
						Elements lis = ele.select("span[class=line S_line1]");
						if (lis != null && lis.size() == 7) {
							System.out.println("原文转发数---->" + lis.get(0).text());
							System.out.println("原文评论数---->" + lis.get(1).text());
							System.out.println("原文点赞数---->" + lis.get(2).select("em").html());
						}

						System.out.println("转发时间---->" + datas.get(1).attr("title"));
						System.out.println("转发时间---->" + datas.get(1).attr("date"));
					}

				}

				datas = ele.getElementsByAttributeValue("node-type", "feed_list_content");
				if (datas != null && datas.size() >= 1) {
					System.out.println("文章内容：" + datas.text());
				}

			}
		}
	}

	private void readProfileTags(Document doc) {
		Elements eles;
		eles = doc.select(TAG_PARENT);
		if (eles != null && eles.size() > 0) {
			Elements datas = null;
			for (Element ele : eles) {
				datas = ele.select(TAG_LEVEL);
				if (datas != null && datas.size() > 0) {
					System.out.println("level----->" + datas.get(0).html());
				}
				datas = ele.getElementsByClass(TAG_PLACE);
				if (datas != null && datas.size() >= 1) {
					String address = ele.getElementsByClass("item_text").text().trim();
					System.out.println("address---->" + address);

				}
				datas = ele.getElementsByClass(TAG_INFO);
				if (datas != null && datas.size() >= 1) {
					String info = ele.getElementsByClass("item_text").text().trim();
					System.out.println("info---->" + info);
				}
				datas = ele.getElementsByClass(TAG_TAG);
				if (datas != null && datas.size() >= 1) {
					String tag = ele.text();
					System.out.println("tag---->" + tag);

				}
				datas = ele.getElementsByClass(TAG_BIRTH);
				if (datas != null && datas.size() >= 1) {
					String birth = ele.text();
					System.out.println("birth---->" + birth);

				}

			}
		}
	}

	private void handleNullNameSpace(Map<String, String> map) {
		String value = map.get(S_DOMID_KEY);
		if (StringUtils.isBlank(value)) {
			return;
		}

		if (value.startsWith(S_DOMID_BASE_FIND)) {
			String realContent = map.get(CONTENT_HTML);
			Document doc = Jsoup.parse(realContent);
			Elements eles = doc.select(ELEMENT_STRONG);
			if (eles != null && eles.size() == 3) {
				System.out.println("关注数" + eles.get(0).html());
				System.out.println("粉丝数" + eles.get(1).html());
				System.out.println("微博数" + eles.get(2).html());
			}
		}

	}

}
