/* --------------------------------------
    File Name: UploadAttachment.java
    Author: Jimmy Wong (PCCW)
    Date: 24-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - File Upload Servlet for EBS External Application

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
        <1.1>           20171123        Jimmy Wong      Code Cleaning
	<1.0>		20171024        Jimmy Wong	Initial version
   -------------------------------------- */

package hk.org.ha.eam.util;

import hk.org.ha.ebs.common.EBSFileUtil;
import hk.org.ha.ebs.common.EBSUtil;
import hk.org.ha.ebs.common.ParamFile;
import hk.org.ha.ebs.common.WebFileUtil;
import java.io.*;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.Connection;
import java.sql.SQLException;

public class UploadAttachment extends HttpServlet { 
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
            
            PrintWriter out = response.getWriter();
            
            // Verify the content type
            String contentType = request.getContentType();
            
            if ((contentType.indexOf("multipart/form-data") >= 0)) {
                ArrayList<Long> docIds = null;
                String result="";
                Connection conn = null;
                try {
//                    hSession.setAttribute("ebsUserId", "131500");
//                    hSession.setAttribute("ebsRespId", "54993");
//                    hSession.setAttribute("ebsRespAppId", "426");
                    HttpSession hSession = request.getSession();
                    conn = ConnectionProvider.getInitConnection(hSession);
                    String tempDir = EBSUtil.getSysParm(conn, "TEMP_LOCATION");
                    File repository = new File(tempDir);
                    if (!repository.exists()) repository.mkdir();
                    ParamFile uploadPF = WebFileUtil.uploadFile(request, response, repository);
                    File uploadFolder = uploadPF.getUploadFolder();
                    String title1 = uploadPF.getTitle();
                    String title = new String(title1.getBytes("ISO-8859-1"), "UTF-8");
                    String desc1 = uploadPF.getDesc();
                    String desc = new String(desc1.getBytes("ISO-8859-1"), "UTF-8");
                    docIds = EBSFileUtil.uploadFilesToDB(conn, uploadFolder, title, desc);
                  
                } catch (Exception ex) {
                    out.println("ERROR:" + ex.getMessage());
                    ex.printStackTrace(out);
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                            conn = null;
                        } catch (SQLException e) {}
                    }
                }
                
                for(Long docId: docIds){
                    result += docId.toString()+",";
                }
                out.print(result.substring(0, result.length()-1));

            } else {
                out.println("ERROR: No file uploaded."); 
            }
            out.flush();
            out.close();
	}
}