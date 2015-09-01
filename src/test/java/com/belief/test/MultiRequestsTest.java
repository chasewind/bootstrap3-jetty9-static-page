package com.belief.test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

/**
 * 多线程集成测试
 * 
 * @author yudongwei
 *
 */
public class MultiRequestsTest {
	@Test
	public void test() throws Throwable {
		TestRunnable runner = new TestRunnable() {
			@Override
			public void runTest() throws Throwable {
				TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 15));
				System.out.println(new Date() + "---->" + RandomStringUtils.randomAlphanumeric(20));
			}
		};
		int runnerCount = 20;
		TestRunnable[] trs = new TestRunnable[runnerCount];
		for (int i = 0; i < runnerCount; i++) {
			trs[i] = runner;
		}
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		mttr.runTestRunnables();
	}

}
