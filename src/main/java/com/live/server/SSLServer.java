package com.live.server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;

public class SSLServer extends Thread {
    private static final int SERVER_PORT = 12120;
    private static final String SERVER_KEYSTORE_PWD = "chaseecho";
    private static final String SERVER_TRUST_KEYSTORE_PWD = "admin123";

    private SSLServerSocket serverSocket;

    public SSLServer() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            KeyStore trustKeyStore = KeyStore.getInstance("JKS");
            try {
                String serverKeyUrl =
                        System.getProperty("user.dir") + "/src/main/resources/server.keystore";
                File serverFile = new File(serverKeyUrl);
                keyStore.load(new FileInputStream(serverFile), SERVER_KEYSTORE_PWD.toCharArray());
                String trustKeyUrl =
                        System.getProperty("user.dir")
                                + "/src/main/resources/trustOfServer.keystore";
                trustKeyStore.load(new FileInputStream(trustKeyUrl),
                        SERVER_TRUST_KEYSTORE_PWD.toCharArray());
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
                keyManagerFactory.init(keyStore, SERVER_KEYSTORE_PWD.toCharArray());
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

            // Set up Server Socket
            try {
                serverSocket =
                        (SSLServerSocket) context.getServerSocketFactory().createServerSocket(
                                SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket.setNeedClientAuth(true);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        if (serverSocket == null) {
            System.out.println("Null server socket");
            return;
        }
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                // Response To Client
                OutputStream output = socket.getOutputStream();
                BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
                bufferedOutput.write("Server Response: Hello".getBytes());
                bufferedOutput.flush();

                // Receive From Client
                InputStream input = socket.getInputStream();
                System.out.println("------Receive------");
                // use byte array to initialize the output string
                System.out.println(new String(StreamToByteArray(input)));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        System.out.println("=======Start Server !======");
        new SSLServer().start();
    }
}
