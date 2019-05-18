package com.mgtv.autoplug.jiratool.manager;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mgtv.autoplug.jiratool.message.JiraReleaseEntity;
import com.mgtv.autoplug.jiratool.service.EmailSender;
import com.mgtv.autoplug.jiratool.service.JiraAuthService;
import com.mgtv.autoplug.jiratool.service.JiraIssueService;

@Service
public class JiraIssueManagerImpl implements JiraIssueManager{
	
	@Value("${spring.mail.username}")
    private String eFrom; //读取配置文件中的参数
	
	public final Logger logger = LoggerFactory.getLogger(JiraIssueManagerImpl.class);
	
	@Autowired
	private JiraAuthService jiraAuthService;
	
	@Autowired
	private JiraIssueService jiraIssueService;
	
	@Autowired
	private EmailSender emailSender;
	
	/**
	@Override
	public String createIssue(String userName, String passWord, Map<String, String> map, String restUrl) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		String postBody = jiraIssueService.createIssuePostBody(map);
		return jiraIssueService.createIssue(restUrl, postBody, authInfo);
		
	}

	@Override
	public List<JiraEntity> findIssues(String userName, String passWord, Map<String, String> map, String reqeustUrl) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		String postBody = jiraIssueService.findIssuePostBody(map);
		return jiraIssueService.findIssues(reqeustUrl, postBody, authInfo);
	}
	*/

	@Override
	public String createSubIssueByParentId(String userName, String passWord, String projectKey, String parentIssueId, String subIssueType,
			String searchUrl, String createUrl) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		JiraReleaseEntity parentIssue = jiraIssueService.findReleaseIssueById(searchUrl, projectKey, parentIssueId, authInfo);
		if(parentIssue==null) {
			logger.error("Cannot find issue by id "+parentIssueId);
			return null;
		}else {
			//map:projectKey 项目key，parentIssueKey 主任务issueKey，summary 子任务主题， gitTag，gitProject
			Map<String, String> map = new HashMap<String, String>();
			map.put("projectKey", projectKey);
			map.put("parentIssueKey", parentIssue.getKey());
			map.put("gitOnlineTag", parentIssue.getOnlineGitTag());
			map.put("gitRollbackTag", parentIssue.getRollbackGitTag());
			map.put("gitProject", parentIssue.getGitProject());
			map.put("summary", "[版本上线]["+parentIssue.getGitProject()+"]["+parentIssue.getOnlineGitTag()+"]");
			map.put("reporter", parentIssue.getReporter());
			map.put("issuetype", subIssueType);
			String postBody = jiraIssueService.constructFindIssuePostBody(map);
			logger.info("postBody: "+postBody);
			return jiraIssueService.createIssue(createUrl, postBody, authInfo);
		}
	}

	@Override
	public JiraReleaseEntity findIssueById(String userName, String passWord, String projectKey, String issueId,
			String searchUrl) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		return jiraIssueService.findReleaseIssueById(searchUrl, projectKey, issueId, authInfo);
	}

	@Override
	public void emailNofity(JiraReleaseEntity jiraEntity) {
		//init params
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reporter", jiraEntity.getReporter());
		params.put("git", jiraEntity.getGitProject());
		params.put("version", jiraEntity.getOnlineGitTag());
		params.put("detail", jiraEntity.getDetail());
		params.put("key", jiraEntity.getKey());
		params.put("title", jiraEntity.getSummary());
		//emailSender.sendSimpleEmail(eFrom, jiraEntity.getNotifyEmail(), null, jiraEntity.getSummary(), "release.vm", params);
		String[] toEmails = null;
		
		if(jiraEntity.getReporter()!=null && !jiraEntity.getReporter().equals(jiraEntity.getTester())) {
			toEmails = new String[3];
			toEmails[0] = jiraEntity.getReporter();
			toEmails[1] = jiraEntity.getTester();
			toEmails[2] = jiraEntity.getOperator();
		}else if(jiraEntity.getReporter()!=null && jiraEntity.getReporter().equals(jiraEntity.getTester())){
			toEmails = new String[2];
			toEmails[0] = jiraEntity.getReporter();
			toEmails[1] = jiraEntity.getOperator();
		}else {
			//理论上不会出现这种情况，因为上线执行人和测试负责人都是必选
			toEmails = new String[1];
			toEmails[0] = jiraEntity.getReporter();
		}
		
		emailSender.sendHtmlEmail(jiraEntity.getReporter(), toEmails, jiraEntity.getNotifyEmail(), "[上线通报]"+jiraEntity.getSummary(), "release_html.vm", params);
	}


}
