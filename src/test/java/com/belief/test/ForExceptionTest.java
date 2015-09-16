package com.belief.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class ForExceptionTest {

	@Test
	public void testException() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 50; i++) {
			if (i == 30) {
				try {
					throw new Exception(RandomStringUtils.randomAlphanumeric(200));
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			list.add(i);
		}
		System.out.println(list);
	}
}
