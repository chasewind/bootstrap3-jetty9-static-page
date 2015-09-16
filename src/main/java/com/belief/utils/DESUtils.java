package com.belief.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtils {

	private static final String ALGORITHM = "DES";
	private static final int CACHE_SIZE = 1024;

	public static String getSecretKey() throws Exception {
		return getSecretKey(null);
	}

	public static String getSecretKey(String seed) throws Exception {
		SecureRandom secureRandom;
		if (seed != null && !"".equals(seed))
			secureRandom = new SecureRandom(seed.getBytes());
		else
			secureRandom = new SecureRandom();
		KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
		keyGenerator.init(secureRandom);
		SecretKey secretKey = keyGenerator.generateKey();
		return Base64Utils.encode(secretKey.getEncoded());
	}

	public static byte[] encrypt(byte[] data, String key) throws Exception {
		Key k = toKey(Base64Utils.decode(key));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		return cipher.doFinal(data);
	}

	public static void encryptFile(String key, String sourceFilePath, String destFilePath) throws Exception {
		File sourceFile = new File(sourceFilePath);
		File destFile = new File(destFilePath);
		if (sourceFile.exists() && sourceFile.isFile()) {
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}
			destFile.createNewFile();
			InputStream in = new FileInputStream(sourceFile);
			OutputStream out = new FileOutputStream(destFile);
			Key k = toKey(Base64Utils.decode(key));
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, k);
			CipherInputStream cin = new CipherInputStream(in, cipher);
			byte[] cache = new byte[CACHE_SIZE];
			int nRead = 0;
			while ((nRead = cin.read(cache)) != -1) {
				out.write(cache, 0, nRead);
				out.flush();
			}
			out.close();
			cin.close();
			in.close();
		}
	}

	public static byte[] decrypt(byte[] data, String key) throws Exception {
		Key k = toKey(Base64Utils.decode(key));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);
		return cipher.doFinal(data);
	}

	public static void decryptFile(String key, String sourceFilePath, String destFilePath) throws Exception {
		File sourceFile = new File(sourceFilePath);
		File destFile = new File(destFilePath);
		if (sourceFile.exists() && sourceFile.isFile()) {
			if (!destFile.getParentFile().exists()) {
				destFile.getParentFile().mkdirs();
			}
			destFile.createNewFile();
			InputStream in = new FileInputStream(sourceFile);
			OutputStream out = new FileOutputStream(destFile);
			Key k = toKey(Base64Utils.decode(key));
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, k);
			CipherOutputStream cout = new CipherOutputStream(out, cipher);
			byte[] cache = new byte[CACHE_SIZE];
			int nRead = 0;
			while ((nRead = in.read(cache)) != -1) {
				cout.write(cache, 0, nRead);
				cout.flush();
			}
			cout.close();
			out.close();
			in.close();
		}
	}

	private static Key toKey(byte[] key) throws Exception {
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey secretKey = keyFactory.generateSecret(dks);
		return secretKey;
	}

}