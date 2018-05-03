/* --------------------------------------
    File Name: WorkOrderDao.java
    Author: Fanny Hung (PCCW)
    Date: 10-Aug-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Lov Search Function Implementation

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.3>		20171214	Carmen Ng	Added contactMethod
	<1.2>		20171208	Carmen Ng	Added vendor name to contract lov
	<1.1>		20171117	Carmen Ng	Added checkValidity and modified searchKey to return match or no match
	<1.0>		20170815	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.eam.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import hk.org.ha.eam.model.SearchLov;
import hk.org.ha.eam.model.SearchLovResult;
import hk.org.ha.eam.model.ContactMethodResult;

@Repository
public class SearchLovDaoImpl implements SearchLovDao {

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;   
	private static final Logger logger = Logger.getLogger(SearchLovDaoImpl.class);
	
    private static final String LOV_SQL[] = {
            "select vendor_number, (case when enabled_flag='N' then '(Inactivation)' end)||vendor_name vendor_name, '' locationCode,'' description1,'' address, ''assetOwner, '' description2, '' department_code, '' description3, '' user_name, '' full_name, '' assetNumber, '' assetDescription, '' contract_num from xxeam_maintenance_body_dff_v where 1=1",
            //"SELECT '' vendor_number,'' vendor_name,loc.location_code locationCode, loc.description description1, loc.address_line_2 || ' ' || loc.address_line_3 AS address, ''assetOwner, '' description2, '' department_code, '' description3, '' user_name, '' full_name, '' assetNumber, '' assetDescription, '' contract_num FROM hr_locations loc where loc.inventory_organization_id not in (SELECT mp.organization_id FROM apps.mtl_parameters mp, apps.hr_organization_units ou WHERE ou.organization_id = mp.organization_id AND NVL (ou.attribute6, 'XX') LIKE 'DRUGS') and loc.location_code like :location_code order by locationCode",
            "SELECT '' vendor_number,'' vendor_name,loc.location_code locationCode, (CASE WHEN location_code = (SELECT organization_code FROM mtl_parameters WHERE organization_code = location_code) THEN '(Inactivation)' WHEN NVL (TRUNC (inactive_date), SYSDATE + 1) <= TRUNC (SYSDATE) THEN '(Inactivation)' END )||loc.description description1, loc.address_line_2 || ' ' || loc.address_line_3 AS address, ''assetOwner, '' description2, '' department_code, '' description3, '' user_name, '' full_name, '' assetNumber, '' assetDescription, '' contract_num FROM hr_locations loc where loc.inventory_organization_id not in (SELECT mp.organization_id FROM apps.mtl_parameters mp, apps.hr_organization_units ou WHERE ou.organization_id = mp.organization_id AND NVL (ou.attribute6, 'XX') LIKE 'DRUGS') and loc.location_code like :location_code order by locationCode",
            "select '' vendor_number,'' vendor_name, '' locationCode, '' description1, '' address, ffv.flex_value assetOwner, ffv.description description2, '' department_code, '' description3, '' user_name, '' full_name, '' assetNumber, '' assetDescription, '' contract_num from fnd_flex_value_sets ffvs, fnd_flex_values_vl ffv where ffvs.flex_value_set_id = ffv.flex_value_set_id and flex_value_set_name = 'XXEAM_ASSET_OWNER' AND (:flex_value is null or ffv.flex_value like :flex_value)  order by assetOwner",
            "SELECT * FROM (select '' vendor_number,'' vendor_name, '' locationCode, '' description1, '' address, ''assetOwner, '' description2, department_code, description description3, '' user_name, '' full_name, '' assetNumber, '' assetDescription, '' contract_num from xxeam_accessible_dept_v where nvl(disable_date, sysdate+1) >= sysdate AND organization_id IN (SELECT organization_id FROM XXEAM_ACCESSIBLE_ORG_V)) QRSLT WHERE ((:department_code is null or department_code like :department_code) AND (:description3 is null or description3 like :description3) ) ORDER BY department_code",
            "SELECT '' vendor_number, '' vendor_name, '' locationCode,'' description1,'' address, ''assetOwner, '' description2, '' department_code, '' description3, user_name, full_name, '' assetNumber, '' assetDescription, '' contract_num FROM fnd_user fu, (select * FROM per_all_people_f WHERE employee_number is not null AND trunc ( sysdate ) between effective_start_date AND effective_end_date ) pe WHERE fu.employee_id = pe.person_id(+)",
            "SELECT '' vendor_number, '' vendor_name, '' locationCode, '' description1, '' address, ''assetOwner, '' description2, '' department_code, '' description3, '' user_name, '' full_name, asset_number assetNumber, asset_description assetDescription, '' contract_num FROM XXEAM_EXT_ASSET_NUMBERS_V WHERE INV_ORGANIZATION_ID IN (select organization_id from xxeam_accessible_org_v) AND OWNING_DEPARTMENT_ID IN (SELECT department_id FROM xxeam_accessible_dept_v) AND ASSET_NUMBER LIKE :assetNumber ORDER BY ASSET_NUMBER",
            "",
            //"SELECT '' vendor_number, '' vendor_name, '' locationCode, '' description1, '' address, ''assetOwner, '' description2, '' department_code, '' description3, '' user_name, '' full_name, cii.INSTANCE_NUMBER assetNumber, cii.instance_description assetDescription, '' contract_num FROM mtl_parameters mp, eam_org_maint_defaults eomd, csi_item_instances cii, MTL_EAM_ASSET_ATTR_VALUES av WHERE eomd.object_type (+)= 50 AND mp.organization_id = cii.last_vld_organization_id AND cii.instance_id = av.maintenance_object_id AND eomd.object_id (+)= cii.instance_id AND eomd.organization_id (+)= cii.last_vld_organization_id AND av.ATTRIBUTE_CATEGORY = 'Maintenance Details' /*and xxeam_maint_body_sec_chk_wo(av.maintenance_body) = 'Y'*/ AND (:assetNumber IS NULL OR cii.INSTANCE_NUMBER LIKE :assetNumber) AND mp.organization_id IN (SELECT organization_id FROM xxeam_accessible_org_v) AND (fnd_global.resp_name not like '%EAM Maintenance User (IT Asset)' or (fnd_global.resp_name like '%EAM Maintenance User (IT Asset)' and eomd.owning_department_id IN (SELECT department_id FROM xxeam_accessible_dept_v )) ) /*AND TRUNC(sysdate)<=TRUNC(NVL(av.D_ATTRIBUTE2,sysdate))*/ ORDER BY cii.INSTANCE_NUMBER",
            //"SELECT '' vendor_number, '' vendor_name, '' locationCode, '' description1, '' address, ''assetOwner, '' description2, '' department_code, '' description3, '' user_name, '' full_name, cii.INSTANCE_NUMBER assetNumber, cii.instance_description assetDescription, '' contract_num FROM mtl_parameters mp, eam_org_maint_defaults eomd, csi_item_instances cii, MTL_EAM_ASSET_ATTR_VALUES av, MTL_EAM_ASSET_ATTR_VALUES ASSET_ATTR WHERE eomd.object_type (+)= 50 AND mp.organization_id = cii.last_vld_organization_id AND cii.instance_id = av.maintenance_object_id AND eomd.object_id (+)= cii.instance_id AND eomd.organization_id (+)= cii.last_vld_organization_id AND cii.instance_id  = ASSET_ATTR.MAINTENANCE_OBJECT_ID (+) AND av.ATTRIBUTE_CATEGORY = 'Maintenance Details' AND ASSET_ATTR.ATTRIBUTE_CATEGORY (+) = 'Asset Details' AND SUBSTR(ASSET_ATTR.C_ATTRIBUTE2,1,8) <> 'Inactive' and ASSET_ATTR.C_ATTRIBUTE2 <> 'Interim-Transfer In Progress' and xxeam_maint_body_sec_chk_wo(av.C_ATTRIBUTE7) = 'Y' AND (:assetNumber IS NULL OR cii.INSTANCE_NUMBER LIKE :assetNumber) AND mp.organization_id IN (SELECT organization_id FROM xxeam_accessible_org_v) AND (fnd_global.resp_name not like '%EAM Maintenance User (IT Asset)' or (fnd_global.resp_name like '%EAM Maintenance User (IT Asset)' and eomd.owning_department_id IN (SELECT department_id FROM xxeam_accessible_dept_v )) ) AND TRUNC(NVL(av.D_ATTRIBUTE2,sysdate))<=TRUNC(sysdate) AND ( (fnd_global.resp_name NOT LIKE '%EAM In-House Maintenance User (Non-IT Asset)' and fnd_global.resp_name NOT LIKE '%EAM Third-Party Maintenance User (Non-IT Asset)') or ((fnd_global.resp_name LIKE '%EAM In-House Maintenance User (Non-IT Asset)' or fnd_global.resp_name LIKE '%EAM Third-Party Maintenance User (Non-IT Asset)') and (CASE WHEN av.D_ATTRIBUTE1 IS NULL AND av.D_ATTRIBUTE2 IS NULL AND av.D_ATTRIBUTE3 IS NULL AND av.D_ATTRIBUTE4 IS NULL THEN NULL WHEN TRUNC(SYSDATE) BETWEEN av.D_ATTRIBUTE1 AND av.D_ATTRIBUTE2 THEN 'WARRANTY' WHEN TRUNC(SYSDATE) BETWEEN av.D_ATTRIBUTE1 AND NVL(av.D_ATTRIBUTE2,NVL(av.D_ATTRIBUTE3-1,TO_DATE('31/12/4712','DD/MM/YYYY'))) THEN 'WARRANTY' WHEN TRUNC(SYSDATE) BETWEEN NVL(av.D_ATTRIBUTE1,TO_DATE('01/01/1951','DD/MM/YYYY')) AND av.D_ATTRIBUTE2 THEN 'WARRANTY' WHEN TRUNC(SYSDATE) BETWEEN av.D_ATTRIBUTE3 AND av.D_ATTRIBUTE4 THEN 'MAINTENANCE' WHEN TRUNC(SYSDATE) BETWEEN av.D_ATTRIBUTE3 AND NVL(av.D_ATTRIBUTE4,TO_DATE('31/12/4712','DD/MM/YYYY'))THEN 'MAINTENANCE' WHEN TRUNC(SYSDATE) BETWEEN NVL(av.D_ATTRIBUTE3,NVL(av.D_ATTRIBUTE2+1,TO_DATE('01/01/1951','DD/MM/YYYY'))) AND av.D_ATTRIBUTE4 THEN 'MAINTENANCE' ELSE NULL END) is not null) ) ORDER BY cii.INSTANCE_NUMBER",
            "SELECT '' vendor_number, '' vendor_name, '' locationCode,'' description1,'' address, ''assetOwner, '' description2, '' department_code, '' description3, user_name, full_name, '' assetNumber, '' assetDescription, '' contract_num From fnd_user u, Per_people_x p Where u.employee_id = p.person_id (+)And nvl(p.current_employee_flag ,'Y') = 'Y' And trunc(sysdate) >=  nvl(u.start_date, trunc(sysdate))  and trunc(sysdate)<=  nvl(u.end_date-1, trunc(sysdate))",
            "SELECT DISTINCT '' vendor_number, S.VENDOR_NAME, '' locationCode, '' description1, '' address, ''assetOwner, '' description2, '' department_code, '' description3, '' user_name, '' full_name, '' assetNumber, '' assetDescription, H.SEGMENT1 contract_num FROM PO_HEADERS_ALL H, AP_SUPPLIERS S WHERE S.VENDOR_ID = H.VENDOR_ID AND H.TYPE_LOOKUP_CODE = 'BLANKET' AND H.ATTRIBUTE8 = 'E' and h.segment1 like :contract_num ORDER BY H.SEGMENT1",
			"select vendor_number, vendor_name, '' locationCode,'' description1,'' address, ''assetOwner, '' description2, '' department_code, '' description3, '' user_name, '' full_name, '' assetNumber, '' assetDescription, '' contract_num from xxeam_maintenance_body_dff_v where 1=1"
        };

    private static final String ASSET_LOV_SQL[] = {
    		"SELECT '' vendor_number, '' vendor_name, '' locationCode, '' description1, '' address, ''assetOwner, '' description2, '' department_code, '' description3, '' user_name, '' full_name, asset_number assetNumber, asset_description assetDescription, '' contract_num  FROM XXEAM_EXT_MAINT_ASSET_INFO EE,MTL_EAM_ASSET_ATTR_VALUES AA WHERE EE.MAINTENANCE_OBJECT_ID       = AA.MAINTENANCE_OBJECT_ID (+) AND EE.ASSET_ORGANIZATION_ID IN (select organization_id from xxeam_accessible_org_v)  AND EE.OWNING_DEPARTMENT_ID IN (SELECT department_id FROM xxeam_accessible_dept_v) AND xxeam_maint_body_sec_chk_wo(maintenance_body_num) = 'Y' AND MAINTENANCE_BODY_TYPE IS NOT NULL AND EE.MAINTAINABLE_FLAG = 'Y' AND NVL(SUBSTR(AA.C_ATTRIBUTE2,1,8),'Active') <> 'Inactive' AND AA.C_ATTRIBUTE2 <> 'Interim-Transfer In Progress' AND AA.ATTRIBUTE_CATEGORY (+) = 'Asset Details' AND EE.ASSET_NUMBER LIKE :assetNumber ORDER BY ASSET_NUMBER",
    		"SELECT '' vendor_number, '' vendor_name, '' locationCode, '' description1, '' address, ''assetOwner, '' description2, '' department_code, '' description3, '' user_name, '' full_name, asset_number assetNumber, asset_description assetDescription, '' contract_num  FROM XXEAM_EXT_ASSET_NUMBERS_V EA,MTL_EAM_ASSET_ATTR_VALUES AA WHERE EA.MAINTENANCE_OBJECT_ID       = AA.MAINTENANCE_OBJECT_ID (+) AND AA.ATTRIBUTE_CATEGORY (+) = 'Asset Details' AND EA.INV_ORGANIZATION_ID IN (select organization_id from xxeam_accessible_org_v)  AND EA.OWNING_DEPARTMENT_ID IN (SELECT department_id FROM xxeam_accessible_dept_v) AND EA.MAINTAINABLE_FLAG = 'Y' AND NVL(SUBSTR(AA.C_ATTRIBUTE2,1,8),'Active')  <> 'Inactive' AND AA.C_ATTRIBUTE2 <> 'Interim-Transfer In Progress' AND EA.ASSET_NUMBER LIKE :assetNumber ORDER BY ASSET_NUMBER",
    		"",
    		""
    };
    
	@Autowired
	private DataSource dataSource;
	@Autowired
	private WorkRequestDao workRequestDao;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

    public SearchLovDaoImpl(DataSource dataSource) {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    
    @Override
    public List<SearchLovResult> searchLov(SearchLov searchCriteria,String userID,String respID,String appID) {
   	
        int query = searchCriteria.getQuery();
        int type = searchCriteria.getType();
        String value = searchCriteria.getValue();
        logger.debug("value " + value);
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        
//        String[] lovSQL = Arrays.copyOf(LOV_SQL, LOV_SQL.length);
//        String tempQuery=lovSQL[query].toString();
      String tempQuery=LOV_SQL[query].toString();
    	if (query == 0 || query == 9) {
    		if(type == 1) {
    			tempQuery+=" AND vendor_name like '%"+value.toUpperCase()+"%' ";
    		}
    		else {
    			tempQuery+=" AND vendor_number like '%"+value.toUpperCase()+"%' ";
    		}
    		
    		if(query == 9) {
    			tempQuery+=" and enabled_flag = 'Y'";
    		}
			tempQuery+=" order by vendor_number";
    		logger.info("gatest type query == 0,9");
    	} else if (query == 1) {
    		tempQuery=tempQuery.replaceAll(":location_code", (type == 1)?("'%"+value.toUpperCase()+"%'"):"'%'");
    		logger.info("gatest type query == 1");
    	} else if (query == 2) {
    		tempQuery=tempQuery.replaceAll(":flex_value", (type == 1)?("'%"+value.toUpperCase()+"%'"):"'%'");
    		logger.info("gatest type query == 2");
    	} else if (query == 3) {
    		tempQuery=tempQuery.replaceAll(":department_code", (type == 1)?("'%"+value.toUpperCase()+"%'"):"'%'");
    		tempQuery=tempQuery.replaceAll(":description3", (type == 2)?("'%"+value.toUpperCase()+"%'"):"'%'");
    		logger.info("gatest type query == 3");
    	} else if (query == 4){
    		if(type == 1) {
    			tempQuery+=" AND full_name like '%"+value.toUpperCase()+"%' ";
    		}
    		else {
    			tempQuery+=" AND user_name like '%"+value.toUpperCase()+"%' ";
    		}
    		
    		tempQuery+=" ORDER BY user_name";
    		logger.info("gatest type query == 4");
    	} else if (query == 5){
    		tempQuery=tempQuery.replaceAll(":assetNumber", (type == 1)?("'%"+value.toUpperCase()+"%'"):"'%'");
    		logger.info("gatest type query == 5");
    	} else if (query == 7){
    		if(type == 1) {
    			tempQuery+=" AND full_name like '%"+value.toUpperCase()+"%' ";
    		}
    		else {
    			tempQuery+=" AND user_name like '%"+value.toUpperCase()+"%' ";
    		}
    		
    		tempQuery+=" ORDER BY user_name";
    		logger.info("gatest type query == 7");
    	} else if (query == 6){
    		if(workRequestDao.getAssetAttrRespType()==2) {
    			tempQuery=ASSET_LOV_SQL[0].toString();
    		}
    		else {
    			tempQuery=ASSET_LOV_SQL[1].toString();
    		}
    		
    		tempQuery=tempQuery.replaceAll(":assetNumber", (type == 1)?("'%"+value.toUpperCase()+"%'"):"'%'");
    		logger.info("gatest type query == 6");
    	} else if (query == 8){
    		logger.info("gatest type query == 8");
    		tempQuery=tempQuery.replaceAll(":contract_num", (type == 1)?("'%"+value.toUpperCase()+"%'"):"'%'");
    	}

		StringBuffer sqlQuery = new StringBuffer();

		logger.info("gatest value.toUpperCase : " +value.toUpperCase());
		logger.info("gatest query 2 : " +tempQuery);
		
		sqlQuery.append("SELECT * \n");
		sqlQuery.append("FROM TABLE(XXEAM_SEARCH.XXEAM_SEARCH_LOV( \n");
		sqlQuery.append(":query,:userID,:respID,:appID))");
		
		paramMap.addValue("query",tempQuery, java.sql.Types.CLOB);
		paramMap.addValue("userID", userID);
		paramMap.addValue("respID", respID);
		paramMap.addValue("appID", appID);

		logger.info("SEARCH userID : " +userID);
		logger.info("SEARCH respID : " +respID);
		logger.info("SEARCH appID : " +appID);
		logger.info("SEARCH LOV SQL : " +tempQuery.toString());
		
        List<SearchLovResult> listSearchLovResult = namedParameterJdbcTemplate.query(sqlQuery.toString(), paramMap , new RowMapper<SearchLovResult>() {
     
            @Override
            public SearchLovResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            	SearchLovResult aSearchLovResult = new SearchLovResult();
            	
	            aSearchLovResult.setVendor_number(rs.getString("vendor_number"));
	            aSearchLovResult.setVendor_name(rs.getString("vendor_name"));
            	aSearchLovResult.setLocationcode(rs.getString("locationCode"));
            	aSearchLovResult.setDescription1(rs.getString("description1"));     
            	aSearchLovResult.setAddress(rs.getString("address"));
            	aSearchLovResult.setAssetowner(rs.getString("assetOwner"));
            	aSearchLovResult.setDescription2(rs.getString("description2"));            	
            	aSearchLovResult.setDepartment_code(rs.getString("department_code"));
            	aSearchLovResult.setDescription3(rs.getString("description3"));
            	aSearchLovResult.setUser_name(rs.getString("user_name"));
            	aSearchLovResult.setFull_name(rs.getString("full_name"));
            	aSearchLovResult.setAssetNumber(rs.getString("assetNumber"));
            	aSearchLovResult.setAssetDescription(rs.getString("assetDescription"));
            	aSearchLovResult.setContract_num(rs.getString("contract_num"));
            	logger.debug("ASSET " + aSearchLovResult.getAssetNumber());
                return aSearchLovResult;
            }
     
        });
        
        return listSearchLovResult;   
        
    }

    @Override
    public String searchKey(String value, int query) {
    	StringBuilder sqlBuilder = new StringBuilder();
    	if(query == 0 || query == 9) {
    		sqlBuilder.append("SELECT ")
            .append("vendor_number, VENDOR_NAME FROM xxeam_maintenance_body_dff_v ").append("WHERE ")
            .append("VENDOR_NAME = :searchValue ");
    	}
    	else if(query == 1) {
    		sqlBuilder.append("SELECT ")
            .append("user_name, full_name From fnd_user u, Per_people_x p ").append("WHERE ")
            .append("u.employee_id = p.person_id (+)And nvl(p.current_employee_flag ,'Y') = 'Y' And trunc(sysdate) >=  nvl(u.start_date, trunc(sysdate))  and trunc(sysdate)<=  nvl(u.end_date-1, trunc(sysdate)) AND ")
            .append("(full_name = :searchValue OR user_name = :searchValue)");
    	}
    	else if(query == 2) {
    		sqlBuilder.append("SELECT ")
            .append("distinct STATUS_ID, WORK_ORDER_STATUS from EAM_WO_STATUSES_V ").append("WHERE ")
            .append("WORK_ORDER_STATUS = :searchValue");
    	}
        

        String sql = sqlBuilder.toString();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("searchValue", value);
        
        String result = namedParameterJdbcTemplate.query(sql, paramMap,
        		new ResultSetExtractor<String>() {
                public String extractData(ResultSet rs)
                                throws SQLException, DataAccessException {
                		int rowCount = 0;
                		String key = "";
                		if (rs != null) {
                			while (rs.next()) {
                				key = rs.getString(1);
                				rowCount++;
                			}
												 
                			if (rowCount == 1) {
            					return key;
                			}
                			return "No Match";
                		}
                		return "No Match";
                	}
        	});
        return result;
    }
    
	@Override
    public String checkValidity(String value, int query) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
                
    	if (query == 0) {
        	paramMap.addValue("vendor_name", value.toUpperCase());
    		paramMap.addValue("vendor_number", "%");
    	} else if (query == 1) {
    		paramMap.addValue("location_code", value.toUpperCase());
    	} else if (query == 2) {
    		paramMap.addValue("flex_value", value.toUpperCase());
    	} else if (query == 3) {
    		paramMap.addValue("department_code", value.toUpperCase());
    		paramMap.addValue("description3", "%");
    	} else if (query == 4){
    		paramMap.addValue("full_name", value.toUpperCase());
    		paramMap.addValue("user_name", "%");
    	} else if (query == 5){
    		paramMap.addValue("assetNumber", value.toUpperCase());
    	} else if (query == 7){
    		paramMap.addValue("full_name", value.toUpperCase());
    		paramMap.addValue("user_name", "%");
    	} else if (query == 6){
    		paramMap.addValue("assetNumber", value.toUpperCase());
    	} else if (query == 8){
    		paramMap.addValue("contract_num", value.toUpperCase());
    	}
        
        String result = namedParameterJdbcTemplate.query(LOV_SQL[query], paramMap,
        		new ResultSetExtractor<String>() {
                public String extractData(ResultSet rs)
                                throws SQLException, DataAccessException {
                		int rowCount = 0;
                		if (rs != null) {
                			while (rs.next()) {
                				rowCount++;
                			}
                			
                			if (rowCount == 1) {
            					return "Match";
                			}
                			return "No Match";
                		}
                		return "No Match";
                	}
        	});
        return result;
    }
	 @Override
	    public List<ContactMethodResult> contactMethod(String maintenanceNumber) {
	        StringBuilder sqlBuilder = new StringBuilder();
	        sqlBuilder.append("SELECT ")
	        .append("H.ATTRIBUTE13 AUTO_SEND, H.ATTRIBUTE9 CONTACT_PERSON, H.ATTRIBUTE10 PHONE, H.ATTRIBUTE11 FAX, H.ATTRIBUTE12 EMAIL FROM PO_HEADERS_ALL H, AP_SUPPLIERS S ")
	        .append("WHERE ")
	        .append("H.VENDOR_ID = S.VENDOR_ID AND H.ATTRIBUTE8 = 'E' AND H.TYPE_LOOKUP_CODE = 'BLANKET' AND S.SEGMENT1 = :MAINT_BODY_NUMBER AND EXISTS ")
	        .append("(SELECT 1 FROM XXEAM_MAINT_CONTRACT C, XXEAM_CONTRACT_ASSO A WHERE C.PO_HEADER_ID = H.PO_HEADER_ID AND C.PO_LINE_ID = A.PO_LINE_ID AND TRUNC(SYSDATE) BETWEEN A.ASSOCIATION_START_DATE AND A.ASSOCIATION_END_DATE AND NVL(A.DISASSOCIATED_FLAG,'N') = 'N' ) ")
	        .append("GROUP BY H.ATTRIBUTE9 , H.ATTRIBUTE10 ,  H.ATTRIBUTE11 , H.ATTRIBUTE12 , H.ATTRIBUTE13 ORDER BY H.ATTRIBUTE13");
	        
    		String sql = sqlBuilder.toString();

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("MAINT_BODY_NUMBER", maintenanceNumber);
	    		
	        List<ContactMethodResult> listContactMethodResult = namedParameterJdbcTemplate.query(sql, paramMap , new RowMapper<ContactMethodResult>() {
	     
	            @Override
	            public ContactMethodResult mapRow(ResultSet rs, int rowNum) throws SQLException {
	            	ContactMethodResult aContactMethodResult = new ContactMethodResult();
	            	
	            	aContactMethodResult.setAuto_send(rs.getString("auto_send"));
	            	aContactMethodResult.setContact_person(rs.getString("contact_person"));
	            	aContactMethodResult.setContact_phone(rs.getString("phone"));
	            	aContactMethodResult.setContact_fax(rs.getString("fax"));     
	            	aContactMethodResult.setContact_email(rs.getString("email"));
	            	
	                return aContactMethodResult;
	            }
	     
	        });
	        
	        return listContactMethodResult;   
	        
	    }
}