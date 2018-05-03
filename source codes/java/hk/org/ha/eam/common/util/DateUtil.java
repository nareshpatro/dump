/* --------------------------------------
    File Name: DateUtil.java
    Author: Fanny Hung (PCCW)
    Date: 01-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Date Conversion

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170901	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.eam.common.util;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.util.Date;

@Service("dateUtil")
public class DateUtil {

	private static final Logger logger = Logger.getLogger(DateUtil.class);
	
	/**
	 * Parse String to Date
	 * 
	 * If input dateStr or datePattern is null or empty, return null.
	 * 
	 * @param Date Pattern
	 * @param Date String
	 * @return
	 * @throws java.text.ParseException 
	 */
	public Date parseStrToDate(String dateStr, String datePattern) {
		
		Date date = null;
		
		if(null != dateStr && !"".equals(dateStr.trim())
				&& null != datePattern && !"".equals(datePattern.trim())){
			
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			
			try {
				sdf.setLenient(false);//Not parsing lenient
				date = sdf.parse(dateStr);
				
			} catch (java.text.ParseException e) {
				logger.info("dateStr : " + dateStr + ", datePattern : " + datePattern);
				logger.info("Input date string failed to be parseable...");
			}
		}
		
		return date;
		
	}
	
	/**
	 * Format date to String.
	 * 
	 * If inputDate is null || datePattern is null or empty, return "".
	 * 
	 * @param dateFormat
	 * @param inputDate
	 * @return
	 */
	public String formatDateToStr(Date inputDate, String datePattern) {
		
		String dateStr = "";
		
		if(null != inputDate 
				&& null != datePattern && !"".equals(datePattern.trim())){
			
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			dateStr = sdf.format(inputDate);
			
		}
		
		return dateStr;
	}
	
}
