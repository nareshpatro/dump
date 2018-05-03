/* --------------------------------------
    File Name: DashBoardDaoImpl.java
    Author: Fanny Hung (PCCW)
    Date: 9-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - DashBoard Summary Implementation

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171009	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.eam.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import hk.org.ha.eam.model.DashBoard;
import hk.org.ha.eam.model.Dropdown;

@Repository
public class DashBoardDaoImpl implements DashBoardDao {

	private static final Logger logger = Logger.getLogger(DashBoardDaoImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private JdbcTemplate jdbcTemplate;

	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");

	@Autowired
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DashBoardDaoImpl(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<DashBoard> getWrSummaryStat(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception {

		String dashboardFor = searchCriteria.getDashboardFor();
		String eamOrg = searchCriteria.getEamOrg();
		String owningDept = searchCriteria.getOwningDept();
		String assetLocation = searchCriteria.getAssetLocation();
		String criticalMedEquip = searchCriteria.getCriticalMedEquip();
		String creationFrom = searchCriteria.getCreationFrom();
		String creationTo = searchCriteria.getCreationTo();
		String statusType = searchCriteria.getStatusType();
		String assetRisk = searchCriteria.getRiskLevel();
		String mBody = searchCriteria.getMBody();
		String scheduleFrom = searchCriteria.getScheduleFrom();
		String scheduleTo = searchCriteria.getScheduleTo();
		String module = searchCriteria.getModule();
		String exeSQL;

		logger.debug("getDashboardFor " +searchCriteria.getDashboardFor() );
		logger.debug("getDashboardFor " +searchCriteria.getEamOrg() );
		logger.debug("getCreationFrom " +searchCriteria.getCreationFrom() );
		logger.debug("getCreationTo " + searchCriteria.getCreationTo() );
		
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, -1);
		Date lastYear = cal.getTime();

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MONTH, -3);
		Date threeMonsBefore = cal2.getTime();
		cal2.add(Calendar.MONTH, 6);
		Date threeMonsAfter = cal2.getTime();

		if ("".equals(creationFrom)) {
			creationFrom = DATE_FORMAT.format(lastYear);
		}

		if ("".equals(creationTo)) {
			creationTo = DATE_FORMAT.format(today);
		}

		if ("".equals(scheduleFrom)) {
			scheduleFrom = DATE_FORMAT.format(threeMonsBefore);
		}

		if ("".equals(scheduleTo)) {
			scheduleTo = DATE_FORMAT.format(threeMonsAfter);
		}
		logger.debug("creationFrom " +creationFrom );
		logger.debug("creationTo " +creationTo );
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuffer tempQuery = new StringBuffer();
		
		tempQuery.append("select meaning wrstatuscode, description wrstatus, ");
		tempQuery.append("decode(description,'Total', wr_sum, wr_cnt) as wrstatuscount ");
		tempQuery.append("from ( select meaning, description, wr_cnt, sum(wr_cnt) over() as wr_sum ");
		tempQuery.append("from ( select  fv.meaning, fv.description, nvl(wr2.cnt,0) wr_cnt ");
		tempQuery.append("from mfg_lookups_v fv, ");
		tempQuery.append("( select count(*) cnt, work_request_status_code from ");
		tempQuery.append("(select work_request_status_code ");
		if((mBody != null && !"".equals(mBody)) || (assetLocation != null && !"".equals(assetLocation)) || (assetRisk != null && !"".equals(assetRisk)) || ("true".equals(criticalMedEquip))) {
			tempQuery.append("from xxeam_wr_summary_dboard_v wr ");
		}		
		else {
			tempQuery.append("from XXEAM_WR_SUMMARY_DBOARD_S_V wr ");
			
		}
		tempQuery.append("where wr.work_request_type_code = 10 ");
		tempQuery.append("and xxeam_maint_body_sec_chk_wo(wr.maintenance_body_number) = 'Y' ");
		tempQuery.append("and wr.organization_id in (select organization_id from xxeam_accessible_org_v) ");
		tempQuery.append("and wr.owning_department_id in (select department_id from xxeam_accessible_dept_v) ");
		
		if (dashboardFor != null && !"".equals(dashboardFor)) {
			tempQuery.append("AND WR.CREATED_BY = '"+dashboardFor+"' ");
		}
		if (mBody != null && !"".equals(mBody)) {
			tempQuery.append("AND wr.maintenance_body_number = '"+mBody+"' ");
		}
		if (eamOrg != null && !"".equals(eamOrg)) {
			tempQuery.append("AND WR.ORGANIZATION_ID = '"+eamOrg+"' ");
		} else {	
			tempQuery.append(" AND WR.ORGANIZATION_ID IN (SELECT ORGANIZATION_ID FROM XXEAM_ACCESSIBLE_ORG_V) ");
		}
		if (owningDept != null && !"".equals(owningDept)) {
			tempQuery.append("AND WR.OWNING_DEPARTMENT = '"+owningDept+"' ");
		}
		if (assetLocation != null && !"".equals(assetLocation)) {
			tempQuery.append("AND WR.ASSET_LOCATION_CODE = '"+assetLocation+"' ");
		}
		if (assetRisk != null && !"".equals(assetRisk)) {
			tempQuery.append("AND WR.RISK_LEVEL = '"+assetRisk+"' ");
		}
		if (creationFrom != null && !"".equals(creationFrom)) {
			tempQuery.append("AND trunc(WR.CREATION_DATE) >= trunc(to_date('"+creationFrom+"', 'dd/mm/yyyy')) ");
		}
		if (creationTo != null && !"".equals(creationTo)) {
			tempQuery.append("AND trunc(WR.CREATION_DATE) <= trunc(to_date('"+creationTo+"', 'dd/mm/yyyy')) ");
		}
		if ("true".equals(criticalMedEquip)) {
			tempQuery.append("AND EXISTS (\r\n" + 
            		"SELECT 1 \r\n" + 
            		"FROM MTL_SYSTEM_ITEMS MSI,\r\n" + 
            		"     MFG_LOOKUPS  L\r\n" + 
            		"WHERE\r\n" + 
            		"MSI.INVENTORY_ITEM_ID = WR.ITEM_ID\r\n" + 
            		"AND L.MEANING = MSI.SEGMENT1\r\n" + 
            		"AND MSI.ORGANIZATION_ID = 121\r\n" + 
            		"AND L.LOOKUP_TYPE = 'XXEAM_CRITICAL_MEDICAL_EQUIP' \r\n" + 
            		"AND L.ENABLED_FLAG = 'Y' \r\n" + 
            		"AND nvl(L.START_DATE_ACTIVE,trunc(sysdate)) <= trunc(sysdate)\r\n" + 
            		"AND nvl(L.END_DATE_ACTIVE,trunc(sysdate)) >= trunc(sysdate\r\n" + 
            		"))\r\n ");
		}

		tempQuery.append("and wr.work_request_status in ('Open','Awaiting Work Order','On Work Order','Rejected','Cancelled by User','Complete')) temp ");
		tempQuery.append("group by temp.work_request_status_code ) wr2 ");
		tempQuery.append("where fv.lookup_type = 'XXEAM_WR_STATUS_SUMMARY' ");
		tempQuery.append("and fv.enabled_flag = 'Y' ");
		tempQuery.append("and TRUNC(SYSDATE) between NVL(fv.start_date_active, TRUNC(SYSDATE)) ");
		tempQuery.append("and NVL(fv.end_date_active,TRUNC(SYSDATE)) ");
		tempQuery.append("and fv.meaning = wr2.work_request_status_code(+) ");
		tempQuery.append("order by fv.lookup_code ))");
		
		StringBuffer sqlQuery = new StringBuffer();

		sqlQuery.append("SELECT * \n");
		sqlQuery.append("FROM TABLE(XXEAM_DASHBOARD_SUMMARY.XXEAM_DASH_WRSUMMARY( \n");
		sqlQuery.append(":query,:userID,:respID,:appID))");
		
		paramMap.addValue("query",tempQuery.toString(), java.sql.Types.CLOB);
		paramMap.addValue("userID", userID);
		paramMap.addValue("respID", respID);
		paramMap.addValue("appID", appID);

		logger.info("WR SUMMARY SQL " +tempQuery.toString());

		List<DashBoard> listDashBoard = namedParameterJdbcTemplate.query(sqlQuery.toString(), paramMap,
				new RowMapper<DashBoard>() {
			@Override
			public DashBoard mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashBoard aDashBoard = new DashBoard();

				aDashBoard.setWrstatuscode(rs.getString("wrStatusCode"));
				aDashBoard.setWrstatus(rs.getString("wrStatus"));
				aDashBoard.setWrstatuscount(rs.getString("wrStatusCount"));


				logger.debug("WR STATUS CODE=" + aDashBoard.getWrstatuscode());
				logger.debug("WR STATUS " + aDashBoard.getWrstatus());
				logger.debug("WR STATUS COUNT " + aDashBoard.getWrstatuscount());

				return aDashBoard;
			}

		});

		return listDashBoard;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<DashBoard> getWoSummaryStat(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception {

		String dashboardFor = searchCriteria.getDashboardFor();
		String eamOrg = searchCriteria.getEamOrg();
		String owningDept = searchCriteria.getOwningDept();
		String assetLocation = searchCriteria.getAssetLocation();
		String criticalMedEquip = searchCriteria.getCriticalMedEquip();
		String creationFrom = searchCriteria.getCreationFrom();
		String creationTo = searchCriteria.getCreationTo();
		String statusType = searchCriteria.getStatusType();
		String assetRisk = searchCriteria.getRiskLevel();
		String mBody = searchCriteria.getMBody();
		String scheduleFrom = searchCriteria.getScheduleFrom();
		String scheduleTo = searchCriteria.getScheduleTo();
		String module = searchCriteria.getModule();
		logger.debug("criticalMedEquip in DAO " + criticalMedEquip);
		
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, -1);
		Date lastYear = cal.getTime();

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MONTH, -3);
		Date threeMonsBefore = cal2.getTime();
		cal2.add(Calendar.MONTH, 6);
		Date threeMonsAfter = cal2.getTime();
		
		String exeSQL;

		if ("".equals(creationFrom)) {
			creationFrom = DATE_FORMAT.format(lastYear);
		}

		if ("".equals(creationTo)) {
			creationTo = DATE_FORMAT.format(today);
		}

		if ("".equals(scheduleFrom)) {
			scheduleFrom = DATE_FORMAT.format(threeMonsBefore);
		}

		if ("".equals(scheduleTo)) {
			scheduleTo = DATE_FORMAT.format(threeMonsAfter);
		}

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuffer tempQuery = new StringBuffer();

		tempQuery.append("select '10' wotype, lookup_code wostatuscode, description wostatus, to_char(decode(description,'Total', wo_sum, wo_cnt)) as wostatuscount from ( \n");
		tempQuery.append("select lookup_code, description, wo_cnt, sum(wo_cnt) over() as wo_sum \n");
		tempQuery.append("from ( \n");
		tempQuery.append("select \n");
		tempQuery.append("fv.lookup_code, \n");
		tempQuery.append("fv.description, \n");
		tempQuery.append("nvl(wo2.cnt,0) wo_cnt \n");
		tempQuery.append("from mfg_lookups_v fv, \n");
		tempQuery.append("( select \n");  
		tempQuery.append("count(*) cnt, \n");
		tempQuery.append("wo_status \n");
		tempQuery.append("from \n"); 
		tempQuery.append("(select \n"); 
		tempQuery.append("wo_status \n");
		logger.debug("creationFrom:"+creationFrom);
		logger.debug("creationTo:"+creationTo);
		logger.debug("owningDept:"+owningDept);
		logger.debug("assetLocation:"+assetLocation);
		logger.debug("mBody:"+mBody);
		logger.debug("assetRisk:"+assetRisk);
		logger.debug("criticalMedEquip:"+criticalMedEquip);
		if (creationFrom != null && !"".equals(creationFrom) && creationTo != null && !"".equals(creationTo) &&
				(owningDept==null || "".equals(owningDept)) && (assetLocation == null || "".equals(assetLocation)) && (mBody == null || "".equals(mBody)) && (assetRisk == null || "".equals(assetRisk)) && "false".equals(criticalMedEquip) ) { 
			tempQuery.append("from XXEAM_EXT_WO_SIMPLE_V wo \n");
		}else if ("false".equals(criticalMedEquip) && (assetRisk == null || "".equals(assetRisk)) ) {
			tempQuery.append("from xxeam_wo_summary_dboard_s_v wo \n");
		}else {
			tempQuery.append("from xxeam_wo_summary_dboard_v wo \n");
		}
		tempQuery.append("where wo.work_order_type = '10' \n");
		tempQuery.append("and xxeam_maint_body_sec_chk_wo(wo.maintenance_body_number) = 'Y' \n");
		tempQuery.append("and wo.organization_id in (select organization_id from xxeam_accessible_org_v) \n");
		tempQuery.append("and wo.owning_department_id in (select department_id from xxeam_accessible_dept_v) \n");

		if (dashboardFor != null && !"".equals(dashboardFor)) {
			tempQuery.append("AND wo.wo_created_by_user_id = '"+dashboardFor+"' \n");
		}
		if (mBody != null && !"".equals(mBody)) {
			//tempQuery.append("AND wo.vendor_number = :mBody ");
			tempQuery.append("AND wo.maintenance_body_number = '"+mBody+"' \n");
		}
		if (eamOrg != null && !"".equals(eamOrg)) {
			tempQuery.append("AND wo.ORGANIZATION_ID = '"+eamOrg+"'  \n");
	    } 
		if (owningDept != null && !"".equals(owningDept)) {
			tempQuery.append("AND wo.owning_department_id = (select department_id from xxeam_accessible_dept_v where department_code = '"+owningDept+"') ");
		}
		if (assetLocation != null && !"".equals(assetLocation)) {
			tempQuery.append("AND wo.location_id = (select location_id from hr_locations_all where location_code = '"+assetLocation+"') ");
		}
		if (assetRisk != null && !"".equals(assetRisk)) {
			tempQuery.append("AND wo.risk_level = '"+assetRisk+"' ");
		}
		if (creationFrom != null && !"".equals(creationFrom)) {
			tempQuery.append("AND trunc(wo.wo_creation_date) >= trunc(to_date( '"+creationFrom+"' , 'dd/mm/yyyy')) ");
		}
		if (creationTo != null && !"".equals(creationTo)) {
			tempQuery.append("AND trunc(wo.wo_creation_date) <= trunc(to_date('"+creationTo+"', 'dd/mm/yyyy')) ");
		}
		if ("true".equals(criticalMedEquip)) {
			tempQuery.append("and (NVL('"+criticalMedEquip+"' ,'N') = 'N' or\n");
			tempQuery.append("     wo.item_code IS NULL or \n");
			tempQuery.append("     '"+criticalMedEquip+"'  = 'true' \n");
			tempQuery.append("     and exists (\n");
			tempQuery.append("      select 1\n");
			tempQuery.append("      from fnd_lookup_values cmed\n");
			tempQuery.append("      where cmed.lookup_type = 'XXEAM_CRITICAL_MEDICAL_EQUIP'\n");
			tempQuery.append("      and cmed.lookup_code = wo.item_code\n");
			tempQuery.append("      and cmed.enabled_flag = 'Y'\n");
			tempQuery.append("      and trunc(sysdate) between NVL(cmed.start_date_active, trunc(sysdate)) and \n");
			tempQuery.append("          NVL(cmed.end_date_active, trunc(sysdate))\n");
			tempQuery.append("      ))");
		}

		tempQuery.append("and wo.wo_status in ('Unreleased', 'Released', 'On Hold', 'Rejected', 'Cancelled', 'Complete', 'Complete - Pending Close', 'Closed') \n");
		tempQuery.append(") temp \n");
		tempQuery.append("group by temp.wo_status \n");
		tempQuery.append(") wo2 \n");
		tempQuery.append("where fv.lookup_type = 'XXEAM_WO_STATUS_SUMMARY' \n");
		tempQuery.append("and fv.enabled_flag = 'Y' \n");          
		tempQuery.append("and TRUNC(SYSDATE) between NVL(fv.start_date_active, TRUNC(SYSDATE)) \n"); 
		tempQuery.append("and NVL(fv.end_date_active,TRUNC(SYSDATE)) \n");
		tempQuery.append("and fv.meaning = wo2.wo_status(+) \n"); 
		tempQuery.append("and fv.description <> 'Cancelled By PM' \n");
		tempQuery.append("order by fv.lookup_code \n");
		tempQuery.append(")) ");

		StringBuffer sqlQuery = new StringBuffer();

		sqlQuery.append("SELECT * \n");
		sqlQuery.append("FROM TABLE(XXEAM_DASHBOARD_SUMMARY.XXEAM_DASH_WOSUMMARY( \n");
		sqlQuery.append(":query,:userID,:respID,:appID))");
		
		paramMap.addValue("query",tempQuery.toString(), java.sql.Types.CLOB);
		paramMap.addValue("userID", userID);
		paramMap.addValue("respID", respID);
		paramMap.addValue("appID", appID);

		//logger.debug("Work Orders Summary SQL: " + tempQuery.toString());
		logger.info("Work Orders Summary SQL: " + tempQuery.toString());
		logger.info("dashboardFor:"+dashboardFor);
		logger.info("mBody:"+mBody);
		logger.info("eamOrg:"+eamOrg);
		logger.info("owningDept:"+owningDept);
		logger.info("assetLocation:"+assetLocation);
		logger.info("assetRisk:"+assetRisk);
		logger.info("creationFrom:"+creationFrom);
		logger.info("creationTo:"+creationTo);

		List<DashBoard> listDashBoard = namedParameterJdbcTemplate.query(sqlQuery.toString(), paramMap,
				new RowMapper<DashBoard>() {
			@Override
			public DashBoard mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashBoard aDashBoard = new DashBoard();

				aDashBoard.setWostatus(rs.getString("wostatus"));
				aDashBoard.setWostatuscount(rs.getString("wostatuscount"));
				aDashBoard.setWostatuscode(rs.getString("wostatuscode"));

				logger.info("WO STATUS " + aDashBoard.getWostatus());
				logger.debug("WO STATUS COUNT " + aDashBoard.getWostatuscount());

				return aDashBoard;
			}

		});
		logger.info("END OF WO SUMMARY ");
		return listDashBoard;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<DashBoard> getOutstandingWo(DashBoard searchCriteria, String userID, String respID, String appID, String chart) throws Exception {

		String dashboardFor = searchCriteria.getDashboardFor();
		String eamOrg = searchCriteria.getEamOrg();
		String owningDept = searchCriteria.getOwningDept();
		String assetLocation = searchCriteria.getAssetLocation();
		String criticalMedEquip = searchCriteria.getCriticalMedEquip();
		String creationFrom = searchCriteria.getCreationFrom();
		String creationTo = searchCriteria.getCreationTo();
		String assetRisk = searchCriteria.getRiskLevel();
		String mBody = searchCriteria.getMBody();
	
		logger.debug("dashboardFor in getOutstandingWo DAO " + dashboardFor);
		logger.debug("eamOrg in getOutstandingWo DAO " + eamOrg);

		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, -1);
		Date lastYear = cal.getTime();

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MONTH, -3);
		Date threeMonsBefore = cal2.getTime();
		cal2.add(Calendar.MONTH, 6);
		Date threeMonsAfter = cal2.getTime();

		if ("".equals(creationFrom)) {
			creationFrom = DATE_FORMAT.format(lastYear);
		}

		if ("".equals(creationTo)) {
			creationTo = DATE_FORMAT.format(today);
		}
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuffer tempQuery = new StringBuffer();

		tempQuery.append("SELECT LOOKUP_CODE, DESCRIPTION, DECODE(LOOKUP_CODE,'T', sum(wo_cnt) over(),WO_CNT) AS WO_CNT FROM ( \n");
		tempQuery.append("select ");
		tempQuery.append("fv.lookup_code, ");
		tempQuery.append("fv.description description, ");
		tempQuery.append("nvl(wo2.cnt,0) wo_cnt ");  
		tempQuery.append("from mfg_lookups_v fv, ");        
		tempQuery.append("( select "); 
		tempQuery.append(" count(*) cnt, ");
		tempQuery.append("daterange ");
		tempQuery.append("from "); 
		
		if ("2".equals(chart)) {
			tempQuery.append(" (select case when trunc(sysdate) - trunc(wo.wo_creation_date) <= 1 then '1' ");
			tempQuery.append(" when trunc(sysdate) - trunc(wo.wo_creation_date) > 1 and ");
			tempQuery.append(" trunc(sysdate) - trunc(wo.wo_creation_date) <= 2 then '2' ");
			tempQuery.append(" when trunc(sysdate) - trunc(wo.wo_creation_date) > 2 and ");
			tempQuery.append(" trunc(sysdate) - trunc(wo.wo_creation_date) <= 3 then '3' ");
			tempQuery.append(" when trunc(sysdate) - trunc(wo.wo_creation_date) > 3 and ");
			tempQuery.append(" trunc(sysdate) - trunc(wo.wo_creation_date) <= 7 then '4' ");
			tempQuery.append(" when trunc(sysdate) - trunc(wo.wo_creation_date) > 7 then '5' ");       
		}
		else {
			tempQuery.append(" (select case when trunc(sysdate) - trunc(wo.wo_creation_date) <= 3 then '1' ");
			tempQuery.append(" when trunc(sysdate) - trunc(wo.wo_creation_date) > 3 and ");
			tempQuery.append(" trunc(sysdate) - trunc(wo.wo_creation_date) <= 7 then '2' ");
			tempQuery.append(" when trunc(sysdate) - trunc(wo.wo_creation_date) > 7 and ");
			tempQuery.append(" trunc(sysdate) - trunc(wo.wo_creation_date) <= 14 then '3' ");
			tempQuery.append(" when trunc(sysdate) - trunc(wo.wo_creation_date) > 14 and ");
			tempQuery.append(" trunc(sysdate) - trunc(wo.wo_creation_date) <= 21 then '4' ");
			tempQuery.append(" when trunc(sysdate) - trunc(wo.wo_creation_date) > 21 then '5' ");       
		}
		       
		tempQuery.append(" end daterange ");
		if (creationFrom != null && !"".equals(creationFrom) && creationTo != null && !"".equals(creationTo) &&
				(owningDept==null || "".equals(owningDept)) && (assetLocation == null || "".equals(assetLocation)) && (mBody == null || "".equals(mBody)) && (assetRisk == null || "".equals(assetRisk)) && "false".equals(criticalMedEquip) ) { 
			tempQuery.append("from XXEAM_EXT_WO_SIMPLE_V wo \n");
		}else if ("false".equals(criticalMedEquip) && (assetRisk == null || "".equals(assetRisk)) ) {
			tempQuery.append("from xxeam_wo_summary_dboard_s_v wo \n");
		}else {
			tempQuery.append("from xxeam_wo_summary_dboard_v wo \n");
		}
		tempQuery.append(" where wo.work_order_type = 10 ");
		tempQuery.append("and xxeam_maint_body_sec_chk_wo(wo.maintenance_body_number) = 'Y' \n");
		tempQuery.append("and wo.organization_id in (select organization_id from xxeam_accessible_org_v) \n");
		tempQuery.append("and wo.owning_department_id in (select department_id from xxeam_accessible_dept_v) \n");
		
		if("1".equals(chart)) {
			tempQuery.append(" and wo_status in ('On Hold', 'Released') ");
		}
		else if ("2".equals(chart)) {
			tempQuery.append(" and wo_status in ('Unreleased') ");
		}
		else {
			tempQuery.append(" and wo_status in ('Unreleased','On Hold', 'Released') ");
		}
		
		tempQuery.append(" and wo_status not in ('Complete', 'Complete - No Charges', 'Closed', 'Cancelled', 'Draft') ");
		tempQuery.append(" and wo_status not in ('Complete', 'Complete - No Charges', 'Closed', 'Cancelled', 'Draft') ");
		if (eamOrg != null && !"".equals(eamOrg)) {
			tempQuery.append(" and wo.organization_id = '"+eamOrg+"' ");
	    } 
		if (owningDept != null && !"".equals(owningDept)) {
			tempQuery.append(" and wo.owning_department_id = (select department_id from xxeam_accessible_dept_v where department_code = '"+owningDept+"') ");
		}
		if (assetLocation != null && !"".equals(assetLocation)) {
			tempQuery.append("AND wo.location_id = (select location_id from hr_locations_all where location_code = '"+assetLocation+"') ");
		}
		if (mBody != null && !"".equals(mBody)) {
			tempQuery.append(" and wo.maintenance_body_number = '"+mBody+"' ");
		}
		if (assetRisk != null && !"".equals(assetRisk)) {
			tempQuery.append(" and wo.risk_level = '"+assetRisk+"' ");
		}
		if (creationFrom != null && !"".equals(creationFrom)) {
			tempQuery.append("AND trunc(wo.wo_creation_date) >= trunc(to_date('"+creationFrom+"', 'dd/mm/yyyy')) \n");
		}
		if (creationTo != null && !"".equals(creationTo)) {
			tempQuery.append("AND trunc(wo.wo_creation_date) <= trunc(to_date('"+creationTo+"', 'dd/mm/yyyy')) \n");
		}
		/*if (scheduleFrom != null && !"".equals(scheduleFrom)) {
			tempQuery.append("AND trunc(wo.scheduled_start_date) >= trunc(to_date('"+scheduleFrom+"' , 'dd/mm/yyyy')) ");
		}
		if (scheduleTo != null && !"".equals(scheduleTo)) {
			tempQuery.append("AND trunc(wo.scheduled_start_date) <= trunc(to_date('"+scheduleTo+"', 'dd/mm/yyyy')) ");
		}*/
		if (dashboardFor != null && !"".equals(dashboardFor)) {
			tempQuery.append(" and wo.wo_created_by_user_id = '"+dashboardFor+"' ");
		}
		if ("true".equals(criticalMedEquip)) {  
		     
			tempQuery.append(" and '"+criticalMedEquip+"' = 'true' and exists ( ");
			tempQuery.append(" select 1 ");
			tempQuery.append(" from fnd_lookup_values flv ");
			tempQuery.append(" where flv.lookup_type = 'XXEAM_CRITICAL_MEDICAL_EQUIP' ");
			tempQuery.append(" and flv.lookup_code = wo.item_code ");
			tempQuery.append(" and flv.enabled_flag = 'Y' ");
			tempQuery.append(" and TRUNC(sysdate) between NVL(flv.start_date_active, TRUNC(SYSDATE)) and ");
			tempQuery.append(" NVL(flv.end_date_active, TRUNC(SYSDATE)) ");
			tempQuery.append(" ) ");
		}
   
		tempQuery.append(" ) temp ");
		tempQuery.append(" group by temp.daterange ");
		tempQuery.append(" /*union all ");
		tempQuery.append(" select "); 
		tempQuery.append(" count(*) cnt, ");
		tempQuery.append(" 'T' daterange ");
		if (creationFrom != null && !"".equals(creationFrom) && creationTo != null && !"".equals(creationTo) &&
				(owningDept==null || "".equals(owningDept)) && (assetLocation == null || "".equals(assetLocation)) && (mBody == null || "".equals(mBody)) && (assetRisk == null || "".equals(assetRisk)) && "false".equals(criticalMedEquip) ) { 
			tempQuery.append("from XXEAM_EXT_WO_SIMPLE_V wo \n");
		}else if ("false".equals(criticalMedEquip) && (assetRisk == null || "".equals(assetRisk)) ) {
			tempQuery.append("from xxeam_wo_summary_dboard_s_v wo \n");
		}else {
			tempQuery.append("from xxeam_wo_summary_dboard_v wo \n");
		}
		tempQuery.append(" where wo.work_order_type = 10 \n");
		tempQuery.append("and xxeam_maint_body_sec_chk_wo(wo.maintenance_body_number) = 'Y' \n");
		tempQuery.append("and wo.organization_id in (select organization_id from xxeam_accessible_org_v) \n");
		tempQuery.append("and wo.owning_department_id in (select department_id from xxeam_accessible_dept_v) \n");
		tempQuery.append(" and wo_status in ('Unreleased','Released','On Hold') ");
		tempQuery.append(" and wo_status not in ('Complete', 'Complete - No Charges', 'Closed', 'Cancelled', 'Draft') ");
		tempQuery.append(" and wo_status not in ('Complete', 'Complete - No Charges', 'Closed', 'Cancelled', 'Draft') ");
		if (eamOrg != null && !"".equals(eamOrg)) {
			tempQuery.append(" and wo.organization_id = '"+eamOrg+"' ");
	    } 
		if (owningDept != null && !"".equals(owningDept)) {
			tempQuery.append(" and wo.owning_department_id = (select department_id from xxeam_accessible_dept_v where department_code =  '"+owningDept+"') ");
		}
		if (assetLocation != null && !"".equals(assetLocation)) {
			tempQuery.append("AND wo.location_id = (select location_id from hr_locations_all where location_code = '"+assetLocation+"') ");
		}
		if (mBody != null && !"".equals(mBody)) {
			tempQuery.append(" and wo.maintenance_body_number = '"+mBody+"' ");
		}
		if (assetRisk != null && !"".equals(assetRisk)) {
			tempQuery.append(" and wo.risk_level = '"+assetRisk+"' ");
		}
		if (creationFrom != null && !"".equals(creationFrom)) {
			tempQuery.append("AND trunc(wo.wo_creation_date) >= trunc(to_date('"+creationFrom+"' , 'dd/mm/yyyy')) \n");
		}
		if (creationTo != null && !"".equals(creationTo)) {
			tempQuery.append("AND trunc(wo.wo_creation_date) <= trunc(to_date('"+creationTo+"' , 'dd/mm/yyyy')) \n");
		}
		
		if (dashboardFor != null && !"".equals(dashboardFor)) {
			tempQuery.append(" and wo.wo_created_by_user_id = '"+dashboardFor+"' ");
		}
		if ("true".equals(criticalMedEquip)) {
			tempQuery.append(" and '"+criticalMedEquip+"' = 'true' and exists ( ");
			tempQuery.append(" select 1 ");
			tempQuery.append(" from fnd_lookup_values flv ");
			tempQuery.append(" where flv.lookup_type = 'XXEAM_CRITICAL_MEDICAL_EQUIP' ");
			tempQuery.append(" and flv.lookup_code = wo.item_code ");
			tempQuery.append(" and flv.enabled_flag = 'Y' ");
			tempQuery.append(" and TRUNC(sysdate) between NVL(flv.start_date_active, TRUNC(SYSDATE)) and ");
			tempQuery.append(" NVL(flv.end_date_active, TRUNC(SYSDATE)) ");
			tempQuery.append(" ) ");
		}

		tempQuery.append("*/ ) wo2 ");
		tempQuery.append(" where ");  
		tempQuery.append(" fv.lookup_type = 'XXEAM_OS_WORK_ORDERS' ");
		tempQuery.append(" and fv.enabled_flag = 'Y' ");
		tempQuery.append(" and TRUNC(SYSDATE) between NVL(fv.start_date_active, TRUNC(SYSDATE)) ");
		tempQuery.append(" and NVL(fv.end_date_active,TRUNC(SYSDATE)) ");
		tempQuery.append(" and fv.lookup_code = wo2.daterange(+) ");
		tempQuery.append(" order by fv.lookup_code ");
		tempQuery.append(" ) ");
		
		StringBuffer sqlQuery = new StringBuffer();

		sqlQuery.append("SELECT * \n");
		sqlQuery.append("FROM TABLE(XXEAM_DASHBOARD_SUMMARY.XXEAM_DASH_OUTSTDWO( \n");
		sqlQuery.append(":query,:userID,:respID,:appID))");
		
		paramMap.addValue("query",tempQuery.toString(), java.sql.Types.CLOB);
		paramMap.addValue("userID", userID);
		paramMap.addValue("respID", respID);
		paramMap.addValue("appID", appID);

		
		logger.debug("Outstanding Work Orders SQL("+chart+"): " + tempQuery.toString());

		List<DashBoard> listDashBoard = namedParameterJdbcTemplate.query(sqlQuery.toString(), paramMap,
				new RowMapper<DashBoard>() {
			@Override
			public DashBoard mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashBoard aDashBoard = new DashBoard();

				aDashBoard.setOutstanding(rs.getString("description"));
				aDashBoard.setOutstandingcount(rs.getString("wo_cnt"));
				aDashBoard.setWrstatuscode("lookup_code");

				logger.debug("Outstanding Description " + aDashBoard.getOutstanding());
				logger.debug("Outstanding Count " + aDashBoard.getOutstandingcount());

				return aDashBoard;
			}

		});

		return listDashBoard;

	}

	public List<DashBoard> getPmSummary(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception {
		String dashboardFor = searchCriteria.getDashboardFor();
		String eamOrg = searchCriteria.getEamOrg();
		String owningDept = searchCriteria.getOwningDept();
		String assetLocation = searchCriteria.getAssetLocation();
		String criticalMedEquip = searchCriteria.getCriticalMedEquip();
		String creationFrom = searchCriteria.getCreationFrom();
		String creationTo = searchCriteria.getCreationTo();
		String statusType = searchCriteria.getStatusType();
		String assetRisk = searchCriteria.getRiskLevel();
		String mBody = searchCriteria.getMBody();
		String scheduleFrom = searchCriteria.getScheduleFrom();
		String scheduleTo = searchCriteria.getScheduleTo();
		String module = searchCriteria.getModule();
		String exeSQL;

		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, -1);
		Date lastYear = cal.getTime();

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MONTH, -3);
		Date threeMonsBefore = cal2.getTime();
		cal2.add(Calendar.MONTH, 6);
		Date threeMonsAfter = cal2.getTime();

		if ("".equals(creationFrom)) {
			creationFrom = DATE_FORMAT.format(lastYear);
		}

		if ("".equals(creationTo)) {
			creationTo = DATE_FORMAT.format(today);
		}

		if ("".equals(scheduleFrom)) {
			scheduleFrom = DATE_FORMAT.format(threeMonsBefore);
		}

		if ("".equals(scheduleTo)) {
			scheduleTo = DATE_FORMAT.format(threeMonsAfter);
		}

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuffer tempQuery = new StringBuffer();

		tempQuery.append("select ");		   
		tempQuery.append("  fv.description description, ");
		tempQuery.append("  nvl(wo2.cnt,0) wo_cnt ");
		tempQuery.append("  from mfg_lookups_v fv, ");        
		tempQuery.append("  ( select ");
		tempQuery.append("    count(*) cnt, ");
		tempQuery.append("    status ");
		tempQuery.append("    from ");
		tempQuery.append("    (select case WHEN trunc(wo.scheduled_start_date) < trunc(sysdate) AND wo.wo_status IN ('Unreleased') THEN '1' "); //overdue
		tempQuery.append("            WHEN trunc(wo.scheduled_start_date) >= trunc(sysdate) AND wo.wo_status IN ('Unreleased') THEN '3' "); //upcoming
		tempQuery.append("            WHEN wo_status IN ('Rejected','Cancelled','Cancelled By PM','Complete','Complete - Pending Close','Closed') THEN '2' ");//completed
		tempQuery.append("      end status ");
		if (creationFrom != null && !"".equals(creationFrom) && creationTo != null && !"".equals(creationTo) &&
				(owningDept==null || "".equals(owningDept)) && (assetLocation == null || "".equals(assetLocation)) && (mBody == null || "".equals(mBody)) && (assetRisk == null || "".equals(assetRisk)) && "false".equals(criticalMedEquip) ) { 
			tempQuery.append("from XXEAM_EXT_WO_SIMPLE_V wo \n");
		}else if ("false".equals(criticalMedEquip) && (assetRisk == null || "".equals(assetRisk)) ) {
			tempQuery.append("from xxeam_wo_summary_dboard_s_v wo \n");
		}else {
			tempQuery.append("from xxeam_wo_summary_dboard_v wo \n");
		}
		tempQuery.append("     where wo.work_order_type = 20 \n");
		tempQuery.append("and xxeam_maint_body_sec_chk_wo(wo.maintenance_body_number) = 'Y' \n");
		tempQuery.append("and wo.organization_id in (select organization_id from xxeam_accessible_org_v) \n");
		tempQuery.append("and wo.owning_department_id in (select department_id from xxeam_accessible_dept_v) \n");
		
		if (scheduleFrom != null && !"".equals(scheduleFrom)) {
			tempQuery.append("		 and trunc(wo.scheduled_start_date) >= TO_DATE('"+scheduleFrom+"', 'DD/MM/YYYY') ");
		}
		if (scheduleTo != null && !"".equals(scheduleTo)) {
			tempQuery.append("     and trunc(wo.scheduled_start_date) <= TO_DATE('"+scheduleTo+"', 'DD/MM/YYYY') ");
		}
		tempQuery.append("     and wo.wo_status <> 'Draft' ");

		if (eamOrg != null && !"".equals(eamOrg)) {
			tempQuery.append(" and wo.organization_id = '"+eamOrg+"' ");
	    } 
		if (owningDept != null && !"".equals(owningDept)) {
			tempQuery.append(" and wo.owning_department_id = (select department_id from xxeam_accessible_dept_v where department_code = '"+owningDept+"') ");
		}
		if (assetLocation != null && !"".equals(assetLocation)) {
			tempQuery.append("AND wo.location_id = (select location_id from hr_locations_all where location_code = '"+assetLocation+"') ");
		}
		if (mBody != null && !"".equals(mBody)) {
			tempQuery.append(" and wo.maintenance_body_number = '"+mBody+"' ");
		}
		if (assetRisk != null && !"".equals(assetRisk)) {
			tempQuery.append(" and wo.risk_level = '"+assetRisk+"' ");
		}
		if (dashboardFor != null && !"".equals(dashboardFor)) {
			tempQuery.append(" and wo.wo_created_by_user_id = '"+dashboardFor+"' ");
		}
		if ("true".equals(criticalMedEquip)) {
			tempQuery.append(" and exists (                                                             \n");
			tempQuery.append("      select 1                                                            \n");
			tempQuery.append("      from fnd_lookup_values f                                            \n");
			tempQuery.append("      where f.lookup_type = 'XXEAM_CRITICAL_MEDICAL_EQUIP'                \n");
			tempQuery.append("      and f.lookup_code = wo.item_code                                    \n");
			tempQuery.append("      and f.enabled_flag = 'Y'                                            \n");
			tempQuery.append("      and trunc(sysdate) between NVL(f.start_date_active, trunc(sysdate)) \n");
			tempQuery.append("      and NVL(f.end_date_active, trunc(sysdate))                          \n");
			tempQuery.append("      )                                                                   \n");
		}
		
		tempQuery.append("     ) temp ");
		tempQuery.append("     group by temp.status ");
		tempQuery.append("     union all ");
		tempQuery.append("     select ");
		tempQuery.append("     count(*) cnt, ");
		tempQuery.append(" 'T' status ");
		if (creationFrom != null && !"".equals(creationFrom) && creationTo != null && !"".equals(creationTo) &&
				(owningDept==null || "".equals(owningDept)) && (assetLocation == null || "".equals(assetLocation)) && (mBody == null || "".equals(mBody)) && (assetRisk == null || "".equals(assetRisk)) && "false".equals(criticalMedEquip) ) { 
			tempQuery.append("from XXEAM_EXT_WO_SIMPLE_V wo \n");
		}else if ("false".equals(criticalMedEquip) && (assetRisk == null || "".equals(assetRisk)) ) {
			tempQuery.append("from xxeam_wo_summary_dboard_s_v wo \n");
		}else {
			tempQuery.append("from xxeam_wo_summary_dboard_v wo \n");
		}
		tempQuery.append("     where wo.work_order_type = 20 \n");
		tempQuery.append("and wo.wo_status IN ('Rejected','Cancelled','Cancelled By PM','Complete','Complete - Pending Close','Closed','Unreleased') \n");
		tempQuery.append("and xxeam_maint_body_sec_chk_wo(wo.maintenance_body_number) = 'Y' \n");
		tempQuery.append("and wo.organization_id in (select organization_id from xxeam_accessible_org_v) \n");
		tempQuery.append("and wo.owning_department_id in (select department_id from xxeam_accessible_dept_v) \n");
		
		if (scheduleFrom != null && !"".equals(scheduleFrom)) {
			tempQuery.append("		 and trunc(wo.scheduled_start_date) >= TO_DATE('"+scheduleFrom+"', 'DD/MM/YYYY') ");
		}
		if (scheduleTo != null && !"".equals(scheduleTo)) {
			tempQuery.append("     and trunc(wo.scheduled_start_date) <= TO_DATE('"+scheduleTo+"', 'DD/MM/YYYY') ");
		}		
		tempQuery.append(" and wo.wo_status <> 'Draft' ");
		if (eamOrg != null && !"".equals(eamOrg)) {
			tempQuery.append(" and wo.organization_id = '"+eamOrg+"' ");
	    } 
		if (owningDept != null && !"".equals(owningDept)) {
			tempQuery.append(" and wo.owning_department_id = (select department_id from xxeam_accessible_dept_v where department_code = '"+owningDept+"') ");
		}
		if (assetLocation != null && !"".equals(assetLocation)) {
			tempQuery.append("AND wo.location_id = (select location_id from hr_locations_all where location_code = '"+assetLocation+"') ");
		}
		if (mBody != null && !"".equals(mBody)) {
			tempQuery.append(" and wo.maintenance_body_number = '"+mBody+"' ");
		}
		if (assetRisk != null && !"".equals(assetRisk)) {
			tempQuery.append(" and wo.risk_level = '"+assetRisk+"' ");
		}
		if (dashboardFor != null && !"".equals(dashboardFor)) {
			tempQuery.append(" and wo.wo_created_by_user_id = '"+dashboardFor+"' ");
		}
		if ("true".equals(criticalMedEquip)) {
			tempQuery.append(" and exists (                                                             \n");
			tempQuery.append("      select 1                                                            \n");
			tempQuery.append("      from fnd_lookup_values f                                            \n");
			tempQuery.append("      where f.lookup_type = 'XXEAM_CRITICAL_MEDICAL_EQUIP'                \n");
			tempQuery.append("      and f.lookup_code = wo.item_code                                    \n");
			tempQuery.append("      and f.enabled_flag = 'Y'                                            \n");
			tempQuery.append("      and trunc(sysdate) between NVL(f.start_date_active, trunc(sysdate)) \n");
			tempQuery.append("      and NVL(f.end_date_active, trunc(sysdate))                          \n");
			tempQuery.append("      )                                                                   \n");
		}

		tempQuery.append("     ) wo2 ");
		tempQuery.append("    where  ");   
		tempQuery.append("     fv.lookup_type = 'XXEAM_PM_SUMMARY' ");
		tempQuery.append("     and fv.enabled_flag = 'Y' ");
		tempQuery.append("     and TRUNC(SYSDATE) between NVL(fv.start_date_active, TRUNC(SYSDATE)) ");
		tempQuery.append("         and NVL(fv.end_date_active,TRUNC(SYSDATE)) ");
		tempQuery.append("     and fv.lookup_code = wo2.status(+) ");
		tempQuery.append("     order by fv.lookup_code ");
		
		StringBuffer sqlQuery = new StringBuffer();

		sqlQuery.append("SELECT * \n");
		sqlQuery.append("FROM TABLE(XXEAM_DASHBOARD_SUMMARY.XXEAM_DASH_PMSUM( \n");
		sqlQuery.append(":query,:userID,:respID,:appID))");
		
		paramMap.addValue("query",tempQuery.toString(), java.sql.Types.CLOB);
		paramMap.addValue("userID", userID);
		paramMap.addValue("respID", respID);
		paramMap.addValue("appID", appID);

		logger.debug("PM Summary SQL: " + tempQuery.toString());

		List<DashBoard> listDashBoard = namedParameterJdbcTemplate.query(sqlQuery.toString(), paramMap,
				new RowMapper<DashBoard>() {
			@Override
			public DashBoard mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashBoard aDashBoard = new DashBoard();
				
				if("Overdue".equals(rs.getString("description")) || "Upcoming".equals(rs.getString("description"))) {
					aDashBoard.setPmSummaryDesc(rs.getString("description")+"*");
				}
				else {
					aDashBoard.setPmSummaryDesc(rs.getString("description"));
				}
				
				aDashBoard.setPmSummaryCount(rs.getString("wo_cnt"));

				logger.debug("PM Summary Description " + aDashBoard.getPmSummaryDesc());
				logger.debug("PM Summary Count " + aDashBoard.getPmSummaryCount());

				return aDashBoard;
			}

		});

		return listDashBoard;

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<DashBoard> getPmStatusSummaryStat(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception {

		String dashboardFor = searchCriteria.getDashboardFor();
		String eamOrg = searchCriteria.getEamOrg();
		String owningDept = searchCriteria.getOwningDept();
		String assetLocation = searchCriteria.getAssetLocation();
		String criticalMedEquip = searchCriteria.getCriticalMedEquip();
		String creationFrom = searchCriteria.getCreationFrom();
		String creationTo = searchCriteria.getCreationTo();
		String statusType = searchCriteria.getStatusType();
		String assetRisk = searchCriteria.getRiskLevel();
		String mBody = searchCriteria.getMBody();
		String scheduleFrom = searchCriteria.getScheduleFrom();
		String scheduleTo = searchCriteria.getScheduleTo();
		String module = searchCriteria.getModule();
		logger.debug("criticalMedEquip in DAO " + criticalMedEquip);

		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, -1);
		Date lastYear = cal.getTime();

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MONTH, -3);
		Date threeMonsBefore = cal2.getTime();
		cal2.add(Calendar.MONTH, 6);
		Date threeMonsAfter = cal2.getTime();

		if ("".equals(creationFrom)) {
			creationFrom = DATE_FORMAT.format(lastYear);
		}

		if ("".equals(creationTo)) {
			creationTo = DATE_FORMAT.format(today);
		}

		if ("".equals(scheduleFrom)) {
			scheduleFrom = DATE_FORMAT.format(threeMonsBefore);
		}

		if ("".equals(scheduleTo)) {
			scheduleTo = DATE_FORMAT.format(threeMonsAfter);
		}

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuffer tempQuery = new StringBuffer();

		//tempQuery.append(woStatusCommonSQL(dashboardFor, DashBoardConstant.WO_TYPE_20));
		tempQuery.append("select '20' wotype, description wostatus20, decode(description,'Total', wo_sum, wo_cnt) as wostatuscount20 from ( \n");
		tempQuery.append("select description, wo_cnt, sum(wo_cnt) over() as wo_sum \n");
		tempQuery.append("from ( \n");
		tempQuery.append("select \n");
		tempQuery.append("fv.description, \n");
		tempQuery.append("nvl(wo2.cnt,0) wo_cnt \n");
		tempQuery.append("from mfg_lookups_v fv, \n");
		tempQuery.append("( select \n");  
		tempQuery.append("count(*) cnt, \n");
		tempQuery.append("wo_status \n");
		tempQuery.append("from \n"); 
		tempQuery.append("(select \n"); 
		tempQuery.append("wo_status \n");
		if (creationFrom != null && !"".equals(creationFrom) && creationTo != null && !"".equals(creationTo) &&
				(owningDept==null || "".equals(owningDept)) && (assetLocation == null || "".equals(assetLocation)) && (mBody == null || "".equals(mBody)) && (assetRisk == null || "".equals(assetRisk)) && "false".equals(criticalMedEquip) ) { 
			tempQuery.append("from XXEAM_EXT_WO_SIMPLE_V wo \n");
		}else if ("false".equals(criticalMedEquip) && (assetRisk == null || "".equals(assetRisk)) ) {
			tempQuery.append("from xxeam_wo_summary_dboard_s_v wo \n");
		}else {
			tempQuery.append("from xxeam_wo_summary_dboard_v wo \n");
		}
		tempQuery.append("where wo.work_order_type = 20 -- preventive \n");		
		tempQuery.append("and xxeam_maint_body_sec_chk_wo(wo.maintenance_body_number) = 'Y' \n");
		tempQuery.append("and wo.organization_id in (select organization_id from xxeam_accessible_org_v) \n");
		tempQuery.append("and wo.owning_department_id in (select department_id from xxeam_accessible_dept_v) \n");
		
		if (dashboardFor != null && !"".equals(dashboardFor)) {
			tempQuery.append("AND wo.wo_created_by_user_id = '"+dashboardFor+"' \n");
		}
		if (mBody != null && !"".equals(mBody)) {
			tempQuery.append("AND wo.maintenance_body_number = '"+mBody+"' \n");
		}
		if (eamOrg != null && !"".equals(eamOrg)) {
			tempQuery.append("AND wo.ORGANIZATION_ID = '"+eamOrg+"'  \n");
	    } 
		if (owningDept != null && !"".equals(owningDept)) {
			tempQuery.append("AND wo.owning_department_id = (select department_id from xxeam_accessible_dept_v where department_code = '"+owningDept+"') ");
		}
		if (assetLocation != null && !"".equals(assetLocation)) {
			tempQuery.append("AND wo.location_id = (select location_id from hr_locations_all where location_code = '"+assetLocation+"') ");
		}
		if (assetRisk != null && !"".equals(assetRisk)) {
			tempQuery.append("AND wo.risk_level = '"+assetRisk+"' ");
		}		
		
		//if (dashboardFor != null && !"".equals(dashboardFor)) {
			if (scheduleFrom != null && !"".equals(scheduleFrom)) {
				tempQuery.append("AND trunc(wo.scheduled_start_date) >= trunc(to_date('"+scheduleFrom+"' , 'dd/mm/yyyy')) ");
			}
			if (scheduleTo != null && !"".equals(scheduleTo)) {
				tempQuery.append("AND trunc(wo.scheduled_start_date) <= trunc(to_date('"+scheduleTo+"' , 'dd/mm/yyyy')) ");
			}
			/*
			if (creationFrom != null && !"".equals(creationFrom)) {
				tempQuery.append("AND trunc(wo.wo_creation_date) >= trunc(to_date('"+creationFrom+"' , 'dd/mm/yyyy')) \n");
				paramMap.addValue("creationFrom", creationFrom);
			}
			if (creationTo != null && !"".equals(creationTo)) {
				tempQuery.append("AND trunc(wo.wo_creation_date) <= trunc(to_date('"+creationTo+"' , 'dd/mm/yyyy')) \n");
				paramMap.addValue("creationTo", creationTo);
			}	*/		
		//}
		if ("true".equals(criticalMedEquip)) {
			tempQuery.append(" and exists (                                                             \n");
			tempQuery.append("      select 1                                                            \n");
			tempQuery.append("      from fnd_lookup_values f                                            \n");
			tempQuery.append("      where f.lookup_type = 'XXEAM_CRITICAL_MEDICAL_EQUIP'                \n");
			tempQuery.append("      and f.lookup_code = wo.item_code                                    \n");
			tempQuery.append("      and f.enabled_flag = 'Y'                                            \n");
			tempQuery.append("      and trunc(sysdate) between NVL(f.start_date_active, trunc(sysdate)) \n");
			tempQuery.append("      and NVL(f.end_date_active, trunc(sysdate))                          \n");
			tempQuery.append("      )                                                                   \n");
		}
	
		tempQuery.append("and wo.wo_status in ('Unreleased', 'Released', 'On Hold', 'Rejected', 'Cancelled', 'Complete', 'Complete - Pending Close', 'Closed', 'Cancelled By PM') \n");
		tempQuery.append(") temp \n");
		tempQuery.append("group by temp.wo_status \n");
		tempQuery.append(") wo2 \n");
		tempQuery.append("where fv.lookup_type = 'XXEAM_WO_STATUS_SUMMARY' \n");
		tempQuery.append("and fv.enabled_flag = 'Y' \n");          
		tempQuery.append("and TRUNC(SYSDATE) between NVL(fv.start_date_active, TRUNC(SYSDATE)) \n"); 
		tempQuery.append("and NVL(fv.end_date_active,TRUNC(SYSDATE)) \n");
		tempQuery.append("and fv.meaning = wo2.wo_status(+) \n"); 
		tempQuery.append("order by fv.lookup_code \n");
		tempQuery.append("))");
		
		StringBuffer sqlQuery = new StringBuffer();

		sqlQuery.append("SELECT * \n");
		sqlQuery.append("FROM TABLE(XXEAM_DASHBOARD_SUMMARY.XXEAM_DASH_PMSTATSUM( \n");
		sqlQuery.append(":query,:userID,:respID,:appID))");
		
		paramMap.addValue("query",tempQuery.toString(), java.sql.Types.CLOB);
		paramMap.addValue("userID", userID);
		paramMap.addValue("respID", respID);
		paramMap.addValue("appID", appID);

		logger.info("PM Status Summary SQL: " + tempQuery.toString());
		logger.info("dashboardFor:"+dashboardFor);
		logger.info("mBody:"+mBody);
		logger.info("eamOrg:"+eamOrg);
		logger.info("owningDept:"+owningDept);
		logger.info("assetLocation:"+assetLocation);
		logger.info("assetRisk:"+assetRisk);
		logger.info("scheduleFrom:"+scheduleFrom);
		logger.info("scheduleTo:"+scheduleTo);
		logger.info("creationFrom:"+creationFrom);
		logger.info("creationTo:"+creationTo);

		List<DashBoard> listDashBoard = namedParameterJdbcTemplate.query(sqlQuery.toString(), paramMap,
				new RowMapper<DashBoard>() {
			@Override
			public DashBoard mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashBoard aDashBoard = new DashBoard();

				aDashBoard.setPmWostatus(rs.getString("wostatus20"));
				aDashBoard.setPmWostatuscount(rs.getString("wostatuscount20"));

				logger.debug("PM WO STATUS " + aDashBoard.getPmWostatus());
				logger.debug("PM WO STATUS COUNT " + aDashBoard.getPmWostatuscount());

				return aDashBoard;
			}

		});

		return listDashBoard;
	}
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<DashBoard> getPmUpcomingStat(DashBoard searchCriteria, String userID, String respID, String appID, String chart) throws Exception {

		String dashboardFor = searchCriteria.getDashboardFor();
		String eamOrg = searchCriteria.getEamOrg();
		String owningDept = searchCriteria.getOwningDept();
		String assetLocation = searchCriteria.getAssetLocation();
		String criticalMedEquip = searchCriteria.getCriticalMedEquip();
		String creationFrom = searchCriteria.getCreationFrom();
		String creationTo = searchCriteria.getCreationTo();
		String statusType = searchCriteria.getStatusType();
		String assetRisk = searchCriteria.getRiskLevel();
		String mBody = searchCriteria.getMBody();
		String scheduleFrom = searchCriteria.getScheduleFrom();
		String scheduleTo = searchCriteria.getScheduleTo();
		String module = searchCriteria.getModule();
		logger.debug("criticalMedEquip in DAO " + criticalMedEquip);

		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, -1);
		Date lastYear = cal.getTime();

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MONTH, -3);
		Date threeMonsBefore = cal2.getTime();
		cal2.add(Calendar.MONTH, 6);
		Date threeMonsAfter = cal2.getTime();

		if ("".equals(creationFrom)) {
			creationFrom = DATE_FORMAT.format(lastYear);
		}

		if ("".equals(creationTo)) {
			creationTo = DATE_FORMAT.format(today);
		}

		if ("".equals(scheduleFrom)) {
			scheduleFrom = DATE_FORMAT.format(threeMonsBefore);
		}

		if ("".equals(scheduleTo)) {
			scheduleTo = DATE_FORMAT.format(threeMonsAfter);
		}

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuffer tempQuery = new StringBuffer();

        tempQuery.append("SELECT '6' sqltype, range_code, range_desc,                                  \n" );
        tempQuery.append("       DECODE(range_code,'T', range_sum, range_cnt) as range_cnt             \n" );
        tempQuery.append("FROM                                                                         \n" );
        tempQuery.append("	(select range_code,                                                        \n" );
        tempQuery.append("    range_desc,                                                              \n" );
        tempQuery.append("		range_cnt,                                                             \n" );
        tempQuery.append("		sum(range_cnt) over() as range_sum                                     \n" );
        tempQuery.append("	from                                                                       \n" );
        tempQuery.append("		(                                                                      \n" );
        tempQuery.append("		select                                                                 \n" );
        tempQuery.append("		fv.lookup_code range_code, fv.description range_desc,                  \n" );
        tempQuery.append("		NVL(wo2.range_cnt, 0) range_cnt                                        \n" );
        tempQuery.append("		from                                                                   \n" );
        tempQuery.append("			(select daterange, count(*) range_cnt                              \n" );
        tempQuery.append("			from                                                               \n" );
        tempQuery.append("			(select case when trunc(sysdate) - trunc(wo.scheduled_start_date) > 14 then '1'  \n" );
        tempQuery.append("				when trunc(sysdate) - trunc(wo.scheduled_start_date) > 7 and                 \n" );
        tempQuery.append("                trunc(sysdate) - trunc(wo.scheduled_start_date) <= 14 then '2'             \n" );
        tempQuery.append("				when trunc(sysdate) - trunc(wo.scheduled_start_date)  <= 7 and               \n" );
        tempQuery.append("                trunc(sysdate) - trunc(wo.scheduled_start_date) > 0 then '3'             	 \n" );
        tempQuery.append("				when trunc(wo.scheduled_start_date) - trunc(sysdate) <= 14 then '4'          \n" );
        tempQuery.append("				when trunc(wo.scheduled_start_date) - trunc(sysdate) > 14 then '5'           \n" );
        tempQuery.append("			end daterange                                                      \n" );
        if (creationFrom != null && !"".equals(creationFrom) && creationTo != null && !"".equals(creationTo) &&
				(owningDept==null || "".equals(owningDept)) && (assetLocation == null || "".equals(assetLocation)) && (mBody == null || "".equals(mBody)) && (assetRisk == null || "".equals(assetRisk)) && "false".equals(criticalMedEquip) ) { 
			tempQuery.append("from XXEAM_EXT_WO_SIMPLE_V wo \n");
        }else if ("false".equals(criticalMedEquip) && (assetRisk == null || "".equals(assetRisk)) ) {
			tempQuery.append("			from xxeam_wo_summary_dboard_s_v wo \n");
		}else {
			tempQuery.append("			from xxeam_wo_summary_dboard_v wo \n");
		}
        tempQuery.append("			where wo.work_order_type = '20'                                    \n" );
        tempQuery.append("			and xxeam_maint_body_sec_chk_wo(wo.maintenance_body_number) = 'Y' \n");
		tempQuery.append("			and wo.organization_id in (select organization_id from xxeam_accessible_org_v) \n");
		tempQuery.append("			and wo.owning_department_id in (select department_id from xxeam_accessible_dept_v) \n");
		
		if("true".equals(chart)) {
			tempQuery.append("			and wo_status in ('Unreleased')                                 \n" );
		}
		else {
			tempQuery.append("			and wo_status in ('Released', 'On Hold')                                 \n" );
		}
		
		
		if (dashboardFor != null && !"".equals(dashboardFor)) {
			tempQuery.append("and wo.wo_created_by_user_id = '"+dashboardFor+"' \n");
		}
		if (mBody != null && !"".equals(mBody)) {
			tempQuery.append("and wo.maintenance_body_number = '"+mBody+"' \n");
		}
		if (eamOrg != null && !"".equals(eamOrg)) {
			tempQuery.append("and wo.organization_id = '"+eamOrg+"'  \n");
	    } 
		if (owningDept != null && !"".equals(owningDept)) {
			tempQuery.append("and wo.owning_department_id = (select department_id from xxeam_accessible_dept_v where department_code = '"+owningDept+"') \n");
		}
		if (assetLocation != null && !"".equals(assetLocation)) {
			tempQuery.append("and wo.location_id = (select location_id from hr_locations_all where location_code = '"+assetLocation+"') ");
		}
		if (assetRisk != null && !"".equals(assetRisk)) {
			tempQuery.append("and wo.risk_level = '"+assetRisk+"' \n");
		}				
		
		tempQuery.append("AND trunc(wo.scheduled_start_date) >= TO_DATE('"+scheduleFrom+"', 'DD/MM/YYYY') \n");
		tempQuery.append("AND trunc(wo.scheduled_start_date) <= TO_DATE('"+scheduleTo+"', 'DD/MM/YYYY') \n");
		
		if ("true".equals(criticalMedEquip)) {
			tempQuery.append(" and exists (                                                             \n");
			tempQuery.append("      select 1                                                            \n");
			tempQuery.append("      from fnd_lookup_values f                                            \n");
			tempQuery.append("      where f.lookup_type = 'XXEAM_CRITICAL_MEDICAL_EQUIP'                \n");
			tempQuery.append("      and f.lookup_code = wo.item_code                                    \n");
			tempQuery.append("      and f.enabled_flag = 'Y'                                            \n");
			tempQuery.append("      and trunc(sysdate) between NVL(f.start_date_active, trunc(sysdate)) \n");
			tempQuery.append("      and NVL(f.end_date_active, trunc(sysdate))                          \n");
			tempQuery.append("      )                                                                   \n");
		}		
		
        tempQuery.append("			) temp                                                             \n" );
        tempQuery.append("			group by temp.daterange                                            \n" );
        tempQuery.append("			) wo2,                                                             \n" );
        tempQuery.append("			mfg_lookups_v fv                                                   \n" );
        tempQuery.append("		where fv.lookup_type = 'XXEAM_OS_PM_WORK_ORDERS'                       \n" );
        tempQuery.append("		and   fv.enabled_flag = 'Y'                                            \n" );
        tempQuery.append("		and   TRUNC(SYSDATE) between NVL(fv.start_date_active, TRUNC(SYSDATE)) \n" );
        tempQuery.append("		and   NVL(fv.end_date_active,TRUNC(SYSDATE))                           \n" );
        tempQuery.append("		and   fv.lookup_code = wo2.daterange(+)                                \n" );
        tempQuery.append("		order by fv.lookup_code                                                \n" );
        tempQuery.append("		)                                                                      \n" );
        tempQuery.append("	)" );
		
		StringBuffer sqlQuery = new StringBuffer();

		sqlQuery.append("SELECT * \n");
		sqlQuery.append("FROM TABLE(XXEAM_DASHBOARD_SUMMARY.XXEAM_DASH_PMUPCOME( \n");
		sqlQuery.append(":query,:userID,:respID,:appID))");
		
		paramMap.addValue("query",tempQuery.toString(), java.sql.Types.CLOB);
		paramMap.addValue("userID", userID);
		paramMap.addValue("respID", respID);
		paramMap.addValue("appID", appID);

		logger.info("PM Upcoming Work Orders Stat SQL: " + tempQuery.toString());
		logger.info("dashboardFor:"+dashboardFor);
		logger.info("mBody:"+mBody);
		logger.info("eamOrg:"+eamOrg);
		logger.info("owningDept:"+owningDept);
		logger.info("assetLocation:"+assetLocation);
		logger.info("assetRisk:"+assetRisk);
		logger.info("creationFrom:"+creationFrom);
		logger.info("creationTo:"+creationTo);
		logger.info("criticalMedEquip:"+criticalMedEquip);

		List<DashBoard> listDashBoard = namedParameterJdbcTemplate.query(sqlQuery.toString(), paramMap,
				new RowMapper<DashBoard>() {
			@Override
			public DashBoard mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashBoard aDashBoard = new DashBoard();

				aDashBoard.setDateRange(rs.getString("range_desc"));
				aDashBoard.setUpcomingWoCount(rs.getString("range_cnt"));

				logger.debug("PM Upcoming WO " + aDashBoard.getDateRange());
				logger.debug("PM Upcoming WO Count " + aDashBoard.getUpcomingWoCount());
				return aDashBoard;
			}

		});

		return listDashBoard;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<DashBoard> getTop10MaintBody(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception {

		String dashboardFor = searchCriteria.getDashboardFor();
		String eamOrg = searchCriteria.getEamOrg();
		String owningDept = searchCriteria.getOwningDept();
		String assetLocation = searchCriteria.getAssetLocation();
		String criticalMedEquip = searchCriteria.getCriticalMedEquip();
		String creationFrom = searchCriteria.getCreationFrom();
		String creationTo = searchCriteria.getCreationTo();
		String statusType = searchCriteria.getStatusType();
		String assetRisk = searchCriteria.getRiskLevel();
		String mBody = searchCriteria.getMBody();
		String scheduleFrom = searchCriteria.getScheduleFrom();
		String scheduleTo = searchCriteria.getScheduleTo();
		String module = searchCriteria.getModule();
		logger.debug("criticalMedEquip in DAO " + criticalMedEquip);

		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, -1);
		Date lastYear = cal.getTime();

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MONTH, -3);
		Date threeMonsBefore = cal2.getTime();
		cal2.add(Calendar.MONTH, 6);
		Date threeMonsAfter = cal2.getTime();

		if ("".equals(creationFrom)) {
			creationFrom = DATE_FORMAT.format(lastYear);
		}

		if ("".equals(creationTo)) {
			creationTo = DATE_FORMAT.format(today);
		}

		if ("".equals(scheduleFrom)) {
			scheduleFrom = DATE_FORMAT.format(threeMonsBefore);
		}

		if ("".equals(scheduleTo)) {
			scheduleTo = DATE_FORMAT.format(threeMonsAfter);
		}

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuffer tempQuery = new StringBuffer();

		tempQuery.append("select a.maintenance_body_number as maint_body_number,\n");
		tempQuery.append("             mb.vendor_name as maint_body_name,\n");
		tempQuery.append("            a.maint_body_count as maint_body_count \n");
		tempQuery.append("from  ( \n");
		tempQuery.append("      select maintenance_body_number,\n");
		tempQuery.append("              maint_body_count,\n");
		tempQuery.append("          rank() over (order by maint_body_count desc) as maint_body_rank \n");
		tempQuery.append("          from ( \n");
		tempQuery.append("          select maintenance_body_number, \n");
		tempQuery.append("          count(*) maint_body_count \n");
		// Use simple view for fast access
		if ("false".equals(criticalMedEquip) 
				&& (assetRisk == null || "".equals(assetRisk))
				&& (assetLocation == null || "".equals(assetLocation))) {
			tempQuery.append("from XXEAM_EXT_WO_SIMPLE_V wo \n");
		}else {
			tempQuery.append("from xxeam_wo_summary_dboard_v wo \n");
		}
		tempQuery.append("          where work_order_type = '20' \n");
		
		if (searchCriteria.getPmSummaryDesc() != null && !"".equals(searchCriteria.getPmSummaryDesc())) {
			  if (searchCriteria.getPmSummaryDesc().split(":")[0].equals("Overdue*")) {
				  tempQuery.append("      and  trunc(SCHEDULED_START_DATE) < trunc(sysdate) and wo_status in ('Unreleased') \n"); 
			  } 
			  if (searchCriteria.getPmSummaryDesc().split(":")[0].equals("Upcoming*")) {
				  tempQuery.append("      and  trunc(SCHEDULED_START_DATE) >= trunc(sysdate) and wo_status in ('Unreleased') \n");
			  }
			  if (searchCriteria.getPmSummaryDesc().split(":")[0].equals("Completed")) {
				  tempQuery.append("      and  wo_status in ('Rejected','Cancelled','Cancelled By PM','Complete','Complete - Pending Close','Closed') \n"); 
			  }
			  if (searchCriteria.getPmSummaryDesc().split(":")[0].equals("Total")) {
				  tempQuery.append("      and wo_status in ('Unreleased', 'Rejected', 'Cancelled', 'Cancelled By PM', 'Complete', 'Complete - Pending Close', 'Closed') \n"); 
			  }
		} else {
			String status = searchCriteria.getPmWostatus().split(":")[0];
			if(!"Total".equals(status)) {
				tempQuery.append("          and wo_status = '" + status + "' \n");
			} else {
				tempQuery.append("      and wo_status in ('Unreleased', 'Rejected', 'Cancelled', 'Cancelled By PM', 'Complete', 'Complete - Pending Close', 'Closed') \n"); 
			}
		}
		tempQuery.append("          and xxeam_maint_body_sec_chk_wo(maintenance_body_number) = 'Y'  \n");
		tempQuery.append("			and organization_id in (select organization_id from xxeam_accessible_org_v) \n");
		tempQuery.append("			and owning_department_id in (select department_id from xxeam_accessible_dept_v) \n");
		
		// Do not include maintenance_body_number null case
		tempQuery.append("          and maintenance_body_number is not null  \n");

		if (dashboardFor != null && !"".equals(dashboardFor)) {
			tempQuery.append("AND wo_created_by_user_id = '"+dashboardFor+"' \n");
		}
		if (eamOrg != null && !"".equals(eamOrg)) {
			tempQuery.append("AND ORGANIZATION_ID = '"+eamOrg+"'  \n");
	    } 
		if (owningDept != null && !"".equals(owningDept)) {
			tempQuery.append("AND owning_department_id = (select department_id from xxeam_accessible_dept_v where department_code = '"+owningDept+"') \n");
		}
		if (scheduleFrom != null && !"".equals(scheduleFrom)) {
			tempQuery.append("AND trunc(scheduled_start_date) >= trunc(to_date('"+scheduleFrom+"' , 'dd/mm/yyyy')) \n");
		}
		if (scheduleTo != null && !"".equals(scheduleTo)) {
			tempQuery.append("AND trunc(scheduled_start_date) <= trunc(to_date('"+scheduleTo+"', 'dd/mm/yyyy')) \n");
		}
		
		if (assetLocation != null && !"".equals(assetLocation)) {
			tempQuery.append("AND wo.location_id = (select location_id from hr_locations_all where location_code = '"+assetLocation+"') ");
		}
		if (assetRisk != null && !"".equals(assetRisk)) {
			tempQuery.append(" and wo.risk_level = '"+assetRisk+"' ");
		}
		if ("true".equals(criticalMedEquip)) {
			tempQuery.append(" and exists (                                                             \n");
			tempQuery.append("      select 1                                                            \n");
			tempQuery.append("      from fnd_lookup_values f                                            \n");
			tempQuery.append("      where f.lookup_type = 'XXEAM_CRITICAL_MEDICAL_EQUIP'                \n");
			tempQuery.append("      and f.lookup_code = wo.item_code                                    \n");
			tempQuery.append("      and f.enabled_flag = 'Y'                                            \n");
			tempQuery.append("      and trunc(sysdate) between NVL(f.start_date_active, trunc(sysdate)) \n");
			tempQuery.append("      and NVL(f.end_date_active, trunc(sysdate))                          \n");
			tempQuery.append("      )                                                                   \n");
		}
		
		tempQuery.append("          group by maintenance_body_number  \n");
		tempQuery.append("          )) a,  \n");
		tempQuery.append("          xxeam_maintenance_body_dff_v mb \n");
		tempQuery.append("where a.maint_body_rank <= 10 and rownum <= 10 \n");
		tempQuery.append("and a.maintenance_body_number = mb.vendor_number(+) \n");
		tempQuery.append("order by maint_body_count desc  \n");

		StringBuffer sqlQuery = new StringBuffer();

		sqlQuery.append("SELECT * \n");
		sqlQuery.append("FROM TABLE(XXEAM_DASHBOARD_SUMMARY.XXEAM_DASH_MAINBODY( \n");
		sqlQuery.append(":query,:userID,:respID,:appID))");
		
		paramMap.addValue("query",tempQuery.toString(), java.sql.Types.CLOB);
		paramMap.addValue("userID", userID);
		paramMap.addValue("respID", respID);
		paramMap.addValue("appID", appID);

		logger.info("Top 10 Maintenance Body SQL: " + tempQuery.toString());

		List<DashBoard> listDashBoard = namedParameterJdbcTemplate.query(sqlQuery.toString(), paramMap,
				new RowMapper<DashBoard>() {
			@Override
			public DashBoard mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashBoard aDashBoard = new DashBoard();

				aDashBoard.setMaintenanceVendor(rs.getString("maint_body_name"));
				aDashBoard.setMaintenanceVendorNumber(rs.getString("maint_body_number"));
				aDashBoard.setMaintenanceVendorCount(rs.getString("maint_body_count"));

				logger.debug("Maintenance vendor name " + aDashBoard.getMaintenanceVendor());
				logger.debug("Maintenance vendor number " + aDashBoard.getMaintenanceVendorNumber());
				logger.debug("Maintenance vendor count " + aDashBoard.getMaintenanceVendorCount());
				return aDashBoard;
			}

		});
		int count = 0;
		if (searchCriteria.getPmSummaryDesc() != null 
				&& !"".equals(searchCriteria.getPmSummaryDesc())) {
			count = Integer.parseInt(searchCriteria.getPmSummaryDesc().split(":")[1]);
		} 
		if (searchCriteria.getPmWostatus() != null 
				&& !"".equals(searchCriteria.getPmWostatus())) {
			count = Integer.parseInt(searchCriteria.getPmWostatus().split(":")[1]);
		}
		int total = 0;
		for (DashBoard temp: listDashBoard) {
			total = total + Integer.parseInt(temp.getMaintenanceVendorCount());
		}
		if ((searchCriteria.getPmSummaryDesc() != null 
				&& !"".equals(searchCriteria.getPmSummaryDesc())) ||
				(searchCriteria.getPmWostatus() != null 
				&& !"".equals(searchCriteria.getPmWostatus()))) {
			if ( total < count) {
				DashBoard Others = new DashBoard();
				int other = count - total;
				Others.setMaintenanceVendor("Others");
				Others.setMaintenanceVendorNumber("");
				Others.setMaintenanceVendorCount(Integer.toString(other));
				listDashBoard.add(Others);
			}
		}  

		return listDashBoard;
	}

	@Override
	public List<Dropdown> getRiskLevelList() throws Exception {
		String sql = "select ffvl.flex_value as flex_value, ffvl.description as description from fnd_flex_value_sets ffvs, fnd_flex_values_vl ffvl where ffvs.flex_value_set_name = 'HA_RISK' and ffvs.flex_value_set_id = ffvl.flex_value_set_id order by ffvl.flex_value";

		List<Dropdown> listDropdown = jdbcTemplate.query(sql, new RowMapper<Dropdown>() {

			@Override
			public Dropdown mapRow(ResultSet rs, int rowNum) throws SQLException {
				Dropdown aDropdown = new Dropdown();

				aDropdown.setName(rs.getString("flex_value"));
				aDropdown.setDesc(rs.getString("description"));
				return aDropdown;
			}

		});

		return listDropdown;

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<DashBoard> getWoTrendCM(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception {
		String dashboardFor = searchCriteria.getDashboardFor();
		String eamOrg = searchCriteria.getEamOrg();
		String owningDept = searchCriteria.getOwningDept();
		String assetLocation = searchCriteria.getAssetLocation();
		
		StringBuffer sqlQuery = new StringBuffer();

		sqlQuery.append("SELECT /*+ index(LOC_ATTR XXMTL_EAM_ASSET_ATTR_VAL_N1) */ TO_CHAR(TRUNC(to_date(OPERATIONINFO.ATTRIBUTE1, 'yyyy/mm/dd HH24:MI:SS'),'MONTH'), 'MON') as MONTHTIME,COUNT(1) AS CNT  \n");
		sqlQuery.append("FROM WIP_DISCRETE_JOBS WDJ, WIP_OPERATIONS OPERATIONINFO, MTL_EAM_ASSET_ATTR_VALUES LOC_ATTR, HR_LOCATIONS HL \n");
		sqlQuery.append("WHERE WDJ.WIP_ENTITY_ID = OPERATIONINFO.WIP_ENTITY_ID \n");
		sqlQuery.append("AND WDJ.ORGANIZATION_ID = OPERATIONINFO.ORGANIZATION_ID \n");
		sqlQuery.append("AND WDJ.MAINTENANCE_OBJECT_ID   = LOC_ATTR.MAINTENANCE_OBJECT_ID(+) \n");
		sqlQuery.append("AND LOC_ATTR.C_ATTRIBUTE1 = TO_CHAR(HL.LOCATION_ID(+)) \n");
		sqlQuery.append("AND xxeam_maint_body_sec_chk_wo(WDJ.ATTRIBUTE1) = 'Y' \n");
		sqlQuery.append("AND WDJ.organization_id IN (SELECT organization_id FROM xxeam_accessible_org_v) \n");
		sqlQuery.append("AND NVL(WDJ.WORK_ORDER_TYPE,'10') = '10' \n");
		
		if (dashboardFor != null && !"".equals(dashboardFor)) {
			sqlQuery.append("AND WDJ.LAST_UPDATED_BY = '"+dashboardFor+"' \n");
		}
		if (eamOrg != null && !"".equals(eamOrg)) {
			sqlQuery.append("and WDJ.organization_id = '"+eamOrg+"'  \n");
	    } 
		if (owningDept != null && !"".equals(owningDept)) {
			sqlQuery.append("AND WDJ.owning_department = (select department_id from xxeam_accessible_dept_v where department_code = '"+owningDept+"') \n");
		}
		if (assetLocation != null && !"".equals(assetLocation)) {
			sqlQuery.append("AND HL.LOCATION_CODE = '"+assetLocation+"' ");
		}
		
		sqlQuery.append("AND LOC_ATTR.ATTRIBUTE_CATEGORY(+)   = 'Location' \n");
		sqlQuery.append("AND to_date(OPERATIONINFO.ATTRIBUTE1, 'yyyy/mm/dd HH24:MI:SS') >= trunc(add_months( sysdate, -11 ), 'Month') \n");
		sqlQuery.append("AND to_date(OPERATIONINFO.ATTRIBUTE1, 'yyyy/mm/dd HH24:MI:SS') <= sysdate \n");
		sqlQuery.append("GROUP BY TRUNC(to_date(OPERATIONINFO.ATTRIBUTE1, 'yyyy/mm/dd HH24:MI:SS'),'MONTH') \n");
		sqlQuery.append("ORDER BY TRUNC(to_date(OPERATIONINFO.ATTRIBUTE1, 'yyyy/mm/dd HH24:MI:SS'),'MONTH') ASC \n");
		
		StringBuffer query = new StringBuffer();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		
		query.append("SELECT * \n");
		query.append("FROM TABLE(XXEAM_DASHBOARD_SUMMARY.XXEAM_DASH_WOTREND( \n");
		query.append(":query,:userID,:respID,:appID))");
		
		paramMap.addValue("query",sqlQuery.toString(), java.sql.Types.CLOB);
		paramMap.addValue("userID", userID);
		paramMap.addValue("respID", respID);
		paramMap.addValue("appID", appID);

		List<DashBoard> listDashBoard = namedParameterJdbcTemplate.query(sqlQuery.toString(), paramMap,
				new RowMapper<DashBoard>() {

			@Override
			public DashBoard mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashBoard aDashBoard = new DashBoard();
				
				aDashBoard.setTrendMonth(rs.getString("MONTHTIME"));
				aDashBoard.setTrendCount(rs.getString("CNT"));
				
				return aDashBoard;
			}

		});

		return listDashBoard;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<DashBoard> getWoTrendPM(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception {
		String dashboardFor = searchCriteria.getDashboardFor();
		String eamOrg = searchCriteria.getEamOrg();
		String owningDept = searchCriteria.getOwningDept();
		String assetLocation = searchCriteria.getAssetLocation();
		
		StringBuffer sqlQuery = new StringBuffer();

		sqlQuery.append("SELECT /*+ index(LOC_ATTR XXMTL_EAM_ASSET_ATTR_VAL_N1) */ TO_CHAR(TRUNC(WDJ.scheduled_start_date,'MONTH'), 'MON') as MONTHTIME,COUNT(1) AS CNT \n");
		sqlQuery.append("FROM WIP_DISCRETE_JOBS WDJ, MTL_EAM_ASSET_ATTR_VALUES LOC_ATTR, HR_LOCATIONS HL \n");
		sqlQuery.append("WHERE WDJ.MAINTENANCE_OBJECT_ID   = LOC_ATTR.MAINTENANCE_OBJECT_ID(+) \n");
		sqlQuery.append("AND LOC_ATTR.C_ATTRIBUTE1 = TO_CHAR(HL.LOCATION_ID(+)) \n");
		sqlQuery.append("AND xxeam_maint_body_sec_chk_wo(WDJ.ATTRIBUTE1) = 'Y' \n");
		sqlQuery.append("AND WDJ.organization_id IN (SELECT organization_id FROM xxeam_accessible_org_v) \n");
		sqlQuery.append("AND WDJ.WORK_ORDER_TYPE = '20' \n");
		
		
		if (dashboardFor != null && !"".equals(dashboardFor)) {
			sqlQuery.append("AND WDJ.LAST_UPDATED_BY = '"+dashboardFor+"' \n");
		}
		if (eamOrg != null && !"".equals(eamOrg)) {
			sqlQuery.append("and WDJ.organization_id = '"+eamOrg+"'  \n");
	    } 
		if (owningDept != null && !"".equals(owningDept)) {
			sqlQuery.append("and WDJ.owning_department  = (select department_id from xxeam_accessible_dept_v where department_code = '"+owningDept+"') \n");
		}
		if (assetLocation != null && !"".equals(assetLocation)) {
			sqlQuery.append("ANd HL.LOCATION_CODE = '"+assetLocation+"' ");
		}
		
		sqlQuery.append("AND LOC_ATTR.ATTRIBUTE_CATEGORY(+)   = 'Location' \n");
		sqlQuery.append("AND WDJ.scheduled_start_date >= trunc(add_months( sysdate, -11 ), 'Month') \n");
		sqlQuery.append("AND WDJ.scheduled_start_date <= sysdate \n");
		sqlQuery.append("GROUP BY TRUNC(WDJ.scheduled_start_date,'MONTH') \n");
		sqlQuery.append("ORDER BY TRUNC(WDJ.scheduled_start_date,'MONTH') ASC \n");
				
		StringBuffer query = new StringBuffer();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		
		query.append("SELECT * \n");
		query.append("FROM TABLE(XXEAM_DASHBOARD_SUMMARY.XXEAM_DASH_WOTREND( \n");
		query.append(":query,:userID,:respID,:appID))");
		
		paramMap.addValue("query",sqlQuery.toString(), java.sql.Types.CLOB);
		paramMap.addValue("userID", userID);
		paramMap.addValue("respID", respID);
		paramMap.addValue("appID", appID);

		List<DashBoard> listDashBoard = namedParameterJdbcTemplate.query(sqlQuery.toString(), paramMap,
				new RowMapper<DashBoard>() {
			@Override
			public DashBoard mapRow(ResultSet rs, int rowNum) throws SQLException {
				DashBoard aDashBoard = new DashBoard();
				
				aDashBoard.setTrendMonth(rs.getString("MONTHTIME"));
				aDashBoard.setTrendCount(rs.getString("CNT"));
				
				return aDashBoard;
			}

		});

		return listDashBoard;
	}
}
