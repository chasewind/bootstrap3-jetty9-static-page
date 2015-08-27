package com.belief.test;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Ignore;
import org.junit.Test;

public class CallableAndFuture {

	@Test
	@Ignore
	public void testBase() throws InterruptedException, ExecutionException {
		Callable<Integer> callable = new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				System.out.println("first begin:" + new Date());
				TimeUnit.SECONDS.sleep(15);
				return RandomUtils.nextInt(1, 200);
			}

		};
		FutureTask<Integer> futureTask = new FutureTask<Integer>(callable);
		new Thread(futureTask).start();
		System.out.println("first end:" + futureTask.get());
	}

	@Test
	@Ignore
	public void testExecutor() throws InterruptedException, ExecutionException {
		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		Callable<Integer> callable = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("second begin:" + new Date());
				TimeUnit.SECONDS.sleep(10);
				return RandomUtils.nextInt(1, 200);
			}

		};
		FutureTask<Integer> futureTask = (FutureTask<Integer>) threadPool.submit(callable);
		System.out.println("second end:" + futureTask.get());
	}

	@Test
	public void testCompletionService() throws InterruptedException, ExecutionException {
		System.out.println("begin..." + new Date());
		ExecutorService threadPool = Executors.newCachedThreadPool();
		CompletionService<Integer> cs = new ExecutorCompletionService<Integer>(threadPool);
		for (int i = 0; i < 5; i++) {
			Callable<Integer> callable = new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					TimeUnit.SECONDS.sleep(5);
					return RandomUtils.nextInt(1, 200);
				}

			};
			cs.submit(callable);
		}
		for (int i = 0; i < 5; i++) {
			System.out.println(cs.take().get());
		}
		System.out.println("end..." + new Date());
	}
}
