/* --------------------------------------
    File Name: NewHireController.java
    Author: Carlo (PCCW)
    Date: 31-Jul-2017
	Project: EAM0761-Usability enhancement for New Hire
    Description:
    - New Hire Function

    ---------- Modification History ----------
	Version		Date		Author		  Change
	-------		--------	-------		  -------
	<1.0>		20180424	Carlo (PCCW)  Initial version
   ------------------------------------------- */
package hk.org.ha.eam.controller;

import java.io.IOException;

import hk.org.ha.eam.model.NewHire;
import hk.org.ha.eam.model.WorkRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;  
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/")
public class NewHireController {
	private static final Logger logger = Logger.getLogger(NewHireController.class);
	
	@RequestMapping(value="/newHire", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView newHire(ModelAndView model, HttpServletRequest req) throws IOException, Exception {
		model.setViewName("newHire");
		return model;
	}
	
	@RequestMapping(value = "/saveAndContinue", method = RequestMethod.POST)
	@ResponseBody
    public NewHire submit(@RequestBody NewHire newHire, 
    		HttpServletRequest req, HttpServletResponse resp) {
		logger.info("New Hire Controller!!!");
//		NewHire nHire = new NewHire();
//		nHire.setEmpnum(newHire.getHkidName());
		String hkidName = newHire.getHkidName();
		newHire.setHkidName("New " + hkidName);
        return newHire;
        
        //Map<String, Object> 
    }
	
//	@RequestMapping(value="/saveAndContinue", method = {RequestMethod.POST, RequestMethod.GET})
//	public ModelAndView submit(ModelAndView model, HttpServletRequest req) throws IOException, Exception {
//		model.setViewName("newHire");
//		return model;
//	}
	
}
