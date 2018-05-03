/* --------------------------------------
    File Name: WorkOrderDao.java
    Author: Fanny Hung (PCCW)
    Date: 10-Aug-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Lov Search Function

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.2>		20171214	Carmen Ng	Added contactMethod
	<1.1>		20171117	Carmen Ng	Added checkValidity
	<1.0>		20170815	Fanny Hung	Initial version
   -------------------------------------- */

package hk.org.ha.eam.dao;

import java.util.List;

import hk.org.ha.eam.model.ContactMethodResult;
import hk.org.ha.eam.model.SearchLov;
import hk.org.ha.eam.model.SearchLovResult;

public interface SearchLovDao {

    public List<SearchLovResult>  searchLov(SearchLov searchCriteria,String userID,String respID,String appID);
    public String  searchKey(String value, int query);
	public String  checkValidity(String value, int query);
	public List<ContactMethodResult> contactMethod(String maintenanceNumber);
}
