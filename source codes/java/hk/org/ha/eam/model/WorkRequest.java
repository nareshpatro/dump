/* --------------------------------------
    File Name: WorkRequest.java
    Author: Kin Shum (PCCW)
    Date: 1-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - WorkRequest Bean

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170901	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.model;

import java.util.Date;
import java.util.List;

public class WorkRequest {
	
	private String wrNumber;
	private String assetNumber;
	private String wrType;
	private String wrStatus;
	private String eamOrg;
	private String dateType;
	private String dateFrom;
	private String dateTo;
	private String maintenanceVendor;
	private String assetLocation;
	private String assetOwner;
	private String owningDept;
//	private String createdBy;
	
    private String workRequestNumber;	
	private String assetDescription;
	private String workRequestType;
	private String workRequestStatus;
    private String woNumber;
    private String woStatus;
    private String cmBreakdownDate;
    private String pmScheduleDate;
    private String createdby;
    private String maintenancevendor;
    private String assetlocation;
    private String assetowner;
    private String owningdept;
    private Date createddate;
    private String serialnumber;
    private String manufacturer;
    private String brand;
    private String model;
    
	private Date wrCreatedDate;
	private String wrCreatedBy;
	private String haContactPerson;
	private String haContactPhone;
	private String haContactEmail;
	private String requestFor;
	private String description;
	private String maintVendor;
	private String contractNumber;
	private String maintenancePlan;
	private String maintenanceJoinDate;
	private String maintenanceExpiryDate;
	private String supplierAgreementNumber;
	private String autoSend;
	private String maintContact;
	private String maintPhone;
	private String maintFax;
	private String maintEmail;
	
    private int workRequestId;
//    private String assetNumber;
    private int workRequestStatusId;
    private int workRequestTypeId;

    private String disinfection;
    private String requestType;
    private String breakdownDateInput;
    private String scheduleDateInput;
    private String equipmentSent;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String remark;
    private String mode;
    
    private AssetInfo assetInfo;
    private List<AttachmentInfo> attachmentInfo;
    
    private String assetOrganisation;
	private String assetOwnerDesc;
	private String assetStatus;
	private String dob;
	private String assetLocationDesc;
	private String purchasePrice;
	private String assetSupplier;
	private String itemCodeNumber;
	private String fatherAssetNumber;
	private String maintenanceBody;
	private String maintenanceBodyType;
	private String maintenanceInterval;
	private String assetOwningDepartmentDesc;
	private String riskLevel;
	private String descriptionHistory;
	private String requestedForEmployee;
	
	private String attachmentMode;
	private String docId;
	
	private String maintenanceBodyNum;
	
	private long assetGroup;
	private long assetOwningDepartmentId;
	private long assetOrganisationId;
	private String lastUpdateDate;
	
	private String workRequestOrg;

	public WorkRequest() {
    }
 
    public WorkRequest(String wrNumber, String wrType, String wrStatus, String eamOrg,
    		String dateType, String dateFrom, String dateTo, String maintenanceVendor, 
    		String assetLocation, String assetOwner, String owningDept, String createdby, int workRequestId, String workRequestNumber, String assetNumber, int workRequestStatusId, int workRequestTypeId
    		, String assetOrganisation, String assetOwnerDesc, String assetStatus, String dob, String assetLocationDesc
    		, String purchasePrice, String assetSupplier, String itemCodeNumber, String maintenanceBody, String maintenanceBodyType, String fatherAssetNumber, String maintenanceInterval, String assetOwningDepartmentDesc) {
        this.workRequestNumber = workRequestNumber;
        this.assetNumber = assetNumber;
        this.wrType = wrType;
        this.wrStatus = wrStatus;
        this.eamOrg = eamOrg;
        this.dateType = dateType;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.maintenanceVendor = maintenanceVendor;
        this.assetLocation = assetLocation;
        this.assetOwner = assetOwner;
        this.owningDept = owningDept;
        this.createdby = createdby;
        this.wrNumber = wrNumber;
        this.wrType = wrType;
        
    	this.workRequestId = workRequestId;
        this.workRequestStatusId = workRequestStatusId;
        this.workRequestTypeId = workRequestTypeId;
        
        this.assetOrganisation = assetOrganisation;
        this.assetOwnerDesc = assetOwnerDesc;
        this.assetStatus = assetStatus;
        this.dob = dob;
        this.assetLocationDesc = assetLocationDesc;
        this.purchasePrice = purchasePrice;
        this.assetSupplier = assetSupplier;
        this.itemCodeNumber = itemCodeNumber;
        this.fatherAssetNumber = fatherAssetNumber;
        this.maintenanceBody = maintenanceBody;
        this.maintenanceBodyType = maintenanceBodyType;
        this.maintenanceInterval = maintenanceInterval;
        this.assetOwningDepartmentDesc = assetOwningDepartmentDesc;
    }
    
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
	public String getAssetDescription() {
		return assetDescription;
	}
	public void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}
	public String getWorkRequestType() {
		return workRequestType;
	}
	public void setWorkRequestType(String workRequestType) {
		this.workRequestType = workRequestType;
	}
	public String getWorkRequestStatus() {
		return workRequestStatus;
	}
	public void setWorkRequestStatus(String workRequestStatus) {
		this.workRequestStatus = workRequestStatus;
	}
	public String getWoNumber() {
		return woNumber;
	}
	public void setWoNumber(String woNumber) {
		this.woNumber = woNumber;
	}
	public String getWoStatus() {
		return woStatus;
	}
	public void setWoStatus(String woStatus) {
		this.woStatus = woStatus;
	}
	public String getCmBreakdownDate() {
		return cmBreakdownDate;
	}
	public void setCmBreakdownDate(String cmBreakdownDate) {
		this.cmBreakdownDate = cmBreakdownDate;
	}
	public String getPmScheduleDate() {
		return pmScheduleDate;
	}
	public void setPmScheduleDate(String pmScheduleDate) {
		this.pmScheduleDate = pmScheduleDate;
	}
	public String getCreatedby() {
		return createdby;
	}
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}
	public String getMaintenancevendor() {
		return maintenancevendor;
	}
	public void setMaintenancevendor(String maintenancevendor) {
		this.maintenancevendor = maintenancevendor;
	}
	public String getAssetlocation() {
		return assetlocation;
	}
	public void setAssetlocation(String assetlocation) {
		this.assetlocation = assetlocation;
	}	
	public String getAssetowner() {
		return assetowner;
	}
	public void setAssetowner(String assetowner) {
		this.assetowner = assetowner;
	}	
	public String getOwningdept() {
		return owningdept;
	}
	public void setOwningdept(String owningdept) {
		this.owningdept = owningdept;
	}	
	public Date getCreateddate() {
		return createddate;
	}
	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}	
	public String getSerialnumber() {
		return serialnumber;
	}
	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
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
	
	public String getWrType() {
		return wrType;
	}
	public void setWrType(String wrType) {
		this.wrType = wrType;
	}
	public String getWrStatus() {
		return wrStatus;
	}
	public void setWrStatus(String wrStatus) {
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
	public String getmMintenanceVendor() {
		return maintenanceVendor;
	}
	public void setmMintenanceVendor(String maintenanceVendor) {
		this.maintenanceVendor = maintenanceVendor;
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

	
	public int getWorkRequestId() {
		return workRequestId;
	}
	public void setWorkRequestId(int workRequestId) {
		this.workRequestId = workRequestId;
	}
	public int getWorkRequestStatusId() {
		return workRequestStatusId;
	}
	public void setWorkRequestStatusId(int workRequestStatusId) {
		this.workRequestStatusId = workRequestStatusId;
	}
	public int getWorkRequestTypeId() {
		return workRequestTypeId;
	}
	public void setWorkRequestTypeId(int workRequestTypeId) {
		this.workRequestTypeId = workRequestTypeId;
	}
		
	public String getWorkRequestNumber() {
		return workRequestNumber;
	}
	public void setWorkRequestNumber(String workRequestNumber) {
		this.workRequestNumber = workRequestNumber;
	}
	
	public Date getWrCreatedDate() {
		return wrCreatedDate;
	}
	public void setWrCreatedDate(Date wrCreatedDate) {
		this.wrCreatedDate = wrCreatedDate;
	}
	
	public String getWrCreatedBy() {
		return wrCreatedBy;
	}
	public void setWrCreatedBy(String wrCreatedBy) {
		this.wrCreatedBy = wrCreatedBy;
	}
	
	public String getHaContactPerson() {
		return haContactPerson;
	}
	public void setHaContactPerson(String haContactPerson) {
		this.haContactPerson = haContactPerson;
	}
	
	public String getHaContactPhone() {
		return haContactPhone;
	}
	public void setHaContactPhone(String haContactPhone) {
		this.haContactPhone = haContactPhone;
	}
	
	public String getHaContactEmail() {
		return haContactEmail;
	}
	public void setHaContactEmail(String haContactEmail) {
		this.haContactEmail = haContactEmail;
	}
	
	public String getRequestedFor() {
		return requestFor;
	}
	public void setRequestedFor(String requestFor) {
		this.requestFor = requestFor;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getMaintVendor() {
		return maintVendor;
	}
	public void setMaintVendor(String maintVendor) {
		this.maintVendor = maintVendor;
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
	
	public String getSupplierAgreementNumber() {
		return supplierAgreementNumber;
	}
	public void setSupplierAgreementNumber(String supplierAgreementNumber) {
		this.supplierAgreementNumber = supplierAgreementNumber;
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
	
	public String getDisinfection() {
		return disinfection;
	}
	public void setDisinfection(String disinfection) {
		this.disinfection = disinfection;
	}
	
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	
	public String getBreakdownDateInput() {
		return breakdownDateInput;
	}
	public void setBreakdownDateInput(String breakdownDateInput) {
		this.breakdownDateInput = breakdownDateInput;
	}
	
	public String getScheduleDateInput() {
		return scheduleDateInput;
	}
	public void setScheduleDateInput(String scheduleDateInput) {
		this.scheduleDateInput = scheduleDateInput;
	}
	
	public String getEquipmentSent() {
		return equipmentSent;
	}
	public void setEquipmentSent(String equipmentSent) {
		this.equipmentSent = equipmentSent;
	}
	
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public AssetInfo getAssetInfo() {
		return assetInfo;
	}
	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
	}
	
	public List<AttachmentInfo> getAttachmentInfo() {
		return attachmentInfo;
	}
	public void setAttachmentInfo(List<AttachmentInfo> listWrAttachment) {
		this.attachmentInfo = listWrAttachment;
	}
	
	public String getAssetOrganisation() {
		return assetOrganisation;
	}
	public void setAssetOrganisation(String assetOrganisation) {
		this.assetOrganisation = assetOrganisation;
	}
	
	public String getAssetOwnerDesc() {
		return assetOwnerDesc;
	}
	public void setAssetOwnerDesc(String assetOwnerDesc) {
		this.assetOwnerDesc = assetOwnerDesc;
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
	
	public String getMaintenanceBody() {
		return maintenanceBody;
	}
	public void setMaintenanceBody(String maintenanceBody) {
		this.maintenanceBody = maintenanceBody;
	}
	
	public String getMaintenanceBodyType() {
		return maintenanceBodyType;
	}
	public void setMaintenanceBodyType(String maintenanceBodyType) {
		this.maintenanceBodyType = maintenanceBodyType;
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
	
	public String getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	
	public String getDescriptionHistory() {
		return descriptionHistory;
	}
	public void setDescriptionHistory(String descriptionHistory) {
		this.descriptionHistory = descriptionHistory;
	}
	
	public String getRequestedForEmployee() {
		return requestedForEmployee;
	}
	public void setRequestedForEmployee(String requestedForEmployee) {
		this.requestedForEmployee = requestedForEmployee;
	}

	public String getAttachmentMode() {
		return attachmentMode;
	}

	public void setAttachmentMode(String attachmentMode) {
		this.attachmentMode = attachmentMode;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	public String getMaintenanceBodyNum() {
		return maintenanceBodyNum;
	}

	public void setMaintenanceBodyNum(String maintenanceBodyNum) {
		this.maintenanceBodyNum = maintenanceBodyNum;
	}
	
	public long getAssetGroup() {
		return assetGroup;
	}

	public void setAssetGroup(long assetGroup) {
		this.assetGroup = assetGroup;
	}

	public long getAssetOwningDepartmentId() {
		return assetOwningDepartmentId;
	}

	public void setAssetOwningDepartmentId(long assetOwningDepartmentId) {
		this.assetOwningDepartmentId = assetOwningDepartmentId;
	}

	public long getAssetOrganisationId() {
		return assetOrganisationId;
	}

	public void setAssetOrganisationId(long assetOrganisationId) {
		this.assetOrganisationId = assetOrganisationId;
	}
	
	public String getlastUpdateDate() {
		return lastUpdateDate;
	}

	public void setlastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	public String getWorkRequestOrg() {
		return workRequestOrg;
	}

	public void setWorkRequestOrg(String workRequestOrg) {
		this.workRequestOrg = workRequestOrg;
	}
	
	@Override
	public String toString() {
		return "WorkRequest [workRequestNumber=" + workRequestNumber + ", assetDescription=" + assetDescription + ", workRequestType=" + workRequestType  + ", workRequestStatus=" + workRequestStatus + ", workRequestStatusId=" + workRequestStatusId + "]";
	}
	
}
