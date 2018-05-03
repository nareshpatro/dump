/* --------------------------------------
    File Name: Lov.java
    Author: Jimmy Wong (PCCW)
    Date: 24-Mar-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Static LOV function for eam

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.8>		20170426	Kylie Wu	Add Work Order Status query
	<1.7>		20170419	Jimmy Wong	Centralized BeanItemContainer as static object for reducing cloning time.
	<1.6>		20170414	Jimmy Wong	Change refresh approach to 0300 everyday, Used new asset location sql
	<1.5>		20170328	Jimmy Wong	Added getSelectionData method
	<1.4>		20170328	Jimmy Wong	Added null value handling
	<1.3>		20170327	Kylie Wu	Changed "WORK_REQUEST_TYPE", "WORK_REQUEST_STATUS" LOV
	<1.2>		20170324	Jimmy Wong	Added  "DISINFECTION_STATUS" LOV
	<1.1>		20170324	Jimmy Wong	Added Prototype LOV list
	<1.0>		20170324	Jimmy Wong	Initial version
	
   -------------------------------------- */

package hk.org.ha.eam.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import hk.org.ha.eam.dao.SearchLovDaoImpl;

/**
 * last Update: 20170321
 */
public class Lov {
	
	private static final Logger logger = Logger.getLogger(SearchLovDaoImpl.class);
	
	private static JdbcTemplate jdbcTemplate;   
	
	@Autowired
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
    public Lov(DataSource dataSource) {
    	jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
    private static Date nextUpdateDate = null;
    //private static long DURATION = 86400000; // 1 day;
    private static int UPDATE_TIME[] = {3, 0, 0, 0}; // 03:00:00.000
    
    public static final int WORK_REQUEST_TYPE = 0;
    public static final int WORK_REQUEST_STATUS = 1;
    public static final int OWNING_DEPT = 2;
    public static final int MAINTENANCE_VENDOR = 3;
    public static final int ASSET_OWNER = 4;
    public static final int ASSET_LOCATION = 5;
    public static final int ORGANIZATION = 6;
    public static final int DISINFECTION_STATUS = 7;
    public static final int WORK_ORDER_STATUS = 8; //@20170426 Added by Kylie for print work order
    /*
    private static HashMap<Long, String> workRequestType = null;
    private static HashMap<Long, String> workRequestStatus = null;
    private static HashMap<Long, String> owningDept = null;
    private static HashMap<Long, String> maintenanceVendor = null;
    private static HashMap<Long, String> assetOwner = null;
    private static HashMap<Long, String> assetLocation = null;
    private static HashMap<Long, String> disinfectionStatus = nulsl;
    */
    private static HashMap <Integer, HashMap<String, String>> lovData = null;
    //private static HashMap <Integer, HashMap<Long, HashMap<Long, String>>> responLovData = null;
    
    private static final String LOV_SQL[] = {
        "select LOOKUP_CODE name, MEANING descr from mfg_lookups where lookup_type='WIP_EAM_WORK_REQ_TYPE' order by MEANING",
        "select LOOKUP_CODE name, MEANING descr from mfg_lookups where lookup_type='WIP_EAM_WORK_REQ_STATUS' AND LOOKUP_CODE != 2  order by MEANING",
//        "select LOOKUP_CODE, MEANING from mfg_lookups where lookup_type='WIP_EAM_WORK_REQ_STATUS' order by MEANING",
        "SELECT MIN(DEPARTMENT_ID) name, DEPARTMENT_CODE descr from bom_departments GROUP BY DEPARTMENT_CODE order by DEPARTMENT_CODE", // FOR PROTOTYPE!!!
        "select MIN(VENDOR_ID) name, VENDOR_NAME descr FROM ap_suppliers group by VENDOR_NAME order by VENDOR_NAME", // FOR PROTOTYPE!!!
        "select ffv.flex_value name, ffv.description descr from fnd_flex_value_sets ffvs, fnd_flex_values_vl ffv where ffvs.flex_value_set_id = ffv.flex_value_set_id and flex_value_set_name = 'XXEAM_ASSET_OWNER' order by ffv.description",
        // "SELECT loc.location_code locationCode, loc.description description FROM hr_locations loc where loc.inventory_organization_id not in (SELECT mp.organization_id FROM apps.mtl_parameters mp, apps.hr_organization_units ou WHERE ou.organization_id = mp.organization_id AND NVL (ou.attribute6, 'XX') LIKE 'DRUGS')", 
        "SELECT loc.location_code name, loc.location_code descr FROM hr_locations loc where loc.inventory_organization_id not in (SELECT mp.organization_id FROM apps.mtl_parameters mp, apps.hr_organization_units ou WHERE ou.organization_id = mp.organization_id AND NVL (ou.attribute6, 'XX') LIKE 'DRUGS') order by loc.location_code", 
        "select MIN(organization_id) name, organization_code descr from mtl_parameters group by organization_code order by organization_code", // FOR PROTOTYPE!!!
        "SELECT V.FLEX_VALUE name, V.DESCRIPTION descr FROM FND_FLEX_VALUE_SETS S, FND_FLEX_VALUES_VL V WHERE S.FLEX_VALUE_SET_ID = V.FLEX_VALUE_SET_ID AND S.FLEX_VALUE_SET_NAME = 'XXEAM_DISINFECTION_STATUS' AND V.ENABLED_FLAG = 'Y' AND TRUNC(SYSDATE) BETWEEN NVL(V.START_DATE_ACTIVE,TRUNC(SYSDATE)) AND  NVL(V.END_DATE_ACTIVE, TRUNC(SYSDATE)) order by V.DESCRIPTION",
        "select LOOKUP_CODE name, MEANING descr from mfg_lookups where lookup_type = 'WIP_JOB_STATUS' AND LOOKUP_CODE in (1, 3, 4, 6, 17) order by MEANING" //@20170426 Added by Kylie for print work order
    };
    

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean initLov() {
    	
    	lovData = new HashMap<Integer, HashMap<String, String>>();
        for (int i = 0; i < LOV_SQL.length; i++) {
            HashMap<String, String> mapping = new HashMap<String, String>();
            mapping = jdbcTemplate.query(LOV_SQL[i], new ResultSetExtractor<HashMap>() {
//            List<WorkRequest> listWorkRequest = namedParameterJdbcTemplate.query(LOV_SQL[i], paramMap , new RowMapper<WorkRequest>() {
		                @Override
		                public HashMap extractData(ResultSet rs) throws SQLException,DataAccessException {
		                HashMap<String,String> mapRet= new HashMap<String,String>();
		                while(rs.next()){
		                    mapRet.put(rs.getString("string1"),rs.getString("string2"));
		                }
		                return mapRet;
                	                    
		                }
         
            });
            lovData.put(i, mapping);
        }
        	
/*        Connection dbConn = null;
        try{
            dbConn = ConnectionProvider.getConnection();
            Statement stmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs;
            lovData = new HashMap<Integer, HashMap<String, String>>();
            for (int i = 0; i < LOV_SQL.length; i++) {
                rs = stmt.executeQuery(LOV_SQL[i]);
                if (rs.first()) {
                    HashMap<String, String> mapping = new HashMap<String, String>();
                    do {
                        mapping.put(rs.getString(1), rs.getString(2));
                    } while (rs.next());
                    lovData.put(i, mapping);
                }
                rs.close();
            }

            stmt.close();
            dbConn.close();*/
            Calendar tempCal = Calendar.getInstance();
            tempCal.set(Calendar.HOUR_OF_DAY, UPDATE_TIME[0]);
            tempCal.set(Calendar.MINUTE, UPDATE_TIME[1]);
            tempCal.set(Calendar.SECOND, UPDATE_TIME[2]);
            tempCal.set(Calendar.MILLISECOND, UPDATE_TIME[3]);
            tempCal.add(Calendar.DATE, 1);
            nextUpdateDate = tempCal.getTime();
            return true;
/*        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }*/
    }
    
    private static boolean isValid() {
        if (lovData == null || lovData.size() == 0) return false;
        //return Calendar.getInstance().getTime().getTime() - lastUpdatedDate.getTime() < DURATION;
        return nextUpdateDate.getTime() >= Calendar.getInstance().getTime().getTime();
    }
    
    public static synchronized String getLovData(int LOV_TYPE, String id) throws SQLException {
        if (!isValid()) initLov();
        String rtnValue = lovData.get(LOV_TYPE).get(id);
        return rtnValue == null? "": rtnValue;
    }
    
    public static synchronized String getLovData(int LOV_TYPE, long id) throws SQLException {
        return getLovData(LOV_TYPE, Long.toString(id));
    }
    
    public static synchronized String[][] getSelectionData(int LOV_TYPE) {
    	// As initLOV alway return true, rewrite as following to pass the CodeScan
    	if (!isValid()) {
    		initLov();
    	}
//        if (!isValid()) 
//            if (!initLov()) return null;
        String rtn[][] = new String[lovData.get(LOV_TYPE).size()][2];
                
        Iterator it = lovData.get(LOV_TYPE).entrySet().iterator();
        int index = 0;
        while (it.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry) it.next();
            rtn[index][0] = (String)pair.getKey();
            rtn[index][1] = (String)pair.getValue();
            index++;
        }
        return rtn;
    }
}
