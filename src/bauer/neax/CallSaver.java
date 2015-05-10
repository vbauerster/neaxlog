package bauer.neax;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bauer.neax.dao.CallDAO;
import bauer.neax.domain.Call;

public class CallSaver {
	
	static final Logger logger = Logger.getLogger(CallSaver.class);
	
	private CallDAO callDAO;
	
	/**
	 * 
	 * @param daoContext path to spring dao initialization xml file
	 */
	public CallSaver(String daoContext){
		
		ApplicationContext context;
		try {
			context = new ClassPathXmlApplicationContext(daoContext);
			callDAO = (CallDAO) context.getBean("CallDAO");
			logger.debug("CallDAO initialized: " + callDAO);
		} catch (BeansException e) {
		 logger.error("Error loading spring dao: " + daoContext, e);
		 logger.warn("Initializing spring dao using default config file: DaoContext.xml");
		 context = new ClassPathXmlApplicationContext("DaoContext.xml");
		 callDAO = (CallDAO) context.getBean("CallDAO");
		 logger.debug("CallDAO initialized: " + callDAO);
		}
	}
	
	public void insert(Call c){
		callDAO.insertCall(c);
	}
	
}
