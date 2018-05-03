/* --------------------------------------
    File Name: WorkRequestServiceImpl.java
    Author: Kin Shum (PCCW)
    Date: 5-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - WorkRequestService interface implements

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170905	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hk.org.ha.eam.dao.WorkRequestDao;
import hk.org.ha.eam.model.AssetInfo;
import hk.org.ha.eam.model.AttachmentInfo;
import hk.org.ha.eam.model.SearchWorkRequest;
import hk.org.ha.eam.model.WorkRequest;

@Service
public class WorkRequestServiceImpl implements WorkRequestService {
	
	private static final Logger logger = Logger.getLogger(WorkRequestServiceImpl.class);
	
	@Autowired
	private WorkRequestDao workRequestDao;
	
	@Transactional
    public String newWorkRequest(WorkRequest workRequest, HttpServletRequest request) throws Exception {
    	
    	String[] org = {""};
    	List<AssetInfo> assetInfoList = workRequestDao.getAssetAttr(workRequest.getAssetNumber(), org);
    	if (assetInfoList.size() != 0) {
    		workRequest.setAssetInfo(assetInfoList.get(0));
    	}
    	String a = workRequestDao.saveWorkRequest(workRequest, org, (String)request.getSession().getAttribute("ebsUserId"), (String)request.getSession().getAttribute("ebsRespId"), (String)request.getSession().getAttribute("ebsRespAppId"));
    	
    	return a;
    	
    }
	
	@Transactional
    public String chkWorkRequest(WorkRequest workRequest) throws Exception {
		logger.debug("Start chkWorkRequest..."+new Date());
		logger.debug("Mode="+workRequest.getMode());
		String resStr = "";
		String[] str;
    	String resWRChecking = workRequestDao.checkWorkRequest(workRequest.getAssetNumber(),workRequest.getRequestType(),workRequest.getWorkRequestId(),workRequest.getMode());
    	String resWOChecking = "";
    	String resWRSchDateChecking = "";
    	String resWOSchDateChecking = "";

    	logger.debug("resWRChecking"+resWRChecking);
    	// Check (1)Any CM WR in Awaiting Work Order Status
    	// (2) Any CM WO in On Hold, Released, Unreleased status => Warning 
    	if ("10".equals(workRequest.getRequestType())) {
    		if (resWRChecking!="Y") {
    			str = resWRChecking.split("\\|");
	    		resStr = "Outstanding CM Work Request #" + str[0] + " is created by " + str[2] + " on " + str[1] + ". Are you sure you want to proceed?";
	    		return resStr;
    		}else {//No outstanding CM WR found check: If CM WO (On Hold,Unreleased,Released) found 
    			resWOChecking = workRequestDao.checkWorkOrder(workRequest.getAssetNumber(),workRequest.getRequestType(),workRequest.getScheduleDateInput(),null);
    			logger.debug("resWOChecking"+resWOChecking);
    			if (resWOChecking!="Y") {
    				str = resWOChecking.split("\\|");
		    		resStr = "Outstanding CM Work Order #" + str[0] + " is created by " + str[2] + " on " + str[1] + ". Are you sure you want to proceed?";
		    		return resStr;
    			}else {
    				return "Y";
    			}
    		}
    	}else {
    		// Check (1)Any PM WR in Awaiting Work Order Status with same schedule date => Error
        	// (2) Any PM WO in On Hold, Released, Unreleased, Complete-Pending Close, Complete, Closed status with same schedule date => Error
    		resWRSchDateChecking = workRequestDao.checkWRSchDateRec(workRequest.getAssetNumber(),workRequest.getRequestType(),workRequest.getScheduleDateInput(),workRequest.getWorkRequestId(),workRequest.getMode());
			logger.debug("resWRSchDateChecking"+resWRSchDateChecking);
			if (resWRSchDateChecking!="Y") {
				str = resWRSchDateChecking.split("\\|");
				if (workRequest.getMode().equals("UPDATE")) {
					resStr = resStr + "[N]PM Work Request #" + str[0] + " with same schedule date has been created by " + str[2] + ". Update is not allowed.";
				}else {
					resStr = resStr + "[N]PM Work Request #" + str[0] + " with same schedule date has been created by " + str[2] + ". Creation is not allowed.";
				}
	    		return resStr;
			}else {//Check PM WO
				resWOSchDateChecking = workRequestDao.checkWorkOrderSchDate(workRequest.getAssetNumber(),workRequest.getRequestType(),workRequest.getScheduleDateInput(),null);
				logger.debug("resWOSchDateChecking"+resWOSchDateChecking);
				if (resWOSchDateChecking!="Y") {//O/S PM WO with same schedule date exists => do not allow creation
					str = resWOSchDateChecking.split("\\|");
					if (workRequest.getMode().equals("UPDATE")) {
						resStr = "[N]PM Work Order #" + str[0] + " with the same schedule date has been created by " + str[2] + ". Update is not allowed.";
					}else {
						resStr = "[N]PM Work Order #" + str[0] + " with the same schedule date has been created by " + str[2] + ". Creation is not allowed.";
					}
					return resStr;
				}else {
					return "Y";
				}
			}
    	}
    	
/*    	if ("10".equals(workRequest.getRequestType())) {
    		resWRSchDateChecking = "Y";
    	}

    	if ("10".equals(workRequest.getRequestType())) {
    		resWOSchDateChecking = "Y";
    	}
    	
    	if ("10".equals(workRequest.getRequestType())) { // For CM WR Creation
    		if (resWRChecking!="Y") {
	    		str = resWRChecking.split("\\|");
	    		resStr = resStr + "Outstanding CM Work Request #" + str[0] + " is created by " + str[2] + " on " + str[1] + ". Are you sure you want to proceed?";
	    		return resStr;
	    	}else {
	    		return "Y"; //No O/S WR found
	    	}
    	}else { // For PM WR Creation
    		if (resWRChecking!="Y") { // O/S PM WR exists, check if same schedule date record exists
    			resWRSchDateChecking = workRequestDao.checkWRSchDateRec(workRequest.getAssetNumber(),workRequest.getRequestType(),workRequest.getScheduleDateInput(),workRequest.getWorkRequestId(),workRequest.getMode());
    			logger.debug("resWRSchDateChecking"+resWRSchDateChecking);
    			if (resWRSchDateChecking!="Y") {	// O/S PM WR with same schedule date exists => do not allow create WR
    				//str = resWRChecking.split("\\|");
    				str = resWRSchDateChecking.split("\\|");
    				if (workRequest.getMode().equals("UPDATE")) {
    					resStr = resStr + "[N]PM Work Request #" + str[0] + " with same schedule date has been created by " + str[2] + ". Update is not allowed.";
    				}else {
    					resStr = resStr + "[N]PM Work Request #" + str[0] + " with same schedule date has been created by " + str[2] + ". Creation is not allowed.";
    				}
    	    		return resStr;
    			}else {	// O/S PM WR with same schedule date not exists => check O/S WO
    				resWOChecking = workRequestDao.checkWorkOrder(workRequest.getAssetNumber(),workRequest.getRequestType(),workRequest.getScheduleDateInput(),null);
    				logger.debug("resWOChecking"+resWOChecking);
    				if (resWOChecking!="Y") { // O/S PM WO exists, check if same schedule date record exists
    					resWOSchDateChecking = workRequestDao.checkWorkOrderSchDate(workRequest.getAssetNumber(),workRequest.getRequestType(),workRequest.getScheduleDateInput(),null);
    					logger.debug("resWOSchDateChecking"+resWOSchDateChecking);
    					if (resWOSchDateChecking!="Y") {//O/S PM WO with same schedule date exists => do not allow creation
    						str = resWOSchDateChecking.split("\\|");
    						if (workRequest.getMode().equals("UPDATE")) {
    							resStr = "[N]PM Work Order #" + str[0] + " with the same schedule date has been created by " + str[2] + ". Update is not allowed.";
    						}else {
    							resStr = "[N]PM Work Order #" + str[0] + " with the same schedule date has been created by " + str[2] + ". Creation is not allowed.";
    						}
    			    		return resStr;
    					}else { // Only O/S PM WO exists => warning message
    						str = resWOChecking.split("\\|");
    			    		resStr = resStr + "Outstanding PM Work Order #" + str[0] + " is created by " + str[2] + " with schedule date " + str[1] + ". Are you sure you want to proceed?";
    			    		return resStr;
    					}
    				}else { // Only O/S PM WR exists, no O/S WO exists => WR warning
    					str = resWRChecking.split("\\|");
    		    		resStr = resStr + "Outstanding PM Work Request #" + str[0] + " is created by " + str[2] + " with schedule date " + str[1] + ". Are you sure you want to proceed?";
    		    		return resStr;
    				}
    			}
    		}else { // O/S PM WR not exists => check PM O/S WO
    			resWOChecking = workRequestDao.checkWorkOrder(workRequest.getAssetNumber(),workRequest.getRequestType(),workRequest.getScheduleDateInput(),null);
    			logger.debug("resWOChecking"+resWOChecking);
				if (resWOChecking!="Y") { // O/S PM WO exists, check if same schedule date record exists
					resWOSchDateChecking = workRequestDao.checkWorkOrderSchDate(workRequest.getAssetNumber(),workRequest.getRequestType(),workRequest.getScheduleDateInput(),null);
					logger.debug("resWOSchDateChecking"+resWOSchDateChecking);
					if (resWOSchDateChecking!="Y") {//O/S PM WO with same schedule date exists => do not allow creation
						str = resWOSchDateChecking.split("\\|");
						if (workRequest.getMode().equals("UPDATE")) {
							resStr = "[N]PM Work Order #" + str[0] + " is created by " + str[2] + " with same schedule date. Update is not allowed.";
						}else {
							resStr = "[N]PM Work Order #" + str[0] + " is created by " + str[2] + " with same schedule date. Creation is not allowed.";
						}
			    		return resStr;
					}else { // Only O/S PM WO exists => warning message
						str = resWOChecking.split("\\|");
			    		resStr = resStr + "Outstanding PM Work Order #" + str[0] + " is created by " + str[2] + " with schedule date " + str[1] + ". Are you sure you want to proceed?";
			    		return resStr;
					}
				}else { // Both O/S WR and WO not exists => Create directly
		    		return "Y";
				}
    		}
    	} // For PM end
*/    }
	
	@Transactional
    public List<WorkRequest> viewWR (SearchWorkRequest searchCriteria) throws Exception {
		List<AttachmentInfo>  listWrAttachment = workRequestDao.getAttachmentInfo(searchCriteria.getWrNumber());
		List<WorkRequest> listWorkRequest = workRequestDao.searchWorkRequestDetail(searchCriteria);
		 
		listWorkRequest.get(0).setAttachmentInfo(listWrAttachment);
		
		return listWorkRequest;
    }
	
}
