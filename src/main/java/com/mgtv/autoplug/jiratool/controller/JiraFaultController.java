package com.mgtv.autoplug.jiratool.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mgtv.autoplug.jiratool.manager.JiraFaultIssueManager;
import com.mgtv.autoplug.jiratool.message.JiraFaultEntity;
import com.mgtv.autoplug.jiratool.message.JiraOpopEntity;

@RestController
@RequestMapping("/fault")
public class JiraFaultController {
	
	public final Logger logger = LoggerFactory.getLogger(JiraFaultController.class);
	
	@Autowired
	private JiraFaultIssueManager jiraFaultIssueManager;
	
	@Value("${jira.fault.import.file}")
	private String file;
	
	@RequestMapping(value="/sendEmailById", method=RequestMethod.POST)
	public String sendEmailById(@RequestParam(required=true) String issueId) {
		logger.info(issueId+" wanna send email to name");
		JiraFaultEntity jiraEntity =  jiraFaultIssueManager.findIssueById(issueId);
		if(jiraEntity!=null) {
			logger.info(jiraEntity.getKey()+" wanna send email to person");
			jiraFaultIssueManager.emailNofity(jiraEntity,(short)1);
			return "OK";
		}else {
			logger.error("邮件发送失败, issueId="+issueId);
			return "Error";
		}
	}
	
	@RequestMapping(value="/previewEmailById", method=RequestMethod.POST)
	public String previewEmailById(@RequestParam(required=true) String issueId) {
		logger.info(issueId+" wanna send email to name");
		JiraFaultEntity jiraEntity =  jiraFaultIssueManager.findIssueById(issueId);
		if(jiraEntity!=null) {
			logger.info(jiraEntity.getKey()+" wanna send email to group");
			jiraFaultIssueManager.emailNofity(jiraEntity,(short)0);
			return "OK";
		}else {
			logger.error("邮件发送失败, issueId="+issueId);
			return "Error";
		}
	}
	
	
	/**
	 * 工具类功能，存在安全隐患，不要上生产
	@RequestMapping(value="/importExcel", method=RequestMethod.GET)
	public String importExcel() {
		logger.info(" importExcel");
		jiraFaultIssueManager.importExcel(file);
		return "OK";	
	}
	*/

}
