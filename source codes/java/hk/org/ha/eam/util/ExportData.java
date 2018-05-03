/* --------------------------------------
    File Name: ExportData.java
    Author: Jimmy Wong (PCCW)
    Date: 9-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
	Export data servlet
	
    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
        <1.4>           20171128        Jimmy Wong      Fixed PrintWriter Issue
        <1.3>           20171123        Jimmy Wong      Code Cleaning
	<1.2>		20171117	Jimmy Wong	(ST08157 fix) remove random number before push to download
	<1.1>		20171107	Jimmy Wong	Updated to accommadate for 1000+ records
	<1.0>		20171009	Jimmy Wong	Initial version
   -------------------------------------- */

package hk.org.ha.eam.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hk.org.ha.ebs.common.WebFileUtil;

public class ExportData extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        PrintWriter out = null;
        response.setContentType("text/html;charset=UTF-8");
        
        String reportType = request.getParameter("exportType");
        String userName = request.getParameter("userName");
        String parameter = request.getParameter("parameter");
        String wrNumber = request.getParameter("wrNumber");
        String woNumber = request.getParameter("woNumber");
        int reportTypeInt = 0;
        String parms = "";
        String downloadFileName = "";
        try{
            
            if (userName == null || reportType == null || parameter  == null || (wrNumber == null && woNumber == null)) {
                throw new Exception("Not enough parameter");
            }
            reportType = reportType.trim();
            userName = userName.trim();
            parameter = parameter.trim();
            
            if (wrNumber != null) {
            	wrNumber = wrNumber.trim();
            }
            if(woNumber != null) {
            	woNumber = woNumber.trim();
            }
            if ("".equals(reportType) || "".equals(userName) || "".equals(parameter) || ("".equals(wrNumber) && "".equals(woNumber))) {
                throw new Exception("Not enough parameter (exportType, userName, parameter, wrNumber/woNumber)");
            }

            if ("WorkRequest".equalsIgnoreCase(reportType)) {
                reportTypeInt = ExportExcelUtil.WORK_REQUEST_EXCEL;
                parms = wrNumber;
            }
            if ("workorder".equalsIgnoreCase(reportType)) {
                reportTypeInt = ExportExcelUtil.WORK_ORDER_EXCEL;
                parms = woNumber;
            }
            String[] infoArray = {"Parameter:\t" + parameter,
                "Exported By:\t" + userName,
                "Exported Date:\t" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())
            };
            File tempFile = null;

            tempFile = ExportExcelUtil.exportExcel(request.getSession(), infoArray, reportTypeInt, parms);
            // 20171117
            downloadFileName = tempFile.getName();
            downloadFileName = downloadFileName.substring(0, downloadFileName.lastIndexOf("_")) + downloadFileName.substring(downloadFileName.lastIndexOf("."));
            // 20171117 - End
            WebFileUtil.downloadFile(response, tempFile, downloadFileName, true);
            
        } catch (Exception e) {
            out = new PrintWriter(response.getOutputStream());
            out.println("ERROR: " + e.getMessage());
            e.printStackTrace(out);
        } finally {
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
