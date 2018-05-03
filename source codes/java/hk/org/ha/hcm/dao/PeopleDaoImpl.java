/* --------------------------------------
    File Name: WorkRequestDaoImpl.java
    Author: Fanny Hung (PCCW)
    Date: 31-Jul-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Work Request Function Implementation

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170731	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.hcm.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import hk.org.ha.eam.model.AssetInfo;
import hk.org.ha.eam.model.AttachmentInfo;
import hk.org.ha.eam.model.Dropdown;
import hk.org.ha.eam.model.SearchWorkRequest;
import hk.org.ha.eam.model.WorkOrder;
import hk.org.ha.eam.model.WorkRequest;
import hk.org.ha.eam.util.ConnectionProvider;
import hk.org.ha.eam.util.WorkOrderConstant;
import hk.org.ha.eam.util.WorkRequestConstant;
import hk.org.ha.ebs.common.EBSUtil;
import hk.org.ha.eam.common.util.DateUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;

/**
 * @author imssbora
 *
 */
@Repository
public class PeopleDaoImpl implements PeopleDao {
	
	private static final Logger logger = Logger.getLogger(PeopleDaoImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;   
	private JdbcTemplate jdbcTemplate;
	
	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	
	@Autowired
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	@Autowired
	DateUtil dateUtil;
	
	@Autowired
	private MessageSource messageSource;
	
    public PeopleDaoImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
 
    @Override
    @Transactional(rollbackFor=Exception.class)	
    public List<WorkRequest> searchWorkRequest(SearchWorkRequest searchCriteria) throws Exception {
    	
        String wrNumber = searchCriteria.getWrNumber();
        String assetNumber = searchCriteria.getAssetNumber();
        String wrType = searchCriteria.getWrType();
        List wrStatus = searchCriteria.getWrStatus();
        String dateType = searchCriteria.getDateType();
        String dateFrom = searchCriteria.getDateFrom();
        String dateTo = searchCriteria.getDateTo();
        String maintenanceVendor = searchCriteria.getMaintenanceVendor();
        String hiddenMBody = searchCriteria.getHiddenMBody();
        String assetLocation =searchCriteria.getAssetLocation();
        String assetOwner = searchCriteria.getAssetOwner();
        String owningDept = searchCriteria.getOwningDept();
        String createdBy = searchCriteria.getCreatedBy();
        String eanOrg = searchCriteria.getEamOrg();
        String riskLevel = searchCriteria.getRiskLevel();
        boolean criticalOnly = searchCriteria.getCriticalOnly();
        
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        
        sqlBuffer.append("SELECT v.ASSET_NUMBER assetNumber, v.ASSET_DESCRIPTION assetDescription, v.ITEM_CODE_NUMBER itemCode, v.ASSET_STATUS assetStatus, v.DOB dateOfBirth, v.SERIAL_NUMBER serialNumber, \n");
        sqlBuffer.append("v.ASSET_LOCATION_CODE assetLocation, v.asset_location_desc, v.ASSET_OWNER assetOwner, v.asset_owner_desc, v.ASSET_OWNING_DEPARTMENT owningDept, v.asset_owning_department_desc, \n"); 
        sqlBuffer.append("v.MANUFACTURER, v.BRAND, v.MODEL, v.ASSET_SUPPLIER assetSupplier, v.RISK_LEVEL riskLevel, v.PARENT_ASSET_NUMBER parentAssetNumber, v.PURCHASE_PRICE purchasePrice, \n");
        sqlBuffer.append("v.WORK_REQUEST_NUMBER wrNumber, v.WORK_REQUEST_STATUS wrStatus, v.WORK_REQUEST_TYPE wrType, TO_CHAR(v.CM_BREAKDOWN_DATE, 'dd/MM/yyyy hh24:MI') cmBreakdownDate, \n");
        sqlBuffer.append("TO_CHAR(v.PM_SCHEDULE_DATE, 'dd/MM/yyyy hh24:MI') pmScheduleDate, v.CREATION_DATE createdDate, nvl(v.created_by_employee,v.created_by_user) createdBy, \n");
        sqlBuffer.append("v.DISINFECTION_STATUS disinfectionStatus, v.EQUIPMENT_SENT_TO_WORKSHOP equipSentToWorkshop, v.HA_CONTACT_PERSON haContactPerson, v.HA_CONTACT_NUMBER haContactPhone, \n");
        sqlBuffer.append("v.HA_CONTACT_EMAIL haContactEmail, v.REQUESTED_FOR requestedFor, v.description, v.MAINTENANCE_BODY_NAME||decode(v.MAINTENANCE_BODY_NUMBER,null,null,' ('||v.MAINTENANCE_BODY_NUMBER||')') maintenanceVendor, v.MAINTENANCE_BODY_TYPE maintenanceVendorType, \n");
        sqlBuffer.append("v.MAINTENANCE_CONTRACT_NUMBER contractNumber, v.MAINTENANCE_PLAN maintenancePlan, v.MAINTENANCE_JOIN_DATE maintenanceJoinDate, v.MAINTENANCE_EXPIRY_DATE maintenanceExpiryDate, \n");
        sqlBuffer.append("v.SUPPLIER_AGREEMENT_NUMBER supplierAgreementNumber, v.AUTO_SEND_WO_TO_SUPPLIER autoSend, v.MAINTENANCE_CONTACT_PERSON maintenanceContactPerson,  v.MAINTENANCE_CONTACT_PHONE maintenanceContactPhone, v.MAINTENANCE_CONTACT_FAX maintenanceContactFax, v.MAINTENANCE_CONTACT_EMAIL maintenanceContactEmail, v.MAINTENANCE_INTERVAL MAINTENANCE_INTERVAL, \n"); 
        sqlBuffer.append("v.WORK_ORDER_NUMBER woNumber, v.ORGANIZATION_CODE org, v.CANCEL_REJECT_REMARK remark, \n");
//        sqlBuffer.append("(select ewsv.work_order_status from eam_work_order_details ewod, EAM_WO_STATUSES_V ewsv where ewsv.status_id = ewod.user_defined_status_id and ewod.wip_entity_id = v.WIP_ENTITY_ID) woStatus \n");
        sqlBuffer.append("v.WORK_ORDER_STATUS woStatus \n");
        /*if (criticalOnly) {
        	sqlBuffer.append("from XXEAM_EXT_WORK_REQUESTS_WRN_V v \n");
        }else {
        	sqlBuffer.append("from XXEAM_EXT_WORK_REQUESTS_V v \n");
        }*/
        sqlBuffer.setLength(0);
        sqlBuffer.append("SELECT v.WORK_REQUEST_ID, v.WORK_REQUEST_NUMBER wrNumber, v.REQUESTED_FOR_USER_ID, v.REQUESTED_FOR requestedFor, v.REQUESTED_FOR_EMPLOYEE, v.WORK_REQUEST_TYPE_CODE, v.WORK_REQUEST_TYPE wrType, v.WORK_REQUEST_STATUS_CODE, v.WORK_REQUEST_STATUS wrStatus, TO_CHAR(v.CM_BREAKDOWN_DATE, 'dd/MM/yyyy hh24:MI') cmBreakdownDate, TO_CHAR(v.PM_SCHEDULE_DATE, 'dd/MM/yyyy hh24:MI') pmScheduleDate, v.ORGANIZATION_ID, v.ORGANIZATION_CODE org, v.OWNING_DEPARTMENT_ID, v.OWNING_DEPARTMENT, v.OWNING_DEPARTMENT_DESC, v.LEGACY_SERIAL_NUMBER, v.ASSET_NUMBER assetNumber, v.ASSET_DESCRIPTION assetDescription, v.ASSET_LOCATION_CODE assetLocation, v.ASSET_OWNING_DEPARTMENT owningDept, v.ASSET_OWNING_DEPARTMENT_ID, v.ASSET_OWNING_DEPARTMENT_DESC, v.ASSET_OWNER assetOwner, v.MANUFACTURER, v.BRAND, v.MODEL, v.SERIAL_NUMBER serialNumber, v.RISK_LEVEL riskLevel, v.MAINTENANCE_BODY_NAME||decode(v.MAINTENANCE_BODY_NUMBER,null,null,' ('||v.MAINTENANCE_BODY_NUMBER||')') maintenanceVendor, v.ITEM_CODE_NUMBER itemCode, v.CREATION_DATE createdDate, v.CREATED_BY, nvl(v.created_by_employee,v.created_by_user) createdBy, v.LAST_UPDATE_DATE, v.LAST_UPDATED_BY, v.WORK_REQUEST_CREATED_BY, v.WIP_ENTITY_ID, v.WORK_ORDER_NUMBER woNumber, v.WORK_ORDER_STATUS woStatus, v.description \n");
        sqlBuffer.append("from XXEAM_EXT_WR_SIMPLE_V v \n");
        sqlBuffer.append("where xxeam_maint_body_sec_chk_wo(v.MAINTENANCE_BODY_NUMBER) = 'Y' \n");
        sqlBuffer.append("and v.ORGANIZATION_ID in (SELECT organization_id FROM xxeam_accessible_org_v) \n");
        //sqlBuffer.append("and v.OWNING_DEPARTMENT_ID in (SELECT department_id FROM xxeam_accessible_dept_v) \n");
        
        if(eanOrg != null && !"".equals(eanOrg)){
        	sqlBuffer.append("AND v.ORGANIZATION_ID = :eanOrg ");
        	paramMap.addValue("eanOrg", eanOrg);
        }
               
        if(wrNumber != null && !"".equals(wrNumber)){
        	sqlBuffer.append("AND v.WORK_REQUEST_NUMBER LIKE :wrNumber ");
        	paramMap.addValue("wrNumber", wrNumber.trim() + "%");
        }
        
        if(assetNumber != null && !"".equals(assetNumber)){
        	sqlBuffer.append("AND v.ASSET_NUMBER = :assetNumber ");
        	paramMap.addValue("assetNumber", assetNumber);
        }
        
        if(wrType != null && !"".equals(wrType)){
        	sqlBuffer.append("AND v.WORK_REQUEST_TYPE_CODE = :wrType ");
        	paramMap.addValue("wrType", wrType);
        }
        
        if(wrStatus != null && wrStatus.size() != 0){
        	sqlBuffer.append("AND v.WORK_REQUEST_STATUS_CODE in (:wrStatus) ");
	        paramMap.addValue("wrStatus", wrStatus);
        }
        
        if((WorkRequestConstant.BREAKDOWN_DATE).equals(dateType)) {
            if(dateFrom != null && !"".equals(dateFrom)){
            	sqlBuffer.append("AND trunc(v.CM_BREAKDOWN_DATE) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy')) ");
              	paramMap.addValue("dateFrom", dateFrom);
            }   
            if(dateTo != null && !"".equals(dateTo)){
            	sqlBuffer.append( "AND trunc(v.CM_BREAKDOWN_DATE) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
              	paramMap.addValue("dateTo", dateTo);
            }   
        }else if((WorkRequestConstant.CREATED_DATE).equals(dateType)) {
            if(dateFrom != null && !"".equals(dateFrom)){
            	sqlBuffer.append("AND trunc(v.CREATION_DATE) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy'))  ");
              	paramMap.addValue("dateFrom", dateFrom);
            }   
            if(dateTo != null && !"".equals(dateTo)){
            	sqlBuffer.append("AND trunc(v.CREATION_DATE) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
              	paramMap.addValue("dateTo", dateTo);
            }   
        }else if((WorkRequestConstant.SCHEDULE_DATE).equals(dateType)) {            
            if(dateFrom != null && !"".equals(dateFrom)){
            	sqlBuffer.append("AND trunc(v.PM_SCHEDULE_DATE) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy')) ");
              	paramMap.addValue("dateFrom", dateFrom);
            }   
            if(dateTo != null && !"".equals(dateTo)){
            	sqlBuffer.append("AND trunc(v.PM_SCHEDULE_DATE) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
              	paramMap.addValue("dateTo", dateTo);
            }   
        }
                    
        if(maintenanceVendor != null && !"".equals(maintenanceVendor)){
        	sqlBuffer.append("AND v.MAINTENANCE_BODY_NUMBER = :maintenanceVendorNum ");
        	paramMap.addValue("maintenanceVendorNum", hiddenMBody);
        }
        
        if(assetLocation != null && !"".equals(assetLocation)){
        	sqlBuffer.append("AND v.ASSET_LOCATION_CODE = :assetLocation ");
        	paramMap.addValue("assetLocation", assetLocation);
        }
        
        if(assetOwner != null && !"".equals(assetOwner)){
        	sqlBuffer.append("AND v.ASSET_OWNER = :assetOwner ");
        	paramMap.addValue("assetOwner", assetOwner);
        }
        
        if(owningDept != null && !"".equals(owningDept)){
        	sqlBuffer.append("AND v.OWNING_DEPARTMENT = :owningDept ");
        	paramMap.addValue("owningDept", owningDept);
        }

        if(riskLevel != null && !"".equals(riskLevel)){
        	sqlBuffer.append("AND v.RISK_LEVEL = :riskLevel ");
        	paramMap.addValue("riskLevel", riskLevel);
        }
        
        if(createdBy != null && !"".equals(createdBy) ){
        	sqlBuffer.append("AND v.CREATED_BY_USER = :createdBy ");
        	paramMap.addValue("createdBy", createdBy);
        }
        logger.debug("createdBy " + createdBy);
        if(criticalOnly){
        	//sqlBuffer.append("AND v.ITEM_CODE_NUMBER in (select MEANING from MFG_LOOKUPS where LOOKUP_TYPE = 'XXEAM_CRITICAL_MEDICAL_EQUIP' and ENABLED_FLAG = 'Y' and nvl(trunc(START_DATE_ACTIVE),trunc(sysdate)) <= trunc(sysdate) and nvl(TRUNC(END_DATE_ACTIVE),trunc(sysdate)) >= trunc(sysdate)) ");
        	sqlBuffer.append("AND v.MAINTENANCE_OBJECT_ID in ( \n");
        	sqlBuffer.append("	SELECT PO_ATTR.MAINTENANCE_OBJECT_ID \n");
        	sqlBuffer.append("  FROM MTL_EAM_ASSET_ATTR_VALUES PO_ATTR, MTL_SYSTEM_ITEMS ITEM, MFG_LOOKUPS LV \n");
        	sqlBuffer.append("  WHERE PO_ATTR.C_ATTRIBUTE7        = ITEM.INVENTORY_ITEM_ID \n");
        	sqlBuffer.append("  AND LV.MEANING = ITEM.SEGMENT1 \n");
        	sqlBuffer.append("  AND PO_ATTR.ATTRIBUTE_CATEGORY    = 'Purchase Order Details' \n");
        	sqlBuffer.append("  AND ITEM.ORGANIZATION_ID = 121 \n");
        	sqlBuffer.append("  AND LV.LOOKUP_TYPE = 'XXEAM_CRITICAL_MEDICAL_EQUIP' \n");
        	sqlBuffer.append("  AND LV.ENABLED_FLAG = 'Y' \n");
        	sqlBuffer.append("  AND NVL(TRUNC(LV.START_DATE_ACTIVE),TRUNC(sysdate)) <= TRUNC(sysdate) \n");
        	sqlBuffer.append("  AND NVL(TRUNC(LV.END_DATE_ACTIVE),TRUNC(sysdate))   >= TRUNC(sysdate) \n");
        	sqlBuffer.append(") \n");
        }
        
    	//sqlBuffer.append("ORDER BY wrNumber DESC");
        
//        String sql = "SELECT * FROM WIP_EAM_WORK_REQUESTS WHERE WORK_REQUEST_NUMBER='" + wrNumber + "'";
//        String sql = "SELECT * FROM WIP_EAM_WORK_REQUESTS WHERE WORK_REQUEST_ID=5193";
    	
//        List<WorkRequest> listWorkRequest = jdbcTemplate.query(sqlBuffer.toString(), new RowMapper<WorkRequest>() {
        List<WorkRequest> listWorkRequest = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<WorkRequest>() {
            @Override
            public WorkRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
            	WorkRequest aWorkRequest = new WorkRequest();
            	Date aDate = new Date();
            	     
            	aWorkRequest.setWorkRequestNumber(rs.getString("wrNumber"));
            	aWorkRequest.setAssetNumber(rs.getString("assetNumber"));
            	aWorkRequest.setAssetDescription(rs.getString("assetDescription"));
            	aWorkRequest.setWorkRequestType(rs.getString("wrType"));
            	aWorkRequest.setWorkRequestStatus(rs.getString("wrStatus"));
            	aWorkRequest.setWoNumber(rs.getString("woNumber"));
            	aWorkRequest.setWoStatus(rs.getString("woStatus"));
            	//aDate = rs.getTimestamp("cmBreakdownDate");
            	//aWorkRequest.setCmBreakdownDate(aDate);
            	aWorkRequest.setCmBreakdownDate(rs.getString("cmBreakdownDate"));
            	//aDate = rs.getTimestamp("pmScheduleDate");
            	//aWorkRequest.setPmScheduleDate(aDate);
            	aWorkRequest.setPmScheduleDate(rs.getString("pmScheduleDate"));
            	aWorkRequest.setCreatedby(rs.getString("createdBy"));
            	logger.debug("CREATED BY " +aWorkRequest.getCreatedby() );           	
            	
            	aWorkRequest.setMaintenancevendor(rs.getString("maintenanceVendor"));
            	aWorkRequest.setAssetlocation(rs.getString("assetLocation"));
            	aWorkRequest.setAssetowner(rs.getString("assetOwner"));
            	aWorkRequest.setOwningdept(rs.getString("owningDept"));
            	aDate = rs.getTimestamp("createdDate");
            	aWorkRequest.setCreateddate(aDate);
            	//aWorkRequest.setCreateddate(rs.getDate("createdDate"));
            	
            	aWorkRequest.setSerialnumber(rs.getString("serialNumber"));
            	aWorkRequest.setManufacturer(rs.getString("manufacturer"));
            	aWorkRequest.setBrand(rs.getString("brand"));
            	aWorkRequest.setModel(rs.getString("model"));

//            	aWorkRequest.setDisinfection(rs.getString("disinfectionStatus"));
//            	aWorkRequest.setEquipmentSent(rs.getString("equipSentToWorkshop"));
//            	aWorkRequest.setHaContactPerson(rs.getString("haContactPerson"));
//            	aWorkRequest.setHaContactPhone(rs.getString("haContactPhone"));
//            	aWorkRequest.setHaContactEmail(rs.getString("haContactEmail"));
            	//aWorkRequest.setRequestedFor(rs.getInt("requestedFor"));
            	aWorkRequest.setRequestedFor(rs.getString("requestedFor"));
            	aWorkRequest.setDescription(rs.getString("description"));
//            	aWorkRequest.setMaintVendor(rs.getString("maintenanceVendorType"));
//            	aWorkRequest.setContractNumber(rs.getString("contractNumber"));
//            	aWorkRequest.setMaintenancePlan(rs.getString("maintenancePlan"));
//            	aWorkRequest.setMaintenanceJoinDate(rs.getString("maintenanceJoinDate"));
//            	aWorkRequest.setMaintenanceExpiryDate(rs.getString("maintenanceExpiryDate"));
//            	aWorkRequest.setSupplierAgreementNumber(rs.getString("supplierAgreementNumber"));
//            	aWorkRequest.setAutoSend(rs.getString("autoSend"));
//            	aWorkRequest.setMaintContact(rs.getString("maintenanceContactPerson"));
//            	aWorkRequest.setMaintPhone(rs.getString("maintenanceContactPhone"));
//            	aWorkRequest.setMaintFax(rs.getString("maintenanceContactFax"));
//            	aWorkRequest.setMaintEmail(rs.getString("maintenanceContactEmail"));
            	aWorkRequest.setEamOrg(rs.getString("org"));
            	
                logger.debug("Retrieved Work Request Number " + aWorkRequest.getWorkRequestNumber());
                
                return aWorkRequest;
            }
     
        });
        logger.debug("No of rec="+listWorkRequest.size());
        logger.debug("Limit="+searchCriteria.getQueryLimit());
        if (listWorkRequest.size()>searchCriteria.getQueryLimit()) {
        	int wrsize = listWorkRequest.size();
        	listWorkRequest.clear();
        	WorkRequest tWorkRequest = new WorkRequest();
        	//tWorkRequest.setDescription(String.valueOf(wrsize));
        	tWorkRequest.setMaintFax(String.valueOf(wrsize));
        	listWorkRequest.add(tWorkRequest);
        }
      //Sorting records by wr number (descending)
        if (listWorkRequest.size()>1) {
        	logger.debug("Sorting Start "+new Date());
	        Collections.sort(listWorkRequest, new Comparator<WorkRequest>() {
	            public int compare(WorkRequest wr2, WorkRequest wr1) {
	                  return wr1.getWorkRequestNumber().compareTo(wr2.getWorkRequestNumber());
	             }
	        });
	        logger.debug("Sorting Complete "+new Date());
        }
        return listWorkRequest;
    }

    @Override
    public List<Dropdown> getMfgLookupList(String lookupType) throws Exception {
//        String sql = "SELECT LOOKUP_CODE, MEANING from mfg_lookups where lookup_type=? order by MEANING";
        
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT LOOKUP_CODE, MEANING from mfg_lookups where lookup_type=? ");
        if ("WIP_EAM_WORK_REQ_STATUS".equals(lookupType)) {
       	 sqlBuffer.append("AND LOOKUP_CODE NOT IN (2, 1) " +
       	 		" ORDER BY CASE\r\n" + 
       	 		"              WHEN MEANING = 'Awaiting Work Order' THEN '2'\r\n" + 
       	 		"              WHEN MEANING = 'On Work Order' THEN '3'\r\n" + 
       	 		"              WHEN MEANING = 'Rejected' THEN '4'\r\n" + 
       	 		"              WHEN MEANING = 'Cancelled by User' THEN '5'\r\n" + 
       	 		"              WHEN MEANING = 'Complete' THEN '6'\r\n" + 
       	 		"              ELSE MEANING END ASC");
       } else if ("WIP_JOB_STATUS".equals(lookupType)) {
       	sqlBuffer.append("AND LOOKUP_CODE in (1, 3, 4, 6, 17) order by MEANING");
       }
       else if ("WIP_EAM_WORK_REQ_STATUS_LIMITED".equals(lookupType)) {
       	sqlBuffer.append("AND LOOKUP_CODE IN (3, 5, 7) order by LOOKUP_CODE");
       	lookupType = "WIP_EAM_WORK_REQ_STATUS";
       }
                
        List<Dropdown> listDropdown = jdbcTemplate.query(sqlBuffer.toString(), new Object[] {lookupType}, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(rs.getString("LOOKUP_CODE"));
            	aDropdown.setDesc(rs.getString("MEANING"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    }

    
    @Override
    public List<Dropdown> getEamOrgList() throws Exception{
        String sql = "select MIN(ORGANIZATION_ID) AS ORGANIZATION_ID, ORGANIZATION_CODE from mtl_parameters where organization_id IN (SELECT organization_id FROM XXEAM_ACCESSIBLE_ORG_V) group by organization_code order by organization_code";
        
        List<Dropdown> listDropdown = jdbcTemplate.query(sql, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(String.valueOf(rs.getInt("ORGANIZATION_ID")));
            	aDropdown.setDesc(rs.getString("ORGANIZATION_CODE"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    }    
    
    @Override
    public List<Dropdown> getDisinfectionList() throws Exception {
        String sql = "SELECT V.FLEX_VALUE name, V.DESCRIPTION descr FROM FND_FLEX_VALUE_SETS S, FND_FLEX_VALUES_VL V WHERE S.FLEX_VALUE_SET_ID = V.FLEX_VALUE_SET_ID AND S.FLEX_VALUE_SET_NAME = 'XXEAM_DISINFECTION_STATUS' AND V.ENABLED_FLAG = 'Y' AND TRUNC(SYSDATE) BETWEEN NVL(V.START_DATE_ACTIVE,TRUNC(SYSDATE)) AND  NVL(V.END_DATE_ACTIVE, TRUNC(SYSDATE)) order by V.DESCRIPTION";
        
        List<Dropdown> listDropdown = jdbcTemplate.query(sql, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(rs.getString("name"));
            	aDropdown.setDesc(rs.getString("descr"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    }  
    
    @Override
   	@Transactional(readOnly = true, rollbackFor = Exception.class)
       public List<AssetInfo> getWRAssetAttr(String wrNumber) throws SQLException, Exception {
           MapSqlParameterSource paramMap = new MapSqlParameterSource();
           
           logger.debug("getting work request asset info function" + wrNumber);
           StringBuffer sqlBuffer = new StringBuffer();

           sqlBuffer.append("SELECT ASSET_GROUP_ID, "
           		+ " ASSET_ORGANIZATION_ID,"
           		+ " ASSET_OWNING_DEPARTMENT_ID,"
           		+ " MAINTENANCE_BODY_NUMBER,"
           		+ " MAINTENANCE_BODY_TYPE,"
           		+ " MAINTENANCE_CONTRACT_NUMBER,"
           		+ " MAINTENANCE_PLAN,"
           		+ " MAINTENANCE_JOIN_DATE,"
           		+ " MAINTENANCE_EXPIRY_DATE,"
           		+ " SUPPLIER_AGREEMENT_NUMBER,"
           		+ " AUTO_SEND_WO_TO_SUPPLIER,"
           		+ " MAINTENANCE_CONTACT_PERSON,"
           		+ " MAINTENANCE_CONTACT_PHONE,"
           		+ " MAINTENANCE_CONTACT_FAX,"
           		+ " MAINTENANCE_CONTACT_EMAIL,"
           		+ " ASSET_NUMBER, LEGACY_SERIAL_NUMBER"
           		+ " FROM XXEAM_EXT_WORK_REQUESTS_V");
           sqlBuffer.append(" WHERE WORK_REQUEST_NUMBER = :wrNumber \n");
           paramMap.addValue("wrNumber", wrNumber);
                      
           List<AssetInfo> listAssetInfo = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<AssetInfo>() {
               @Override
               public AssetInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
	               	AssetInfo aAssetInfo = new AssetInfo();
	        
	               	aAssetInfo.setAssetGroup(rs.getLong("ASSET_GROUP_ID"));
	               	aAssetInfo.setAssetOrgId(rs.getLong("ASSET_ORGANIZATION_ID"));
	               	aAssetInfo.setAssetOwningDepartmentId(rs.getLong("ASSET_OWNING_DEPARTMENT_ID"));
	               	aAssetInfo.setMaintenanceBodyNum(rs.getString("MAINTENANCE_BODY_NUMBER"));
	               	aAssetInfo.setMaintenanceBodyType(rs.getString("MAINTENANCE_BODY_TYPE"));
	               	aAssetInfo.setContractNumber(rs.getString("MAINTENANCE_CONTRACT_NUMBER"));
	               	aAssetInfo.setMaintenancePlan(rs.getString("MAINTENANCE_PLAN"));
					aAssetInfo.setMaintenanceJoinDate(rs.getString("MAINTENANCE_JOIN_DATE"));
					aAssetInfo.setMaintenanceExpiryDate(rs.getString("MAINTENANCE_EXPIRY_DATE"));
					aAssetInfo.setSupplierAgreementNumber(rs.getString("SUPPLIER_AGREEMENT_NUMBER"));
					aAssetInfo.setAutoSend(rs.getString("AUTO_SEND_WO_TO_SUPPLIER"));
					aAssetInfo.setMaintContact(rs.getString("MAINTENANCE_CONTACT_PERSON"));
					aAssetInfo.setMaintPhone(rs.getString("MAINTENANCE_CONTACT_PHONE"));
					aAssetInfo.setMaintFax(rs.getString("MAINTENANCE_CONTACT_FAX"));
					aAssetInfo.setMaintEmail(rs.getString("MAINTENANCE_CONTACT_EMAIL"));
					aAssetInfo.setAssetNumber(rs.getString("ASSET_NUMBER"));
					aAssetInfo.setLegacySerialNumber(rs.getString("LEGACY_SERIAL_NUMBER"));
	                return aAssetInfo;
               }  
           });
           
		return listAssetInfo;
       }
    
    @Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<AssetInfo> getAssetAttr(String assetNumber, String[] org) throws SQLException, Exception {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();

        /*sqlBuffer.append("SELECT ASSET_DESCRIPTION, ASSET_GROUP, ASSET_GROUP_ID, ASSET_LOCATION_CODE, ASSET_LOCATION_DESC, ASSET_NUMBER, ASSET_ORGANIZATION_ID, ASSET_ORGANIZATION_CODE, ASSET_OWNER, ASSET_OWNER_DESC, ASSET_OWNING_DEPARTMENT, ASSET_OWNING_DEPARTMENT_ID, ASSET_OWNING_DEPARTMENT_DESC, ASSET_STATUS, DOB, MANUFACTURER, BRAND, MODEL, SERIAL_NUMBER, PURCHASE_PRICE, ASSET_SUPPLIER_NAME, ASSET_SUPPLIER_NUMBER, ASSET_SUPPLIER, RISK_LEVEL, PARENT_ASSET_NUMBER, AUTO_SEND_WO_TO_SUPPLIER, MAINTENANCE_CONTRACT_NUM, MAINTENANCE_BODY_NUM, MAINTENANCE_BODY, MAINTENANCE_BODY_TYPE, MAINTENANCE_CONTACT_EMAIL, MAINTENANCE_CONTACT_FAX_NUMBER, MAINTENANCE_CONTACT_PERSON, MAINTENANCE_CONTACT_PHONE, MAINTENANCE_EXPIRY_DATE,MAINTENANCE_JOIN_DATE, MAINTENANCE_INTERVAL, MAINTENANCE_PLAN, SUPPLIER_AGREEMENT_NUMBER, ITEM_CODE_NUMBER, MAINTENANCE_OBJECT_ID, MAINTENANCE_OBJECT_TYPE, LEGACY_SERIAL_NUMBER from xxeam_create_wr_asset_v \n");
        sqlBuffer.append(" WHERE ASSET_NUMBER = :assetNumber \n");
        sqlBuffer.append(" AND ASSET_ORGANIZATION_ID IN (select organization_id from xxeam_accessible_org_v) \n");
        sqlBuffer.append(" AND xxeam_maint_body_sec_chk_wo(maintenance_body_num) = 'Y' \n");
        sqlBuffer.append(" AND (fnd_global.resp_name NOT LIKE '%EAM Maintenance User (IT Asset)'\r\n" + 
        		"OR (fnd_global.resp_name LIKE '%EAM Maintenance User (IT Asset)'\r\n" + 
        		"AND ASSET_OWNING_DEPARTMENT_ID IN\r\n" + 
        		"  (SELECT department_id FROM xxeam_accessible_dept_v\r\n" + 
        		"  )) )\r\n" + 
        		"AND ( (fnd_global.resp_name NOT LIKE '%EAM In-House Maintenance User (Non-IT Asset)'\r\n" + 
        		"AND fnd_global.resp_name NOT LIKE '%EAM Third-Party Maintenance User (Non-IT Asset)')\r\n" + 
        		"OR ((fnd_global.resp_name LIKE '%EAM In-House Maintenance User (Non-IT Asset)'\r\n" + 
        		"OR fnd_global.resp_name LIKE '%EAM Third-Party Maintenance User (Non-IT Asset)')\r\n" + 
        		"AND \r\n" + 
        		" MAINTENANCE_BODY_TYPE\r\n" + 
        		"   IS NOT NULL )) \n");*/
        sqlBuffer.append("SELECT ASSET_DESCRIPTION, ASSET_GROUP, ASSET_GROUP_ID, ASSET_LOCATION_CODE, ASSET_LOCATION_DESC, ASSET_NUMBER, ASSET_ORGANIZATION_ID, ASSET_ORGANIZATION_CODE, ASSET_OWNER, ASSET_OWNER_DESC, ASSET_OWNING_DEPARTMENT, ASSET_OWNING_DEPARTMENT_ID, ASSET_OWNING_DEPARTMENT_DESC, ASSET_STATUS, DOB, MANUFACTURER, BRAND, MODEL, SERIAL_NUMBER, PURCHASE_PRICE, ASSET_SUPPLIER_NAME, ASSET_SUPPLIER_NUMBER, ASSET_SUPPLIER, RISK_LEVEL, PARENT_ASSET_NUMBER, AUTO_SEND_WO_TO_SUPPLIER, MAINTENANCE_CONTRACT_NUM, MAINTENANCE_BODY_NUM, MAINTENANCE_BODY, MAINTENANCE_BODY_TYPE, MAINTENANCE_CONTACT_EMAIL, MAINTENANCE_CONTACT_FAX_NUMBER, MAINTENANCE_CONTACT_PERSON, MAINTENANCE_CONTACT_PHONE, MAINTENANCE_EXPIRY_DATE,MAINTENANCE_JOIN_DATE, MAINTENANCE_INTERVAL, MAINTENANCE_PLAN, SUPPLIER_AGREEMENT_NUMBER, ITEM_CODE_NUMBER, MAINTENANCE_OBJECT_ID, MAINTENANCE_OBJECT_TYPE, LEGACY_SERIAL_NUMBER from xxeam_create_wr_asset_v \n");
        sqlBuffer.append(" WHERE ASSET_NUMBER = :assetNumber \n");
        sqlBuffer.append(" AND ASSET_ORGANIZATION_ID IN (select organization_id from xxeam_accessible_org_v) \n");
        sqlBuffer.append(" AND xxeam_maint_body_sec_chk_wo(maintenance_body_num) = 'Y' \n");
//        sqlBuffer.append(" AND (fnd_global.resp_name NOT LIKE '%EAM Maintenance User (IT Asset)'\r\n" + 
//        		"OR (fnd_global.resp_name LIKE '%EAM Maintenance User (IT Asset)'\r\n" + 
//        		"AND ASSET_OWNING_DEPARTMENT_ID IN\r\n" + 
//        		"  (SELECT department_id FROM xxeam_accessible_dept_v\r\n" + 
//        		"  )) )\r\n" + " AND fnd_global.resp_name LIKE '%(IT Asset)' \n");
//        sqlBuffer.append(" UNION ALL \n");
//        sqlBuffer.append("SELECT ASSET_DESCRIPTION, ASSET_GROUP, ASSET_GROUP_ID, ASSET_LOCATION_CODE, ASSET_LOCATION_DESC, ASSET_NUMBER, ASSET_ORGANIZATION_ID, ASSET_ORGANIZATION_CODE, ASSET_OWNER, ASSET_OWNER_DESC, ASSET_OWNING_DEPARTMENT, ASSET_OWNING_DEPARTMENT_ID, ASSET_OWNING_DEPARTMENT_DESC, ASSET_STATUS, DOB, MANUFACTURER, BRAND, MODEL, SERIAL_NUMBER, PURCHASE_PRICE, ASSET_SUPPLIER_NAME, ASSET_SUPPLIER_NUMBER, ASSET_SUPPLIER, RISK_LEVEL, PARENT_ASSET_NUMBER, AUTO_SEND_WO_TO_SUPPLIER, MAINTENANCE_CONTRACT_NUM, MAINTENANCE_BODY_NUM, MAINTENANCE_BODY, MAINTENANCE_BODY_TYPE, MAINTENANCE_CONTACT_EMAIL, MAINTENANCE_CONTACT_FAX_NUMBER, MAINTENANCE_CONTACT_PERSON, MAINTENANCE_CONTACT_PHONE, MAINTENANCE_EXPIRY_DATE,MAINTENANCE_JOIN_DATE, MAINTENANCE_INTERVAL, MAINTENANCE_PLAN, SUPPLIER_AGREEMENT_NUMBER, ITEM_CODE_NUMBER, MAINTENANCE_OBJECT_ID, MAINTENANCE_OBJECT_TYPE, LEGACY_SERIAL_NUMBER from xxeam_create_wr_asset_v \n");
//        sqlBuffer.append(" WHERE ASSET_NUMBER = :assetNumber2 \n");
//        sqlBuffer.append(" AND ASSET_ORGANIZATION_ID IN (select organization_id from xxeam_accessible_org_v) \n");
//        sqlBuffer.append(" AND xxeam_maint_body_sec_chk_wo(maintenance_body_num) = 'Y' \n");
//        sqlBuffer.append("AND ( (fnd_global.resp_name NOT LIKE '%EAM In-House Maintenance User (Non-IT Asset)'\r\n" + 
//        		"AND fnd_global.resp_name NOT LIKE '%EAM Third-Party Maintenance User (Non-IT Asset)')\r\n" + 
//        		"OR ((fnd_global.resp_name LIKE '%EAM In-House Maintenance User (Non-IT Asset)'\r\n" + 
//        		"OR fnd_global.resp_name LIKE '%EAM Third-Party Maintenance User (Non-IT Asset)')\r\n" + 
//        		"AND \r\n" + 
//        		" MAINTENANCE_BODY_TYPE\r\n" + 
//        		"   IS NOT NULL )) \n" + " AND fnd_global.resp_name LIKE '%(Non-IT Asset)' \n");
        int assetAttrRespType = getAssetAttrRespType();
        if(assetAttrRespType==1) {
        	sqlBuffer.append(" AND ASSET_OWNING_DEPARTMENT_ID IN (SELECT department_id FROM xxeam_accessible_dept_v) \n");
        }
        if(assetAttrRespType==2) {
        	sqlBuffer.append(" AND MAINTENANCE_BODY_TYPE IS NOT NULL \n");
        }
//        1 --> fnd_global.resp_name LIKE '%EAM Maintenance User (IT Asset)'
//        2 -->(fnd_global.resp_name LIKE '%EAM In-House Maintenance User (Non-IT Asset)' OR 
//        		fnd_global.resp_name LIKE '%EAM Third-Party Maintenance User (Non-IT Asset)')
//		  0 --> Other
        logger.debug("gatest assetAttrRespType : " + assetAttrRespType);
        logger.debug("gatest sqlBuffer : " + sqlBuffer.toString());

        paramMap.addValue("assetNumber", assetNumber);
//      paramMap.addValue("assetNumber2", assetNumber);
        List<AssetInfo> listAssetInfo = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<AssetInfo>() {
            @Override
            public AssetInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	AssetInfo aAssetInfo = new AssetInfo();
     
            	aAssetInfo.setAssetDescription(rs.getString("ASSET_DESCRIPTION"));
            	aAssetInfo.setAssetGroup(rs.getLong("ASSET_GROUP_ID"));
            	aAssetInfo.setAssetGroupName(rs.getString("ASSET_GROUP"));
            	aAssetInfo.setAssetlocation(rs.getString("ASSET_LOCATION_CODE"));
            	aAssetInfo.setAssetLocationDesc(rs.getString("ASSET_LOCATION_DESC"));
            	aAssetInfo.setAssetNumber(rs.getString("ASSET_NUMBER"));
            	aAssetInfo.setAssetOrganisation(rs.getString("ASSET_ORGANIZATION_CODE"));
            	aAssetInfo.setAssetOwner(rs.getString("ASSET_OWNER"));
            	aAssetInfo.setAssetOwnerDesc(rs.getString("ASSET_OWNER_DESC"));
            	aAssetInfo.setOwningDept(rs.getString("ASSET_OWNING_DEPARTMENT"));
            	aAssetInfo.setAssetStatus(rs.getString("ASSET_STATUS"));
            	aAssetInfo.setDob(rs.getString("DOB"));
            	aAssetInfo.setSerialnumber(rs.getString("SERIAL_NUMBER"));
            	aAssetInfo.setPurchasePrice(rs.getString("PURCHASE_PRICE"));
            	aAssetInfo.setAssetSupplier(rs.getString("ASSET_SUPPLIER"));
            	aAssetInfo.setItemCodeNumber(rs.getString("ITEM_CODE_NUMBER"));
            	//aAssetInfo.setFatherAssetNumber(rs.getString("FATHER_ASSET_NUMBER"));
            	aAssetInfo.setFatherAssetNumber(rs.getString("PARENT_ASSET_NUMBER"));
            	//aAssetInfo.setContractNumber(rs.getString("CONTRACT_NUM"));
            	aAssetInfo.setContractNumber(rs.getString("MAINTENANCE_CONTRACT_NUM"));
            	aAssetInfo.setMaintenanceBodyNum(rs.getString("MAINTENANCE_BODY_NUM"));
            	aAssetInfo.setMaintenanceBody(rs.getString("MAINTENANCE_BODY"));
            	aAssetInfo.setMaintenanceBodyType(rs.getString("MAINTENANCE_BODY_TYPE"));
            	aAssetInfo.setMaintContact(rs.getString("MAINTENANCE_CONTACT_PERSON"));
            	aAssetInfo.setMaintPhone(rs.getString("MAINTENANCE_CONTACT_PHONE"));
            	aAssetInfo.setMaintenanceExpiryDate(rs.getString("MAINTENANCE_EXPIRY_DATE"));
            	aAssetInfo.setMaintenanceJoinDate(rs.getString("MAINTENANCE_JOIN_DATE"));    
            	aAssetInfo.setMaintenancePlan(rs.getString("MAINTENANCE_PLAN"));
            	aAssetInfo.setAutoSend(rs.getString("AUTO_SEND_WO_TO_SUPPLIER"));           	
            	aAssetInfo.setMaintFax(rs.getString("MAINTENANCE_CONTACT_FAX_NUMBER"));
            	aAssetInfo.setMaintEmail(rs.getString("MAINTENANCE_CONTACT_EMAIL"));
            	aAssetInfo.setSupplierAgreementNumber(rs.getString("SUPPLIER_AGREEMENT_NUMBER"));
            	aAssetInfo.setMaintenanceInterval(rs.getString("MAINTENANCE_INTERVAL"));
            	aAssetInfo.setAssetOwningDepartmentDesc(rs.getString("ASSET_OWNING_DEPARTMENT_DESC"));
            	aAssetInfo.setManufacturer(rs.getString("MANUFACTURER"));
            	aAssetInfo.setBrand(rs.getString("BRAND"));
            	aAssetInfo.setModel(rs.getString("MODEL"));
            	aAssetInfo.setAssetOwningDepartmentId(rs.getLong("ASSET_OWNING_DEPARTMENT_ID"));
            	aAssetInfo.setAssetOrgId(rs.getLong("ASSET_ORGANIZATION_ID"));
            	aAssetInfo.setRiskLevel(rs.getString("RISK_LEVEL"));
            	aAssetInfo.setMaintenanceObjectId(rs.getLong("MAINTENANCE_OBJECT_ID"));
            	aAssetInfo.setMaintenanceObjectType(rs.getLong("MAINTENANCE_OBJECT_TYPE"));
            	aAssetInfo.setLegacySerialNumber(rs.getString("LEGACY_SERIAL_NUMBER"));
                return aAssetInfo;
            }
     
        });
     
        return listAssetInfo;
        
    }
    
    @Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<AssetInfo> chkAssetAttr(String assetNumber, String[] org) throws SQLException, Exception {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("SELECT ASSET_DESCRIPTION, ASSET_GROUP, ASSET_GROUP_ID, ASSET_LOCATION_CODE, ASSET_LOCATION_DESC, ASSET_NUMBER, ASSET_ORGANIZATION_ID, ASSET_ORGANIZATION_CODE, ASSET_OWNER, ASSET_OWNER_DESC, ASSET_OWNING_DEPARTMENT, ASSET_OWNING_DEPARTMENT_ID, ASSET_OWNING_DEPARTMENT_DESC, ASSET_STATUS, DOB, MANUFACTURER, BRAND, MODEL, SERIAL_NUMBER, PURCHASE_PRICE, ASSET_SUPPLIER_NAME, ASSET_SUPPLIER_NUMBER, ASSET_SUPPLIER, RISK_LEVEL, PARENT_ASSET_NUMBER, AUTO_SEND_WO_TO_SUPPLIER, MAINTENANCE_CONTRACT_NUM, MAINTENANCE_BODY_NUM, MAINTENANCE_BODY, MAINTENANCE_BODY_TYPE, MAINTENANCE_CONTACT_EMAIL, MAINTENANCE_CONTACT_FAX_NUMBER, MAINTENANCE_CONTACT_PERSON, MAINTENANCE_CONTACT_PHONE, MAINTENANCE_EXPIRY_DATE,MAINTENANCE_JOIN_DATE, MAINTENANCE_INTERVAL, MAINTENANCE_PLAN, SUPPLIER_AGREEMENT_NUMBER, ITEM_CODE_NUMBER, MAINTENANCE_OBJECT_ID, MAINTENANCE_OBJECT_TYPE, LEGACY_SERIAL_NUMBER from xxeam_create_wr_asset_v \n");
        sqlBuffer.append(" WHERE ASSET_NUMBER = :assetNumber \n");
        sqlBuffer.append(" AND ASSET_ORGANIZATION_ID IN (select organization_id from xxeam_accessible_org_v) \n");
        sqlBuffer.append(" AND xxeam_maint_body_sec_chk_wo(maintenance_body_num) = 'Y' \n");
        sqlBuffer.append(" AND (fnd_global.resp_name NOT LIKE '%EAM Maintenance User (IT Asset)'\r\n" + 
        		"OR (fnd_global.resp_name LIKE '%EAM Maintenance User (IT Asset)'\r\n" + 
        		"AND ASSET_OWNING_DEPARTMENT_ID IN\r\n" + 
        		"  (SELECT department_id FROM xxeam_accessible_dept_v\r\n" + 
        		"  )) )\r\n" );
        
        paramMap.addValue("assetNumber", assetNumber);
        
        List<AssetInfo> listAssetInfo = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<AssetInfo>() {
            @Override
            public AssetInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	AssetInfo aAssetInfo = new AssetInfo();
     
            	aAssetInfo.setAssetDescription(rs.getString("ASSET_DESCRIPTION"));
            	aAssetInfo.setAssetGroup(rs.getLong("ASSET_GROUP_ID"));
            	aAssetInfo.setAssetGroupName(rs.getString("ASSET_GROUP"));
            	aAssetInfo.setAssetlocation(rs.getString("ASSET_LOCATION_CODE"));
            	aAssetInfo.setAssetLocationDesc(rs.getString("ASSET_LOCATION_DESC"));
            	aAssetInfo.setAssetNumber(rs.getString("ASSET_NUMBER"));
            	aAssetInfo.setAssetOrganisation(rs.getString("ASSET_ORGANIZATION_CODE"));
            	aAssetInfo.setAssetOwner(rs.getString("ASSET_OWNER"));
            	aAssetInfo.setAssetOwnerDesc(rs.getString("ASSET_OWNER_DESC"));
            	aAssetInfo.setOwningDept(rs.getString("ASSET_OWNING_DEPARTMENT"));
            	aAssetInfo.setAssetStatus(rs.getString("ASSET_STATUS"));
            	aAssetInfo.setDob(rs.getString("DOB"));
            	aAssetInfo.setSerialnumber(rs.getString("SERIAL_NUMBER"));
            	aAssetInfo.setPurchasePrice(rs.getString("PURCHASE_PRICE"));
            	aAssetInfo.setAssetSupplier(rs.getString("ASSET_SUPPLIER"));
            	aAssetInfo.setItemCodeNumber(rs.getString("ITEM_CODE_NUMBER"));
            	//aAssetInfo.setFatherAssetNumber(rs.getString("FATHER_ASSET_NUMBER"));
            	aAssetInfo.setFatherAssetNumber(rs.getString("PARENT_ASSET_NUMBER"));
            	//aAssetInfo.setContractNumber(rs.getString("CONTRACT_NUM"));
            	aAssetInfo.setContractNumber(rs.getString("MAINTENANCE_CONTRACT_NUM"));
            	aAssetInfo.setMaintenanceBodyNum(rs.getString("MAINTENANCE_BODY_NUM"));
            	aAssetInfo.setMaintenanceBody(rs.getString("MAINTENANCE_BODY"));
            	aAssetInfo.setMaintenanceBodyType(rs.getString("MAINTENANCE_BODY_TYPE"));
            	aAssetInfo.setMaintContact(rs.getString("MAINTENANCE_CONTACT_PERSON"));
            	aAssetInfo.setMaintPhone(rs.getString("MAINTENANCE_CONTACT_PHONE"));
            	aAssetInfo.setMaintenanceExpiryDate(rs.getString("MAINTENANCE_EXPIRY_DATE"));
            	aAssetInfo.setMaintenanceJoinDate(rs.getString("MAINTENANCE_JOIN_DATE"));    
            	aAssetInfo.setMaintenancePlan(rs.getString("MAINTENANCE_PLAN"));
            	aAssetInfo.setAutoSend(rs.getString("AUTO_SEND_WO_TO_SUPPLIER"));           	
            	aAssetInfo.setMaintFax(rs.getString("MAINTENANCE_CONTACT_FAX_NUMBER"));
            	aAssetInfo.setMaintEmail(rs.getString("MAINTENANCE_CONTACT_EMAIL"));
            	aAssetInfo.setSupplierAgreementNumber(rs.getString("SUPPLIER_AGREEMENT_NUMBER"));
            	aAssetInfo.setMaintenanceInterval(rs.getString("MAINTENANCE_INTERVAL"));
            	aAssetInfo.setAssetOwningDepartmentDesc(rs.getString("ASSET_OWNING_DEPARTMENT_DESC"));
            	aAssetInfo.setManufacturer(rs.getString("MANUFACTURER"));
            	aAssetInfo.setBrand(rs.getString("BRAND"));
            	aAssetInfo.setModel(rs.getString("MODEL"));
            	aAssetInfo.setAssetOwningDepartmentId(rs.getLong("ASSET_OWNING_DEPARTMENT_ID"));
            	aAssetInfo.setAssetOrgId(rs.getLong("ASSET_ORGANIZATION_ID"));
            	aAssetInfo.setRiskLevel(rs.getString("RISK_LEVEL"));
            	aAssetInfo.setMaintenanceObjectId(rs.getLong("MAINTENANCE_OBJECT_ID"));
            	aAssetInfo.setMaintenanceObjectType(rs.getLong("MAINTENANCE_OBJECT_TYPE"));
            	aAssetInfo.setLegacySerialNumber(rs.getString("LEGACY_SERIAL_NUMBER"));
                return aAssetInfo;
            }
     
        });
     
        return listAssetInfo;
        
    }
    
    @Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<AttachmentInfo> getAttachmentInfo(String wrNumber) throws SQLException, Exception {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        logger.info("***wrNumber="+wrNumber);
        sqlBuffer.append(" SELECT * FROM XXEAM_EXT_WORK_REQ_ATT_V");
        sqlBuffer.append(" WHERE WR_NO = :wrNumber");
        
        paramMap.addValue("wrNumber", wrNumber);
        
        List<AttachmentInfo> listAttachmentInfo = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<AttachmentInfo>() {
            @Override
            public AttachmentInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	AttachmentInfo aAttachmentInfo = new AttachmentInfo();
     
            	aAttachmentInfo.setDocumentId(rs.getString("DOC_ID"));
            	aAttachmentInfo.setFileName(rs.getString("FILE_NAME"));
            	aAttachmentInfo.setTitle(rs.getString("TITLE"));
            	aAttachmentInfo.setDescription(rs.getString("DESCRIPTION"));
            	logger.info("***setFileName="+rs.getString("FILE_NAME"));
            	
                return aAttachmentInfo;
            }
        });
        logger.info("***Size="+listAttachmentInfo.size());
        return listAttachmentInfo;
    }
    
	@Transactional(rollbackFor = Exception.class)
	public String saveWorkRequest(WorkRequest workRequest, String[] org, String userId, String respId, String appId)
			throws Exception {
		
		int wrPriority = 20;
		//Perform validation
//		String validateResult;
//		validateResult = validateWRData(workRequest);
//		if (!validateResult.equals("PASS")) {
//			return validateResult;
//		}
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("DECLARE \n");
		sqlBuilder.append("  l_wip_eam_rec       wip_eam_work_requests%ROWTYPE; \n");
		sqlBuilder.append("  l_wip_eam_ext_rec   XXEAM_WORK_REQUESTS_EXT%ROWTYPE; \n");
		sqlBuilder.append("  l_user_id   number; \n");
		sqlBuilder.append("  v_chk_update_date varchar2(20); \n");
		sqlBuilder.append("BEGIN \n");
		sqlBuilder.append("  l_wip_eam_rec.asset_number            := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.asset_group              := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.organization_id          := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.work_request_status_id   := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.work_request_priority_id := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.work_request_owning_dept := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.work_request_type_id     := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.phone_number             := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.e_mail                   := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.contact_preference       := NULL; \n");
		sqlBuilder.append("  l_wip_eam_rec.notify_originator        := 2; \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute1               := NULL; \n");
        if ("CREATE".equals(workRequest.getMode())) {
        	sqlBuilder.append("  l_wip_eam_rec.expected_resolution_date := SYSDATE;\n");
        } else {
    		sqlBuilder.append("  l_wip_eam_rec.expected_resolution_date       := NULL; \n");
        }
		sqlBuilder.append("  l_wip_eam_rec.work_request_id          := ?;  \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute1               := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute2               := ?;  \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute3               := ?;  \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute4               := ?;  \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute5               := ?;  \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute6               := ?;  \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute7               := ?;  \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute8               := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute10              := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute11              := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute12              := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute13              := ?; \n");
		sqlBuilder.append("  l_wip_eam_rec.attribute14              := ?; \n");
		sqlBuilder.append("	 select user_id into l_user_id from fnd_user where user_name = ?;\n");
		sqlBuilder.append("  l_wip_eam_rec.CREATED_FOR              := l_user_id; \n");
		sqlBuilder.append("  l_wip_eam_ext_rec.cm_breakdown_date          := to_date(?, 'DD/MM/YYYY HH24:MI');\n");
		sqlBuilder.append("  l_wip_eam_ext_rec.disinfection_status        := ?;\n");
		sqlBuilder.append("  l_wip_eam_ext_rec.pm_schedule_date           := to_date(?, 'DD/MM/YYYY HH24:MI');\n");
		sqlBuilder.append("  l_wip_eam_ext_rec.equipment_sent_to_workshop := ?;\n");
		sqlBuilder.append("  l_wip_eam_ext_rec.cancel_reject_remark       := ?;\n");
		sqlBuilder.append("  l_wip_eam_rec.DESCRIPTION   := ?;\n");
		sqlBuilder.append("  l_wip_eam_rec.MAINTENANCE_OBJECT_TYPE   := 3;\n");	
//		sqlBuilder.append("  l_wip_eam_ext_rec.WORK_REQUEST_DESCRIPTION   := ?;\n");
		if("UPDATE".equals(workRequest.getMode())) {
			logger.debug("LastUpdateDate="+workRequest.getlastUpdateDate());
			sqlBuilder.append(" select to_char(last_update_date, 'DD/MM/YYYY HH24:MI:SS') into v_chk_update_date from WIP_EAM_WORK_REQUESTS where work_request_id = l_wip_eam_rec.work_request_id; \n");
			sqlBuilder.append(" if v_chk_update_date <> '" + workRequest.getlastUpdateDate() + "' then \n");
			sqlBuilder.append(" 	raise_application_error( -20001, 'Record has been updated!' ); else \n");
			sqlBuilder.append(" update WIP_EAM_WORK_REQUESTS set last_update_date = sysdate where work_request_id = l_wip_eam_rec.work_request_id; end if; \n");
		}
		sqlBuilder.append("  xxeam_workrequest_pkg.work_request_import(\n");
		sqlBuilder.append("  p_api_version      => 1.0, \n");
		sqlBuilder.append("  p_init_msg_list    =>   fnd_api.g_true, \n");
		sqlBuilder.append("  p_commit           =>   fnd_api.g_false, \n");
		sqlBuilder.append("  p_validation_level => 	fnd_api.g_valid_level_full, \n");
		sqlBuilder.append("  x_return_status    =>   ?, \n");
		sqlBuilder.append("  x_msg_count        =>   ?, \n");
		sqlBuilder.append("  x_msg_data         =>   ?, \n");
		sqlBuilder.append("  p_mode             =>   ?, \n");
		sqlBuilder.append("  p_work_request_rec => 	l_wip_eam_rec, \n");
		sqlBuilder.append("  p_work_request_ext_rec => 	l_wip_eam_ext_rec, \n");
		sqlBuilder.append("  p_request_log      =>   ?, \n");
		//sqlBuilder.append("  p_user_id          =>   ?, \n");
		sqlBuilder.append("  p_user_id          =>    fnd_global.user_id, \n");
		sqlBuilder.append( "  x_work_request_id  =>   ?, \n");
		sqlBuilder.append( "  x_work_order_number  =>   ?, \n");
		sqlBuilder.append( "  p_attachment_flag  =>   ?, \n");
		sqlBuilder.append( "  p_document_id  =>   ?); \n");
		sqlBuilder.append("END;");	
//		sqlBuilder.append("{CALL EDIS_STM.RTN_TBL_PRTY(?, ?, ?)}");
		String sql = sqlBuilder.toString();
		
		Connection connection = null;
		CallableStatement cs = null;
		PreparedStatement pstmt = null;
        String result = "";
        
		try {
			connection = ConnectionProvider.getInitConnection(userId, respId, appId);
			//connection = jdbcTemplate.getDataSource().getConnection();
			cs = connection.prepareCall(sql);
			
			AssetInfo assetInfo = null;
						
			if("UPDATE".equals(workRequest.getMode())) {
				if("5".equals(workRequest.getWrStatus()) || "7".equals(workRequest.getWrStatus())) {
					List<AssetInfo> assetInfoList = getWRAssetAttr(workRequest.getWrNumber()); 
					assetInfo = assetInfoList.get(0);
				}
				else {
					assetInfo = workRequest.getAssetInfo();
				}
			}
			else {
				assetInfo = workRequest.getAssetInfo();
			}
			
			logger.debug("assetNumber " +workRequest.getAssetNumber() );
			logger.debug("LegacySerialNumber "+assetInfo.getLegacySerialNumber());
			logger.debug("getAssetGroup " +assetInfo.getAssetGroup() );
			logger.debug("getAssetOrgId " +assetInfo.getAssetOrgId() );
			logger.debug("getWrStatus " +workRequest.getWrStatus() );
			logger.debug("getAssetOwningDepartmentId " +assetInfo.getAssetOwningDepartmentId() );
			logger.debug("getRequestType " +workRequest.getRequestType() );
			logger.debug("getContactPhone " +workRequest.getContactPhone() );
			logger.debug("getContactEmail " +workRequest.getContactEmail() );
			logger.debug("getMode " +workRequest.getMode() );
			logger.debug("getWorkRequestId " +workRequest.getWorkRequestId() );
			
			logger.debug("getMaintenanceBody " +assetInfo.getMaintenanceBody() );
			logger.debug("getMaintenanceBodyType " +assetInfo.getMaintenanceBodyType() );		
			logger.debug("getContractNumber " +assetInfo.getContractNumber() );
			logger.debug("getMaintenancePlan " +assetInfo.getMaintenancePlan() );
			logger.debug("getMaintenanceJoinDate " +assetInfo.getMaintenanceJoinDate() );
			
			logger.debug("getMaintenanceExpiryDate " +assetInfo.getMaintenanceExpiryDate() );
			logger.debug("getSupplierAgreementNumber " +assetInfo.getSupplierAgreementNumber() );		
			logger.debug("getAutoSend " +assetInfo.getAutoSend() );
			logger.debug("getMaintContact " +assetInfo.getMaintContact() );
			logger.debug("getMaintPhone " +assetInfo.getMaintPhone() );		
			logger.debug("getMaintFax " +assetInfo.getMaintFax() );
			logger.debug("getMaintEmail " +assetInfo.getMaintEmail() );
			logger.debug("getContactPerson " +workRequest.getContactPerson() );
			logger.debug("getRequestedFor " +workRequest.getRequestedFor() );
			
			logger.debug("getBreakdownDateInput " +workRequest.getBreakdownDateInput() );
			logger.debug("getDisinfection " +workRequest.getDisinfection() );
			logger.debug("getScheduleDateInput " +workRequest.getScheduleDateInput() );
			logger.debug("getEquipmentSent " +workRequest.getEquipmentSent() );
			logger.debug("getRemark " +workRequest.getRemark() );
			logger.debug("getDescription " +workRequest.getDescription() );
			
			int wrStatus = 0;
			
			//cs.setString(1, workRequest.getAssetNumber());
			cs.setString(1, assetInfo.getLegacySerialNumber());
	        cs.setLong(2, assetInfo.getAssetGroup());
	        cs.setLong(3, assetInfo.getAssetOrgId());
	        cs.setLong(4, (workRequest.getWrStatus()!=null && !"".equals(workRequest.getWrStatus()))?Long.parseLong(workRequest.getWrStatus()):wrStatus );
	        cs.setLong(5, wrPriority);
	        cs.setLong(6, assetInfo.getAssetOwningDepartmentId());
	        cs.setLong(7, Long.parseLong(workRequest.getRequestType()));
	        cs.setString(8, workRequest.getContactPhone());
	        cs.setString(9, workRequest.getContactEmail());
	        if ("CREATE".equals(workRequest.getMode())) {
	            cs.setNull(10, Types.NUMERIC);
	        } else if("UPDATE".equals(workRequest.getMode())){
	            cs.setLong(10, workRequest.getWorkRequestId() );
	        } else {
	        	// For undefined getMode, treat as create
	        	cs.setNull(10, Types.NUMERIC);
	        }
	        //cs.setString(11, assetInfo.getMaintenanceBody());
	        cs.setString(11, assetInfo.getMaintenanceBodyNum());
	        cs.setString(12, assetInfo.getMaintenanceBodyType());
	        cs.setString(13, assetInfo.getContractNumber());
	        cs.setString(14, assetInfo.getMaintenancePlan());
	    	String newJoinDate =dateUtil.formatDateToStr( dateUtil.parseStrToDate(assetInfo.getMaintenanceJoinDate(), "dd/MM/yyyy")  , "yyyy/MM/dd HH:mm:ss" )     ;
	    	String newExpiryDate =dateUtil.formatDateToStr( dateUtil.parseStrToDate(assetInfo.getMaintenanceExpiryDate(), "dd/MM/yyyy")  , "yyyy/MM/dd HH:mm:ss" )     ;
	    	logger.debug("newJoinDate " + newJoinDate);
	        logger.debug("newExpiryDate " + newExpiryDate);
	        cs.setString(15, newJoinDate);
	        cs.setString(16, newExpiryDate);
	        cs.setString(17, assetInfo.getSupplierAgreementNumber());
	        cs.setString(18, assetInfo.getAutoSend());
	        cs.setString(19, assetInfo.getMaintContact());
	        cs.setString(20, assetInfo.getMaintPhone());
	        cs.setString(21, assetInfo.getMaintFax());
	        cs.setString(22, assetInfo.getMaintEmail());
	        cs.setString(23, workRequest.getContactPerson());
	        if(workRequest.getRequestedFor() != null && !"".equals(workRequest.getRequestedFor()) ){
	            //cs.setLong(24, Long.parseLong(workRequest.getRequestedFor() ) );
	        	cs.setString(24, workRequest.getRequestedFor() );
	        }else{
	            cs.setNull(24, Types.VARCHAR);
	        }              
	        cs.setString(25, workRequest.getBreakdownDateInput());
	        cs.setString(26, workRequest.getDisinfection());
	        cs.setString(27, workRequest.getScheduleDateInput());
	        cs.setString(28, workRequest.getEquipmentSent());
	        cs.setString(29, workRequest.getRemark());
	        cs.setString(30, "DESCRIPTION");
	        // Package will handle the description substring
	        //cs.setString(30, workRequest.getDescription());
	//        cs.setString(31, workRequest.getDescription());
	        cs.registerOutParameter(31, Types.VARCHAR);
	        cs.registerOutParameter(32, Types.NUMERIC);
	        cs.registerOutParameter(33, Types.VARCHAR);
	        cs.setString(34, workRequest.getMode());
	        cs.setString(35, workRequest.getDescription());
	//        cs.setNull(35, Types.VARCHAR);
	        //cs.setLong(36, Long.parseLong(userId)); // replaced by  fnd_global.user_id
	        cs.registerOutParameter(36, Types.NUMERIC);
	        cs.registerOutParameter(37, Types.VARCHAR); //x_work_order_number
	        cs.setString(38, workRequest.getAttachmentMode());
	        if(workRequest.getDocId() != null && !"".equals(workRequest.getDocId()) ){
	        	cs.setString(39, workRequest.getDocId());
	        } else {
	        	 cs.setNull(39, Types.NUMERIC); 
	        }
	        cs.execute();
			
	        logger.debug("out 1 " +cs.getString(31) );
	        logger.debug("out 2 " +cs.getString(32) );
	        logger.debug("out 3 " +cs.getString(33) );
	        logger.debug("out 4 " +cs.getString(36) );
	        logger.info("out 5 " +cs.getString(37) );
	        
	        if("S".equals(cs.getString(31))){
	        	//result = Long.toString(cs.getLong(36));
	        	result = Long.toString(cs.getLong(36)) + "&woNumber=" + cs.getString(37)+ "|" + cs.getString(31);
	        	logger.info("result=" + result);
	//        	result = "5289";	
	        }
	        else{
	            result = cs.getString(33);
	            if ((WorkOrderConstant.UPDATE).equals(workRequest.getMode())) {
            		sql = "update wip_eam_work_requests set last_update_date = to_date('"+workRequest.getlastUpdateDate()+"','DD/MM/YYYY HH24:MI:SS') where work_request_id = "+workRequest.getWorkRequestId();
            		pstmt = connection.prepareStatement(sql);
            		pstmt.execute();
            		//cs.executeUpdate();
            	}
	        }
		} catch (SQLException se) {
			if (se.getErrorCode() == 20001) {
				try {
					result = messageSource.getMessage("HA_ERROR_GENERAL_RECORDUPDATEDBYOTHER", null, Locale.US);
				} catch(Exception e) {
					// If no message found, set the default message
					result = "This record has been updated by another user, please cancel and requery the record. ";
				}
			}else {
				result = "<strong>Error! </strong> " + se.getMessage();
			}
		} catch (Exception e) {
			result = "<strong>Error! </strong> " + e.getMessage();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch(Exception e) {}
			}
			
			if (cs != null) {
				try {
					cs.close();
				} catch(Exception e) {}
			}

			if (connection != null) {
				try {
					connection.close();
				} catch(Exception e) {}
			}
		}
		
	    return result;
	}
	
    @Override
    @Transactional(rollbackFor=Exception.class)	
    public List<WorkRequest> searchWorkRequestDetail(SearchWorkRequest searchCriteria) throws Exception {
    	
        String wrNumber = searchCriteria.getWrNumber();
        String assetNumber = searchCriteria.getAssetNumber();
        String wrType = searchCriteria.getWrType();
        List wrStatus = searchCriteria.getWrStatus();
        String dateType = searchCriteria.getDateType();
        String dateFrom = searchCriteria.getDateFrom();
        String dateTo = searchCriteria.getDateTo();
        String maintenanceVendor = searchCriteria.getMaintenanceVendor();
        String assetLocation =searchCriteria.getAssetLocation();
        String assetOwner = searchCriteria.getAssetOwner();
        String owningDept = searchCriteria.getOwningDept();
        String createdBy = searchCriteria.getCreatedBy();
        String eanOrg = searchCriteria.getEamOrg();
        boolean criticalOnly = searchCriteria.getCriticalOnly();
        
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("SELECT v.ASSET_NUMBER assetNumber, v.ASSET_DESCRIPTION assetDescription, v.ITEM_CODE_NUMBER itemCode, v.ASSET_STATUS assetStatus, to_char(v.DOB, 'DD/MM/YYYY') dateOfBirth, v.SERIAL_NUMBER serialNumber, \n"); 
        sqlBuffer.append("v.ASSET_LOCATION_CODE assetLocation, v.asset_location_desc assetLocationDesc, v.ASSET_OWNER assetOwner, v.asset_owner_desc assetOwnerDesc, v.ASSET_OWNING_DEPARTMENT owningDept, v.asset_owning_department_desc assetOwningDepartmentDesc, \n"); 
        sqlBuffer.append("v.MANUFACTURER, v.BRAND, v.MODEL, v.ASSET_SUPPLIER assetSupplier, v.RISK_LEVEL riskLevel, v.PARENT_ASSET_NUMBER parentAssetNumber, v.PURCHASE_PRICE purchasePrice, \n");
        sqlBuffer.append("v.WORK_REQUEST_NUMBER wrNumber, v.WORK_REQUEST_STATUS_CODE wrStatus, v.WORK_REQUEST_STATUS, v.WORK_REQUEST_TYPE_CODE wrType, v.WORK_REQUEST_TYPE, TO_CHAR(v.CM_BREAKDOWN_DATE, 'dd/MM/yyyy hh24:MI') cmBreakdownDate, \n");
        sqlBuffer.append("TO_CHAR(v.PM_SCHEDULE_DATE, 'dd/MM/yyyy hh24:MI') pmScheduleDate, v.CREATION_DATE createdDate, (select user_name from fnd_user where user_id = v.WORK_REQUEST_CREATED_BY)  createdBy, \n");
        sqlBuffer.append("v.DISINFECTION_STATUS disinfectionStatus, v.EQUIPMENT_SENT_TO_WORKSHOP equipSentToWorkshop, v.HA_CONTACT_PERSON haContactPerson, v.HA_CONTACT_NUMBER haContactPhone, \n"); 
        sqlBuffer.append("v.HA_CONTACT_EMAIL haContactEmail, v.REQUESTED_FOR requestedFor, nvl(v.REQUESTED_FOR_EMPLOYEE,v.REQUESTED_FOR) requestedForEmployee, v.description, v.MAINTENANCE_BODY maintenanceBody, v.MAINTENANCE_BODY_NAME||decode(v.MAINTENANCE_BODY_NUMBER,null,null,' ('||v.MAINTENANCE_BODY_NUMBER||')') maintenanceVendor, v.MAINTENANCE_BODY_TYPE maintenanceBodyType, v.MAINTENANCE_BODY_NUMBER maintenanceVendorNum, \n"); 
        sqlBuffer.append("v.MAINTENANCE_CONTRACT_NUMBER contractNumber, v.MAINTENANCE_PLAN maintenancePlan, v.MAINTENANCE_JOIN_DATE maintenanceJoinDate, v.MAINTENANCE_EXPIRY_DATE maintenanceExpiryDate, \n");
        sqlBuffer.append("v.SUPPLIER_AGREEMENT_NUMBER supplierAgreementNumber, v.AUTO_SEND_WO_TO_SUPPLIER autoSend, v.MAINTENANCE_CONTACT_PERSON maintenanceContactPerson,  v.MAINTENANCE_CONTACT_PHONE maintenanceContactPhone, v.MAINTENANCE_CONTACT_FAX maintenanceContactFax, v.MAINTENANCE_CONTACT_EMAIL maintenanceContactEmail, v.MAINTENANCE_INTERVAL maintenanceInterval, \n"); 
        sqlBuffer.append("v.WORK_ORDER_NUMBER woNumber, v.ASSET_ORGANIZATION_CODE assetOrg, v.CANCEL_REJECT_REMARK remark, v.ORGANIZATION_CODE wrOrg, \n"); 
        //sqlBuffer.append("(select ewsv.work_order_status from eam_work_order_details ewod, EAM_WO_STATUSES_V ewsv where ewsv.status_id = ewod.user_defined_status_id and ewod.wip_entity_id = v.WIP_ENTITY_ID) woStatus \n"); 
        sqlBuffer.append("v.WORK_ORDER_STATUS woStatus, last_update_date, \n");
        sqlBuffer.append("(select listagg(notes, Chr(10)) within group (order by work_request_note_id) from wip_eam_work_req_notes where work_request_id = v.work_request_id) descriptionHistory \n");
        sqlBuffer.append("from XXEAM_EXT_WORK_REQUESTS_V v \n");        
        sqlBuffer.append("where xxeam_maint_body_sec_chk_wo(v.MAINTENANCE_BODY_NUMBER) = 'Y' \n"); 
        sqlBuffer.append("AND v.ORGANIZATION_ID in (SELECT organization_id FROM xxeam_accessible_org_v) \n"); 
        sqlBuffer.append("and v.OWNING_DEPARTMENT_ID in (SELECT department_id FROM xxeam_accessible_dept_v) \n");
        
        if(eanOrg != null && !"".equals(eanOrg)){
//        	sqlBuffer.append("AND MP.ORGANIZATION_CODE = :eanOrg "); 
        	sqlBuffer.append("AND v.ORGANIZATION_ID = :eanOrg ");
        	paramMap.addValue("eanOrg", eanOrg);
        }
               
        if(wrNumber != null && !"".equals(wrNumber)){
        	//sqlBuffer.append("AND WR.WORK_REQUEST_NUMBER LIKE :wrNumber "); 
        	sqlBuffer.append("AND v.WORK_REQUEST_NUMBER = :wrNumber ");
        	paramMap.addValue("wrNumber", wrNumber);
        }
        
        if(assetNumber != null && !"".equals(assetNumber)){
        	//sqlBuffer.append("AND WR.ASSET_NUMBER = :assetNumber ");
        	sqlBuffer.append("AND v.ASSET_NUMBER = :assetNumber ");
        	paramMap.addValue("assetNumber", assetNumber);
        }
        
        if(wrType != null && !"".equals(wrType)){
        	//sqlBuffer.append("AND LV3.LOOKUP_CODE = :wrType ");
        	sqlBuffer.append("AND v.WORK_REQUEST_TYPE_CODE = :wrType ");
        	paramMap.addValue("wrType", wrType);
        }
        
        if(wrStatus != null && wrStatus.size() != 0){
	        //sqlBuffer.append("AND LV1.LOOKUP_CODE in (:wrStatus)");
	        sqlBuffer.append("AND v.WORK_REQUEST_STATUS_CODE in (:wrStatus)");
	        paramMap.addValue("wrStatus", wrStatus);
        }
        
        if((WorkRequestConstant.BREAKDOWN_DATE).equals(dateType)) {
            if(dateFrom != null && !"".equals(dateFrom)){
            	//sqlBuffer.append("AND trunc(WREXT.CM_BREAKDOWN_DATE) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy')) ");
            	sqlBuffer.append("AND trunc(v.CM_BREAKDOWN_DATE) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy')) ");
              	paramMap.addValue("dateFrom", dateFrom);
            }   
            if(dateTo != null && !"".equals(dateTo)){
            	//sqlBuffer.append( "AND trunc(WREXT.CM_BREAKDOWN_DATE) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
            	sqlBuffer.append( "AND trunc(v.CM_BREAKDOWN_DATE) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
              	paramMap.addValue("dateTo", dateTo);
            }   
        }else if((WorkRequestConstant.CREATED_DATE).equals(dateType)) {
            if(dateFrom != null && !"".equals(dateFrom)){
            	//sqlBuffer.append("AND trunc(WR.CREATION_DATE) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy'))  ");
            	sqlBuffer.append("AND trunc(v.CREATION_DATE) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy'))  ");
              	paramMap.addValue("dateFrom", dateFrom);
            }   
            if(dateTo != null && !"".equals(dateTo)){
            	//sqlBuffer.append("AND trunc(WR.CREATION_DATE) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
            	sqlBuffer.append("AND trunc(v.CREATION_DATE) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
              	paramMap.addValue("dateTo", dateTo);
            }   
        }else if((WorkRequestConstant.SCHEDULE_DATE).equals(dateType)) {            
            if(dateFrom != null && !"".equals(dateFrom)){
            	//sqlBuffer.append("AND trunc(WREXT.PM_SCHEDULE_DATE) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy')) ");
            	sqlBuffer.append("AND trunc(v.PM_SCHEDULE_DATE) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy')) ");
              	paramMap.addValue("dateFrom", dateFrom);
            }   
            if(dateTo != null && !"".equals(dateTo)){
            	//sqlBuffer.append("AND trunc(WREXT.PM_SCHEDULE_DATE) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
            	sqlBuffer.append("AND trunc(v.PM_SCHEDULE_DATE) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
              	paramMap.addValue("dateTo", dateTo);
            }   
        }
                    
        if(maintenanceVendor != null && !"".equals(maintenanceVendor)){
        	//sqlBuffer.append("AND WR.ATTRIBUTE1 = :maintenanceVendor ");
        	sqlBuffer.append("AND v.MAINTENANCE_BODY_NUMBER = :maintenanceVendor ");
        	paramMap.addValue("maintenanceVendor", maintenanceVendor);
        }
        
        if(assetLocation != null && !"".equals(assetLocation)){
        	//sqlBuffer.append("AND HL.LOCATION_CODE = :assetLocation ");
        	sqlBuffer.append("AND v.ASSET_LOCATION_CODE = :assetLocation ");
        	paramMap.addValue("assetLocation", assetLocation);
        }
        
        if(assetOwner != null && !"".equals(assetOwner)){
        	//sqlBuffer.append("AND OWNER_ATTR.C_ATTRIBUTE4 = :assetOwner ");
        	sqlBuffer.append("AND v.ASSET_OWNER = :assetOwner ");
        	paramMap.addValue("assetOwner", assetOwner);
        }
        
        /*if(owningDept != null && !"".equals(owningDept)){
        	sqlBuffer.append("AND BD.DEPARTMENT_CODE = :owningDept ");
        	paramMap.addValue("owningDept", owningDept);
        }*/
        if(owningDept != null && !"".equals(owningDept)){
        	if (eanOrg!=null && !"".equals(eanOrg)) {
        		//sqlBuffer.append("AND BD.DEPARTMENT_CODE = :owningDept and BD.organization_id = MP.organization_id ");
        		sqlBuffer.append("AND v.OWNING_DEPARTMENT = :owningDept and v.organization_id = MP.organization_id ");
        	}else {
        		//sqlBuffer.append("AND BD.DEPARTMENT_CODE = :owningDept and BD.organization_id IN (select organization_id from XXEAM_ACCESSIBLE_ORG_V) ");
        		sqlBuffer.append("AND v.OWNING_DEPARTMENT = :owningDept and v.organization_id IN (select organization_id from XXEAM_ACCESSIBLE_ORG_V) ");
        	}
        	//sqlBuffer.append("AND BD.DEPARTMENT_CODE = :owningDept ");
        	paramMap.addValue("owningDept", owningDept);
        }
        
        if(createdBy != null && !"".equals(createdBy) ){
        	//sqlBuffer.append("AND WR.CREATED_BY = :createdBy ");
        	sqlBuffer.append("AND v.CREATED_BY = :createdBy ");
        	paramMap.addValue("createdBy", createdBy);
        }
        logger.debug("createdBy " + createdBy);
        if(criticalOnly){
        	//sqlBuffer.append("AND ITEM.SEGMENT1 in (select MEANING from MFG_LOOKUPS where LOOKUP_TYPE = 'XXEAM_CRITICAL_MEDICAL_EQUIP' and ENABLED_FLAG = 'Y' and nvl(trunc(START_DATE_ACTIVE),trunc(sysdate)) <= trunc(sysdate) and nvl(TRUNC(END_DATE_ACTIVE),trunc(sysdate)) >= trunc(sysdate)) ");
        	sqlBuffer.append("AND v.ITEM_CODE_NUMBER in (select MEANING from MFG_LOOKUPS where LOOKUP_TYPE = 'XXEAM_CRITICAL_MEDICAL_EQUIP' and ENABLED_FLAG = 'Y' and nvl(trunc(START_DATE_ACTIVE),trunc(sysdate)) <= trunc(sysdate) and nvl(TRUNC(END_DATE_ACTIVE),trunc(sysdate)) >= trunc(sysdate)) ");
        }

//        List<WorkRequest> listWorkRequest = jdbcTemplate.query(sqlBuffer.toString(), new RowMapper<WorkRequest>() {
        List<WorkRequest> listWorkRequest = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<WorkRequest>() {
            @Override
            public WorkRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
            	WorkRequest aWorkRequest = new WorkRequest();
            	Date aDate = new Date();
            	     
            	aWorkRequest.setWorkRequestNumber(rs.getString("wrNumber"));
            	aWorkRequest.setAssetNumber(rs.getString("assetNumber"));
            	aWorkRequest.setAssetDescription(rs.getString("assetDescription"));
            	aWorkRequest.setWrType(rs.getString("wrType"));
            	aWorkRequest.setWorkRequestType(rs.getString("WORK_REQUEST_TYPE"));
            	aWorkRequest.setWrStatus(rs.getString("wrStatus"));
            	aWorkRequest.setWorkRequestStatus(rs.getString("WORK_REQUEST_STATUS"));
            	aWorkRequest.setWoNumber(rs.getString("woNumber"));
            	aWorkRequest.setWoStatus(rs.getString("woStatus"));
            	//aDate = rs.getTimestamp("cmBreakdownDate");
            	//aWorkRequest.setCmBreakdownDate(aDate);
            	
/*            	if (aDate!= null) {
            		aWorkRequest.setBreakdownDateInput(df.format(aDate));
            		logger.debug("BREAK DOWN " + aWorkRequest.getBreakdownDateInput());
            	} else {
            		aWorkRequest.setBreakdownDateInput("");
            	}*/
            	aWorkRequest.setBreakdownDateInput(rs.getString("cmBreakdownDate"));
            	aWorkRequest.setCmBreakdownDate(rs.getString("cmBreakdownDate"));
//            	aDate = rs.getTimestamp("pmScheduleDate");
//            	aWorkRequest.setPmScheduleDate(aDate);
            	aWorkRequest.setScheduleDateInput(rs.getString("pmScheduleDate"));
            	aWorkRequest.setPmScheduleDate(rs.getString("pmScheduleDate"));
/*            	if (aDate!= null) {
	            	aWorkRequest.setScheduleDateInput(df.format(aDate));
            	} else {
	            	aWorkRequest.setScheduleDateInput("");
            	}*/
            	aWorkRequest.setCreatedby(rs.getString("createdBy"));
            	logger.debug("CREATED BY " +aWorkRequest.getCreatedby() );           	
            	
//            	aWorkRequest.setMaintenancevendor(rs.getString("maintenanceVendor"));
            	aWorkRequest.setAssetlocation(rs.getString("assetLocation"));
            	aWorkRequest.setAssetowner(rs.getString("assetOwner"));
            	aWorkRequest.setOwningDept(rs.getString("owningDept"));
            	aDate = rs.getTimestamp("createdDate");
            	aWorkRequest.setCreateddate(aDate);
            	//aWorkRequest.setCreateddate(rs.getDate("createdDate"));
            	
            	aWorkRequest.setSerialnumber(rs.getString("serialNumber"));
            	aWorkRequest.setManufacturer(rs.getString("manufacturer"));
            	aWorkRequest.setBrand(rs.getString("brand"));
            	aWorkRequest.setModel(rs.getString("model"));

            	aWorkRequest.setDisinfection(rs.getString("disinfectionStatus"));
            	aWorkRequest.setEquipmentSent(rs.getString("equipSentToWorkshop"));
            	aWorkRequest.setHaContactPerson(rs.getString("haContactPerson"));
            	aWorkRequest.setHaContactPhone(rs.getString("haContactPhone"));
            	aWorkRequest.setHaContactEmail(rs.getString("haContactEmail"));
            	//aWorkRequest.setRequestedFor(rs.getInt("requestedFor")); 
            	aWorkRequest.setRequestedFor(rs.getString("requestedFor"));
            	aWorkRequest.setRequestedForEmployee(rs.getString("requestedForEmployee"));
            	aWorkRequest.setDescription(rs.getString("description"));
            	aWorkRequest.setMaintVendor(rs.getString("maintenanceBodyType"));
            	aWorkRequest.setContractNumber(rs.getString("contractNumber"));
            	aWorkRequest.setMaintenancePlan(rs.getString("maintenancePlan"));            	
            	
            	aWorkRequest.setMaintenanceJoinDate(dateUtil.formatDateToStr(dateUtil.parseStrToDate(rs.getString("maintenanceJoinDate"), "yyyy-MM-dd HH:mm:ss") , "dd/MM/yyyy"));
            	aWorkRequest.setMaintenanceExpiryDate(dateUtil.formatDateToStr(dateUtil.parseStrToDate(rs.getString("maintenanceExpiryDate"), "yyyy-MM-dd HH:mm:ss") , "dd/MM/yyyy"));
            	aWorkRequest.setSupplierAgreementNumber(rs.getString("supplierAgreementNumber"));
            	aWorkRequest.setAutoSend(rs.getString("autoSend"));
            	aWorkRequest.setMaintContact(rs.getString("maintenanceContactPerson"));
            	aWorkRequest.setMaintPhone(rs.getString("maintenanceContactPhone"));
            	aWorkRequest.setMaintFax(rs.getString("maintenanceContactFax"));
            	aWorkRequest.setMaintEmail(rs.getString("maintenanceContactEmail"));
            	
            	aWorkRequest.setAssetOrganisation(rs.getString("assetOrg"));
            	aWorkRequest.setAssetDescription(rs.getString("assetDescription"));
            	aWorkRequest.setAssetOwner(rs.getString("assetOwner"));
            	aWorkRequest.setAssetOwnerDesc(rs.getString("assetOwnerDesc"));
            	aWorkRequest.setAssetStatus(rs.getString("assetStatus"));
            	aWorkRequest.setDob(rs.getString("dateOfBirth"));
            	aWorkRequest.setAssetLocationDesc(rs.getString("assetLocationDesc"));
            	aWorkRequest.setPurchasePrice(rs.getString("purchasePrice") );
            	aWorkRequest.setAssetSupplier(rs.getString("assetSupplier") );
            	aWorkRequest.setItemCodeNumber(rs.getString("itemCode") );
            	aWorkRequest.setFatherAssetNumber(rs.getString("parentAssetNumber") );
            	aWorkRequest.setMaintenanceBody(rs.getString("maintenanceBody"));
            	aWorkRequest.setMaintenanceBodyType(rs.getString("maintenanceBodyType"));
            	aWorkRequest.setMaintenanceInterval(rs.getString("maintenanceInterval"));
            	aWorkRequest.setAssetOwningDepartmentDesc(rs.getString("assetOwningDepartmentDesc"));
            	aWorkRequest.setRiskLevel(rs.getString("riskLevel"));
            	aWorkRequest.setDescriptionHistory(rs.getString("descriptionHistory"));
            	aWorkRequest.setRemark(rs.getString("remark"));
            	aWorkRequest.setMaintenanceBodyNum(rs.getString("maintenanceVendorNum"));
            	aWorkRequest.setlastUpdateDate(dateUtil.formatDateToStr(rs.getTimestamp("last_update_date"), "dd/MM/yyyy HH:mm:ss"));
            	aWorkRequest.setWorkRequestOrg(rs.getString("wrOrg"));

                logger.debug("Retrieved Work Request Number " + aWorkRequest.getWorkRequestNumber());
                
                return aWorkRequest;
            }
     
        });
     
        return listWorkRequest;
        
    	
    }
    
    @Override
    @Transactional(rollbackFor=Exception.class)
    public String checkWorkRequest(String assetNum, String type, int wrId, String mode)	throws Exception {
    	//String sql = "select count(1) as cnt from WIP_EAM_WORK_REQUESTS where asset_number = ? and work_request_status_id = 3";
    	String sql;
    	if (type.equals("20")) { // PM - Schedule Date
    		if (mode.equals("UPDATE")) {
    			sql = "select wr.work_request_number||'|'||to_char(we.pm_schedule_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wr.created_by) res from WIP_EAM_WORK_REQUESTS wr, XXEAM_WORK_REQUESTS_EXT we where wr.work_request_id = we.work_request_id and wr.work_request_id = (select max(work_request_id) from WIP_EAM_WORK_REQUESTS where asset_number = ? and work_request_type_id = ? and work_request_status_id = 3 and work_request_id <> ?)";
    		}else {
    			sql = "select wr.work_request_number||'|'||to_char(we.pm_schedule_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wr.created_by) res from WIP_EAM_WORK_REQUESTS wr, XXEAM_WORK_REQUESTS_EXT we where wr.work_request_id = we.work_request_id and wr.work_request_id = (select max(work_request_id) from WIP_EAM_WORK_REQUESTS where asset_number = ? and work_request_type_id = ? and work_request_status_id = 3)";
    		}
    	}else { // CM - Creation Date
    		if (mode.equals("UPDATE")) {
    			sql = "select wr.work_request_number||'|'||to_char(wr.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wr.created_by) res from WIP_EAM_WORK_REQUESTS wr where wr.work_request_id = (select max(work_request_id) from WIP_EAM_WORK_REQUESTS where asset_number = ? and work_request_type_id = ? and work_request_status_id = 3 and work_request_id <> ?)";
    		}else {
    			sql = "select wr.work_request_number||'|'||to_char(wr.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wr.created_by) res from WIP_EAM_WORK_REQUESTS wr where wr.work_request_id = (select max(work_request_id) from WIP_EAM_WORK_REQUESTS where asset_number = ? and work_request_type_id = ? and work_request_status_id = 3)";
    		}
    	}
    	Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String returnValue;
		try {
			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, assetNum);
			pstmt.setInt(2, Integer.parseInt(type));
			if (mode.equals("UPDATE")) {
				pstmt.setInt(3, wrId);
			}
			rs = pstmt.executeQuery();
			if(!rs.next()) {
				return "Y";
			}else {
				returnValue = rs.getString("res");
				return returnValue;
				/*if (returnValue == 0) {
					return "N";
				}else {
					return "Y";
				}*/
			}
		}catch(Exception e) {
			logger.debug("ERROR (EXCEPTION): checkWorkRequest");
//			e.printStackTrace();
			return "N";
		}finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (Exception e) {}
			}
			if (conn != null) {
				try {
					conn.close();
					conn = null;
				} catch (Exception e) {}
			}
		}
    	
    }
    
    @Override
    @Transactional(rollbackFor=Exception.class)
    public String checkWorkOrder(String assetNum, String type, String pmSchDate, String woNumber) throws Exception {
    	String hvRecordSql;
    	
    	String sql;
    	if (type.equals("10")) {
    		hvRecordSql = "SELECT 1 from wip_discrete_jobs job, wip_entities entity, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND job.status_type in (select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning in ('On Hold', 'Unreleased', 'Released') ) AND job.work_order_type = ? AND ROWNUM=1 ";
    		//sql = "select wo.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from WIP_EAM_WORK_ORDERS_V wo where wo.wip_entity_name = (  select max(wip_entity_name)   from WIP_EAM_WORK_ORDER_DTLS_V   where status_type in (    select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning not in ('Complete','Complete - No Charges','Cancelled','Closed')) and asset_number = ? and organization_id in (select inv_organization_id from XXEAM_EXT_ASSET_NUMBERS_V where asset_number = ?) and work_order_type = ?)";
    		//sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select to_char(MAX(to_number(entity.wip_entity_name))) from wip_discrete_jobs job, wip_entities entity, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND job.status_type in (select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning not in ('Complete','Complete - No Charges','Cancelled','Closed') ) AND job.work_order_type = ? ";
    		//sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select to_char(MAX(to_number(entity.wip_entity_name))) from wip_discrete_jobs job, wip_entities entity, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND job.status_type in (select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning in ('On Hold', 'Unreleased', 'Released') ) AND job.work_order_type = ? ";
    		sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select max(WIP_ENTITY_NAME) FROM ( SELECT entity.wip_entity_name, RANK() OVER (ORDER BY entity.creation_date DESC) AS the_rank from wip_discrete_jobs job, wip_entities entity, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND job.status_type in (select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning in ('On Hold', 'Unreleased', 'Released') ) AND job.work_order_type = ? ";
    	}else { //20 - need to check schedule date
    		hvRecordSql = "SELECT 1 from wip_discrete_jobs job, wip_entities entity, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND job.status_type in (select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning not in ('Complete','Complete - No Charges','Cancelled','Closed') ) AND job.work_order_type = ? AND ROWNUM=1 ";
    		//sql = "select wo.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from WIP_EAM_WORK_ORDERS_V wo where wo.wip_entity_name = (  select max(wip_entity_name)   from WIP_EAM_WORK_ORDER_DTLS_V   where status_type in (    select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning not in ('Complete','Complete - No Charges','Cancelled','Closed')) and asset_number = ? and organization_id in (select inv_organization_id from XXEAM_EXT_ASSET_NUMBERS_V where asset_number = ?) and work_order_type = ? and trunc(completion_date) = to_date(substr(?,0,10),'DD/MM/YYYY') )";
    		//sql = "select ent.wip_entity_name||'|'||to_char(wo.scheduled_start_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select to_char(MAX(to_number(entity.wip_entity_name))) from wip_discrete_jobs job, wip_entities entity, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND job.status_type in (select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning not in ('Complete','Complete - No Charges','Cancelled','Closed') ) AND job.work_order_type = ? ";
    		sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select max(WIP_ENTITY_NAME) FROM ( SELECT entity.wip_entity_name, RANK() OVER (ORDER BY entity.creation_date DESC) AS the_rank from wip_discrete_jobs job, wip_entities entity, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND job.status_type in (select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning not in ('Complete','Complete - No Charges','Cancelled','Closed') ) AND job.work_order_type = ? ";
    	}
    	if (woNumber!=null) {
    		hvRecordSql = hvRecordSql + " and entity.wip_entity_name <> ?";
    		sql = sql + " and entity.wip_entity_name <> ? ) WHERE the_rank = 1)";
    	}else {
    		sql = sql + " ) WHERE the_rank = 1)";
    	}
    	logger.debug("ChkWO SQL="+sql);

    	Connection connection = null;
    	CallableStatement cs = null;
		ResultSet rs = null;
		String returnValue;
			
			
			try {
		    	connection = jdbcTemplate.getDataSource().getConnection();
				cs = connection.prepareCall(hvRecordSql);
				
				/*******Check whether there is record duplicate*******/
				cs.setString(1, assetNum);
				cs.setString(2, assetNum);
				cs.setString(3, type);
				if (woNumber!=null) {
					cs.setString(4, woNumber);
				}
				rs = cs.executeQuery();
				if(!rs.next()) {
					return "Y";
				}
				/*******Check whether there is record duplicate*******/
			}catch(Exception e) {
				logger.debug("ERROR (EXCEPTION): checkWorkOrder "+e.getMessage());
				return "N";
			}finally {
				if (rs != null) {
					try {
						rs.close();
						rs = null;
					} catch (Exception e) {}
				}
				if (cs != null) {
					try {
						cs.close();
						cs = null;
					} catch (Exception e) {}
				}
				if (connection != null) {
					try {
						connection.close();
						connection = null;
					} catch (Exception e) {}
				}
			}
			
			try {
		    	connection = jdbcTemplate.getDataSource().getConnection();
				cs = connection.prepareCall(sql);
				/*******Find the exact duplicate record*******/
				cs.setString(1, assetNum);
				cs.setString(2, assetNum);
				cs.setString(3, type);
				if (woNumber!=null) {
					cs.setString(4, woNumber);
				}
				rs = cs.executeQuery();
				if(!rs.next()) {
					return "Y";
				}else {
					returnValue = rs.getString("res");
					return returnValue;
				}
				/*******Find the exact duplicate record*******/
			}catch(Exception e) {
				logger.debug("ERROR (EXCEPTION): checkWorkOrder : "+e.getMessage());
				return "N";
			}finally {
				if (rs != null) {
					try {
						rs.close();
						rs = null;
					} catch (Exception e) {}
				}
				if (cs != null) {
					try {
						cs.close();
						cs = null;
					} catch (Exception e) {}
				}
				if (connection != null) {
					try {
						connection.close();
						connection = null;
					} catch (Exception e) {}
				}
			}
    }
	
	// Only check when work request type = PM (type = 20)
    @Override
    @Transactional(rollbackFor=Exception.class)
    public String checkWRSchDateRec(String assetNum, String type, String pmSchDate, int wrId, String mode) throws Exception {
    	String sql = "select wr.work_request_number||'|'||to_char(wr.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wr.created_by) res from WIP_EAM_WORK_REQUESTS wr where wr.work_request_id = (select max(r.work_request_id) from WIP_EAM_WORK_REQUESTS r, XXEAM_WORK_REQUESTS_EXT e where r.work_request_id = e.work_request_id and r.asset_number = ? and r.work_request_type_id = ? and r.work_request_status_id = 3 and trunc(e.pm_schedule_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI')))";
    	if (mode.equals("UPDATE")) {
    		sql = "select wr.work_request_number||'|'||to_char(wr.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wr.created_by) res from WIP_EAM_WORK_REQUESTS wr where wr.work_request_id = (select max(r.work_request_id) from WIP_EAM_WORK_REQUESTS r, XXEAM_WORK_REQUESTS_EXT e where r.work_request_id = e.work_request_id and r.asset_number = ? and r.work_request_type_id = ? and r.work_request_status_id = 3 and trunc(e.pm_schedule_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI')) and r.work_request_id <> ? )";
    	}
    	logger.debug("chk assetNumber="+assetNum);
    	logger.debug("chk type="+type);
    	logger.debug("chk pmSchDate="+pmSchDate);
    	Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String returnValue;
		try {
			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, assetNum);
			pstmt.setString(2, type);
			pstmt.setString(3, pmSchDate);
			if (mode.equals("UPDATE")) {
				pstmt.setInt(4, wrId);
			}
			
			rs = pstmt.executeQuery();
			if(!rs.next()) {
				return "Y";
			}else {
				returnValue = rs.getString("res");
				return returnValue;
			}
		}catch(Exception e) {
			logger.debug("ERROR (EXCEPTION): checkWRSchDateRec");
			return "N";
		}finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (Exception e) {}
			}
			if (conn != null) {
				try {
					conn.close();
					conn = null;
				} catch (Exception e) {}
			}
		}
    }
    
    //Only call for wr type = 20
    @Override
    @Transactional(rollbackFor=Exception.class)
    public String checkWorkOrderSchDate(String assetNum, String type, String pmSchDate, String woNumber) throws Exception {
    	logger.debug("checkWorkOrderSchDate....");
    	logger.debug("assetNum="+assetNum);
    	logger.debug("type="+type);
    	logger.debug("pmSchDate="+pmSchDate);
    	logger.debug("woNumber="+woNumber);
    	String sql; //= "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select to_char(MAX(to_number(entity.wip_entity_name))) from wip_discrete_jobs job, wip_entities entity, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND job.status_type in (select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning in ('Unreleased','Released','On Hold') ) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') ))";
    	if (woNumber==null) { // for work request create/update checking
    		//sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select to_char(MAX(to_number(entity.wip_entity_name))) from wip_discrete_jobs job, wip_entities entity, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND job.status_type in (select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning in ('Unreleased','Released','On Hold') ) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') ))";
    		if (type.equals("20")) {
    			//sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select to_char(MAX(to_number(entity.wip_entity_name))) from wip_discrete_jobs job, wip_entities entity, eam_work_order_details ewod, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND job.wip_entity_id          = ewod.wip_entity_id AND job.organization_id          = ewod.organization_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND ewod.user_defined_status_id IN (select status_id from EAM_WO_STATUSES_V where work_order_status in ('On Hold', 'Unreleased', 'Released', 'Complete', 'Complete - Pending Close',  'Closed')) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') ))";
    			sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select max(WIP_ENTITY_NAME) FROM ( SELECT entity.wip_entity_name, RANK() OVER (ORDER BY entity.creation_date DESC) AS the_rank from wip_discrete_jobs job, wip_entities entity, eam_work_order_details ewod, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND job.wip_entity_id          = ewod.wip_entity_id AND job.organization_id          = ewod.organization_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND ewod.user_defined_status_id IN (select status_id from EAM_WO_STATUSES_V where work_order_status in ('On Hold', 'Unreleased', 'Released', 'Complete', 'Complete - Pending Close',  'Closed')) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') )) WHERE the_rank = 1)";
    		}else {
    			//sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select to_char(MAX(to_number(entity.wip_entity_name))) from wip_discrete_jobs job, wip_entities entity, eam_work_order_details ewod, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND job.wip_entity_id          = ewod.wip_entity_id AND job.organization_id          = ewod.organization_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND ewod.user_defined_status_id IN (select status_id from EAM_WO_STATUSES_V where work_order_status in ('On Hold', 'Unreleased', 'Released')) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') ))";
    			sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select max(WIP_ENTITY_NAME) FROM ( SELECT entity.wip_entity_name, RANK() OVER (ORDER BY entity.creation_date DESC) AS the_rank from wip_discrete_jobs job, wip_entities entity, eam_work_order_details ewod, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND job.wip_entity_id          = ewod.wip_entity_id AND job.organization_id          = ewod.organization_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND ewod.user_defined_status_id IN (select status_id from EAM_WO_STATUSES_V where work_order_status in ('On Hold', 'Unreleased', 'Released')) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') )) WHERE the_rank = 1)";
    		}
    	}else {	// for work order create/update checking
    		//sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select to_char(MAX(to_number(entity.wip_entity_name))) from wip_discrete_jobs job, wip_entities entity, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND job.status_type in (select lookup_code    from mfg_lookups    where lookup_type = 'WIP_JOB_STATUS' and meaning in ('Unreleased','Released','On Hold') ) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') ) AND entity.wip_entity_name <> ?)";
    		if (type.equals("20")) {
    			//sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select to_char(MAX(to_number(entity.wip_entity_name))) from wip_discrete_jobs job, wip_entities entity, eam_work_order_details ewod, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND job.wip_entity_id          = ewod.wip_entity_id AND job.organization_id          = ewod.organization_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND ewod.user_defined_status_id IN (select status_id from EAM_WO_STATUSES_V where work_order_status in ('On Hold', 'Unreleased', 'Released', 'Complete', 'Complete - Pending Close',  'Closed')) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') ) AND entity.wip_entity_name <> ?)";
    			sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select max(WIP_ENTITY_NAME) FROM ( SELECT entity.wip_entity_name, RANK() OVER (ORDER BY entity.creation_date DESC) AS the_rank from wip_discrete_jobs job, wip_entities entity, eam_work_order_details ewod, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND job.wip_entity_id          = ewod.wip_entity_id AND job.organization_id          = ewod.organization_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND ewod.user_defined_status_id IN (select status_id from EAM_WO_STATUSES_V where work_order_status in ('On Hold', 'Unreleased', 'Released', 'Complete', 'Complete - Pending Close',  'Closed')) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') ) AND entity.wip_entity_name <> ? ) WHERE the_rank = 1)";
    		}else {
    			//sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select to_char(MAX(to_number(entity.wip_entity_name))) from wip_discrete_jobs job, wip_entities entity, eam_work_order_details ewod, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND job.wip_entity_id          = ewod.wip_entity_id AND job.organization_id          = ewod.organization_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND ewod.user_defined_status_id IN (select status_id from EAM_WO_STATUSES_V where work_order_status in ('On Hold', 'Unreleased', 'Released')) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') ) AND entity.wip_entity_name <> ?)";
    			sql = "select ent.wip_entity_name||'|'||to_char(wo.creation_date, 'DD/MM/YYYY')||'|'||(select nvl(full_name, u.user_name) from PER_PEOPLE_X x, fnd_user u where x.person_id(+) = u.employee_id and u.user_id = wo.created_by) res from wip_discrete_jobs wo, wip_entities ent where wo.wip_entity_id = ent.wip_entity_id and ent.wip_entity_name = (  select max(WIP_ENTITY_NAME) FROM ( SELECT entity.wip_entity_name, RANK() OVER (ORDER BY entity.creation_date DESC) AS the_rank from wip_discrete_jobs job, wip_entities entity, eam_work_order_details ewod, mtl_system_items_kfv msi, csi_item_instances cii where job.wip_entity_id         = entity.wip_entity_id AND job.wip_entity_id          = ewod.wip_entity_id AND job.organization_id          = ewod.organization_id AND entity.entity_type          = DECODE(job.status_type,12,7,6) AND job.maintenance_object_type = 3 AND job.maintenance_object_id   = cii.instance_id AND msi.inventory_item_id       = cii.inventory_item_id AND msi.organization_id         = cii.last_vld_organization_id AND DECODE(msi.eam_item_type, 1, cii.instance_number, NULL) = ? AND msi.organization_id in (SELECT inv_organization_id FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE asset_number = ?) AND ewod.user_defined_status_id IN (select status_id from EAM_WO_STATUSES_V where work_order_status in ('On Hold', 'Unreleased', 'Released')) AND job.work_order_type = ? AND trunc(job.scheduled_start_date) = trunc(to_date(?,'DD/MM/YYYY HH24:MI') ) AND entity.wip_entity_name <> ? ) WHERE the_rank = 1)";
    		}
    	}
    	Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String returnValue;
		try {
			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, assetNum);
			pstmt.setString(2, assetNum);
			pstmt.setString(3, type);
			pstmt.setString(4, pmSchDate);
			if (woNumber!=null) {
				pstmt.setString(5, woNumber);
			}
			
			rs = pstmt.executeQuery();
			if(!rs.next()) {
				return "Y";
			}else {
				returnValue = rs.getString("res");
				return returnValue;
			}
		}catch(Exception e) {
			logger.debug("ERROR (EXCEPTION): checkWorkOrderSchDate");
//			e.printStackTrace();
			return "N";
		}finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (Exception e) {}
			}
			if (conn != null) {
				try {
					conn.close();
					conn = null;
				} catch (Exception e) {}
			}
		}
    }
    
    // Return "PASS" if pass validation on data, return error message if problem found
    public String validateWRData(WorkRequest workRequest) throws Exception{
    	String result;
    	String sql;
    	ResultSet rs = null;
    	Connection connection = null;
    	CallableStatement cs = null;
    	int usercnt = 0;
    	sql = "select count(1) from fnd_user where user_name = ? ";
    	try {
	    	connection = jdbcTemplate.getDataSource().getConnection();
			cs = connection.prepareCall(sql);
			cs.setString(1, workRequest.getRequestedFor());
			rs = cs.executeQuery();
			while (rs.next()) {
				usercnt = rs.getInt(1);
			}

    	}catch(Exception e) {
    		logger.debug("ERROR (EXCEPTION): validateWRData");
//			e.printStackTrace();
			return e.toString();
		}finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
					
				} catch (Exception e) {}
			}
			if (cs != null) {
				try {
					cs.close();
					cs = null;
				} catch (Exception e) {}
			}
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (Exception e) {}
			}
		}
		
		if (usercnt>0) {
			result = "PASS";
		}else {
			result = "<strong>Requested For user not exists!</strong> ";
		}
    	return result;
    }
    
    public int getAssetAttrRespType(){
    	int result=0;
    	String sql;
    	ResultSet rs = null;
    	Connection connection = null;
    	CallableStatement cs = null;

    	sql = "SELECT CASE WHEN fnd_global.resp_name LIKE '%EAM Maintenance User (IT Asset)' THEN 1 WHEN (fnd_global.resp_name LIKE '%EAM In-House Maintenance User (Non-IT Asset)' OR fnd_global.resp_name LIKE '%EAM Third-Party Maintenance User (Non-IT Asset)') THEN 2 ELSE 0 END SELECTCRITERIA FROM DUAL";
    	try {
	    	connection = jdbcTemplate.getDataSource().getConnection();
			cs = connection.prepareCall(sql);
			rs = cs.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
    	}catch(Exception e) {
    		logger.debug("ERROR (EXCEPTION): getAssetAttrRespType");
			return -1;
		}finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
					
				} catch (Exception e) {}
			}
			if (cs != null) {
				try {
					cs.close();
					cs = null;
				} catch (Exception e) {}
			}
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (Exception e) {}
			}
		}

    	return result;
    }
}
