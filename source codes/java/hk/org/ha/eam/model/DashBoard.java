/* --------------------------------------
    File Name: DashBoard.java
    Author: Kin Shum (PCCW)
    Date: 1-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - DashBoard Bean

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170901	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.model;

public class DashBoard {

	String eamOrg;
	String eamOrgSaved;
	String riskLevel;

    String dashboardFor;
    String owningDept; 
    String assetLocation;
    String criticalMedEquip;
    String creationFrom;
    String creationTo;
    String statusType;
    String mBody;
    String scheduleFrom;
    String scheduleTo;
    String module;
    
	// WR Result
    String wrstatuscode;
	String wrstatus;
	String wrstatuscount;
	
	// WO Result
	String wostatus;
	String wostatuscount;
	String wostatuscode;
	
	// Outstanding WO
	String outstanding;
	String outstandingcount;
	
	// PM Summary
	String pmSummaryDesc;
	String pmSummaryCount;
	
	// PM WO Result
	String pmWostatus;
	String pmWostatuscount;

	// Upcoming WO Result
	String dateRange;
	String upcomingWoCount;
	String maintenanceVendor;
	
	// Maintenance Body
	String maintenanceVendorNumber;
	String maintenanceVendorCount;
	
	//Work Order Trend Results
	String trendMonth;
	String trendCount;
	
	public String getEamOrg() {
		return eamOrg;
	}

	public void setEamOrg(String eamOrg) {
		this.eamOrg = eamOrg;
	}
	
	public String getEamOrgSaved() {
		return eamOrgSaved;
	}

	public void setEamOrgSaved(String eamOrgSaved) {
		this.eamOrgSaved = eamOrgSaved;
	}
	
	public String getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	
	public String getDashboardFor() {
		return dashboardFor;
	}

	public void setDashboardFor(String dashboardFor) {
		this.dashboardFor = dashboardFor;
	}
	
	public String getOwningDept() {
		return owningDept;
	}

	public void setOwningDept(String owningDept) {
		this.owningDept = owningDept;
	}
	
	public String getAssetLocation() {
		return assetLocation;
	}

	public void setAssetLocation(String assetLocation) {
		this.assetLocation = assetLocation;
	}
	
	public String getCriticalMedEquip() {
		return criticalMedEquip;
	}

	public void setCriticalMedEquip(String criticalMedEquip) {
		this.criticalMedEquip = criticalMedEquip;
	}
	
	public String getCreationFrom() {
		return creationFrom;
	}

	public void setCreationFrom(String creationFrom) {
		this.creationFrom = creationFrom;
	}
	
	public String getCreationTo() {
		return creationTo;
	}

	public void setCreationTo(String creationTo) {
		this.creationTo = creationTo;
	}
	
	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}
	
	public String getMBody() {
		return mBody;
	}

	public void setMBody(String mBody) {
		this.mBody = mBody;
	}
	
	
	public String getScheduleFrom() {
		return scheduleFrom;
	}

	public void setScheduleFrom(String scheduleFrom) {
		this.scheduleFrom = scheduleFrom;
	}
	
	public String getScheduleTo() {
		return scheduleTo;
	}

	public void setScheduleTo(String scheduleTo) {
		this.scheduleTo = scheduleTo;
	}
	
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
	
	
	public String getWrstatuscode() {
		return wrstatuscode;
	}

	public void setWrstatuscode(String wrstatuscode) {
		this.wrstatuscode = wrstatuscode;
	}
	
	public String getWrstatus() {
		return wrstatus;
	}

	public void setWrstatus(String wrstatus) {
		this.wrstatus = wrstatus;
	}
	
	public String getWrstatuscount() {
		return wrstatuscount;
	}

	public void setWrstatuscount(String wrstatuscount) {
		this.wrstatuscount = wrstatuscount;
	}
	
	public String getWostatus() {
		return wostatus;
	}

	public void setWostatus(String wostatus) {
		this.wostatus = wostatus;
	}
	
	public String getWostatuscount() {
		return wostatuscount;
	}

	public void setWostatuscount(String wostatuscount) {
		this.wostatuscount = wostatuscount;
	}
	
	public String getWostatuscode() {
		return wostatuscode;
	}

	public void setWostatuscode(String wostatuscode) {
		this.wostatuscode = wostatuscode;
	}
	
	public String getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(String outstanding) {
		this.outstanding = outstanding;
	}
	
	public String getOutstandingcount() {
		return outstandingcount;
	}

	public void setOutstandingcount(String outstandingcount) {
		this.outstandingcount = outstandingcount;
	}
	
	public String getPmSummaryDesc() {
		return pmSummaryDesc;
	}

	public void setPmSummaryDesc(String pmSummaryDesc) {
		this.pmSummaryDesc = pmSummaryDesc;
	}
	
	public String getPmSummaryCount() {
		return pmSummaryCount;
	}

	public void setPmSummaryCount(String pmSummaryCount) {
		this.pmSummaryCount = pmSummaryCount;
	}
	
	public String getPmWostatus() {
		return pmWostatus;
	}

	public void setPmWostatus(String pmWostatus) {
		this.pmWostatus = pmWostatus;
	}
	
	public String getPmWostatuscount() {
		return pmWostatuscount;
	}

	public void setPmWostatuscount(String pmWostatuscount) {
		this.pmWostatuscount = pmWostatuscount;
	}
	
	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}
	
	public String getUpcomingWoCount() {
		return upcomingWoCount;
	}

	public void setUpcomingWoCount(String upcomingWoCount) {
		this.upcomingWoCount = upcomingWoCount;
	}
	
	public String getMaintenanceVendor() {
		return maintenanceVendor;
	}

	public void setMaintenanceVendor(String maintenanceVendor) {
		this.maintenanceVendor = maintenanceVendor;
	}

	public String getMaintenanceVendorNumber() {
		return maintenanceVendorNumber;
	}

	public void setMaintenanceVendorNumber(String maintenanceVendorNumber) {
		this.maintenanceVendorNumber = maintenanceVendorNumber;
	}
	
	public String getMaintenanceVendorCount() {
		return maintenanceVendorCount;
	}

	public void setMaintenanceVendorCount(String maintenanceVendorCount) {
		this.maintenanceVendorCount = maintenanceVendorCount;
	}
	
	public String getTrendMonth() {
		return trendMonth;
	}

	public void setTrendMonth(String trendMonth) {
		this.trendMonth = trendMonth;
	}
	
	public String getTrendCount() {
		return trendCount;
	}

	public void setTrendCount(String trendCount) {
		this.trendCount = trendCount;
	}

}
