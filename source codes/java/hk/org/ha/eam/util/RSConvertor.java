package hk.org.ha.eam.util;

/* --------------------------------------
    File Name: RSConvertor.java
    Author: Jimmy Wong (PCCW)
    Date: 9-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Resultset JSON converter

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171009	Jimmy Wong	Initial version
   -------------------------------------- */

import org.json.JSONArray;  
import org.json.JSONObject;  
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class RSConvertor {
    /**
     * Convert a result set into a JSON Array
     * @param resultSet
     * @return a JSONArray
     * @throws Exception
     */
    public static JSONObject convertToJSON(ResultSet resultSet)
            throws Exception {
        JSONObject output = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int colCount = rsmd.getColumnCount();
        int rowCount = 0;
        while (resultSet.next()) {
            JSONObject obj = new JSONObject();
            
            for (int i = 0; i < colCount; i++) {
                obj.put(rsmd.getColumnLabel(i + 1)
                        .toLowerCase(), resultSet.getObject(i + 1));
            }
            jsonArray.put(obj);
            rowCount++;
        }
        output.put("total", rowCount);
        output.put("rows", jsonArray);
        return output;
    }
    
    public static JSONArray convertToJSONArray (ResultSet resultSet) throws Exception{
        JSONArray jsonArray = new JSONArray();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int colCount = rsmd.getColumnCount();
        while (resultSet.next()) {
            JSONObject obj = new JSONObject();
            for (int i = 0; i < colCount; i++) {
                obj.put(rsmd.getColumnLabel(i + 1)
                        .toLowerCase(), resultSet.getObject(i + 1));
            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }
    /**
     * Convert a result set into a XML List
     * @param resultSet
     * @return a XML String with list elements
     * @throws Exception if something happens
     */
    /*
    public static String convertToXML(ResultSet resultSet)
            throws Exception {
        StringBuffer xmlArray = new StringBuffer("<results>");
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int colCount = rsmd.getColumnCount();
        int rowCount = 0;
        while (resultSet.next()) {
            xmlArray.append("<result ");
            for (int i = 0; i < colCount; i++) {
                xmlArray.append(" " + resultSet.getMetaData().getColumnLabel(i + 1)
                .toLowerCase() + "='" + resultSet.getObject(i + 1) + "'"); }
            xmlArray.append(" />");
        }
        xmlArray.append("</results>");
        return xmlArray.toString();
    }*/
}