/* --------------------------------------
    File Name: LovSearchController.java
    Author: Fanny Hung (PCCW)
    Date: 05-Aug-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Lov Search

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.2>		20171214	Carmen Ng	Added ContactMethod
	<1.1>		20171117	Carmen Ng	Added LovValidity
	<1.0>		20170805	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.eam.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hk.org.ha.eam.dao.SearchLovDao;
import hk.org.ha.eam.model.ContactMethodResult;
import hk.org.ha.eam.model.SearchLov;
import hk.org.ha.eam.model.SearchLovResult;

@Controller
@RequestMapping("/")
public class LovSearchController {
	
	private static final Logger logger = Logger.getLogger(LovSearchController.class);

	@Autowired
	private SearchLovDao searchLovDao;
	
	 @RequestMapping(value="/LovSearch", method = RequestMethod.POST)
	 @ResponseBody
	 public List<SearchLovResult> processLovSearch( @RequestBody SearchLov searchCriteria, HttpServletRequest req, HttpServletResponse resp) throws IOException, Exception{
		logger.debug("Process LovSearchController!!!");	
		logger.debug("getQuery from OBJECT!! " + searchCriteria.getQuery());
		logger.debug("getParameters from OBJECT!! " + searchCriteria.getParameters());
		logger.debug("getType from OBJECT!! " + searchCriteria.getType());
		logger.debug("getValue from OBJECT!! " + searchCriteria.getValue());
		
		String userID = (String)req.getSession().getAttribute("ebsUserId");
		String respID = (String)req.getSession().getAttribute("ebsRespId");
		String appID = (String)req.getSession().getAttribute("ebsRespAppId");

		List<SearchLovResult> listSearchLov = searchLovDao.searchLov(searchCriteria,userID,respID,appID);
		return listSearchLov;
	 }
	
	 @RequestMapping(value="/LovLookup", method = RequestMethod.POST)
	 @ResponseBody
	 public String lovLookup( @RequestParam String value, int query) throws IOException{
		String key= searchLovDao.searchKey(value, query);
		return key;
	 }
	 
	 @RequestMapping(value="/LovValidity", method = RequestMethod.POST)
	 @ResponseBody
	 public String lovValidity( @RequestParam String value, int query) throws IOException{
		String count = searchLovDao.checkValidity(value, query);
		return count;
	 }																
	 @RequestMapping(value="/ContactMethod", method = RequestMethod.POST)
	 @ResponseBody
	 public List<ContactMethodResult> contactMethod( @RequestParam String maintenanceNumber, HttpServletRequest req, HttpServletResponse resp) throws IOException{		
		List<ContactMethodResult> contactMethodList = searchLovDao.contactMethod(maintenanceNumber);
		return contactMethodList;
	 }
}