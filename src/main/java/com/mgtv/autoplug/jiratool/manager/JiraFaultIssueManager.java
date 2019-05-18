package com.mgtv.autoplug.jiratool.manager;

import com.mgtv.autoplug.jiratool.message.JiraFaultEntity;

public interface JiraFaultIssueManager {
	
	void importExcel(String file);
	
	String createIssue(JiraFaultEntity jiraFaultEntity);
	
	JiraFaultEntity getIssueByKey(String key);
	
	JiraFaultEntity findIssueById(String id);
	
	//type=0 预览 type=1 正式发送
	void emailNofity(JiraFaultEntity jiraEntity, short type);
	
}
