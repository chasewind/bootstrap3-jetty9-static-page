package com.belief.crawler.sina;

import java.util.ArrayList;
import java.util.List;

import com.belief.crawler.NextPage;

public class SinaTargetFactory {

    private static List<NextPage> targetList = new ArrayList<NextPage>();

    static {
        // 初始化一个微博的地址
        NextPage info = new NextPage();
        info.setUrl("http://weibo.com/u/2089012125");
        // info.setUrl("http://weibo.com/u/1832527363");
        targetList.add(info);
    }

    public static List<NextPage> getAllTarget() {
        return targetList;
    }
}
