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
public class SinaMicroBlogTask extends TaskHandlerCenter<NextPage, SinaResultInfo> {

    private List<NextPage> targets = null;

    public static String BASE_PATH = System.getProperty("user.dir");
    public static String BASE_URL = "http://weibo.com";
    /** 文档元素之脚本 */
    private static final String DOC_SCRIPT = "script";
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
    protected void init() {}

    @Override
    protected List<NextPage> getAllTarget() {
        return this.targets;
    }

    @Override
    protected void setTargets(List<NextPage> targets) {
        this.targets = targets;
    }


    @Override
    protected SinaResultInfo fetchData(NextPage target) {

        String url = target.getUrl();
        if (StringUtils.isNoneBlank(url)) {
            String htmlContent = downLoader.GetHtml(url);
            SinaResultInfo info = analyseHtml(url, htmlContent);
            info.setUrl(url);
            return info;
        }
        return null;
    }



    private SinaResultInfo analyseHtml(String url, String htmlContent) {
        SinaResultInfo info = new SinaResultInfo();
        Document doc = Jsoup.parse(htmlContent);
        info.setTitle(doc.title());
        Elements scripts = doc.select(DOC_SCRIPT);
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
                logger.error("map convert failed------>" + e.getMessage());
                continue;
            }

            String value = map.get(S_NS);
            // 命名空间为空
            if (StringUtils.isBlank(value)) {
                handleNullNameSpace(map, info);
            } else {
                handleNotNullNameSpace(map, value, info);
            }

        }

        return info;
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

    private void handleNotNullNameSpace(Map<String, String> map, String value, SinaResultInfo info) {
        String realContent = null;
        Document doc = null;
        if (StringUtils.equalsIgnoreCase(S_NS_TITLE, value)) {
            readBaseInfo(map, info);
        } else if (StringUtils.equalsIgnoreCase(S_NS_LEVEL, value)) {
            realContent = (String) map.get(CONTENT_HTML);
            if (StringUtils.isNoneBlank(realContent)) {
                doc = Jsoup.parse(realContent);
                String domid = map.get(S_DOMID_KEY);
                if (domid.startsWith(S_DOMID_LEVEL_FIND)) {
                    readProfileTags(doc, info);

                } else if (domid.startsWith(S_DOMID_ARTICLE_FIND)) {
                    List<SinaArticle> articles = readArticles(doc);
                    info.setArticles(articles);
                }

            }

        } else if (StringUtils.equalsIgnoreCase(S_NS_RELATION, value)) {
            realContent = (String) map.get(CONTENT_HTML);
            if (StringUtils.isNoneBlank(realContent)) {
                readRelations(map, info);
            }
        }

    }

    private void readRelations(Map<String, String> map, SinaResultInfo info) {
        Document doc;
        String domid = map.get(S_DOMID_KEY);
        String realContent = (String) map.get(CONTENT_HTML);
        if (StringUtils.isBlank(realContent)) {
            return;
        }

        List<NextPage> followPages = new ArrayList<NextPage>();
        List<NextPage> fanPages = new ArrayList<NextPage>();
        doc = Jsoup.parse(realContent);
        if (domid.startsWith(S_DOMID_USERGRID_FIND)) {
            Elements followsPerson = doc.select("div.m_wrap>div:nth-child(1)");
            if (followsPerson != null && followsPerson.size() > 0) {
                Elements hrefs = followsPerson.select("li a[class=S_txt1]");
                if (hrefs != null && hrefs.size() > 0) {
                    for (Element href : hrefs) {
                        String path = href.attr("href");

                        if (!path.startsWith("http")) {
                            NextPage nextPage = new NextPage();
                            String followUrl = BASE_URL + path;
                            nextPage.setUrl(followUrl);
                            nextPage.setTitle(href.attr("title"));
                            followPages.add(nextPage);
                        }
                    }
                    info.setFollowPages(followPages);
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
                            NextPage nextPage = new NextPage();
                            nextPage.setUrl(fanUrl);
                            nextPage.setTitle(href.attr("title"));
                            fanPages.add(nextPage);
                        }
                    }
                }
                info.setFanPages(fanPages);
            }
        }

    }

    private void readBaseInfo(Map<String, String> map, SinaResultInfo info) {
        String realContent;
        Document doc;
        Elements eles;
        realContent = (String) map.get(CONTENT_HTML);
        doc = Jsoup.parse(realContent);
        eles = doc.select(PROFILE_SEX_FEMALE);
        if (eles.size() > 0) {
            info.setSex("0");
        } else {
            info.setSex("1");
        }

        if (doc.select(PROFILE_VERIFY_BLUE).size() > 0) {
            info.setVerifyLevel("BLUE");
        } else if (doc.select(PROFILE_VERIFY_YELLOW).size() > 0) {
            info.setVerifyLevel("YELLOW");
        } else if (doc.select(PROFILE_VERIFY_CLUB).size() > 0) {
            info.setVerifyLevel("CLUB");
        } else {
            info.setVerifyLevel("ZERO");
        }
    }

    private List<SinaArticle> readArticles(Document doc) {
        Elements eles;
        List<SinaArticle> articles = new ArrayList<SinaArticle>();
        eles = doc.getElementsByAttributeValue("action-type", "feed_list_item");
        if (eles == null || eles.size() == 0) {
            return articles;
        }
        for (Element ele : eles) {
            SinaArticle article = new SinaArticle();
            // 文章作者
            Elements datas = ele.getElementsByAttributeValue("node-type", "feed_list_originNick");
            if (datas != null && datas.size() >= 1) {
                article.setAuthor(datas.attr("title"));
            }
            datas = ele.getElementsByAttributeValue("node-type", "feed_list_content");
            if (datas != null && datas.size() >= 1) {
                article.setContent(datas.text());
            }
            // 转发时间
            datas = ele.getElementsByAttributeValue("node-type", "feed_list_item_date");
            if (datas != null && datas.size() >= 1) {
                int len = datas.size();
                // 如果长度为一，表明是自己发表的
                // 如果长度为二，表明是一个为别人的文章转发时间，一个是自己转发的时间
                if (len == 1) {
                    article.setPublishDay(datas.attr("title"));
                    article.setPublishDate(datas.attr("date"));
                } else if (len == 2) {
                    article.setOrignDate(datas.get(0).attr("date"));
                    article.setOrignDay(datas.get(0).attr("title"));
                    article.setOrignLink(BASE_URL + datas.get(0).attr("href"));
                    article.setPublishDate(datas.get(1).attr("date"));
                    article.setPublishDay(datas.get(1).attr("title"));
                    // 转发，评论，点赞
                    // 这里会获取六个，只有前三个有效,后面四个是二次操作中的收藏，评论转发和点赞
                    Elements lis = ele.select("span[class=line S_line1]");
                    if (lis != null && lis.size() == 7) {
                        article.setOrignForward(lis.get(0).text());
                        article.setOrignComments(lis.get(1).text());
                        article.setOrignPraise(lis.get(2).select("em").html());
                    }


                }

            }
            articles.add(article);

        }
        return articles;
    }

    private void readProfileTags(Document doc, SinaResultInfo info) {
        Elements eles;
        eles = doc.select(TAG_PARENT);
        if (eles != null && eles.size() > 0) {
            Elements datas = null;
            for (Element ele : eles) {
                datas = ele.select(TAG_LEVEL);
                if (datas != null && datas.size() > 0) {
                    info.setLevel(datas.get(0).html());
                }
                datas = ele.getElementsByClass(TAG_PLACE);
                if (datas != null && datas.size() >= 1) {
                    String address = ele.getElementsByClass("item_text").text().trim();
                    info.setAddr(address);
                }
                datas = ele.getElementsByClass(TAG_INFO);
                if (datas != null && datas.size() >= 1) {
                    String profile = ele.getElementsByClass("item_text").text().trim();
                    info.setProfile(profile);
                }
                datas = ele.getElementsByClass(TAG_TAG);
                if (datas != null && datas.size() >= 1) {
                    String tag = ele.text();
                    info.setTag(tag);
                }
                datas = ele.getElementsByClass(TAG_BIRTH);
                if (datas != null && datas.size() >= 1) {
                    String birth = ele.text();
                    info.setBirth(birth);

                }

            }
        }
    }

    private void handleNullNameSpace(Map<String, String> map, SinaResultInfo info) {
        String value = map.get(S_DOMID_KEY);
        if (StringUtils.isBlank(value)) {
            return;
        }

        if (value.startsWith(S_DOMID_BASE_FIND)) {
            String realContent = map.get(CONTENT_HTML);
            Document doc = Jsoup.parse(realContent);
            Elements eles = doc.select(ELEMENT_STRONG);
            if (eles != null && eles.size() == 3) {
                info.setFollowCnt(eles.get(0).html());
                info.setFansCnt(eles.get(1).html());
                info.setBlogCnt(eles.get(2).html());
            }
        }

    }



}
