
package hk.org.ha.eam.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import hk.org.ha.eam.util.EBizUtil;

@Controller
@RequestMapping("/")
public class CommonHandleController {

	private static final Logger logger = Logger.getLogger(CommonHandleController.class);
	
	@RequestMapping(value="/redirect")
	public ModelAndView logout(ModelAndView model, HttpServletRequest req) throws IOException, Exception {
		logger.debug("Logging Out...");
		String agent = EBizUtil.getEBizInstance().getAppsServletAgent();
		model.setViewName("redirect");
	    return model;
	}
	
}