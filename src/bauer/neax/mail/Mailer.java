package bauer.neax.mail;

import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;


public class Mailer implements Runnable {

	static final Logger logger = Logger.getLogger(Mailer.class);
//	private static final int DELAY = 1000;

    private static volatile Authenticator authenticator;

    /** The javamail session object. */
	protected Session session;
	/** The sender's email address */
	protected String from;
	/** The subject of the message. */
	protected String subject;
	/** The recipient ("To:"), as Strings. */
	protected ArrayList toList = new ArrayList();
	/** The CC list, as Strings. */
	protected ArrayList ccList = new ArrayList();
	/** The BCC list, as Strings. */
	protected ArrayList bccList = new ArrayList();
	/** The text of the message. */
	protected String body;
	/** The SMTP relay host */
	protected String SMTPServer;
    protected String SMTPPort;
    protected boolean SMTPUseSSL;
    protected boolean SMTPUseAuth;

	public Mailer(Properties p){
		  try{
		      SMTPServer = p.getProperty("SMTPServer");
		      SMTPPort = p.getProperty("SMTPPort");
		      SMTPUseSSL = Boolean.parseBoolean(p.getProperty("SMTPUseSSL"));
		      SMTPUseAuth = Boolean.parseBoolean(p.getProperty("SMTPUseAuth"));

		      from = p.getProperty("from");
		      setBccList(p.getProperty("bcc",""));
		      
			    // create some properties and get the default Session
			    Properties props = System.getProperties();
			    props.put("mail.smtp.host", SMTPServer);
			    props.put("mail.smtp.port", SMTPPort);
			    
			    if(SMTPUseSSL){
			    	props.put("mail.smtp.socketFactory.port", SMTPPort);
			    	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
			    }
			    
			    if (SMTPUseAuth){
			        props.put("mail.smtp.auth", "true");
                    if(authenticator==null){
                        String user = p.getProperty("user");
                        String encPwd = p.getProperty("pwd");
                        String keyFile = p.getProperty("keyfile");
                        authenticator = new SmtpAuthenticator(user, encPwd, keyFile);
                    }
				    session = Session.getInstance(props, authenticator);
			    }else{
			        session = Session.getInstance(props, null);
			    }
		     }catch (Exception e) {
		    	 logger.error("Error initializing mail properties: ", e);
		     }
	}
	
	
	private MimeMessage getMimeMessage() throws MessagingException, IllegalArgumentException{

		 if (!isComplete())
	     throw new IllegalArgumentException("Cannot get MimeMessage, before other params are set!");
		
	      // create a message
	      MimeMessage mesg = new MimeMessage(session);
	      // From Address
	      mesg.setFrom(new InternetAddress(from));
	      
	      InternetAddress[] addresses;

	      // TO Address list
	      addresses = new InternetAddress[toList.size()];
	      for (int i = 0; i < addresses.length; i++) {
	          addresses[i] = new InternetAddress((String) toList.get(i));
	      }
	      mesg.setRecipients(Message.RecipientType.TO, addresses);

	      // CC Address list
	      addresses = new InternetAddress[ccList.size()];
	      for (int i = 0; i < addresses.length; i++) {
	          addresses[i] = new InternetAddress((String) ccList.get(i));
	      }
	      mesg.setRecipients(Message.RecipientType.CC, addresses);

	      // BCC Address list
	      addresses = new InternetAddress[bccList.size()];
	      for (int i = 0; i < addresses.length; i++) {
	          addresses[i] = new InternetAddress((String) bccList.get(i));
	      }
	      mesg.setRecipients(Message.RecipientType.BCC, addresses);

	      mesg.setSubject(subject);
	      
//	      MimeBodyPart bp = new MimeBodyPart();
//	      bp.setContent(html_data, "text/html");
//	      
//	      // create the Multipart and add its parts to it
//	      Multipart mp = new MimeMultipart();
//	      mp.addBodyPart(bp);
//	      
//	      mesg.setContent(mp);
	      
	      mesg.setContent(body, "text/html");
		
		return mesg;
	}
	
	
	@Override
	public void run() {
        try {
            Transport.send(getMimeMessage());
//	      Thread.sleep(DELAY);
        } catch (MessagingException e) {
            logException(e);
        } catch (IllegalArgumentException e) {
            logException(e);
        }
//		catch (InterruptedException e) {
//        	logger.warn("Asked to interrupt mailer thread");
//        }
    }

    private void logException(Exception e){
        System.err.println("Mailer: " + e.getMessage());
        logger.error(e);
    }

    // SETTERS/GETTERS FOR TO: LIST
	 
 	/** Get tolist, as an array of Strings */
	public ArrayList getToList() {
		return toList;
	}

	/** Set to list to an ArrayList of Strings */
	public void setToList(ArrayList to) {
		toList = to;
	}
	/** Set to as a string like "tom, mary, robin@host". Loses any
	 * previously-set values. */
	public void setToList(String s) {
		toList = tokenize(s);
	}

	/** Add one "to" recipient */
	public void addTo(String to) {
		toList.add(to);
	}
        
	// SETTERS/GETTERS FOR CC: LIST 

	/** Get cclist, as an array of Strings */
	public ArrayList getCcList() {
		return ccList;
	}

	/** Set cc list to an ArrayList of Strings */
	public void setCcList(ArrayList cc) {
		ccList = cc;
	}

	/** Set cc as a string like "tom, mary, robin@host". Loses any
	 * previously-set values. */
	public void setCcList(String s) {
		ccList = tokenize(s);
	}

	/** Add one "cc" recipient */
	public void addCc(String cc) {
		ccList.add(cc);
	}

	// SETTERS/GETTERS FOR BCC: LIST 

	/** Get bcclist, as an array of Strings */
	public ArrayList getBccList() {
		return bccList;
	}

	/** Set bcc list to an ArrayList of Strings */
	public void setBccList(ArrayList bcc) {
		bccList = bcc;
	}

	/** Set bcc as a string like "tom, mary, robin@host". Loses any
	 * previously-set values. */
	public void setBccList(String s) {
		bccList = tokenize(s);
	}

	/** Add one "bcc" recipient */
	public void addBcc(String bcc) {
		bccList.add(bcc);
	}

	// SETTER/GETTER FOR MESSAGE BODY

	/** Get message */
	public String getBody() {
		return body;
	}

	/** Set message */
	public void setBody(String text) {
		body = text;
	}
        
       	/** Get subject */
	public String getSubject() {
		return subject;
	}

	/** Set subject */
	public void setSubject(String subj) {
		subject = subj;
	}
		
	/** Check if all required fields have been set before sending.
	 * Normally called e.g., by a JSP before calling doSend.
	 * Is also called by doSend for verification.
	 */
	public boolean isComplete() {
		if (from == null    || from.length()==0) {
			logger.error("*** Mailer ***");
			logger.error("No FROM");
			return false;
		}
		if (subject == null || subject.length()==0) {
			logger.error("*** Mailer ***");
			logger.error("No SUBJECT");
			return false;
		}
		if (toList.size()==0) {
			logger.error("*** Mailer ***");
			logger.error("No recipients");
			return false;
		}
		if (body == null || body.length()==0) {
			logger.error("*** Mailer ***");
			logger.error("No body");
			return false;
		}
		if (SMTPServer == null || SMTPServer.length()==0) {
			logger.error("*** Mailer ***");
			logger.error("No server host");
			return false;
		}
		return true;
	}
	
	/** Convert a list of addresses to an ArrayList. This will work
	 * for simple names like "tom, mary@foo.com, 123.45@c$.com"
	 * but will fail on certain complex (but RFC-valid) names like
	 * "(Darwin, Ian) <ian@darwinsys.com>".
	 * Or even "Ian Darwin <ian@darwinsys.com>".
	 */
	protected ArrayList tokenize(String s) {
		ArrayList al = new ArrayList();
		StringTokenizer tf = new StringTokenizer(s, ";");
		// For each word found in the line
		while (tf.hasMoreTokens()) {
			// trim blanks, and add to list.
			al.add(tf.nextToken().trim());
		}
		return al;
	}  	
}
