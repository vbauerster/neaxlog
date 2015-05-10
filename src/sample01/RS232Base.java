package sample01;

import java.util.Enumeration;
import gnu.io.CommPortIdentifier;

public class RS232Base {

	private Enumeration portList = null;
	private CommPortIdentifier portId = null;
	private String defaultPort = null;
	private boolean portFound = false;
	private int baudRate = 0;
	
	/*******************************
	* Constructor for the base class
	* ******************************/
	public RS232Base(String defaultPort, int baudRate){
		this.defaultPort = defaultPort;
		this.baudRate = baudRate;
	}
	/********************************
	 * Methode to check the presence
	 * of ports on this system
	 *******************************/
	public void checkPorts(){
		
		/***************************************
		 * Get a list of all ports on the system
		 **************************************/
		portList = CommPortIdentifier.getPortIdentifiers();
		System.out.println("List of all serial ports on this system:");
		
		while(portList.hasMoreElements()){
			portId = (CommPortIdentifier)portList.nextElement();
			if(portId.getName().equals(defaultPort)){
				portFound = true;
				System.out.println("Port found on: " + defaultPort);

				new SerialProgram(portId, baudRate);  // If port found, create a new class
			}	
		}
		
		if(!portFound){
			System.out.println("No serial port found!!!");
		}
	}
	
	
	
	public static void main(String[] args) {

		RS232Base serial = new RS232Base("/dev/ttyUSB0", 9600);
		serial.checkPorts();
	}

}

