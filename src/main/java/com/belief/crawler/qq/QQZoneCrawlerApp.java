package com.belief.crawler.qq;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.belief.crawler.NextPage;

public class QQZoneCrawlerApp {
	public static void main(String[] args) {
		List<NextPage> list = QQZoneTargetFactory.getAllTarget();

		QQZoneDownloader downloader = new QQZoneDownloader();
		if (CollectionUtils.isNotEmpty(list)) {
			for (NextPage nextPage : list) {
				String html = downloader.GetHtml(nextPage.getUrl());
				if (StringUtils.isNoneBlank(html)) {
					System.out.println(html);
				}

			}
		}
	}
}
