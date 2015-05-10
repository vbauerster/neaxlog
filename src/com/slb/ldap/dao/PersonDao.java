package com.slb.ldap.dao;

import java.util.List;

import com.slb.ldap.domain.Person;

public interface PersonDao {

//	public List getAllContactNames();

    public List<Person> getPersonByCN(String cn);

	public Person getPersonByAlias(String alias);

    public Person getPersonByBadgeKey(String Key);
    
    public Person getPersonByGin(String Key);
    
    public List<Person> getReporters(String dn);

	/**
	 * 
	 * @param alias User Name
	 * @param password Password
	 * @param b boolean var args
	 * 1st boolean if true, password check bypassed;
	 * 2nd boolean if true, auth person is not get from ldap, but dummy one inserted
	 */  
    boolean authenticate(final String alias, final String password, boolean ... b);
    
	public boolean isPassNoCheck();

	public void setPassNoCheck(boolean passNoCheck);

	public boolean isLdapNoCheck();

	public void setLdapNoCheck(boolean ldapNoCheck);
    
//    public void setDmode(boolean dmode);

//	public boolean isDmode();
    

 }
