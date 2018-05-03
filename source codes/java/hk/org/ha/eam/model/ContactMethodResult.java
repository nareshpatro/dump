/* --------------------------------------
    File Name: ContactMethodResult.java
    Author: Carmen Ng (PCCW)
    Date: 14-Dec-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Contact Method LOV model

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171214	Carmen Ng	Initial version
   -------------------------------------- */

package hk.org.ha.eam.model;

public class ContactMethodResult {

	String auto_send;
	String contact_person;
	String contact_phone;
	String contact_fax;
	String contact_email;
	
	public String getAuto_send() {
		return auto_send;
	}
	public void setAuto_send(String auto_send) {
		this.auto_send = auto_send;
	}
	public String getContact_person() {
		return contact_person;
	}
	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}
	public String getContact_phone() {
		return contact_phone;
	}
	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}
	public String getContact_fax() {
		return contact_fax;
	}
	public void setContact_fax(String contact_fax) {
		this.contact_fax = contact_fax;
	}
	public String getContact_email() {
		return contact_email;
	}
	public void setContact_email(String contact_email) {
		this.contact_email = contact_email;
	}
	
	
}
