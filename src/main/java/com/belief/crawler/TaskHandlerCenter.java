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

	/**
	 * 根据指定的目标抓取数据
	 * 
	 * @param target
	 * @return
	 */

	protected abstract Result fetchData(Target target);

	/**
	 * 分析处理结果
	 * 
	 * @param result
	 */
	protected abstract void process(Result result);

	protected abstract List<Target> getNextPageTargets();

	protected abstract int addTargetToNextPage(Target target);

	public final void doTask() {
		List<Target> list = getAllTarget();
		logger.info("begin task..." + DateUtils.getCurrentTime());
		for (Target target : list) {
			// 处理当前页面数据
			handleTarget(target);
			// 根据粉丝和关注进行下一层的抓取
			List<Target> nextTargets = getNextPageTargets();
			if (!CollectionUtils.isEmpty(nextTargets)) {
				List<Target> nextTargetsCopy = new ArrayList<Target>(nextTargets.size());
				nextTargetsCopy.addAll(nextTargets);
				for (Target t : nextTargetsCopy) {
					handleTarget(t);
				}
				nextTargets.clear();
				nextTargetsCopy.clear();
				System.out.println(nextTargets.size());
			}

		}
		logger.info("end task..." + DateUtils.getCurrentTime());
	}

	private void handleTarget(Target target) {
		Result result = fetchData(target);
		if (result != null) {
			process(result);
		}
	}

}
