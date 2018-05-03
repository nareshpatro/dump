/* --------------------------------------
    File Name: LogoutController.java
    Author: Kin Shum (PCCW)
    Date: 20-Sep-2017
	Project: EAM0761-Usability enhancement for Work Request and Work Order (SR#2016-064)
    Description:
    - Logout

    ---------- Modification History ----------
	Version		Date		Author		Change
	-------		--------	-------		-------
	<1.0>		20170920	Kin Shum	Initial version
   -------------------------------------- */

package hk.org.ha.eam.controller;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import hk.org.ha.eam.util.EBizUtil;

@Controller
@RequestMapping("/")
public class LogoutController {

	private static final Logger logger = Logger.getLogger(LogoutController.class);
	
	@RequestMapping(value="/logout")
	public RedirectView logout() throws IOException{
		logger.debug("Logging Out...");
		RedirectView redirectView = new RedirectView();
		String url_prefix = EBizUtil.getEBizInstance().getAppsServletAgent().toString();
		//logger.debug("Logging Out...prefix:"+url_prefix);
	    redirectView.setUrl(url_prefix+"OALogout.jsp?menu=Y");
	    return redirectView;
	}
	
}