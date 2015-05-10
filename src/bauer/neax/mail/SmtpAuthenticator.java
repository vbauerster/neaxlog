package bauer.neax.mail;

import java.io.File;
import java.io.IOException;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.apache.log4j.Logger;



public class SmtpAuthenticator extends Authenticator {
    
	static final Logger log = Logger.getLogger(SmtpAuthenticator.class);
	
    private String user;
    private String pwd;
    private String keyFile;


    public SmtpAuthenticator(String user, String encPwd, String keyFile){
       this.user = user;
       this.pwd = encPwd;
       this.keyFile = keyFile;
    }

  @Override
  public PasswordAuthentication getPasswordAuthentication() {
    try{
        pwd = CryptoUtils.decrypt(pwd, new File(keyFile));
    }catch (IOException e){
       log.error("Error reading key file: " + keyFile, e);
    }catch (java.security.GeneralSecurityException e){
      log.error("Decrypt error", e);
    }
    return new PasswordAuthentication(user, pwd);
  }

//    private static String xorMessage(String message, String key) {
//        try {
//            if (message == null || key == null) {
//                return null;
//            }
//            char[] keys = key.toCharArray();
//            char[] mesg = message.toCharArray();
//
//            int ml = mesg.length;
//            int kl = keys.length;
//            char[] newmsg = new char[ml];
//
//            for (int i = 0; i < ml; i++) {
//                newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
//            }//for i
//
//            mesg = null;
//            keys = null;
//            return new String(newmsg);
//        } catch (Exception e) {
//            return null;
//        }
//    }//xorMessage
}
