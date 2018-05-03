/* --------------------------------------
    File Name: EBSWrapperFilter.java
    Author: Jimmy Wong (PCCW)
    Date: 3-Sept-2017
    Description:
    - Bypass specific URI
    - Request Wrapping
    - Session Authenication (Positive: save userid to session, Negative: redirect to HomePage)

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
        <1.8>           20171129        Jimmy Wong      Added "QUERY_LIMIT" attribute -> ebsQueryLimit
                                                        Added invalid session control
	<1.7>		20171013 	Jimmy Wong 	Commented all code related to "Putting DB Connection to Session" *** Recommand to use ConnectionProvider.getInitConnection()
	<1.6>		20171012 	Jimmy Wong 	Modified to use wrapper Connection
	<1.5>		20171012 	Carmen Ng 	Fixed sidebar not changing after resp change  fix
	<1.4>		20171010 	Carmen Ng 	Added function security control logic
	<1.3>		20171009 	Jimmy Wong 	Added user info checking and refreshing logic
	<1.2>		20170905 	Jimmy Wong 	Added "localhost" bypass, no need to handle code changes while deploying to pccwtest01
	<1.1>		20170905 	Jimmy Wong 	Changed bypass criteria
	<1.0>		20170903 	Jimmy Wong 	Initial version
   -------------------------------------- */


package hk.org.ha.eam.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.StringUtils;

import hk.org.ha.ebs.common.EBSUtil;
import oracle.apps.fnd.ext.common.AppsRequestWrapper;
import oracle.apps.fnd.ext.common.AppsRequestWrapper.WrapperException;
import oracle.apps.fnd.ext.common.CookieStatus;
import oracle.apps.fnd.ext.common.Session;


public class EBSWrapperFilter implements Filter {
    private static final Logger logger = Logger.getLogger(EBSWrapperFilter.class.getName());

    public void destroy() {
        logger.info("Filter destroyed ");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException { 
        AppsRequestWrapper wrapper = null;
        HttpServletRequest tempRequest = (HttpServletRequest)request;
        HttpServletResponse tempResponse = (HttpServletResponse)response;
        HttpSession hSession = tempRequest.getSession();
        
        String requestURI = ((HttpServletRequest)request).getRequestURI();
        // Handle bypass case
        if ("Y".equals(request.getParameter("ebsSessionFail"))) {
        	chain.doFilter(request, response); 
            return;
        }
        // Handle Logout without check session
        if(requestURI.contains("/logout")) {
            hSession.removeAttribute("ebsQueryLimit");
            hSession.removeAttribute("ebsExportLimit");
            hSession.removeAttribute("ebsCMDateRange");
            hSession.removeAttribute("ebsPMDateRange");
            hSession.removeAttribute("ebsCorpDateRangeLimit");
            hSession.removeAttribute("ebsCorpPMDateRange");
            hSession.removeAttribute("ebsCorpCMDateRange");
            hSession.removeAttribute("ebsSummaryDefaultOff");
            hSession.removeAttribute("ebsUserId");
            hSession.removeAttribute("ebsUserName");
            hSession.removeAttribute("ebsFunctionId");
            hSession.removeAttribute("ebsRespId");
            hSession.removeAttribute("ebsRespAppId");
            hSession.removeAttribute("ebsICXCookieValue");
            hSession.removeAttribute("ebsSessionId");
            hSession.removeAttribute("ebsRespName");
            hSession.removeAttribute("ebsFullName");
            hSession.removeAttribute("ebsEmail");
            hSession.removeAttribute("ebsHAContactPhone");
            hSession.removeAttribute("respOrderCreate");
            hSession.removeAttribute("respOrderUpdate");
            hSession.removeAttribute("respOrderView");
            hSession.removeAttribute("respRequestCreate");
            hSession.removeAttribute("respRequestUpdate");
            hSession.removeAttribute("respRequestView");
        	chain.doFilter(request, response); 
            return;
        }
//        if ( (!requestURI.contains("/dashBoard") )
//         if ((!requestURI.contains(".jsp") && !requestURI.contains("logout") && !requestURI.contains("EAM"))
                // || "localhost".equalsIgnoreCase(request.getServerName()*/)
                // requestURI.contains("/APP/")
                // if (true
//                     )
//         {

 //            chain.doFilter(request, response);
 //            System.out.println("20170904: URI NOT matched - bypass: " + requestURI);
  //           return;
 //        }

        try {
            wrapper = new AppsRequestWrapper(tempRequest, tempResponse, ConnectionProvider.getConnection(), EBizUtil.getEBizInstance());
            
            Session session = wrapper.getAppsSession(true);
            logger.info("(Gatest0.1) getSessionId : "+session.getSessionId()+", cFunctionId : "+hSession.getAttribute("ebsFunctionId")+", cRespId : "+hSession.getAttribute("ebsRespId")+", ebsRespAppId : "+hSession.getAttribute("ebsRespAppId")+", ebsUserId : "+hSession.getAttribute("ebsUserId"));
        } catch (WrapperException e2) {
            logger.log(Level.SEVERE, "WrapperException error encountered ", e2);
            throw new ServletException(e2);
        } catch (SQLException e2) {
            logger.log(Level.SEVERE, "SQLException error encountered ", e2);
            throw new ServletException(e2);
        }
        // After AppsRequestWrapper preparation, authenticated it
        try {
            logger.info("Created AppsRequestWrapper object." +" Continuing the filter chain.");
            logger.info("Before isAuthenticated()");
            if (isAuthenticated(wrapper, tempRequest)) {
                
                // 20171009 start
                // Check any changes on entry resp
                String functionId = wrapper.getAppsSession().getInfo().get("FUNCTION_ID");
                String respId = wrapper.getAppsSession().getInfo().get("RESPONSIBILITY_ID");
                String respAppId = wrapper.getAppsSession().getInfo().get("RESPONSIBILITY_APPLICATION_ID");
                String userId = wrapper.getCurrentUserId();

                String cFunctionId = (String) hSession.getAttribute("ebsFunctionId");
                String cRespId = (String) hSession.getAttribute("ebsRespId");
                String cRespAppId = (String) hSession.getAttribute("ebsRespAppId");
                String cUserId = (String) hSession.getAttribute("ebsUserId");
                boolean refreshInfo = false;
                if (cFunctionId == null
                        || cRespId == null
                        || cRespAppId == null
                        || cUserId == null) {
                    refreshInfo = true;
                } else {
                    if (!cFunctionId.equals(functionId)
                            || !cRespId.equals(respId)
                            || !cRespAppId.equals(respAppId)
                            || !cUserId.equals(userId))
                    refreshInfo = true;
                }

                // if any changes on userid, respid, respappid or functionid, refreshInfo with reinitialization
                if (refreshInfo) {
                    hSession.setAttribute("ebsUserId", userId);
                    hSession.setAttribute("ebsUserName", wrapper.getAppsSession().getUserName());
                    hSession.setAttribute("ebsFunctionId", functionId);
                    hSession.setAttribute("ebsRespId", respId);
                    hSession.setAttribute("ebsRespAppId", respAppId);
                    // hSession.setAttribute("ebsRefreshInfo", "Y");
                    // hSession.setAttribute("ebsDbConn", wrapper.getConnection());
                    hSession.setAttribute("ebsICXCookieValue", wrapper.getICXCookieValue());
                    hSession.setAttribute("ebsSessionId", wrapper.getCurrentSessionId());
                    
                    setSessionInfofromDB(hSession);
                }
                // 20171009 end
                // if authenticated, chain to next filter
                chain.doFilter(wrapper, response);
            } else {
            	
            	logger.info("else isAuthenticated()");
                // if not authenticated, remove all session attribute and redirect to login page
                hSession.removeAttribute("ebsQueryLimit");
                hSession.removeAttribute("ebsExportLimit");
                hSession.removeAttribute("ebsCMDateRange");
                hSession.removeAttribute("ebsPMDateRange");
                hSession.removeAttribute("ebsCorpDateRangeLimit");
                hSession.removeAttribute("ebsCorpPMDateRange");
                hSession.removeAttribute("ebsCorpCMDateRange");
                hSession.removeAttribute("ebsSummaryDefaultOff");
                hSession.removeAttribute("ebsUserId");
                hSession.removeAttribute("ebsUserName");
                hSession.removeAttribute("ebsFunctionId");
                hSession.removeAttribute("ebsRespId");
                hSession.removeAttribute("ebsRespAppId");
                hSession.removeAttribute("ebsICXCookieValue");
                hSession.removeAttribute("ebsSessionId");
                hSession.removeAttribute("ebsRespName");
                hSession.removeAttribute("ebsFullName");
                hSession.removeAttribute("ebsEmail");
                hSession.removeAttribute("ebsHAContactPhone");
                hSession.removeAttribute("respOrderCreate");
                hSession.removeAttribute("respOrderUpdate");
                hSession.removeAttribute("respOrderView");
                hSession.removeAttribute("respRequestCreate");
                hSession.removeAttribute("respRequestUpdate");
                hSession.removeAttribute("respRequestView");
                
                // 20171129 Invalid Session Control
                String agent = EBizUtil.getEBizInstance().getAppsServletAgent(); 
                String redirectTo1 = EBSUtil.getSysParm(wrapper.getConnection(), "INV_SESS_REDIRECT_LOGIN_PAGE");
                //String redirectTo = EBSUtil.getLookUpValue(wrapper.getConnection(), "XXFND_PORTAL_PARM", "INV_SESS_REDIRECT_PAGE" );
                String redirectParameter = "redirect_to=" + agent + "/" + redirectTo1;
                if (!StringUtils.isEmpty(tempRequest.getHeader("x-requested-with")) && tempRequest.getHeader("x-requested-with").toUpperCase().contains("XMLHTTPREQUEST")) {
                  //tempResponse.setHeader("redirectUrl", redirectTo  + "?ebsSessionFail=Y&" + redirectParameter);
                	tempResponse.setHeader("redirectUrl", "redirect"  + "?ebsSessionFail=Y&" + redirectParameter);
                	tempResponse.setHeader("sessionstatus", "timeout");   	
                } else {
                  //tempResponse.sendRedirect(redirectTo  + "?ebsSessionFail=Y&" + redirectParameter);
                	tempResponse.sendRedirect("redirect"  + "?ebsSessionFail=Y&" + redirectParameter);
                }
                
            }
            logger.info("- the filter chain ends");
        } catch (Exception e3) {
        	logger.log(Level.SEVERE, "SQLException error encountered during EBSUtil.getLookUpValue", e3);
        } finally {
             if (wrapper != null) {
                 logger.info("- releasing the connection attached to the" + " current AppsRequestWrapper instance ");
                 try {
                     wrapper.getConnection().close();
                 } catch (SQLException e3) {
                     logger.log(Level.WARNING, "SQLException error while closing connection-- ", e3);
                 }
             }
             wrapper = null;
        }

    }
	
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Filter initialized ");
    }
	
    private boolean isAuthenticated(AppsRequestWrapper wrappedRequest, HttpServletRequest currReq) throws ServletException {
    	logger.info("20170904: Authenticating....");
    	if (wrappedRequest == null) {
    		logger.info("20170904: NULL wrappedRequest???");
            return false;
        }
        Session session = wrappedRequest.getAppsSession(true);
        // It is always good to check for nullability
        // A null value means something went wrong in the JDBC operation
        if (session == null)
            throw new ServletException("Could not initailize ICX session object");
        CookieStatus icxCookieStatus = session.getCurrentState().getIcxCookieStatus();
        if (!icxCookieStatus.equals(CookieStatus.VALID)) {
            logger.info("Icx session either has expired or is invalid");
            return false;
        } else {
        	 PreparedStatement stmt = null;
        	 ResultSet rs = null;
            try {                       
                stmt = wrappedRequest.getConnection().prepareStatement("SELECT sid, fnd_global.user_id, fnd_global.resp_id from v$session where audsid = userenv('sessionid')");
                rs = stmt.executeQuery();       		
                rs.next();

            } catch (Exception e) {
            currReq.getSession().setAttribute("ebsRespName","E"+e.toString());
            logger.info("Cannot run fnd_global.apps_initialize:" + e.toString());        		
            } finally {
            	if (rs != null) {
                    try {
                        rs.close();
                        rs = null;
                    } catch (Exception e) { }
            	}
            	if (stmt != null) {
                    try {
                        stmt.close();
                        stmt = null;
                    } catch (Exception e) {}
            	}
            }

            return true;
        }
        //return true;
    }
    
    private void releaseDBConn(HttpSession hSession) {
    	Connection conn;
    	conn = (Connection) hSession.getAttribute("ebsDbConn");
    	if (conn != null)
    		try {
    		conn.close();
    		conn = null;
    	} catch (SQLException e) {}
    }
    
    // 20171009 start
    private void setSessionInfofromDB(HttpSession hSession) {
    	logger.info("In setSessionInfofromDB (WrapperFilter)");
//    	releaseDBConn(hSession);
    	String respId = (String)hSession.getAttribute("ebsRespId");
        hSession.removeAttribute("ebsQueryLimit");
        hSession.removeAttribute("ebsExportLimit");
        hSession.removeAttribute("ebsCMDateRange");
        hSession.removeAttribute("ebsPMDateRange");
        hSession.removeAttribute("ebsCorpDateRangeLimit");
        hSession.removeAttribute("ebsCorpPMDateRange");
        hSession.removeAttribute("ebsCorpCMDateRange");
        hSession.removeAttribute("ebsSummaryDefaultOff");
        hSession.removeAttribute("ebsRespName");
        hSession.removeAttribute("ebsFullName");
        hSession.removeAttribute("ebsEmail");
        hSession.removeAttribute("ebsHAContactPhone");
	/* Sidebar not refreshing bug fix start 12/10/17 */
        hSession.removeAttribute("respOrderCreate");
        hSession.removeAttribute("respOrderUpdate");
        hSession.removeAttribute("respOrderView");
        hSession.removeAttribute("respRequestCreate");
        hSession.removeAttribute("respRequestUpdate");
        hSession.removeAttribute("respRequestView");
        /* Sidebar not refreshing bug fix end 12/10/17 */
        
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String fullName = "";
        String respName = "";
        String createAllowed = "";
        String updateAllowed = "";
        String viewAllowed = "";
        String summaryOff = "";
        try {
            conn = ConnectionProvider.getInitConnection(hSession);
            //Session.setAttribute("ebsDbConn", conn);
            //conn = (Connection)hSession.getAttribute("ebsDbConn");
            hSession.setAttribute("ebsQueryLimit", EBSUtil.getSysParm(conn, "QUERY_LIMIT"));
            hSession.setAttribute("ebsExportLimit", EBSUtil.getSysParm(conn, "EXPORT_LIMIT"));
            hSession.setAttribute("ebsCMDateRange", EBSUtil.getLookUpValue( conn, "XXEAM_PORTAL_PARM", ("XXEAM_CM_DEFAULT_SEARCH_PERIOD")));
            hSession.setAttribute("ebsPMDateRange", EBSUtil.getLookUpValue( conn, "XXEAM_PORTAL_PARM", ("XXEAM_PM_DEFAULT_SEARCH_PERIOD")));
            hSession.setAttribute("ebsCorpDateRangeLimit", EBSUtil.getLookUpValue( conn, "XXEAM_PORTAL_PARM", ("XXEAM_CORP_DATE_RANGE_LIMIT")));
            hSession.setAttribute("ebsCorpPMDateRange", EBSUtil.getLookUpValue( conn, "XXEAM_PORTAL_PARM", ("XXEAM_CORP_PM_DEFAULT_PERIOD")));
            hSession.setAttribute("ebsCorpCMDateRange", EBSUtil.getLookUpValue( conn, "XXEAM_PORTAL_PARM", ("XXEAM_CORP_CM_DEFAULT_PERIOD")));
                        
            stmt = conn.createStatement();
            rs = stmt.executeQuery("Select fnd_global.resp_name, full_name, email_address from per_people_x where person_id = fnd_global.employee_id union select fnd_global.resp_name, null full_name, null email_address from dual where fnd_global.employee_id = -1");
            if (rs.next()) {
                hSession.setAttribute("ebsRespName", rs.getString("resp_name"));
                respName = rs.getString("resp_name");
                fullName = rs.getString("full_name");
                if (fullName == null) {
                	fullName = (String) hSession.getAttribute("ebsUserName");
                }	
                else {
                    if ("".equals(fullName.trim())) {
                            fullName = (String) hSession.getAttribute("ebsUserName");
                    }
                }
                hSession.setAttribute("ebsFullName", fullName);
                hSession.setAttribute("ebsEmail", rs.getString("email_address"));
            }
            rs.close();
            rs = stmt.executeQuery("select phone_number " +
                                    "from per_phones p, " +
                                    "fnd_lookup_values l " +
                                    "where p.parent_table = 'PER_ALL_PEOPLE_F' " +
                                    "and p.parent_id = fnd_global.employee_id " +
                                    "and p.phone_type = l.lookup_code " +
                                    "and l.lookup_type = 'PHONE_TYPE' " +
                                    "and l.meaning = 'Work' " +
                                    "and rownum = 1");
            if (rs.next()) {
                hSession.setAttribute("ebsHAContactPhone", rs.getString("phone_number"));
            }
            rs.close();

            //20171026 start
            pstmt = conn.prepareStatement("SELECT RESP_TYPE FROM xxeam_resp_function_v WHERE RESPONSIBILITY_ID = ?");
            pstmt.setString(1, respId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                hSession.setAttribute("respType", rs.getString("RESP_TYPE"));
            }
            rs.close();
            pstmt.close();
            ////20171026 end
            
            // 20171010 start
            pstmt = conn.prepareStatement("SELECT CREATE_ALLOWED, UPDATE_ALLOWED, VIEW_ALLOWED FROM xxeam_resp_function_v WHERE RESPONSIBILITY_ID = ? AND FUNCTION_CODE = 'WORK ORDER'");
            pstmt.setString(1, respId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	createAllowed = rs.getString("CREATE_ALLOWED");
            	updateAllowed = rs.getString("UPDATE_ALLOWED");
            	viewAllowed = rs.getString("VIEW_ALLOWED");
            	if("Y".equals(createAllowed)) {
                    hSession.setAttribute("respOrderCreate", createAllowed);
            	}
            	if("Y".equals(updateAllowed)) {
                    hSession.setAttribute("respOrderUpdate", updateAllowed);
            	}
            	if("Y".equals(viewAllowed)) {
                    hSession.setAttribute("respOrderView", viewAllowed);
            	}
            	createAllowed = "";
            	updateAllowed = "";
            	viewAllowed = "";
            }
            rs.close();
            pstmt.close();
            
            pstmt = conn.prepareStatement("SELECT CREATE_ALLOWED, UPDATE_ALLOWED, VIEW_ALLOWED FROM xxeam_resp_function_v WHERE RESPONSIBILITY_ID = ? AND FUNCTION_CODE = 'WORK REQUEST'");
            pstmt.setString(1, respId);
            rs = pstmt.executeQuery();
           
            if (rs.next()) {
            	createAllowed = rs.getString("CREATE_ALLOWED");
            	updateAllowed = rs.getString("UPDATE_ALLOWED");
            	viewAllowed = rs.getString("VIEW_ALLOWED");
            	if("Y".equals(createAllowed)) {
                    hSession.setAttribute("respRequestCreate", createAllowed);
            	}
            	if("Y".equals(updateAllowed)) {
                    hSession.setAttribute("respRequestUpdate", updateAllowed);
            	}
            	if("Y".equals(viewAllowed)) {
                    hSession.setAttribute("respRequestView", viewAllowed);
            	}
            	createAllowed = "";
            	updateAllowed = "";
            	viewAllowed = "";
            }
            pstmt.close();
            // 20171010 end
            
            pstmt = conn.prepareStatement("select description " + 
            		"         from fnd_lookup_values " + 
            		"         where enabled_flag = 'Y' " + 
            		"         and trunc(sysdate) between trunc(nvl(start_date_active,sysdate)) and trunc(nvl(end_date_active,sysdate)) " + 
            		"         and lookup_type = 'XXEAM_SUMMARY_DEFAULT_OFF'");
            rs = pstmt.executeQuery();
           
            if(rs != null) {
            	while (rs.next()) {
                	summaryOff = rs.getString("description");
                	
                	if(respName.toLowerCase().contains(summaryOff.toLowerCase())) {
                        hSession.setAttribute("ebsSummaryDefaultOff", "Y");
                	}
                	
                	summaryOff = "";
                }
            }
            pstmt.close();
            pstmt = null;
           
        } catch (Exception e) {
        	logger.info("ERROR (EXCEPTION): validateWRData");
//            e.printStackTrace();
            
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException e) {}
            }
            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
                } catch (SQLException e) {}
            }
            if (pstmt != null) {
                try{
                    pstmt.close();
                    pstmt = null;
                } catch (SQLException sqle) {}
            }
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (SQLException e) {}
            }
        }
    }
}