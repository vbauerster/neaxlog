package sample01;


import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class SerialReader implements SerialPortEventListener{

	private BufferedReader inStream;
	
	
	// Constructor
	public SerialReader(InputStream is){
		inStream = new BufferedReader(new InputStreamReader(is));
	}
	
	
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		
		String rawInput = null;
		
		switch(event.getEventType()){
		case SerialPortEvent.BI:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.FE:
		case SerialPortEvent.OE:
		case SerialPortEvent.PE:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
			
		case SerialPortEvent.DATA_AVAILABLE:
			try {
				rawInput = inStream.readLine();
				if(rawInput == null){
					System.out.println("No input on serial port");
					System.exit(0);
				}
				
				System.out.println(rawInput);
				
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			break;
			
		default:
			break;
			
		}
				
	}
}
