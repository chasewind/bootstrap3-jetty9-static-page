package com.belief.crawler;

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
    public static String basePath = System.getProperty("user.dir");
    /** 需要解析的脚本标志性开头 */
    private static final String S_SCRIPT_START_FM = "FM.view(";
    private static final String S_NS = "ns";
    /** 用户信息认证等级相关 */
    private static final String S_NS_TITLE = "pl.header.head.index";
    private static final String S_NS_LEVEL = "pl.content.homeFeed.index";

    private static final String S_DOMID_KEY = "domid";
    private static final String S_DOMID_BASE_FIND = "Pl_Core_T8CustomTriColumn__";
    private static final String S_DOMID_LEVEL_FIND = "Pl_Core_UserInfo__";

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
    protected void init() {}

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
            String json = html.substring((S_SCRIPT_START_FM.length() + 1), html.length() - 3);

            Map<String, String> map = new HashMap<String, String>();
            convertToMap(json, map);

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

    private void convertToMap(String json, Map<String, String> map) {
        String[] array = json.split(",");
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
            if (StringUtils.isNoneBlank(value) && value.length() >= 2
                    && !StringUtils.equals(value, "\"\"")) {
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
        Elements eles = null;
        if (StringUtils.equalsIgnoreCase(S_NS_TITLE, value)) {
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
        } else if ((StringUtils.equalsIgnoreCase(S_NS_LEVEL, value))) {
            System.out.println("id----->" + map.get(S_DOMID_KEY));
            realContent = (String) map.get(CONTENT_HTML);
            doc = Jsoup.parse(realContent);
            // System.out.println(doc.html());
            String domid = map.get(S_DOMID_KEY);
            // 获取个人标签

            if (domid.startsWith(S_DOMID_LEVEL_FIND)) {
                // private static final String TAG_PLACE = "ficon_cd_place";
                // private static final String TAG_INFO = "ficon_pinfo";
                // private static final String TAG_TAG = "ficon_cd_coupon";
                // private static final String TAG_BIRTH = "ficon_constellation";
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
                        datas = ele.getElementsByClass("ficon_cd_coupon");
                        for (Element data : datas) {
                            String tag = ele.text();
                            System.out.println("tag---->" + tag);

                        }
                        System.out.println("li------>" + ele.html());
                    }
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
