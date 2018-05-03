/* --------------------------------------
    File Name: WorkOrderDao.java
    Author: Carmen Ng (PCCW)
    Date: 15-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Work Order Function Implementation

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.7>		20171208	Carmen Ng	Set asset risk to model
	<1.6>		20171204	Carmen Ng	Added operation_description to view work order
	<1.5>		20171120	Fanny Hung	Add SaveWorkOrder Function
	<1.4> 		20171120	Carmen Ng	Added equipment condition dropdown
	<1.3>		20171117	Carmen Ng	Added wildcard search to serial number
	<1.2>		20171114	Carmen Ng	Added variable for overdue/upcoming orders
	<1.1>		20171110	Carmen Ng	Added set variables for view work order
										Fixed breakdownScheduleDate format
	<1.0>		20171015	Carmen Ng	Initial version
   -------------------------------------- */

package hk.org.ha.eam.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import hk.org.ha.eam.model.AssetInfo;
import hk.org.ha.eam.model.AttachmentInfo;
import hk.org.ha.eam.model.Dropdown;
import hk.org.ha.eam.model.MaintenanceInfo;
import hk.org.ha.eam.model.SearchLovResult;
import hk.org.ha.eam.model.SearchWorkOrder;
import hk.org.ha.eam.model.WorkOrder;
import hk.org.ha.eam.model.WorkRequest;
import hk.org.ha.eam.dao.WorkOrderDao;
import hk.org.ha.eam.util.ConnectionProvider;
import hk.org.ha.eam.util.EamConstant;
import hk.org.ha.eam.util.WorkOrderConstant;
import hk.org.ha.eam.util.WorkRequestConstant;
import hk.org.ha.eam.common.util.DateUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;

@Repository
public class WorkOrderDaoImpl implements WorkOrderDao {
	
	private static final Logger logger = Logger.getLogger(WorkOrderDaoImpl.class);

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
	
    public WorkOrderDaoImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
 
    @Override
    public List<Dropdown> getWoStatusList() throws Exception{
        //String sql = "select distinct STATUS_ID, WORK_ORDER_STATUS from EAM_WO_STATUSES_V where STATUS_ID in (1, 3, 7, 6, 1001, 4, 1000, 12, 98) order by WORK_ORDER_STATUS";
    	String sql = "select distinct STATUS_ID, WORK_ORDER_STATUS from EAM_WO_STATUSES_V where WORK_ORDER_STATUS in ('Rejected','Unreleased','Closed','Released','Complete - Pending Close','Complete','Cancelled By PM','On Hold','Cancelled') ORDER BY CASE WHEN WORK_ORDER_STATUS = 'Unreleased' THEN '1'" + 
				" WHEN WORK_ORDER_STATUS = 'Released' THEN '2'" + 
				" WHEN WORK_ORDER_STATUS = 'On Hold' THEN '3'" + 
				" WHEN WORK_ORDER_STATUS = 'Rejected' THEN '4'" + 
				" WHEN WORK_ORDER_STATUS = 'Cancelled' THEN '5'" + 
				" WHEN WORK_ORDER_STATUS = 'Cancelled By PM' THEN '6'" + 
				" WHEN WORK_ORDER_STATUS = 'Complete' THEN '7'" + 
				" WHEN WORK_ORDER_STATUS = 'Complete - Pending Close' THEN '8'" + 
				" WHEN WORK_ORDER_STATUS = 'Closed' THEN '9'" + 
				" ELSE WORK_ORDER_STATUS END ASC";        
        List<Dropdown> listDropdown = jdbcTemplate.query(sql, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(String.valueOf(rs.getInt("STATUS_ID")));
            	aDropdown.setDesc(rs.getString("WORK_ORDER_STATUS"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    }  
    
    @Override
    public List<Dropdown> getWoTypeList() throws Exception{
        String sql = "select LOOKUP_CODE, MEANING from mfg_lookups where lookup_type='WIP_EAM_WORK_ORDER_TYPE' order by MEANING";
        
        List<Dropdown> listDropdown = jdbcTemplate.query(sql, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(String.valueOf(rs.getInt("LOOKUP_CODE")));
            	aDropdown.setDesc(rs.getString("MEANING"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    }  
    
    @Override
    public List<Dropdown> getFailureCauseCode() throws Exception{
        //String sql = "SELECT FAILURE_CODE FROM EAM_FAILURE_CODES WHERE NVL(EFFECTIVE_END_DATE, SYSDATE) >= SYSDATE ORDER BY FAILURE_CODE";
        String sql ="SELECT CAUSE_CODE FROM EAM_CAUSE_CODES WHERE NVL(EFFECTIVE_END_DATE, SYSDATE) >= SYSDATE ORDER BY CAUSE_CODE";
        
        List<Dropdown> listDropdown = jdbcTemplate.query(sql, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(rs.getString("CAUSE_CODE"));
            	aDropdown.setDesc(rs.getString("CAUSE_CODE"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    }  
    
    @Override
    public List<Dropdown> getFailureSymptomCode() throws Exception{
        //String sql = "SELECT CAUSE_CODE FROM EAM_CAUSE_CODES WHERE NVL(EFFECTIVE_END_DATE, SYSDATE) >= SYSDATE ORDER BY CAUSE_CODE";
    	String sql = "SELECT FAILURE_CODE FROM EAM_FAILURE_CODES WHERE NVL(EFFECTIVE_END_DATE, SYSDATE) >= SYSDATE ORDER BY FAILURE_CODE";
        
        List<Dropdown> listDropdown = jdbcTemplate.query(sql, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(rs.getString("FAILURE_CODE"));
            	aDropdown.setDesc(rs.getString("FAILURE_CODE"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    } 
    
    @Override
    public List<Dropdown> getResolutionCode(String cause, String symptom) throws Exception{
        String sql = "SELECT C.RESOLUTION_CODE FROM EAM_FAILURE_COMBINATIONS_V C, EAM_FAILURE_CODES F WHERE C.FAILURE_CODE = F.FAILURE_CODE AND C.FAILURE_CODE = :P_FAILURE_CODE AND C.CAUSE_CODE = :P_CAUSE_CODE AND NVL(C.EFFECTIVE_END_DATE, SYSDATE) >= SYSDATE ORDER BY C.RESOLUTION_CODE";
        
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        paramMap.addValue("P_FAILURE_CODE", symptom);
        paramMap.addValue("P_CAUSE_CODE", cause);
        
        List<Dropdown> listDropdown = namedParameterJdbcTemplate.query(sql, paramMap , new RowMapper<Dropdown>() {
        //List<Dropdown> listDropdown = jdbcTemplate.query(sql, paramMap, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(rs.getString("RESOLUTION_CODE"));
            	aDropdown.setDesc(rs.getString("RESOLUTION_CODE"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    } 
    
    @Override
    public List<Dropdown> getEquipmentCondition() throws Exception{
        String sql = "select v.flex_value " + 
        		"from fnd_flex_values_vl v, " + 
        		"fnd_flex_value_sets s " + 
        		"where v.flex_value_set_id = s.flex_value_set_id " + 
        		"and s.flex_value_set_name = 'XXEAM_EQUIPMENT_CONDITION' " + 
        		"and v.enabled_flag = 'Y' " + 
        		"and v.summary_flag = 'N' " + 
        		"and trunc(sysdate) between nvl(v.start_date_active, trunc(sysdate)) and nvl(v.end_date_active,trunc(sysdate))";
        
        	List<Dropdown> listDropdown = jdbcTemplate.query(sql, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(rs.getString("flex_value"));
            	aDropdown.setDesc(rs.getString("flex_value"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    } 
    
    @Override
    public List<Dropdown> getMaintenanceBodyTypeList() throws Exception{
        String sql = "select v.flex_value " + 
        		"from fnd_flex_value_sets s, " + 
        		"fnd_flex_values v " + 
        		"where s.flex_value_set_id = v.flex_value_set_id " + 
        		"and s.flex_value_set_name = 'XXEAM_MAINT_BODY_TYPE' " + 
        		"and v.enabled_flag = 'Y' " + 
        		"and trunc(sysdate) between nvl(v.start_date_active, trunc(sysdate)) and nvl(v.end_date_active,trunc(sysdate))";
        
        	List<Dropdown> listDropdown = jdbcTemplate.query(sql, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(rs.getString("flex_value"));
            	aDropdown.setDesc(rs.getString("flex_value"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    } 
    
    @Override
    public List<Dropdown> getAutoSendWOList() throws Exception{
        String sql = "select v.flex_value " + 
        		"from fnd_flex_value_sets s, " + 
        		"fnd_flex_values v " + 
        		"where s.flex_value_set_id = v.flex_value_set_id " + 
        		"and s.flex_value_set_name = 'XXEAM_WO_SEND_METHOD' " + 
        		"and v.enabled_flag = 'Y' " + 
        		"and trunc(sysdate) between nvl(v.start_date_active, trunc(sysdate)) and nvl(v.end_date_active,trunc(sysdate))";
        
        	List<Dropdown> listDropdown = jdbcTemplate.query(sql, new RowMapper<Dropdown>() {
            
            @Override
            public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Dropdown aDropdown = new Dropdown();
     
            	aDropdown.setName(rs.getString("flex_value"));
            	aDropdown.setDesc(rs.getString("flex_value"));
     
                return aDropdown;
            }
     
        });
        
        return listDropdown;

    } 
    
    @Override
    @Transactional(rollbackFor=Exception.class)	
    public List<WorkOrder> searchWorkOrder(SearchWorkOrder searchCriteria) throws Exception {
    	
    	String woNumber = searchCriteria.getWoNumber();
    	String assetNumber = searchCriteria.getAssetNumber();
    	String woType = searchCriteria.getWoType();
    	List<String> woStatus = searchCriteria.getWoStatus();
    	String eamOrg = searchCriteria.getEamOrg();
    	String dateType = searchCriteria.getDateType();
        String dateFrom = searchCriteria.getDateFrom();
        String dateTo = searchCriteria.getDateTo();
    	String assetRisk = searchCriteria.getAssetRisk();
    	String maintenanceContract = searchCriteria.getMaintenanceContract();
    	String maintenanceVendor = searchCriteria.getMaintenanceVendor();
        String hiddenMBody = searchCriteria.getHiddenMBody();
    	String serialNumber = searchCriteria.getSerialNumber();
    	String assetLocation = searchCriteria.getAssetLocation();
        String assetOwner = searchCriteria.getAssetOwner();
        String owningDept = searchCriteria.getOwningDept();
        String createdBy = searchCriteria.getCreatedBy();
        boolean criticalOnly = searchCriteria.getCriticalOnly();
    	boolean urgentOnly = searchCriteria.getUrgentOnly();
    	String dashboardValue = searchCriteria.getDashboardValue();

    	logger.debug("woNumber:"+woNumber);
    	logger.debug("assetNumber:"+assetNumber);
    	logger.debug("woType:"+woType);
    	logger.debug("woStatus:"+woStatus);
    	logger.debug("eamOrg:"+eamOrg);
    	logger.debug("dateType:"+dateType);
    	logger.debug("dateFrom:"+dateFrom);
    	logger.debug("dateTo:"+dateTo);
    	logger.debug("assetRisk:"+assetRisk);
    	logger.debug("maintenanceContract:"+maintenanceContract);
    	logger.debug("maintenanceVendor:"+maintenanceVendor);
    	logger.debug("hiddenMBody:"+hiddenMBody);
    	logger.debug("serialNumber:"+serialNumber);
    	logger.debug("assetLocation:"+assetLocation);
    	logger.debug("assetOwner:"+assetOwner);
    	logger.debug("owningDept:"+owningDept);
    	logger.debug("createdBy:"+createdBy);
    	logger.debug("criticalOnly:"+criticalOnly);
    	logger.debug("urgentOnly:"+urgentOnly);
    	logger.debug("dashboardValue:"+dashboardValue);
    	
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT \n");
        sqlBuffer.append("	VO.WIP_ENTITY_ID				AS woId, \n");
        sqlBuffer.append("	VO.WIP_ENTITY_NAME	 			AS woNumber,\n");
        sqlBuffer.append("	VO.STATUS_TYPE					AS woStatusId, \n");
        sqlBuffer.append("	EWSV2.WORK_ORDER_STATUS                          AS woStatus, \n");
        sqlBuffer.append("	--asset details\n");
        sqlBuffer.append("	VO.ASSET_NUMBER					AS asset_number, \n");
        sqlBuffer.append("	ITEM.SEGMENT1 					AS item_code_number,\n");
        sqlBuffer.append("	VO.ASSET_DESCRIPTION			AS asset_description,\n");
        sqlBuffer.append("	VO.ASSET_GROUP_ID				AS assetGroupId, \n");
        sqlBuffer.append("	VO.ASSET_GROUP					AS assetGroup,\n");
        sqlBuffer.append("	ASSET_ATTR.C_ATTRIBUTE2         AS asset_status,\n");
        sqlBuffer.append("	TO_CHAR(ASSET_ATTR.D_ATTRIBUTE1, 'dd/MM/yyyy')        AS dob,\n");
        sqlBuffer.append("	VO.ASSET_SERIAL_NUMBER			AS serial_number, \n");
        sqlBuffer.append("	HL.LOCATION_CODE				AS asset_location_code,\n");
        sqlBuffer.append("	HL.DESCRIPTION                  AS asset_location_desc,\n");
        sqlBuffer.append("	OWNER_ATTR.C_ATTRIBUTE4         AS asset_owner,		\n");
        sqlBuffer.append("	FFV.DESCRIPTION                 AS asset_owner_desc,		\n");
        sqlBuffer.append("	VO.ASSET_OWNING_DEPARTMENT	AS asset_owning_department, \n");
        sqlBuffer.append("	MBM_ATTR.C_ATTRIBUTE1           AS manufacturer,\n");
        sqlBuffer.append("	MBM_ATTR.C_ATTRIBUTE2          	AS brand,\n");
        sqlBuffer.append("	MBM_ATTR.C_ATTRIBUTE3          	AS model,\n");
        sqlBuffer.append("	PO_ATTR.N_ATTRIBUTE2            AS purchase_price,\n");
        sqlBuffer.append("	PO_ATTR.C_ATTRIBUTE6            AS asset_supplier,\n");
        sqlBuffer.append("	XXEAM_ASSET_SEARCH_UTIL_PKG.GET_RISK_LEVEL(121,PO_ATTR.C_ATTRIBUTE7,NULL) AS risk_level,\n");
        sqlBuffer.append("	CASE\n");
        sqlBuffer.append("		WHEN (MP_PARENT.MAINT_ORGANIZATION_ID IS NOT NULL AND (MP_PARENT.MAINT_ORGANIZATION_ID  != FND_PROFILE.VALUE('MFG_ORGANIZATION_ID')))\n");
        sqlBuffer.append("			THEN CII_PARENT.INSTANCE_NUMBER || '(' || MP_PARENT.ORGANIZATION_CODE || ')'\n");
        sqlBuffer.append("		ELSE CII_PARENT.INSTANCE_NUMBER\n");
        sqlBuffer.append("	END FATHER_ASSET_NUMBER,  \n");
        sqlBuffer.append( "	--order details\n");
        sqlBuffer.append("	VO.WORK_ORDER_TYPE 				AS woTypeId,\n");
        sqlBuffer.append("	MFG1.MEANING 					AS woType, \n");
        sqlBuffer.append("	TO_CHAR(to_date(OPERATIONINFO.ATTRIBUTE1, 'yyyy/mm/dd HH24:MI:SS'), 'dd/MM/yyyy hh24:MI')   AS breakdownScheduleDate,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE4 		AS equipSentToWorkshop,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE5 		AS disinfectionStatus,\n");
        sqlBuffer.append("	REGEXP_SUBSTR(WDJ.ATTRIBUTE15,'[^(]*',1,1) AS haContactPerson,\n");
        sqlBuffer.append("	REGEXP_SUBSTR(WDJ.ATTRIBUTE15,'\\((.*?)\\)',1,1, null,1) AS haContactPhone,\n");
        sqlBuffer.append("	WOEXT.HA_CONTACT_EMAIL			AS haContactEmail,\n");
        sqlBuffer.append("	WR.WORK_REQUEST_NUMBER			AS wrNumber,\n");
        sqlBuffer.append("	WDJ.ATTRIBUTE14					AS outboundDateTime, \n");
        sqlBuffer.append("	VO.DESCRIPTION					AS description, \n");
        sqlBuffer.append("	WOEXT.REMARK_FOR_VENDOR			AS remarkForVendor,\n");
        sqlBuffer.append("	WOEXT.REMARK_FOR_USER			AS remarkForUser,\n");
        sqlBuffer.append("	--MAINTENANCE DETAILS\n");
        sqlBuffer.append("	WDJ.ATTRIBUTE1					AS maintenance_body, \n");
        sqlBuffer.append("	WDJ.ATTRIBUTE8					AS maintenance_body_type, \n");
        sqlBuffer.append("	WDJ.ATTRIBUTE3					AS contract_num, \n");
        sqlBuffer.append("	WDJ.ATTRIBUTE13					AS auto_send_wo_to_supplier,\n");
        sqlBuffer.append("	WDJ.ATTRIBUTE12					AS supplier_agreement_number, 	\n");
        sqlBuffer.append("	WDJ.ATTRIBUTE9					AS maintenance_plan, \n");
        sqlBuffer.append("	WDJ.ATTRIBUTE10					AS maintenance_join_date, \n");
        sqlBuffer.append("	WDJ.ATTRIBUTE11					AS maintenance_expiry_date, \n");
        sqlBuffer.append("	WDJ.ATTRIBUTE2					AS maintenance_contact_person, \n");
        sqlBuffer.append("	WDJ.ATTRIBUTE4					AS maintenance_contact_phone, \n");
        sqlBuffer.append("	WDJ.ATTRIBUTE5					AS maintenance_contact_fax_number, \n");
        sqlBuffer.append("	WDJ.ATTRIBUTE6					AS maintenance_contact_email, \n");
        sqlBuffer.append("	CONTRACTINFO.MAINTENANCE_INTERVAL AS maintenance_interval,\n");
        sqlBuffer.append("	--Other Details\n");
        sqlBuffer.append("	WDJ.CREATED_BY					AS createdById,\n");
        sqlBuffer.append("	FU.USER_NAME					AS createdBy, \n");
        sqlBuffer.append("	WDJ.CREATION_DATE				AS creationDate,\n");
        sqlBuffer.append("	MP.ORGANIZATION_CODE                            AS asset_organization_code,\n");
        sqlBuffer.append("   BD.DEPARTMENT_CODE 				AS owningDept,\n");
        sqlBuffer.append("   BD.DESCRIPTION                          AS asset_owning_department_desc,\n");
        sqlBuffer.append("	--Repair Details\n");
        sqlBuffer.append("	( SELECT ATTRIBUTE5 FROM EAM_JOB_COMPLETION_TXNS EJCT WHERE EJCT.TRANSACTION_ID= (SELECT MAX(TRANSACTION_ID) FROM EAM_JOB_COMPLETION_TXNS EJCT1 WHERE EJCT1.ORGANIZATION_ID=VO.ORGANIZATION_ID AND EJCT1.WIP_ENTITY_ID=WDJ.WIP_ENTITY_ID AND VO.STATUS_TYPE IN (4,5,12,14,15) ) ) as vendorReferenceNumber, 	\n");
        sqlBuffer.append("	FAILUREINFO.CAUSE_CODE			AS failureCauseCode, \n");
        sqlBuffer.append("	FAILUREINFO.FAILURE_CODE		AS failureSymptonCode, \n");
        sqlBuffer.append("	FAILUREINFO.RESOLUTION_CODE		AS failureResolCode,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE2 		AS callReceivedDateTime,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE3 		AS equipCondition,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE7 		AS attendanceDateTime,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE8 		AS equipReceivedDateTime,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE9 		AS reinstatementDateTime,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE10 		AS technicalName,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE11 		AS laborCost,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE12 		AS sparePartDesc,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE13 		AS sparePartCost,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE14 		AS servicReportReference,\n");
        sqlBuffer.append("	OPERATIONINFO.ATTRIBUTE15		AS resultAndAction,\n");
        sqlBuffer.append("	( SELECT ATTRIBUTE1 FROM EAM_JOB_COMPLETION_TXNS EJCT WHERE EJCT.TRANSACTION_ID= (SELECT MAX(TRANSACTION_ID) FROM EAM_JOB_COMPLETION_TXNS EJCT1 WHERE EJCT1.ORGANIZATION_ID=VO.ORGANIZATION_ID AND EJCT1.WIP_ENTITY_ID=WDJ.WIP_ENTITY_ID AND VO.STATUS_TYPE IN (4,5,12,14,15) ) ) AS woCompletedBy, \n");
        sqlBuffer.append("	( SELECT ATTRIBUTE2 FROM EAM_JOB_COMPLETION_TXNS EJCT WHERE EJCT.TRANSACTION_ID= (SELECT MAX(TRANSACTION_ID) FROM EAM_JOB_COMPLETION_TXNS EJCT1 WHERE EJCT1.ORGANIZATION_ID=VO.ORGANIZATION_ID AND EJCT1.WIP_ENTITY_ID=WDJ.WIP_ENTITY_ID AND VO.STATUS_TYPE IN (4,5,12,14,15) ) ) AS addLaborCost, \n");
        sqlBuffer.append("	( SELECT ATTRIBUTE3 FROM EAM_JOB_COMPLETION_TXNS EJCT WHERE EJCT.TRANSACTION_ID= (SELECT MAX(TRANSACTION_ID) FROM EAM_JOB_COMPLETION_TXNS EJCT1 WHERE EJCT1.ORGANIZATION_ID=VO.ORGANIZATION_ID AND EJCT1.WIP_ENTITY_ID=WDJ.WIP_ENTITY_ID AND VO.STATUS_TYPE IN (4,5,12,14,15) ) ) AS addMaterialCost, \n");
        sqlBuffer.append("	( SELECT ATTRIBUTE4 FROM EAM_JOB_COMPLETION_TXNS EJCT WHERE EJCT.TRANSACTION_ID= (SELECT MAX(TRANSACTION_ID) FROM EAM_JOB_COMPLETION_TXNS EJCT1 WHERE EJCT1.ORGANIZATION_ID=VO.ORGANIZATION_ID AND EJCT1.WIP_ENTITY_ID=WDJ.WIP_ENTITY_ID AND VO.STATUS_TYPE IN (4,5,12,14,15) ) ) AS addMaterialDesc\n");
        sqlBuffer.append("FROM WIP_EAM_WORK_ORDER_DTLS_V VO , \n");
        sqlBuffer.append("	MFG_LOOKUPS MFG1, \n");
        sqlBuffer.append("	MTL_SYSTEM_ITEMS_KFV MSI , \n");
        sqlBuffer.append("	WIP_DISCRETE_JOBS WDJ , \n");
        sqlBuffer.append("	BOM_DEPARTMENTS BD, \n");
        sqlBuffer.append("	FND_USER FU, \n");
        sqlBuffer.append("	MTL_EAM_ASSET_ATTR_VALUES ASSET_ATTR,\n");
        sqlBuffer.append("	MTL_EAM_ASSET_ATTR_VALUES OWNER_ATTR,\n");
        sqlBuffer.append("	MTL_EAM_ASSET_ATTR_VALUES MBM_ATTR,\n");
        sqlBuffer.append("	MTL_EAM_ASSET_ATTR_VALUES PO_ATTR,\n");
        sqlBuffer.append("	MTL_EAM_ASSET_ATTR_VALUES LOC_ATTR,\n");
        sqlBuffer.append("	HR_LOCATIONS HL,\n");
        sqlBuffer.append("	CSI_ITEM_INSTANCES CII,\n");
        sqlBuffer.append("	MTL_SYSTEM_ITEMS_B_KFV MSI_PARENT,\n");
        sqlBuffer.append("	MTL_SERIAL_NUMBERS MSN,\n");
        sqlBuffer.append("	MTL_OBJECT_GENEALOGY MOG,\n");
        sqlBuffer.append("	MTL_SERIAL_NUMBERS MSN_PARENT,\n");
        sqlBuffer.append("	CSI_ITEM_INSTANCES CII_PARENT,\n");
        sqlBuffer.append("	MTL_PARAMETERS MP_PARENT,\n");
        sqlBuffer.append("	XXEAM_WORK_ORDERS_EXT WOEXT,\n");
        sqlBuffer.append("	WIP_EAM_WORK_REQUESTS WR,\n");
        sqlBuffer.append("	WIP_ENTITIES WE,\n");
        sqlBuffer.append("	EAM_WORK_ORDER_DETAILS EWOD,\n");
        sqlBuffer.append("	EAM_WO_STATUSES_V EWSV,\n");
        sqlBuffer.append("	EAM_WO_STATUSES_V EWSV2,\n");
        sqlBuffer.append("	EAM_FAILUREINFO_V FAILUREINFO,\n");
        sqlBuffer.append("	WIP_OPERATIONS OPERATIONINFO,\n");
        sqlBuffer.append("	XXEAM_MAINT_CONTRACT CONTRACTINFO,\n");
        sqlBuffer.append("	FND_FLEX_VALUES_VL FFV,\n");
        sqlBuffer.append("	FND_FLEX_VALUE_SETS FFVS,\n");                    
        sqlBuffer.append("	MTL_PARAMETERS MP,\n");
        sqlBuffer.append("	( SELECT MSIB.INVENTORY_ITEM_ID, MSIB.SEGMENT1 FROM MTL_SYSTEM_ITEMS_B MSIB, HR_ORGANIZATION_UNITS HOU WHERE MSIB.ORGANIZATION_ID = HOU.ORGANIZATION_ID AND HOU.NAME = 'ITEM MASTER' ) ITEM \n");
        sqlBuffer.append("WHERE MFG1.LOOKUP_TYPE = 'WIP_EAM_WORK_ORDER_TYPE'\n");
        sqlBuffer.append("AND TO_CHAR(MFG1.LOOKUP_CODE(+)) = VO.WORK_ORDER_TYPE \n");
        sqlBuffer.append("AND MSI.INVENTORY_ITEM_ID(+) = VO.REBUILD_ITEM_ID \n");
        sqlBuffer.append("AND MSI.ORGANIZATION_ID(+) = VO.ORGANIZATION_ID \n");
        sqlBuffer.append("AND VO.WIP_ENTITY_ID = WDJ.WIP_ENTITY_ID \n");
        sqlBuffer.append("AND WDJ.CREATED_BY = FU.USER_ID(+) \n");
        sqlBuffer.append("AND WDJ.OWNING_DEPARTMENT = BD.DEPARTMENT_ID(+) \n");
        sqlBuffer.append("AND CII.INSTANCE_NUMBER (+) = VO.ASSET_NUMBER\n");
        sqlBuffer.append("AND CII.INSTANCE_ID   = ASSET_ATTR.MAINTENANCE_OBJECT_ID (+)\n");
        sqlBuffer.append("AND CII.INSTANCE_ID   = OWNER_ATTR.MAINTENANCE_OBJECT_ID (+)\n");
        sqlBuffer.append("AND CII.INSTANCE_ID   = PO_ATTR.MAINTENANCE_OBJECT_ID (+)\n");
        sqlBuffer.append("AND CII.INSTANCE_ID   = MBM_ATTR.MAINTENANCE_OBJECT_ID (+)\n");
        sqlBuffer.append("AND CII.INSTANCE_ID   = LOC_ATTR.MAINTENANCE_OBJECT_ID (+)\n");
        sqlBuffer.append("AND ASSET_ATTR.ATTRIBUTE_CATEGORY (+) = 'Asset Details'\n");
        sqlBuffer.append("AND OWNER_ATTR.ATTRIBUTE_CATEGORY (+) = 'Ownership Details'\n");
        sqlBuffer.append("AND PO_ATTR.ATTRIBUTE_CATEGORY (+)    = 'Purchase Order Details'\n");
        sqlBuffer.append("AND MBM_ATTR.ATTRIBUTE_CATEGORY (+)   = 'MBM Details'\n");
        sqlBuffer.append("AND LOC_ATTR.ATTRIBUTE_CATEGORY (+)   = 'Location'\n");
        sqlBuffer.append("AND LOC_ATTR.C_ATTRIBUTE1               = HL.LOCATION_ID (+)\n");
        sqlBuffer.append("AND CII.INVENTORY_ITEM_ID               = MSN.INVENTORY_ITEM_ID \n");
        sqlBuffer.append("AND CII.SERIAL_NUMBER                   = MSN.SERIAL_NUMBER\n");
        sqlBuffer.append("AND MSN.GEN_OBJECT_ID                   = MOG.OBJECT_ID(+)\n");
        sqlBuffer.append("AND MOG.PARENT_OBJECT_ID                = MSN_PARENT.GEN_OBJECT_ID(+)\n");
        sqlBuffer.append("AND MOG.GENEALOGY_TYPE(+)               = 5\n");
        sqlBuffer.append("AND SYSDATE                            >= NVL(MOG.START_DATE_ACTIVE(+), SYSDATE)\n");
        sqlBuffer.append("AND SYSDATE                            <= NVL(MOG.END_DATE_ACTIVE(+), SYSDATE)  \n");
        sqlBuffer.append("AND MSN_PARENT.INVENTORY_ITEM_ID        = CII_PARENT.INVENTORY_ITEM_ID (+)\n");
        sqlBuffer.append("AND MSN_PARENT.SERIAL_NUMBER            = CII_PARENT.SERIAL_NUMBER (+)\n");
        sqlBuffer.append("AND CII_PARENT.INVENTORY_ITEM_ID        = MSI_PARENT.INVENTORY_ITEM_ID(+)\n");
        sqlBuffer.append("AND CII_PARENT.LAST_VLD_ORGANIZATION_ID = MSI_PARENT.ORGANIZATION_ID(+)\n");
        sqlBuffer.append("AND CII_PARENT.LAST_VLD_ORGANIZATION_ID = MP_PARENT.ORGANIZATION_ID (+)\n");
        sqlBuffer.append("AND	VO.WIP_ENTITY_ID = WOEXT.WIP_ENTITY_ID (+)\n");
        sqlBuffer.append("AND VO.WIP_ENTITY_ID = WR.WIP_ENTITY_ID (+)\n");
        sqlBuffer.append("AND VO.WIP_ENTITY_ID = FAILUREINFO.WIP_ENTITY_ID (+)\n");
        sqlBuffer.append("AND VO.WIP_ENTITY_ID = OPERATIONINFO.WIP_ENTITY_ID (+)\n");
        sqlBuffer.append("AND WR.WIP_ENTITY_ID = WE.WIP_ENTITY_ID (+)\n");
        sqlBuffer.append("AND WR.ORGANIZATION_ID = WE.ORGANIZATION_ID (+)\n");
        sqlBuffer.append("AND WE.WIP_ENTITY_ID = ewod.WIP_ENTITY_ID (+)\n");
        sqlBuffer.append("AND ewsv.status_id (+) = ewod.user_defined_status_id\n");
        sqlBuffer.append("AND vo.status_type = ewsv2.status_id (+)\n");
        sqlBuffer.append("AND REGEXP_SUBSTR(WDJ.ATTRIBUTE3,'[^-]*',1,1) = TO_CHAR(CONTRACTINFO.PO_NUMBER (+))\n");
        sqlBuffer.append("AND REGEXP_SUBSTR(WDJ.ATTRIBUTE3,'[^-]*',1,3) = TO_CHAR(CONTRACTINFO.LINE (+))\n");
        sqlBuffer.append("AND UPPER(OWNER_ATTR.C_ATTRIBUTE4) = FFV.FLEX_VALUE (+) \n");
        sqlBuffer.append("AND FFVS.FLEX_VALUE_SET_ID (+) = FFV.FLEX_VALUE_SET_ID \n");
        sqlBuffer.append("AND FFVS.FLEX_VALUE_SET_NAME = 'XXEAM_ASSET_OWNER'\n");
        sqlBuffer.append("AND FFV.ENABLED_FLAG (+) = 'Y'\n");
        sqlBuffer.append("AND TRUNC(SYSDATE) BETWEEN NVL(FFV.START_DATE_ACTIVE (+), TRUNC(SYSDATE))\n");
        sqlBuffer.append("AND NVL(FFV.END_DATE_ACTIVE (+), TRUNC(SYSDATE)) \n");
        sqlBuffer.append("AND PO_ATTR.C_ATTRIBUTE7 = ITEM.INVENTORY_ITEM_ID (+) \n");
        sqlBuffer.append("AND VO.ORGANIZATION_ID = MP.ORGANIZATION_ID (+) ");

        sqlBuffer.setLength(0);
        if (assetRisk!="" && assetRisk != null) {
        	sqlBuffer.append("select woId, woNumber, woStatusId, woStatus, user_woStatus, asset_number, item_code_number, asset_description, assetGroupId, assetGroup, asset_status, dob, serial_number, asset_location_code, asset_location_desc, asset_owner, asset_owner_desc, asset_owning_department, manufacturer, brand, model, purchase_price, asset_supplier, risk_level, FATHER_ASSET_NUMBER, woTypeId, woType, breakdownScheduleDate, scheduleDate, breakdownDate, equipSentToWorkshop, disinfectionStatus, haContactPerson, haContactPhone, haContactEmail, wrNumber, outboundDateTime, description, remarkForVendor, remarkForUser, maintenance_body, maintenance_body_type, contract_num, auto_send_wo_to_supplier, supplier_agreement_number, maintenance_plan, maintenance_join_date, maintenance_expiry_date, maintenance_contact_person, maintenance_contact_phone, maintenance_contact_fax_number, maintenance_contact_email, maintenance_interval, createdById, createdBy, creationDate, asset_organization_code, owningDept, OWNINGDEPTDESC, vendorReferenceNumber, failureCauseCode, failureSymptonCode, failureResolCode, callReceivedDateTime, equipCondition, attendanceDateTime, equipReceivedDateTime, reinstatementDateTime, technicalName, laborCost, sparePartDesc, sparePartCost, servicReportReference, resultAndAction, woCompletedBy, addLaborCost, addMaterialCost, addMaterialDesc, operation_description, createdByName, organizationcode \n");
        	sqlBuffer.append(" (select vendor_name from xxeam_maintenance_body_dff_v where vendor_number = VO.maintenance_body) as maintenance_body_name \n");
        	sqlBuffer.append("from XXEAM_EXT_WORK_ORDERS_V VO, xxeam_accessible_org_v AO \n");
        }else {
        	sqlBuffer.append("select woNumber, asset_number, asset_description, woType, user_woStatus, breakdownDate, scheduleDate, createdBy, maintenance_body, owningDept, contract_num, asset_location_code, asset_owner, assetGroup, creationDate, serial_number, manufacturer, brand, model, createdByName, description, organizationcode, \n");
    		sqlBuffer.append(" (select vendor_name from xxeam_maintenance_body_dff_v where vendor_number = VO.maintenance_body) as maintenance_body_name \n");
        	sqlBuffer.append("from XXEAM_EXT_WORK_ORDER_SIMPLE_V VO, xxeam_accessible_org_v AO \n");
        }
        sqlBuffer.append("WHERE xxeam_maint_body_sec_chk_wo(VO.maintenance_body) = 'Y' \n");
        //sqlBuffer.append("AND VO.organization_id IN (SELECT organization_id FROM xxeam_accessible_org_v) \n");
        //sqlBuffer.append("AND  VO.OWNING_DEPARTMENT_ID  IN (SELECT department_id FROM xxeam_accessible_dept_v) \n");
        sqlBuffer.append("AND VO.organization_id = AO.organization_id \n");
        //gatest4 comment
        if(eamOrg != null && !"".equals(eamOrg)){
            sqlBuffer.append("AND VO.ORGANIZATION_ID = :eamOrg ");
            paramMap.addValue("eamOrg", eamOrg.toUpperCase());
        }
        
        if(woNumber != null && !"".equals(woNumber)){
            sqlBuffer.append("AND VO.WONUMBER LIKE :woNumber ");
            paramMap.addValue("woNumber", woNumber.trim() + "%");
        }
        
        if(assetNumber != null && !"".equals(assetNumber)){
            sqlBuffer.append("AND VO.ASSET_NUMBER = :assetNumber ");
            paramMap.addValue("assetNumber", assetNumber);
        }
        
        if(woType != null && !"".equals(woType)){
            sqlBuffer.append("AND VO.woTypeId = :woType ");
            paramMap.addValue("woType", woType);
        }
        
        if(woStatus != null && woStatus.size() != 0){
			sqlBuffer.append("AND VO.user_defined_status_id in (:woStatus) ");
            paramMap.addValue("woStatus", woStatus);
        }


        if((WorkRequestConstant.BREAKDOWN_DATE).equals(dateType)) {
            if(dateFrom != null && !"".equals(dateFrom)){
                sqlBuffer.append("AND trunc(VO.breakdownDate) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy')) ");
                paramMap.addValue("dateFrom", dateFrom);
            }   
            if(dateTo != null && !"".equals(dateTo)){
                sqlBuffer.append( "AND trunc(VO.breakdownDate) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
                paramMap.addValue("dateTo", dateTo);
            }   
        }else if((WorkRequestConstant.CREATED_DATE).equals(dateType)) {
            if(dateFrom != null && !"".equals(dateFrom)){
                sqlBuffer.append("AND trunc(VO.creationDate) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy'))  ");
                paramMap.addValue("dateFrom", dateFrom);
            }   
            if(dateTo != null && !"".equals(dateTo)){
                sqlBuffer.append("AND trunc(VO.creationDate) <= trunc(to_date(:dateTo , 'dd/mm/yyyy'))  ");
                paramMap.addValue("dateTo", dateTo);
            }   
        }else if((WorkRequestConstant.SCHEDULE_DATE).equals(dateType)) {            
            if(dateFrom != null && !"".equals(dateFrom)){
                sqlBuffer.append("AND trunc(VO.SCHEDULEDATE) >= trunc(to_date(:dateFrom , 'dd/mm/yyyy')) ");
                paramMap.addValue("dateFrom", dateFrom);
            }   
            if(dateTo != null && !"".equals(dateTo)){
                sqlBuffer.append( "AND trunc(VO.SCHEDULEDATE) <= trunc(to_date(:dateTo , 'dd/mm/yyyy')) ");
                paramMap.addValue("dateTo", dateTo);
            }   
        }

        if(assetRisk != null && !"".equals(assetRisk)){
            sqlBuffer.append("AND VO.risk_level = :assetRisk ");
            paramMap.addValue("assetRisk", assetRisk.toUpperCase());
        }

        if(maintenanceContract != null && !"".equals(maintenanceContract)){
            sqlBuffer.append("AND VO.contract_num LIKE :maintenanceContract ");
            paramMap.addValue("maintenanceContract", maintenanceContract + "%");
        }
        
        if(maintenanceVendor != null && !"".equals(maintenanceVendor)){
            sqlBuffer.append("AND VO.maintenance_body = :maintenanceVendorNum ");
            paramMap.addValue("maintenanceVendorNum", hiddenMBody.toUpperCase());
        }

        if(serialNumber != null && !"".equals(serialNumber)){
            sqlBuffer.append("AND VO.serial_number LIKE :serialNumber ");
            paramMap.addValue("serialNumber", serialNumber + "%");
        }

        if(assetLocation != null && !"".equals(assetLocation)){
            sqlBuffer.append("AND VO.asset_location_code = :assetLocation ");
            paramMap.addValue("assetLocation", assetLocation);
        }
        
        if(assetOwner != null && !"".equals(assetOwner)){
            sqlBuffer.append("AND VO.asset_owner = :assetOwner ");
            paramMap.addValue("assetOwner", assetOwner);
        }
        
        if(owningDept != null && !"".equals(owningDept)){
            sqlBuffer.append("AND VO.owningDept = :owningDept ");
            paramMap.addValue("owningDept", owningDept);
        }
        
        if(createdBy != null && !"".equals(createdBy)){
            sqlBuffer.append("AND VO.createdBy = :createdBy ");
            paramMap.addValue("createdBy", createdBy);
        }
        
        if(criticalOnly){
            sqlBuffer.append("AND EXISTS (\r\n" + 
            		"SELECT 1 \r\n" + 
            		"FROM MTL_SYSTEM_ITEMS MSI,\r\n" + 
            		"     MFG_LOOKUPS  L\r\n" + 
            		"WHERE\r\n" + 
            		"MSI.INVENTORY_ITEM_ID = VO.ITEM_ID\r\n" + 
            		"AND L.MEANING = MSI.SEGMENT1\r\n" + 
            		"AND MSI.ORGANIZATION_ID = 121\r\n" + 
            		"AND L.LOOKUP_TYPE = 'XXEAM_CRITICAL_MEDICAL_EQUIP' \r\n" + 
            		"AND L.ENABLED_FLAG = 'Y' \r\n" + 
            		"AND nvl(L.START_DATE_ACTIVE,trunc(sysdate)) <= trunc(sysdate)\r\n" + 
            		"AND nvl(L.END_DATE_ACTIVE,trunc(sysdate)) >= trunc(sysdate\r\n" + 
            		"))\r\n ");
        }
        
        if(urgentOnly){
            sqlBuffer.append("AND VO.DESCRIPTION = 'URGENT ORDER CREATED BY INTERFACE' ");
        }
        
        if(dashboardValue != null && !"".equals(dashboardValue)){
        	if("Overdue".equals(dashboardValue)) {
        		sqlBuffer.append("AND trunc(VO.SCHEDULED_START_DATE) <= trunc(sysdate) ");
        	}
        	else if("Upcoming".equals(dashboardValue)) {
        		sqlBuffer.append("AND trunc(VO.SCHEDULED_START_DATE) > trunc(sysdate) ");
        	}
        }
        
        //sqlBuffer.append("ORDER BY creationDate DESC");
        
        logger.info("SearchWO SQL:"+sqlBuffer.toString());        
//        List<WorkRequest> listWorkRequest = jdbcTemplate.query(sqlBuffer.toString(), new RowMapper<WorkRequest>() {
        List<WorkOrder> listWorkOrder = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<WorkOrder>() {
            @Override
            public WorkOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
            	WorkOrder aWorkOrder = new WorkOrder();
            	Date aDate = new Date();
            	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            	//*aWorkOrder.setWorkOrderId(rs.getInt("woId"));            	     
            	aWorkOrder.setWorkOrderNumber(rs.getString("woNumber"));
            	aWorkOrder.setAssetNumber(rs.getString("asset_number"));
            	aWorkOrder.setAssetDescription(rs.getString("asset_description"));
            	aWorkOrder.setWoType(rs.getString("woType"));
            	//aWorkOrder.setWoStatus(rs.getString("woStatus"));
				aWorkOrder.setWoStatus(rs.getString("user_woStatus"));
				/* *if(rs.getString("breakdownScheduleDate") != null) {
            		aDate = rs.getTimestamp("breakdownScheduleDate");
                	aWorkOrder.setBreakdownScheduleDate(sdf.format(aDate));
            	}* */
				if(rs.getString("breakdownDate") != null) {
            		aDate = rs.getTimestamp("breakdownDate");
                	aWorkOrder.setBreakdownDate(sdf.format(aDate));
            	}
				if(rs.getString("scheduleDate") != null) {
            		aDate = rs.getTimestamp("scheduleDate");
                	aWorkOrder.setScheduleDate(sdf.format(aDate));
            	}
				//*aWorkOrder.setEquipmentSent(rs.getString("equipSentToWorkshop"));
				//*aWorkOrder.setWoDescription(rs.getString("description"));
				//*aWorkOrder.setUserRemark(rs.getString("remarkForUser"));
				//*aWorkOrder.setVendorRemark(rs.getString("remarkForVendor"));
				//*aWorkOrder.setHaContactPerson(rs.getString("haContactPerson"));
				//*aWorkOrder.setHaContactPhone(rs.getString("haContactPhone"));
				//*aWorkOrder.setHaContactEmail(rs.getString("haContactEmail"));
				//*aWorkOrder.setDisinfectionStatus(rs.getString("disinfectionStatus"));
				
				/* *aWorkOrder.setMaintenanceBodyType(rs.getString("maintenance_body_type"));
				aWorkOrder.setAutoSendWO(rs.getString("auto_send_wo_to_supplier"));
				aWorkOrder.setmContactPerson(rs.getString("maintenance_contact_person"));
				aWorkOrder.setmContactPhone(rs.getString("maintenance_contact_phone"));
				aWorkOrder.setmContactFax(rs.getString("maintenance_contact_fax_number"));
				aWorkOrder.setmContactEmail(rs.getNString("maintenance_contact_email"));* */
				
            	aWorkOrder.setCreatedBy(rs.getString("createdByName"));
            	//aWorkOrder.setMaintenanceBody(rs.getString("maintenance_body"));
            	if (rs.getString("maintenance_body_name")!=null) {
            		aWorkOrder.setMaintenanceBody(rs.getString("maintenance_body_name") + " (" + rs.getString("maintenance_body") + ")");
            	}
            	aWorkOrder.setOwningDepartment(rs.getString("owningDept"));
            	aWorkOrder.setMaintenanceContract(rs.getString("contract_num"));
            	aWorkOrder.setAssetLocation(rs.getString("asset_location_code"));
            	aWorkOrder.setAssetOwner(rs.getString("asset_owner"));
            	//**aWorkOrder.setAssetRisk(rs.getString("risk_level"));
            	aDate = rs.getTimestamp("creationDate");
            	aWorkOrder.setCreationDate(aDate);
            	aWorkOrder.setAssetGroup(rs.getString("assetGroup"));
            	aWorkOrder.setAssetSerialNumber(rs.getString("serial_number"));
            	aWorkOrder.setManufacturer(rs.getString("manufacturer"));
            	aWorkOrder.setBrand(rs.getString("brand"));
            	aWorkOrder.setModel(rs.getString("model"));
            	
            	/* *aWorkOrder.setSupplerAgreementNo(rs.getString("supplier_agreement_number"));
            	aWorkOrder.setOutboundDate(rs.getString("outboundDateTime"));
            	aWorkOrder.setWrNumber(rs.getString("wrnumber"));
            	aWorkOrder.setWoStatusId(rs.getString("wostatusid"));
            	aWorkOrder.setWoTypeId(rs.getString("wotypeid"));
            	aWorkOrder.setItemCodeNumber(rs.getString("item_code_number"));
            	aWorkOrder.setAssetStatus(rs.getString("asset_status"));
            	aWorkOrder.setDob(rs.getString("dob"));
            	aWorkOrder.setAssetLocationDesc(rs.getString("asset_location_desc"));
            	aWorkOrder.setAssetOwnerDesc(rs.getString("asset_owner_desc"));
            	aWorkOrder.setAssetOwningDepartmentDesc(rs.getString("asset_owning_department_desc"));
            	aWorkOrder.setPurchasePrice(rs.getString("purchase_price"));
            	aWorkOrder.setAssetSupplier(rs.getString("asset_supplier"));
            	aWorkOrder.setFatherAssetNumber(rs.getString("father_asset_number"));
            	aWorkOrder.setAssetOrg(rs.getString("asset_organization_code"));
            	aWorkOrder.setOperationDescription(rs.getString("operation_description"));* */
            	aWorkOrder.setWorkOrderOrg(rs.getString("organizationcode"));
            	aWorkOrder.setWoDescription(rs.getString("description"));
            	
                logger.debug("Retrieved Work Order Number " + aWorkOrder.getWorkOrderNumber());
                
                return aWorkOrder;
            }
     
        });
        logger.debug("Query Finish time "+new Date());
        logger.debug("WO No of rec="+listWorkOrder.size());
        logger.debug("Query Limit="+searchCriteria.getQueryLimit());
        if (listWorkOrder.size()>searchCriteria.getQueryLimit()) {
        	int wosize = listWorkOrder.size();
        	listWorkOrder.clear();
        	WorkOrder tWorkOrder = new WorkOrder();
        	//tWorkOrder.setWoDescription(String.valueOf(wosize));
        	tWorkOrder.setOperationDescription(String.valueOf(wosize));
        	listWorkOrder.add(tWorkOrder);
        }
        //Sorting records by creation date (descending)
        if (listWorkOrder.size()>1) {
        	logger.debug("Sorting Start "+new Date());
	        Collections.sort(listWorkOrder, new Comparator<WorkOrder>() {
	            public int compare(WorkOrder wo2, WorkOrder wo1) {
	                  return wo1.getCreationDate().compareTo(wo2.getCreationDate());
	             }
	        });
	        logger.debug("Sorting Complete "+new Date());
        }
        
        return listWorkOrder;
        
    	
    }
   
    public String saveWorkOrder(WorkOrder workOrder, Boolean autoValue, String[] org, String userId, String respId, String appId) throws Exception {
		Long transactionCode = null;
				
		//Perform validation
		String validateResult;
//		validateResult = validateWOData(workOrder);
//		if (!validateResult.equals("PASS")) {
//			return validateResult;
//		}
  
		int woStatus = 0;
		
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("DECLARE \n");
		sqlBuilder.append("	i_eam_wo_rec              EAM_PROCESS_WO_PUB.eam_wo_rec_type;\n");
		sqlBuilder.append("	i_eam_op_rec              EAM_PROCESS_WO_PUB.eam_op_rec_type;\n");
		sqlBuilder.append("	i_eam_op_tbl              EAM_PROCESS_WO_PUB.eam_op_tbl_type;\n");
		sqlBuilder.append("	i_eam_op_network_tbl      EAM_PROCESS_WO_PUB.eam_op_network_tbl_type;\n");
		sqlBuilder.append("	i_eam_res_tbl             EAM_PROCESS_WO_PUB.eam_res_tbl_type;\n");
		sqlBuilder.append("	i_eam_res_inst_tbl        EAM_PROCESS_WO_PUB.eam_res_inst_tbl_type;\n");
		sqlBuilder.append("	i_eam_sub_res_tbl         EAM_PROCESS_WO_PUB.eam_sub_res_tbl_type;\n");
		sqlBuilder.append("	i_eam_res_usage_tbl       EAM_PROCESS_WO_PUB.eam_res_usage_tbl_type;\n");
		sqlBuilder.append("	i_eam_mat_req_rec         EAM_PROCESS_WO_PUB.eam_mat_req_rec_type;\n");
		sqlBuilder.append("	i_eam_mat_req_tbl         EAM_PROCESS_WO_PUB.eam_mat_req_tbl_type;\n");
		sqlBuilder.append("	i_eam_direct_items_tbl    EAM_PROCESS_WO_PUB.eam_direct_items_tbl_type;\n");
		sqlBuilder.append("	i_eam_wo_comp_rec         EAM_PROCESS_WO_PUB.eam_wo_comp_rec_type;\n");
		sqlBuilder.append("	i_eam_wo_quality_tbl      EAM_PROCESS_WO_PUB.eam_wo_quality_tbl_type;\n");
		sqlBuilder.append("	i_eam_meter_reading_tbl   EAM_PROCESS_WO_PUB.eam_meter_reading_tbl_type;\n");
		sqlBuilder.append("	i_eam_counter_prop_tbl    EAM_PROCESS_WO_PUB.eam_counter_prop_tbl_type;\n");
		sqlBuilder.append("	i_eam_wo_comp_rebuild_tbl EAM_PROCESS_WO_PUB.eam_wo_comp_rebuild_tbl_type;\n");
		sqlBuilder.append("	i_eam_wo_comp_mr_read_tbl EAM_PROCESS_WO_PUB.eam_wo_comp_mr_read_tbl_type;\n");
		sqlBuilder.append("	i_eam_op_comp_tbl         EAM_PROCESS_WO_PUB.eam_op_comp_tbl_type;\n");
		sqlBuilder.append("	i_eam_request_tbl         EAM_PROCESS_WO_PUB.eam_request_tbl_type;\n");
		sqlBuilder.append("	i_eam_permit_tbl          EAM_PROCESS_PERMIT_PUB.eam_wp_tbl_type;\n");
		sqlBuilder.append("	i_eam_permit_wo_assoc_tbl EAM_PROCESS_PERMIT_PUB.eam_wp_association_tbl_type;\n");
		sqlBuilder.append("	i_work_order_ext_rec  XXEAM_WORK_ORDERS_EXT%ROWTYPE;\n");
		sqlBuilder.append(" v_chk_update_date varchar2(20); \n");
		sqlBuilder.append("begin\n");
		sqlBuilder.append("	i_eam_wo_rec.transaction_type 			:= ?;\n" ); // 1
		sqlBuilder.append("	i_eam_wo_rec.wip_entity_name 			:= ?;\n" ); // 2
		sqlBuilder.append("	i_eam_wo_rec.wip_entity_id 			:= ?;\n" ); // 3
		sqlBuilder.append("	i_eam_wo_rec.organization_id 			:= ?;\n" ); // 4
		sqlBuilder.append("	i_eam_wo_rec.asset_group_id 			:= ?;\n" ); // 5
		sqlBuilder.append("	i_eam_wo_rec.asset_number 			:= ?;\n" ); // 6
		sqlBuilder.append("	i_eam_wo_rec.maintenance_object_source          := 1;\n" ); 
		sqlBuilder.append("	i_eam_wo_rec.maintenance_object_type            := 3;\n" ); 
		sqlBuilder.append("	i_eam_wo_rec.maintenance_object_id 		:= ?;\n" ); // 7
		sqlBuilder.append("	i_eam_wo_rec.class_code 			:= 'EAMDEFAULT';\n" ); 
		sqlBuilder.append("	i_eam_wo_rec.user_defined_status_id 	:= ?;\n" ); // 8
		sqlBuilder.append("	i_eam_wo_rec.scheduled_start_date 		:= to_date(?, 'DD/MM/YYYY HH24:MI');\n" ); // 9
		sqlBuilder.append("	i_eam_wo_rec.requested_start_date 		:= sysdate;\n" );
		sqlBuilder.append("	i_eam_wo_rec.scheduled_completion_date          := i_eam_wo_rec.scheduled_start_date;\n" );
		sqlBuilder.append("	i_eam_wo_rec.WORK_ORDER_TYPE 			:= ?;\n" ); // 10
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE15 			:= ?;\n" ); // 11
		sqlBuilder.append("	i_eam_wo_rec.DESCRIPTION 			:= ?;\n" ); // 12
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE1 			:= ?;\n" ); // 13
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE8 			:= ?;\n" ); // 14
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE13 			:= ?;\n" ); // 15
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE2 			:= ?;\n" ); // 16
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE4 			:= ?;\n" ); // 17
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE5 			:= ?;\n" ); // 18
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE6 			:= ?;\n" ); // 19
		
		sqlBuilder.append("	i_eam_wo_rec.OWNING_DEPARTMENT 			:= ?;\n" ); // 20
		sqlBuilder.append("	i_eam_wo_rec.PRIORITY 			:= 20;\n" );
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE3 			:= ?;\n" ); // 21
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE9 			:= ?;\n" ); // 22
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE10 			:= ?;\n" ); // 23
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE11 			:= ?;\n" ); // 24
		sqlBuilder.append("	i_eam_wo_rec.ATTRIBUTE12 			:= ?;\n" ); // 25
		
		sqlBuilder.append("	i_eam_op_tbl(1).TRANSACTION_TYPE 		:= ?;\n" ); // 26
		sqlBuilder.append("	i_eam_op_tbl(1).WIP_ENTITY_ID 			:= ?;\n" ); // 27
        sqlBuilder.append("	i_eam_op_tbl(1).ORGANIZATION_ID 		:= ?;\n" ); // 28
        sqlBuilder.append("	i_eam_op_tbl(1).OPERATION_SEQ_NUM 		:= 10;\n" );
        sqlBuilder.append("	i_eam_op_tbl(1).DEPARTMENT_ID 			:= ?;\n" ); // 29
        sqlBuilder.append("	i_eam_op_tbl(1).MINIMUM_TRANSFER_QUANTITY       := 0;\n" );
        sqlBuilder.append("	i_eam_op_tbl(1).START_DATE 			:= i_eam_wo_rec.scheduled_start_date;\n" );
        sqlBuilder.append("	i_eam_op_tbl(1).COMPLETION_DATE 		:= i_eam_wo_rec.scheduled_start_date;\n" );
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE1 			:= ?;\n" ); // 30 breakdown date
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE2 			:= ?;\n" ); // 31 call receive date
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE3 			:= ?;\n" ); // 32 equipment condition
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE4 			:= ?;\n" ); // 33 equipment sent
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE5 			:= ?;\n" ); // 34 disinfection
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE7 			:= ?;\n" ); // 35 attendance date
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE8 			:= ?;\n" ); // 36 equip received date
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE9 			:= ?;\n" ); // 37 reinstatement date      
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE10 			:= ?;\n" ); // 38 technical name      
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE11 			:= ?;\n" ); // 39 labor cost      
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE12 			:= ?;\n" ); // 40 spare part desc
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE13 			:= ?;\n" ); // 41 spare part cost
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE14 			:= ?;\n" ); // 42 service report       
        sqlBuilder.append("	i_eam_op_tbl(1).ATTRIBUTE15 			:= ?;\n" ); // 43 result and action
        
        sqlBuilder.append("	i_work_order_ext_rec.HA_CONTACT_EMAIL           := ?;\n" ); // 44
        sqlBuilder.append("	i_work_order_ext_rec.REMARK_FOR_VENDOR          := ?;\n" ); // 45
        sqlBuilder.append("	i_work_order_ext_rec.REMARK_FOR_USER            := ?;\n" ); // 46
        
        sqlBuilder.append("	i_eam_wo_comp_rec.ATTRIBUTE1            := ?;\n" ); // 47 completed_by
        sqlBuilder.append("	i_eam_wo_comp_rec.ATTRIBUTE2            := ?;\n" ); // 48 add labour cost
        sqlBuilder.append("	i_eam_wo_comp_rec.ATTRIBUTE3            := ?;\n" ); // 49 add material cost
        sqlBuilder.append("	i_eam_wo_comp_rec.ATTRIBUTE4            := ?;\n" ); // 50 add material desc
        sqlBuilder.append("	i_eam_wo_comp_rec.ATTRIBUTE5            := ?;\n" ); // 51  vendor ref num
        
        sqlBuilder.append("	i_eam_wo_rec.eam_failure_codes_tbl(1).failure_code          := ?;\n" ); // 52  failure code
        sqlBuilder.append("	i_eam_wo_rec.eam_failure_codes_tbl(1).cause_code            := ?;\n" ); // 53  cause code
        sqlBuilder.append("	i_eam_wo_rec.eam_failure_codes_tbl(1).resolution_code       := ?;\n" ); // 54  resolution code
		sqlBuilder.append("	i_eam_wo_comp_rec.ACTUAL_END_DATE 		:= to_date(?, 'DD/MM/YYYY HH24:MI');\n" ); // 55 completion date
		sqlBuilder.append("	i_eam_wo_comp_rec.ACTUAL_START_DATE 		:= i_eam_wo_comp_rec.ACTUAL_END_DATE;\n" ); // 55 completion date
		if ((WorkOrderConstant.UPDATE).equals(workOrder.getMode())) {
			sqlBuilder.append(" select to_char(job.last_update_date, 'DD/MM/YYYY HH24:MI:SS') into v_chk_update_date from wip_discrete_jobs job, wip_entities wip where job.wip_entity_id = wip.wip_entity_id and wip.wip_entity_name = i_eam_wo_rec.wip_entity_name and wip.organization_id = i_eam_op_tbl(1).ORGANIZATION_ID; \n");
			sqlBuilder.append(" if v_chk_update_date <> '" + workOrder.getlastUpdateDate() + "' then \n");
			sqlBuilder.append(" 	raise_application_error( -20001, 'Record has been updated!' ); else \n");
			sqlBuilder.append(" update wip_discrete_jobs set last_update_date = sysdate where wip_entity_id in (select wip_entity_id from wip_entities where wip_entity_name = i_eam_wo_rec.wip_entity_name and organization_id = i_eam_op_tbl(1).ORGANIZATION_ID); end if; \n");
		}
        sqlBuilder.append("\n" );
        sqlBuilder.append("	XXEAM_WORKORDER_PKG.PROCESS_WO(\n" );
        sqlBuilder.append("		p_bo_identifier           => 'EAM',\n" );
        sqlBuilder.append("		p_api_version_number      => 1.0,\n" );
        sqlBuilder.append("		p_init_msg_list           => TRUE,\n" );
        sqlBuilder.append("		p_commit                  => 'Y',\n" );
        sqlBuilder.append("		p_eam_wo_rec              => i_eam_wo_rec,\n" );
        sqlBuilder.append("		p_eam_op_tbl              => i_eam_op_tbl,\n" );
        sqlBuilder.append("		p_eam_op_network_tbl      => i_eam_op_network_tbl,\n");
        sqlBuilder.append("		p_eam_res_tbl             => i_eam_res_tbl,\n" );
        sqlBuilder.append("		p_eam_res_inst_tbl        => i_eam_res_inst_tbl,\n" );
        sqlBuilder.append("		p_eam_sub_res_tbl         => i_eam_sub_res_tbl,\n" );
        sqlBuilder.append("		p_eam_res_usage_tbl       => i_eam_res_usage_tbl,\n" );
        sqlBuilder.append("		p_eam_mat_req_tbl         => i_eam_mat_req_tbl,\n" );
        sqlBuilder.append("		p_eam_direct_items_tbl    => i_eam_direct_items_tbl,\n" );
        sqlBuilder.append("		p_eam_wo_comp_rec         => i_eam_wo_comp_rec,\n" );
        sqlBuilder.append("		p_eam_wo_quality_tbl      => i_eam_wo_quality_tbl,\n" );
        sqlBuilder.append("		p_eam_meter_reading_tbl   => i_eam_meter_reading_tbl,\n" );
        sqlBuilder.append("		p_eam_counter_prop_tbl    => i_eam_counter_prop_tbl,\n" );
        sqlBuilder.append("		p_eam_wo_comp_rebuild_tbl => i_eam_wo_comp_rebuild_tbl,\n" );
        sqlBuilder.append("		p_eam_wo_comp_mr_read_tbl => i_eam_wo_comp_mr_read_tbl,\n" );
        sqlBuilder.append("		p_eam_op_comp_tbl         => i_eam_op_comp_tbl,\n" );
        sqlBuilder.append("		p_eam_request_tbl         => i_eam_request_tbl,\n" );
        sqlBuilder.append("		p_eam_permit_tbl          => i_eam_permit_tbl,\n" );
        sqlBuilder.append("		p_eam_permit_wo_assoc_tbl => i_eam_permit_wo_assoc_tbl,\n" );
        sqlBuilder.append("		p_work_order_ext_rec      => i_work_order_ext_rec,\n" );
        sqlBuilder.append("		x_wip_entity_id           => ?,\n" ); // 56
        sqlBuilder.append("		x_wip_entity_name           => ?,\n" ); // 57
        sqlBuilder.append("		x_return_status           => ?,\n" ); // 58
        sqlBuilder.append("		x_msg_count               => ?,\n" ); // 59
        sqlBuilder.append("		x_msg_data               => ?,\n" ); // 60
		sqlBuilder.append("  	p_attachment_flag  		=>   ?, \n"); // 61
		sqlBuilder.append("  	p_document_id  			=>   ?, \n"); // 62
		sqlBuilder.append("  	p_work_request_number  	=>   ?); \n"); // 63		        
//        sqlBuilder.append("	);\n" );
        sqlBuilder.append("END;");
		
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
			if((WorkOrderConstant.UPDATE).equals(workOrder.getMode())){
				//if(!"1".equals(workOrder.getWoStatus())) {
					List<AssetInfo> assetInfoList = getWOAssetAttr(workOrder.getWoNumber());
					//String serialNumber = workOrder.getAssetInfo().getLegacySerialNumber();
					//assetInfoList.get(0).setLegacySerialNumber(serialNumber);
					assetInfo = assetInfoList.get(0);
				//} else {
				//	assetInfo = workOrder.getAssetInfo();
				//}
			}
			else {
				assetInfo = workOrder.getAssetInfo();
			}
			
			String attr15 = new StringBuilder(workOrder.getContactPerson()).append(" (").append(workOrder.getContactPhone()).append(") ").toString();
			
			logger.debug("getMode " +workOrder.getMode() );
			logger.debug("getWorkOrderId " +workOrder.getWorkOrderId() );
			logger.debug("getWoNumber " +workOrder.getWoNumber() );
			logger.debug("assetNumber " +workOrder.getAssetNumber() );
			logger.debug("LegancySerailNumber " +assetInfo.getLegacySerialNumber() );
			logger.debug("getAssetGroup " +assetInfo.getAssetGroup() );
			logger.debug("getAssetOrgId " +assetInfo.getAssetOrgId() );
			logger.debug("getMaintenanceObjectType " +assetInfo.getMaintenanceObjectType() );
			logger.debug("getMaintenanceObjectId " +assetInfo.getMaintenanceObjectId() );
			logger.debug("getAssetOwningDepartmentId " +assetInfo.getAssetOwningDepartmentId() );
			logger.debug("getWoStatus " +workOrder.getWoStatus() );
			logger.debug("getWoType " +workOrder.getWoType() );
			logger.debug("Attr15 " + attr15 );
			logger.debug("getWoDescription " +workOrder.getWoDescription() );
			logger.debug("getMaintenanceBody " +workOrder.getMaintenanceBody() );
			logger.debug("getMaintenanceBodyType " +workOrder.getMaintenanceBodyType() );		
			logger.debug("getAutoSendWO " +workOrder.getAutoSendWO() );
			logger.debug("getmContactPerson " +workOrder.getmContactPerson()+"|" );
			logger.debug("getmContactPhone " +workOrder.getmContactPhone() );		
			logger.debug("getmContactFax " +workOrder.getmContactFax() );
			logger.debug("getmContactEmail " +workOrder.getmContactEmail() );
			
			logger.debug("getDisinfectionStatus " +workOrder.getDisinfectionStatus() );
			logger.debug("getBreakdownScheduleDate " + workOrder.getBreakdownScheduleDate());
			logger.debug("getEquipmentSent " +workOrder.getEquipmentSent() );
			logger.debug("getContactEmail " +workOrder.getContactEmail() );
			logger.debug("getUserRemark " +workOrder.getUserRemark() );
			logger.debug("getVendorRemark " +workOrder.getVendorRemark() );
			
			//Repair Values
			logger.debug("getVendorReferenceNo " +workOrder.getVendorReferenceNo() );
			logger.debug("getFailureCauseCode " +workOrder.getFailureCauseCode() );
			logger.debug("getCallRecieved " +workOrder.getCallRecieved() );
			logger.debug("getFailureSymptomCode " +workOrder.getFailureSymptomCode() );
			logger.debug("getEquipmentRecievedDate " +workOrder.getEquipmentRecievedDate() );
			logger.debug("getRepairResoultionCode " +workOrder.getRepairResoultionCode() );
			logger.debug("getAttendanceDate " +workOrder.getAttendanceDate() );
			logger.debug("getEquipmentCondition " +workOrder.getEquipmentCondition() );
			logger.debug("getReinstatementCompletionDate " +workOrder.getReinstatementCompletionDate() );
			logger.debug("getSparePartCost " +workOrder.getSparePartCost() );
			logger.debug("getLaborCost " +workOrder.getLaborCost() );
			logger.debug("getSparePartDesc " +workOrder.getSparePartDesc() );
			logger.debug("getAddMaterialCost " +workOrder.getAddMaterialCost() );
			logger.debug("getAddLaborCost " +workOrder.getAddLaborCost() );		
			logger.debug("getAddMaterialDesc " +workOrder.getAddMaterialDesc() );
			logger.debug("getTechnicalName " +workOrder.getTechnicalName() );
			logger.debug("getWorkOrderCompletedBy " +workOrder.getWorkOrderCompletedBy() );
			logger.debug("getResultAndAction " +workOrder.getResultAndAction() );
			logger.debug("getServiceReport " +workOrder.getServiceReport() );
			logger.debug("getWrNumber " +workOrder.getWrNumber() );
			logger.debug("getCompletionDate " +workOrder.getCompletionDate() );
			
/*	
			logger.debug("getContactPhone " +workRequest.getContactPhone() );			
			logger.debug("getContractNumber " +assetInfo.getContractNumber() );
			logger.debug("getMaintenancePlan " +assetInfo.getMaintenancePlan() );
			logger.debug("getMaintenanceJoinDate " +assetInfo.getMaintenanceJoinDate() );
			
			logger.debug("getMaintenanceExpiryDate " +assetInfo.getMaintenanceExpiryDate() );
			logger.debug("getSupplierAgreementNumber " +assetInfo.getSupplierAgreementNumber() );		
			logger.debug("getContactPerson " +workRequest.getContactPerson() );
			logger.debug("getRequestedFor " +workRequest.getRequestedFor() );			

*/                

	        if ((WorkOrderConstant.CREATE).equals(workOrder.getMode())) {
                transactionCode = Long.parseLong(WorkOrderConstant.G_OPR_CREATE); //G_OPR_CREATE = 1, G_OPR_UPDATE = 2
            } else {
                transactionCode = Long.parseLong(WorkOrderConstant.G_OPR_UPDATE); //G_OPR_CREATE = 1, G_OPR_UPDATE = 2
            }
            cs.setLong(1, transactionCode); 
            
	        if ((WorkOrderConstant.CREATE).equals(workOrder.getMode())) {
	            cs.setNull(2, Types.VARCHAR); //WIP_ENTITY_NAME: null for create mode
	            cs.setNull(3, Types.NUMERIC); //WIP_ENTITY_ID: null for create mode
	        } else if((WorkOrderConstant.UPDATE).equals(workOrder.getMode())){
	            cs.setString(2, workOrder.getWoNumber() );
	        	cs.setLong(3, workOrder.getWorkOrderId() );
	        } else {
	        	// For un-defined getMode, treate as create
	            cs.setNull(2, Types.VARCHAR); //WIP_ENTITY_NAME: null for create mode
	            cs.setNull(3, Types.NUMERIC); //WIP_ENTITY_ID: null for create mode
	        }

        	cs.setLong(4, assetInfo.getAssetOrgId());
            cs.setString(6, assetInfo.getLegacySerialNumber());
            cs.setLong(7, assetInfo.getMaintenanceObjectId());
            cs.setLong(28, assetInfo.getAssetOrgId());
            
	        if ((WorkOrderConstant.CREATE).equals(workOrder.getMode())) {
	        	cs.setLong(5, assetInfo.getAssetGroup());
    	        cs.setLong(20, assetInfo.getAssetOwningDepartmentId());
    	        cs.setLong(29, assetInfo.getAssetOwningDepartmentId());
	        }
	        else {
				logger.debug("getWoStatus " +workOrder.getWoStatus() );
				logger.debug("getWoType " +workOrder.getWoType() );
				if(workOrder.getAssetGroupId()!=null) {
	        		cs.setLong(5, workOrder.getAssetGroupId());
				}
				else {
		        	cs.setLong(5, assetInfo.getAssetGroup());
				}
    	        cs.setLong(20, workOrder.getOwningDepartmentId());
    	        cs.setLong(29, workOrder.getOwningDepartmentId());
	        }
	        
	        cs.setLong(8, (workOrder.getWoStatus()!=null && !"".equals(workOrder.getWoStatus()))?Long.parseLong(workOrder.getWoStatus()):woStatus );

	        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	        cs.setString(9, workOrder.getBreakdownScheduleDate());
	        cs.setLong(10, Long.parseLong(workOrder.getWoType()));
            cs.setString(11, attr15);
            cs.setString(12, workOrder.getWoDescription());
            cs.setString(13, workOrder.getMaintenanceBody());
            cs.setString(14, workOrder.getMaintenanceBodyType());
            cs.setString(15, workOrder.getAutoSendWO());
            cs.setString(16, workOrder.getmContactPerson());
            cs.setString(17, workOrder.getmContactPhone());
            cs.setString(18, workOrder.getmContactFax());
            cs.setString(19, workOrder.getmContactEmail());
//	        cs.setLong(20, assetInfo.getAssetOwningDepartmentId());

            if(autoValue){
                cs.setString(21, assetInfo.getContractNumber());
                cs.setString(22, assetInfo.getMaintenancePlan());
    	    	String newJoinDate =dateUtil.formatDateToStr( dateUtil.parseStrToDate(assetInfo.getMaintenanceJoinDate(), "dd/MM/yyyy")  , "yyyy/MM/dd HH:mm:ss" )     ;
    	    	String newExpiryDate =dateUtil.formatDateToStr( dateUtil.parseStrToDate(assetInfo.getMaintenanceExpiryDate(), "dd/MM/yyyy")  , "yyyy/MM/dd HH:mm:ss" )     ;
    	    	logger.debug("newJoinDate " + newJoinDate);
    	        logger.debug("newExpiryDate " + newExpiryDate);
    	        cs.setString(23, newJoinDate);
    	        cs.setString(24, newExpiryDate);
    	        cs.setString(25, assetInfo.getSupplierAgreementNumber());
    			logger.debug("Not Set Maintenance Body Blank");
            }
            else {
            	cs.setNull(21, Types.VARCHAR  );
            	cs.setNull(22, Types.VARCHAR  );
            	cs.setNull(23, Types.VARCHAR  );
            	cs.setNull(24, Types.VARCHAR  );
            	cs.setNull(25, Types.VARCHAR );
    			logger.debug("Set Maintenance Body Blank");
            }
            cs.setLong(26, transactionCode); 
	        if ((WorkOrderConstant.CREATE).equals(workOrder.getMode())) {
	        	cs.setNull(27, Types.NUMERIC); //WIP_ENTITY_ID: null for create mode
	        } else if((WorkOrderConstant.UPDATE).equals(workOrder.getMode())){
	        	cs.setLong(27, workOrder.getWorkOrderId() );
	        } else {
	        	// for un-defined getMode, treat as create
	        	cs.setNull(27, Types.NUMERIC); //WIP_ENTITY_ID: null for create mode
	        }
	        
	        if (!"".equals(workOrder.getBreakdownScheduleDate()) && workOrder.getBreakdownScheduleDate()!=null) {
	        	cs.setString(30, formatter2.format(formatter.parse(workOrder.getBreakdownScheduleDate())).toString());
	        }else {
	        	cs.setNull(30, Types.VARCHAR);
	        }
            if (!"".equals(workOrder.getCallRecieved()) && workOrder.getCallRecieved()!=null) {
            	//cs.setString(31, formatter2.format(formatter.parse(workOrder.getCallRecieved())).toString());
				cs.setString(31, dateUtil.formatDateToStr(dateUtil.parseStrToDate(workOrder.getCallRecieved(), "dd/MM/yyyy HH:mm")  , "yyyy/MM/dd HH:mm:ss" ));
            }else {
            	cs.setNull(31, Types.VARCHAR);
            }
            cs.setString(32, workOrder.getEquipmentCondition());
            cs.setString(33, workOrder.getEquipmentSent());
            cs.setString(34, workOrder.getDisinfectionStatus());            
            if (!"".equals(workOrder.getAttendanceDate()) && workOrder.getAttendanceDate()!=null) {
            	//cs.setString(35, formatter2.format(formatter.parse(workOrder.getAttendanceDate())).toString());
				cs.setString(35, dateUtil.formatDateToStr(dateUtil.parseStrToDate(workOrder.getAttendanceDate(), "dd/MM/yyyy HH:mm")  , "yyyy/MM/dd HH:mm:ss" ));
            }else {
            	cs.setNull(35, Types.VARCHAR);
            }
//	        logger.debug("Attendance Date to DB " + formatter2.format(formatter.parse(workOrder.getAttendanceDate())).toString());            
            if (!"".equals(workOrder.getEquipmentRecievedDate()) && workOrder.getEquipmentRecievedDate()!=null) {
            	//cs.setString(36, formatter2.format(formatter.parse(workOrder.getEquipmentRecievedDate())).toString());
				cs.setString(36, dateUtil.formatDateToStr(dateUtil.parseStrToDate(workOrder.getEquipmentRecievedDate(), "dd/MM/yyyy HH:mm")  , "yyyy/MM/dd HH:mm:ss" ));
            }else {
            	cs.setNull(36, Types.VARCHAR);
            }            
            if (!"".equals(workOrder.getReinstatementCompletionDate()) && workOrder.getReinstatementCompletionDate()!=null) {
            	//cs.setString(37, formatter2.format(formatter.parse(workOrder.getReinstatementCompletionDate())).toString());
				cs.setString(37, dateUtil.formatDateToStr(dateUtil.parseStrToDate(workOrder.getReinstatementCompletionDate(), "dd/MM/yyyy HH:mm")  , "yyyy/MM/dd HH:mm:ss" ));
            }else {
            	cs.setNull(37, Types.VARCHAR);
            }
            cs.setString(38, workOrder.getTechnicalName());            
            cs.setString(39, workOrder.getLaborCost()); 
            cs.setString(40, workOrder.getSparePartDesc());
            cs.setString(41, workOrder.getSparePartCost());
            cs.setString(42, workOrder.getServiceReport());
            cs.setString(43, workOrder.getResultAndAction());
            
            cs.setString(44, workOrder.getContactEmail());
            cs.setString(45, workOrder.getVendorRemark());
            cs.setString(46, workOrder.getUserRemark());
            
            cs.setString(47, workOrder.getWorkOrderCompletedBy());
            cs.setString(48, workOrder.getAddLaborCost());
            cs.setString(49, workOrder.getAddMaterialCost());
            cs.setString(50, workOrder.getAddMaterialDesc());
            cs.setString(51, workOrder.getVendorReferenceNo());
           
            cs.setString(52, workOrder.getFailureSymptomCode());
            cs.setString(53, workOrder.getFailureCauseCode());
            cs.setString(54, workOrder.getRepairResoultionCode());
            
	        cs.setString(55, workOrder.getCompletionDate());
            
            cs.registerOutParameter(56, Types.NUMERIC); // wo id          
            cs.registerOutParameter(57, Types.VARCHAR); // wo number     
            cs.registerOutParameter(58, Types.VARCHAR); // status
            cs.registerOutParameter(59, Types.NUMERIC); // msg cnt
            cs.registerOutParameter(60, Types.VARCHAR); // msg
	        cs.setString(61, workOrder.getAttachmentMode());
	        if(workOrder.getDocId() != null && !"".equals(workOrder.getDocId()) ){
	        	//cs.setLong(62, Long.parseLong(workOrder.getDocId()));
	        	cs.setString(62, workOrder.getDocId());
	        } else {
	        	 cs.setNull(62, Types.NUMERIC); 
	        }
	        cs.setString(63, workOrder.getWrNumber());
	        logger.debug("SQL String " + sqlBuilder.toString()) ;  
	        
            cs.execute();
	        logger.debug("out wo number " +cs.getString(57) );  
	        logger.debug("out return status " +cs.getString(58) );  
	        logger.debug("out msg " +cs.getString(60) );  
	        
            if("S".equals(cs.getString(58))){
	       		result = Long.toString(cs.getLong(56)) + "&woNumber=" + cs.getString(57);
	        	logger.info("result=" + result);
            }
            else{
            	result = cs.getString(60);
            	if ((WorkOrderConstant.UPDATE).equals(workOrder.getMode())) {
            		sql = "update wip_discrete_jobs set last_update_date = to_date('"+workOrder.getlastUpdateDate()+"','DD/MM/YYYY HH24:MI:SS') where wip_entity_id in (select wip_entity_id from wip_entities where wip_entity_name = '"+workOrder.getWoNumber()+"' and organization_id = "+assetInfo.getAssetOrgId()+")";
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
					//result = "<strong>Error! This record has been updated by another user, please cancel and requery the record.</strong> ";
					result = "This record has been updated by another user, please cancel and requery the record. ";
				}
			}else {
				result = "<strong>Error! </strong> " + se.getMessage();
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
             
			result = "<strong>Error! </strong> " + e.getMessage() + sw.toString();
			sw.close();
		} finally {
			if (cs != null) {
				try {
					cs.close();
					cs = null;
				} catch (Exception e) {}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
					pstmt = null;
				} catch (Exception e) {}
			}
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} catch (Exception e) {}
			}
		}
//		result = Long.toString(663434) + "&woNumber=" + "203027" ;  
		return result;
			
    }
    
    @Override
    @Transactional(rollbackFor=Exception.class)	
    public List<WorkOrder> searchWorkOrderDetail(SearchWorkOrder searchCriteria) throws Exception {
    	String woNumber = searchCriteria.getWoNumber();
    	String orgID = searchCriteria.getEamOrg();

        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select woId, woNumber, woStatusId, woStatus, user_wostatus, asset_number, item_code_number, asset_description, assetGroupId, assetGroup, asset_status, dob, serial_number, asset_location_code, asset_location_desc, asset_owner, asset_owner_desc, asset_owning_department, manufacturer, brand, model, purchase_price, asset_supplier, risk_level, FATHER_ASSET_NUMBER, woTypeId, woType, breakdownScheduleDate, equipSentToWorkshop, disinfectionStatus, haContactPerson, haContactPhone, haContactEmail, wrNumber, outboundDateTime, description, remarkForVendor, remarkForUser, maintenance_body, maintenance_body_type, contract_num, auto_send_wo_to_supplier, supplier_agreement_number, maintenance_plan, maintenance_join_date, maintenance_expiry_date, maintenance_contact_person, maintenance_contact_phone, maintenance_contact_fax_number, maintenance_contact_email, maintenance_interval, createdById, createdBy, creationDate, asset_organization_code, OWNING_DEPARTMENT_ID,owningDept, owningdeptdesc, vendorReferenceNumber, failureCauseCode, failureSymptonCode, failureResolCode, callReceivedDateTime, equipCondition, attendanceDateTime, equipReceivedDateTime, reinstatementDateTime, actual_end_date, technicalName, laborCost, sparePartDesc, sparePartCost, servicReportReference, resultAndAction, woCompletedBy, addLaborCost, addMaterialCost, addMaterialDesc, last_update_date, ORGANIZATION_ID, SCHEDULED_END_DATE, SCHEDULED_START_DATE, ORGANIZATIONCODE,  \n");
		sqlBuffer.append("(select vendor_name from xxeam_maintenance_body_dff_v where vendor_number = VO.maintenance_body) as maintenance_body_name, \n");
		sqlBuffer.append("(select vendor_name from XXEAM_ASSET_SUPPLIER_DFF_V where vendor_number = VO.asset_supplier) as asset_supplier_name \n");
        sqlBuffer.append("from XXEAM_EXT_WORK_ORDERS_V VO WHERE \n");
        //sqlBuffer.append("VO.WONUMBER LIKE :woNumber ");
        sqlBuffer.append("VO.WONUMBER = :woNumber ");
        
        //paramMap.addValue("woNumber", woNumber + "%");
        paramMap.addValue("woNumber", woNumber);

        logger.info("SearchWO SQL:"+sqlBuffer.toString());        
        logger.debug("SearchWO orgID:"+orgID);

        List<WorkOrder> listWorkOrder = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<WorkOrder>() {
            @Override
            public WorkOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
            	WorkOrder aWorkOrder = new WorkOrder();
            	Date aDate = new Date();
            	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            	aWorkOrder.setWorkOrderId(rs.getInt("woId"));            	     
            	aWorkOrder.setWorkOrderNumber(rs.getString("woNumber"));
            	aWorkOrder.setAssetNumber(rs.getString("asset_number"));
            	aWorkOrder.setAssetDescription(rs.getString("asset_description"));
            	aWorkOrder.setWoType(rs.getString("woType"));
            	aWorkOrder.setWoStatus(rs.getString("user_wostatus"));
				if(rs.getString("breakdownScheduleDate") != null) {
            		aDate = rs.getTimestamp("breakdownScheduleDate");
                	aWorkOrder.setBreakdownScheduleDate(sdf.format(aDate));
            	}	
				aWorkOrder.setEquipmentSent(rs.getString("equipSentToWorkshop"));
				aWorkOrder.setWoDescription(rs.getString("description"));
				aWorkOrder.setUserRemark(rs.getString("remarkForUser"));
				aWorkOrder.setVendorRemark(rs.getString("remarkForVendor"));
				aWorkOrder.setHaContactPerson(rs.getString("haContactPerson"));
				aWorkOrder.setHaContactPhone(rs.getString("haContactPhone"));
				aWorkOrder.setHaContactEmail(rs.getString("haContactEmail"));
				aWorkOrder.setDisinfectionStatus(rs.getString("disinfectionStatus"));
				
				aWorkOrder.setMaintenanceBodyType(rs.getString("maintenance_body_type"));
				aWorkOrder.setAutoSendWO(rs.getString("auto_send_wo_to_supplier"));
				aWorkOrder.setmContactPerson(rs.getString("maintenance_contact_person"));
				aWorkOrder.setmContactPhone(rs.getString("maintenance_contact_phone"));
				aWorkOrder.setmContactFax(rs.getString("maintenance_contact_fax_number"));
				aWorkOrder.setmContactEmail(rs.getNString("maintenance_contact_email"));
            	aWorkOrder.setCreatedBy(rs.getString("createdBy"));
            	if (rs.getString("maintenance_body_name")!=null) {
            		aWorkOrder.setMaintenanceBody(rs.getString("maintenance_body_name") + " (" + rs.getString("maintenance_body") + ")");
            	}
            	aWorkOrder.setMaintenanceBodyNum(rs.getString("maintenance_body"));
            	aWorkOrder.setOwningDepartment(rs.getString("owningDept"));
            	aWorkOrder.setOwningDepartmentId(rs.getLong("OWNING_DEPARTMENT_ID"));
            	aWorkOrder.setMaintenanceContract(rs.getString("contract_num"));
            	aWorkOrder.setAssetLocation(rs.getString("asset_location_code"));
            	aWorkOrder.setAssetOwner(rs.getString("asset_owner"));
            	aWorkOrder.setAssetRisk(rs.getNString("risk_level"));
            	aDate = rs.getTimestamp("creationDate");
            	aWorkOrder.setCreationDate(aDate);
            	aWorkOrder.setAssetGroupId(rs.getLong("assetGroupId"));
            	aWorkOrder.setAssetGroup(rs.getString("assetGroup"));
            	aWorkOrder.setAssetSerialNumber(rs.getString("serial_number"));
            	aWorkOrder.setManufacturer(rs.getString("manufacturer"));
            	aWorkOrder.setBrand(rs.getString("brand"));
            	aWorkOrder.setModel(rs.getString("model"));
            	
            	aWorkOrder.setSupplierAgreementNumber(rs.getString("supplier_agreement_number"));
            	aWorkOrder.setmPlan(rs.getString("maintenance_plan"));
            	if (rs.getString("maintenance_join_date")!=null) {
            		//aWorkOrder.setmJoinDate(rs.getString("maintenance_join_date"));
            		//aWorkOrder.setmJoinDate(dateUtil.formatDateToStr(dateUtil.parseStrToDate(rs.getString("maintenance_join_date"),"yyyy/MM/dd HH:mm:ss"),"dd/MM/yyyy HH:mm"));
            		aWorkOrder.setmJoinDate(dateUtil.formatDateToStr(rs.getTimestamp("maintenance_join_date"),"dd/MM/yyyy HH:mm"));
            	}
            	if (rs.getString("maintenance_expiry_date")!=null) {
            		//aWorkOrder.setmExpiryDate(rs.getString("maintenance_expiry_date"));
            		//aWorkOrder.setmExpiryDate(dateUtil.formatDateToStr(dateUtil.parseStrToDate(rs.getString("maintenance_expiry_date"),"yyyy/MM/dd HH:mm:ss"),"dd/MM/yyyy HH:mm"));
            		aWorkOrder.setmExpiryDate(dateUtil.formatDateToStr(rs.getTimestamp("maintenance_expiry_date"),"dd/MM/yyyy HH:mm"));
            	}
            	aWorkOrder.setmInterval(rs.getString("maintenance_interval"));
            	if (rs.getString("outboundDateTime")!=null) {
            		//aWorkOrder.setOutboundDate(dateUtil.formatDateToStr(dateUtil.parseStrToDate(rs.getString("outboundDateTime"),"yyyy/MM/dd HH:mm:ss"),"dd/MM/yyyy HH:mm"));
            		aWorkOrder.setOutboundDate(dateUtil.formatDateToStr(rs.getTimestamp("outboundDateTime"), "dd/MM/yyyy HH:mm"));
            	}
            	aWorkOrder.setWrNumber(rs.getString("wrnumber"));
            	aWorkOrder.setWoStatusId(rs.getString("wostatusid"));
            	aWorkOrder.setWoTypeId(rs.getString("wotypeid"));
            	aWorkOrder.setItemCodeNumber(rs.getString("item_code_number"));
            	aWorkOrder.setAssetStatus(rs.getString("asset_status"));
            	//aWorkOrder.setDob(rs.getString("dob"));
            	aWorkOrder.setDob(dateUtil.formatDateToStr(rs.getTimestamp("dob"), "dd/MM/yyyy"));
            	aWorkOrder.setAssetLocationDesc(rs.getString("asset_location_desc"));
            	aWorkOrder.setAssetOwnerDesc(rs.getString("asset_owner_desc"));
            	aWorkOrder.setAssetOwningDepartmentDesc(rs.getString("owningdeptdesc"));
            	aWorkOrder.setPurchasePrice(rs.getString("purchase_price"));
            	aWorkOrder.setAssetSupplier(rs.getString("asset_supplier_name")+ " ("+rs.getString("asset_supplier")+")");
            	aWorkOrder.setFatherAssetNumber(rs.getString("father_asset_number"));
            	aWorkOrder.setAssetOrg(rs.getString("asset_organization_code"));
            	aWorkOrder.setEamOrg(String.valueOf(rs.getInt("ORGANIZATION_ID")));
                logger.debug("Retrieved Work Order Number " + aWorkOrder.getWorkOrderNumber());
                
                aWorkOrder.setDisinfectionStatus(rs.getString("disinfectionStatus"));
            	aWorkOrder.setEquipmentSent(rs.getString("equipSentToWorkshop"));
            	aWorkOrder.setHaContactPerson(rs.getString("haContactPerson"));
            	aWorkOrder.setHaContactPhone(rs.getString("haContactPhone"));
            	aWorkOrder.setHaContactEmail(rs.getString("haContactEmail"));
            	//Get repair info
            	aWorkOrder.setVendorReferenceNo(rs.getString("vendorReferenceNumber"));
            	if(rs.getString("callReceivedDateTime") != null) {
            		//aWorkOrder.setCallRecieved(rs.getString("callReceivedDateTime"));
            		aWorkOrder.setCallRecieved(dateUtil.formatDateToStr(rs.getTimestamp("callReceivedDateTime"), "dd/MM/yyyy HH:mm"));
            	}
            	if(rs.getString("equipReceivedDateTime") != null) {
            		//aWorkOrder.setEquipmentRecievedDate(rs.getString("equipReceivedDateTime"));
            		aWorkOrder.setEquipmentRecievedDate(dateUtil.formatDateToStr(rs.getTimestamp("equipReceivedDateTime"), "dd/MM/yyyy HH:mm"));
            	}
            	if(rs.getString("attendanceDateTime") != null) {
            		//aWorkOrder.setAttendanceDate(rs.getString("attendanceDateTime"));
            		aWorkOrder.setAttendanceDate(dateUtil.formatDateToStr(rs.getTimestamp("attendanceDateTime"), "dd/MM/yyyy HH:mm"));
            	}
            	if(rs.getString("reinstatementDateTime") != null) {
            		//aWorkOrder.setReinstatementCompletionDate(rs.getString("reinstatementDateTime"));
            		aWorkOrder.setReinstatementCompletionDate(dateUtil.formatDateToStr(rs.getTimestamp("reinstatementDateTime"), "dd/MM/yyyy HH:mm"));
            	}
				if(rs.getString("actual_end_date") != null) {
            		aDate = rs.getTimestamp("actual_end_date");
                	aWorkOrder.setCompletionDate(sdf.format(aDate));
            	}	
            	aWorkOrder.setSparePartCost(rs.getString("sparePartCost"));
            	aWorkOrder.setSparePartDesc(rs.getString("sparePartDesc"));
            	aWorkOrder.setAddMaterialCost(rs.getString("addMaterialCost"));
            	aWorkOrder.setAddMaterialDesc(rs.getString("addMaterialDesc"));
            	aWorkOrder.setTechnicalName(rs.getString("technicalName"));
            	aWorkOrder.setResultAndAction(rs.getString("resultAndAction"));
            	aWorkOrder.setServiceReport(rs.getString("servicReportReference"));
            	aWorkOrder.setFailureCauseCode(rs.getString("failureCauseCode"));
            	aWorkOrder.setFailureSymptomCode(rs.getString("failureSymptonCode"));
            	aWorkOrder.setRepairResoultionCode(rs.getString("failureResolCode"));
            	aWorkOrder.setEquipmentCondition(rs.getString("equipCondition"));
            	aWorkOrder.setLaborCost(rs.getString("laborCost"));
            	aWorkOrder.setAddLaborCost(rs.getString("addLaborCost"));
            	aWorkOrder.setWorkOrderCompletedBy(rs.getString("woCompletedBy"));
            	aWorkOrder.setlastUpdateDate(dateUtil.formatDateToStr(rs.getTimestamp("last_update_date"), "dd/MM/yyyy HH:mm:ss"));
            	aWorkOrder.setScheduleEndDate(dateUtil.formatDateToStr(rs.getTimestamp("SCHEDULED_END_DATE"), "dd/MM/yyyy HH:mm"));
            	aWorkOrder.setScheduleStartDate(dateUtil.formatDateToStr(rs.getTimestamp("SCHEDULED_START_DATE"), "dd/MM/yyyy HH:mm"));
            	aWorkOrder.setWorkOrderOrg(rs.getString("ORGANIZATIONCODE"));
            	
                return aWorkOrder;
            }
     
        });
     
        return listWorkOrder;
    }
    
	@Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<AttachmentInfo> getAttachmentInfo(String orgID, String woId) throws SQLException, Exception {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        logger.info("***woId="+woId);
        sqlBuffer.append(" SELECT * FROM XXEAM_EXT_WORK_ORD_ATT_V");
        sqlBuffer.append(" WHERE ORG_ID = :orgID ");
        sqlBuffer.append(" AND WO_ID = :woId ");
        
        paramMap.addValue("orgID", orgID);
        paramMap.addValue("woId", woId);
        
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
	
	public String resendWorkOrder(WorkOrder workOrder) throws Exception {

		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("BEGIN \n");
        sqlBuilder.append("	XXEAM_EMAIL_FAX_INS_CTL(\n" );
        sqlBuilder.append("		P_IN_WO_ID           	=> ?,\n" );
        sqlBuilder.append("		P_IN_WO_NUM           	=> ?,\n" );
        sqlBuilder.append("		P_IN_CONTACT_METHOD  	=> ?,\n" );
        sqlBuilder.append("		P_IN_FAX_NUM           	=> ?,\n" );
        sqlBuilder.append("		P_IN_EMAIL           	=> ?,\n" );
        sqlBuilder.append("		P_FLAG           		=> ?);\n" );
        sqlBuilder.append("END;");
		
        String sql = sqlBuilder.toString();

		Connection connection = null;
		CallableStatement cs = null;
        String result = "";
        
		try {
			connection = jdbcTemplate.getDataSource().getConnection();
			
			cs = connection.prepareCall(sql);
			
            cs.setLong(1, workOrder.getWorkOrderId());
            cs.setString(2, workOrder.getWoNumber());
            cs.setString(3, workOrder.getAutoSendWO());
            cs.setString(4, workOrder.getmContactFax());
            cs.setString(5, workOrder.getmContactEmail());
            cs.registerOutParameter(6, Types.VARCHAR); // P_Flag
            
            cs.execute();
            
	        logger.debug("out p_flag " +cs.getString(6) );
	        
	        if("Y".equals(cs.getString(6)) || "X".equals(cs.getString(6))){
	        	result = messageSource.getMessage("HA_INFO_WO_RESENDSUCCESS", null, Locale.US);
	        } else {
	        	result = messageSource.getMessage("HA_ERROR_WO_RESENTFAIL", null, Locale.US);
	        }
		} catch (SQLException se) {
			result = "<strong>Error! </strong> " + se.getMessage();  
		} catch (Exception e) {
			result = "<strong>Error! </strong> " + e.getMessage();
		} finally {
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
		logger.debug("Result: " + result);
		return result;
    }
	
	public List<MaintenanceInfo> getMaintenanceInfo(String assetNumber, String maintenanceNumber, boolean hvMaintNum) throws Exception {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" SELECT * FROM XXEAM_EXT_MAINT_ASSET_INFO");
        sqlBuffer.append(" WHERE ASSET_NUMBER = :assetNumber");
        if(hvMaintNum) {
        	sqlBuffer.append(" AND MAINTENANCE_BODY_NUM = :maintenanceNumber");
        }
        
        paramMap.addValue("assetNumber", assetNumber);
        if(hvMaintNum) {
        	paramMap.addValue("maintenanceNumber", maintenanceNumber);
        }
        
        List<MaintenanceInfo> listMaintenanceInfo = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<MaintenanceInfo>() {
            @Override
            public MaintenanceInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            	MaintenanceInfo aMaintenanceInfo = new MaintenanceInfo();

            	aMaintenanceInfo.setMaintenanceBody(rs.getString("MAINTENANCE_BODY"));
            	aMaintenanceInfo.setMaintenanceBodyNum(rs.getString("MAINTENANCE_BODY_NUM"));
            	aMaintenanceInfo.setMaintenanceBodyType(rs.getString("MAINTENANCE_BODY_TYPE"));
            	aMaintenanceInfo.setContractNumber(rs.getString("MAINTENANCE_CONTRACT_NUM"));
            	aMaintenanceInfo.setMaintContact(rs.getString("MAINTENANCE_CONTACT_PERSON"));
            	aMaintenanceInfo.setMaintPhone(rs.getString("MAINTENANCE_CONTACT_PHONE"));
            	aMaintenanceInfo.setMaintFax(rs.getString("MAINTENANCE_CONTACT_FAX_NUMBER"));
            	aMaintenanceInfo.setMaintEmail(rs.getString("MAINTENANCE_CONTACT_EMAIL"));
            	aMaintenanceInfo.setAutoSend(rs.getString("AUTO_SEND_WO_TO_SUPPLIER"));
            	aMaintenanceInfo.setMaintenancePlan(rs.getString("MAINTENANCE_PLAN"));
            	aMaintenanceInfo.setMaintenanceJoinDate(rs.getString("MAINTENANCE_JOIN_DATE"));
            	aMaintenanceInfo.setMaintenanceExpiryDate(rs.getString("MAINTENANCE_EXPIRY_DATE"));
            	aMaintenanceInfo.setSupplierAgreementNumber(rs.getString("SUPPLIER_AGREEMENT_NUMBER"));
            	aMaintenanceInfo.setMaintenanceInterval(rs.getString("MAINTENANCE_INTERVAL"));
            	
                return aMaintenanceInfo;
            }
        });
        return listMaintenanceInfo;
	}
	
	@Override
    public Boolean gs1VendorCheck(String maintenanceNumber) {
    	StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT 1 ")
        .append("FROM FND_LOOKUP_VALUES FLV ").append("WHERE ")
        .append("flv.lookup_type = 'XXEAM_VENDOR_SUPPORT_INFO' ")
        .append("AND flv.enabled_flag = 'Y' ")
		.append("AND TRUNC(SYSDATE) BETWEEN NVL(FLV.START_DATE_ACTIVE, TRUNC(SYSDATE)) AND NVL(FLV.END_DATE_ACTIVE, TRUNC(SYSDATE)) ")
		.append("AND flv.LOOKUP_CODE = :maintenanceNumber");

        String sql = sqlBuilder.toString();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("maintenanceNumber", maintenanceNumber);
        
        String result = namedParameterJdbcTemplate.query(sql, paramMap,
        		new ResultSetExtractor<String>() {
                public String extractData(ResultSet rs)
                                throws SQLException, DataAccessException {
                		int rowCount = 0;
                		if (rs != null) {
                			while (rs.next()) {
                				rowCount++;
                			}
												 
                			if (rowCount > 0) {
            					return "exists";
                			}
                			
                			return "does not exist";
                		}
                		return "does not exist";
                	}
        });
        
        if(result == "exists") {
        	return true;
        }
        else {
        	return false;
        }
    }
	
	 @Override
	   	@Transactional(readOnly = true, rollbackFor = Exception.class)
	       public List<AssetInfo> getWOAssetAttr(String woNumber) throws SQLException, Exception {
	           MapSqlParameterSource paramMap = new MapSqlParameterSource();
	           
	           StringBuffer sqlBuffer = new StringBuffer();

	           sqlBuffer.append("SELECT ORGANIZATION_ID, "
	           		+ " ASSETGROUPID,"
	           		+ " MAINTENANCE_OBJECT_ID,"
	           		+ " ASSET_OWNING_DEPARTMENT_ID,"
	           		+ " CONTRACT_NUM,"
	           		+ " MAINTENANCE_PLAN,"
	           		+ " MAINTENANCE_JOIN_DATE,"
	           		+ " MAINTENANCE_EXPIRY_DATE,"
	           		+ " SUPPLIER_AGREEMENT_NUMBER,"
	           		+ " LEGACY_SERIAL_NUMBER"
	           		+ " FROM XXEAM_EXT_WORK_ORDERS_V");
	           sqlBuffer.append(" WHERE WONUMBER = :woNumber \n");
	           paramMap.addValue("woNumber", woNumber);
	                      
	           List<AssetInfo> listAssetInfo = namedParameterJdbcTemplate.query(sqlBuffer.toString(), paramMap , new RowMapper<AssetInfo>() {
	               @Override
	               public AssetInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		               	AssetInfo aAssetInfo = new AssetInfo();
		        
		               	aAssetInfo.setAssetGroup(rs.getLong("ASSETGROUPID"));
		               	aAssetInfo.setAssetOrgId(rs.getLong("ORGANIZATION_ID"));
		               	aAssetInfo.setMaintenanceObjectId(rs.getLong("MAINTENANCE_OBJECT_ID"));
		               	aAssetInfo.setAssetOwningDepartmentId(rs.getLong("ASSET_OWNING_DEPARTMENT_ID"));
		               	aAssetInfo.setContractNumber(rs.getString("CONTRACT_NUM"));
		               	aAssetInfo.setMaintenancePlan(rs.getString("MAINTENANCE_PLAN"));
						aAssetInfo.setMaintenanceJoinDate(rs.getString("MAINTENANCE_JOIN_DATE"));
						aAssetInfo.setMaintenanceExpiryDate(rs.getString("MAINTENANCE_EXPIRY_DATE"));
						aAssetInfo.setSupplierAgreementNumber(rs.getString("SUPPLIER_AGREEMENT_NUMBER"));
						aAssetInfo.setLegacySerialNumber(rs.getString("LEGACY_SERIAL_NUMBER"));
						
		                return aAssetInfo;
	               }  
	           });
	           
			return listAssetInfo;
	       }
		
	 @Override
	 public void updateWorkOrderInfo(String query,ArrayList<String> param) throws Exception {
		 Connection connection = null;
		 PreparedStatement pstmt = null;
		 int noOfParam=1;

		 connection = jdbcTemplate.getDataSource().getConnection();
		 try {
			 pstmt = connection.prepareStatement(query);

			 for(String tmp:param) {
				 pstmt.setNString(noOfParam,tmp);
				 noOfParam++;
			 }

			 pstmt.execute();
			 connection.commit();

		 }catch (Exception e) {
			 logger.info(e.getMessage());
		 } finally {
			 if(pstmt != null) {
				 try {
					 pstmt.close();
					 pstmt = null;
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
	
	@Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
    public String chkResendRecord(String WONumber) throws SQLException, Exception {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        int cnt = 0;

        sqlBuffer.append("select count(1) \n");
        sqlBuffer.append("from xxeam_wo_notification_ctl \n");
        sqlBuffer.append("where wip_entity_name = :WONumber \n");
        sqlBuffer.append("and notification_status is null \n");
        
        paramMap.addValue("WONumber", WONumber);
        
        try {
        	cnt = namedParameterJdbcTemplate.queryForObject(sqlBuffer.toString(), paramMap , int.class);
        	logger.debug("chkResendRecord="+cnt);
        	if (cnt>0){
            	return "Y";
            }else {
            	return "N";
            }
        }catch(DataAccessException se) {
        	logger.debug("ERROR chkResendRecord: "+se.toString());
        	return "N";
        }
    }
	
	@Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
    public String chkAssetEnabled(String assetNumber, String orgCode) throws SQLException, Exception {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        int cnt = 0;

        sqlBuffer.append("select count(1) \n");
        sqlBuffer.append("from csi_item_instances cii, mtl_parameters mp, MTL_EAM_ASSET_ATTR_VALUES ASSET_ATTR \n");
        sqlBuffer.append("where mp.organization_id  = cii.last_vld_organization_id \n");
        sqlBuffer.append("and cii.instance_id = ASSET_ATTR.MAINTENANCE_OBJECT_ID(+) \n");
        sqlBuffer.append("and asset_attr.ATTRIBUTE_CATEGORY(+) = 'Asset Details' \n");
        sqlBuffer.append("and NVL(SUBSTR(ASSET_ATTR.C_ATTRIBUTE2,1,8),'Active') <> 'Inactive' \n");
        sqlBuffer.append("and ASSET_ATTR.C_ATTRIBUTE2 <> 'Interim-Transfer In Progress' \n");
        sqlBuffer.append("and cii.maintainable_flag = 'Y' \n");
        sqlBuffer.append("and mp.organization_code = :orgCode \n");
        sqlBuffer.append("and cii.instance_number = :assetNumber \n");
        
        paramMap.addValue("assetNumber", assetNumber);
        paramMap.addValue("orgCode", orgCode);
        
        try {
        	cnt = namedParameterJdbcTemplate.queryForObject(sqlBuffer.toString(), paramMap , int.class);
        	logger.debug("chkAssetEnabled="+cnt);
        	if (cnt>0){
            	return "Y";
            }else {
            	return "N";
            }
        }catch(DataAccessException se) {
        	logger.debug("ERROR chkAssetAttr: "+se.toString());
        	return "N";
        }
    }
	
	@Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
    public String chkMainBodyEnabled(String mainBodyNumber) throws SQLException, Exception {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        int cnt = 0;

        sqlBuffer.append("select count(1) \n");
        sqlBuffer.append("from xxeam_maintenance_body_dff_v mbody \n");
        sqlBuffer.append("where mbody.vendor_number = :vendor \n");
        sqlBuffer.append("and mbody.enabled_flag = 'Y' ");
        
        paramMap.addValue("vendor", mainBodyNumber);
        
        try {
        	cnt = namedParameterJdbcTemplate.queryForObject(sqlBuffer.toString(), paramMap , int.class);
        	logger.debug("chkMainBodyEnabled="+cnt);
        	if (cnt>0){
            	return "Y";
            }else {
            	return "N";
            }
        }catch(DataAccessException se) {
        	logger.debug("ERROR chkMainBodyEnabled: "+se.toString());
        	return "N";
        }
    }
	
	@Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
    public boolean getIsNonITAsset(String respId) throws SQLException, Exception {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        int cnt = 0;

        sqlBuffer.append("SELECT COUNT(1) \n");
        sqlBuffer.append("FROM xxeam_responsibilities_v \n");
        sqlBuffer.append("WHERE RESPONSIBILITY_NAME LIKE '%Non-IT Asset%' \n");
        sqlBuffer.append("AND RESP_TYPE='Non-IT Asset' \n");
        sqlBuffer.append("AND RESPONSIBILITY_ID = :respId \n");
        
        paramMap.addValue("respId", respId);
        
        try {
        	cnt = namedParameterJdbcTemplate.queryForObject(sqlBuffer.toString(), paramMap , int.class);
        	logger.debug("getIsNonITAsset="+cnt);
        	if (cnt>0){
            	return true;
            }else {
            	return false;
            }
        }catch(DataAccessException se) {
        	logger.debug("ERROR getIsNonITAsset : "+se.toString());
        	return false;
        }
    }
	
	@Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
    public boolean isAssetExpired(String assetNumber) throws SQLException, Exception{
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
        StringBuffer sqlBuffer = new StringBuffer();
        int cnt = 0;

        sqlBuffer.append("SELECT COUNT(1) CNT ");
        sqlBuffer.append("FROM XXEAM_EXT_MAINT_ASSET_INFO ");
        sqlBuffer.append("WHERE ASSET_NUMBER = :assetNumber ");
        sqlBuffer.append("AND (MAINTENANCE_BODY_TYPE IS NOT NULL ");
        sqlBuffer.append("OR MAINTENANCE_EXPIRY_DATE >= SYSDATE) ");
        
        paramMap.addValue("assetNumber", assetNumber);
        
        try {
        	cnt = namedParameterJdbcTemplate.queryForObject(sqlBuffer.toString(), paramMap , int.class);
        	logger.debug("isAssetExpired="+cnt);
        	if (cnt>0){
            	return false;
            }else {
            	return true;
            }
        }catch(DataAccessException se) {
        	logger.debug("ERROR isAssetExpired : "+se.toString());
        	return false;
        }
	}
		
}
