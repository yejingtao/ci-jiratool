package com.mgtv.autoplug.jiratool.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mgtv.autoplug.jiratool.message.JiraTechqaEntity;
import com.mgtv.autoplug.jiratool.message.JiraTechqaSubEntity;
import com.mgtv.autoplug.jiratool.service.EmailSender;
import com.mgtv.autoplug.jiratool.service.JiraAuthService;
import com.mgtv.autoplug.jiratool.service.JiraIssueService;
import com.mgtv.autoplug.jiratool.util.EmailUtil;
import com.mgtv.autoplug.jiratool.util.TimeToolUtil;

@Service
public class JiraTechqaManager {
	
	public final Logger logger = LoggerFactory.getLogger(JiraTechqaManager.class);
	
	@Value("${spring.mail.username}")
    private String eFrom; //读取配置文件中的参数
	
	@Value("${jira.baseUrl}")
	private String baseUrl;
	
	@Value("${jira_auth_username}")
	private String userName;
	
	@Value("${jira_auth_password}")
	private String passWord;
	
	@Value("${jira.techqa.project.key}")
	private String projectKey;
	
	@Value("${jira.techqa.project.issue.type}")
	private String issueType;
	
	@Value("${jira.techqa.project.issue.sub.type}")
	private String issueSubType;
	
	@Autowired
	private JiraAuthService jiraAuthService;
	
	@Autowired
	private JiraIssueService jiraIssueService;
	
	@Autowired
	private EmailSender emailSender;
	
	
	//提测工单邮件
	public void subCreateNotify(String issueId) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		JiraTechqaSubEntity jiraEntity = jiraIssueService.findTechqaSubById(baseUrl+"/rest/api/2/search", projectKey, issueSubType, issueId, authInfo);		
		if(jiraEntity!=null) {
			jiraEntity.setAssigner(jiraIssueService.findIssueById(baseUrl+"/rest/api/2/issue/", issueId, authInfo).getAssigner());
			//发送邮件
			//init params
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reporter", jiraEntity.getReporter().getDisplayName());
			params.put("key", jiraEntity.getKey());
			params.put("title", jiraEntity.getSummary());
			params.put("testProject", jiraEntity.getTestProject()==null?"":jiraEntity.getTestProject());
			params.put("testRound", jiraEntity.getTestRound());
			params.put("tester", jiraEntity.getAssigner().getDisplayName());
			params.put("testDescribe", jiraEntity.getTestDescribe()==null?null:jiraEntity.getTestDescribe());
			params.put("onlineTime", jiraEntity.getOnlineTime()==null?"":TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, jiraEntity.getOnlineTime()));
			
			Set<String> originalEmail = new HashSet<String>();
			originalEmail.add(jiraEntity.getAssigner().getEmailAddress());
			
			if(originalEmail.size()>0) {
				emailSender.sendHtmlEmail(jiraEntity.getReporter(), EmailUtil.cleanout(originalEmail), new String[] {"tech-qa@mgtv.com"}, jiraEntity.getSummary(), "test_create_sub.vm", params);
				//emailSender.sendHtmlEmail(jiraEntity.getReporter(), EmailUtil.cleanout(originalEmail), new String[] {"jingtao@mgtv.com"}, jiraEntity.getSummary(), "test_create_sub.vm", params);
			}else {
				logger.error("No email to address for issue: " + jiraEntity.getKey());
			}
			
		}
		
	}
	
	//用例评审邮件
	public void caseReviewNotify(String issueId) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		JiraTechqaEntity jiraEntity = jiraIssueService.findTechqaById(baseUrl+"/rest/api/2/search", projectKey, issueType, issueId, authInfo);
		if(jiraEntity!=null) {
			jiraEntity.setAssigner(jiraIssueService.findIssueById(baseUrl+"/rest/api/2/issue/", issueId, authInfo).getAssigner());
			//发送邮件
			//init params
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reporter", jiraEntity.getReporter().getDisplayName());
			params.put("key", jiraEntity.getKey());
			if(jiraEntity.getSummary().startsWith("测试任务")) {
				params.put("title",jiraEntity.getSummary().replaceFirst("测试任务", "用例评审"));
			}else {
				params.put("title", "用例评审-"+jiraEntity.getSummary());
			}
			
			params.put("testProject", jiraEntity.getTestProject()==null?"":jiraEntity.getTestProject());
			params.put("tester", jiraEntity.getAssigner().getDisplayName());
			params.put("caseDetail", jiraEntity.getCaseDetail()==null?"":jiraEntity.getCaseDetail());

			Set<String> originalEmail = new HashSet<String>();
			originalEmail.add(jiraEntity.getAssigner().getEmailAddress());
			if(jiraEntity.getNotifyEmailPerson()!=null && jiraEntity.getNotifyEmailPerson().length>0) {				
				for(String email : jiraEntity.getNotifyEmailPerson()) {
					if(email!=null && email.trim().length()>0) {
						originalEmail.add(email);
					}					
				}
			}
			
			if(originalEmail.size()>0) {
				emailSender.sendHtmlEmail(jiraEntity.getReporter(), EmailUtil.cleanout(originalEmail), jiraEntity.getNotifyEmail(), jiraEntity.getSummary(), "test_review.vm", params);
			}else {
				logger.error("No email to address for issue: " + jiraEntity.getKey());
			}
			
		}
	}
	
	//测试报告邮件
	public void caseReportNofity(String issueId) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		JiraTechqaSubEntity jiraEntity = jiraIssueService.findTechqaSubById(baseUrl+"/rest/api/2/search", projectKey, issueSubType, issueId, authInfo);
		if(jiraEntity!=null) {
			jiraEntity.setAssigner(jiraIssueService.findIssueById(baseUrl+"/rest/api/2/issue/", issueId, authInfo).getAssigner());
			//发送邮件
			//init params
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reporter", jiraEntity.getReporter().getDisplayName());
			params.put("key", jiraEntity.getKey());
			if(jiraEntity.getSummary().startsWith("提测工单")) {
				params.put("title",jiraEntity.getSummary().replaceFirst("提测工单", "测试报告"));
			}else {
				params.put("title", "测试报告-"+jiraEntity.getSummary());
			}
			params.put("testProject", jiraEntity.getTestProject());
			params.put("testRound", jiraEntity.getTestRound());
			params.put("tester", jiraEntity.getAssigner().getDisplayName());
			params.put("testTitle", jiraEntity.getSummary());
			params.put("testResult", jiraEntity.getTestResult());			
			params.put("reportComment", jiraEntity.getReportComment());			
			params.put("reportDetail", jiraEntity.getReportDetail());
			params.put("startTime", jiraEntity.getCreateTime()==null?"":TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, jiraEntity.getCreateTime()));
			params.put("endTime", jiraEntity.getTestEndTime()==null?"":TimeToolUtil.formatDate(TimeToolUtil.DATE_FORMAT_1, jiraEntity.getTestEndTime()));

			Set<String> originalEmail = new HashSet<String>();
			originalEmail.add(jiraEntity.getAssigner().getEmailAddress());
			if(jiraEntity.getNotifyEmailPerson()!=null && jiraEntity.getNotifyEmailPerson().length>0) {				
				for(String email : jiraEntity.getNotifyEmailPerson()) {
					if(email!=null && email.trim().length()>0) {
						originalEmail.add(email);
					}					
				}
			}
			
			if(originalEmail.size()>0) {
				emailSender.sendHtmlEmail(jiraEntity.getReporter(), EmailUtil.cleanout(originalEmail), jiraEntity.getNotifyEmail(), jiraEntity.getSummary(), "test_report.vm", params);
			}else {
				logger.error("No email to address for issue: " + jiraEntity.getKey());
			}
			
		}
	}
}
