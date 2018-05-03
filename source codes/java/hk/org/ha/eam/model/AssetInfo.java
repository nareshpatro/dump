/* --------------------------------------
    File Name: AssetInfo.java
    Author: Kin Shum (PCCW)
    Date: 1-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - AssetInfo Bean

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170901	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.model;

public class AssetInfo {

	private String assetDescription;
	private long assetGroup;
	private String assetGroupName;
	private String assetNumber;
	private String assetOrganisation;
	private String assetOwner;
	private String assetOwnerDesc;
	private String owningDept;
	private String assetStatus;
	private String dob;
	private String serialnumber;
	private String assetlocation;
	private String assetLocationDesc;
	private String purchasePrice;
	private String assetSupplier;
	private String itemCodeNumber;
	private String fatherAssetNumber;
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
	private String assetOwningDepartmentDesc;
    private String manufacturer;
    private String brand;
    private String model;
	private String riskLevel;
	private long assetOwningDepartmentId;
	private long setAssetOrgId;
	private long maintenanceObjectId;
	private long maintenanceObjectType;
	private String legacySerialNumber;
	
    public AssetInfo() {
    }
 
    public AssetInfo(String assetDescription, long assetGroup, String assetNumber, String assetOrganisation, String assetOwner, String assetOwnerDesc, String owningDept, String assetStatus, String dob, String serialnumber
    		, String assetlocation, String assetLocationDesc, String purchasePrice, String assetSupplier, String itemCodeNumber, String fatherAssetNumber
    		, String maintenanceBody, String maintenanceBodyType, String contractNumber, String maintContact, String maintPhone, String maintFax
    		, String maintEmail, String autoSend, String maintenancePlan, String maintenanceJoinDate, String maintenanceExpiryDate, String supplierAgreementNumber, String maintenanceInterval, String assetOwningDepartmentDesc,
    		String manufacturer, String brand, String model, String riskLevel, long assetOwningDepartmentId, long setAssetOrgId, long maintenanceObjectId, long maintenanceObjectType) {
            this.assetDescription = assetDescription;
            this.assetGroup = assetGroup;
            this.assetNumber = assetNumber;
            this.assetOrganisation = assetOrganisation;
            this.assetOwner = assetOwner;
            this.assetOwnerDesc = assetOwnerDesc;
            this.owningDept = owningDept;
            this.assetStatus = assetStatus;
            this.dob = dob;
            this.serialnumber = serialnumber;
            this.assetlocation = assetlocation;
            this.assetLocationDesc = assetLocationDesc;
            this.purchasePrice = purchasePrice;
            this.assetSupplier = assetSupplier;
            this.itemCodeNumber = itemCodeNumber;
            this.fatherAssetNumber = fatherAssetNumber;
            
            this.maintenanceBody = maintenanceBody;
            this.maintenanceBodyType = maintenanceBodyType;
            this.contractNumber = contractNumber;
            this.maintContact = maintContact;
            this.maintPhone = maintPhone;
            this.maintFax = maintFax;
            this.maintEmail = maintEmail;
            this.autoSend = autoSend;
            this.maintenancePlan = maintenancePlan;
            this.maintenanceJoinDate = maintenanceJoinDate;
            this.maintenanceExpiryDate = maintenanceExpiryDate;
            this.supplierAgreementNumber = supplierAgreementNumber;
            this.maintenanceInterval = maintenanceInterval;
            this.assetOwningDepartmentDesc = assetOwningDepartmentDesc;
            this.manufacturer = manufacturer;
            this.model = model;
            this.brand = brand;
            this.riskLevel = riskLevel;
            this.assetOwningDepartmentId = assetOwningDepartmentId;
            this.setAssetOrgId = setAssetOrgId;
            this.maintenanceObjectId = maintenanceObjectId;
            this.maintenanceObjectType = maintenanceObjectType;
            
	}
    
	public String getAssetDescription() {
		return assetDescription;
	}
	public void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}
	
	public long getAssetGroup() {
		return assetGroup;
	}
	public void setAssetGroup(long assetGroup) {
		this.assetGroup = assetGroup;
	}
	
	public String getAssetGroupName() {
		return assetGroupName;
	}
	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}
	
	public String getAssetNumber() {
		return assetNumber;
	}
	public void setAssetNumber(String assetNumber) {
		this.assetNumber = assetNumber;
	}
	
	public String getAssetOrganisation() {
		return assetOrganisation;
	}
	public void setAssetOrganisation(String assetOrganisation) {
		this.assetOrganisation = assetOrganisation;
	}
	
	public String getAssetOwner() {
		return assetOwner;
	}
	public void setAssetOwner(String assetOwner) {
		this.assetOwner = assetOwner;
	}
	
	public String getAssetOwnerDesc() {
		return assetOwnerDesc;
	}
	public void setAssetOwnerDesc(String assetOwnerDesc) {
		this.assetOwnerDesc = assetOwnerDesc;
	}
	
	public String getOwningDept() {
		return owningDept;
	}
	public void setOwningDept(String owningDept) {
		this.owningDept = owningDept;
	}
	
	public String getAssetStatus() {
		return assetStatus;
	}
	public void setAssetStatus(String assetStatus) {
		this.assetStatus = assetStatus;
	}
	
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	public String getSerialnumber() {
		return serialnumber;
	}
	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}
	
	public String getAssetlocation() {
		return assetlocation;
	}
	public void setAssetlocation(String assetlocation) {
		this.assetlocation = assetlocation;
	}
	
	public String getAssetLocationDesc() {
		return assetLocationDesc;
	}
	public void setAssetLocationDesc(String assetLocationDesc) {
		this.assetLocationDesc = assetLocationDesc;
	}
	
	public String getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	
	public String getAssetSupplier() {
		return assetSupplier;
	}
	public void setAssetSupplier(String assetSupplier) {
		this.assetSupplier = assetSupplier;
	}
	
	public String getItemCodeNumber() {
		return itemCodeNumber;
	}
	public void setItemCodeNumber(String itemCodeNumber) {
		this.itemCodeNumber = itemCodeNumber;
	}
	
	public String getFatherAssetNumber() {
		return fatherAssetNumber;
	}
	public void setFatherAssetNumber(String fatherAssetNumber) {
		this.fatherAssetNumber = fatherAssetNumber;
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
	
	public String getAssetOwningDepartmentDesc() {
		return assetOwningDepartmentDesc;
	}
	public void setAssetOwningDepartmentDesc(String assetOwningDepartmentDesc) {
		this.assetOwningDepartmentDesc = assetOwningDepartmentDesc;
	}
	
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}	
	
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}	
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}	
	
	public String getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	
	public long getAssetOwningDepartmentId() {
		return assetOwningDepartmentId;
	}
	public void setAssetOwningDepartmentId(long assetOwningDepartmentId) {
		this.assetOwningDepartmentId = assetOwningDepartmentId;
	}
	
	public long getAssetOrgId() {
		return setAssetOrgId;
	}
	public void setAssetOrgId(long setAssetOrgId) {
		this.setAssetOrgId = setAssetOrgId;
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
	
	public String getLegacySerialNumber() {
		return legacySerialNumber;
	}
	public void setLegacySerialNumber(String legacySerialNumber) {
		this.legacySerialNumber = legacySerialNumber;
	}
	
	
}

