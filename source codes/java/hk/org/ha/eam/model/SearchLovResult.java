/* --------------------------------------
    File Name: SearchLovResult.java
    Author: Kin Shum (PCCW)
    Date: 1-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - SearchLovResult Bean

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170901	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.model;

public class SearchLovResult {

	String vendor_number;
	String vendor_name;
	String locationcode;
	String description1;
	String address;
	String assetowner;
	String description2;
	String department_code;
	String description3;
	String user_name;
	String full_name;
	String assetNumber;
	String assetDescription;
	String contract_num;
	
	public String getVendor_number() {
		return vendor_number;
	}

	public void setVendor_number(String vendor_number) {
		this.vendor_number = vendor_number;
	}

	public String getVendor_name() {
		return vendor_name;
	}

	public void setVendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}

	public String getLocationcode() {
		return locationcode;
	}

	public void setLocationcode( String locationcode) {
		this.locationcode = locationcode;
	}
	
	public String getDescription1() {
		return description1;
	}

	public void setDescription1(String description1) {
		this.description1 = description1;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress( String address) {
		this.address = address;
	}
	
	public String getAssetowner() {
		return assetowner;
	}

	public void setAssetowner(String assetowner) {
		this.assetowner = assetowner;
	}
	
	public String getDescription2() {
		return description2;
	}

	public void setDescription2( String description2) {
		this.description2 = description2;
	}
	
	public String getDepartment_code() {
		return department_code;
	}

	public void setDepartment_code(String department_code) {
		this.department_code = department_code;
	}
	
	public String getDescription3() {
		return description3;
	}

	public void setDescription3(String description3) {
		this.description3 = description3;
	}
	
	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}
	
	public String getAssetNumber() {
		return assetNumber;
	}

	public void setAssetNumber( String assetNumber) {
		this.assetNumber = assetNumber;
	}
	
	public String getAssetDescription() {
		return assetDescription;
	}

	public void setAssetDescription( String assetDescription) {
		this.assetDescription = assetDescription;
	}
	
	public String getContract_num() {
		return contract_num;
	}

	public void setContract_num( String contract_num) {
		this.contract_num = contract_num;
	}
	
	@Override
	public String toString() {
		return "SearchLovResult [vendor_number=" + vendor_number + ", vendor_name=" + vendor_name + ", locationcode=" + locationcode  + ", locationcode=" + locationcode + "]";
	}

	
}
