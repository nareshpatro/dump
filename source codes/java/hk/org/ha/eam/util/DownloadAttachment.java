package hk.org.ha.eam.util;

/* --------------------------------------
    File Name: DownloadAttachment.java
    Author: Jimmy Wong (PCCW)
    Date: 24-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - DownloadAttachment servlet

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
        <1.1>           20171123        Jimmy Wong      Code Cleaning
	<1.0>		20171024        Jimmy Wong	Initial version
   -------------------------------------- */
   
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;

import hk.org.ha.ebs.common.EBSFileUtil;
import hk.org.ha.ebs.common.WebFileUtil;

public class DownloadAttachment extends HttpServlet { 
   
    private static final Logger logger = Logger.getLogger(DownloadAttachment.class);
	
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
        
        Connection conn = null;
//        PrintWriter out = null;
        
        try {
            
            response.setContentType("text/html;charset=UTF-8");
            String docId = request.getParameter("docId");
            
            logger.debug("docId:"+docId);

            if (docId == null) {
                throw new Exception("Not enough parameter. (docId)");
            }
            docId = docId.trim();
            if (docId.isEmpty()) {
                throw new Exception("Not enough parameter. (docId)");
            }
            conn = ConnectionProvider.getConnection();
            Map<String,Object> hMap = EBSFileUtil.getFileInfoFromDB(conn, EBSFileUtil.DOC_ID, Long.parseLong(docId));
            logger.debug("hMap.size():"+hMap.size());
            logger.debug("FileName:"+(String)hMap.get("filename"));
            WebFileUtil.downloadFile(response, (File)hMap.get("file"), (String)hMap.get("filename"), true);	// true: save as attachment rather than inline brower open
        } catch (Exception ex) {
//            out.println("ERROR: " + ex.getMessage());
//            ex.printStackTrace(out);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (SQLException e) {}
            }
//            if (out != null) {
//                try {
//                    out.flush();
//                    out.close();
//                    out = null;
//                } catch (Exception e) {}
//            }
        }

    }   

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
        
}
