// package com.belief.test;
//
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.security.InvalidKeyException;
// import java.security.Key;
// import java.security.KeyStore;
// import java.security.KeyStoreException;
// import java.security.NoSuchAlgorithmException;
// import java.security.NoSuchProviderException;
// import java.security.SignatureException;
// import java.security.cert.Certificate;
// import java.security.cert.CertificateException;
// import java.security.cert.X509Certificate;
//
// import org.junit.Ignore;
// import org.junit.Test;
//
// import sun.security.x509.CertAndKeyGen;
// import sun.security.x509.X500Name;
//
// @SuppressWarnings("restriction")
// public class KeyToolTest {
//
// /**
// * 建一个空的keystore
// *
// * @throws KeyStoreException
// * @throws NoSuchAlgorithmException
// * @throws CertificateException
// * @throws IOException
// */
// @Test
// @Ignore
// public void testKeyStoreFile() throws KeyStoreException, NoSuchAlgorithmException,
// CertificateException, IOException {
// File file = new File("jetty.keystore");
// KeyStore keyStore = KeyStore.getInstance("jks");
// keyStore.load(null, null);
// keyStore.store(new FileOutputStream(file), "chaseEcho".toCharArray());
// }
//
// /**
// * 生成私钥
// *
// * @throws KeyStoreException
// * @throws NoSuchAlgorithmException
// * @throws CertificateException
// * @throws IOException
// * @throws InvalidKeyException
// * @throws NoSuchProviderException
// * @throws SignatureException
// */
// @Test
// @Ignore
// public void testPrivateKey() throws KeyStoreException, NoSuchAlgorithmException,
// CertificateException, IOException, InvalidKeyException, SignatureException,
// NoSuchProviderException {
// File file = new File("jetty.keystore");
// File privateFile = new File("jetty.keystore2");
// KeyStore keyStore = KeyStore.getInstance("jks");
// keyStore.load(new FileInputStream(file), "chaseEcho".toCharArray());
// CertAndKeyGen gen = new CertAndKeyGen("RSA", "SHA1WithRSA");
// gen.generate(1024);
// Key key = gen.getPrivateKey();
// X509Certificate cert = gen.getSelfCertificate(new X500Name("CN=ROOT"), (long) 365 * 24 * 3600);
// X509Certificate[] chain = new X509Certificate[1];
// chain[0] = cert;
// keyStore.setKeyEntry("rootKey", key, "morning".toCharArray(), chain);
// keyStore.store(new FileOutputStream(privateFile), "chaseEcho2".toCharArray());
// }
//
// /**
// * 存放证书
// *
// * @throws KeyStoreException
// * @throws NoSuchAlgorithmException
// * @throws CertificateException
// * @throws IOException
// * @throws InvalidKeyException
// * @throws SignatureException
// * @throws NoSuchProviderException
// */
// @Test
// @Ignore
// public void testCertificate() throws KeyStoreException, NoSuchAlgorithmException,
// CertificateException, IOException, InvalidKeyException, SignatureException,
// NoSuchProviderException {
// File file = new File("jetty.keystore");
// File certificateFile = new File("jetty.keystore3");
// KeyStore keyStore = KeyStore.getInstance("jks");
// keyStore.load(new FileInputStream(file), "chaseEcho".toCharArray());
// CertAndKeyGen gen = new CertAndKeyGen("RSA", "SHA1WithRSA");
// gen.generate(1024);
// X509Certificate cert = gen.getSelfCertificate(new X500Name("CN=ROOT"), (long) 365 * 24 * 3600);
//
// keyStore.setCertificateEntry("rootCertificate", cert);
//
// keyStore.store(new FileOutputStream(certificateFile), "chaseEcho3".toCharArray());
// }
//
// /**
// * 获取私钥
// *
// * @throws KeyStoreException
// * @throws NoSuchAlgorithmException
// * @throws CertificateException
// * @throws FileNotFoundException
// * @throws IOException
// */
// @Test
// @Ignore
// public void getPrivateKey() throws KeyStoreException, NoSuchAlgorithmException,
// CertificateException, FileNotFoundException, IOException {
// File file = new File("jetty.keystore2");
// KeyStore keyStore = KeyStore.getInstance("jks");
// keyStore.load(new FileInputStream(file), "chaseEcho2".toCharArray());
// Certificate[] chain = keyStore.getCertificateChain("rootKey");
// for (Certificate cert : chain) {
// System.out.println(cert.toString());
// }
// }
//
// /**
// * 获取证书
// *
// * @throws KeyStoreException
// * @throws NoSuchAlgorithmException
// * @throws CertificateException
// * @throws FileNotFoundException
// * @throws IOException
// */
// @Test
// @Ignore
// public void getCertificate() throws KeyStoreException, NoSuchAlgorithmException,
// CertificateException, FileNotFoundException, IOException {
// File file = new File("jetty.keystore3");
// KeyStore keyStore = KeyStore.getInstance("jks");
// keyStore.load(new FileInputStream(file), "chaseEcho3".toCharArray());
// Certificate cert = keyStore.getCertificate("rootCertificate");
// System.out.println(cert.toString());
// }
//
// }
