package com.belief.crawler;

import java.io.Serializable;
import java.util.List;

public class SinaResultInfo implements Serializable {
    private static final long serialVersionUID = -1479110772759984883L;
    /** 当前地址 */
    private String url;
    private String sex;
    private String verifyLevel;
    /** 标题 */
    private String title;
    /** 关注数 */
    private String followCnt;
    /** 粉丝数 */
    private String fansCnt;
    /** 博客数 */
    private String blogCnt;
    /** 等级 */
    private String level;
    /** 地址 */
    private String addr;
    /** 个人简讯 */
    private String profile;
    /** 标签 */
    private String tag;
    /** 生日 */
    private String birth;

    private List<SinaArticle> articles;
    private List<NextPage> followPages;
    private List<NextPage> fanPages;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getVerifyLevel() {
        return verifyLevel;
    }

    public void setVerifyLevel(String verifyLevel) {
        this.verifyLevel = verifyLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFollowCnt() {
        return followCnt;
    }

    public void setFollowCnt(String followCnt) {
        this.followCnt = followCnt;
    }

    public String getFansCnt() {
        return fansCnt;
    }

    public void setFansCnt(String fansCnt) {
        this.fansCnt = fansCnt;
    }

    public String getBlogCnt() {
        return blogCnt;
    }

    public void setBlogCnt(String blogCnt) {
        this.blogCnt = blogCnt;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public List<SinaArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<SinaArticle> articles) {
        this.articles = articles;
    }

    public List<NextPage> getFollowPages() {
        return followPages;
    }

    public void setFollowPages(List<NextPage> followPages) {
        this.followPages = followPages;
    }

    public List<NextPage> getFanPages() {
        return fanPages;
    }

    public void setFanPages(List<NextPage> fanPages) {
        this.fanPages = fanPages;
    }



}
