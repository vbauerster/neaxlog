package bauer.neax.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import org.apache.log4j.Logger;

import bauer.neax.dao.CallDAO;
import bauer.neax.domain.Call;
import bauer.neax.domain.Owner;

public class CallDAOImpl implements CallDAO{
	
	static final Logger logger = Logger.getLogger(CallDAOImpl.class);
	
	static final private String activePinsQuery = "select AuthCode, Alias from ACODE_OWNERS where Status=1";
	static final private String ownerQuery = "select * from OWNER_INFO where Alias=?";
	static final private String changePinQuery = "update ACODE_OWNERS set AuthCode=?, UpdatedBy=?, EvenTime=GETDATE() where AuthCode=?";
	static final private String ALIAS_BY_PIN = "select alias from ACODE_OWNERS where Status=1 and AuthCode=?";
	
	
	   private JdbcTemplate jdbcTemplate;
	   private SimpleJdbcCall procInsertCall;
	   private SimpleJdbcCall procInsertOwner;
	   private SimpleJdbcCall procDeactivatePin;
	   
	    public void setDataSource (DataSource dataSource){
	    	this.jdbcTemplate = new JdbcTemplate(dataSource);
	        this.procInsertCall = new SimpleJdbcCall(dataSource).withProcedureName("usp_insertCall");
	        this.procInsertOwner = new SimpleJdbcCall(dataSource).withProcedureName("usp_insertOwner");
	        this.procDeactivatePin = new SimpleJdbcCall(dataSource).withProcedureName("usp_deactivatePin");
	    }	   
	   
	    
	    public boolean insertCall(Call c){
	    		    	
	    	Map<String, Object> inputs = new HashMap<String, Object>(10);
	    	inputs.put("rType", c.getRecordType());
	    	inputs.put("trunkRoute", c.getTrunkRoute());
	    	inputs.put("trunkNo", c.getTrunkNo());
	    	inputs.put("tenantNo", c.getTenantNo());
	    	inputs.put("ext", c.getExtension());
	    	inputs.put("calledNum", c.getCalledNumber());
	    	inputs.put("authCode", c.getAuthCode());
	    	inputs.put("callTime", c.getCallTime());
	    	inputs.put("duration", c.getDuration());
	    	
//	    	SqlParameterSource in = new MapSqlParameterSource().addValues(inputs);
//	    	procInsertCall.execute(in);
	    	try{
	    		Map<String,Object> out = procInsertCall.execute(inputs);
	    		logger.debug("Out map returned by procInsertCall: " + out);
	    		return true;
	    	}catch (Exception e){
	    		logger.error("Cannot insert: " + c);
	    		logger.error("Exception: " + e.getMessage());
	    		return false;
	    	}
	    	
//	    	if(inserted){  //if call inserted and there is no such AuthCode in active hashmap(AuthCode, Alias)
//	    		//update active hashmap(AuthCode, Alias)   		 		
//	    	}
	    	
//	    	logger.info("----- OUT MAP -----");
//	    	logger.debug("Out map returned by procInsertCall: " + out);
//	    	Map out = procInsertCall.execute(in);
	    }

	    public boolean insertOwner(Map<String, String> owner){
	    	
	    	try{
	    		Map<String,Object> out = procInsertOwner.execute(owner);
	    		logger.debug("Out map returned by procInsertCall: " + out);
	    		return true;
	    	}catch (Exception e){
	    		logger.error("Cannot insert: " + owner);
	    		logger.error("Exception: ", e);
	    		//send mail about not inserted call
	    		return false;
	    	}	    	
	    	
	    }
	    
	    
	    public Map<String,Object> deactivatePin(Map<String, String> argMap){
	    	
	    	try{
	    		Map<String,Object> out = procDeactivatePin.execute(argMap);
	    		logger.debug("Out map returned by procDeactivatePin: " + out);
//	    		logger.info(out.get("alias"));
	    		return out;
	    	}catch (Exception e){
	    		logger.error("Cannot deactivate: " + argMap);
	    		logger.error("Exception: ", e);
	    		return null;
	    	}
	    	
	    }
	    
	    /**
	     * 
	     * @param args old pin, new pin, updated by
	     * @return rows affected
	     */
	    public int changePin(final String... args){
	    	
	    	if(args.length<3){
//	    		String[] temp = new String[args.length + 1];
//	    		System.arraycopy(args, 0, temp, 0, args.length);
//	    		args = temp;
	    		
	    		throw new IllegalArgumentException("changePin received less arguments than expected");
	    	}
	    	
	        int updateCounts = jdbcTemplate.update(changePinQuery,
	                new PreparedStatementSetter() {
						@Override
						public void setValues(PreparedStatement ps)
								throws SQLException {
														
							ps.setString(1, args[1]);// new pin
							ps.setString(2, args[2]);// updated by
							ps.setString(3, args[0]);//old pin
						}                   
	                } );
	        
	    	return updateCounts;
	    }
	    
	    public Owner getOwner(String alias){
	    	
	    	return jdbcTemplate.queryForObject(ownerQuery, new ownerMapper(), alias);
	    	
	    }
	    
	    public HashMap<String,String> getActivePins(){
	        return  (HashMap<String, String>) jdbcTemplate.query(activePinsQuery, new KeyValueExtractor());
	    }	    

	    public String findAlias(String pin){
	    	try{
	    	 return jdbcTemplate.queryForObject(ALIAS_BY_PIN, String.class, pin);
	    	}catch(EmptyResultDataAccessException e){	
			  return null;
			}
	    }
	    
	    private static final class KeyValueExtractor implements ResultSetExtractor {

	         public HashMap<String,String> extractData(ResultSet rs) throws SQLException, DataAccessException {
	             HashMap<String,String> h = new HashMap<String, String>();
	             while (rs.next()) {
	             h.put(rs.getString(1), rs.getString(2));
	             }
	            return h;
	         }
	     }	
	    
	    private static final class ownerMapper implements RowMapper<Owner> {
	    	
	    	public Owner mapRow(ResultSet rs, int rowNum) throws SQLException {
	    		
	    		Owner owner = new Owner();
	    		owner.setAlias(rs.getString("Alias"));
	    		owner.setFirstName(rs.getString("FirstName"));
	    		owner.setLastName(rs.getString("LastName"));
	    		owner.setMail(rs.getString("Mail"));
	    		owner.setNotify(rs.getBoolean("Notify"));
	    		
	    		return owner;
	    	}
	    	
	    }
}
