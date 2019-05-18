package com.mgtv.autoplug.jiratool.manager;

import com.mgtv.autoplug.jiratool.message.JiraOpopEntity;

public interface JiraOpopIssueManager {
	
	void emailNofity(JiraOpopEntity jiraEntity);
	
	JiraOpopEntity findIssueById(String issueId);
}
