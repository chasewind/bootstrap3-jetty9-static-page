package com.belief.test.task;

import java.util.List;

public abstract class CTask<Target> {
	protected abstract List<Target> getAllTargets();

	/**
	 * 设置爬取任务
	 * 
	 * @param targets
	 */
	protected abstract void setTargets(List<Target> targets);

	public void work() {
		List<Target> list = getAllTargets();
		for (Target target : list) {
			Taskhandler<Target> handler = new Taskhandler<Target>(target);
		}
	}
}
