package com.mgtv.autoplug.jiratool.manager;

import com.mgtv.autoplug.jiratool.message.JiraReleaseEntity;

public interface JiraIssueManager {
	
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
	
	
	void emailNofity(JiraReleaseEntity jiraEntity);
	
}
