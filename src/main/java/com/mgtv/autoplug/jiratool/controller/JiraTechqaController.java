package com.mgtv.autoplug.jiratool.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mgtv.autoplug.jiratool.manager.JiraTechqaManager;

@RestController
@RequestMapping("/techqa")
public class JiraTechqaController {

	public final Logger logger = LoggerFactory.getLogger(JiraTechqaController.class);
	
	@Autowired
	private JiraTechqaManager jiraTechqaManager;
	
	
	@RequestMapping(value="/subCreateNotify", method=RequestMethod.POST)
	public String subCreateNotify(@RequestParam(required=true) String issueId) {
		logger.info(issueId+" wanna send email");
		jiraTechqaManager.subCreateNotify(issueId);
		return "ok";
	}
	
	@RequestMapping(value="/caseReviewNotify", method=RequestMethod.POST)
	public String caseReviewNotify(@RequestParam(required=true) String issueId) {
		logger.info(issueId+" wanna send email");
		jiraTechqaManager.caseReviewNotify(issueId);
		return "ok";
	}
	
	@RequestMapping(value="/caseReportNofity", method=RequestMethod.POST)
	public String caseReportNofity(@RequestParam(required=true) String issueId) {
		logger.info(issueId+" wanna send email");
		jiraTechqaManager.caseReportNofity(issueId);
		return "ok";
	}
}
