package com.belief.test.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

public class MulThreadEx {
	public static void main(String[] args) {
		new MulThreadEx().test();
	}

	@Test(timeout = 1000000)
	public void test() {
		RepoEx repo = new RepoEx();
		repo.init();
		for (int i = 0; i < 20; i++) {
			new Thread(new ProducerEx(repo)).start();
		}
		for (int i = 0; i < 15; i++) {
			new Thread(new ConsumerEx(repo)).start();
		}

	}
}

class RepoEx {
	List<String> list = Collections.synchronizedList(new ArrayList<String>());
	static final int SIZE_LIMIT = 160;
	int count = 0;

	public void init() {
		synchronized (this.list) {
			for (int i = 0; i < 100; i++) {
				String data = i + "-->" + RandomStringUtils.randomAlphanumeric(20);
				this.list.add(data);
			}
			count = 100;
		}
	}

	public synchronized void produce(int amount) {

		try {
			while (this.list.size() >= SIZE_LIMIT || this.list.size() + amount > SIZE_LIMIT) {
				System.out.println("------produce---fast---" + list.size() + "-->" + Thread.currentThread().getName());
				this.wait();
			}
			for (int i = 0; i < amount; i++) {
				String data = count + "-->" + RandomStringUtils.randomAlphanumeric(20);
				this.list.add(data);
				count++;
			}
			System.out.println("produce---->" + this.list.size() + "-->" + Thread.currentThread().getName());
			this.notifyAll();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void consume(int amount) {
		try {
			if (this.list.size() == 0 || this.list.size() - amount <= 0) {
				if (this.list.size() == 0) {
					System.out.println("------consume------empty!!!" + "-->" + Thread.currentThread().getName());
				} else {
					System.out.println("------consume------normal" + "-->" + Thread.currentThread().getName());
				}
				this.wait();
			}
			if (list.size() - amount < 0) {
				System.out.println("task not enough" + "-->" + Thread.currentThread().getName());
			} else {
				List<String> execludeList = new ArrayList<String>();
				for (int i = 0; i < amount; i++) {
					String data = list.get(i);
					execludeList.add(data);
				}
				list.removeAll(execludeList);
				System.out.println("consume---->" + this.list.size() + "-->" + Thread.currentThread().getName());

			}
			this.notifyAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class ProducerEx implements Runnable {

	private RepoEx repo;

	public ProducerEx(RepoEx repo) {
		this.repo = repo;
	}

	@Override
	public void run() {
		while (true) {
			try {
				TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 5));
				this.repo.produce(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}

class ConsumerEx implements Runnable {

	private RepoEx repo;

	public ConsumerEx(RepoEx repo) {
		this.repo = repo;
	}

	@Override
	public void run() {
		while (true) {
			try {
				TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 5));
				this.repo.consume(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}