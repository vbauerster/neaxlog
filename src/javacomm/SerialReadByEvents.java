package javacomm;

import java.io.IOException;
import java.util.TooManyListenersException;

import gnu.io.*;
import javax.swing.JFrame;

/**
 * Read from a Serial port, notifying when data arrives.
 * Simulation of part of an event-logging service.
 * @version $Id: SerialReadByEvents.java,v 1.6 2005/06/24 20:04:16 ian Exp $
 * @author	Ian F. Darwin, http://www.darwinsys.com/
 */
public class SerialReadByEvents extends CommPortOpen 
	implements SerialPortEventListener {

	public static void main(String[] argv)
		throws IOException, NoSuchPortException, PortInUseException,
			UnsupportedCommOperationException {

		new SerialReadByEvents(null).converse();
	}

	/* Constructor */
	public SerialReadByEvents(JFrame f)
		throws IOException, NoSuchPortException, PortInUseException,
			UnsupportedCommOperationException {
		
		super(f);
	}

	/** 
	 * Hold the conversation. 
	 */
	protected void converse() throws IOException {

		System.out.println("In converse of SerialRead");
		if (!(thePort instanceof SerialPort)) {
			System.err.println("But I wanted a SERIAL port!");
			System.exit(1);
		}
		// Tell the Comm API that we want serial events.
		((SerialPort)thePort).notifyOnDataAvailable(true);
		try {
			((SerialPort)thePort).addEventListener(this);
		} catch (TooManyListenersException ev) {
			// "CantHappen" error
			System.err.println("Too many listeners(!) " + ev);
			System.exit(0);
		}
	
		
	}
	public void serialEvent(SerialPortEvent ev) {
		switch (ev.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			String line;
			try {
				line = is.readLine();
				if (line == null) {
					System.out.println("EOF on serial port.");
					System.exit(0);
				}
//				os.println(line);
				System.out.println(line);
			} catch (IOException ex) {
				System.err.println("IO Error " + ex);
			}
			break;
		default:
			System.out.println("Event type " + ev.getEventType());
		}
	}
}
