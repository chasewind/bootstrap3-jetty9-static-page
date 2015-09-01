package com.belief.test.task;

import org.junit.Test;

public class ObjParams {

	@Test
	public void test() {
		varObj();
		int[] array = new int[] { 1, 2, 3, 4, 5, 6, 7 };
		varObj(array);
	}

	public void varObj(int... params) {
		System.out.println(params.length);

	}
}
