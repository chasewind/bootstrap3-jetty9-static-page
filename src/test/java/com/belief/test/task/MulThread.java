package com.belief.test.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

public class MulThread {
	public static void main(String[] args) {
		new MulThread().test();
	}

	@Test(timeout = 1000000)
	public void test() {
		Repo repo = new Repo();
		repo.init();
		new Thread(new Producer(repo)).start();
		new Thread(new Consumer(repo)).start();

	}
}

class Repo {
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
				System.out.println("------produce---fast---" + list.size());
				this.wait();
			}
			for (int i = 0; i < amount; i++) {
				String data = count + "-->" + RandomStringUtils.randomAlphanumeric(20);
				this.list.add(data);
				count++;
			}
			System.out.println("produce---->" + this.list.size());
			this.notifyAll();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void consume(int amount) {
		try {
			if (this.list.size() == 0 || this.list.size() - amount <= 0) {
				if (this.list.size() == 0) {
					System.out.println("------consume------empty!!!");
				} else {
					System.out.println("------consume------normal");
				}
				this.wait();
			}
			if (list.size() - amount < 0) {
				System.out.println("task not enough");
			} else {
				List<String> execludeList = new ArrayList<String>();
				for (int i = 0; i < amount; i++) {
					String data = list.get(i);
					execludeList.add(data);

				}
				list.removeAll(execludeList);
				System.out.println("consume---->" + this.list.size());

			}
			this.notifyAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Producer implements Runnable {

	private Repo repo;

	public Producer(Repo repo) {
		this.repo = repo;
	}

	@Override
	public void run() {
		while (true) {
			try {
				TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 5));
				this.repo.produce(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}

class Consumer implements Runnable {

	private Repo repo;

	public Consumer(Repo repo) {
		this.repo = repo;
	}

	@Override
	public void run() {
		while (true) {
			try {
				TimeUnit.SECONDS.sleep(RandomUtils.nextInt(1, 5));
				this.repo.consume(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}