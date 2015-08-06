package com.belief.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class StringReg {

	@Test
	public void test() {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]+$");
		String result = RandomStringUtils.randomAlphanumeric(18);
		System.out.println(result);
		Matcher match = pattern.matcher(result + "aaaaa");
		System.out.println(match.find() + "," + match.matches());
	}
}
