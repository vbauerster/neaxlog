package javacomm;

import java.io.*;
import java.util.*;
import gnu.io.*;

public class SimpleRead implements Runnable, SerialPortEventListener {
    static CommPortIdentifier portId;
    static Enumeration portList;

    InputStream inputStream;
//    BufferedReader inStream;
    SerialPort serialPort;
    Thread readThread;

    public static void main(String[] args) {
        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                 if (portId.getName().equals("COM2")) {
//                if (portId.getName().equals("/dev/term/a")) {
                    SimpleRead reader = new SimpleRead();
                }
            }
        }
    }

    public SimpleRead() {
        try {
            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
        } catch (PortInUseException e) {}
        try {
            inputStream = serialPort.getInputStream();
//            inStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        } catch (IOException e) {}
	try {
            serialPort.addEventListener(this);
	} catch (TooManyListenersException e) {}
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(19200,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_OUT);
           
//            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
        } catch (UnsupportedCommOperationException e) {}
        readThread = new Thread(this);
        readThread.start();
    }

    public void run() {
        try {
            Thread.sleep(10000); //sleep 10 sec
        } catch (InterruptedException e) {
        	System.out.println("Interrupting");
        	closePort();
        }
    }

	public void closePort() {
	    if (serialPort != null) {
	        try {
	        	System.out.println("Closing: " + serialPort.getName());
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
        	System.out.println("Some other than DATA_AVAILABLE serial event!");
            break;
        case SerialPortEvent.DATA_AVAILABLE:

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
//                    System.out.println("One line: " + len);
                    System.out.println(new String(buffer,0,len)); 
                    
                }catch (IOException e) {
                	e.printStackTrace();
                	System.exit(-1);
                	}
            break;
        }
    }
}
