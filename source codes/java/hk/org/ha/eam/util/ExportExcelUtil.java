/* --------------------------------------
    File Name: ExportExcelUtil.java
    Author: Jimmy Wong (PCCW)
    Date: 9-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
	- General method for Excel file creation and export

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.6>		20171207	Carmen Ng	Fixed column order for work request and date formats
        <1.5>           20171129        Jimmy Wong      Fixed String / long sorting issue
        <1.4>           20171128        Jimmy Wong      ST08208 for number ordering
        <1.3>           20171123        Jimmy Wong      ST08208 fix
	<1.2>		20171117	Jimmy Wong	(ST08157 fix) Added FILENAME_PREFIX for different template
	<1.1>		20171107	Jimmy Wong	Updated to accommadate for 1000+ records
	<1.0>		20171006	Jimmy Wong	Initial version
   -------------------------------------- */

package hk.org.ha.eam.util;

import hk.org.ha.ebs.common.EBSUtil;
import hk.org.ha.ebs.common.DateUtil2;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import javax.servlet.http.HttpSession;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class ExportExcelUtil {
    
    public static final int WORK_REQUEST_EXCEL = 0;
    public static final int WORK_ORDER_EXCEL = 1;
    
    private static final int SQL_ROW_LIMIT = 999;
	
    private static final String[] FILENAME_PREFIX = {"Work_Request_Export", "Work_Order_Export"};

    private static String TEMPLATE_SQL[] = {
                "select " + 
                    "V.ASSET_NUMBER, " + 
                    "V.ASSET_DESCRIPTION, " + 
                    "V.ITEM_CODE_NUMBER, " + 
                    "V.ASSET_STATUS, " + 
                    "TO_CHAR(V.DOB, 'dd/MM/yyyy'), " + 
                    "V.SERIAL_NUMBER, " + 
                    "V.ASSET_LOCATION_CODE, " + 
                    "V.ASSET_OWNER, " + 
                    "V.ASSET_OWNING_DEPARTMENT, " + 
                    "V.MANUFACTURER, " + 
                    "V.BRAND, " + 
                    "V.MODEL, " +
                    "V.PURCHASE_PRICE,  " + 
                    "V.ASSET_SUPPLIER_NAME, " + 
                    "V.RISK_LEVEL, " + 
                    "V.WARRANTY_BODY_NAME, " + 
                    "TO_CHAR(V.WARRANTY_JOIN_DATE, 'dd/MM/yyyy'), " + 
                    "TO_CHAR(V.WARRANTY_EXPIRY_DATE, 'dd/MM/yyyy'), " + 
                    "V.PARENT_ASSET_NUMBER, " + 
                    "CASE WHEN " + 
                    "ITEM_CODE_NUMBER in (select MEANING from MFG_LOOKUPS where LOOKUP_TYPE = 'XXEAM_CRITICAL_MEDICAL_EQUIP' and ENABLED_FLAG = 'Y' and nvl(trunc(START_DATE_ACTIVE),trunc(sysdate)) <= trunc(sysdate) and nvl(TRUNC(END_DATE_ACTIVE),trunc(sysdate)) >= trunc(sysdate)) " +
                    "THEN 'YES' " + 
                    "ELSE 'NO' " + 
                    "END as critical, " + 
                    "V.ORGANIZATION_CODE, " +
                    "V.WORK_REQUEST_NUMBER, " + 
                    "V.WORK_REQUEST_STATUS, " + 
                    "V.WORK_REQUEST_TYPE,  " + 
                    "TO_CHAR(V.CM_BREAKDOWN_DATE, 'dd/MM/yyyy hh24:MI:SS'), " + 
                    "TO_CHAR(V.PM_SCHEDULE_DATE, 'dd/MM/yyyy hh24:MI:SS'),  " + 
                    "V.EQUIPMENT_SENT_TO_WORKSHOP, " + 		
                    "V.DISINFECTION_STATUS, " + 
                    "V.REQUESTED_FOR,  " + 
                    "V.HA_CONTACT_PERSON, " + 
                    "V.HA_CONTACT_NUMBER, " + 
                    "V.HA_CONTACT_EMAIL,  " + 
                    "V.DESCRIPTION,  " + 
                    "V.CREATED_BY_EMPLOYEE, " +
                    "TO_CHAR(V.CREATION_DATE, 'dd/MM/yyyy hh24:MI:SS'), " + 
                    "V.WORK_ORDER_NUMBER, " + 
                    "V.WORK_ORDER_STATUS, " + 
                    "V.MAINTENANCE_BODY, " + 
                    "V.MAINTENANCE_BODY_TYPE,  " + 
                    "V.MAINTENANCE_CONTRACT_NUMBER, " + 
                    "V.SUPPLIER_AGREEMENT_NUMBER, " + 
                    "V.MAINTENANCE_PLAN,  " + 
                    "TO_CHAR(V.MAINTENANCE_JOIN_DATE, 'dd/MM/yyyy'), " + 
                    "TO_CHAR(V.MAINTENANCE_EXPIRY_DATE, 'dd/MM/yyyy'), " + 
                    "V.MAINTENANCE_INTERVAL,  " + 
                    "V.AUTO_SEND_WO_TO_SUPPLIER,  " + 
                    "V.MAINTENANCE_CONTACT_PERSON, " + 
                    "V.MAINTENANCE_CONTACT_PHONE, " + 
                    "V.MAINTENANCE_CONTACT_FAX, " + 
                    "V.MAINTENANCE_CONTACT_EMAIL " + 
                    "from XXEAM_EXT_WORK_REQUESTS_V V " + 
                    "WHERE V.WORK_REQUEST_NUMBER IN ("
                , "select " + 
                    "V.ASSET_NUMBER, " + 
                    "V.ASSET_DESCRIPTION, " + 
                    "CASE WHEN " + 
                    "ITEM_CODE_NUMBER in (select MEANING from MFG_LOOKUPS where LOOKUP_TYPE = 'XXEAM_CRITICAL_MEDICAL_EQUIP' and ENABLED_FLAG = 'Y' and nvl(trunc(START_DATE_ACTIVE),trunc(sysdate)) <= trunc(sysdate) and nvl(TRUNC(END_DATE_ACTIVE),trunc(sysdate)) >= trunc(sysdate)) " +
                    "THEN 'YES' " + 
                    "ELSE 'NO' " + 
                    "END as critical, " + 
                    "V.ITEM_CODE_NUMBER, " +
                    "V.ASSETGROUP, " + 
                    "V.ASSET_STATUS, " + 
                    "TO_CHAR(V.DOB, 'dd/MM/yyyy'), " + 
                    "V.SERIAL_NUMBER, " +
                    "V.ASSET_LOCATION_CODE, " +
                    "V.ASSET_OWNER, " +
                    "V.ASSET_OWNING_DEPARTMENT, " +
                    "V.MANUFACTURER, " +
                    "V.BRAND, " +
                    "V.MODEL, " +
                    "V.PURCHASE_PRICE, " +
                    "(select vendor_name from XXEAM_ASSET_SUPPLIER_DFF_V where vendor_number = V.asset_supplier) as asset_supplier_name, " +
                    "V.RISK_LEVEL, " +
                    "V.WARRANTY_BODY_NAME, " +
                    "TO_CHAR(V.WARRANTY_JOIN_DATE, 'dd/MM/yyyy'), " +
                    "TO_CHAR(V.WARRANTY_EXPIRY_DATE, 'dd/MM/yyyy'), " +
                    "V.FATHER_ASSET_NUMBER, " + 
                    "CASE WHEN " +
                    "DESCRIPTION = 'URGENT ORDER CREATED BY INTERFACE' " +
                    "THEN 'YES' " +
                    "ELSE 'NO' " +
                    "END as urgent, " +
                    "V.ORGANIZATIONCODE, " +
                    "V.WONUMBER, " +
                    "V.WOSTATUS, " +
                    "V.WOTYPE, " +
                    "decode(V.WOTYPE, 'Corrective', TO_CHAR(V.BREAKDOWNSCHEDULEDATE, 'dd/MM/yyyy hh24:MI:SS'), null) BREAKDOWNDATE, " +
                    "decode(V.WOTYPE, 'Preventive', TO_CHAR(V.BREAKDOWNSCHEDULEDATE, 'dd/MM/yyyy hh24:MI:SS'), null) SCHEDULEDATE, " +
                    "V.EQUIPSENTTOWORKSHOP, " +
                    "V.DISINFECTIONSTATUS, " + 
                    "V.HACONTACTPERSON, " +
                    "V.HACONTACTPHONE, " +
                    "V.HACONTACTEMAIL, " +
                    "V.DESCRIPTION, " +
                    "V.REMARKFORVENDOR, " +
                    "V.WRNUMBER, " +
                    "TO_CHAR(V.OUTBOUNDDATETIME, 'dd/MM/yyyy hh24:MI:SS'), " +
                    "V.CREATEDBYNAME, " +
                    "TO_CHAR(V.CREATIONDATE, 'dd/MM/yyyy hh24:MI:SS'), " +
                    "CASE\r\n" + 
                    "  WHEN\r\n" + 
                    "    (select vendor_name from xxeam_maintenance_body_dff_v where vendor_number = V.maintenance_body) IS NULL \r\n" + 
                    "      THEN\r\n" + 
                    "        '' \r\n" + 
                    "  ELSE\r\n" + 
                    "    (select vendor_name from xxeam_maintenance_body_dff_v where vendor_number = V.maintenance_body) || ' (' || V.maintenance_body || ')' \r\n" + 
                    "END, " + 
                    "V.MAINTENANCE_BODY_TYPE, " +
                    "V.CONTRACT_NUM, " +
                    "V.SUPPLIER_AGREEMENT_NUMBER, " +
                    "V.MAINTENANCE_PLAN, " +
                    "TO_CHAR(V.MAINTENANCE_JOIN_DATE, 'dd/MM/yyyy'), " + 
                    "TO_CHAR(V.MAINTENANCE_EXPIRY_DATE, 'dd/MM/yyyy'), " + 
                    "V.MAINTENANCE_INTERVAL, " +
                    "V.AUTO_SEND_WO_TO_SUPPLIER, " +
                    "V.MAINTENANCE_CONTACT_PERSON, " +
                    "V.MAINTENANCE_CONTACT_PHONE, " +
                    "V.MAINTENANCE_CONTACT_FAX_NUMBER, " +
                    "V.MAINTENANCE_CONTACT_EMAIL, " +
                    "TO_CHAR(V.CALLRECEIVEDDATETIME, 'dd/MM/yyyy hh24:MI:SS'), " + 
                    "TO_CHAR(V.EQUIPRECEIVEDDATETIME, 'dd/MM/yyyy hh24:MI:SS'), " +
                    "TO_CHAR(V.ATTENDANCEDATETIME, 'dd/MM/yyyy hh24:MI:SS'), " + 
                    "decode(V.WOTYPE, 'Corrective', TO_CHAR(V.REINSTATEMENTDATETIME, 'dd/MM/yyyy  hh24:MI:SS'), null) REINSTATEMENTDATE, " +
                    "V.FAILURECAUSECODE, " +
                    "V.FAILURESYMPTONCODE, " +
                    "V.FAILURERESOLCODE, " +
                    "V.EQUIPCONDITION, " +
                    "V.SPAREPARTCOST, " +
                    "V.SPAREPARTDESC, " +
                    "V.LABORCOST, " +
                    "TO_CHAR(V.response_date_time, 'dd/MM/yyyy  hh24:MI:SS'), " +
                    "V.TECHNICALNAME, " +
                    "V.RESULTANDACTION, " +
                    "V.SERVICREPORTREFERENCE, " +
                    "V.REMARKFORUSER, " +
                    "V.VENDORREFERENCENUMBER, " +
                    "TO_CHAR(V.actual_end_date, 'dd/MM/yyyy hh24:MI:SS'), " + 
                    "V.WOCOMPLETEDBY, " +
                    "V.ADDLABORCOST, " +
                    "V.ADDMATERIALCOST, " + 
                    "V.ADDMATERIALDESC " +
                "from XXEAM_EXT_WORK_ORDERS_V V " + 
                "WHERE V.WONUMBER IN ("};

    private static final String TEMPLATE_SQL_SUFFIX[] = {
        ") ORDER BY V.WORK_REQUEST_ID DESC"
        , ") ORDER BY V.WONUMBER DESC"
    };
    
    public static File exportExcel(HttpSession hSession, String[] infoArray, int templateId, String noList) throws Exception {
        Connection conn = null;
        String tempLocation = "";
        File outFile = null;
        Exception tempE = null;
        FileOutputStream outputStream = null;
        try {
            conn = ConnectionProvider.getInitConnection(hSession);
            tempLocation = EBSUtil.getSysParm(conn, "TEMP_LOCATION");
            do {
                // 20171006
                outFile = new File (tempLocation, FILENAME_PREFIX[templateId] + "_" + DateUtil2.getRandomFileName() + ".xlsx"); // 20171117
                // 20171006: end
            } while (outFile.exists());
            outputStream = new FileOutputStream(outFile);
            SXSSFWorkbook excelwb = new SXSSFWorkbook();
            excelwb.setCompressTempFiles(true);
            SXSSFSheet dtlsheet = (SXSSFSheet)excelwb.createSheet("export");
            int startRowId = 0;        

            if (templateId == WORK_REQUEST_EXCEL)
                startRowId = createWRTemplateHeader(excelwb, dtlsheet, infoArray);
            if (templateId == WORK_ORDER_EXCEL)
                startRowId = createWOTemplateHeader(excelwb, dtlsheet, infoArray);
            
            // 20171128 Start
            String noArray[] = sortLongArray(noList.split(","));
            
            //for (int i = 0; i < noArray.length;) {
            for (int i = noArray.length-1; i >= 0;) {
                String sql = TEMPLATE_SQL[templateId];
                int offSet = 0;
                for (int j = 0; j < SQL_ROW_LIMIT && i >= 0; j++, i--, offSet++) {
                    sql += "'" + noArray[i] + "',";
                }
                packDBData(conn, dtlsheet, startRowId, sql.substring(0, sql.length()-1) + TEMPLATE_SQL_SUFFIX[templateId]);
                startRowId += offSet;
            }
            // 20171128 End
            excelwb.write(outputStream);
        } catch (Exception e) {
            tempE = e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {}
                conn = null;
            }
            if(outputStream != null) {
            	outputStream.close();
            	outputStream = null;
            }
        }
        if (tempE != null) throw tempE;
        return outFile;
    }
    
    private static String[] sortLongArray (String inputStr[]) {
        String outputArray[] = new String[inputStr.length];
        long tempArray[] = new long[inputStr.length];
        try {
            for (int i = 0; i < outputArray.length; i++) {
                tempArray[i] = Long.parseLong(inputStr[i]);
            }
            Arrays.sort(tempArray);
            for (int i = 0; i < outputArray.length; i++) {
                outputArray[i] = Long.toString(tempArray[i]);
            }
        } catch (NumberFormatException e) {
            System.arraycopy(inputStr, 0, outputArray, 0, outputArray.length);
            Arrays.sort(outputArray);
        }
        return outputArray;
    }
    
    private static void packDBData(Connection dbConn, SXSSFSheet dtlsheet, int startRowId, String sql) throws SQLException{

        Statement st = null;
        ResultSet rs = null;
        
        try{
            st = dbConn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            rs = st.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            rs.beforeFirst();
            while (rs.next()) {
                SXSSFRow resultRow = (SXSSFRow)dtlsheet.createRow(startRowId++);
                for (int i = 1; i <= colCount; i++) {
                    SXSSFCell resultCell = (SXSSFCell)resultRow.createCell(i-1);
                    resultCell.setCellValue(rs.getString(i));
                }
            }            
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException e) {}
            }
            if (st != null) {
                try {
                    st.close();
                    st = null;
                } catch (SQLException e) {}
            }
        }        
    }
    
    private static int createWRTemplateHeader(SXSSFWorkbook excelwb, SXSSFSheet dtlsheet, String infoArray[]) {
        int rowId = 0;        
        // row 1
        XSSFCellStyle headerstyle1 = (XSSFCellStyle)excelwb.createCellStyle();

        Font headerfont1 = excelwb.createFont();
             headerfont1.setBoldweight(Font.BOLDWEIGHT_BOLD); 
             headerfont1.setFontHeightInPoints((short)18);
             headerfont1.setUnderline(HSSFFont.U_SINGLE);
             headerstyle1.setFont(headerfont1);
             
        SXSSFRow headerRow = (SXSSFRow)dtlsheet.createRow(rowId);
            SXSSFCell cell = (SXSSFCell)headerRow.createCell(0);
            cell.setCellValue("Work Request Export");
            cell.setCellStyle(headerstyle1);
        // End of row 1
        
        // Param Row
        for (String info : infoArray) { 
            if (info != null && !"".equals(info)) {
                String[] split = info.split("\t");
                if (split != null) {          
                    SXSSFRow Para = (SXSSFRow)dtlsheet.createRow(++rowId);
                    
                    int i = 0;
                    for (String splitter : split) {
                        if (splitter != null && !"".equals(splitter)) {                            
                            SXSSFCell cellParam = (SXSSFCell)Para.createCell(i++);
                            cellParam.setCellValue(splitter);
                        }
                    }
                }
            }
        }
        // End of Param Row
		
        dtlsheet.createRow(++rowId);
        
        // row Title
        XSSFCellStyle rowTAstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowTAstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
            rowTAstyle.setAlignment(CellStyle.ALIGN_CENTER);
            rowTAstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(253, 233, 217))); 
            rowTAstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowTAstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowTAstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowTAstyle.setBorderTop(CellStyle.BORDER_THIN);
        XSSFCellStyle rowTBstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowTBstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
            rowTBstyle.setAlignment(CellStyle.ALIGN_CENTER);
            rowTBstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(229, 244, 236))); 
            rowTBstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowTBstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowTBstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowTBstyle.setBorderTop(CellStyle.BORDER_THIN);
        XSSFCellStyle rowTCstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowTCstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
            rowTCstyle.setAlignment(CellStyle.ALIGN_CENTER);              
            rowTCstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(242, 221, 220))); 
            rowTCstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowTCstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowTCstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowTCstyle.setBorderTop(CellStyle.BORDER_THIN); 
        XSSFCellStyle rowTDstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowTDstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
            rowTDstyle.setAlignment(CellStyle.ALIGN_CENTER);    
            rowTDstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(219, 238, 243)));
            rowTDstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowTDstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowTDstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowTDstyle.setBorderTop(CellStyle.BORDER_THIN);             

        Font rowTfont = excelwb.createFont();
             rowTfont.setBoldweight(Font.BOLDWEIGHT_BOLD);  
             rowTfont.setFontHeightInPoints((short)16);
             rowTAstyle.setFont(rowTfont);
             rowTBstyle.setFont(rowTfont);
             rowTCstyle.setFont(rowTfont);
             rowTDstyle.setFont(rowTfont);             
             
        SXSSFRow assetDetail = (SXSSFRow)dtlsheet.createRow(++rowId);
        
        for (int i = 0; i <= 19; i++) {
            SXSSFCell rowTcell = (SXSSFCell)assetDetail.createCell(i);
            rowTcell.setCellStyle(rowTAstyle);
        }
        for (int i = 20; i <= 34; i++) {
            SXSSFCell rowTcell = (SXSSFCell)assetDetail.createCell(i);
            rowTcell.setCellStyle(rowTBstyle);
        }
        for (int i = 35; i <= 36; i++) {
            SXSSFCell rowTcell = (SXSSFCell)assetDetail.createCell(i);
            rowTcell.setCellStyle(rowTCstyle);
        }
        for (int i = 37; i <= 49; i++) {
            SXSSFCell rowTcell = (SXSSFCell)assetDetail.createCell(i);
            rowTcell.setCellStyle(rowTDstyle);
        }
       
        assetDetail.getCell(0).setCellValue("Asset Details");
        assetDetail.getCell(20).setCellValue("Request Details");
        assetDetail.getCell(35).setCellValue("Work Order Information");
        assetDetail.getCell(37).setCellValue("Maintenance Details");
        dtlsheet.addMergedRegion(new CellRangeAddress(rowId,rowId,0,19));
        dtlsheet.addMergedRegion(new CellRangeAddress(rowId,rowId,20,34));
        dtlsheet.addMergedRegion(new CellRangeAddress(rowId,rowId,35,36));
        dtlsheet.addMergedRegion(new CellRangeAddress(rowId,rowId,37,49));
        // End of row Title
        
        
        // row Column Name
        XSSFCellStyle rowCNAstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowCNAstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
            rowCNAstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(253, 233, 217))); 
            rowCNAstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowCNAstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowCNAstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowCNAstyle.setBorderTop(CellStyle.BORDER_THIN);
        XSSFCellStyle rowCNBstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowCNBstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
            rowCNBstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(229, 244, 236))); 
            rowCNBstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowCNBstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowCNBstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowCNBstyle.setBorderTop(CellStyle.BORDER_THIN);
        XSSFCellStyle rowCNCstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowCNCstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);              
            rowCNCstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(242, 221, 220)));  
            rowCNCstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowCNCstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowCNCstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowCNCstyle.setBorderTop(CellStyle.BORDER_THIN);
        XSSFCellStyle rowCNDstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowCNDstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
            rowCNDstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(219, 238, 243)));   
            rowCNDstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowCNDstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowCNDstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowCNDstyle.setBorderTop(CellStyle.BORDER_THIN);         

        Font rowCNfont = excelwb.createFont();
             rowCNfont.setBoldweight(Font.BOLDWEIGHT_BOLD);  
             rowCNfont.setFontHeightInPoints((short)12);
             rowCNAstyle.setFont(rowCNfont);
             rowCNBstyle.setFont(rowCNfont);
             rowCNCstyle.setFont(rowCNfont);
             rowCNDstyle.setFont(rowCNfont); 
        
        SXSSFRow columnName = (SXSSFRow)dtlsheet.createRow(++rowId);
        
        for (int i = 0; i <= 19; i++) {
            SXSSFCell rowCNCell = (SXSSFCell)columnName.createCell(i);
            rowCNCell.setCellStyle(rowCNAstyle);            
        }
        for (int i = 20; i <= 34; i++) {
            SXSSFCell rowCNCell = (SXSSFCell)columnName.createCell(i);
            rowCNCell.setCellStyle(rowCNBstyle);            
        }
        for (int i = 35; i <= 36; i++) {
            SXSSFCell rowCNCell = (SXSSFCell)columnName.createCell(i);
            rowCNCell.setCellStyle(rowCNCstyle);            
        }
        for (int i = 37; i <= 49; i++) {
            SXSSFCell rowCNCell = (SXSSFCell)columnName.createCell(i);
            rowCNCell.setCellStyle(rowCNDstyle);            
        }
        columnName.getCell(0).setCellValue("Asset Number");
        columnName.getCell(1).setCellValue("Asset Description");
        columnName.getCell(2).setCellValue("Item Code");
        columnName.getCell(3).setCellValue("Asset Status");
        columnName.getCell(4).setCellValue("Date of Birth");
        columnName.getCell(5).setCellValue("Serial Number");
        columnName.getCell(6).setCellValue("Location Code");
        columnName.getCell(7).setCellValue("Asset Owner");
        columnName.getCell(8).setCellValue("Owning Department");
        columnName.getCell(9).setCellValue("Manufacturer");
        columnName.getCell(10).setCellValue("Brand");
        columnName.getCell(11).setCellValue("Model");
        columnName.getCell(12).setCellValue("Purchase Price");
        columnName.getCell(13).setCellValue("Asset Supplier");
        columnName.getCell(14).setCellValue("Risk Level");
        columnName.getCell(15).setCellValue("Warranty Body");
        columnName.getCell(16).setCellValue("Warranty Join Date");
        columnName.getCell(17).setCellValue("Warranty Expiry Date");
        columnName.getCell(18).setCellValue("Parent Asset Number");
        columnName.getCell(19).setCellValue("Critical Medical Equipment Flag");
        columnName.getCell(20).setCellValue("EAM Org");
        columnName.getCell(21).setCellValue("Work Request Number");
        columnName.getCell(22).setCellValue("Work Request Status");
        columnName.getCell(23).setCellValue("Work Request Type");
        columnName.getCell(24).setCellValue("CM Breakdown Date Time");
        columnName.getCell(25).setCellValue("PM Scheduled Date Time");
        columnName.getCell(26).setCellValue("Equipment Sent to Workshop");
        columnName.getCell(27).setCellValue("Disinfection Status");
        columnName.getCell(28).setCellValue("Requested For");
        columnName.getCell(29).setCellValue("HA Contact Person");
        columnName.getCell(30).setCellValue("HA Contact Phone");
        columnName.getCell(31).setCellValue("HA Contact Email");
        columnName.getCell(32).setCellValue("Work Request Description");
        columnName.getCell(33).setCellValue("Work Request Creation By");
        columnName.getCell(34).setCellValue("Work Request Creation Date Time");
        columnName.getCell(35).setCellValue("Work Order Number");
        columnName.getCell(36).setCellValue("Work Order Status");
        columnName.getCell(37).setCellValue("Maintenance Body");
        columnName.getCell(38).setCellValue("Maintenance Body Type");
        columnName.getCell(39).setCellValue("Maintenance Contract Number");
        columnName.getCell(40).setCellValue("Supplier Agreement Number");
        columnName.getCell(41).setCellValue("Maintenance Plan");
        columnName.getCell(42).setCellValue("Maintenance Join Date");
        columnName.getCell(43).setCellValue("Maintenance Expiry Date");
        columnName.getCell(44).setCellValue("Maintenance Interval (Months)");
        columnName.getCell(45).setCellValue("Auto Send WO to Supplier");
        columnName.getCell(46).setCellValue("Maintenance Contact Person");
        columnName.getCell(47).setCellValue("Maintenance Contact Phone");
        columnName.getCell(48).setCellValue("Maintenance Contact Fax Number");
        columnName.getCell(49).setCellValue("Maintenance Contact Email");       
        // End of Column Name
        
        return ++rowId;
    }
    
    private static int createWOTemplateHeader(SXSSFWorkbook excelwb, SXSSFSheet dtlsheet, String infoArray[]) {
        int rowId = 0;
        
        // row 1
        XSSFCellStyle headerstyle1 = (XSSFCellStyle)excelwb.createCellStyle();

        Font headerfont1 = excelwb.createFont();
             headerfont1.setBoldweight(Font.BOLDWEIGHT_BOLD); 
             headerfont1.setFontHeightInPoints((short)18);
             headerfont1.setUnderline(HSSFFont.U_SINGLE);
             headerstyle1.setFont(headerfont1);
             
        SXSSFRow headerRow = (SXSSFRow)dtlsheet.createRow(rowId);        
            SXSSFCell cell = (SXSSFCell)headerRow.createCell(0);
            cell.setCellValue("Work Order Export");
            cell.setCellStyle(headerstyle1);
        // End of row 1
        
        // Param Row
        for (String info : infoArray) { 
            if (info != null && !"".equals(info)) {
                String[] split = info.split("\t");
                if (split != null) {          
                    SXSSFRow Para = (SXSSFRow)dtlsheet.createRow(++rowId);
                    
                    int i = 0;
                    for (String splitter : split) {
                        if (splitter != null && !"".equals(splitter)) {                            
                            SXSSFCell cellParam = (SXSSFCell)Para.createCell(i++);
                            cellParam.setCellValue(splitter);
                        }
                    }
                }
            }
        }
        // End of Param Row        
 
        dtlsheet.createRow(++rowId);
        
        // row Title
        XSSFCellStyle rowTAstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowTAstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
            rowTAstyle.setAlignment(CellStyle.ALIGN_CENTER);
            rowTAstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(253, 233, 217))); 
            rowTAstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowTAstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowTAstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowTAstyle.setBorderTop(CellStyle.BORDER_THIN);
        XSSFCellStyle rowTBstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowTBstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
            rowTBstyle.setAlignment(CellStyle.ALIGN_CENTER);
            rowTBstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(229, 244, 236))); 
            rowTBstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowTBstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowTBstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowTBstyle.setBorderTop(CellStyle.BORDER_THIN);
        XSSFCellStyle rowTCstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowTCstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
            rowTCstyle.setAlignment(CellStyle.ALIGN_CENTER);              
            rowTCstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(242, 221, 220))); 
            rowTCstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowTCstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowTCstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowTCstyle.setBorderTop(CellStyle.BORDER_THIN); 
        XSSFCellStyle rowTDstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowTDstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
            rowTDstyle.setAlignment(CellStyle.ALIGN_CENTER);    
            rowTDstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(219, 238, 243)));
            rowTDstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowTDstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowTDstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowTDstyle.setBorderTop(CellStyle.BORDER_THIN);  
       XSSFCellStyle rowTEstyle = (XSSFCellStyle)excelwb.createCellStyle();
	       rowTEstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
	       rowTEstyle.setAlignment(CellStyle.ALIGN_CENTER);    
	       rowTEstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(221, 217, 196)));
	       rowTEstyle.setBorderBottom(CellStyle.BORDER_THIN);
	       rowTEstyle.setBorderLeft(CellStyle.BORDER_THIN);
	       rowTEstyle.setBorderRight(CellStyle.BORDER_THIN);
	       rowTEstyle.setBorderTop(CellStyle.BORDER_THIN);  

        Font rowTfont = excelwb.createFont();
             rowTfont.setBoldweight(Font.BOLDWEIGHT_BOLD);  
             rowTfont.setFontHeightInPoints((short)16);
             rowTAstyle.setFont(rowTfont);
             rowTBstyle.setFont(rowTfont);
             rowTCstyle.setFont(rowTfont);
             rowTDstyle.setFont(rowTfont);   
             rowTEstyle.setFont(rowTfont);   
             
        SXSSFRow assetDetail = (SXSSFRow)dtlsheet.createRow(++rowId);
        
        for (int i = 0; i <= 21; i++) {
            SXSSFCell rowTcell = (SXSSFCell)assetDetail.createCell(i);
            rowTcell.setCellStyle(rowTAstyle);
        }
        for (int i = 22; i <= 38; i++) {
            SXSSFCell rowTcell = (SXSSFCell)assetDetail.createCell(i);
            rowTcell.setCellStyle(rowTBstyle);
        }
        for (int i = 39; i <= 51; i++) {
            SXSSFCell rowTcell = (SXSSFCell)assetDetail.createCell(i);
            rowTcell.setCellStyle(rowTCstyle);
        }
        for (int i = 52; i <= 67; i++) {
            SXSSFCell rowTcell = (SXSSFCell)assetDetail.createCell(i);
            rowTcell.setCellStyle(rowTDstyle);
        }
        for (int i = 68; i <= 73; i++) {
            SXSSFCell rowTcell = (SXSSFCell)assetDetail.createCell(i);
            rowTcell.setCellStyle(rowTEstyle);
        }
       
        assetDetail.getCell(0).setCellValue("Asset Details");
        assetDetail.getCell(22).setCellValue("Order Details");
        assetDetail.getCell(39).setCellValue("Maintenance Details");
        assetDetail.getCell(52).setCellValue("Repair Details");
        assetDetail.getCell(68).setCellValue("Completion Details");
        dtlsheet.addMergedRegion(new CellRangeAddress(rowId,rowId,0,21));
        dtlsheet.addMergedRegion(new CellRangeAddress(rowId,rowId,22,38));
        dtlsheet.addMergedRegion(new CellRangeAddress(rowId,rowId,39,51));
        dtlsheet.addMergedRegion(new CellRangeAddress(rowId,rowId,52,67));
        dtlsheet.addMergedRegion(new CellRangeAddress(rowId,rowId,68,73));
        // End of row Title        
        
        // row Column Name
        XSSFCellStyle rowCNAstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowCNAstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
            rowCNAstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(253, 233, 217))); 
            rowCNAstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowCNAstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowCNAstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowCNAstyle.setBorderTop(CellStyle.BORDER_THIN);
        XSSFCellStyle rowCNBstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowCNBstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
            rowCNBstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(229, 244, 236))); 
            rowCNBstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowCNBstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowCNBstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowCNBstyle.setBorderTop(CellStyle.BORDER_THIN);
        XSSFCellStyle rowCNCstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowCNCstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);              
            rowCNCstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(242, 221, 220)));  
            rowCNCstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowCNCstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowCNCstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowCNCstyle.setBorderTop(CellStyle.BORDER_THIN);
        XSSFCellStyle rowCNDstyle = (XSSFCellStyle)excelwb.createCellStyle();
            rowCNDstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
            rowCNDstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(219, 238, 243)));   
            rowCNDstyle.setBorderBottom(CellStyle.BORDER_THIN);
            rowCNDstyle.setBorderLeft(CellStyle.BORDER_THIN);
            rowCNDstyle.setBorderRight(CellStyle.BORDER_THIN);
            rowCNDstyle.setBorderTop(CellStyle.BORDER_THIN);     
        XSSFCellStyle rowCNEstyle = (XSSFCellStyle)excelwb.createCellStyle();
	        rowCNEstyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);  
	        rowCNEstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(221, 217, 196)));   
	        rowCNEstyle.setBorderBottom(CellStyle.BORDER_THIN);
	        rowCNEstyle.setBorderLeft(CellStyle.BORDER_THIN);
	        rowCNEstyle.setBorderRight(CellStyle.BORDER_THIN);
	        rowCNEstyle.setBorderTop(CellStyle.BORDER_THIN);     

        Font rowCNfont = excelwb.createFont();
             rowCNfont.setBoldweight(Font.BOLDWEIGHT_BOLD);  
             rowCNfont.setFontHeightInPoints((short)12);
             rowCNAstyle.setFont(rowCNfont);
             rowCNBstyle.setFont(rowCNfont);
             rowCNCstyle.setFont(rowCNfont);
             rowCNDstyle.setFont(rowCNfont); 
             rowCNEstyle.setFont(rowCNfont); 
             
        SXSSFRow columnName = (SXSSFRow)dtlsheet.createRow(++rowId);
        
        for (int i = 0; i <= 21; i++) {
            SXSSFCell rowCNCell = (SXSSFCell)columnName.createCell(i);
            rowCNCell.setCellStyle(rowCNAstyle);            
        }
        for (int i = 22; i <= 38; i++) {
            SXSSFCell rowCNCell = (SXSSFCell)columnName.createCell(i);
            rowCNCell.setCellStyle(rowCNBstyle);            
        }
        for (int i = 39; i <= 51; i++) {
            SXSSFCell rowCNCell = (SXSSFCell)columnName.createCell(i);
            rowCNCell.setCellStyle(rowCNCstyle);            
        }
        for (int i = 52; i <= 67; i++) {
            SXSSFCell rowCNCell = (SXSSFCell)columnName.createCell(i);
            rowCNCell.setCellStyle(rowCNDstyle);            
        }
        for (int i = 68; i <= 73; i++) {
            SXSSFCell rowCNCell = (SXSSFCell)columnName.createCell(i);
            rowCNCell.setCellStyle(rowCNEstyle);            
        }
        columnName.getCell(0).setCellValue("Asset Number");
        columnName.getCell(1).setCellValue("Asset Description");
        columnName.getCell(2).setCellValue("Critical Medical Equipment Flag");
        columnName.getCell(3).setCellValue("Item Code");
        columnName.getCell(4).setCellValue("Asset Group");
        columnName.getCell(5).setCellValue("Asset Status");
        columnName.getCell(6).setCellValue("Date of Birth");
        columnName.getCell(7).setCellValue("Serial Number");
        columnName.getCell(8).setCellValue("Location Code");
        columnName.getCell(9).setCellValue("Asset Owner");
        columnName.getCell(10).setCellValue("Owning Department");
        columnName.getCell(11).setCellValue("Manufacturer");
        columnName.getCell(12).setCellValue("Brand");
        columnName.getCell(13).setCellValue("Model");
        columnName.getCell(14).setCellValue("Purchase Price");
        columnName.getCell(15).setCellValue("Asset Supplier");
        columnName.getCell(16).setCellValue("Risk Level");
        columnName.getCell(17).setCellValue("Warranty Body");
        columnName.getCell(18).setCellValue("Warranty Join Date");
        columnName.getCell(19).setCellValue("Warranty Expiry Date");
        columnName.getCell(20).setCellValue("Parent Asset Number");
        columnName.getCell(21).setCellValue("Urgent Work Order");
        columnName.getCell(22).setCellValue("EAM Org");
        columnName.getCell(23).setCellValue("Work Order Number");
        columnName.getCell(24).setCellValue("Work Order Status");
        columnName.getCell(25).setCellValue("Work Order Type");
        columnName.getCell(26).setCellValue("CM Breakdown Date Time");
        columnName.getCell(27).setCellValue("PM Scheduled Date Time");
        columnName.getCell(28).setCellValue("Equipment Sent to Workshop");
        columnName.getCell(29).setCellValue("Disinfection Status");
        columnName.getCell(30).setCellValue("HA Contact Person");
        columnName.getCell(31).setCellValue("HA Contact Phone");
        columnName.getCell(32).setCellValue("HA Contact Email");
        columnName.getCell(33).setCellValue("Work Order Description");
        columnName.getCell(34).setCellValue("Remarks to Vendor");
        columnName.getCell(35).setCellValue("Work Request Number");
        columnName.getCell(36).setCellValue("Outbound Date Time");
        columnName.getCell(37).setCellValue("Work Order Creation By");
        columnName.getCell(38).setCellValue("Work Order Creation Date Time");
        columnName.getCell(39).setCellValue("Maintenance Body");
        columnName.getCell(40).setCellValue("Maintenance Body Type");
        columnName.getCell(41).setCellValue("Maintenance Contract Number");
        columnName.getCell(42).setCellValue("Supplier Agreement Number");
        columnName.getCell(43).setCellValue("Maintenance Plan");
        columnName.getCell(44).setCellValue("Maintenance Join Date");
        columnName.getCell(45).setCellValue("Maintenance Expiry Date");
        columnName.getCell(46).setCellValue("Maintenance Interval (Months)");
        columnName.getCell(47).setCellValue("Auto Send WO to Supplier");
        columnName.getCell(48).setCellValue("Maintenance Contact Person");
        columnName.getCell(49).setCellValue("Maintenance Contact Phone"); 
        columnName.getCell(50).setCellValue("Maintenance Contact Fax Number");
        columnName.getCell(51).setCellValue("Maintenance Contact Email");
        columnName.getCell(52).setCellValue("Call Received Date Time");
        columnName.getCell(53).setCellValue("Equipment Received Date Time");
        columnName.getCell(54).setCellValue("Attendance Date Time");
        columnName.getCell(55).setCellValue("CM Reinstatement Date Time");
        columnName.getCell(56).setCellValue("Failure Cause Code");
        columnName.getCell(57).setCellValue("Failure Symptom Code");
        columnName.getCell(58).setCellValue("Repair Resolution Code");
        columnName.getCell(59).setCellValue("Equipment Condition");
        columnName.getCell(60).setCellValue("Spare Part Cost");
        columnName.getCell(61).setCellValue("Spare Part Description");
        columnName.getCell(62).setCellValue("Labor Cost");
        columnName.getCell(63).setCellValue("Response Date Time");
        columnName.getCell(64).setCellValue("Technician Name");
        columnName.getCell(65).setCellValue("Result and Action Taken");
        columnName.getCell(66).setCellValue("Service Report Reference");  
        columnName.getCell(67).setCellValue("Remarks to User");
        columnName.getCell(68).setCellValue("Vendor Reference Number");
        columnName.getCell(69).setCellValue("PM Completion Date Time");
        columnName.getCell(70).setCellValue("Work Order Completed by");
        columnName.getCell(71).setCellValue("Additional Labor Cost");
        columnName.getCell(72).setCellValue("Additional Material Cost");
        columnName.getCell(73).setCellValue("Additional Material Description");
      
        // End of Column Name        
        
        return ++rowId;
    }
    
}
