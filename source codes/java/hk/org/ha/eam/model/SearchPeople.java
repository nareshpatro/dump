/* --------------------------------------
    File Name: SearchWorkRequest.java
    Author: Kin Shum (PCCW)
    Date: 1-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - SearchWorkRequest Bean
	- Store details of Work Request

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170901	Kin Shum	Initial version
   -------------------------------------- */
   
package hk.org.ha.eam.model;

import java.util.List;

public class SearchPeople {

	String effDate;
	String hkid;
	String empNumber;
	String hkidName;
	String chnName;
	int personId;
	
	int queryLimit;

	
	public String getEffDate() {
		return effDate;
	}

	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}

	public String getHkid() {
		return hkid;
	}

	public void setHkid(String hkid) {
		this.hkid = hkid;
	}

	public String getEmpNumber() {
		return empNumber;
	}

	public void setEmpNumber(String empNumber) {
		this.empNumber = empNumber;
	}

	public String getHkidName() {
		return hkidName;
	}

	public void setHkidName(String hkidName) {
		this.hkidName = hkidName;
	}

	public String getChnName() {
		return chnName;
	}

	public void setChnName(String chnName) {
		this.chnName = chnName;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}
	
	public int getQueryLimit() {
		return queryLimit;
	}

	public void setQueryLimit(int queryLimit) {
		this.queryLimit = queryLimit;
	}

	@Override
	public String toString() {
		return "SearchPeople [empNumber=" + empNumber + ", hkid=" + hkid + "]";
	}

	
}
