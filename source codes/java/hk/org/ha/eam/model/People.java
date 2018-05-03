package hk.org.ha.eam.model;

import java.util.Date;
import java.util.List;

public class People {

	private String empNumber;
	private String effDate;
	private String hkid;
	private String hkidName;
	private String chnName;
	private String lastName;
	private String firstName;
	private String fullName;
	private String maritalStatus;
	private String sex;
	private String title;
	
	private Date effStartDate;
	private Date effEndDate;
	private Date dOB;
	
	private int personId;
	private int personTypeId;
	
	public People() {
		
	}
	
	public People(String empNumber, String hkid, String hkidName, String chnName, String lastName,
			String firstName, String fullName, String maritalStatus, String sex, String title,
			Date effStartDate, Date effEndDate, Date dOB, int personId, int personTypeId) {
		this.empNumber=empNumber;
		this.hkid=hkid;
		this.hkidName=hkidName;
		this.chnName=chnName;
		this.lastName=lastName;
		this.firstName=firstName;
		this.fullName=fullName;
		this.maritalStatus=maritalStatus;
		this.sex=sex;
		this.title=title;
		this.effStartDate=effStartDate;
		this.effEndDate=effEndDate;
		this.dOB=dOB;
		this.personId=personId;
		this.personTypeId=personTypeId;
	}

	public String getEmpNumber() {
		return empNumber;
	}

	public void setEmpNumber(String empNumber) {
		this.empNumber = empNumber;
	}

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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getEffStartDate() {
		return effStartDate;
	}

	public void setEffStartDate(Date effStartDate) {
		this.effStartDate = effStartDate;
	}

	public Date getEffEndDate() {
		return effEndDate;
	}

	public void setEffEndDate(Date effEndDate) {
		this.effEndDate = effEndDate;
	}

	public Date getdOB() {
		return dOB;
	}

	public void setdOB(Date dOB) {
		this.dOB = dOB;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public int getPersonTypeId() {
		return personTypeId;
	}

	public void setPersonTypeId(int personTypeId) {
		this.personTypeId = personTypeId;
	}
	
	
	
	
	
	
}
