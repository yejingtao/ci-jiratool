package com.mgtv.autoplug.jiratool.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mgtv.autoplug.jiratool.manager.JiraCommonIssueManager;
import com.mgtv.autoplug.jiratool.message.JiraReleaseEntity;

@RestController
@RequestMapping("/common")
public class JiraCommonController {
	
	public final Logger logger = LoggerFactory.getLogger(JiraCommonController.class);
	
	@Autowired
	private JiraCommonIssueManager jiraCommonIssueManager;
			
	@RequestMapping(value="/sendTextById", method=RequestMethod.POST)
	public String sendEmailById(@RequestParam(required=true) String issueId) {
		//http://jira.imgo.tv/rest/api/2/issue/CODEPLOY-12?fields=assignee,summary
		logger.info(issueId+" wanna send message by youdu");
		jiraCommonIssueManager.sendTextByIssueId(issueId);
		return "OK";		
	}

}
