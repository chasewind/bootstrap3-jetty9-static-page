package com.belief.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class PhoneTest {
	private static final Pattern pattern = Pattern.compile("^1((3|4|5|7|8)\\d)\\d{8}$", Pattern.CASE_INSENSITIVE);

	@Test
	public void test() {
		Matcher matcher = pattern.matcher("14787866380");
		if (matcher.find()) {
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
		}
	}
}
