package com.belief.cer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class NginxSSL {
	static String BASE_DIR = System.getProperty("user.dir");
	static String KEY_PASS = "Kunion2014@!#";
	// static String HOST_ADDR = "www.kunion.com.cn";
	static String HOST_ADDR = "10.8.200.62";

	public static void main(String[] args) throws Exception {
		KeyStore ks = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(BASE_DIR + "/client.p12"));
		try {
			ks.load(instream, KEY_PASS.toCharArray());
		} finally {
			instream.close();
		}
		SSLSocketFactory ssf = null;
		SSLContext ctx = SSLContext.getInstance("TLS");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, KEY_PASS.toCharArray());

		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream caInput = new BufferedInputStream(new FileInputStream(BASE_DIR + "/server.crt"));
		Certificate ca;
		try {
			ca = cf.generateCertificate(caInput);
			System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
		} finally {
			caInput.close();
		}

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		KeyStore tks = KeyStore.getInstance(KeyStore.getDefaultType());
		tks.load(null, null);
		tks.setCertificateEntry("ca", ca);
		tmf.init(tks);
		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

		// /////////////////
		ssf = ctx.getSocketFactory();
		SSLSocket socket = (SSLSocket) ssf.createSocket(HOST_ADDR, 9443);
		System.out.println("create socket success.");
		socket.startHandshake();
		System.out.println("handshake success.");

		// /////////////////////
		URL url = new URL("https://" + HOST_ADDR + ":9443/rest/sys/config");

		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
		urlConnection.setSSLSocketFactory(ctx.getSocketFactory());
		InputStream input = urlConnection.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null) {
			result.append(line);
			result.append("\n");
		}
		System.out.println(result.toString());
	}
}
