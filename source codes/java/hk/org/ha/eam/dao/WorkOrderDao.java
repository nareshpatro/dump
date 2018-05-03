/* --------------------------------------
    File Name: WorkOrderDao.java
    Author: Carmen Ng (PCCW)
    Date: 15-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Work Order Function

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.1>		20171120	Carmen Ng	Added getEquipmentCondition
	<1.0>		20171015	Carmen Ng	Initial version
   -------------------------------------- */

package hk.org.ha.eam.dao;/**

 *
 */

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import hk.org.ha.eam.model.AssetInfo;
import hk.org.ha.eam.model.AttachmentInfo;
import hk.org.ha.eam.model.Dropdown;
import hk.org.ha.eam.model.MaintenanceInfo;
import hk.org.ha.eam.model.SearchWorkOrder;
import hk.org.ha.eam.model.WorkOrder;

public interface WorkOrderDao {
    public List<WorkOrder> searchWorkOrder(SearchWorkOrder searchCriteria) throws Exception ;
    
    public List<AssetInfo> getWOAssetAttr(String wrNumber) throws Exception ;
    
    public List<Dropdown> getWoStatusList() throws Exception ;
    
    public List<Dropdown> getWoTypeList() throws Exception ;
    
    public List<Dropdown> getFailureCauseCode() throws Exception ;

    public List<Dropdown> getFailureSymptomCode() throws Exception ;
    
    public List<Dropdown> getResolutionCode(String cause, String symptom) throws Exception ;
    
    public List<Dropdown> getEquipmentCondition() throws Exception;

    public List<Dropdown> getMaintenanceBodyTypeList() throws Exception;
    
    public List<Dropdown> getAutoSendWOList() throws Exception;
    
    public String saveWorkOrder(WorkOrder workOrder, Boolean autoValue,String[] org, String userId, String respId, String appId) throws Exception ;
    
    public List<WorkOrder> searchWorkOrderDetail(SearchWorkOrder searchCriteria) throws Exception ;
	
	public List<AttachmentInfo> getAttachmentInfo(String orgID, String woId) throws SQLException, Exception ;
	
	public String resendWorkOrder(WorkOrder workOrder) throws Exception;
	
	public List<MaintenanceInfo> getMaintenanceInfo(String assetNumber, String maintenanceNumber, boolean hvMaintNum) throws Exception;
	
	public Boolean gs1VendorCheck(String maintenanceNumber) throws Exception;
	
	public void updateWorkOrderInfo(String query,ArrayList<String> param) throws Exception;
	
	public String chkAssetEnabled(String assetNumber, String orgCode) throws SQLException, Exception;

	public boolean getIsNonITAsset(String respId) throws SQLException, Exception;
	
	public boolean isAssetExpired(String assetNumber) throws SQLException, Exception;
	
	public String chkMainBodyEnabled(String mainBodyNumber) throws SQLException, Exception;
	
	public String chkResendRecord(String WONumber) throws SQLException, Exception;
}
