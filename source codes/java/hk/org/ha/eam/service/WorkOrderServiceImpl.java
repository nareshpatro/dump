package hk.org.ha.eam.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hk.org.ha.eam.dao.WorkOrderDao;
import hk.org.ha.eam.dao.WorkRequestDao;
import hk.org.ha.eam.model.AssetInfo;
import hk.org.ha.eam.model.AttachmentInfo;
import hk.org.ha.eam.model.SearchWorkOrder;
import hk.org.ha.eam.model.WorkOrder;
import hk.org.ha.eam.util.WorkOrderConstant;
import hk.org.ha.eam.common.util.DateUtil;

@Service
public class WorkOrderServiceImpl implements WorkOrderService{
	
	private static final Logger logger = Logger.getLogger(WorkOrderServiceImpl.class);

	@Autowired
	DateUtil dateUtil;
	
	@Autowired
	private WorkOrderDao workOrderDao;
	
	@Autowired
	private WorkRequestDao workRequestDao;
	
	@Transactional
    public String newWorkOrder(WorkOrder workOrder, HttpServletRequest request) throws Exception {
    	
    	String[] org = {""};
        String userId = (String)request.getSession().getAttribute("ebsUserId");
        String respId = (String)request.getSession().getAttribute("ebsRespId"); 
        String appId = (String)request.getSession().getAttribute("ebsRespAppId");
        Boolean autoValue=false;
        
    	List<AssetInfo> assetInfoList = workRequestDao.getAssetAttr(workOrder.getAssetNumber(), org);
    	if (assetInfoList.size() != 0) {
    		
    		workOrder.setAssetInfo(assetInfoList.get(0));
    		logger.debug("workOrder.getMaintenanceBody() : "+workOrder.getMaintenanceBody());
    		logger.debug("assetInfoList.get(0).getMaintenanceBodyNum() : "+assetInfoList.get(0).getMaintenanceBodyNum());
        	if (workOrder.getMaintenanceBody().equals(assetInfoList.get(0).getMaintenanceBodyNum())) {
        		autoValue = true;
    			logger.debug("Not Set Maintenance Body Blank");
    			logger.debug("workOrder."+workOrder.getAssetInfo().getAssetNumber());
        	}
        	else {
        		autoValue = false;    		
    			logger.debug("Set Maintenance Body Blank");
        	}
    	}
    	else {
    		autoValue = false;    		
    	}

    	String a = workOrderDao.saveWorkOrder(workOrder,autoValue, org, userId, respId, appId);
    	
    	if((WorkOrderConstant.UPDATE).equals(workOrder.getMode())) {
			/*************Update Maintenance Details*************/
			StringBuffer sqlBuffer = new StringBuffer();
			ArrayList<String> param = new ArrayList<String>();

			sqlBuffer.append("UPDATE WIP_DISCRETE_JOBS  ");
			sqlBuffer.append("SET ");

			sqlBuffer.append("ATTRIBUTE3 = ?");	//maint_contract_no
			param.add(workOrder.getMaintenanceContract());
			sqlBuffer.append(",ATTRIBUTE12 = ?");	//supplier_agreement
			param.add(workOrder.getSupplierAgreementNumber());
			sqlBuffer.append(",ATTRIBUTE9 = ?");	//maint_plan
			param.add(workOrder.getmPlan());
	    	String newJoinDate =dateUtil.formatDateToStr( dateUtil.parseStrToDate(workOrder.getmJoinDate(), "dd/MM/yyyy")  , "yyyy/MM/dd HH:mm:ss" );
	    	String newExpiryDate =dateUtil.formatDateToStr( dateUtil.parseStrToDate(workOrder.getmExpiryDate(), "dd/MM/yyyy")  , "yyyy/MM/dd HH:mm:ss" );
	        sqlBuffer.append(",ATTRIBUTE10 = ?");	//maint_join_date
			param.add(newJoinDate);
			sqlBuffer.append(",ATTRIBUTE11 = ?");	//maint_expiry_date
			param.add(newExpiryDate);

			sqlBuffer.append(",ATTRIBUTE8 = ?");	//maint_body_type
			param.add(workOrder.getMaintenanceBodyType());
			sqlBuffer.append(",ATTRIBUTE13 = ?");	//auto_send_method
			param.add(workOrder.getAutoSendWO());
			sqlBuffer.append(",ATTRIBUTE2 = ?");	//maint_contact_person
			param.add(workOrder.getmContactPerson());
			sqlBuffer.append(",ATTRIBUTE4 = ?");	//maint_contact_phone
			param.add(workOrder.getmContactPhone());
			sqlBuffer.append(",ATTRIBUTE5 = ?");	//maint_fax_number
			param.add(workOrder.getmContactFax());
			sqlBuffer.append(",ATTRIBUTE6 = ?");	//maint_email
			param.add(workOrder.getmContactEmail());

			sqlBuffer.append(" WHERE WIP_ENTITY_ID = ?");
			param.add(Integer.toString(workOrder.getWorkOrderId()));
				
			workOrderDao.updateWorkOrderInfo(sqlBuffer.toString(),param);
			/*************Update Maintenance Details*************/

			/*************Update Repair Details*************/
			sqlBuffer = new StringBuffer();
			param = new ArrayList<String>();

			sqlBuffer.append("UPDATE WIP_OPERATIONS  ");
			sqlBuffer.append("SET ");

			sqlBuffer.append("ATTRIBUTE2 = ?");	//ATTRIBUTE2	callReceivedDateTime,
			param.add((workOrder.getCallRecieved() != null && workOrder.getCallRecieved().length()>0) ? workOrder.getCallRecieved().substring(6,10)+"/"+workOrder.getCallRecieved().substring(3,5)+"/"+workOrder.getCallRecieved().substring(0,2)+workOrder.getCallRecieved().substring(10)+":00": null);
			sqlBuffer.append(",ATTRIBUTE8 = ?");	//ATTRIBUTE8	equipReceivedDateTime,
			param.add((workOrder.getEquipmentRecievedDate() != null && workOrder.getEquipmentRecievedDate().length()>0) ? workOrder.getEquipmentRecievedDate().substring(6,10)+"/"+workOrder.getEquipmentRecievedDate().substring(3,5)+"/"+workOrder.getEquipmentRecievedDate().substring(0,2)+workOrder.getEquipmentRecievedDate().substring(10)+":00": null);
			sqlBuffer.append(",ATTRIBUTE7 = ?");	//ATTRIBUTE7	attendanceDateTime,
			param.add((workOrder.getAttendanceDate() != null && workOrder.getAttendanceDate().length()>0) ? workOrder.getAttendanceDate().substring(6,10)+"/"+workOrder.getAttendanceDate().substring(3,5)+"/"+workOrder.getAttendanceDate().substring(0,2)+workOrder.getAttendanceDate().substring(10)+":00": null);
			sqlBuffer.append(",ATTRIBUTE9 = ?");	//ATTRIBUTE9	reinstatementDateTime,
			param.add((workOrder.getReinstatementCompletionDate() != null && workOrder.getReinstatementCompletionDate().length()>0) ? workOrder.getReinstatementCompletionDate().substring(6,10)+"/"+workOrder.getReinstatementCompletionDate().substring(3,5)+"/"+workOrder.getReinstatementCompletionDate().substring(0,2)+workOrder.getReinstatementCompletionDate().substring(10)+":00": null);
			sqlBuffer.append(",ATTRIBUTE11 = ?");	//ATTRIBUTE11	laborCost,
			param.add(workOrder.getLaborCost());
			sqlBuffer.append(",ATTRIBUTE3 = ?");	//ATTRIBUTE3	equipCondition,
			param.add(workOrder.getEquipmentCondition());
			sqlBuffer.append(",ATTRIBUTE13 = ?");	//ATTRIBUTE13	sparePartCost,
			param.add(workOrder.getSparePartCost());
			sqlBuffer.append(",ATTRIBUTE12 = ?");	//ATTRIBUTE12	sparePartDesc,
			param.add(workOrder.getSparePartDesc());
			sqlBuffer.append(",ATTRIBUTE10 = ?");	//ATTRIBUTE10	technicalName,
			param.add(workOrder.getTechnicalName());
			sqlBuffer.append(",ATTRIBUTE15 = ?");	//ATTRIBUTE15	resultAndAction,
			param.add(workOrder.getResultAndAction());
			sqlBuffer.append(",ATTRIBUTE14 = ?");	//ATTRIBUTE14	servicReportReference,
			param.add(workOrder.getServiceReport());

			sqlBuffer.append(" WHERE WIP_ENTITY_ID = ?");
			param.add(Integer.toString(workOrder.getWorkOrderId()));
				
			workOrderDao.updateWorkOrderInfo(sqlBuffer.toString(),param);
			/*************Update Repair Details*************/
    	}
    	
    	return a;
    	
    }
	
	@Transactional
    public List<WorkOrder> viewWO(SearchWorkOrder searchCriteria) throws Exception {
		List<WorkOrder> listWorkOrder = workOrderDao.searchWorkOrderDetail(searchCriteria);
		logger.debug("EAMORG="+listWorkOrder.get(0).getEamOrg());
		List<AttachmentInfo>  listWoAttachment = workOrderDao.getAttachmentInfo(listWorkOrder.get(0).getEamOrg(), Integer.toString(listWorkOrder.get(0).getWorkOrderId())); 
		listWorkOrder.get(0).setAttachmentInfo(listWoAttachment);
		
		return listWorkOrder;
    }
	
	@Transactional
    public String resendWorkOrder(WorkOrder workOrder) throws Exception {
    	
    	String a = workOrderDao.resendWorkOrder(workOrder);
    	return a;
    	
    }
	
	@Transactional
    public String chkWorkOrder(WorkOrder workOrder) throws Exception {
		logger.debug("Start chkWorkOrder..."+new Date());
		logger.debug("Mode="+workOrder.getMode());
		logger.debug("WO Type="+workOrder.getWoType());
		String resStr = "";
		String[] str;
    	String resWOChecking = "Y";
    	String resWOSchDateChecking = "Y";
    	
    	//Check any existing CM WO in Oh hold, Unreleased and Released status during create WO => Warning message
    	if (workOrder.getWoType().equals("10")) {
    		if (workOrder.getMode().equals("CREATE")) {
    			resWOChecking = workRequestDao.checkWorkOrder(workOrder.getAssetNumber(),workOrder.getWoType(),workOrder.getScheduleDateInput(),workOrder.getMode().equals("UPDATE")?workOrder.getWoNumber():null);
    			if (resWOChecking!="Y") {
    				str = resWOChecking.split("\\|");
    				resStr = "Outstanding CM Work Order #" + str[0] + " is created by " + str[2] + " on " + str[1] + ". Are you sure you want to proceed?";
    			}else {
    				resStr = "Y";
    			}
    		}else {
    			resStr = "Y"; //No need to check for CM WO update
    		}
    	}else {//Check any existing PM WO in On hold, Unreleased, Released, Complete, Complete - Pending Close, Closed status with same schedule date => Error not allow create/update
    		resWOSchDateChecking = workRequestDao.checkWorkOrderSchDate(workOrder.getAssetNumber(),workOrder.getWoType(),workOrder.getBreakdownScheduleDate(),workOrder.getMode().equals("UPDATE")?workOrder.getWoNumber():null);
    		if (resWOSchDateChecking!="Y") {
	    		str = resWOSchDateChecking.split("\\|");
				if (workOrder.getMode().equals("UPDATE")) {
					resStr = "[N]PM Work Order #" + str[0] + " with the same schedule date has been created by " + str[2] + ". Update is not allowed.";
				}else {
					resStr = "[N]PM Work Order #" + str[0] + " with the same schedule date has been created by " + str[2] + ". Creation is not allowed.";
				}
    		}else {
    			resStr = "Y"; //No duplicate PM WO found during create/update
    		}
    	}
		logger.debug("resWOChecking"+resWOChecking);
		logger.debug("resWOSchDateChecking"+resWOSchDateChecking);
		return resStr;
		
//		if (resWOChecking!="Y") { // O/S PM WO exists, check if same schedule date record exists
//			if (workOrder.getWoType().equals("20")) {
//				resWOSchDateChecking = workRequestDao.checkWorkOrderSchDate(workOrder.getAssetNumber(),workOrder.getWoType(),workOrder.getBreakdownScheduleDate(),workOrder.getMode().equals("UPDATE")?workOrder.getWoNumber():null);
//			}
//			logger.debug("resWOSchDateChecking"+resWOSchDateChecking);
//			if (resWOSchDateChecking!="Y") {/*O/S PM WO with same schedule date exists => do not allow creation*/
//				str = resWOSchDateChecking.split("\\|");
//				if (workOrder.getMode().equals("UPDATE")) {
//					resStr = "[N]PM Work Order #" + str[0] + " is created by " + str[2] + " with same schedule date. Update is not allowed.";
//				}else {
//					resStr = "[N]PM Work Order #" + str[0] + " is created by " + str[2] + " with same schedule date. Creation is not allowed.";
//				}
//	    		return resStr;
//			}else { /* Only O/S PM WO exists => warning message*/
//				str = resWOChecking.split("\\|");
//	    		resStr = resStr + "Outstanding ";
//				if (workOrder.getWoType().equals("10")) {
//					resStr = resStr + "CM";
//				}else {
//					resStr = resStr + "PM";
//				}
//				/*resStr = resStr + " Work Order #" + str[0] + " is created by " + str[2] + " with schedule date " + str[1] + ". Are you sure you want to proceed?";*/
//				resStr = resStr + " Work Order #" + str[0] + " is created by " + str[2] + " on " + str[1] + ". Are you sure you want to proceed?";
//	    		return resStr;
//			}
//		}else {
//			return "Y";
//		}
    }

}
