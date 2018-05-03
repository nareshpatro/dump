/* --------------------------------------
    File Name: SearchWorkOrder.java
    Author: Kin Shum (PCCW)
    Date: 1-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - SearchWorkOrder Bean
	- Store Work Order details

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.1>		20171114	Carmen Ng	Added support for dashboard overdue/upcoming
	<1.0>		20170901	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.model;

import java.util.List;

public class SearchWorkOrder {

	String woNumber;
	String assetNumber;
	String woType;
	List<String> woStatus;
	String eamOrg;
	String dateType;
	String dateFrom;
	String dateTo;
	String assetRisk;
	String maintenanceContract;
	String maintenanceVendor;
	String hiddenMBody;
	String serialNumber;
	String assetLocation;
	String assetOwner;
	String owningDept;
	String createdBy;
	boolean criticalOnly;
	boolean urgentOnly;
	String dashboardValue;
	String woMode;
	int queryLimit;
	
	public SearchWorkOrder() {
    }

	public String getWoNumber() {
		return woNumber;
	}

	public void setWoNumber(String woNumber) {
		this.woNumber = woNumber;
	}

	public String getAssetNumber() {
		return assetNumber;
	}

	public void setAssetNumber(String assetNumber) {
		this.assetNumber = assetNumber;
	}

	public String getWoType() {
		return woType;
	}

	public void setWoType(String woType) {
		this.woType = woType;
	}
	
	public List<String> getWoStatus() {
		return woStatus;
	}

	public void setWoStatus(List<String> woStatus) {
		this.woStatus = woStatus;
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
	
	public String getAssetRisk() {
		return assetRisk;
	}

	public void setAssetRisk(String assetRisk) {
		this.assetRisk = assetRisk;
	}
	
	public String getMaintenanceContract() {
		return maintenanceContract;
	}

	public void setMaintenanceContract(String maintenanceContract) {
		this.maintenanceContract = maintenanceContract;
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
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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
	
	public boolean getUrgentOnly() {
		return urgentOnly;
	}

	public void setUrgentOnly(boolean urgentOnly) {
		this.urgentOnly = urgentOnly;
	}
	
	public String getDashboardValue() {
		return dashboardValue;
	}

	public void setDashboardValue(String dashboardValue) {
		this.dashboardValue = dashboardValue;
	}
	
	public String getWoMode() {
		return woMode;
	}

	public void setWoMode(String woMode) {
		this.woMode = woMode;
	}
	
	public int getQueryLimit() {
		return queryLimit;
	}

	public void setQueryLimit(int queryLimit) {
		this.queryLimit = queryLimit;
	}
	
	@Override
	public String toString() {
		return "SearchWorkRequest [woNumber=" + woNumber + ", assetNumber=" + assetNumber + "]";
	}

	
}
