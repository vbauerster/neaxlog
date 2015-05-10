package javacomm;

import java.io.*;

import gnu.io.*;

import java.util.*;

/**
 * Read from multiple Serial ports, notifying when data arrives on any.
 * @version $Id: SerialLogger.java,v 1.5 2004/09/08 20:13:03 ian Exp $
 * @author	Ian F. Darwin, http://www.darwinsys.com/
 */
public class SerialLogger {

	public static void main(String[] argv)
		throws IOException, NoSuchPortException, PortInUseException,
			UnsupportedCommOperationException {

		new SerialLogger();
	}

	/* Constructor */
	public SerialLogger()
		throws IOException, NoSuchPortException, PortInUseException,
			UnsupportedCommOperationException {
		
		// get list of ports available on this particular computer,
		// by calling static method in CommPortIdentifier.
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();

		// Process the list, processing only serial ports.
		while (pList.hasMoreElements()) {
			CommPortIdentifier cpi = (CommPortIdentifier)pList.nextElement();
			String name = cpi.getName();
			System.out.print("Port " + name + " ");
			if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				System.out.println("is a Serial Port: " + cpi);

				SerialPort thePort;
				try {
					thePort = (SerialPort)cpi.open("Logger", 1000);
					thePort.setSerialPortParams(19200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
					thePort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
				} catch (PortInUseException ev) {
					System.err.println("Port in use: " + name);
					continue;
				}

				// Tell the Comm API that we want serial events.
				thePort.notifyOnDataAvailable(true);
				try {
					thePort.addEventListener(new Logger(cpi.getName(), thePort));
//					thePort.addEventListener(new COMReader(cpi.getName(), thePort));
				} catch (TooManyListenersException ev) {
					// "CantHappen" error
					System.err.println("Too many listeners(!) " + ev);
					System.exit(0);
				}
			}
		}
	}

	/** Handle one port. */
	public class Logger implements SerialPortEventListener { 
		String portName;
		SerialPort thePort;
		BufferedReader inStream;
		
		public Logger(String name, SerialPort port) throws IOException {
			portName = name;
			thePort = port;
			// Make a reader for the input file.
			inStream = new BufferedReader(
				new InputStreamReader(thePort.getInputStream()));
		}
		public void serialEvent(SerialPortEvent ev) {
		
			String rawInput = null;
			
			switch(ev.getEventType()){
			case SerialPortEvent.BI:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.FE:
			case SerialPortEvent.OE:
			case SerialPortEvent.PE:
			case SerialPortEvent.RI:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				System.out.println("Other serial event");
				break;
				
			case SerialPortEvent.DATA_AVAILABLE:
				try {
					rawInput = inStream.readLine();
					if(rawInput == null){
						System.out.println("No input on serial port");
						System.exit(0);
					}
					
					System.out.println(portName + ": " + rawInput);
					
				} catch (IOException e) {
					System.out.println("InLogger IO ex");
					e.printStackTrace();
					System.exit(-1);
				}
				break;
				
			default:
				break;
				
			}			

		}
	}
	
    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example. 
     */
    public static class COMReader implements SerialPortEventListener 
    {
		private String portName;
		private SerialPort thePort;
        private InputStream in;
        private byte[] buffer = new byte[1024];
        
        public COMReader ( String name, SerialPort port ) throws IOException
        {
           portName = name;
           thePort = port;
           
           in = thePort.getInputStream();
        }
        
        public void serialEvent(SerialPortEvent arg0) {
            int data;
          
            try
            {
                int len = 0;
                while ( ( data = in.read()) > -1 )
                {
                    if ( data == '\n' ) {
                        break;
                    }
                    buffer[len++] = (byte) data;
                }
                System.out.print(portName + ": " + new String(buffer,0,len));
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                System.exit(-1);
            }             
        }

    }	
}
