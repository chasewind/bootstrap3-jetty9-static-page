package com.belief.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileTest {
	@Test
	public void test() throws IOException {
		String basePath = System.getProperty("user.dir");

		File log = new File(basePath + "/log.txt");
		String result = FileUtils.readFileToString(log);
		System.out.println(result);

		String[] array = result.split(",\"");
		int len = array.length;
		for (int i = 1; i < len; i++) {
			array[i] = "\"" + array[i];
			System.out.println(array[i]);
		}
		System.out.println(array);
	}

}
