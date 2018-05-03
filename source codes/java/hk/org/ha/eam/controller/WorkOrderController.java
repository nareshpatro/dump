/* --------------------------------------
    File Name: WorkOrderController.java
    Author: Carmen Ng (PCCW)
    Date: 15-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Work Order Function

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.2>		20171127	Carmen Ng	Changed "created" to "creation"
	<1.1>		20171120	Carmen Ng	Added populate equipment condition dropdown
	<1.0>		20171015	Carmen Ng	Initial version
   -------------------------------------- */

package hk.org.ha.eam.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;  
import org.springframework.web.servlet.ModelAndView;

import hk.org.ha.eam.dao.CommonUtilDao;
import hk.org.ha.eam.dao.DashBoardDao;
import hk.org.ha.eam.dao.WorkOrderDao;
import hk.org.ha.eam.dao.WorkRequestDao;
import hk.org.ha.eam.model.Dropdown;
import hk.org.ha.eam.model.MaintenanceInfo;
import hk.org.ha.eam.model.SearchWorkOrder;
import hk.org.ha.eam.model.WorkOrder;
import hk.org.ha.eam.service.WorkOrderService;

@Controller
@RequestMapping("/")
public class WorkOrderController {
	
	private static final Logger logger = Logger.getLogger(WorkOrderController.class);
	
	@Autowired
	private WorkOrderDao workOrderDao;
	@Autowired
	private WorkRequestDao workRequestDao;
	@Autowired
	private DashBoardDao dashBoardDao;
	@Autowired
	private CommonUtilDao commonUtilDao;
	@Autowired
	private WorkOrderService workOrderService;
	
	@RequestMapping(value="/searchWorkOrder")
	public ModelAndView initSearchWorkOrder(ModelAndView model, HttpServletRequest req) throws IOException, Exception {
		logger.debug("Processing work order controller: initSearchWorkOrder");
		WorkOrder newWorkOrder = new WorkOrder();
		model.addObject("workOrder", newWorkOrder);
		commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
		populateDefaultModel(model);
		//model.addObject("WorkOrderTypeList", getWorkOrderTypeList());
		model.setViewName("searchWO");
		return model;
	}
	
	@RequestMapping(value="/initCreateWorkOrder")
	public ModelAndView initCreateWorkOrder(ModelAndView model, HttpServletRequest req) throws IOException, Exception {
		logger.debug("Process initCreateWorkOrder Controller: initCreateWorkOrder");
		String wrNumber = (String)req.getParameter("wrNumber");
		String contactPerson = (String)req.getParameter("contactPerson");
		String contactPhone = (String)req.getParameter("contactPhone");
		String contactEmail = (String)req.getParameter("contactEmail");
		String breakdownDateInput = (String)req.getParameter("breakdownDateInput");
		String scheduleDateInput = (String)req.getParameter("scheduleDateInput");
		String disinfection = (String)req.getParameter("disinfection");
		String equipmentSent = (String)req.getParameter("equipmentSent");
		String fullName = (String)req.getSession().getAttribute("ebsFullName");
		String phone = (String)req.getSession().getAttribute("ebsHAContactPhone");
		String email = (String)req.getSession().getAttribute("ebsEmail");
		logger.debug("initCreateWorkOrder wrNumber: " + wrNumber);
		logger.debug("initCreateWorkOrder breakdownDateInput: " + breakdownDateInput);
		logger.debug("initCreateWorkOrder scheduleDateInput: " + scheduleDateInput);
		logger.debug("initCreateWorkOrder disinfection: " + disinfection);
		logger.debug("initCreateWorkOrder equipmentSent: " + equipmentSent);
		WorkOrder newWorkOrder = new WorkOrder();
		if (wrNumber!=null && !("").equals(wrNumber)) {
			logger.debug("initCreateWorkOrder Copy WR record");
			
//			SearchWorkRequest searchCriteria = new SearchWorkRequest();
//			searchCriteria.setWrNumber(wrNumber);
//			List<WorkRequest> wrLisr = workRequestDao.searchWorkRequestDetail(searchCriteria);
			
			newWorkOrder.setWrNumber(wrNumber);
			newWorkOrder.setContactPerson(contactPerson);
			newWorkOrder.setContactPhone(contactPhone);
			newWorkOrder.setContactEmail(contactEmail);
			if (breakdownDateInput!=null && !("").equals(breakdownDateInput)) {
				newWorkOrder.setBreakdownDateInput(breakdownDateInput);
			} else {
				newWorkOrder.setScheduleDateInput(scheduleDateInput);
			}
			newWorkOrder.setDisinfectionStatus(disinfection);
			newWorkOrder.setEquipmentSent(equipmentSent);
		} else {
			newWorkOrder.setContactPerson(fullName);
			newWorkOrder.setContactPhone(phone);
			newWorkOrder.setContactEmail(email);
		}
		model.addObject("workOrder", newWorkOrder);
		commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
		populateCreateModel(model);
//		model.addObject("workRequestTypeList", getWorkRequestTypeList());
		model.setViewName("createWO");
		return model;
	}
	
	@RequestMapping(value="/processWorkOrder", method = RequestMethod.POST)
	 @ResponseBody
	 public List<WorkOrder>  processWorkOrder( @RequestBody SearchWorkOrder searchCriteria, HttpServletRequest req, HttpServletResponse resp) throws IOException, Exception{
		logger.debug("Process WorkOrder Controller: processWorkOrder");
		logger.debug("wrNumber from SearchWorkOrder!! " + searchCriteria.getWoNumber());
		commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
		searchCriteria.setQueryLimit(Integer.parseInt(req.getSession().getAttribute("ebsExportLimit").toString()));
		List<WorkOrder> listWorkOrder = workOrderDao.searchWorkOrder(searchCriteria);
		return listWorkOrder;
	 }
	
	@RequestMapping(value = "/createWorkOrder", method = RequestMethod.POST)
	@ResponseBody
	public String newWorkOrder(@RequestBody WorkOrder workOrder, HttpServletRequest req) throws Exception {
		logger.debug("Entering Create Work Order");
		logger.debug("mode=" + workOrder.getMode());
//		logger.debug("requestId=" + workOrder.getWorkOrderId());
		logger.debug("getAssetNumber=" + workOrder.getAssetNumber());
//		logger.debug("getDescription=" + workOrder.getDescription());
		logger.debug("getEquipmentSent=" + workOrder.getEquipmentSent());
		logger.debug("getContactEmail=" + workOrder.getContactEmail());
		logger.debug("getContactPhone=" + workOrder.getContactPhone());
//		logger.debug("getBreakdownDateInput=" + workOrder.getBreakdownDateInput());
		commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
		String result = workOrderService.newWorkOrder(workOrder, req);	
		return result;
		
	}
	
	@RequestMapping(value="/processWorkOrderDetail", method = RequestMethod.POST)
	@ResponseBody
	public List<WorkOrder> processWorkOrderDetail( @RequestBody SearchWorkOrder searchCriteria, HttpServletRequest req, HttpServletResponse resp) throws IOException, Exception{
		String outputMessage=null;

		logger.debug("Process WorkOrder Detail Controller!!!");
		logger.debug("woNumber from SearchWorkOrder!! " + searchCriteria.getWoNumber());
		commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));

		/*********Gavin Add(20180104)*********
		logger.debug("searchCriteria.getWOMode() : "+searchCriteria.getWoMode());

		WorkOrder currentWO = workOrderDao.searchWorkOrderDetail(searchCriteria).get(0);
		String woStatus = currentWO.getWoStatus();
		String contractNumber = currentWO.getMaintenanceContract();

		logger.debug("processWorkOrderDetail woStatus : "+woStatus);
		logger.debug("processWorkOrderDetail contractNumber : "+contractNumber);

		if(searchCriteria.getWoMode().equals("e") && woStatus.equals("Unreleased")) {
			logger.debug("currentWO.getWoTypeId() : "+currentWO.getWoTypeId());
			logger.debug("gatest Con1 : "+(contractNumber!=null));
			logger.debug("gatest Con2 :"+workOrderDao.getIsNonITAsset(req.getSession().getAttribute("ebsRespId").toString()));
			logger.debug("gatest Con3 :"+currentWO.getWoTypeId().equals("10"));
			if(contractNumber!=null && workOrderDao.getIsNonITAsset(req.getSession().getAttribute("ebsRespId").toString()) && currentWO.getWoTypeId().equals("10")) {
				//Assume XXEAM_EXT_MAINT_ASSET_INFO only show updated data.

				/********Case 4 Only (20180110)********
		        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//		        currentWO.getWoType()=10
		        try {
		        	Date mExpiryDate = formatter.parse(currentWO.getmExpiryDate());
		        	logger.debug("mExpiryDate : "+mExpiryDate.toString());
		        	Calendar c = Calendar.getInstance(); 
		        	c.setTime(mExpiryDate); 
		        	c.add(Calendar.DATE, 1);
		        	mExpiryDate = c.getTime();
		        	logger.debug("mExpiryDate + 1 : "+mExpiryDate.toString());
		        	
		        	Date today = new Date();
		        	today.setHours(0);
		        	today.setMinutes(0);
		        	today.setSeconds(0);
		        	logger.debug("today : "+today.toString());
					logger.debug("currentWO.getAssetNumber() : "+currentWO.getAssetNumber());
		        	logger.debug("today.after(mExpiryDate) : "+today.after(mExpiryDate));
					logger.debug("workOrderDao.isAssetExpired(currentWO.getAssetNumber()) : "+workOrderDao.isAssetExpired(currentWO.getAssetNumber()));
					 
					if(today.after(mExpiryDate) || workOrderDao.isAssetExpired(currentWO.getAssetNumber())) {
					 outputMessage="Please note that the maintenance has expired.";
					 logger.debug("(Testing) Case 4");
					}
		        } catch (ParseException e) {
		            e.printStackTrace();
		        }
				/********Case 4 Only (20180110)********
			}
		}
		/*********Gavin Add(20180104)*********/

		List<WorkOrder> listWorkOrder = workOrderService.viewWO(searchCriteria);
		listWorkOrder.get(0).setOutputMessage(outputMessage);
		return listWorkOrder;
	}

	private void populateDefaultModel(ModelAndView model) throws Exception {
		List<Dropdown> disinfectionList = workRequestDao.getDisinfectionList();
		model.addObject("disinfectionList", disinfectionList);		
		   
		List<Dropdown> eamOrgList = workRequestDao.getEamOrgList();
		model.addObject("eamOrgList", eamOrgList);
	
		List<Dropdown> riskLevelList = dashBoardDao.getRiskLevelList();
		model.addObject("riskLevelList", riskLevelList);
		
		List<Dropdown> woStatusList = workOrderDao.getWoStatusList();
		model.addObject("woStatusList", woStatusList);
		
		List<Dropdown> woTypeList = workOrderDao.getWoTypeList();
		model.addObject("woTypeList", woTypeList);
		
		List<Dropdown> failureCauseList = workOrderDao.getFailureCauseCode();
		model.addObject("failureCauseList", failureCauseList);
		
		List<Dropdown> failureSymptomList = workOrderDao.getFailureSymptomCode();
		model.addObject("failureSymptomList", failureSymptomList);
		
		List<Dropdown> equipmentConditionList = workOrderDao.getEquipmentCondition();
		model.addObject("equipmentConditionList", equipmentConditionList);
		
		List<Dropdown> dateTypeList = new ArrayList<Dropdown>();
		dateTypeList.add(new Dropdown("B","CM Breakdown Date"));
		dateTypeList.add(new Dropdown("C","Work Order Creation Date"));
		dateTypeList.add(new Dropdown("S","PM Scheduled Date"));
		model.addObject("dateTypeList", dateTypeList);
   }
	
	private void populateCreateModel(ModelAndView model) throws Exception {
		   
		List<Dropdown> disinfectionList = workRequestDao.getDisinfectionList();
		model.addObject("disinfectionList", disinfectionList);		
		   			
		List<Dropdown> woStatusList = workOrderDao.getWoStatusList();
		model.addObject("woStatusList", woStatusList);
		
		List<Dropdown> woTypeList = workOrderDao.getWoTypeList();
		model.addObject("woTypeList", woTypeList);
		
		List<Dropdown> failureCauseList = workOrderDao.getFailureCauseCode();
		model.addObject("failureCauseList", failureCauseList);
		
		List<Dropdown> failureSymptomList = workOrderDao.getFailureSymptomCode();
		model.addObject("failureSymptomList", failureSymptomList);
		
		List<Dropdown> equipmentConditionList = workOrderDao.getEquipmentCondition();
		model.addObject("equipmentConditionList", equipmentConditionList);
			
		List<Dropdown> maintenanceBodyTypeList = workOrderDao.getMaintenanceBodyTypeList();
		model.addObject("maintenanceBodyTypeList", maintenanceBodyTypeList);
	
		List<Dropdown> autoSendWOList = workOrderDao.getAutoSendWOList();
		model.addObject("autoSendWOList", autoSendWOList);

	}
	
	
	@RequestMapping(value="/ResolutionCode", method = RequestMethod.POST)
	 @ResponseBody
	 public List<Dropdown> ResolutionCode( @RequestParam String cause, String symptom) throws Exception{
		List<Dropdown> resolutionCodeList = workOrderDao.getResolutionCode(cause, symptom);
		return resolutionCodeList;
	 }
	
	@RequestMapping(value="/resendWorkOrder", method = RequestMethod.POST)
	@ResponseBody
	 public String resendWorkOrder(@RequestBody WorkOrder workOrder ,HttpServletRequest request) throws Exception {
		logger.debug("Resend Work Order Number " + workOrder.getWoNumber());
		logger.debug("Resend Work Order Id " + workOrder.getWorkOrderId());
		logger.debug("Resend Work Order M AutoSend " + workOrder.getAutoSendWO());
		logger.debug("Resend Work Order M Fax " + workOrder.getmContactFax());
		logger.debug("Resend Work Order M email " + workOrder.getmContactEmail());
		commonUtilDao.ebsAppInit((String)request.getSession().getAttribute("ebsUserId"), (String)request.getSession().getAttribute("ebsRespId"), (String)request.getSession().getAttribute("ebsRespAppId"));
		String result = workOrderService.resendWorkOrder(workOrder);
		return result;
	}
	
	@RequestMapping(value = "/getMaintenanceInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<MaintenanceInfo> getMaintenanceInfo(@RequestParam String assetNumber, String maintenanceNumber) throws Exception {
		List<MaintenanceInfo> maintenanceInfoList = workOrderDao.getMaintenanceInfo(assetNumber, maintenanceNumber,true);
		return maintenanceInfoList;
	}
	
	@RequestMapping(value = "/gs1VendorCheck", method = RequestMethod.POST)
	@ResponseBody
	public Boolean gs1VendorCheck(String maintenanceNumber) throws Exception {
		Boolean vendorCheck = workOrderDao.gs1VendorCheck(maintenanceNumber);
		return vendorCheck;
	}
	
	 @RequestMapping(value = "/checkOSWorkOrder", method = RequestMethod.POST)
	 @ResponseBody
	 public String checkWorkOrder(@RequestBody WorkOrder workOrder, HttpServletRequest req) throws Exception {
		logger.debug("Checking Create Work Order");
		logger.debug("getAssetNumber=" + workOrder.getAssetNumber());			
		commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
		String ret = workOrderService.chkWorkOrder(workOrder);		
			
		return ret;
	}
	 
	//Call this function when update and resend button is clicked and contact method has changed
	@RequestMapping(value = "/chkResendRecord", method = RequestMethod.POST)
	@ResponseBody
	public String chkResendRecord(@RequestParam String WONumber, HttpServletRequest req) throws Exception {
        commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
		return workOrderDao.chkResendRecord(WONumber);
	}
	 
	//Call this function when update WO button is clicked
	@RequestMapping(value = "/chkAssetEnabled", method = RequestMethod.POST)
	@ResponseBody
	public String chkAssetEnabled(@RequestParam String assetNumber, @RequestParam String orgCode, HttpServletRequest req) throws Exception {
		logger.debug("assetNumber/orgCode " + assetNumber+"/"+orgCode);
        commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
		return workOrderDao.chkAssetEnabled(assetNumber, orgCode);
	}
	
	//Call this function when update WO button is clicked
		@RequestMapping(value = "/chkMainBodyEnabled", method = RequestMethod.POST)
		@ResponseBody
		public String chkMainBodyEnabled(@RequestParam String mainBodyNum, HttpServletRequest req) throws Exception {
			logger.debug("mainBodyNum " + mainBodyNum);
	        commonUtilDao.ebsAppInit((String)req.getSession().getAttribute("ebsUserId"), (String)req.getSession().getAttribute("ebsRespId"), (String)req.getSession().getAttribute("ebsRespAppId"));
			return workOrderDao.chkMainBodyEnabled(mainBodyNum);
		}
}

