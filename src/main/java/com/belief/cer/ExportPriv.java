// How to export the private key from keystore?
// Does keytool not have an option to do so?
// This example use the "testkeys" file that comes with JSSE 1.0.3
// Alexey Zilber: Ported to work with Base64Coder: http://www.source-code.biz/snippets/java/2.htm
// $Id: ExportPriv.java 10 2011-09-30 17:28:32Z mark@g.foster.cc $
// $URL: https://java-exportpriv.googlecode.com/svn/trunk/ExportPriv.java $
package com.belief.cer;
import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Vector;

class ExportPriv {
  public static void main(String args[]) throws Exception{
    if (args.length < 2) {
      //Yes I know this sucks (the password is visible to other users via ps
      // but this was a quick-n-dirty fix to export from a keystore to pkcs12
      // someday I may fix, but for now it'll have to do.
      System.err.println("Usage: java ExportPriv <keystore> <alias> <password>");
      System.exit(1);
    }
    ExportPriv myep = new ExportPriv();

    // System.out.println("Args: " + args[0] + " " + args[1] + " " + args[2]);
                

    myep.doit(args[0], args[1], args[2]);
  }

  public void doit(String fileName, String aliasName, String pass) throws Exception{

    KeyStore ks = KeyStore.getInstance("JKS");

    char[] passPhrase = pass.toCharArray();
    //BASE64Encoder myB64 = new BASE64Encoder();

    File certificateFile = new File(fileName);
    // System.out.println("certificateFile: " + certificateFile);

    ks.load(new FileInputStream(certificateFile), passPhrase);
    // System.out.println("ks: " + ks);
    // for(String a : ks.aliases()) {
    //   System.out.println("alias: " + a);
    // }

    KeyPair kp = getPrivateKey(ks, aliasName, passPhrase);
    // System.out.println("kp: " + kp);
    
    PrivateKey privKey = kp.getPrivate();

    char[] b64 = Base64Coder.encode(privKey.getEncoded());

    System.out.println("-----BEGIN PRIVATE KEY-----");
    for (String subSeq : splitArray(b64, 64)) {
        System.out.println(subSeq.toCharArray());
    }
    System.out.println("-----END PRIVATE KEY-----");
  }

// From http://javaalmanac.com/egs/java.security/GetKeyFromKs.html

  public KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {
    try {
      // Get private key
      Key key = keystore.getKey(alias, password);
      if (key instanceof PrivateKey) {
        // Get certificate of public key
        Certificate cert = keystore.getCertificate(alias);

        // Get public key
        PublicKey publicKey = cert.getPublicKey();
    
        // Return a key pair
        return new KeyPair(publicKey, (PrivateKey)key);
      }
    } catch (UnrecoverableKeyException e) {
    } catch (NoSuchAlgorithmException e) {
    } catch (KeyStoreException e) {
    }
    return null;
  }

  private Vector<String> splitArray(char[] chry, int subarrLen) {
    Vector<String> result = new Vector<String>();
    String input = new String(chry);
    int i = 0;
    while (i < chry.length) {
      result.add(input.substring(i, Math.min(input.length(), i + subarrLen)));
      i = i + subarrLen;
    }
    return result;
  }

}

