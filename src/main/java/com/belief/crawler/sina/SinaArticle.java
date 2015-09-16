package com.belief.crawler.sina;

import java.io.Serializable;

public class SinaArticle implements Serializable {
    private static final long serialVersionUID = -1395192017537672018L;
    private String author;
    private String publishDate;
    private String publishDay;
    private String orignDate;
    private String orignDay;
    private String orignLink;
    private String orignForward;
    private String orignComments;
    private String orignPraise;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishDay() {
        return publishDay;
    }

    public void setPublishDay(String publishDay) {
        this.publishDay = publishDay;
    }

    public String getOrignDate() {
        return orignDate;
    }

    public void setOrignDate(String orignDate) {
        this.orignDate = orignDate;
    }

    public String getOrignDay() {
        return orignDay;
    }

    public void setOrignDay(String orignDay) {
        this.orignDay = orignDay;
    }

    public String getOrignLink() {
        return orignLink;
    }

    public void setOrignLink(String orignLink) {
        this.orignLink = orignLink;
    }

    public String getOrignForward() {
        return orignForward;
    }

    public void setOrignForward(String orignForward) {
        this.orignForward = orignForward;
    }

    public String getOrignComments() {
        return orignComments;
    }

    public void setOrignComments(String orignComments) {
        this.orignComments = orignComments;
    }

    public String getOrignPraise() {
        return orignPraise;
    }

    public void setOrignPraise(String orignPraise) {
        this.orignPraise = orignPraise;
    }

}
