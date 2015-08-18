package com.belief.crawler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class SinaCrawlerApp {
	public static void main(String[] args) {
		List<SinaResultInfo> list = new ArrayList<SinaResultInfo>();
		List<NextPage> targets = SinaTargetFactory.getAllTarget();
		// 循环次数决定了爬取的深度
		for (int i = 0; i < 3; i++) {
			targets = singleTask(list, targets);
		}
		System.out.println(list.size());
	}

	private static List<NextPage> singleTask(List<SinaResultInfo> list, List<NextPage> targets) {
		List<NextPage> nextPages = new ArrayList<NextPage>();
		List<SinaResultInfo> results = crawler(targets);
		if (CollectionUtils.isNotEmpty(results)) {
			list.addAll(results);
			nextPages = analyseResults(results);
		}
		return nextPages;
	}

	/**
	 * 分析爬取数据结果，返回第二次需要爬取的页面
	 * 
	 * @param results
	 * @return
	 */
	private static List<NextPage> analyseResults(List<SinaResultInfo> results) {
		List<NextPage> nextPages = new ArrayList<NextPage>();
		if (CollectionUtils.isNotEmpty(results)) {
			for (SinaResultInfo info : results) {
				List<NextPage> followPages = info.getFollowPages();
				List<NextPage> fanPages = info.getFanPages();
				if (CollectionUtils.isNotEmpty(followPages)) {
					nextPages.addAll(followPages);
				}
				if (CollectionUtils.isNotEmpty(fanPages)) {
					nextPages.addAll(fanPages);
				}

			}
		}
		return nextPages;
	}

	/**
	 * 根据指定的列表页面进行页面抓取
	 * 
	 * @param targets
	 * @return
	 */
	public static List<SinaResultInfo> crawler(List<NextPage> targets) {
		SinaMicroBlogTask sinaTask = new SinaMicroBlogTask();
		sinaTask.setTargets(targets);
		return sinaTask.doTask();
	}
}
