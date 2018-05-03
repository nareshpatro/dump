/* --------------------------------------
    File Name: DashBoardService.java
    Author: Kin Shum (PCCW)
    Date: 9-Oct-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - DashBoardService interface

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20171009	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.service;

import java.util.List;

import hk.org.ha.eam.model.DashBoard;

public interface DashBoardService {
    public List<DashBoard> getDashBoardData(DashBoard searchCriteria, String userID, String respID, String appID, String chart) throws Exception ;
    
}
