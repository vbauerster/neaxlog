package bauer.neax.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Call {

	private String recordType;
	private int trunkRoute;
	private int trunkNo;
	private int tenantNo;
	private int extension;
	private String calledNumber;
	private String authCode;
	private Date callTime;
	private long duration; //duration of call in seconds
	
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public int getTrunkRoute() {
		return trunkRoute;
	}
	public void setTrunkRoute(int trunkRoute) {
		this.trunkRoute = trunkRoute;
	}
	public int getTrunkNo() {
		return trunkNo;
	}
	public void setTrunkNo(int trunkNo) {
		this.trunkNo = trunkNo;
	}
	public int getTenantNo() {
		return tenantNo;
	}
	public void setTenantNo(int tenantNo) {
		this.tenantNo = tenantNo;
	}
	public int getExtension() {
		return extension;
	}
	public void setExtension(int extension) {
		this.extension = extension;
	}
	public String getCalledNumber() {
		return calledNumber;
	}
	public void setCalledNumber(String calledNumber) {
		this.calledNumber = calledNumber;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode.length()==0 ? null : authCode;
	}
	public Date getCallTime() {
		return callTime;
	}
	public void setCallTime(Date callTime) {
		this.callTime = callTime;
	}
	
	/**
	 * 
	 * @return long Duration of call in seconds
	 */
	public long getDuration() {
		return duration;
	}
	
	/**
	 * 
	 * @param duration Duration of call in seconds
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public String toString(){
		
		SimpleDateFormat nTime = new SimpleDateFormat ("HH:mm:ss");
		nTime.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		StringBuilder call = new StringBuilder("{recordType=" + recordType);
		call.append(", trunkRoute=" + trunkRoute);
		call.append(", trunkNo=" + trunkNo);
		call.append(", tenantNo=" + tenantNo);
		call.append(", extension=" + extension);
		call.append(", calledNumber=" + calledNumber);
		call.append(", authCode=" + authCode);
		call.append(", callTime=" +  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(callTime));
		call.append(", duration=" + duration + " [" + nTime.format(new Date(duration*1000)) +"]}");
		
		return call.toString();
		
	}

}
