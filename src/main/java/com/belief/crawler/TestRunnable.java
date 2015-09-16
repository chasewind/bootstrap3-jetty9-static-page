package com.belief.crawler;

public class TestRunnable {

	static class SellRunnable implements Runnable {
		private int ticket = 10;

		@Override
		public void run() {
			for (int i = 0; i < 20; i++) {
				synchronized (this) {
					if (ticket > 0) {
						System.out.println("卖票：" + ticket--);
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		SellRunnable runnable = new SellRunnable();
		Thread thread0 = new Thread(runnable);
		Thread thread1 = new Thread(runnable);
		Thread thread2 = new Thread(runnable);
		thread0.start();
		thread1.start();
		thread2.start();
		// 虽然现在程序中有三个线程，但是一共卖了10张票
		// 使用Runnable实现多线程可以达到资源共享的目的
	}
}