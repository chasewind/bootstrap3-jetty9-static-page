package com.belief.crawler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


public class SinaCrawlerApp {
    public static void main(String[] args) {
        SinaMicroBlogTask sinaTask = new SinaMicroBlogTask();
        sinaTask.setTargets(SinaTargetFactory.getAllTarget());
        List<SinaResultInfo> results = sinaTask.doTask();
        if (CollectionUtils.isNotEmpty(results)) {
            List<NextPage> nextPages = new ArrayList<NextPage>();
            for (SinaResultInfo info : results) {
                List<NextPage> followPages = info.getFollowPages();
                List<NextPage> fanPages = info.getFanPages();
                if (CollectionUtils.isNotEmpty(followPages)) {
                    nextPages.addAll(followPages);
                }
                if (CollectionUtils.isNotEmpty(fanPages)) {
                    nextPages.addAll(fanPages);
                }
                // second pages
                int size = nextPages.size();
                for (int i = 0; i < size; i++) {
                    SinaMicroBlogTask task = new SinaMicroBlogTask();
                    task.setTargets(nextPages);
                    task.doTask();
                }

            }
        }
    }
}
