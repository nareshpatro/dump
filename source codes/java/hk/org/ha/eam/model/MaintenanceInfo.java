/* --------------------------------------
    File Name: MaintenanceInfo.java
    Author: Carmen Ng (PCCW)
    Date: 27-Dec-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - MaintenanceInfo Bean

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171227	Carmen Ng	Initial version
   -------------------------------------- */

package hk.org.ha.eam.model;

public class MaintenanceInfo {

	private String assetNumber;
	private String maintenanceBodyNum;	
	private String maintenanceBody;
	private String maintenanceBodyType;
	private String contractNumber;
	private String maintContact;
	private String maintPhone;
	private String maintFax;
	private String maintEmail;
	private String autoSend;
	private String maintenancePlan;
	private String maintenanceJoinDate;
	private String maintenanceExpiryDate;
	private String supplierAgreementNumber;
	private String maintenanceInterval;
	private long maintenanceObjectId;
	private long maintenanceObjectType;
	
    public MaintenanceInfo() {
    }
	
	public String getAssetNumber() {
		return assetNumber;
	}
	public void setAssetNumber(String assetNumber) {
		this.assetNumber = assetNumber;
	}
		
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	
	public String getMaintenancePlan() {
		return maintenancePlan;
	}
	public void setMaintenancePlan(String maintenancePlan) {
		this.maintenancePlan = maintenancePlan;
	}
	
	public String getMaintenanceJoinDate() {
		return maintenanceJoinDate;
	}
	public void setMaintenanceJoinDate(String maintenanceJoinDate) {
		this.maintenanceJoinDate = maintenanceJoinDate;
	}
		
	public String getMaintenanceExpiryDate() {
		return maintenanceExpiryDate;
	}
	public void setMaintenanceExpiryDate(String maintenanceExpiryDate) {
		this.maintenanceExpiryDate = maintenanceExpiryDate;
	}
	
	public String getAutoSend() {
		return autoSend;
	}
	public void setAutoSend(String autoSend) {
		this.autoSend = autoSend;
	}
	
	public String getMaintContact() {
		return maintContact;
	}
	public void setMaintContact(String maintContact) {
		this.maintContact = maintContact;
	}
	
	public String getMaintPhone() {
		return maintPhone;
	}
	public void setMaintPhone(String maintPhone) {
		this.maintPhone = maintPhone;
	}
	
	public String getMaintFax() {
		return maintFax;
	}
	public void setMaintFax(String maintFax) {
		this.maintFax = maintFax;
	}
	
	public String getMaintEmail() {
		return maintEmail;
	}
	public void setMaintEmail(String maintEmail) {
		this.maintEmail = maintEmail;
	}
	
	public String getMaintenanceBody() {
		return maintenanceBody;
	}
	public void setMaintenanceBody(String maintenanceBody) {
		this.maintenanceBody = maintenanceBody;
	}
	
	public String getMaintenanceBodyNum() {
		return maintenanceBodyNum;
	}
	public void setMaintenanceBodyNum(String maintenanceBodyNum) {
		this.maintenanceBodyNum = maintenanceBodyNum;
	}
	
	public String getMaintenanceBodyType() {
		return maintenanceBodyType;
	}
	public void setMaintenanceBodyType(String maintenanceBodyType) {
		this.maintenanceBodyType = maintenanceBodyType;
	}
	
	public String getSupplierAgreementNumber() {
		return supplierAgreementNumber;
	}
	public void setSupplierAgreementNumber(String supplierAgreementNumber) {
		this.supplierAgreementNumber = supplierAgreementNumber;
	}
	
	public String getMaintenanceInterval() {
		return maintenanceInterval;
	}
	public void setMaintenanceInterval(String maintenanceInterval) {
		this.maintenanceInterval = maintenanceInterval;
	}
		
	public long getMaintenanceObjectId() {
		return maintenanceObjectId;
	}
	public void setMaintenanceObjectId(long maintenanceObjectId) {
		this.maintenanceObjectId = maintenanceObjectId;
	}
	
	public long getMaintenanceObjectType() {
		return maintenanceObjectType;
	}
	public void setMaintenanceObjectType(long maintenanceObjectType) {
		this.maintenanceObjectType = maintenanceObjectType;
	}
}

