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
import com.mgtv.autoplug.jiratool.message.JiraReleaseEntity;
import com.mgtv.autoplug.jiratool.message.JiraReleaseSubEntity;
import com.mgtv.autoplug.jiratool.message.JiraUser;
import com.mgtv.autoplug.jiratool.service.EmailSender;
import com.mgtv.autoplug.jiratool.service.JenkinsService;
import com.mgtv.autoplug.jiratool.service.JiraAuthService;
import com.mgtv.autoplug.jiratool.service.JiraIssueService;
import com.mgtv.autoplug.jiratool.util.EmailUtil;

@Service
public class JiraReleaseIssueManagerImpl implements JiraReleaseIssueManager{
	
	@Value("${spring.mail.username}")
    private String eFrom; //读取配置文件中的参数
	
	@Value("${jira.baseUrl}")
	private String baseUrl;
	
	@Value("${jira.release.superman}")
	private String superman;
	
	@Value("${jira.release.project.issue.transition}")
	private String transitionId;
	
	@Value("${jira.release.project.issue.sub.transition}")
	private String subTransitionId;

	@Value("${jira.default.cc}")
	private String autoCc;//自动抄送的邮件
	
	@Value("${package.address.haoming}")
	private String packageHaoMing;
	
	@Value("${package.address.other}")
	private String packageOther;
	
	@Value("${package_auth_username_other}")
	private String packageUserNameOther;
	
	@Value("${package_auth_password_other}")
	private String packagePassWordOther;
	
	@Value("${package_auth_username_haoming}")
	private String packageUserNameHaoming;
	
	@Value("${package_auth_password_haoming}")
	private String packagePassWordHaoming;
	
	
	public final Logger logger = LoggerFactory.getLogger(JiraReleaseIssueManagerImpl.class);
	
	@Autowired
	private JiraAuthService jiraAuthService;
	
	@Autowired
	private JiraIssueService jiraIssueService;
	
	@Autowired
	private JenkinsService jenkinsService;
	
	@Autowired
	private EmailSender emailSender;
	
	public static final String COMMENT_ONLINE_TAG = " 上线版本号重复，keys: ";
	
	public static final String COMMENT_ROLLBACK_TAG = " 回退版本号重复，keys: ";
	
	public static final String COMMENT_JENKINS_COMMIT = "Jenkins自动提交";
	
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
			map.put("reporter", parentIssue.getReporter().getJiraName());
			map.put("issuetype", subIssueType);
			String postBody = jiraIssueService.constructReleaseSubPostBody(map);
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
		params.put("reporter", jiraEntity.getReporter().getDisplayName());
		params.put("git", jiraEntity.getGitProject());
		params.put("onlineVersion", jiraEntity.getOnlineGitTag());
		params.put("rollbackVersion", jiraEntity.getRollbackGitTag());
		params.put("detail", jiraEntity.getDetail());
		params.put("key", jiraEntity.getKey());
		params.put("title", jiraEntity.getSummary());
		params.put("approver", jiraEntity.getApprover().getDisplayName());
		params.put("operator", jiraEntity.getOperator().getDisplayName());
		params.put("tester", jiraEntity.getTester().getDisplayName());
		//emailSender.sendSimpleEmail(eFrom, jiraEntity.getNotifyEmail(), null, jiraEntity.getSummary(), "release.vm", params);
		Set<String> originalEmail = new HashSet<String>();
		if(jiraEntity.getReporter()==null) {
			logger.error("No sender email address");
			return;
		}else if(jiraEntity.getApprover()==null && jiraEntity.getOperator()==null && jiraEntity.getTester()==null) {
			logger.error("No receiver email address");
			return;
		}else {
			originalEmail.add(jiraEntity.getTester().getEmailAddress());
			originalEmail.add(jiraEntity.getOperator().getEmailAddress());
			originalEmail.add(jiraEntity.getApprover().getEmailAddress());
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
			String[] notifyEmails = new String[] {"shangxian@mgtv.com"};
			jiraEntity.setNotifyEmail(notifyEmails);
		}else {
			List<String> newEmails = new ArrayList<String>();
			int sum = jiraEntity.getNotifyEmail().length;
			for(String oldEmail: jiraEntity.getNotifyEmail()) {
				newEmails.add(oldEmail);			
			}
			if(!newEmails.contains("shangxian@mgtv.com")) {
				newEmails.add("shangxian@mgtv.com");
				sum++;
				String[] array =new String[sum];
				newEmails.toArray(array);
				jiraEntity.setNotifyEmail(array);
			}
								
		}
		
		if(originalEmail.size()>0) {
			emailSender.sendHtmlEmail(jiraEntity.getReporter(), EmailUtil.cleanout(originalEmail), jiraEntity.getNotifyEmail(), "[上线通报]"+jiraEntity.getSummary(), "release_html_v2.vm", params);
		}else {
			logger.error("No email to address for issue: " + jiraEntity.getKey());
		}
		
	}

	@Override
	public void preGitValidation(String userName, String passWord, String projectKey, String issueType, String issueId,
			String searchUrl) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		//获取到本次提交的Issue
		JiraReleaseEntity thisEntity = jiraIssueService.findReleaseIssueById(searchUrl, projectKey, issueId, authInfo);
		StringBuffer hasOldIssueKeys = new StringBuffer();
		StringBuffer hasNewIssueKeys = new StringBuffer();
		//根据gitProject和gitTag去查询Issue
		if(thisEntity!=null) {
			//先判断回退版本是否已存在
			String[] oldTagIssueKeys = jiraIssueService.findReleaseIssueByGit(searchUrl, projectKey, issueType, 
					"&cf[10208]~'"+ thisEntity.getGitProject()+"'", 
					"&cf[10223]~"+thisEntity.getRollbackGitTag(), authInfo);
			if(oldTagIssueKeys!=null && oldTagIssueKeys.length>0) {
				for(String issueKeys:oldTagIssueKeys) {
					//排除自己，一般也不可能是自己，因为有状态的判断
					if(!issueKeys.equals(thisEntity.getKey())) {
						hasOldIssueKeys.append(issueKeys).append(",");
					}
				}
			}
			
			//再判断新版本是否已存在
			String[] newTagIssueKeys = jiraIssueService.findReleaseIssueByGit(searchUrl, projectKey, issueType, 
					"&cf[10208]~'"+ thisEntity.getGitProject()+"'", 
					"&cf[10151]~"+thisEntity.getOnlineGitTag(), authInfo);
			if(newTagIssueKeys!=null && newTagIssueKeys.length>0) {
				for(String issueKeys:newTagIssueKeys) {
					//排除自己，一般也不可能是自己，因为有状态的判断
					if(!issueKeys.equals(thisEntity.getKey())) {
						hasNewIssueKeys.append(issueKeys).append(",");
					}
				}
			}
			
			//开始拼接报错信息
			StringBuffer commentInfo = new StringBuffer();
			if(hasNewIssueKeys.length()>0) {
				commentInfo.append(COMMENT_ONLINE_TAG);
				commentInfo.append(hasNewIssueKeys.substring(0, hasNewIssueKeys.length()-1));
			}
			if(hasOldIssueKeys.length()>0) {
				commentInfo.append(COMMENT_ROLLBACK_TAG);
				commentInfo.append(hasOldIssueKeys.substring(0, hasOldIssueKeys.length()-1));
			}
						
			if(commentInfo.length()>0) {
				//发起通知，提交jira
				String title = "Jira任务"+ thisEntity.getKey() +"需求校验不通过";
				sendGitEmail(thisEntity,commentInfo.toString(), title,authInfo);
			}else {
				//自动提交环节
				String restUrl = baseUrl+"/rest/api/2/issue/"+thisEntity.getKey() + "/transitions?expand=transitions.fields";
				jiraIssueService.transIssue(restUrl,transitionId, authInfo);
			}
			
		}		
	}
	
	private void sendGitEmail(JiraReleaseEntity thisEntity, String commentInfo, String title, String authInfo) {
		//添加备注
		String restUrl = baseUrl+"/rest/api/2/issue/"+thisEntity.getKey();
		jiraIssueService.addCommon(restUrl,commentInfo,authInfo);
		
		
		//增加通知机制
		JiraUser autoEmailFrom = new JiraUser();
		autoEmailFrom.setEmailAddress(eFrom);	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("git", thisEntity.getGitProject());
		params.put("newVersion", thisEntity.getOnlineGitTag());
		params.put("backVersion", thisEntity.getRollbackGitTag());
		params.put("key", thisEntity.getKey());
		//params.put("title", "需求上线转交运维审核");
		params.put("user", thisEntity.getOperator().getDisplayName());
		//params.put("errorInfo", commentInfo);
		//String title = "Jira任务"+ thisEntity.getKey() +"进入审核阶段";				
		String[] to = new String[]{thisEntity.getReporter().getEmailAddress()};
		String[] cc = new String[]{thisEntity.getOperator().getEmailAddress()};
		emailSender.sendHtmlEmail(autoEmailFrom, to, cc, title, "unique_html.vm", params);
	}

	@Override
	public void jenkinsAutoTrans(String userName, String passWord, JiraReleaseSubEntity subIssue) {
		boolean jenkinsIsOk = false;
		
		if("haoming@imgo.tv".equals(subIssue.getOperator().getJiraName())) {
			String gitProject = subIssue.getGitProject().substring(subIssue.getGitProject().lastIndexOf("/")+1);			
			if(gitProject.contains(".")) {
				gitProject = gitProject.substring(0,gitProject.lastIndexOf("."));
			}
			String packageAuth = jiraAuthService.getBasicAuth(packageUserNameHaoming, packagePassWordHaoming);
			jenkinsIsOk = jenkinsService.chechPackage(packageHaoMing, gitProject, subIssue.getOnlineGitTag(), ".zip",packageAuth);
		}else {
			String gitProject = subIssue.getGitProject().substring(subIssue.getGitProject().lastIndexOf(":")+1);			
			if(gitProject.contains(".")) {
				gitProject = gitProject.substring(0,gitProject.lastIndexOf("."));
			}
			String packageAuth = jiraAuthService.getBasicAuth(packageUserNameOther, packagePassWordOther);
			jenkinsIsOk = jenkinsService.chechPackage(packageOther, gitProject, subIssue.getOnlineGitTag(), ".zip",packageAuth);
		}
		if(jenkinsIsOk) {
			//自动提交Jira
			String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
			//自动提交环节
			String restUrl = baseUrl+"/rest/api/2/issue/"+subIssue.getKey() + "/transitions?expand=transitions.fields";
			jiraIssueService.transIssue(restUrl, subTransitionId, authInfo);
		}
				
	}

	@Override
	public JiraReleaseSubEntity findSubIssueById(String userName, String passWord, String projectKey, String issueId,
			String searchUrl) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		return jiraIssueService.findReleaseSubIssueById(searchUrl, projectKey, issueId, authInfo);
	}

	@Override
	public void transIssueByGit(String userName, String passWord, String projectKey, String issueType,
			String gitAddress,String gitTag,String searchUrl) {
		String authInfo = jiraAuthService.getBasicAuth(userName, passWord);
		String[] issueKeys = jiraIssueService.findReleaseIssueByGit(searchUrl, projectKey, issueType, 
				"&cf[10208]~'"+ gitAddress+"'", 
				"&cf[10151]~"+ gitTag, authInfo);
		//根据gitProject和gitTag去查询Issue
		if(issueKeys!=null) {
			for(String key : issueKeys) {
				String restUrl = baseUrl+"/rest/api/2/issue/"+key + "/transitions?expand=transitions.fields";
				jiraIssueService.transIssue(restUrl,subTransitionId, authInfo);
			}
		}		
	}


}
