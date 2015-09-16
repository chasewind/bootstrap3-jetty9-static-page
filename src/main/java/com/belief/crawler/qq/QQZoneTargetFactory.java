package com.belief.crawler.qq;

import java.util.ArrayList;
import java.util.List;

import com.belief.crawler.NextPage;

public class QQZoneTargetFactory {
	// https://github.com/code4craft/webmagic
	// http://blog.csdn.net/c_son/article/details/43818391
	private static List<NextPage> targetList = new ArrayList<NextPage>();

	static {
		NextPage info = new NextPage();
		//
		// info.setUrl("http://user.qzone.qq.com/8333286");
		info.setUrl("http://mp.weixin.qq.com/s?__biz=MjM5OTA0Mzc2MA==&mid=210714027&idx=1&sn=2bbda0a08cac8eaf105ea82e6e5c46d9&3rd=MzA3MDU4NTYzMw==&scene=6#rd");
		targetList.add(info);
	}

	public static List<NextPage> getAllTarget() {
		return targetList;
	}
}
