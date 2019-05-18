package com.mgtv.autoplug.jiratool.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mgtv.autoplug.jiratool.message.JiraOpopEntity;
import com.mgtv.autoplug.jiratool.service.EmailSender;
import com.mgtv.autoplug.jiratool.service.JiraAuthService;
import com.mgtv.autoplug.jiratool.service.JiraIssueService;
import com.mgtv.autoplug.jiratool.util.EmailUtil;
import com.mgtv.autoplug.jiratool.util.TimeToolUtil;


@Service
public class JiraOpopIssueManagerImpl implements JiraOpopIssueManager{
	
	@Value("${jira.opop.project.key}")
	private String projectKey;
	
	@Value("${jira.default.reporter}")
	private String reporter;
	
	@Value("${jira.opop.project.issue.type}")
	private String issueType;
	
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
	private EmailSender emailSender;
	
	public final Logger logger = LoggerFactory.getLogger(JiraOpopIssueManagerImpl.class);

	@Override
	public void emailNofity(JiraOpopEntity jiraEntity) {
		//init params
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reporter", jiraEntity.getReporter());
		params.put("key", jiraEntity.getKey());
		params.put("title", jiraEntity.getSummary());
		params.put("opTarget", jiraEntity.getOpTarget());
		//params.put("opSchema", jiraEntity.getOpSchema().substring(0, jiraEntity.getOpSchema().length()-4).substring(3));
		params.put("opSchema", jiraEntity.getOpSchema());
		params.put("opTime", TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, jiraEntity.getOpTime()));
		params.put("reviewer", jiraEntity.getReviewer().getDisplayName());
		params.put("tester", jiraEntity.getTester().getDisplayName());
		params.put("developer", jiraEntity.getDeveloper().getDisplayName());
		params.put("operator", jiraEntity.getOperator().getDisplayName());
		params.put("opRisk", jiraEntity.getOpRisk());
		params.put("implicated", jiraEntity.getImplicated());
		params.put("planb", jiraEntity.getPlanb());
		
		Set<String> originalEmail = new HashSet<String>();
		if(jiraEntity.getReporter()==null) {
			logger.error("No sender email address");
			return;
		}else if(jiraEntity.getReviewer()==null && jiraEntity.getTester()==null && jiraEntity.getDeveloper()==null) {
			logger.error("No receiver email address");
			return;
		}else {
			originalEmail.add(jiraEntity.getTester().getEmailAddress());
			originalEmail.add(jiraEntity.getOperator().getEmailAddress());
			originalEmail.add(jiraEntity.getReviewer().getEmailAddress());
			originalEmail.add(jiraEntity.getDeveloper().getEmailAddress());
			if(jiraEntity.getNotifyEmailPerson()!=null && jiraEntity.getNotifyEmailPerson().length>0) {				
				for(String email : jiraEntity.getNotifyEmailPerson()) {
					if(email!=null && email.trim().length()>0) {
						originalEmail.add(email);
					}					
				}
			}
		}
		
		//定制化开发，管理上要求，没办法，破坏代码的设计
		if(jiraEntity.getNotifyEmail()==null || jiraEntity.getNotifyEmail().length==0) {
			String[] notifyEmails = new String[] {"ops@mgtv.com"};
			jiraEntity.setNotifyEmail(notifyEmails);
		}else {
			List<String> newEmails = new ArrayList<String>();
			int sum = jiraEntity.getNotifyEmail().length;
			for(String oldEmail: jiraEntity.getNotifyEmail()) {
				newEmails.add(oldEmail);			
			}
			if(!newEmails.contains("ops@mgtv.com")) {
				newEmails.add("ops@mgtv.com");
				sum++;
				String[] array =new String[sum];
				newEmails.toArray(array);
				jiraEntity.setNotifyEmail(array);
			}
						
		}
		
		
		if(originalEmail.size()>0) {
			emailSender.sendHtmlEmail(jiraEntity.getReporter(), EmailUtil.cleanout(originalEmail), jiraEntity.getNotifyEmail(), "[运维变更操作通告]-"+jiraEntity.getSummary(), "opop_html.vm", params);
			logger.error("Send email successfully : " + jiraEntity.getKey());
		}else {		
			logger.error("No email to address for issue: " + jiraEntity.getKey());
		}
	}
	
	@Override
	public JiraOpopEntity findIssueById(String issueId) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		return jiraIssueService.findOpopIssueById(baseUrl+"/rest/api/2/search", projectKey, issueId, authInfo);
	}
		

}
