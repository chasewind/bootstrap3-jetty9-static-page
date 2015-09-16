package com.belief.test;

import com.belief.utils.DESUtils;

public class DESTester {
	static String BASE_DIR = System.getProperty("user.dir");
	static String key;

	static {
		try {
			key = DESUtils.getSecretKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		long begin = System.currentTimeMillis();
		decryptFile();
		long end = System.currentTimeMillis();
		System.err.println("耗时：" + (end - begin) / 1000 + "秒");
	}

	static void decryptFile() throws Exception {
		String sourceFilePath = BASE_DIR + "/client.p12";
		String destFilePath = "e:/clientd_dec.crt";
		DESUtils.decryptFile(key, sourceFilePath, destFilePath);
	}

}