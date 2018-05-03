/* --------------------------------------
    File Name: EBIzUtil.java
    Author: Jimmy Wong (PCCW)
    Date: 1-Sept-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Get APP_ID from DB (LOOKUP Table) or resource map
    - Create EBS Instance


    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.1>		20170925	Jimmy Wong	Changed to using SysParm get method
	<1.0>		20170901	Jimmy Wong	Initial version
   -------------------------------------- */

package hk.org.ha.eam.util;

import hk.org.ha.eam.util.ConnectionProvider;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.logging.Level;
//import java.util.logging.Logger;

import oracle.apps.fnd.ext.common.EBiz;
import hk.org.ha.ebs.common.EBSUtil;
import org.apache.log4j.Logger;

public class EBizUtil{
//    private static final Logger logger = Logger.getLogger(EBizUtil.class.getName());
	private static final Logger logger = Logger.getLogger(EBizUtil.class);
    private static EBiz INSTANCE = null;
    
  
    static {
        Connection conn = null;
        try {
        	logger.debug("20170904: getConnection");
        	conn  = ConnectionProvider.getConnection();
        	//WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        	//dataSource  = (DataSource)context.getBean("dataSource");
        	/*Context context = new InitialContext();
        	dataSource = (DataSource)context.lookup("jdbc/EBS");
            conn = dataSource.getConnection();*/
        	logger.debug("20170904: getConnection OK!");
            String sAppId = "";
            //sAppId = EBSUtil.getLookUpValue( conn, "XXEAM_PORTAL_PARM", ("APPL_SERVER_ID"));
            sAppId = EBSUtil.getSysParm(conn, ("APPL_SERVER_ID"));
            logger.debug("sAppId="+sAppId);
            //Testing
            
            try {
            String sql = "select n.server_id " +
                    "from FND_NODES n " +
            		"where (n.node_name = upper(sys_context('USERENV','HOST')) or n.host||'.'||n.domain = sys_context('USERENV','HOST'))";
                    
            //sql = "select sys_context('USERENV','HOST') from dual";
            ResultSet rs = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
	        if (rs != null) {
            	rs.next();
            	String sId = rs.getString(1);
            	logger.debug("APPL_SERVER_ID(FND_NODE)="+sId);
            	//sAppId = sId;
            	rs.close();
	        }
	        
            } catch (Exception e) {
            	logger.debug("APPL_SERVER_ID ERROR"+e.toString());
            }
            /* Commect by Jimmy @ 20170825 for SysParm feature
            try {
            	System.out.println("20170904: InitialContext");
                sAppId = (String)(new InitialContext()).lookup("EBS/APPL_SERVER_ID");
//                sAppId = "47F03246B0B6028CE053C0A8197971E137014573421141988518315993008739";
                // NEW DEV
//              sAppId = "3B304C14B1EC01E6E053C0A81979121B10232688163790661653493942081220";
                System.out.println("20170904: APPL_SERVER_ID - " + sAppId);
            } catch (Exception e) {
                String sql = "select description" +
                            " from fnd_lookup_values" +
                            " where enabled_flag = 'Y'" +
                            " and trunc(sysdate) between trunc(nvl(start_date_active,sysdate)) and trunc(nvl(end_date_active,sysdate))" +
                            " and lookup_type = 'XXEAM_PORTAL_PARM'" +
                            " and lookup_code = 'APPL_SERVER_ID'";
                ResultSet rs = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
                if (rs != null) {
                    rs.next();
                    sAppId = rs.getString(1);
                    System.out.println("APPL_SERVER_ID: " + sAppId);
                    rs.close();
                } else
                    System.out.println("NO APPL_SERVER_ID found in lookup (Type: XXEAM_PORTAL_PARM)");
            }
         	*/
            INSTANCE = new EBiz(conn, sAppId);
            // DO NOT hard code applServerID for a real application
            // Get applServerID as CONTEXT-PARAM from web.xml or elsewhere

            // For ERPWINFS01 
            //INSTANCE = new EBiz(connection, "49064EDCC6050228E053C0A81979D09A35605820402548745492113274538027");

            // For PCCWTEST01
            //INSTANCE = new EBiz(connection, "47F03246B0B6028CE053C0A8197971E137014573421141988518315993008739");
            //INSTANCE = new EBiz(conn, (String)(new InitialContext()).lookup("EBS/APPL_SERVER_ID"));
            //System.out.println("20170410: " + (new InitialContext()).lookup("EBS/APPL_SERVER_ID"));
        } catch (SQLException e) {
        	logger.debug("SQLException while creating EBiz instance -->", e);
//            logger.log(Level.SEVERE, "SQLException while creating EBiz instance -->", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
        	logger.debug("Exception while creating EBiz instance -->", e);
//            logger.log(Level.SEVERE, "Exception while creating EBiz instance -->", e);
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    } catch (SQLException e) {
                }
            }
        }
    }
    
    
    public static EBiz getEBizInstance() {
        return INSTANCE;
    }
}