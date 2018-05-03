/* --------------------------------------
    File Name: GetUserPrefController.java
    Author: Kin Shum (PCCW)
    Date: 20-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - User Preference Setting

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170920	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.controller;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import hk.org.ha.eam.util.ConnectionProvider;



@Controller
@RequestMapping("/")
public class GetUserPrefController {

	private static final Logger logger = Logger.getLogger(GetUserPrefController.class);
	
	@RequestMapping(value="/getUserPrefExt")
	@ResponseBody
	public String getUserPrefExt(HttpServletRequest request) throws IOException, SQLException, Exception{
		logger.debug("Get pref...");
		
		String user_id = request.getParameter("USER_ID");
		String resp_id = request.getParameter("RESP_ID");
		String appl_id = request.getParameter("APP_ID");
		String profile_name = request.getParameter("PREF_NAME");
		String function_code = request.getParameter("FUNCTION_CODE");
		String page_code = request.getParameter("PAGE_CODE");
	
		if (!"".equals(user_id) && !"".equals(resp_id) && !"".equals(appl_id) && !"".equals(profile_name) && !"".equals(function_code) && !"".equals(page_code)) {
			String sql = "select preference_value from xxeam_user_preference_ext "
					+ "where "
					+ "user_id = ? "
					+ "and responsibility_id = ? "
					+ "and application_id = ? "
					+ "and UPPER(preference_name) = ? "
					+ "and function_code = ? "
					+ "and page_code = ? ";
			logger.debug("SQL="+sql);
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String returnValue = "";
			try {
				conn = ConnectionProvider.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_id);
				pstmt.setString(2, resp_id);
				pstmt.setString(3, appl_id);
				pstmt.setString(4, profile_name.toUpperCase());
				pstmt.setString(5, function_code.toUpperCase());
				pstmt.setString(6, page_code.toUpperCase());
				rs = pstmt.executeQuery();
	    		if(!rs.next()) {
	    			returnValue = "NO DATA"; // no data
	    		}else {
	    			logger.debug("Result="+rs.getString("preference_value"));
	    			returnValue = rs.getString("preference_value");
	    		}				
			}catch(Exception e) {
				logger.debug("ERROR (EXCEPTION): getUserPrefExt");
				returnValue = "ERROR (EXCEPTION): " + e.toString();
			} finally {
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
			return returnValue;
		}else {
			logger.debug("Parameters cannot be null");
			return "ERROR: Parameters cannot be null";
		}
	}
		
	@RequestMapping(value="/saveUserPrefExt")
	@ResponseBody
	public String saveUserPrefExt(HttpServletRequest request) throws IOException{
		logger.debug("Save pref...");
		String sql;
		String result;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String user_id = request.getParameter("USER_ID");
		String resp_id = request.getParameter("RESP_ID");
		String appl_id = request.getParameter("APP_ID");
		String profile_name = request.getParameter("PREF_NAME");
		String preference_value = request.getParameter("PREF_VALUE");
		String function_code = request.getParameter("FUNCTION_CODE");
		String page_code = request.getParameter("PAGE_CODE");

		if (!"".equals(user_id) && !"".equals(resp_id) && !"".equals(appl_id) && !"".equals(profile_name) && !"".equals(preference_value)) {
			sql = "select count(1) as preference_value from xxeam_user_preference_ext where user_id = ?" 
                + " and responsibility_id = ? "
                + " and application_id = ? "
                + " and UPPER(preference_name) = ? "
                + " and UPPER(function_code) = ? "
                + " and UPPER(page_code) = ?";
			logger.debug("SQL="+sql);
			try {
				conn = ConnectionProvider.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_id);
				pstmt.setString(2, resp_id);
				pstmt.setString(3, appl_id);
				pstmt.setString(4, profile_name.toUpperCase());
				pstmt.setString(5, function_code.toUpperCase());
				pstmt.setString(6, page_code.toUpperCase());
	    		rs = pstmt.executeQuery();
	    		rs.next();
	    		logger.debug(rs.getInt(1));
	    		
	    		if(rs.getInt(1)==0) {
	    			sql = "insert into xxeam_user_preference_ext "
	    					+ "(preference_value, user_id, responsibility_id, application_id, preference_name"
	    					+ ", function_code, page_code)"
	    					+ "values"
	    					+ "(?, ?, ?, ?, ?, ?, ?)";
	    			
	    			result = "ADDED"; // no data perform insert
	    		}else {
    				//perform update
	    			sql = "update xxeam_user_preference_ext set preference_value = ? "
	    					+ "where user_id = ? "
	    					+ "and responsibility_id = ? "
	    					+ "and application_id = ? "
	    					+ "and UPPER(preference_name) = ? "
	    					+ "and UPPER(function_code) = ? "
	    					+ "and UPPER(page_code) = ?";
    				
    				result = "UPDATED";
	    		}
	    		try {pstmt.close();} catch (Exception e) {}
	    		pstmt = conn.prepareStatement(sql);
	    		pstmt.setString(1, preference_value);
	    		pstmt.setString(2, user_id);
				pstmt.setString(3, resp_id);
				pstmt.setString(4, appl_id);
				pstmt.setString(5, profile_name.toUpperCase());
				pstmt.setString(6, function_code.toUpperCase());
				pstmt.setString(7, page_code.toUpperCase());
				
    			int a = pstmt.executeUpdate();
			}catch(Exception e) {
				logger.debug("ERROR (EXCEPTION): saveUserPrefExt");
				result = "ERROR: " + e.getMessage();
			} finally {
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
			return result;
		}else {
			logger.debug("Parameters cannot be null");
			return "ERROR: Parameters cannot be null";
		}
	}
	
	@RequestMapping(value="/delUserPrefExt")
	@ResponseBody
	public String delUserPrefExt(HttpServletRequest request) throws IOException, SQLException, Exception{
		logger.debug("Restore pref...");
		
		String user_id = request.getParameter("USER_ID");
		String resp_id = request.getParameter("RESP_ID");
		String appl_id = request.getParameter("APP_ID");
		String profile_name = request.getParameter("PREF_NAME");
		String function_code = request.getParameter("FUNCTION_CODE");
		String page_code = request.getParameter("PAGE_CODE");
		
		if (!"".equals(user_id) && !"".equals(resp_id) && !"".equals(appl_id) && !"".equals(profile_name) && !"".equals(function_code) && !"".equals(page_code)) {
			String sql = "delete from xxeam_user_preference_ext "
					+ "where "
					+ "user_id = ? "
					+ "and responsibility_id = ? "
					+ "and application_id = ? "
					+ "and UPPER(preference_name) = ? "
					+ "and function_code = ? "
					+ "and page_code = ? ";
			logger.debug("SQL="+sql);
			Connection conn = null;
			PreparedStatement pstmt = null;
			String returnValue = "";
			try {
				conn = ConnectionProvider.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_id);
				pstmt.setString(2, resp_id);
				pstmt.setString(3, appl_id);
				pstmt.setString(4, profile_name.toUpperCase());
				pstmt.setString(5, function_code.toUpperCase());
				pstmt.setString(6, page_code.toUpperCase());
				int exc = pstmt.executeUpdate();		
			}catch(Exception e) {
				logger.debug("ERROR (EXCEPTION): delUserPrefExt");
//				e.printStackTrace();
				returnValue = "ERROR (EXCEPTION): " + e.toString();
			} finally {
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
			return returnValue;
		}else {
			logger.debug("Parameters cannot be null");
			return "ERROR: Parameters cannot be null";
		}
	}
}