/* --------------------------------------
    File Name: WorkRequestService.java
    Author: Kin Shum (PCCW)
    Date: 5-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - WorkRequestServiceInterface

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170905	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import hk.org.ha.eam.model.SearchWorkRequest;
import hk.org.ha.eam.model.WorkRequest;

public interface WorkRequestService {

    public String newWorkRequest(WorkRequest workRequest, HttpServletRequest request) throws Exception;
	
    public String chkWorkRequest(WorkRequest workRequest) throws Exception;
    
    public List<WorkRequest> viewWR (SearchWorkRequest searchCriteria) throws Exception;
}
