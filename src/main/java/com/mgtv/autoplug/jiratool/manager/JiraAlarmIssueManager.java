package com.mgtv.autoplug.jiratool.manager;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.mgtv.autoplug.jiratool.message.JiraAlarmEntity;
import com.mgtv.autoplug.jiratool.message.JiraUser;

public interface JiraAlarmIssueManager {
	
	String createIssue(String userName, String passWord, JiraAlarmEntity jiraAlarmEntity);
	
	//目前只支持修改故障恢复时间
	String updateIssue(String userName, String passWord, JiraAlarmEntity jiraAlarmEntity);
	
	JiraUser getJiraUserName(String userName, String passWord, String keyName);
	
	List<JiraAlarmEntity> findIssueByAlarmId(String userName, String passWord, String alarmId) throws ClientProtocolException, IOException;
	
	void addAlarmComment(String userName, String passWord, JiraAlarmEntity entity);
	
}
