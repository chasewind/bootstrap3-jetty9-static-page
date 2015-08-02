package com.belief.client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class SSLClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 12120;
    private static final String CLIENT_KEYSTORE_PWD = "admin123";
    private static final String CLIENT_TRUST_KEYSTORE_PWD = "chaseecho";
    SSLSocket clientSocket;

    public SSLClient() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            KeyStore trustKeyStore = KeyStore.getInstance("JKS");
            try {
                String clientKeyUrl =
                        System.getProperty("user.dir") + "/src/main/resources/client.keystore";
                keyStore.load(new FileInputStream(clientKeyUrl), CLIENT_KEYSTORE_PWD.toCharArray());
                String trustKeyUrl =
                        System.getProperty("user.dir")
                                + "/src/main/resources/trustOfClient.keystore";
                trustKeyStore.load(new FileInputStream(trustKeyUrl),
                        CLIENT_TRUST_KEYSTORE_PWD.toCharArray());
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Initialize KeyStore Factory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            try {
                keyManagerFactory.init(keyStore, CLIENT_KEYSTORE_PWD.toCharArray());
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            }
            trustManagerFactory.init(trustKeyStore);

            // Initialize SSLContext
            SSLContext context = SSLContext.getInstance("SSL");
            try {
                context.init(keyManagerFactory.getKeyManagers(),
                        trustManagerFactory.getTrustManagers(), null);
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            // Set up Client Socket
            try {
                clientSocket =
                        (SSLSocket) context.getSocketFactory().createSocket(SERVER_HOST,
                                SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }


    private void Test() {
        if (clientSocket == null) {
            System.out.println("NULL clientSocket");
            return;
        }

        // Run Client Test
        InputStream input = null;
        OutputStream output = null;
        try {
            input = clientSocket.getInputStream();
            output = clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Output Message To Server
        BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
        try {
            // output to Server
            bufferedOutput.write("Client Test Running".getBytes());
            bufferedOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Output To Client Console
        try {
            System.out.println(new String(StreamToByteArray(input)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        // //close client socket
        // try {
        // clientSocket.close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }


    /**
     * convert stream to Byte Array
     * 
     * @param inputStream
     * @return
     * @throws IOException
     */
    public byte[] StreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int readIndex = inputStream.read(buffer);
        bout.write(buffer, 0, readIndex);
        bout.flush();
        bout.close();
        inputStream.close();
        return bout.toByteArray();
    }


    public static void main(String[] args) {
        new SSLClient().Test();
    }
}
