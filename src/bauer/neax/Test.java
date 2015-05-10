package bauer.neax;

import bauer.neax.dao.CallDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
        String pathToDAOConfig = args.length == 0 ? "DaoContext.xml" : args[0];
        ApplicationContext context = new ClassPathXmlApplicationContext(pathToDAOConfig);
        CallDAO callDAO = (CallDAO) context.getBean("CallDao");
        Config config = (Config) context.getBean("config");

        System.out.println(config.getPort());
        System.out.println(config.getMessageTemplate());
        System.out.println(config.getMailerPool());

    }



    private static String readToString(String input) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(input));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }


    /** Read the entire content of a Reader into a String */
    private static String readerToString(Reader is) throws IOException {

        StringBuilder sb = new StringBuilder();

        char[] b = new char[4096];

        int n;

        // Read a block. If it gets any chars, append them.

        while ((n = is.read(b)) > -1) {
            sb.append(b, 0, n);
        }

        // Only construct the String object once, here.

        return sb.toString();

    }

}
