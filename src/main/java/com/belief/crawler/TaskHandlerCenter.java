package com.belief.crawler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.belief.utils.DateUtils;

/**
 * 爬虫任务处理中心，功能：定义抽象步骤
 * 
 * @author yudongwei
 *
 */
public abstract class TaskHandlerCenter<Target, Result> {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected static final PageDownLoader downLoader = new SinaDownloader();

	public TaskHandlerCenter() {
		init();
	}

	/**
	 * 初始化
	 */
	protected abstract void init();

	/**
	 * 获取到所有要处理的目标
	 * 
	 * @return
	 */
	protected abstract List<Target> getAllTarget();

	protected abstract void setTargets(List<Target> targets);

	/**
	 * 根据指定的目标抓取数据
	 * 
	 * @param target
	 * @return
	 */

	protected abstract Result fetchData(Target target);

	public final List<Result> doTask() {
		List<Target> list = getAllTarget();
		List<Result> results = new ArrayList<Result>();
		logger.info("begin task..." + DateUtils.getCurrentTime());
		if (CollectionUtils.isNotEmpty(list)) {
			for (Target target : list) {
				// 处理当前页面数据
				Result result = fetchData(target);
				if (result != null) {
					results.add(result);
				}
			}

		}
		logger.info("end task..." + DateUtils.getCurrentTime());
		return results;
	}

}
