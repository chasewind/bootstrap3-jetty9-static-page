package com.belief.test.net;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.junit.Test;

public class RouteTest {
	public static final String HEXSTRING = "0123456789ABCDEF";

	@Test
	public void test() {

	}

	public static String md5(String originalText) throws Exception {
		byte buf[] = originalText.getBytes("ISO-8859-1");
		StringBuffer hexString = new StringBuffer();
		String result = "";
		String digit = "";

		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(buf);

			byte[] digest = algorithm.digest();

			for (int i = 0; i < digest.length; i++) {
				digit = Integer.toHexString(0xFF & digest[i]);

				if (digit.length() == 1) {
					digit = "0" + digit;
				}

				hexString.append(digit);
			}

			result = hexString.toString();
		} catch (Exception ex) {
			result = "";
		}

		return result.toUpperCase();
	}

	public static String hexchar2bin(String md5str) throws UnsupportedEncodingException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(md5str.length() / 2);

		for (int i = 0; i < md5str.length(); i = i + 2) {
			baos.write((HEXSTRING.indexOf(md5str.charAt(i)) << 4 | HEXSTRING.indexOf(md5str.charAt(i + 1))));
		}

		return new String(baos.toByteArray(), "ISO-8859-1");
	}

	public static String GetPassword(String qq, String password, String verifycode) {
		String V = "";
		try {
			String P = hexchar2bin(md5(password));
			String U = md5(P + hexchar2bin(qq.replace("\\x", "").toUpperCase()));
			V = md5(U + verifycode.toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
			return "加密时不小心就失败了，请求管理员修复啊";
		}
		return V;
	}

	public static String GetG_TK(String str) {
		int hash = 5381;
		for (int i = 0, len = str.length(); i < len; ++i) {
			hash += (hash << 5) + (int) (char) str.charAt(i);
		}
		return (hash & 0x7fffffff) + "";
	}
}
