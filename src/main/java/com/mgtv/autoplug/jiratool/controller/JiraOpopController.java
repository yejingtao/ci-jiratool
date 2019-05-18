package com.mgtv.autoplug.jiratool.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mgtv.autoplug.jiratool.manager.JiraOpopIssueManager;
import com.mgtv.autoplug.jiratool.message.JiraOpopEntity;

@RestController
@RequestMapping("/opop")
public class JiraOpopController {
	
	public final Logger logger = LoggerFactory.getLogger(JiraOpopController.class);
	
	@Autowired
	private JiraOpopIssueManager jiraOpopIssueManager;
	
	@RequestMapping(value="/sendEmailById", method=RequestMethod.POST)
	public String sendEmailById(@RequestParam(required=true) String issueId) {
		logger.info(issueId+" wanna send email");
		JiraOpopEntity jiraEntity =  jiraOpopIssueManager.findIssueById(issueId);
		if(jiraEntity!=null) {
			logger.info(jiraEntity.getKey()+" wanna send email");
			jiraOpopIssueManager.emailNofity(jiraEntity);
			return "OK";
		}else {
			logger.error("邮件发送失败, issueId="+issueId);
			return "Error";
		}		
	}
}
