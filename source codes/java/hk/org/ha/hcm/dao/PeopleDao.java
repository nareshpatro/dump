/* --------------------------------------
    File Name: WorkRequestDao.java
    Author: Fanny Hung (PCCW)
    Date: 31-Jul-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Work Request Function

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170731	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.hcm.dao;

import java.util.List;

import hk.org.ha.eam.model.AssetInfo;
import hk.org.ha.eam.model.AttachmentInfo;
import hk.org.ha.eam.model.Dropdown;
import hk.org.ha.eam.model.SearchWorkRequest;
import hk.org.ha.eam.model.WorkRequest;

public interface PeopleDao {
    public List<WorkRequest> searchWorkRequest(SearchWorkRequest searchCriteria) throws Exception ;
    
    public List<Dropdown> getMfgLookupList(String lookupType) throws Exception ;
    
    public List<Dropdown> getEamOrgList() throws Exception ;
    
    public List<Dropdown> getDisinfectionList() throws Exception ;

    public List<AssetInfo> getAssetAttr(String assetNumber, String[] org) throws Exception ;
    
    public List<AssetInfo> chkAssetAttr(String assetNumber, String[] org) throws Exception ;
    
    public List<AssetInfo> getWRAssetAttr(String wrNumber) throws Exception ;
        
    public List<AttachmentInfo> getAttachmentInfo(String wrNumber) throws Exception ;
    
    public String saveWorkRequest(WorkRequest workRequest, String[] org, String userId, String respId, String appId) throws Exception ;
    
    public List<WorkRequest> searchWorkRequestDetail(SearchWorkRequest searchCriteria) throws Exception ;
 
    public String checkWorkRequest(String assetNum, String type, int wrId, String mode)	throws Exception;
	
	public String checkWRSchDateRec(String assetNum, String type, String pmSchDate, int wrId, String mode)	throws Exception;
    
    public String checkWorkOrder(String assetNum, String type, String pmSchDate, String woNumber)	throws Exception;
	
	public String checkWorkOrderSchDate(String assetNum, String type, String pmSchDate, String woNumber)	throws Exception;
    
    public String validateWRData(WorkRequest workRequest) throws Exception;
    
    public int getAssetAttrRespType();
}
