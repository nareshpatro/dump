/* --------------------------------------
    File Name: DashBoardController.java
    Author: Fanny Hung (PCCW)
    Date: 01-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Control DashBoard action

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170901	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.eam.controller;

import java.io.IOException;
import java.net.URL;
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
import org.springframework.web.servlet.ModelAndView;

import hk.org.ha.eam.dao.CommonUtilDao;
import hk.org.ha.eam.dao.DashBoardDao;
import hk.org.ha.eam.dao.WorkRequestDao;
import hk.org.ha.eam.model.DashBoard;
import hk.org.ha.eam.model.Dropdown;
import hk.org.ha.eam.model.SearchPeople;
import hk.org.ha.eam.model.People;
import hk.org.ha.eam.service.DashBoardService;

@Controller
@RequestMapping("/")
public class DashBoardController {
	private static final Logger logger = Logger.getLogger(WorkRequestController.class);

	@Autowired
	private DashBoardService dashBoardService;
	@Autowired
	private WorkRequestDao workRequestDao;
	@Autowired
	private DashBoardDao dashBoardDao;
	@Autowired
	private CommonUtilDao commonUtilDao;
	@RequestMapping(value="/dashBoard")
	public ModelAndView dashBoard(ModelAndView model, HttpServletRequest request, HttpServletResponse resp) throws IOException, Exception{
		logger.debug("Entering Dashboard");
		
		logger.debug("###JRE VERSION " + Runtime.class.getPackage().getImplementationVersion());
		ClassLoader classloader =
				   org.apache.poi.poifs.filesystem.POIFSFileSystem.class.getClassLoader();
				URL res = classloader.getResource(
				             "org/apache/poi/poifs/filesystem/POIFSFileSystem.class");
				String path = res.getPath();
				logger.debug("POI Core came from " + path);
				
				classloader = org.apache.poi.POIXMLDocument.class.getClassLoader();
				res = classloader.getResource("org/apache/poi/POIXMLDocument.class");
				path = res.getPath();
				logger.debug("POI OOXML came from " + path);
		
		String userID = (String)request.getSession().getAttribute("ebsUserId");
		String respID = (String)request.getSession().getAttribute("ebsRespId");
		String appID = (String)request.getSession().getAttribute("ebsRespAppId");

		DashBoard newDashBoard = new DashBoard();
		model.addObject("dashBoard", newDashBoard);
		
		People newPeople = new People();
		model.addObject("people", newPeople);
		commonUtilDao.ebsAppInit(userID, respID, appID);
		populateDefaultModel(model);
		model.setViewName("index");
		return model;
	}
	
	 @RequestMapping(value="/processDashBoard", method = RequestMethod.POST)
	 @ResponseBody
	 public List<DashBoard> processDashBoard(@RequestBody DashBoard searchCriteria, HttpServletRequest request, HttpServletResponse resp) throws IOException, Exception{
		logger.debug("Process DashBoard Controller!!!");
        String dashboardFor = searchCriteria.getDashboardFor();
        String eamOrg = searchCriteria.getEamOrg();
        String owningDept = searchCriteria.getOwningDept();
        String assetLocation = searchCriteria.getAssetLocation();
        String criticalMedEquip = searchCriteria.getCriticalMedEquip();
        String creationFrom = searchCriteria.getCreationFrom();
        String creationTo = searchCriteria.getCreationTo();
        String statusType = searchCriteria.getStatusType();
        String assetRisk = searchCriteria.getRiskLevel();
        String mBody = searchCriteria.getMBody();
        String scheduleFrom = searchCriteria.getScheduleFrom();
        String scheduleTo = searchCriteria.getScheduleTo();
        String module = request.getParameter("module");
        searchCriteria.setModule(module);
        searchCriteria.setMBody(searchCriteria.getMaintenanceVendor());
        
        String chart = request.getParameter("chart");
        
		logger.debug("EAM ORG from processDashBoard!! " + eamOrg );
		logger.debug("Module from processDashBoard!! " + module );
		logger.debug("dashboardFor from processDashBoard!! " + dashboardFor );
		
		List<DashBoard> listDashBoard = dashBoardService.getDashBoardData(searchCriteria,request.getSession().getAttribute("ebsUserId").toString(),request.getSession().getAttribute("ebsRespId").toString(),request.getSession().getAttribute("ebsRespAppId").toString(), chart);

		return listDashBoard;
	 }
	
	   private void populateDefaultModel(ModelAndView model) throws Exception {
		   
			List<Dropdown> eamOrgList = workRequestDao.getEamOrgList();
			model.addObject("eamOrgList", eamOrgList);
		
			List<Dropdown> riskLevelList = dashBoardDao.getRiskLevelList();
			model.addObject("riskLevelList", riskLevelList);
	
	   }
	   
}
