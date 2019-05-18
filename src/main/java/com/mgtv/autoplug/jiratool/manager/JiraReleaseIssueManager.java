package com.mgtv.autoplug.jiratool.manager;

import com.mgtv.autoplug.jiratool.message.JiraReleaseEntity;
import com.mgtv.autoplug.jiratool.message.JiraReleaseSubEntity;

public interface JiraReleaseIssueManager {
	
	/**
	 * 
	 * @param userName
	 * @param passWord
	 * @param map
	 * @param reqeustUrl
	 * @return
	 */
	String createSubIssueByParentId(String userName, String passWord, String projectKey, String parentIssueId, String subIssueType, String searchUrl, String createUrl);
	
	/**
	 * 
	 * @param userName
	 * @param passWord
	 * @param map
	 * @param reqeustUrl
	 * @return
	 */
	JiraReleaseEntity findIssueById(String userName, String passWord, String projectKey, String issueId, String searchUrl);
	
	JiraReleaseSubEntity findSubIssueById(String userName, String passWord, String projectKey, String issueId, String searchUrl);
	
	void emailNofity(JiraReleaseEntity jiraEntity);
	
	void preGitValidation(String userName, String passWord, String projectKey, String issueType, String issueId, String searchUrl);
	
	void jenkinsAutoTrans(String userName, String passWord, JiraReleaseSubEntity subIssue);
	
	void transIssueByGit(String userName, String passWord, String projectKey, String issueType, String gitAddress,String gitTag,String searchUrl);
}
