/* --------------------------------------
    File Name: getdata.java
    Author: Kylie Wu (PCCW)
    Date: 7-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Get Work Request data servlet

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171007	Kylie Wu	Initial version
   -------------------------------------- */
package hk.org.ha.eam.util;

import hk.org.ha.eam.util.RSConvertor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Scanner;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

/**
 *
 * @author pwerp_kyliewu
 */
@Repository
//@Controller(name = "getdata", urlPatterns = {"/getdata"})
public class getdata extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String wrNumber = request.getParameter("wrNumber");
        String assetNumber = request.getParameter("assetNumber");
        String wrType = request.getParameter("wrType");
        String wrStatus = request.getParameter("wrStatus");
        String dateType = request.getParameter("dateType");
        String dateFrom = request.getParameter("dateFrom");
        String dateTo = request.getParameter("dateTo");
        String maintenanceVendor = request.getParameter("maintenanceVendor");
        String assetLocation = request.getParameter("assetLocation");
        String assetOwner = request.getParameter("assetOwner");
        String owningDept = request.getParameter("owningDept");
        String createdBy = request.getParameter("createdBy");
        String org = request.getParameter("org");
        String criticalOnly = request.getParameter("criticalOnly");
        
        response.setContentType("text/html;charset=UTF-8");
        Connection dbConn = null;
        String query = "";
        Statement st = null;
        PrintWriter out = null;
        ResultSet rs = null;
        try {
            
            out = response.getWriter();
            dbConn = ConnectionProvider.getConnection();
            st = dbConn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            query = "select \n" +
                    "	WR.ASSET_NUMBER assetNumber,\n" +
                    "	CII.INSTANCE_DESCRIPTION assetDescription,\n" +
                    "   ITEM.SEGMENT1 itemCode,\n" +
                    "	ASSET_ATTR.C_ATTRIBUTE2 assetStatus,\n" +
                    "	ASSET_ATTR.D_ATTRIBUTE1 dateOfBirth,\n" +
                    "	CII.SERIAL_NUMBER serialNumber,\n" +
                    "	MEL.LOCATION_CODES assetLocation,\n" +
                    "	OWNER_ATTR.C_ATTRIBUTE4 assetOwner,\n" +
                    "	BD.DEPARTMENT_CODE owningDept,\n" +
                    "	MDM_ATTR.C_ATTRIBUTE1 manufacturer,\n" +
                    "	MDM_ATTR.C_ATTRIBUTE2 brand,\n" +
                    "	MDM_ATTR.C_ATTRIBUTE3 model,\n" +
                    "	PO_ATTR.C_ATTRIBUTE6 assetSupplier,\n" +
                    "	xxeam_asset_search_util_pkg.get_risk_level(121,po_attr.c_attribute7,null) riskLevel,\n" +
                    "	CASE \n" +
                    "		WHEN (MP_PARENT.MAINT_ORGANIZATION_ID IS NOT NULL) AND (MP_PARENT.MAINT_ORGANIZATION_ID  != FND_PROFILE.VALUE('MFG_ORGANIZATION_ID'))\n" +
                    "		THEN CII_PARENT.INSTANCE_NUMBER || '(' || MP_PARENT.ORGANIZATION_CODE || ')'\n" +
                    "		ELSE CII_PARENT.INSTANCE_NUMBER\n" +
                    "	END parentAssetNumber,\n" +
                    "	PO_ATTR.N_ATTRIBUTE2 purchasePrice,\n" +
                    "	WR.WORK_REQUEST_NUMBER wrNumber,\n" +
                    "	LV1.MEANING wrStatus,\n" +
                    "	LV3.MEANING wrType,\n" +
                    "	WREXT.CM_BREAKDOWN_DATE cmBreakdownDate,\n" +
                    "	WREXT.PM_SCHEDULE_DATE pmScheduleDate,\n" +
                    "	WR.CREATION_DATE createdDate,\n" +
                    "	FU.USER_NAME || '(' || WR.WORK_REQUEST_CREATED_BY || ')' createdBy,\n" +
                    "	WREXT.DISINFECTION_STATUS disinfectionStatus,\n" +
                    "	WREXT.EQUIPMENT_SENT_TO_WORKSHOP equipSentToWorkshop,\n" +
                    "	WR.ATTRIBUTE14 haContactPerson,\n" +
                    "	WR.PHONE_NUMBER haContactPhone,\n" +
                    "	WR.E_MAIL haContactEmail,\n" +
                    "	WR.CREATED_FOR requestedFor,\n" +
                    "	WR.DESCRIPTION description,\n" +
                    "	LOV1.vendor_name || '(' || LOV1.segment1 || ')' maintenanceVendor,\n" +
                    "	WR.ATTRIBUTE2 maintenanceVendorType,\n" +
                    "	WR.ATTRIBUTE3 contractNumber,\n" +
                    "	WR.ATTRIBUTE4 maintenancePlan,\n" +
                    "	WR.ATTRIBUTE5 maintenanceJoinDate,\n" +
                    "	WR.ATTRIBUTE6 maintenanceExpiryDate,\n" +
                    "	WR.ATTRIBUTE7 supplierAgreementNumber,\n" +
                    "	WR.ATTRIBUTE8 autoSend,\n" +
                    "	WR.ATTRIBUTE10 maintenanceContactPerson,\n" +
                    "	WR.ATTRIBUTE11 maintenanceContactPhone,\n" +
                    "	WR.ATTRIBUTE12 maintenanceContactFax,\n" +
                    "	WR.ATTRIBUTE13 maintenanceContactEmail,\n" +
                    "	WE.WIP_ENTITY_NAME woNumber, \n" +
                    "   MP.organization_code org, \n" +
                    "   ewsv.work_order_status woStatus \n" +
                    "from\n" +
                    "	WIP_EAM_WORK_REQUESTS WR,\n" +
                    "	WIP_EAM_WORK_REQUESTS_EXT WREXT,\n" +
                    "	CSI_ITEM_INSTANCES CII,\n" +
                    "	MTL_EAM_ASSET_ATTR_VALUES PO_ATTR,\n" +
                    "	MTL_EAM_ASSET_ATTR_VALUES ASSET_ATTR,\n" +
                    "	MTL_EAM_ASSET_ATTR_VALUES OWNER_ATTR,\n" +
                    "	MTL_EAM_ASSET_ATTR_VALUES MDM_ATTR,\n" +
                    "	MTL_EAM_LOCATIONS MEL,\n" +
                    "	EAM_ORG_MAINT_DEFAULTS EOMD,\n" +
                    "	BOM_DEPARTMENTS BD,\n" +
                    "	MTL_SYSTEM_ITEMS_B_KFV MSI_PARENT,\n" +
                    "	MTL_SERIAL_NUMBERS MSN,\n" +
                    "	MTL_OBJECT_GENEALOGY MOG,\n" +
                    "	MTL_SERIAL_NUMBERS MSN_PARENT,\n" +
                    "	CSI_ITEM_INSTANCES CII_PARENT,\n" +
                    "	MTL_PARAMETERS MP_PARENT,\n" +
                    "	MFG_LOOKUPS LV1,\n" +
                    "	MFG_LOOKUPS LV3,\n" +
                    "	WIP_ENTITIES WE, \n" +
                    "	AP_SUPPLIERS LOV1, \n" +
                    "   FND_USER FU, \n" +
                    "   MTL_PARAMETERS MP, \n" +
                    "   eam_work_order_details ewod, \n" +
                    "   EAM_WO_STATUSES_V ewsv,\n" +
                    "   ( SELECT MSIB.INVENTORY_ITEM_ID, MSIB.SEGMENT1 FROM MTL_SYSTEM_ITEMS_B MSIB, HR_ORGANIZATION_UNITS HOU WHERE MSIB.ORGANIZATION_ID = HOU.ORGANIZATION_ID AND HOU.NAME = 'ITEM MASTER' ) ITEM \n" +
                    "where WR.WORK_REQUEST_ID = WREXT.WORK_REQUEST_ID (+)\n" +
                    "AND   WR.MAINTENANCE_OBJECT_ID = CII.INSTANCE_ID (+)\n" +
                    "AND   WR.MAINTENANCE_OBJECT_ID = PO_ATTR.MAINTENANCE_OBJECT_ID (+)\n" +
                    "AND   PO_ATTR.ATTRIBUTE_CATEGORY (+) = 'Purchase Order Details'\n" +
                    "AND   WR.MAINTENANCE_OBJECT_ID = ASSET_ATTR.MAINTENANCE_OBJECT_ID (+)\n" +
                    "AND   ASSET_ATTR.ATTRIBUTE_CATEGORY (+) = 'Asset Details'\n" +
                    "AND   WR.MAINTENANCE_OBJECT_ID = OWNER_ATTR.MAINTENANCE_OBJECT_ID (+)\n" +
                    "AND   OWNER_ATTR.ATTRIBUTE_CATEGORY (+) = 'Ownership Details'\n" +
                    "AND   WR.MAINTENANCE_OBJECT_ID = MDM_ATTR.MAINTENANCE_OBJECT_ID (+)\n" +
                    "AND   MDM_ATTR.ATTRIBUTE_CATEGORY (+) = 'MBM Details'\n" +
                    "AND   MEL.LOCATION_ID (+) = EOMD.AREA_ID\n" +
                    "AND   CII.INSTANCE_ID = EOMD.OBJECT_ID (+)\n" +
                    "AND   EOMD.OBJECT_TYPE(+) = 50\n" +
                    "AND   (EOMD.ORGANIZATION_ID IS NULL OR EOMD.ORGANIZATION_ID = WR.ORGANIZATION_ID)\n" +
                    "AND   BD.DEPARTMENT_ID (+) = WR.WORK_REQUEST_OWNING_DEPT\n" +
                    "AND   LV1.LOOKUP_TYPE (+) = 'WIP_EAM_WORK_REQ_STATUS'\n" +
                    "AND   LV1.LOOKUP_CODE (+) = WR.WORK_REQUEST_STATUS_ID\n" +
                    "AND   LV1.ENABLED_FLAG (+) = 'Y'\n" +
                    "AND   NVL(TRUNC(LV1.START_DATE_ACTIVE (+)), TRUNC(SYSDATE)) <= TRUNC(SYSDATE) \n" +
                    "AND   NVL(TRUNC(LV1.END_DATE_ACTIVE (+)),TRUNC(SYSDATE)) >= TRUNC(SYSDATE)\n" +
                    "AND   LV3.LOOKUP_TYPE (+) = 'WIP_EAM_WORK_REQ_TYPE'\n" +
                    "AND   LV3.LOOKUP_CODE (+) = WR.WORK_REQUEST_TYPE_ID\n" +
                    "AND   LV3.ENABLED_FLAG (+) = 'Y'\n" +
                    "AND   NVL(TRUNC(LV3.START_DATE_ACTIVE (+)),TRUNC(SYSDATE)) <= TRUNC(SYSDATE) \n" +
                    "AND   NVL(TRUNC(LV3.END_DATE_ACTIVE (+)),TRUNC(SYSDATE)) >= TRUNC(SYSDATE)\n" +
                    "AND   CII.INVENTORY_ITEM_ID  = MSN.INVENTORY_ITEM_ID (+)\n" +
                    "AND   CII.SERIAL_NUMBER = MSN.SERIAL_NUMBER (+)\n" +
                    "AND   MSN.GEN_OBJECT_ID = MOG.OBJECT_ID(+)\n" +
                    "AND   MOG.PARENT_OBJECT_ID = MSN_PARENT.GEN_OBJECT_ID(+)\n" +
                    "AND   MOG.GENEALOGY_TYPE(+) = 5\n" +
                    "AND   SYSDATE >= NVL(MOG.START_DATE_ACTIVE(+), SYSDATE)\n" +
                    "AND   SYSDATE <= NVL(MOG.END_DATE_ACTIVE(+), SYSDATE)\n" +
                    "AND   MSN_PARENT.INVENTORY_ITEM_ID = CII_PARENT.INVENTORY_ITEM_ID (+)\n" +
                    "AND   MSN_PARENT.SERIAL_NUMBER = CII_PARENT.SERIAL_NUMBER (+)\n" +
                    "AND   CII_PARENT.INVENTORY_ITEM_ID = MSI_PARENT.INVENTORY_ITEM_ID(+)\n" +
                    "AND   CII_PARENT.LAST_VLD_ORGANIZATION_ID = MSI_PARENT.ORGANIZATION_ID(+)\n" +
                    "AND   CII_PARENT.LAST_VLD_ORGANIZATION_ID = MP_PARENT.ORGANIZATION_ID (+)\n" +
                    "AND   WR.WIP_ENTITY_ID = WE.WIP_ENTITY_ID (+)\n" +
                    "AND   WR.ORGANIZATION_ID = WE.ORGANIZATION_ID (+)\n" +
                    "AND   WR.ATTRIBUTE1 = LOV1.segment1 (+)\n" +
                    "AND   WR.WORK_REQUEST_CREATED_BY = FU.USER_ID (+)\n" +
                    "AND   WR.organization_id = MP.organization_id (+)\n" +
                    "AND   WE.WIP_ENTITY_ID = ewod.WIP_ENTITY_ID (+)\n" +
                    "AND   ewsv.status_id (+) = ewod.user_defined_status_id \n" +
                    "AND   po_attr.c_attribute7 = ITEM.INVENTORY_ITEM_ID (+) ";
            
            if(org != null && org != ""){
                query = query + "AND MP.ORGANIZATION_CODE = '" + org + "' ";
            }
            
            if(wrNumber != null && wrNumber != ""){
                query = query + "AND WR.WORK_REQUEST_NUMBER = '" + wrNumber + "' ";
            }
            
            if(assetNumber != null && assetNumber != ""){
                query = query + "AND WR.ASSET_NUMBER = '" + assetNumber + "' ";
            }
            
            if(wrType != null && wrType != ""){
                query = query + "AND LV3.MEANING = '" + wrType + "' ";
            }
            
            if(wrStatus != null && wrStatus != ""){
                wrStatus = wrStatus.replace(",","', '");
                query = query + "AND LV1.MEANING in ('" + wrStatus + "') ";
            }
            
            
            if("Breakdown Date".equals(dateType)) {
                if(dateFrom != null){
                    query = query + "AND trunc(WREXT.CM_BREAKDOWN_DATE) >= trunc(to_date('" + dateFrom + "', 'dd/mm/yyyy')) ";
                }   
                if(dateTo != null){
                    query = query + "AND trunc(WREXT.CM_BREAKDOWN_DATE) <= trunc(to_date('" + dateTo + "', 'dd/mm/yyyy')) ";
                }   
            }else if("Created Date".equals(dateType)) {
                if(dateFrom != null){
                        query = query + "AND trunc(WR.CREATION_DATE) >= trunc(to_date('" + dateFrom + "', 'dd/mm/yyyy')) ";
                }   
                if(dateTo != null){
                    query = query + "AND trunc(WR.CREATION_DATE) <= trunc(to_date('" + dateTo + "', 'dd/mm/yyyy')) ";
                }   
            }else if("Schedule Date".equals(dateType)) {
                if(dateFrom != null){
                    query = query + "AND trunc(WREXT.PM_SCHEDULE_DATE) >= trunc(to_date('" + dateFrom + "', 'dd/mm/yyyy')) ";
                }   
                if(dateTo != null){
                    query = query + "AND trunc(WREXT.PM_SCHEDULE_DATE) <= trunc(to_date('" + dateTo + "', 'dd/mm/yyyy')) ";
                }   
            }
                        
            if(maintenanceVendor != null && maintenanceVendor != ""){
                query = query + "AND WR.ATTRIBUTE1 = '" + maintenanceVendor + "' ";
            }
            
            if(assetLocation != null && assetLocation != ""){
                query = query + "AND MEL.LOCATION_CODES = '" + assetLocation + "' ";
            }
            
            if(assetOwner != null && assetOwner != ""){
                query = query + "AND OWNER_ATTR.C_ATTRIBUTE4 = '" + assetOwner + "' ";
            }
            
            if(owningDept != null && owningDept != ""){
                query = query + "AND BD.DEPARTMENT_CODE = '" + owningDept + "' ";
            }
            
            if(createdBy != null && createdBy != ""){
                query = query + "AND WR.CREATED_BY = '" + createdBy + "' ";
            }
            if("true".equals(criticalOnly)){
                query = query + "AND ITEM.SEGMENT1 in (select MEANING from MFG_LOOKUPS where LOOKUP_TYPE = 'XXEAM_CRITICAL_MEDICAL_EQUIP' and ENABLED_FLAG = 'Y' and nvl(trunc(START_DATE_ACTIVE),trunc(sysdate)) <= trunc(sysdate) and nvl(TRUNC(END_DATE_ACTIVE),trunc(sysdate)) >= trunc(sysdate)) ";
            }
                
            
            rs = st.executeQuery(query);
            out.print(RSConvertor.convertToJSONArray(rs));
        } catch (Exception e) {
            e.printStackTrace(out);
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
            if (dbConn != null) {
                try {
                    dbConn.close();
                    dbConn = null;
                } catch (SQLException e) {}
            }
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
