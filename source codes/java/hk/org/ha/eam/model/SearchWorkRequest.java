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

public class SearchWorkRequest {

	String wrNumber;
	String assetNumber;
	String wrType;
	List<String> wrStatus;
	String eamOrg;
	String dateType;
	String dateFrom;
	String dateTo;
	String maintenanceVendor;
	String hiddenMBody;
	String assetLocation;
	String assetOwner;
	String owningDept;
	String createdBy;
	boolean criticalOnly;
	String mBody;
	String riskLevel;
	String statusType;
	int queryLimit;
	
	public String getWrNumber() {
		return wrNumber;
	}

	public void setWrNumber(String wrNumber) {
		this.wrNumber = wrNumber;
	}

	public String getAssetNumber() {
		return assetNumber;
	}

	public void setAssetNumber(String assetNumber) {
		this.assetNumber = assetNumber;
	}

	public String getWrType() {
		return wrType;
	}

	public void setWrType(String wrType) {
		this.wrType = wrType;
	}
	
	public List<String> getWrStatus() {
		return wrStatus;
	}

	public void setWrStatus(List<String> wrStatus) {
		this.wrStatus = wrStatus;
	}
	
	public String getEamOrg() {
		return eamOrg;
	}

	public void setEamOrg(String eamOrg) {
		this.eamOrg = eamOrg;
	}
	
	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}
	
	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	
	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getMaintenanceVendor() {
		return maintenanceVendor;
	}

	public void setMaintenanceVendor(String maintenanceVendor) {
		this.maintenanceVendor = maintenanceVendor;
	}
	
	public String getHiddenMBody() {
		return  hiddenMBody;
	}

	public void setHiddenMBody(String  hiddenMBody) {
		this. hiddenMBody =  hiddenMBody;
	}
	
	public String getAssetLocation() {
		return assetLocation;
	}

	public void setAssetLocation(String assetLocation) {
		this.assetLocation = assetLocation;
	}
	
	public String getAssetOwner() {
		return assetOwner;
	}

	public void setAssetOwner(String assetOwner) {
		this.assetOwner = assetOwner;
	}
	
	public String getOwningDept() {
		return owningDept;
	}

	public void setOwningDept(String owningDept) {
		this.owningDept = owningDept;
	}	
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}	
	
	public boolean getCriticalOnly() {
		return criticalOnly;
	}

	public void setCriticalOnly(boolean criticalOnly) {
		this.criticalOnly = criticalOnly;
	}
	
	public String getMBody() {
		return mBody;
	}

	public void setMBody(String mBody) {
		this.mBody = mBody;
	}
	
	public String getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	
	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}
	
	public int getQueryLimit() {
		return queryLimit;
	}

	public void setQueryLimit(int queryLimit) {
		this.queryLimit = queryLimit;
	}
	
	@Override
	public String toString() {
		return "SearchWorkRequest [wrNumber=" + wrNumber + ", assetNumber=" + assetNumber + "]";
	}

	
}
