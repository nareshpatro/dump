/* --------------------------------------
    File Name: CommonUtilDao.java
    Author: Fanny Hung (PCCW)
    Date: 9-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - EBS initialized connection

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170909	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.eam.dao;

public interface CommonUtilDao {

	public void ebsAppInit(String userID, String respID, String appID) throws Exception;
    	
}
