package hk.org.ha.eam.util;

/* --------------------------------------
    File Name: GenReport.java
    Author: Jimmy Wong (PCCW)
    Date: 9-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Servlet for generating PDF report (print function)

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
        <1.1>           20171123        Jimmy Wong      Add error message for parameter checking
	<1.0>		20171009	Jimmy Wong	Initial version
   -------------------------------------- */
import hk.org.ha.ebs.common.WebFileUtil;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GenReport extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        File tempFile = null;
        try {
            response.setContentType("text/html;charset=UTF-8");
            String itemNum = request.getParameter("itemNum");
            String reportType = request.getParameter("reportType");
            int reportTypeInt = 0;
            if (itemNum == null || reportType == null) {
                throw new Exception("Not enough parameter.");
            }
            itemNum = itemNum.trim();
            reportType = reportType.trim();
            if ("".equals(itemNum) || "".equals(reportType)) {
                throw new Exception("Not enough parameter.");
            }
            if ("WorkRequest".equalsIgnoreCase(reportType)) reportTypeInt = EAMReportUtil.WR_REPORT;
            if ("workorder".equalsIgnoreCase(reportType)) reportTypeInt = EAMReportUtil.WO_REPORT;

            tempFile = EAMReportUtil.genEAMReport(reportTypeInt, itemNum);
            WebFileUtil.downloadFile(response, tempFile, tempFile.getName());
            
        } catch (Exception e) {
            //e.printStackTrace();
            PrintWriter out = response.getWriter();
            out.println("ERROR: " + e.getMessage());
            e.printStackTrace(out);
            out.flush();
            out.close();
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
