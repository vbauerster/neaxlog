package bauer.neax.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import bauer.neax.domain.Call;
import bauer.neax.domain.Owner;

public interface CallDAO {
	
	   /** 
	    * This is the method to be used to initialize
	    * database resources ie. connection.
	    */
//	   public void setDataSource(DataSource ds);
	   /** 
	    * This is the method to be used to insert
	    * a record in the Calls table.
	    */
	   public boolean insertCall(Call c);
	   
	   public boolean insertOwner(Map<String, String> owner);
	   
	   public Map<String,Object> deactivatePin(Map<String, String> argMap);
	   
	    /**
	     * 
	     * @param args old pin, new pin, updated by
	     * @return rows affected
	     */
	   public int changePin(final String... args);
	   
	   public Owner getOwner(String alias);
	   
	   public HashMap<String,String> getActivePins();
	   
	   public String findAlias(String pin);

}
