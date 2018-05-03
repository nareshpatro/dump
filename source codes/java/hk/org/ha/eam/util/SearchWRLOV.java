/* --------------------------------------
    File Name: SearchWRLOV.java
    Author: Jimmy Wong (PCCW)
    Date: 9-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - SearchWRLOV servlet

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171009	Jimmy Wong	Initial version
   -------------------------------------- */
package hk.org.ha.eam.util;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchWRLOV extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String lov = request.getParameter("lov");
        String value = request.getParameter("value");
       
        out.println(value);
    }
}
