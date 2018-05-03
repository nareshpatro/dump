/* --------------------------------------
    File Name: WorkRequestController.java
    Author: Fanny Hung (PCCW)
    Date: 31-Jul-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Work Request Function

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.1>		20171127	Carmen Ng	Changed "created" to "creation"
	<1.0>		20170731	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.eam.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.org.ha.eam.dao.CommonUtilDao;
import hk.org.ha.eam.dao.DashBoardDao;
import hk.org.ha.eam.dao.WorkRequestDao;
import hk.org.ha.eam.model.AssetInfo;
import hk.org.ha.eam.model.Dropdown;
import hk.org.ha.eam.model.SearchWorkRequest;
import hk.org.ha.eam.model.WorkRequest;
import hk.org.ha.eam.service.WorkRequestService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;  
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class WorkRequestController {
	
	private static final Logger logger = Logger.getLogger(WorkRequestController.class);
	
	@Autowired
	private WorkRequestDao workRequestDao;
	@Autowired
	private WorkRequestService workRequestService;
	@Autowired
	private CommonUtilDao commonUtilDao;
	@Autowired
	private DashBoardDao dashBoardDao;
	
	@RequestMapping(value="/searchWorkRequest", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView initSearchWorkRequest(ModelAndView model, HttpServletRequest req) throws IOException, Exception {
		WorkRequest newWorkRequest = new WorkRequest();
		model.addObject("workRequest", newWorkRequest);
		
//		// check attributes for evidence of forward, includes, error page handling
//		  Enumeration<String> attributeNames = req.getAttributeNames();
//
//		  while (attributeNames.hasMoreElements()) {
//		    String  attributeName = attributeNames.nextElement();
//			logger.debug("req "+attributeName);
//		  }
//		  
//		  req.get
		
		commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
		populateDefaultModel(model);
//		model.addObject("workRequestTypeList", getWorkRequestTypeList());
		model.setViewName("searchWR");
		return model;
	}

	 @RequestMapping(value="/processWorkRequest", method = RequestMethod.POST)
	 @ResponseBody
	 public List<WorkRequest>  processWorkRequest( @RequestBody SearchWorkRequest searchCriteria, HttpServletRequest req, HttpServletResponse resp) throws IOException, Exception{
		logger.debug("Process WorkRequest Controller!!!");
		logger.debug("wrNumber from SearchWorkRequest!! " + searchCriteria.getWrNumber());
		commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
		searchCriteria.setQueryLimit(Integer.parseInt(req.getSession().getAttribute("ebsExportLimit").toString()));
		List<WorkRequest> listWorkRequest = workRequestDao.searchWorkRequest(searchCriteria);
		return listWorkRequest;
	 }
	 
	 @RequestMapping(value="/initCreateWorkRequest")
	 public ModelAndView initCreateWorkRequest(ModelAndView model, HttpServletRequest req) throws IOException, Exception {
		 logger.debug("Process initCreateWorkRequest Controller!!!");
		 WorkRequest newWorkRequest = new WorkRequest();
		 model.addObject("workRequest", newWorkRequest);
		 commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
		 populateCreateModel(model);
		 model.setViewName("createWR");
		 return model;
	 }
		
		@RequestMapping(value = "/createWorkRequest", method = RequestMethod.POST)
		@ResponseBody
		public String newWorkRequest(@RequestBody WorkRequest workRequest, HttpServletRequest req) throws Exception {
			logger.debug("Entering Create Work Request");
			logger.debug("mode=" + workRequest.getMode());
			logger.debug("requestId=" + workRequest.getWorkRequestId());
			logger.debug("getAssetNumber=" + workRequest.getAssetNumber());
			logger.debug("getDescription=" + workRequest.getDescription());
			logger.debug("getEquipmentSent=" + workRequest.getEquipmentSent());
			logger.debug("getContactEmail=" + workRequest.getContactEmail());
			logger.debug("getContactPhone=" + workRequest.getContactPhone());
			logger.debug("getBreakdownDateInput=" + workRequest.getBreakdownDateInput());
			commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
			String result = workRequestService.newWorkRequest(workRequest, req);	
			return result;
			
		}
		
		 @RequestMapping(value="/processWorkRequestDetail", method = RequestMethod.POST)
		 @ResponseBody
		 public List<WorkRequest>  processWorkRequestDetail( @RequestBody SearchWorkRequest searchCriteria, HttpServletRequest req, HttpServletResponse resp) throws IOException, Exception{
			logger.debug("Process WorkRequest Detail Controller!!!");
			logger.debug("wrNumber from SearchWorkRequest!! " + searchCriteria.getWrNumber());
			commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
			List<WorkRequest> listWorkRequest = workRequestService.viewWR(searchCriteria);
//			List<WorkRequest> listWorkRequest = workRequestDao.searchWorkRequestDetail(searchCriteria);
			return listWorkRequest;
		 }
		
	   private void populateDefaultModel(ModelAndView model) throws Exception {
		   
			List<Dropdown> workRequestTypeList = workRequestDao.getMfgLookupList("WIP_EAM_WORK_REQ_TYPE");
			model.addObject("workRequestTypeList", workRequestTypeList);
			logger.debug("workRequestTypeList size: " +workRequestTypeList.size());
			List<Dropdown> workRequestStatusList = workRequestDao.getMfgLookupList("WIP_EAM_WORK_REQ_STATUS");
			model.addObject("workRequestStatusList", workRequestStatusList);
		
			List<Dropdown> eamOrgList = workRequestDao.getEamOrgList();
			model.addObject("eamOrgList", eamOrgList);
			
			List<Dropdown> riskLevelList = dashBoardDao.getRiskLevelList();
			model.addObject("riskLevelList", riskLevelList);
			
			List<Dropdown> dateTypeList = new ArrayList<Dropdown>();
			dateTypeList.add(new Dropdown("B","CM Breakdown Date"));
			dateTypeList.add(new Dropdown("C","Work Request Creation Date"));
			dateTypeList.add(new Dropdown("S","PM Scheduled Date"));
			model.addObject("dateTypeList", dateTypeList);
			
	   }
	   
	   private void populateCreateModel(ModelAndView model) throws Exception {
		   
			List<Dropdown> workRequestTypeList = workRequestDao.getMfgLookupList("WIP_EAM_WORK_REQ_TYPE");
			model.addObject("workRequestTypeList", workRequestTypeList);
			logger.debug("workRequestTypeList size: " +workRequestTypeList.size());
			List<Dropdown> workRequestStatusLimitedList = workRequestDao.getMfgLookupList("WIP_EAM_WORK_REQ_STATUS_LIMITED");
			model.addObject("workRequestStatusLimitedList", workRequestStatusLimitedList);
		
			List<Dropdown> woStatusList = workRequestDao.getMfgLookupList("WIP_JOB_STATUS");
			model.addObject("woStatusList", woStatusList);			
			List<Dropdown> disinfectionList = workRequestDao.getDisinfectionList();
			model.addObject("disinfectionList", disinfectionList);			
			
	   }
	   
		@RequestMapping(value = "/getAssetInfo", method = RequestMethod.POST)
		@ResponseBody
		public List<AssetInfo> getAssetInfo(@RequestParam String assetNumber, HttpServletRequest req) throws Exception {
			logger.debug("assetNumber " + assetNumber);
	        String[] org = {};
	        commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
			List<AssetInfo> assetInfoList = workRequestDao.getAssetAttr(assetNumber, org);
			logger.debug("Asset info size " + assetInfoList.size());
			return assetInfoList;
		}
		
		//Call this function when update WO button is clicked
		@RequestMapping(value = "/chkAssetInfo", method = RequestMethod.POST)
		@ResponseBody
		public List<AssetInfo> chkAssetInfo(@RequestParam String assetNumber, HttpServletRequest req) throws Exception {
			logger.debug("assetNumber " + assetNumber);
	        String[] org = {};
	        commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
			List<AssetInfo> assetInfoList = workRequestDao.chkAssetAttr(assetNumber, org);
			logger.debug("Asset info size " + assetInfoList.size());
			return assetInfoList;
		}
				 
		 @RequestMapping(value = "/checkOSWorkRequest", method = RequestMethod.POST)
		 @ResponseBody
		 public String checkWorkRequest(@RequestBody WorkRequest workRequest, HttpServletRequest req) throws Exception {
			logger.debug("Checking Create Work Request");
			logger.debug("getAssetNumber=" + workRequest.getAssetNumber());			
			logger.debug("check getWrStatus=" + workRequest.getWrStatus());
			logger.debug("check getWorkRequestStatus=" + workRequest.getWorkRequestStatus());
			commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
			String ret = workRequestService.chkWorkRequest(workRequest);		
				
			return ret;
		}
		 
}

