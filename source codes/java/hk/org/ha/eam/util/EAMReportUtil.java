/* --------------------------------------
    File Name: EAMReportUtil.java
    Author: Jimmy Wong (PCCW)
    Date: 24-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Util Class for WO and WR Report Generation

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171024	Jimmy Wong	Initial version
   -------------------------------------- */

package hk.org.ha.eam.util;

import java.io.File;
import java.sql.Connection;
import hk.org.ha.ebs.common.XMLReportUtil;

public class EAMReportUtil {
    public static final int WR_REPORT = 0;
    public static final int WO_REPORT = 1;
    
    // XXEAM_WORK_REQUEST_REPORT
    private static final String XML_FUNCTIONS[] = {
        "BEGIN \n"
        + ":1 := XXEAM_WORKREQUEST_RPT_PKG.GET_WR_REPORT_XML (:2);\n"
        + " END;"
       , "BEGIN \n"
        + ":1 := XXEAM_WORKORDER_RPT_PKG.GET_WO_REPORT_XML (:2);\n"
        + " END;"
    };
    
    private static final String TEMPLATE_CODE[] = {
        "XXEAM_WORK_REQUEST_REPORT"
        , "XXEAMWOREPORT"
    };
    
    private static final String FILE_PREFIX[] = {
      "WR_REPORT"
      , "WO_REPORT"
    };
    
    public static File genEAMReport(int reportType, String parm) throws Exception{
        File outFile = null;
        Connection conn = null;
        Exception tempE = null;
        String parms[] = {parm};
        try {
            conn = ConnectionProvider.getConnection();
            outFile = XMLReportUtil.genReport(conn, XMLReportUtil.RTF_TEMPLATE, TEMPLATE_CODE[reportType], XML_FUNCTIONS[reportType], parms, FILE_PREFIX[reportType]);
        } catch (Exception e) {
            tempE = e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {}
                conn = null;
            }
        }
        if (tempE != null) throw tempE;
        return outFile;
    }
}
