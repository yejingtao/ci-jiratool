package com.mgtv.autoplug.jiratool.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mgtv.autoplug.jiratool.message.JiraEntity;
import com.mgtv.autoplug.jiratool.service.JiraAuthService;
import com.mgtv.autoplug.jiratool.service.JiraIssueService;
import com.mgtv.autoplug.jiratool.service.YouduSendService;

@Service
public class JiraCommonIssueManagerImpl implements JiraCommonIssueManager{
	
	@Value("${jira.baseUrl}")
	private String baseUrl;
	
	@Value("${jira_auth_username}")
	private String userName;
	
	@Value("${jira_auth_password}")
	private String passWord;
	
	@Autowired
	private JiraAuthService jiraAuthService;
	
	@Autowired
	private JiraIssueService jiraIssueService;
	
	@Autowired
	private YouduSendService youduSendService;
	
	@Override
	public void sendTextByIssueId(String issueId) {
		// 准备鉴权信息
		String basicAuthHeader = jiraAuthService.getBasicAuth(userName, passWord);
		// 查询Jira任务
		JiraEntity jiraEntity = jiraIssueService.findIssueById(baseUrl+"/rest/api/2/issue/", issueId, basicAuthHeader);
		if(jiraEntity!=null && jiraEntity.getAssigner()!=null) {
			String user = jiraEntity.getAssigner().getJiraName();
			user = user.substring(0, user.lastIndexOf("@"));
			String message = "您有新的Jira任务已到\""+jiraEntity.getCurrentStage()+"\"环节， 主题:\"" +jiraEntity.getSummary()+"\", 链接地址:"+baseUrl+"/browse/"+jiraEntity.getKey();
			// 发送有度信息
			youduSendService.sendText(user, message);
		}		
	}

}
