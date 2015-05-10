package bauer.neax;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bauer.neax.dao.CallDAO;
import bauer.neax.domain.Call;
import bauer.neax.domain.Owner;
import bauer.neax.mail.Mailer;

public class CallCollector implements Runnable, SerialPortEventListener {

    static final Logger logger = Logger.getLogger(CallCollector.class);

    private static String port = "COM2";

    InputStream inputStream;
    SerialPort serialPort;
    Thread readThread;
    //Making some threads always available for Mailer
    private static ExecutorService mpool;

    private static CallDAO callDao;

    private static String bodyTlate;

    private static String adminMail;

    private static Properties mailProp = new Properties();

	public CallCollector(CommPortIdentifier portId) {

		/**********************
		 * Open the serial port
		 *********************/

		try {
			serialPort = (SerialPort) portId.open("neaxlog", 2000);
		} catch (PortInUseException e) {
			logger.error("Port in use: " + port);
		}
		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {
			logger.error("Cannot open InputStream from port: " + port, e);
		}
		try {
			serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_OUT);
			// serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
		} catch (UnsupportedCommOperationException e) {
			logger.error("Error setting port " + port + " parameters", e);
		}
		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			logger.error("Too Many Listeners on port: " + port);
		}
		serialPort.notifyOnDataAvailable(true);

		readThread = new Thread(this);
		readThread.start();
	}

    public void run() {
        try {
            Thread.sleep(10*1000); //sleep 10 sec
        } catch (InterruptedException e) {
        	logger.warn("Asked to interrupt read thread");
        	closePort();
        }
    }

	public void closePort() {
	    if (serialPort != null) {
	        try {
	        	logger.info("Closing: " + port);
	            // close the i/o streams.
	            inputStream.close();
	        } catch (IOException ex) {
	            // don't care
	        }
	        // Close the port.
	        serialPort.close();
	    }
	}

    public void serialEvent(SerialPortEvent event) {

        switch(event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                logger.warn("Some other than DATA_AVAILABLE serial event received!");
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                readLine();
                break;
            default:
                logger.warn("Unknown serial event received: " + event);
        }
    }

    private void readLine() {
        byte[] buffer = new byte[256]; // one line is 132 bytes

        try {
                int data;
                int len = 0;
                while ( ( data = inputStream.read()) > -1 )
                {
                    if ( data == '\n' ) { // or count 132 bytes
                        break;
                    }
                    buffer[len++] = (byte) data;
                }

                Call call = CallParser.parseCall(new String(buffer, 0, len));
                boolean inserted = callDao.insertCall(call);

                if(inserted){  //if call inserted and there is no such AuthCode in active hashmap(AuthCode, Alias)

                  logger.info("Inserted: " + call);

                  if(call.getAuthCode()!=null){

                    String alias = callDao.findAlias(call.getAuthCode());

                    if(alias!=null){
                         //send mail to alias@slb.com about call made
                        Owner owner = callDao.getOwner(alias);
                        if(owner.isNotify()){
                            Mailer m = new Mailer(mailProp);
                            m.addTo(owner.getMail());
                            m.setSubject("TM0011 PBX pin code used: " + call.getAuthCode());
                            m.setBody(getHtmlBody(call,
                                    owner.getFirstName() + " " + owner.getLastName()));
                            mpool.execute(m);
                        }
                    }else{
                        logger.warn("Alias not found in active map: " + call);
                    }
                  }
                }else{
                 //send email about not inserted call to admin
                    Mailer m = new Mailer(mailProp);
                    m.addTo(adminMail);
                    m.addCc("vbauerster@gmail.com");
                    m.setSubject("BauerNeax bot: UNKNOW PIN!");
                    m.setBody(call.toString());
                    mpool.execute(m);
                }

            }catch (IOException e) {
                logger.error("Error reading from " + port + ": ", e);
                e.printStackTrace();
            }
    }


    private String getHtmlBody(Call call, String fullName){

    	StringBuilder sb = new StringBuilder(bodyTlate);

    	String greetings = "Dear " + fullName;

    	greetings = greetings + "!<br><br>The message is to infrom you about below call made with your phone pin code." +
    							"<br>If you don't recognize the call, please send request to ithelp@slb.com to change the pin.";

    	sb.insert(sb.indexOf("<body>")+7, greetings);

    	 sb.append("<tbody><tr><td>");
    	 sb.append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(call.getCallTime()));
    	 sb.append("</td><td>");

    	 if(call.getTrunkRoute()==5){
    		 //sinet
    		 sb.append("SINET");
    	 }else{
    		 //landline
    		 sb.append("LANDLINE");
    	 }

		sb.append("</td><td>" + call.getExtension());
		sb.append("</td><td>" + call.getCalledNumber());

		SimpleDateFormat nTime = new SimpleDateFormat("HH:mm:ss");
		nTime.setTimeZone(TimeZone.getTimeZone("UTC"));

		sb.append("</td><td>" + nTime.format(new Date(call.getDuration() * 1000)));
		sb.append("</td></tr></tbody></table><br>");
		sb.append("To get complete report of calls, or disable mail notifications, visit the <a href=\"http://134.32.228.21/pbxaccess\">PBX Reporter</a>");
		sb.append("<br><br>Thank you,<br>The bauerneax bot</body></html>");
		logger.debug("*** HTML BODY ***");
		logger.debug(sb.toString());

    	return sb.toString();
    }



    private static String loadMessageTemplate(String input) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(input));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }


//    /** Read the entire content of a Reader into a String */
//    private String readerToString(Reader is) throws IOException {
//
//        StringBuilder sb = new StringBuilder();
//
//        char[] b = new char[4096];
//        int n;
//        // Read a block. If it gets any chars, append them.
//        while ((n = is.read(b)) > -1) {
//            sb.append(b, 0, n);
//        }
//        // Only construct the String object once, here.
//        return sb.toString( );
//    }


    public static void main(String[] args) {

        String pathToDAOConfig = args.length == 0 ? "DaoContext.xml" : args[0];
        ApplicationContext context = new ClassPathXmlApplicationContext(pathToDAOConfig);
        Config config = (Config) context.getBean("config");

        String messageTemplate = config.getMessageTemplate();
        try {
            bodyTlate = loadMessageTemplate(messageTemplate);
            logger.debug("Message template loaded: " + messageTemplate);
            mpool = Executors.newFixedThreadPool(config.getMailerPool());
            logger.debug("Mailer pool ExecutorService initialized with: " + config.getMailerPool());
            mailProp.load(new FileInputStream("mail.properties"));
        } catch (IOException e) {
            logger.error("Message template not found: " + messageTemplate, e);
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            logger.warn("Invalid Mailer Pool: " + config.getMailerPool());
            int p = 5;
            mpool = Executors.newFixedThreadPool(p);
            logger.debug("Mailer pool ExecutorService initialized with: " + p);
        }
        adminMail = config.getAdminMail();
        port = config.getPort();
        callDao = (CallDAO) context.getBean("callDao");
        logger.debug("CallDAO instanciated in main: " + callDao);

        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                 if (port.equalsIgnoreCase(portId.getName())) {
                    new CallCollector(portId);
                }
            }
        }
    }

}
