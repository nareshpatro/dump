/* --------------------------------------
    File Name: DashBoardServiceImpl.java
    Author: Kin Shum (PCCW)
    Date: 9-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - DashBoard data retrive

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171009	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hk.org.ha.eam.dao.CommonUtilDao;
import hk.org.ha.eam.dao.DashBoardDao;
import hk.org.ha.eam.model.DashBoard;

@Service
public class DashBoardServiceImpl implements DashBoardService{
	private static final Logger logger = Logger.getLogger(DashBoardServiceImpl.class);
	
	@Autowired
	private DashBoardDao dashBoardDao;
	
	@Autowired
	private CommonUtilDao commonUtilDao;
	
	@Transactional(readOnly=true)
	public List<DashBoard> getDashBoardData(DashBoard searchCriteria, String userID, String respID, String appID, String chart) throws Exception {
		logger.debug("Entering getDashBoardData MODULE " + searchCriteria.getModule());
		List<DashBoard> listDashBoard = new ArrayList<DashBoard>();
		
		commonUtilDao.ebsAppInit(userID, respID, appID);
		
        if ("1".equals(searchCriteria.getModule())) {		
    		logger.debug("Entering WR Summary");
        	listDashBoard = dashBoardDao.getWrSummaryStat(searchCriteria, userID, respID, appID);   
        }
        else if ("2".equals(searchCriteria.getModule())) {
    		logger.debug("Entering WO Summary");
           	listDashBoard = dashBoardDao.getWoSummaryStat(searchCriteria, userID, respID, appID);   
        }
        else if ("3".equals(searchCriteria.getModule())) {
    		logger.debug("Entering Outstanding WO");
           	listDashBoard = dashBoardDao.getOutstandingWo(searchCriteria, userID, respID, appID, chart);   
        }
        else if ("4".equals(searchCriteria.getModule())) {
    		logger.debug("Entering PM Summary");
           	listDashBoard = dashBoardDao.getPmSummary(searchCriteria, userID, respID, appID);   
        }
        else if ("5".equals(searchCriteria.getModule())) {
    		logger.debug("Entering WO Status");
           	listDashBoard = dashBoardDao.getPmStatusSummaryStat(searchCriteria, userID, respID, appID);   
           	logger.debug("listDashBoard size: " + listDashBoard.size());
        }
        else if ("6".equals(searchCriteria.getModule())) {
    		logger.debug("Entering WO Upcoming");
           	listDashBoard = dashBoardDao.getPmUpcomingStat(searchCriteria, userID, respID, appID, chart);   
        }
        else if ("7".equals(searchCriteria.getModule())) {
        	logger.debug("Entering Maintenance Body");
           	listDashBoard = dashBoardDao.getTop10MaintBody(searchCriteria, userID, respID, appID); 
        }
        else if ("8".equals(searchCriteria.getModule())) {
        	logger.debug("Entering Work Order Trend CM");
           	listDashBoard = dashBoardDao.getWoTrendCM(searchCriteria, userID, respID, appID);
        }
        else if ("9".equals(searchCriteria.getModule())) {
        	logger.debug("Entering Work Order Trend PM");
           	listDashBoard = dashBoardDao.getWoTrendPM(searchCriteria, userID, respID, appID);
        }
		
		return listDashBoard;
    }
	
	 
}

