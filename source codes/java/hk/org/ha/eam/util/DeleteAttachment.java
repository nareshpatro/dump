/* --------------------------------------
    File Name: DeleteAttachment.java
    Author: Jimmy Wong (PCCW)
    Date: 24-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - File Upload Servlet for EBS External Application

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
        <1.1>           20171127        Jimmy Wong      Added Filename Patching to FND_ATTACHED_DOCUMENTS
	<1.0>		20171123        Jimmy Wong	Initial version
   -------------------------------------- */

package hk.org.ha.eam.util;

import hk.org.ha.ebs.common.EBSFileUtil;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.log4j.Logger;

public class DeleteAttachment extends HttpServlet { 
    
    private static final String FILENAME_SQL = "SELECT DOCUMENT_ID, FILE_NAME FROM FND_DOCUMENTS WHERE DOCUMENT_ID = ? OR MEDIA_ID = ?";
            
    private static final String PATCH_FILENAME_SQL = "UPDATE FND_ATTACHED_DOCUMENTS SET ATTRIBUTE1 = ? " +
                                                        "WHERE ENTITY_NAME IN ('EAM_WORK_REQUESTS', 'EAM_WORK_ORDERS', 'EAM_DISCRETE_OPERATIONS')" +
                                                        "AND DOCUMENT_ID = ?";
    
    private static final Logger logger = Logger.getLogger(DeleteAttachment.class);
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long docId = -1;
        String fileName = "";
        try{
            out = response.getWriter();
            response.setContentType("text/html;charset=UTF-8");
            String idType = request.getParameter("idType");
            String id = request.getParameter("id");
            int idTypeInt = 0;
            long idsLong[];
            String ids[];
            if (idType == null || id == null) {
                throw new Exception("Not enough parameter.");
            }
            idType = idType.trim();
            id = id.trim();
            if ("".equals(idType) || "".equals(id)) {
                throw new Exception("Not enough parameter.");
            }
            if ("doc_id".equalsIgnoreCase(idType)) idTypeInt = EBSFileUtil.DOC_ID;
            if ("file_id".equalsIgnoreCase(idType)) idTypeInt = EBSFileUtil.FILE_ID;
            ids = id.split(",");
            idsLong = new long[ids.length];
            conn = ConnectionProvider.getConnection();
            
            for (int i = 0; i < ids.length; i++) {
                try{
                    idsLong[i] = Long.parseLong(ids[i].trim());
                    pstmt = conn.prepareStatement(FILENAME_SQL);
                    if (idTypeInt == EBSFileUtil.DOC_ID) {
                        pstmt.setLong(1, idsLong[i]);
                        pstmt.setNull(2, Types.BIGINT);
                    }
                    if (idTypeInt == EBSFileUtil.FILE_ID) {
                        pstmt.setLong(2, idsLong[i]);
                        pstmt.setNull(1, Types.BIGINT);
                    }
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        docId = rs.getLong(1);
                        fileName = rs.getString(2);
                    }
                    rs.close();
                    rs = null;
                    pstmt.close();
                    pstmt = conn.prepareStatement(PATCH_FILENAME_SQL);
                    pstmt.setString(1, fileName);
                    pstmt.setLong(2, docId);
                    pstmt.executeUpdate();
                    pstmt.close();
                    pstmt = null;
                } catch (Exception e) {
                    idsLong[i] = -1;
                } finally {
                    if (rs != null) {
                        try{
                            rs.close();
                            rs = null;
                        } catch (SQLException sqle) {}
                    }
                    if (pstmt != null) {
                        try{
                            pstmt.close();
                            pstmt = null;
                        } catch (SQLException sqle) {}
                    }
                }
            }
            
            ArrayList results = EBSFileUtil.deleteFromDB(conn, idTypeInt, idsLong);
            for (int i = 0; i < results.size()-1; i++) {
                out.print("-" + results.get(i) + ",");
            }
            if (results.size() > 0) out.print("-" + results.get(results.size()-1));
        } catch (Exception e) {
            out.println("ERROR: " + e.getMessage());
            e.printStackTrace(out);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (SQLException e) {}
            }
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {}
            }
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
}