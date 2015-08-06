package com.belief.cer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLConnector {
	static String KS_FILE = "./src/main/resources/client.p12";
	static String KS_PASSWORD = "admin123";
	static String TRUST_FILE = "./src/main/resources/server.keystore";
	static String TRUST_PASSWORD = "chaseecho";
	static String HOST_ADDR = "10.8.200.62";

	public static void main(String[] args) throws Exception {
		KeyStore ks = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(KS_FILE));
		try {
			ks.load(instream, KS_PASSWORD.toCharArray());
		} finally {
			instream.close();
		}
		SSLSocketFactory ssf = null;
		SSLContext ctx = SSLContext.getInstance("TLS");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		kmf.init(ks, KS_PASSWORD.toCharArray());
		KeyStore tks = KeyStore.getInstance("JKS");
		tks.load(new FileInputStream(TRUST_FILE), TRUST_PASSWORD.toCharArray());
		tmf.init(tks);
		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

		// /////////////////
		ssf = ctx.getSocketFactory();
		SSLSocket socket = (SSLSocket) ssf.createSocket(HOST_ADDR, 8443);
		System.out.println("create socket success.");
		// handshake
		socket.startHandshake();
		System.out.println("handshake success.");

		// /////////////////////
		URL url = new URL("https://" + HOST_ADDR + ":8443/index.html");

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
