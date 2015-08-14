package com.belief.crawler;

import java.util.ArrayList;
import java.util.List;

public class SinaTargetFactory {

	private static List<SinaTargetInfo> targetList = new ArrayList<SinaTargetInfo>();

	static {
		// 初始化一个微博的地址
		SinaTargetInfo info = new SinaTargetInfo();
		info.setMemo("");
		info.setUrl("http://weibo.com/u/2089012125");
		// info.setUrl("http://weibo.com/u/1832527363");
		targetList.add(info);
	}

	public static List<SinaTargetInfo> getAllTarget() {
		return targetList;
	}
}
