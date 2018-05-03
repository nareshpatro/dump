/* --------------------------------------
    File Name: ConnectionProvider.java
    Author: Jimmy Wong (PCCW)
    Date: 9-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Get DB Connection from jdbc/EBS connection pool
    - EBS initialized connection

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171009	Jimmy Wong	Initial version
   -------------------------------------- */

package hk.org.ha.eam.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class ConnectionProvider {
    private static DataSource myDS = null;
    
    private static final Logger logger = Logger.getLogger(ConnectionProvider.class);
    
    static {
        try {
            Context ctx = new InitialContext();
            myDS = (DataSource)ctx.lookup("jdbc/EBS");
            if (ctx != null)
                ctx.close();
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

    private ConnectionProvider() {
        
    }

    public static Connection getConnection() throws SQLException {
        if (myDS == null)
            throw new IllegalStateException("AppsDatasource is not properly initialized or available");
        return myDS.getConnection();
    }
    
    public static Connection getInitConnection(String userId, String respId, String respAppId) throws SQLException {
        if (myDS == null)
            throw new IllegalStateException("AppsDatasource is not properly initialized or available");
        Connection conn = null;
        conn = myDS.getConnection();
        CallableStatement cstmt = null;
        try {
	        cstmt = conn.prepareCall("begin fnd_global.apps_initialize(?, ?, ?); end;");
	        cstmt.setLong(1, Long.parseLong(userId));
	        cstmt.setLong(2, Long.parseLong(respId));
	        cstmt.setLong(3, Long.parseLong(respAppId));
	        cstmt.execute();
        } catch (Exception e) {
        	logger.error("INIT ERROR:"+e.getMessage());
        	if (conn != null) {
            	try {
            		conn.close();
            		conn = null;
            	} catch (SQLException sqle) {}
            }
        } finally {
        	if(cstmt != null) {
        		cstmt.close();
        	}
            cstmt = null;
        }
        return conn;
    }
    
    public static Connection getInitConnection(HttpSession hSession) throws SQLException {
        String userId = (String) hSession.getAttribute("ebsUserId");
        String respId = (String) hSession.getAttribute("ebsRespId");
        String respAppId = (String) hSession.getAttribute("ebsRespAppId");
        if (userId == null || respId == null || respAppId == null) return null;
        if ("".equals(userId.trim()) || "".equals(respId.trim()) || "".equals(respAppId.trim())) return null;
        return getInitConnection(userId, respId, respAppId);
    }
}
