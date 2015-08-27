package com.belief.test.task;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;

public class TaskWorker implements Callable<String> {
	private MicroUrl microUrl;

	public TaskWorker(MicroUrl microUrl) {
		this.microUrl = microUrl;
	}

	@Override
	public String call() throws Exception {
		TimeUnit.SECONDS.sleep(2);
		System.out.println(this.microUrl.getUrl());
		return RandomStringUtils.randomAlphanumeric(20);
	}

}
