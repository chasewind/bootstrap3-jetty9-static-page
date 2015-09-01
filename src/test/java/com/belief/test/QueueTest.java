package com.belief.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class QueueTest {
	private static final int SIZE_LIMIT = 160;
	private static final int HALF_SIZE_LIMIT = SIZE_LIMIT / 2;
	private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(HALF_SIZE_LIMIT);

	@Test
	public void test() {
		for (int i = 0; i < 20; i++) {
			String data = RandomStringUtils.randomAlphanumeric(20);
			queue.add(data);
			System.out.println(data);
		}
		System.out.println("---------------------");
		for (int i = 0; i < 20; i++) {
			System.out.println(queue.poll());

		}
	}
}
