package com.belief.test;

import java.util.UUID;

import org.junit.Test;

public class UQIDTest {

	@Test
	public void test() {
		String uuid = UUID.randomUUID().toString();
		System.out.println(uuid);
		System.out.println(uuid.replaceAll("-", ""));
		
	}
}
