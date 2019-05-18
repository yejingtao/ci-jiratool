package com.mgtv.autoplug.jiratool.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mgtv.autoplug.jiratool.manager.JiraIssueManager;
import com.mgtv.autoplug.jiratool.message.JiraReleaseEntity;

@RestController
public class JiraIssueController {
	
	public final Logger logger = LoggerFactory.getLogger(JiraIssueController.class);
	
	@Value("${jira_auth_username}")
	private String userName;
	
	@Value("${jira_auth_password}")
	private String passWord;
	
	@Value("${jira.project.key}")
	private String projectKey;
	
	@Value("${jira.baseUrl}")
	private String baseUrl;
	
	@Value("${jira.default.reporter}")
	private String reporter;
	
	@Value("${jira.project.issue.type}")
	private String issueType;
	
	@Value("${jira.project.issue.sub.type}")
	private String issueSubType;
	
	@Autowired
	private JiraIssueManager jiraIssueManager;
	
	/**
	//http://127.0.0.1:8096/createIssue?gitProject=loki&gitTag=333&summary=123
	@RequestMapping(value="/createIssue", method=RequestMethod.GET)
	public String createIssue(@RequestParam(required=true) String gitProject,
			@RequestParam(required=true) String gitTag,
			@RequestParam(required=true) String summary) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("projectKey", projectKey);
		map.put("summary", summary);
		map.put("gitProject", gitProject);
		map.put("gitTag", gitTag);
		map.put("reporter", reporter);
		map.put("issueType", issueType);
		return jiraIssueManager.createIssue(userName, passWord, map, baseUrl+"/rest/api/2/issue");
	}
	
	//http://127.0.0.1:8096/findIssue?gitProject=loki&gitTag=333
	@RequestMapping(value="/findIssue", method=RequestMethod.GET)
	public String findIssue(@RequestParam(required=true) String gitProject,
			@RequestParam(required=true) String gitTag) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("projectKey", projectKey);
		map.put("issueType", issueType);
		map.put("gitProject", gitProject);
		map.put("gitTag", gitTag);
		List<JiraEntity> entityList = jiraIssueManager.findIssues(userName, passWord, map, baseUrl+"/rest/api/2/search");
		return entityList==null?"empty":entityList.toString();
	}
	*/
	
	@RequestMapping(value="/createReleaseSubIssue", method=RequestMethod.POST)
	public String createSubTaskByParent(@RequestParam(required=true) String parentIssueId) {
		logger.info(parentIssueId+" wanna create sub issue");
		return jiraIssueManager.createSubIssueByParentId(userName, passWord, projectKey, 
				parentIssueId, issueSubType, baseUrl+"/rest/api/2/search", baseUrl+"/rest/api/2/issue");
	}
	
	@RequestMapping(value="/findIssueById", method=RequestMethod.GET)
	public String findIssueById(@RequestParam(required=true) String issueId) {
		JiraReleaseEntity entity =  jiraIssueManager.findIssueById(userName, passWord, projectKey, issueId, baseUrl+"/rest/api/2/search");
		return entity==null?"empty":entity.toString();
	}
	
	@RequestMapping(value="/sendEmailById", method=RequestMethod.POST)
	public String sendEmailById(@RequestParam(required=true) String issueId) {
		logger.info(issueId+" wanna send email");
		JiraReleaseEntity jiraEntity =  jiraIssueManager.findIssueById(userName, passWord, projectKey, issueId, baseUrl+"/rest/api/2/search");
		if(jiraEntity!=null) {
			logger.info(jiraEntity.getKey()+" wanna send email");
			jiraIssueManager.emailNofity(jiraEntity);
			return "OK";
		}else {
			logger.error("邮件发送失败, issueId="+issueId);
			return "Error";
		}
		
	}
}
