/* --------------------------------------
    File Name: WorkOrder.java
    Author: Kin Shum (PCCW)
    Date: 1-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - WorkOrder Bean

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.3>		20171204	Carmen Ng	Added operationDescription
	<1.2>		20171114	Carmen Ng	Added support for dashboard's overdue/upcoming
	<1.1>		20171110	Carmen Ng	Added variables for view work order
	<1.0>		20170901	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.model;

import java.util.Date;
import java.util.List;

public class WorkOrder {
	//Search work order results table values
	private String workOrderNumber;
	private String assetNumber;
	private String assetDescription;
	private String woType;
	private String woStatus;
	private String breakdownScheduleDate;
	private String breakdownDate;
	private String scheduleDate;
	private String owningDepartment;
	private Long owningDepartmentId;
	private String createdBy;
	private String maintenanceBody;
	private String maintenanceBodyNum;
	private String maintenanceBodyType;
	private String maintenanceContract;
	private String assetLocation;
	private String assetOwner;
	private Long assetGroupId;
	private String assetGroup;
	private Date creationDate;
	private String assetSerialNumber;
	private String manufacturer;
	private String brand;
	private String model;
	
	//Values for search work order criteria
	private String woNumber;
	private String eamOrg;
	private String dateType;
	private String dateFrom;
	private String dateTo;
	private String assetRisk;
	private String maintenanceVendor;
	private String hiddenMBody;
	private String serialNumber;
	private String owningDept;
	private boolean criticalOnly;
	private boolean urgentOnly;
	private String dashboardValue;
	
	//Values for create work order
	private String equipmentSent;
	private String disinfectionStatus;
	private String woDescription;
	private String vendorRemark;
	private String userRemark;
	private String haContactPerson;
	private String haContactPhone;
	private String haContactEmail;
	private String mBodyType;
	private String contractNumber;
	private String autoSendWO;
	private String supplierAgreementNumber;
	private String mContactPerson;
	private String mPlan;
	private String mContactPhone;
	private String mJoinDate;
	private String mContactFax;
	private String mExpiryDate;
	private String mContactEmail;
	private String mInterval;
	private String vendorReferenceNo;
	private String failureCauseCode;
	private String callRecieved;
//	private Date callRecieved;
	private String failureSymptomCode;
	private String equipmentRecievedDate;
//	private Date equipmentRecievedDate;
	private String repairResoultionCode;
//	private Date attendanceDate;
	private String attendanceDate;
	private String equipmentCondition;
//	private Date reinstatementCompletionDate;
	private String reinstatementCompletionDate;
	private String sparePartCost;
	private String laborCost;
	private String sparePartDesc;
	private String addMaterialCost;
	private String addLaborCost;
	private String addMaterialDesc;
	private String technicalName;
	private String workOrderCompletedBy;
	private String resultAndAction;
	private String serviceReport;
	private String operationDescription;
	
	private String wrNumber;
	private String woStatusId;
	private String woTypeId;
	private String itemCodeNumber;
	private String assetStatus;
	private String dob;
	private String assetLocationDesc;
	private String assetOwnerDesc;
	private String assetOwningDepartmentDesc;
	private String purchasePrice;
	private String assetSupplier;
	private String fatherAssetNumber;
	private String outboundDate;
	private String assetOrg;			
    private AssetInfo assetInfo;
    private List<AttachmentInfo> attachmentInfo;
    private String mode;
    private int workOrderId;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    
	private String attachmentMode;
	private String docId;
    private String breakdownDateInput;
    private String scheduleDateInput;

    private String completionDate;
    private String lastUpdateDate;
    private String scheduleEndDate;
    private String scheduleStartDate;
    
    private String workOrderOrg;
   	
	//Value for pre-update warning message
    private String outputMessage;//0. Normal Case, 1. Same maintenance body on old / new contract 2. Difference maintenance body on old/ new contract, 3. No contract no in work order, 4 Contract Expired
    
	public WorkOrder() {
    }

	public String getWorkOrderNumber() {
		return workOrderNumber;
	}

	public void setWorkOrderNumber(String workOrderNumber) {
		this.workOrderNumber = workOrderNumber;
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

	public String getWoType() {
		return woType;
	}

	public void setWoType(String woType) {
		this.woType = woType;
	}

	public String getWoStatus() {
		return woStatus;
	}

	public void setWoStatus(String woStatus) {
		this.woStatus = woStatus;
	}

	public String getBreakdownScheduleDate() {
		return breakdownScheduleDate;
	}

	public void setBreakdownScheduleDate(String breakdownScheduleDate) {
		this.breakdownScheduleDate = breakdownScheduleDate;
	}
	
	public String getBreakdownDate() {
		return breakdownDate;
	}

	public void setBreakdownDate(String breakdownDate) {
		this.breakdownDate = breakdownDate;
	}
	
	public String getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public Long getOwningDepartmentId() {
		return owningDepartmentId;
	}

	public void setOwningDepartmentId(Long owningDepartmentId) {
		this.owningDepartmentId = owningDepartmentId;
	}
	
	public String getOwningDepartment() {
		return owningDepartment;
	}

	public void setOwningDepartment(String owningDepartment) {
		this.owningDepartment = owningDepartment;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	
	public String getMaintenanceContract() {
		return maintenanceContract;
	}

	public void setMaintenanceContract(String maintenanceContract) {
		this.maintenanceContract = maintenanceContract;
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

	public Long getAssetGroupId() {
		return assetGroupId;
	}

	public void setAssetGroupId(Long assetGroupId) {
		this.assetGroupId = assetGroupId;
	}

	public String getAssetGroup() {
		return assetGroup;
	}

	public void setAssetGroup(String assetGroup) {
		this.assetGroup = assetGroup;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getAssetSerialNumber() {
		return assetSerialNumber;
	}

	public void setAssetSerialNumber(String assetSerialNumber) {
		this.assetSerialNumber = assetSerialNumber;
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
	
	public String getWoNumber() {
		return woNumber;
	}

	public void setWoNumber(String woNumber) {
		this.woNumber = woNumber;
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

	public String getMaintenanceVendor() {
		return maintenanceVendor;
	}

	public void setMaintenanceVendor(String maintenanceVendor) {
		this.maintenanceVendor = maintenanceVendor;
	}

	public String getHiddenMBody() {
		return hiddenMBody;
	}

	public void setHiddenMBody(String hiddenMBody) {
		this.hiddenMBody = hiddenMBody;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getOwningDept() {
		return owningDept;
	}

	public void setOwningDept(String owningDept) {
		this.owningDept = owningDept;
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
	
	public String getEquipmentSent() {
		return equipmentSent;
	}

	public void setEquipmentSent(String equipmentSent) {
		this.equipmentSent = equipmentSent;
	}

	public String getDisinfectionStatus() {
		return disinfectionStatus;
	}

	public void setDisinfectionStatus(String disinfectionStatus) {
		this.disinfectionStatus = disinfectionStatus;
	}

	public String getWoDescription() {
		return woDescription;
	}

	public void setWoDescription(String woDescription) {
		this.woDescription = woDescription;
	}

	public String getVendorRemark() {
		return vendorRemark;
	}

	public void setVendorRemark(String vendorRemark) {
		this.vendorRemark = vendorRemark;
	}

	public String getUserRemark() {
		return userRemark;
	}

	public void setUserRemark(String userRemark) {
		this.userRemark = userRemark;
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

	public String getmBodyType() {
		return mBodyType;
	}

	public void setmBodyType(String mBodyType) {
		this.mBodyType = mBodyType;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getAutoSendWO() {
		return autoSendWO;
	}

	public void setAutoSendWO(String autoSendWO) {
		this.autoSendWO = autoSendWO;
	}

	public String getSupplierAgreementNumber() {
		return supplierAgreementNumber;
	}

	public void setSupplierAgreementNumber(String supplierAgreementNumber) {
		this.supplierAgreementNumber = supplierAgreementNumber;
	}

	public String getmContactPerson() {
		return mContactPerson;
	}

	public void setmContactPerson(String mContactPerson) {
		this.mContactPerson = mContactPerson;
	}

	public String getmPlan() {
		return mPlan;
	}

	public void setmPlan(String mPlan) {
		this.mPlan = mPlan;
	}

	public String getmContactPhone() {
		return mContactPhone;
	}

	public void setmContactPhone(String mContactPhone) {
		this.mContactPhone = mContactPhone;
	}

	public String getmJoinDate() {
		return mJoinDate;
	}

	public void setmJoinDate(String mJoinDate) {
		this.mJoinDate = mJoinDate;
	}

	public String getmContactFax() {
		return mContactFax;
	}

	public void setmContactFax(String mContactFax) {
		this.mContactFax = mContactFax;
	}

	public String getmExpiryDate() {
		return mExpiryDate;
	}

	public void setmExpiryDate(String mExpiryDate) {
		this.mExpiryDate = mExpiryDate;
	}

	public String getmContactEmail() {
		return mContactEmail;
	}

	public void setmContactEmail(String mContactEmail) {
		this.mContactEmail = mContactEmail;
	}

	public String getmInterval() {
		return mInterval;
	}

	public void setmInterval(String mInterval) {
		this.mInterval = mInterval;
	}

	public String getVendorReferenceNo() {
		return vendorReferenceNo;
	}

	public void setVendorReferenceNo(String vendorReferenceNo) {
		this.vendorReferenceNo = vendorReferenceNo;
	}

	public String getFailureCauseCode() {
		return failureCauseCode;
	}

	public void setFailureCauseCode(String failureCauseCode) {
		this.failureCauseCode = failureCauseCode;
	}

	public String getCallRecieved() {
		return callRecieved;
	}

	public void setCallRecieved(String callRecieved) {
		this.callRecieved = callRecieved;
	}

	public String getFailureSymptomCode() {
		return failureSymptomCode;
	}

	public void setFailureSymptomCode(String failureSymptomCode) {
		this.failureSymptomCode = failureSymptomCode;
	}

	public String getEquipmentRecievedDate() {
		return equipmentRecievedDate;
	}

	public void setEquipmentRecievedDate(String equipmentRecievedDate) {
		this.equipmentRecievedDate = equipmentRecievedDate;
	}

	public String getRepairResoultionCode() {
		return repairResoultionCode;
	}

	public void setRepairResoultionCode(String repairResoultionCode) {
		this.repairResoultionCode = repairResoultionCode;
	}

	public String getAttendanceDate() {
		return attendanceDate;
	}

	public void setAttendanceDate(String attendanceDate) {
		this.attendanceDate = attendanceDate;
	}

	public String getEquipmentCondition() {
		return equipmentCondition;
	}

	public void setEquipmentCondition(String equipmentCondition) {
		this.equipmentCondition = equipmentCondition;
	}

	public String getReinstatementCompletionDate() {
		return reinstatementCompletionDate;
	}

	public void setReinstatementCompletionDate(String reinstatementCompletionDate) {
		this.reinstatementCompletionDate = reinstatementCompletionDate;
	}

	public String getSparePartCost() {
		return sparePartCost;
	}

	public void setSparePartCost(String sparePartCost) {
		this.sparePartCost = sparePartCost;
	}

	public String getLaborCost() {
		return laborCost;
	}

	public void setLaborCost(String laborCost) {
		this.laborCost = laborCost;
	}

	public String getSparePartDesc() {
		return sparePartDesc;
	}

	public void setSparePartDesc(String sparePartDesc) {
		this.sparePartDesc = sparePartDesc;
	}

	public String getAddMaterialCost() {
		return addMaterialCost;
	}

	public void setAddMaterialCost(String addMaterialCost) {
		this.addMaterialCost = addMaterialCost;
	}

	public String getAddLaborCost() {
		return addLaborCost;
	}

	public void setAddLaborCost(String addLaborCost) {
		this.addLaborCost = addLaborCost;
	}

	public String getAddMaterialDesc() {
		return addMaterialDesc;
	}

	public void setAddMaterialDesc(String addMaterialDesc) {
		this.addMaterialDesc = addMaterialDesc;
	}

	public String getTechnicalName() {
		return technicalName;
	}

	public void setTechnicalName(String technicalName) {
		this.technicalName = technicalName;
	}

	public String getWorkOrderCompletedBy() {
		return workOrderCompletedBy;
	}

	public void setWorkOrderCompletedBy(String workOrderCompletedBy) {
		this.workOrderCompletedBy = workOrderCompletedBy;
	}

	public String getResultAndAction() {
		return resultAndAction;
	}

	public void setResultAndAction(String resultAndAction) {
		this.resultAndAction = resultAndAction;
	}

	public String getServiceReport() {
		return serviceReport;
	}

	public void setServiceReport(String serviceReport) {
		this.serviceReport = serviceReport;
	}
	
	public String getWrNumber() {
		return wrNumber;
	}

	public void setWrNumber(String wrNumber) {
		this.wrNumber = wrNumber;
	}

	public String getWoStatusId() {
		return woStatusId;
	}

	public void setWoStatusId(String woStatusId) {
		this.woStatusId = woStatusId;
	}

	public String getWoTypeId() {
		return woTypeId;
	}

	public void setWoTypeId(String woTypeId) {
		this.woTypeId = woTypeId;
	}

	public String getItemCodeNumber() {
		return itemCodeNumber;
	}

	public void setItemCodeNumber(String itemCodeNumber) {
		this.itemCodeNumber = itemCodeNumber;
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

	public String getAssetOwnerDesc() {
		return assetOwnerDesc;
	}

	public void setAssetOwnerDesc(String assetOwnerDesc) {
		this.assetOwnerDesc = assetOwnerDesc;
	}

	public String getAssetOwningDepartmentDesc() {
		return assetOwningDepartmentDesc;
	}

	public void setAssetOwningDepartmentDesc(String assetOwningDepartmentDesc) {
		this.assetOwningDepartmentDesc = assetOwningDepartmentDesc;
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

	public String getFatherAssetNumber() {
		return fatherAssetNumber;
	}

	public void setFatherAssetNumber(String fatherAssetNumber) {
		this.fatherAssetNumber = fatherAssetNumber;
	}
	
	public String getOutboundDate() {
		return outboundDate;
	}

	public void setOutboundDate(String outboundDate) {
		this.outboundDate = outboundDate;
	}

	public String getAssetOrg() {
		return assetOrg;
	}

	public void setAssetOrg(String assetOrg) {
		this.assetOrg = assetOrg;
	}			  
	public String getDashboardValue() {
		return dashboardValue;
	}

	public void setDashboardValue(String dashboardValue) {
		this.dashboardValue = dashboardValue;
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
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public int getWorkOrderId() {
		return workOrderId;
	}
	public void setWorkOrderId(int workOrderId) {
		this.workOrderId = workOrderId;
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
	
	public String getOperationDescription() {
		return operationDescription;
	}

	public void setOperationDescription(String operationDescription) {
		this.operationDescription = operationDescription;
	}
	
	public String getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}
	
	public String getlastUpdateDate() {
		return lastUpdateDate;
	}

	public void setlastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	public String getOutputMessage() {
		return outputMessage;
	}

	public void setOutputMessage(String outputMessage) {
		this.outputMessage = outputMessage;
	}
	
	 public String getScheduleEndDate() {
		return scheduleEndDate;
	}

	public void setScheduleEndDate(String scheduleEndDate) {
		this.scheduleEndDate = scheduleEndDate;
	}
	
	public String getScheduleStartDate() {
		return scheduleStartDate;
	}

	public void setScheduleStartDate(String scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}
	
	public String getWorkOrderOrg() {
		return workOrderOrg;
	}

	public void setWorkOrderOrg(String workOrderOrg) {
		this.workOrderOrg = workOrderOrg;
	}

	@Override
	public String toString() {
		return "workOrder [workOrderNumber=" + workOrderNumber + ", assetDescription=" + assetDescription + ", woType=" + woType  + ", woStatus=" + woStatus + "]";
	}
	
}
