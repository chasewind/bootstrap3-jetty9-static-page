package com.belief.crawler.sina;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.belief.crawler.PageDownLoader;
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
	private static Executor executor = new ScheduledThreadPoolExecutor(10);
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
			CountDownLatch latch = new CountDownLatch(list.size());
			for (Target target : list) {
				executor.execute(new CrawlerThread(target, results, latch));
			}

		}
		logger.info("end task..." + DateUtils.getCurrentTime());
		return results;
	}

	class CrawlerThread implements Runnable {
		Target target;
		List<Result> results;
		CountDownLatch latch;

		public CrawlerThread(Target target, List<Result> results, CountDownLatch latch) {
			this.target = target;
			this.results = results;
			this.latch = latch;
		}

		@Override
		public void run() {
			Result result = fetchData(target);
			if (result != null) {
				results.add(result);
			}
			latch.countDown();
		}

	}

}
