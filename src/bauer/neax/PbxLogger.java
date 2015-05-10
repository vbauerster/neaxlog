package bauer.neax;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;


import bauer.neax.domain.Call;

public class PbxLogger {

    final static Logger logger = Logger.getLogger(PbxLogger.class);

    /**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {


		String fileName = args.length == 0 ? "PbxCollect01.log" : args[0];
		

        try {
    		BufferedReader is = new BufferedReader(new FileReader(fileName));
    		String inputLine;
    		
			while ((inputLine = is.readLine()) != null) {
                Call c = CallParser.parseCall(inputLine);
                System.out.println(c);
            }
            is.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
}
