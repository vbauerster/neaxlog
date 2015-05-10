package bauer.neax;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import bauer.neax.domain.Call;


final public class CallParser {
	
	static final Logger logger = Logger.getLogger(CallParser.class);
	
	static final int TYPE_OF_RECORD[] = {4,1};
	static final int TRUNK_ROUTE[] = {5,3};
	static final int TRUNK_NO[] = {8,3};
	static final int TENANT_NO[] = {12,2};
	static final int EXT[] = {14,6};
	static final int YEAR_START[] = {116,2};
	static final int YEAR_END[] = {118,2};
	static final int TIME_START[] = {20,10};
	static final int TIME_END[] = {30,10};
	static final int CALLED_NUM[] = {62,32};
	static final int AUTH_CODE[] = {106,10};

    static final SimpleDateFormat nDate;
    static final SimpleDateFormat nTime;

    static{
        nDate = new SimpleDateFormat ("yyMMddHHmmss");
        nTime = new SimpleDateFormat ("HH:mm:ss");
        nTime.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

	private CallParser(){}
	

	public static Call parseCall(String rawCall){

        logger.debug("Parsing raw call string: " + rawCall);

        Call c = new Call();

        try{

        String recordType = new String(rawCall.substring(TYPE_OF_RECORD[0], TYPE_OF_RECORD[0] + TYPE_OF_RECORD[1]));
        c.setRecordType(recordType);
	    logger.debug("Type of record: " + c.getRecordType());

        String trunkRoute = new String(rawCall.substring(TRUNK_ROUTE[0], TRUNK_ROUTE[0] + TRUNK_ROUTE[1]));
        c.setTrunkRoute(Integer.parseInt(trunkRoute.trim()));
	    logger.debug("Trunk route: " + c.getTrunkRoute());

        String trunkNo = new String(rawCall.substring(TRUNK_NO[0], TRUNK_NO[0] + TRUNK_NO[1]));
        c.setTrunkNo(Integer.parseInt(trunkNo.trim()));
	    logger.debug("Trunk no: " + c.getTrunkNo());

        String tenantNo = new String(rawCall.substring(TENANT_NO[0], TENANT_NO[0] + TENANT_NO[1]));
        c.setTenantNo(Integer.parseInt(tenantNo.trim()));
	    logger.debug("Tenant no: " + c.getTenantNo());

        String extension = new String(rawCall.substring(EXT[0], EXT[0] + EXT[1]));
        c.setExtension(Integer.parseInt(extension.trim()));
	    logger.debug("Ext: " + c.getExtension());

        String dateStart = new String(rawCall.substring(YEAR_START[0], YEAR_START[0] + YEAR_START[1]));
        dateStart += new String(rawCall.substring(TIME_START[0], TIME_START[0] + TIME_START[1]));

        String dateEnd = new String(rawCall.substring(YEAR_END[0], YEAR_END[0] + YEAR_END[1]));
        dateEnd += new String(rawCall.substring(TIME_END[0], TIME_END[0] + TIME_END[1]));

	    Date startDate = nDate.parse(dateStart);
		Date endDate = nDate.parse(dateEnd);

	    c.setCallTime(startDate);
	    logger.debug("Call started: " + c.getCallTime());
        
	    c.setDuration((endDate.getTime() - startDate.getTime())/1000);
	    logger.debug("Duration: " + c.getDuration() + " [" + nTime.format(new Date(c.getDuration()*1000)) +"]");

        String calledNumber = new String(rawCall.substring(CALLED_NUM[0], CALLED_NUM[0] + CALLED_NUM[1]));
        c.setCalledNumber(calledNumber.trim());
	    logger.debug("Called number: " +  c.getCalledNumber());

        String authCode = new String(rawCall.substring(AUTH_CODE[0], AUTH_CODE[0] + AUTH_CODE[1]));
        c.setAuthCode(authCode.trim());
	    logger.debug("Auth code: " + c.getAuthCode());
	    
	    logger.debug("Call: " + c);

        } catch (ParseException e) {
            logger.error("Unparsable Date using " + nDate, e);
        } catch (NumberFormatException e){
            logger.error("Unparsable int", e);
        } catch (Exception e){
            logger.error("Unexpected exception", e);
        }

	    return c;
	}

}
