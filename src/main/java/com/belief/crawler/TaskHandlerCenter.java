package com.belief.crawler;

import java.util.List;

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

	public final void doTask() {
		List<Target> list = getAllTarget();
		logger.info("begin task..." + DateUtils.getCurrentTime());
		for (Target target : list) {
			Result result = fetchData(target);
			if (result != null) {
				process(result);
			}
		}
		logger.info("end task..." + DateUtils.getCurrentTime());
	}

}
