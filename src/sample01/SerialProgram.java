package sample01;

//the subclass serial:

import java.io.DataInputStream;
import java.io.IOException;
import java.util.TooManyListenersException;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;


public class SerialProgram implements Runnable{
	
	private CommPortIdentifier portId = null;
	private SerialPort serialPort = null;
	private DataInputStream is;
	private int baudRate = 0;
	
	private Thread readThread;
	
	
	
	/************************
	 * Constructor definition
	 ***********************/
	public SerialProgram(CommPortIdentifier portId, int baudRate){
		
		this.portId = portId;
		this.baudRate = baudRate;
		
		
		/**********************
		 * Open the serial port
		 *********************/
		try{
			serialPort = (SerialPort)portId.open("Artificial Horizont", 2000);
		} catch (PortInUseException ex){
			System.err.println("Port already in use!");
		}
		
		// Get input stream
		try{
			is = new DataInputStream(serialPort.getInputStream());
		} catch (IOException e){
			System.err.println("Cannot open Input Stream " + e);
			is =null;
		}
		
		
		try{
			serialPort.setSerialPortParams(baudRate,
					                       SerialPort.DATABITS_8,
					                       SerialPort.STOPBITS_1,
                                         SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException ex){
			System.err.println("Wrong settings for the serial port: " + ex.getMessage());
		}
		
		
		try{
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} catch (UnsupportedCommOperationException ex){
			System.err.println("Check the flow control setting: " + ex.getMessage());
		}
		
		// Add an event Listener
		try{
			serialPort.addEventListener(new SerialReader(is));
		} catch (TooManyListenersException ev){
			System.err.println("Too many Listeners! " + ev);
		}
		
		// Advise if data available to be read on the port
		serialPort.notifyOnDataAvailable(true);

		// Define a Thread for reading
		readThread = new Thread(this);
		readThread.start();
	}



	@Override
	public void run() {
		try {
		    Thread.sleep(20000);
		} catch (InterruptedException e) {}
	   
	}

}


