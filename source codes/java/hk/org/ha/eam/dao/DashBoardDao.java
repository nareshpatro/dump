/* --------------------------------------
    File Name: DashBoardDao.java
    Author: Fanny Hung (PCCW)
    Date: 9-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - DashBoard Summary

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171009	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.eam.dao;

import java.util.List;

import hk.org.ha.eam.model.DashBoard;
import hk.org.ha.eam.model.Dropdown;

public interface DashBoardDao {
	
    public List<DashBoard> getWrSummaryStat(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception ;
	
    public List<DashBoard> getWoSummaryStat(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception ;
    
	public List<DashBoard> getOutstandingWo(DashBoard searchCriteria, String userID, String respID, String appID, String woType) throws Exception ;
    
	public List<DashBoard> getPmSummary(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception ;
	
    public List<DashBoard> getPmStatusSummaryStat(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception ;
    
    public List<DashBoard> getPmUpcomingStat(DashBoard searchCriteria, String userID, String respID, String appID, String chart) throws Exception ;
    
	public List<DashBoard> getTop10MaintBody(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception ;
	
	public List<DashBoard> getWoTrendCM(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception ;
	
	public List<DashBoard> getWoTrendPM(DashBoard searchCriteria, String userID, String respID, String appID) throws Exception ;

    public List<Dropdown> getRiskLevelList() throws Exception ;
}
